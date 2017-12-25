package com.yfwl.yfgp.schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.controller.BaseController;
import com.yfwl.yfgp.easemodrest.comm.Constants;
import com.yfwl.yfgp.easemodrest.comm.Roles;
import com.yfwl.yfgp.easemodrest.demo.EasemobMessages;
import com.yfwl.yfgp.easemodrest.vo.ClientSecretCredential;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Score;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.Time;

@Component
public class InitSchedule extends BaseController {
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	@Resource
	GroupService groupService;
	@Resource
	TokenService tokenService;
	@Resource
	CashService cashService;
	@Resource
	UserService userService;
	
	
	public void bindSchedule() {
		initToken();
		updateOrder();
		exrights();
		updateWebStat();
		
	}
	
	public void initToken() {
		Integer userId = Integer.parseInt(PropertiesUtils.getServerUserString());
		String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("grant_type", "client_credentials");
		paramMap.put("open_id", userId.toString());
		String tokenResult = GetHSTokenUtils.sendPost(url,
				paramMap, GetHSTokenUtils.CHARSET,
				GetHSTokenUtils.CHARSET, null,
				GetHSTokenUtils.BASIC, "获取令牌");
		// 新的token
		AccessToken accesstoken = JacksonUtils.json2Object(
				tokenResult, AccessToken.class);
		Token token = new Token();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND,
				Integer.parseInt(accesstoken.getExpires_in()));
		Date expiresTime = c.getTime();// 计算出过期时间
		token.setExpiresTime(expiresTime);
		token.setAccessToken(accesstoken.getAccess_token());
		token.setTokenType(accesstoken.getToken_type());
		token.setRefreshToken(accesstoken.getRefresh_token());
		token.setExpiresIn(accesstoken.getExpires_in());
		token.setUserId(userId);
		boolean isHaven = tokenService.checkUserIdIsRequested(userId);
		if(isHaven == true){
			tokenService.updateTokenLoginOn(token);
		}else{
			tokenService.insertTokenLoginOn(token);
		}
	}
	
	public void exrights() {
		List<Stockinfo> stockList = groupService.getAllStockinfo();
		for(Stockinfo stockinfo: stockList) {
			String stockStr = stockinfo.getStockid().startsWith("6")?stockinfo.getStockid() + ".SS":stockinfo.getStockid() + ".SZ";
			String token = getDefaultToken();
			String resultStr = GetHSTokenUtils.getExrights(stockStr, token);
			JSONObject jsonData = new JSONObject(resultStr);
			int k=0;
			while(jsonData.toString().contains("访问令牌无效或已过期!")){
				k++;
				token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
				resultStr = GetHSTokenUtils.getExrights(stockStr, token);
				jsonData = new JSONObject(resultStr);
				if(k>3){
					break;
				}
			 }
			try {
				JSONObject snapshotData = jsonData.getJSONObject("data").getJSONObject("exrights");
				if(!snapshotData.has(stockStr)) {
					continue;
				}
				JSONArray jsonArray = snapshotData.getJSONArray(stockStr);
				Integer size = jsonArray.length();
				if(size == 0) {
					continue;
				}
				else {
					JSONArray lastArray = jsonArray.getJSONArray(size -1);
					int date = lastArray.getInt(0);
					double allotted_ps = lastArray.getDouble(1);//每1股送多少股 
					double rationed_ps = lastArray.getDouble(2);// 每1股配多少股 
					double rationed_px = lastArray.getDouble(3);//配股的价格 
					double bonus_ps = lastArray.getDouble(4);//每1股分红多少
					
					SimpleDateFormat dateSdf =   new SimpleDateFormat("yyyyMMdd");
					Date datenow = new Date();
					String dateString = dateSdf.format(datenow);
					if(dateString.equals(date + "")){
						Posi posi = new Posi();
						posi.setStockid(stockinfo.getStockid());
						List<Posi> posiList = groupService.getAllStockidPosi(posi);
						if(!posiList.isEmpty()) {
							//String result = GetHSTokenUtils.getReal(stockStr, token);
							//JSONObject jsonObject = new JSONObject(result);
							//JSONObject snapshot = jsonObject.getJSONObject("data").getJSONObject("snapshot");
							//double preclose = snapshot.getJSONArray(stockStr).getDouble(3);
							double newScale = 1 + allotted_ps + rationed_ps;
							//double newPrice = (preclose - bonus_ps + rationed_ps * rationed_px) / newScale;
							for(Posi eposi: posiList) {
								if(bonus_ps > 0) {
									double bonus = eposi.getVol() * 100 * bonus_ps;
									Accounts a = new Accounts();
									a.setGid(eposi.getGid());
									Accounts accounts = groupService.getAccounts(a);
									accounts.setAvailable(accounts.getAvailable() + bonus);
									groupService.updateAccounts(accounts);
								}
								double newPrice = (eposi.getPrice() - bonus_ps + rationed_ps * rationed_px) / newScale;
								Integer amount = (int) (newScale * eposi.getVol());
								eposi.setVol(amount);
								eposi.setAvailable(amount);
								eposi.setPrice(newPrice);
								groupService.updatePosi(eposi);		
							}
						}
						else {
							
						}
					}
					else {
						
					}
				}
				
			}
			catch (Exception e) {
				continue;
			}
		}
	}
	
	public void updateOrder() {
		List<Order> orderList = cashService.getZuheOrder();//trade_type='16' and status='5';
		Date now = new Date();
		Order order = new Order();
		if(!orderList.isEmpty()) {
			for(int i = 0; i < orderList.size(); i++) {
				Order mainOrder = orderList.get(i);
				Integer floor;
				try {
					floor = Integer.parseInt(mainOrder.getExpand());
				} catch (Exception e) {
					floor = 0;
				}
				Integer gid = Integer.parseInt(mainOrder.getOutTradeNo());
				Accounts a = new Accounts();
				a.setGid(gid);
				Accounts mainAccounts = groupService.getAccounts(a);
				if(null == mainAccounts || !mainAccounts.getDel().equals(0)) {
					continue;
				}
				Integer n2r = Time.compareTime(now, mainAccounts.getRaisetime());
				Integer n2e = Time.compareTime(now, mainAccounts.getEndtime());
				if(n2e >= 0) {
					mainAccounts.setDeltime(now);
					mainAccounts.setDel(3);
					groupService.updateAccounts(mainAccounts);
					OrderBook orderbook = new OrderBook();
					orderbook.setGid(gid);
					groupService.deleteAccountsOrder(orderbook);
					Posi posi = new Posi();
					posi.setGid(gid);
					groupService.deletePosi(posi);
					Relation relation = new Relation();
					relation.setGid(gid);
					groupService.deleteRelation(relation);
					Stat stat = new Stat();
					stat.setGid(gid);
					groupService.deleteStat(stat);
					continue;
				}
				if(n2r >= 0) {
					order.setOrderId(mainOrder.getId());
					//List<Order> followOrderList = cashService.getFollowListOrder(order);
					Integer followOrderListSize = cashService.getFollowListOrderSize(order);
					if(followOrderListSize.equals(0)) {
						mainAccounts.setDeltime(now);
						mainAccounts.setDel(1);
						groupService.updateAccounts(mainAccounts);
						OrderBook orderbook = new OrderBook();
						orderbook.setGid(gid);
						groupService.deleteAccountsOrder(orderbook);
						Posi posi = new Posi();
						posi.setGid(gid);
						groupService.deletePosi(posi);
						Relation relation = new Relation();
						relation.setGid(gid);
						groupService.deleteRelation(relation);
						Stat stat = new Stat();
						stat.setGid(gid);
						groupService.deleteStat(stat);
	
//						String accountsName = mainAccounts.getGname();
//						User user = userService.selectUserById(mainAccounts.getUserid());
//						String from = "lbh3zyi";
//				        String targetTypeus = "users";
//				        ObjectNode ext = factory.objectNode();
//				        ArrayNode targetusers = factory.arrayNode();
//				        targetusers.add(user.getEasemobId());
					}
					else if(followOrderListSize < floor) {
						mainAccounts.setDeltime(now);
						mainAccounts.setDel(1);
						groupService.updateAccounts(mainAccounts);
						OrderBook orderbook = new OrderBook();
						orderbook.setGid(gid);
						groupService.deleteAccountsOrder(orderbook);
						Posi posi = new Posi();
						posi.setGid(gid);
						groupService.deletePosi(posi);
						Relation relation = new Relation();
						relation.setGid(gid);
						groupService.deleteRelation(relation);
						Stat stat = new Stat();
						stat.setGid(gid);
						groupService.deleteStat(stat);
					}
					else {
						Integer days  = Time.daysBetween(mainAccounts.getRaisetime(), now);
						if(days.equals(0)) {
							mainOrder.setOrderId(mainOrder.getId());
							List<Order> realFollowOrderList = cashService.getRealFollowListOrder(mainOrder);
							if(!realFollowOrderList.isEmpty()) {
								String accountsName = mainAccounts.getGname();
								User user = userService.selectUserById(mainAccounts.getUserid());
								List<String> pushUserList = new ArrayList<String>();
								pushUserList.add(user.getEasemobId());
								for(int r = 0; r < realFollowOrderList.size(); r++) {
						        	User followUser = userService.selectUserById(realFollowOrderList.get(r).getUserId());
						        	if(null != followUser) {
						        		if(followUser.getUserStatus().equals(4)) {
						        			continue;
						        		}
						        		pushUserList.add(followUser.getEasemobId());
						        	}
						        }
								String msg = "付费组合#" + accountsName +"("+gid+")#已启动";
						        ObjectNode ext = JsonNodeFactory.instance.objectNode();
						        sendEaseMobMsg(pushUserList, ext, msg);
							}
						}
					}
				}
			}
		}
	}
	
	
	public void updateWebStat() {
		List<Accounts> accountsList = groupService.getAllWebAccounts();//attr='8' and del='0'
		Date now = new Date();
		for(Accounts accounts:accountsList)
		{
			Stat stat = new Stat();
			stat.setGid(accounts.getGid());
			Stat updateState = groupService.getStat(stat);
			if(null != updateState) {
				double rd = 0;
				updateState.setRd(rd);
				updateState.setUpdatetime(now);
				groupService.updateStat(updateState);
			}
		}
	}
	
	public  String getDefaultToken(){
		Integer userId = Integer.parseInt(PropertiesUtils.getServerUserString());
		String tokenString;
		Token token = tokenService.selectTokenByUserId(userId);
		Date expiresTime = token.getExpiresTime();
		Date nowDate = new Date();
		if (nowDate.before(expiresTime)) {
			// 当前时间在过期时间前面（token还未过期）
			tokenString = token.getAccessToken();
		} else {
			tokenString = updateToken(userId);
		}
		return tokenString;
	}
	
	public String updateToken(Integer userId) {
		String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("grant_type", "client_credentials");
		paramMap.put("open_id", userId.toString());
		String tokenResult = GetHSTokenUtils.sendPost(url,
				paramMap, GetHSTokenUtils.CHARSET,
				GetHSTokenUtils.CHARSET, null,
				GetHSTokenUtils.BASIC, "获取令牌");
		// 新的token
		AccessToken accesstoken = JacksonUtils.json2Object(
				tokenResult, AccessToken.class);
		Token token = new Token();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND,
				Integer.parseInt(accesstoken.getExpires_in()));
		Date expiresTime = c.getTime();// 计算出过期时间
		token.setExpiresTime(expiresTime);
		token.setAccessToken(accesstoken.getAccess_token());
		token.setTokenType(accesstoken.getToken_type());
		token.setRefreshToken(accesstoken.getRefresh_token());
		token.setExpiresIn(accesstoken.getExpires_in());
		token.setUserId(userId);
		tokenService.updateTokenLoginOn(token);
		return accesstoken.getAccess_token();
	}
}
