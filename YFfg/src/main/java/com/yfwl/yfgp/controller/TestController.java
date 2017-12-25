package com.yfwl.yfgp.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.easemodrest.demo.EasemobMessages;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.Contest;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.RaiseInfo;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Score;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.schedule.ClearSchedule;
import com.yfwl.yfgp.service.AccountService;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.ContestService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.HomeService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController{
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	private static final double INIT_TOTAL = 1000000;		//设置初始金额
	private static final int PAGE_SIZE = 20;
	private static final int RAISE_SIZE = 8;
	
	@Autowired
	GroupService groupService;
	@Autowired
	UserService userService;
	@Autowired
	TokenService tokenService;
	@Autowired
	CashService cashService;
	@Autowired
	HomeService homeService;
	@Autowired
	ContestService contestService;
	@Autowired
	AccountService accountService;
	
	@RequestMapping(value = "/test", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> initAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String a = "20160501";
		boolean has = AccountUtil.HOLIDAY_STRING.contains(a);
		if(has) {
			map.put("true", "afds");
		}
		
		return map;
	}
	
	@RequestMapping(value = "/testaccounts", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> testaccounts(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String useridString = request.getParameter("userid");
		Integer start = 0;
		String startString = request.getParameter("start");
		if(null != startString) {
			start = Integer.parseInt(startString);
		}
		String sort = "DESC";
		Integer limit = PAGE_SIZE;
		List<Order> rankOrderList = cashService.getRankZuheOrder(start, limit, sort);
		ArrayList<Accounts> list = new ArrayList<Accounts>();
		for(Order mainOrder: rankOrderList) {
			Accounts a = new Accounts();
			a.setGid(Integer.parseInt(mainOrder.getOutTradeNo()));
			Accounts accounts = groupService.getAccounts(a);
			if(null != accounts) {
				/*RaiseInfo raiseInfo = new RaiseInfo();
				accounts.setRaiseInfo(raiseInfo);
				User user = userService.selectUserById(mainOrder.getUserId());
				if(null != user) {
					accounts.getRaiseInfo().setStimes(user.getStimes());
					accounts.getRaiseInfo().setLtimes(user.getLtimes());
					accounts.getRaiseInfo().setStop(accounts.getOptigid());
					accounts.setUser(user);
					accounts.setInit(INIT_TOTAL);
				}
				accounts.getRaiseInfo().setFee(mainOrder.getFeeTotal());
				accounts.getRaiseInfo().setBonus(mainOrder.getFeeLeft());
				accounts.getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail()));
				accounts.getRaiseInfo().setProfit(Float.parseFloat(mainOrder.getBody()));
				accounts.getRaiseInfo().setFeeOrigin(mainOrder.getFeeOrigin());
				Order order = new Order();
				order.setOrderId(mainOrder.getId());
				//List<Order> orderList = cashService.getFollowListOrder(order);
				Integer orderListSize = cashService.getFollowListOrderSize(order);
				accounts.getRaiseInfo().setJoinNum(orderListSize);
				accounts.getRaiseInfo().setPay(0);
				
				if(null != useridString) {
					Integer userid = Integer.parseInt(useridString);
					order.setUserId(userid);
					Order userOrder = cashService.hasGetOrder(order);
					if(null != userOrder) {
						accounts.getRaiseInfo().setPay(1);
					}
				}*/
				list.add(accounts);
			}
			else {
				System.out.println(mainOrder.getOutTradeNo());
			}
		}
		if(list.isEmpty()) {
			map = rspFormat("", SUCCESS);
		}
		else {
			//System.out.println(list.size());
			map = rspFormat(list, SUCCESS);
		}
		return map;
	}

	@RequestMapping(value = "/queryinterest", method = { RequestMethod.GET})
	@ResponseBody
	public Map<String, Object> queryInterest(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer uid = Integer.parseInt(request.getParameter("userid"));
		Relation relation = new Relation();
		relation.setAttr(1);
		relation.setDel(0);
		relation.setUid(uid);
		List<Relation> mainRelation = groupService.selectInterestRelation(relation);
		if(!mainRelation.isEmpty()) {
			for(int i = 0; i < mainRelation.size(); i++) {
				User user = userService.selectUserById((Integer) mainRelation.get(i).getAccounts().getUserid());
				if(null != user) {
					mainRelation.get(i).getAccounts().setUser(user);
					RaiseInfo raiseInfo = new RaiseInfo();
					mainRelation.get(i).getAccounts().setRaiseInfo(raiseInfo);
					mainRelation.get(i).getAccounts().setUser(user);
					mainRelation.get(i).getAccounts().setInit(INIT_TOTAL);				//设置初始金额
					mainRelation.get(i).getAccounts().getRaiseInfo().setStimes(user.getStimes());
					mainRelation.get(i).getAccounts().getRaiseInfo().setLtimes(user.getLtimes());
					mainRelation.get(i).getAccounts().getRaiseInfo().setStop(mainRelation.get(i).getAccounts().getOptigid());
					if(mainRelation.get(i).getAccounts().getAttr().equals(7)) {
						Order order = new Order();
						order.setOutTradeNo(mainRelation.get(i).getAccounts().getGid().toString());
						Order mainOrder = cashService.getOrderByOutTradeNo(order);
						
						mainRelation.get(i).getAccounts().getRaiseInfo().setFee(mainOrder.getFeeTotal());
						mainRelation.get(i).getAccounts().getRaiseInfo().setBonus(mainOrder.getFeeLeft());
						mainRelation.get(i).getAccounts().getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail()));
						mainRelation.get(i).getAccounts().getRaiseInfo().setProfit(Float.parseFloat(mainOrder.getBody()));
						
						order.setOrderId(mainOrder.getId());
						List<Order> orderList = cashService.getListOrder(order);
						mainRelation.get(i).getAccounts().getRaiseInfo().setJoinNum(orderList.size());
						mainRelation.get(i).getAccounts().getRaiseInfo().setPay(1);
					}
				}
			}
			map = rspFormat(mainRelation, SUCCESS);
		}
		else {
			map = rspFormat("", SUCCESS);
		}
		
		return map;
	}
	
	@RequestMapping(value = "/testrank", method = { RequestMethod.GET})
	@ResponseBody
	public Map<String, Object> testRank(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		updateRank();
		map = rspFormat("", SUCCESS);
		return map;
	}
	
	
	public void updateRank() {
		List<Accounts> accountsList = groupService.getAllFreeAccounts();
		Integer accountsSize = accountsList.size();
		for(int i = 0 ;i < accountsList.size(); i++) {
			Accounts mainAccounts = accountsList.get(i);
			if(null != accountsList) {
				Integer rankBeyond = groupService.getAccountsRank(mainAccounts);
				double rank = (double) rankBeyond * 100 / accountsSize;
				mainAccounts.setRank(rank);
				groupService.updateAccounts(mainAccounts);
				mainAccounts = null;
			}
		}
		
	}
	
	
	@RequestMapping(value = "/testexrights", method = { RequestMethod.GET})
	@ResponseBody
	public Map<String, Object> testExrights(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		exrights();
		map = rspFormat("", SUCCESS);
		return map;
	}
	
	public void exrights() {
		List<Stockinfo> stockList = groupService.getAllStockinfo();
		for(Stockinfo stockinfo: stockList) {
			String stockStr = stockinfo.getStockid().startsWith("6")?stockinfo.getStockid() + ".SS":stockinfo.getStockid() + ".SZ";
			String token = getDefaultToken();
			String resultStr = GetHSTokenUtils.getExrights(stockStr, token);
			JSONObject jsonData = new JSONObject(resultStr);
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
					double allotted_ps = lastArray.getDouble(1);
					double rationed_ps = lastArray.getDouble(2);
					double rationed_px = lastArray.getDouble(3);
					double bonus_ps = lastArray.getDouble(4);
					
					SimpleDateFormat dateSdf =   new SimpleDateFormat("yyyyMMdd");
					Date datenow = new Date();
					String dateString = dateSdf.format(datenow);
					if(dateString.equals(date + "")){
						Posi posi = new Posi();
						posi.setStockid(stockinfo.getStockid());
						List<Posi> posiList = groupService.getAllStockidPosi(posi);
						if(!posiList.isEmpty()) {
							String result = GetHSTokenUtils.getReal(stockStr, token);
							JSONObject jsonObject = new JSONObject(result);
							JSONObject snapshot = jsonObject.getJSONObject("data").getJSONObject("snapshot");
							double last_px = snapshot.getJSONArray(stockStr).getDouble(2);
							
							double newScale = 1 + allotted_ps + rationed_ps;
							double newPrice = (last_px - bonus_ps + rationed_ps * rationed_px) / newScale;
							for(Posi eposi: posiList) {
								if(bonus_ps > 0) {
									double bonus = eposi.getVol() * 100 * bonus_ps;
									Accounts a = new Accounts();
									a.setGid(eposi.getGid());
									Accounts accounts = groupService.getAccounts(a);
									accounts.setAvailable(accounts.getAvailable() + bonus);
									groupService.updateAccounts(accounts);
								}
								Integer amount = (int) newScale * eposi.getVol();
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
	
	@RequestMapping(value = "/testmsg", method = { RequestMethod.GET})
	@ResponseBody
	public Map<String, Object> testMsg(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
	
		String from = "lbh3zyi";
        String targetTypeus = "users";
        ObjectNode ext = factory.objectNode();
        ArrayNode targetusers = factory.arrayNode();
       
        targetusers.add("ipvnp5j");
        
        ObjectNode txtmsg = factory.objectNode();
		txtmsg.put("msg","test");
        txtmsg.put("type","txt");
        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
		map = rspFormat("", SUCCESS);
		return map;
	}
	
	@RequestMapping(value = "/testupdate", method = { RequestMethod.GET})
	@ResponseBody
	public Map<String, Object> testupdate(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		updateWebStat();
		return map;
	}
	
	public void updateWebStat() {
		List<Accounts> accountsList = groupService.getAllWebAccounts();
		String token = getDefaultToken();
		Date now = new Date();
		for(Accounts accounts:accountsList)
		{
			if(null != accounts) {
				Posi posiNow = new Posi();
				posiNow.setGid(accounts.getGid());
				List<Posi> posiList = groupService.getPosi(posiNow);
				if(!posiList.isEmpty()) {
					String stockStr = "";
					for(int j = 0; j < posiList.size(); j++ ) {
						String stockString = posiList.get(j).getStockid();
						String stocklist = stockString.startsWith("6")?stockString + ".SS":stockString + ".SZ";
						if(j == posiList.size() -1) {
							stockStr += stocklist;
						}
						else {
							stockStr += stocklist + ",";
						}
					}
					String resultStr = GetHSTokenUtils.getReal(stockStr, token);
					JSONObject jsonData = new JSONObject(resultStr);
					try {
						JSONObject snapshotData = jsonData.getJSONObject("data").getJSONObject("snapshot");
						double realPrice;
						double totalStock = 0;
						for(int j = 0; j < posiList.size(); j++ ) {
							String stockString = posiList.get(j).getStockid();
							String stocklist = stockString.startsWith("6")?stockString + ".SS":stockString + ".SZ";
							double last = snapshotData.getJSONArray(stocklist).getDouble(2);
							double preclose = snapshotData.getJSONArray(stocklist).getDouble(3);
							if(last < 0.01) {
								realPrice = preclose;
							}
							else {
								realPrice = last;
							}
							totalStock += realPrice * posiList.get(j).getVol() * 100;
						}
						accounts = groupService.getAccounts(accounts);
						accounts.setTotal(accounts.getAvailable() + accounts.getFrozen() + totalStock);
						accounts.setStock(totalStock);
		
					}catch (Exception e) {
						
					}
				}
			}
			double value;
			Score score = new Score();
			score.setGid(accounts.getGid());
			Score lastScore = groupService.getLastScore(score);
			if(null == lastScore) {
				value = accounts.getInit();
			}
			else {
				value = lastScore.getTotal();
			}
			Stat stat = new Stat();
			stat.setGid(accounts.getGid());
			Stat updateState = groupService.getStat(stat);
			if(null != updateState) {
				double rd = (accounts.getTotal() - value) * 100 / value;
				updateState.setRd(rd);
				updateState.setUpdatetime(now);
				groupService.updateStat(updateState);
			}
		}
	}
	
	
	@RequestMapping(value = "/testjoinconteste", method = { RequestMethod.GET})
	@ResponseBody
	public Map<String, Object> testjoin(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		int user_id=30000;
		for(int i= user_id; i < 30100; i++) {
			joinContest(i, 1017);
		}
		return map;
	}
	
	
	
	public Map<String, Object> joinContest(int user_id, int cid) {
		try{
			Accounts accounts = new Accounts();
			accounts.setUserid(user_id);
			accounts.setCiteId(cid);
			int accountsCount = groupService.joinAccountsCount(accounts);
			if(accountsCount > 0) {
				return rspFormat("", FAIL);
			}
			else {
				User user = userService.selectUserById(user_id);
				//Account account = accountService.getAccount(user_id);
				Contest contest = contestService.getContestByCid(cid);
//				if(account.getMoney() < contest.getJoinFee()) {
//					return rspFormat("", NO_ENOUGH_MONEY);
//				}
				String cname = contest.getCname();
				accounts.setGname(cname+"-"+user.getUserName());
				Date createtime = new Date();
				accounts.setCreatetime(createtime);
				accounts.setAttr(8);
				accounts.setOptigid(0);
				double giveTotal = 1000000.00;
				accounts.setAvailable(giveTotal);
				accounts.setTotal(giveTotal);
				accounts.setInit(giveTotal);
				accounts.setGtnum(0);
				accounts.setGznum(0);
				accounts.setDel(0);
				Integer hasInsert = groupService.initAccounts(accounts);
				if(hasInsert > 0) {
//					account.setMoney(account.getMoney() - contest.getJoinFee());
//					accountService.updateAccount(account);
					
					contest.setJoinNum(contest.getJoinNum() + 1);
					contest.setPool(contest.getPool() + contest.getJoinFee());
					contest.setUpdatedTime(createtime);
					contestService.updateContest(contest);
					
					Integer gid = accounts.getGid();
					Stat stat = new Stat();
					stat.setGid(gid);
					stat.setR7(0);
					stat.setR30(0);
					stat.setR6m(0);
					stat.setR1y(0);
					stat.setCiteId(cid);
					groupService.initStat(stat);
					return rspFormat(accounts, SUCCESS);
				}
				else {
					return rspFormat("", FAIL);
				}
			}
		} catch (Exception e) {
			return rspFormat("", FAIL);
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