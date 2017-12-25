package com.yfwl.yfgp.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.easemodrest.demo.EasemobMessages;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Score;
import com.yfwl.yfgp.model.Stat;
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
public class AccountsSchedule {

	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	@Resource
	GroupService groupService;
	@Resource
	TokenService tokenService;
	@Resource
	CashService cashService;
	@Resource
	UserService userService;
	private final static float PERCENT = 0.8f;
	private static Logger logger = OrderBookSchedule.getLogger("d:/logs/yfgp/schedule/AccountsSchedule_debug_");
	
	public void bindSchedule() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY); 
		int minute = c.get(Calendar.MINUTE);
		if(hour == 9 && minute > 30) {
			logger.debug("============================9:30点至10点(type1)  AccountsSchedule 开始执行============================");
			
			logger.debug("======================(type1) updateAccounts方法  开始执行=====================");
			updateAccounts();
			logger.debug("======================(type1) updateAccounts方法  结束执行======================");
			
			logger.debug("======================(type1) updateOrder方法  开始执行=====================");
			updateOrder();
			logger.debug("======================(type1) updateOrder方法  结束执行======================");
			
			logger.debug("============================9:30点至10点(type1)  AccountsSchedule 结束执行============================");
		}
		else if( hour > 9) {
			logger.debug("============================10点之后(type2)  AccountsSchedule 开始执行============================");
			
			logger.debug("======================(type2) updateAccounts方法  开始执行=====================");
			updateAccounts();
			logger.debug("======================(type2) updateAccounts方法  结束执行======================");
			
			logger.debug("======================(type2) updateOrder方法  开始执行=====================");
			updateOrder();
			logger.debug("======================(type2) updateOrder方法  结束执行======================");
			
			logger.debug("============================10点之后(type2)  AccountsSchedule 结束执行============================");
		}
	}
	
	
	
	
	public void updateAccounts() {
		List<Accounts> accountsList = groupService.getAllRunAccountsList();
		String token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
		Date updatetime = new Date();
		for(int i = 0 ;i < accountsList.size(); i++) {
			Accounts mainAccounts = accountsList.get(i);
			if(null != mainAccounts) {
				
				logger.debug("~~~~~~~~~~~~~~updateAccounts执行中，正在处理Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				
				Posi posiNow = new Posi();
				posiNow.setGid(mainAccounts.getGid());
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
						mainAccounts.setTotal(mainAccounts.getAvailable() + mainAccounts.getFrozen() + totalStock);
						mainAccounts.setStock(totalStock);
						mainAccounts.setUpdatetime(updatetime);
						groupService.updateAccounts(mainAccounts);
						}catch (Exception e) {
						
					}
				}
				logger.debug("~~~~~~~~~~~~~~updateAccounts执行中，处理完Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				mainAccounts = null;
			}
		}
		logger.debug("~~~~~~~~~~~~~~ updateAccounts执行完毕，共处理：" + accountsList.size() + " 个Accounts~~~~~~~~~~~~~~");
	}
	
	
	
	public void updateOrder() {
		List<Order> orderList = cashService.getZuheOrder();
		Date now = new Date();
		Order order = new Order();
		if(!orderList.isEmpty()) {
			for(int i = 0; i < orderList.size(); i++) {
				Order mainOrder = orderList.get(i);
				
				logger.debug("~~~~~~~~~~~~~~updateOrder执行中，正在处理订单 " + mainOrder.getId()+"~~~~~~~~~~~~~~");
				
				Integer gid = Integer.parseInt(mainOrder.getOutTradeNo());
				Accounts a = new Accounts();
				a.setGid(gid);
				Accounts mainAccounts = groupService.getAccounts(a);
				if(null == mainAccounts) {
					continue;
				}
				Integer n2e = Time.compareTime(now, mainAccounts.getEndtime());
				Integer r2n = Time.daysBetween(mainAccounts.getRaisetime(), now);
				
				double profit = (mainAccounts.getTotal() -1000000) / 10000;
				double goal = Double.parseDouble(mainOrder.getBody());
				float bonus = mainOrder.getFeeLeft();
				if(mainAccounts.getDel().equals(0) && n2e >= 0) {
					if(r2n == 0) {
						continue;
					}
					mainAccounts.setDeltime(now);
					mainAccounts.setDel(3);
					groupService.updateAccounts(mainAccounts);
					OrderBook orderbook = new OrderBook();
					orderbook.setGid(gid);
					groupService.deleteAccountsOrder(orderbook);
					Posi posi = new Posi();
					posi.setGid(gid);
					groupService.deletePosi(posi);
					Stat stat = new Stat();
					stat.setGid(gid);
					groupService.deleteStat(stat);
					User user = userService.selectUserById(mainAccounts.getUserid());
					user.setLtimes(user.getLtimes() + 1);
					userService.updateUserSuper(user);
				}
				if(mainAccounts.getDel().equals(0) && n2e <= 0 && profit <= 0 - mainAccounts.getOptigid()) {
					if(r2n == 0) {
						continue;
					}
					mainAccounts.setDeltime(now);
					mainAccounts.setDel(4);
					groupService.updateAccounts(mainAccounts);
					OrderBook orderbook = new OrderBook();
					orderbook.setGid(gid);
					groupService.deleteAccountsOrder(orderbook);
					Posi posi = new Posi();
					posi.setGid(gid);
					groupService.deletePosi(posi);
					Stat stat = new Stat();
					stat.setGid(gid);
					groupService.deleteStat(stat);
					User user = userService.selectUserById(mainAccounts.getUserid());
					user.setLtimes(user.getLtimes() + 1);
					userService.updateUserSuper(user);
				}
				if(mainAccounts.getDel().equals(0) && n2e <= 0 && profit > goal) {
					if(r2n == 0) {
						continue;
					}
					mainAccounts.setDeltime(now);
					mainAccounts.setDel(2);
					groupService.updateAccounts(mainAccounts);
					OrderBook orderbook = new OrderBook();
					orderbook.setGid(gid);
					groupService.deleteAccountsOrder(orderbook);
					Posi posi = new Posi();
					posi.setGid(gid);
					groupService.deletePosi(posi);
					Stat stat = new Stat();
					stat.setGid(gid);
					groupService.deleteStat(stat);
					User user = userService.selectUserById(mainAccounts.getUserid());
					user.setStimes(user.getStimes() + 1);
					userService.updateUserSuper(user);
				}
				
				if(mainAccounts.getDel().equals(1) || mainAccounts.getDel().equals(3) || mainAccounts.getDel().equals(4)) {
					CashAccount mainCash = cashService.getCashAccountByUserId(mainAccounts.getUserid());
					mainOrder.setStatus(0);
					cashService.updateOrder(mainOrder);
					order.setOrderId(mainOrder.getId());
					List<Order> followOrderList = cashService.getListOrder(order);
					if(!followOrderList.isEmpty()) {
						Order useOrder = new Order();
						//float average_bonus = bonus / followOrderList.size();
						Random rand = new Random();
						Integer rand_int = rand.nextInt(followOrderList.size());
						/*if(bonus > 0 && !mainAccounts.getDel().equals(1)) {
							Date timeStart = new Date();
							Date timeExpire = timeStart;
							String transactionId = timeStart.getTime() + "";
							String outTradeNo = transactionId;
							useOrder.setTransactionId(transactionId);
							useOrder.setOutTradeNo(outTradeNo);
							useOrder.setUserId(followOrderList.get(rand_int).getUserId());
							useOrder.setOrderId(followOrderList.get(rand_int).getId());
							useOrder.setTimeStart(timeStart);
							useOrder.setTimeExpire(timeExpire);
							useOrder.setTradeType(23);
							useOrder.setFeeTotal(bonus);
							useOrder.setFeeLeft(0);
							useOrder.setBody("付费组合" + mainAccounts.getGname() + "的红包");
							useOrder.setDetail(gid.toString());
							useOrder.setStatus(0);
							cashService.initOrder(useOrder);
							
							CashAccount followCash = cashService.getCashAccountByUserId(followOrderList.get(rand_int).getUserId());
							followCash.setTotalCash(followCash.getTotalCash() + bonus);
							cashService.updateCashAccount(followCash);
						}*/
						if(mainAccounts.getDel().equals(1)) {
							Date timeStart = new Date();
							Date timeExpire = timeStart;
							String transactionId = timeStart.getTime() + "";
							String outTradeNo = transactionId;
							useOrder.setTransactionId(transactionId);
							useOrder.setOutTradeNo(outTradeNo);
							useOrder.setUserId(mainCash.getUserId());
							useOrder.setOrderId(mainOrder.getId());
							useOrder.setTimeStart(timeStart);
							useOrder.setTimeExpire(timeExpire);
							useOrder.setTradeType(20);
							useOrder.setFeeTotal(bonus);
							useOrder.setFeeLeft(0);
							useOrder.setBody("付费组合取消");
							useOrder.setDetail(gid.toString());
							useOrder.setStatus(0);
							cashService.initOrder(useOrder);
							mainCash.setTotalCash(mainCash.getTotalCash() + bonus);
							mainCash.setTimeTill(now);
							cashService.updateCashAccount(mainCash);
						}
						for(int j = 0; j < followOrderList.size(); j++) {
							
							Date timeStart = new Date();
							Date timeExpire = timeStart;
							String transactionId = timeStart.getTime() + "";
							String outTradeNo = transactionId;
							//Order useOrder = new Order();
							useOrder.setTransactionId(transactionId);
							useOrder.setOutTradeNo(outTradeNo);
							useOrder.setUserId(followOrderList.get(j).getUserId());
							useOrder.setOrderId(followOrderList.get(j).getId());
							useOrder.setTimeStart(timeStart);
							useOrder.setTimeExpire(timeExpire);
							useOrder.setTradeType(19);
							useOrder.setFeeTotal(mainOrder.getFeeTotal());
							useOrder.setFeeLeft(0);
							useOrder.setBody("付费组合失败");
							if(mainAccounts.getDel().equals(1)) {
								useOrder.setTradeType(20);
								useOrder.setBody("付费组合取消");
							}
							useOrder.setDetail("系统返回");
							useOrder.setStatus(0);
							cashService.initOrder(useOrder);
							
							
							CashAccount followCash = cashService.getCashAccountByUserId(followOrderList.get(j).getUserId());
							followCash.setTotalCash(followCash.getTotalCash() + mainOrder.getFeeTotal());
							cashService.updateCashAccount(followCash);
						}
						if(bonus > 0 && !mainAccounts.getDel().equals(1)) {
							Integer size = followOrderList.size() >= 10? 10 : followOrderList.size();
							Date timeStart = new Date();
							Calendar c = Calendar.getInstance();
						    c.setTime(timeStart);
						    c.add(Calendar.DAY_OF_MONTH, 1);
							Date timeExpire = c.getTime();
							String transactionId = timeStart.getTime() + "";
							String outTradeNo = transactionId;
							useOrder.setTransactionId(transactionId);
							useOrder.setOutTradeNo(outTradeNo);
							useOrder.setUserId(mainAccounts.getUserid());
							useOrder.setOrderId(mainOrder.getId());
							useOrder.setTimeStart(timeStart);
							useOrder.setTimeExpire(timeExpire);
							useOrder.setTradeType(24);
							useOrder.setFeeTotal(bonus);
							useOrder.setFeeLeft(bonus);
							useOrder.setBody(mainAccounts.getGname() + "组合红包");
							useOrder.setDetail(size.toString());
							useOrder.setStatus(1);
							cashService.initOrder(useOrder);
							
							
							String from = "lbh3zyi";
					        String targetTypeus = "users";
					        ObjectNode ext = factory.objectNode();
					        ArrayNode targetusers = factory.arrayNode();
					        for(int j = 0; j < followOrderList.size(); j++) {
					        	User followUser = userService.selectUserById(followOrderList.get(j).getUserId());
					        	if(null != followUser) {
					        		targetusers.add(followUser.getEasemobId());
					        	}
					        }
					        
					        ext.put("is_red_paper_message", 1);
					        ext.put("id", useOrder.getId());
					        ext.put("transactionId", useOrder.getTransactionId());
					        ext.put("outTradeNo", useOrder.getOutTradeNo());
					        ext.put("userId", useOrder.getUserId());
					        ext.put("orderId", useOrder.getOrderId());
					        ext.put("timeStart", useOrder.getTimeStart().toString());
					        ext.put("timeExpire", useOrder.getTimeExpire().toString());
					        ext.put("tradeType", useOrder.getTradeType());
					        ext.put("feeLeft", useOrder.getFeeLeft());
					        ext.put("body", useOrder.getBody());
					        ext.put("detail", useOrder.getDetail());
					        ext.put("status", useOrder.getStatus());
					     
					        ObjectNode txtmsg = factory.objectNode();
					        txtmsg.put("msg", "[股哥红包]"+ useOrder.getBody());
					        txtmsg.put("type","txt");
					        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
						}
						Relation relation = new Relation();
						relation.setGid(gid);
						List<Relation> relationList = groupService.getAllRelation(relation);
						if(!relationList.isEmpty()) {
							String accountsName = mainAccounts.getGname();
							User user = userService.selectUserById(mainAccounts.getUserid());
							String from = "lbh3zyi";
					        String targetTypeus = "users";
					        ObjectNode ext = factory.objectNode();
					        ArrayNode targetusers = factory.arrayNode();
					        targetusers.add(user.getEasemobId());
					        for(int r = 0; r < relationList.size(); r++) {
					        	User followUser = userService.selectUserById(relationList.get(r).getUid());
					        	if(null != followUser) {
					        		targetusers.add(followUser.getEasemobId());
					        	}
					        }
					        ObjectNode txtmsg = factory.objectNode();
							
							if(mainAccounts.getDel().equals(1)) {
								txtmsg.put("msg", "组合#" + accountsName +"("+gid+ ")# 取消 ");
							}
							else {
								txtmsg.put("msg", "组合#" + accountsName +"("+gid+ ")# 失败 ");
							}
					        txtmsg.put("type","txt");
					        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
						}
						
					}
					else {
						Date timeStart = new Date();
						Date timeExpire = timeStart;
						String transactionId = timeStart.getTime() + "";
						String outTradeNo = transactionId;
						Order useOrder = new Order();
						useOrder.setTransactionId(transactionId);
						useOrder.setOutTradeNo(outTradeNo);
						useOrder.setUserId(mainCash.getUserId());
						useOrder.setOrderId(mainOrder.getId());
						useOrder.setTimeStart(timeStart);
						useOrder.setTimeExpire(timeExpire);
						useOrder.setTradeType(20);
						useOrder.setFeeTotal(bonus);
						useOrder.setFeeLeft(0);
						useOrder.setBody("付费组合取消");
						useOrder.setDetail(gid.toString());
						useOrder.setStatus(0);
						cashService.initOrder(useOrder);
						mainCash.setTotalCash(mainCash.getTotalCash() + bonus);
						mainCash.setTimeTill(now);
						cashService.updateCashAccount(mainCash);
						
						String accountsName = mainAccounts.getGname();
						User user = userService.selectUserById(mainAccounts.getUserid());
						String from = "lbh3zyi";
				        String targetTypeus = "users";
				        ObjectNode ext = factory.objectNode();
				        ArrayNode targetusers = factory.arrayNode();
				        targetusers.add(user.getEasemobId());
				        ObjectNode txtmsg = factory.objectNode();
						txtmsg.put("msg", "组合#" + accountsName +"("+gid+ ")# 已被取消 ");
				        txtmsg.put("type","txt");
				        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
					}
				}
				
				if(mainAccounts.getDel().equals(2)) {
					CashAccount mainCash = cashService.getCashAccountByUserId(mainAccounts.getUserid());
					mainOrder.setStatus(0);
					cashService.updateOrder(mainOrder);
					order.setOrderId(mainOrder.getId());
					List<Order> followOrderList = cashService.getListOrder(order);
					if(!followOrderList.isEmpty()) {
						float earn = mainOrder.getFeeTotal() * followOrderList.size() * PERCENT;
						mainCash.setTimeTill(now);
						mainCash.setTotalCash(mainCash.getTotalCash() + earn + bonus);
						cashService.updateCashAccount(mainCash);
						
						Date timeStart = new Date();
						Date timeExpire = timeStart;
						String transactionId = timeStart.getTime() + "";
						String outTradeNo = transactionId;
						Order useOrder = new Order();
						useOrder.setTransactionId(transactionId);
						useOrder.setOutTradeNo(outTradeNo);
						useOrder.setUserId(mainCash.getUserId());
						useOrder.setOrderId(mainOrder.getId());
						useOrder.setTimeStart(timeStart);
						useOrder.setTimeExpire(timeExpire);
						useOrder.setTradeType(21);
						useOrder.setFeeTotal(earn);
						useOrder.setFeeLeft(0);
						useOrder.setBody("付费组合成功");
						useOrder.setDetail("系统发放");
						useOrder.setStatus(0);
						cashService.initOrder(useOrder);
						
						if(bonus > 0) {
							useOrder.setTransactionId(transactionId + i);
							useOrder.setOutTradeNo(outTradeNo + i);
							useOrder.setUserId(mainCash.getUserId());
							useOrder.setOrderId(mainOrder.getId());
							useOrder.setTimeStart(timeStart);
							useOrder.setTimeExpire(timeExpire);
							useOrder.setTradeType(23);
							useOrder.setFeeTotal(bonus);
							useOrder.setFeeLeft(0);
							useOrder.setBody("付费组合" + mainAccounts.getGname() + "的红包");
							useOrder.setDetail(gid.toString());
							useOrder.setStatus(0);
							cashService.initOrder(useOrder);
						}
						
						CashAccount systemCash = cashService.getCashAccountByUserId(0);
						if(null != systemCash) {
							float searn = mainOrder.getFeeTotal() * followOrderList.size() *(1- PERCENT);
							Date stimeStart = new Date();
							Date stimeExpire = stimeStart;
							String stransactionId = stimeStart.getTime() + "";
							String soutTradeNo = stransactionId;
							Order suseOrder = new Order();
							suseOrder.setTransactionId(stransactionId);
							suseOrder.setOutTradeNo(soutTradeNo);
							suseOrder.setUserId(0);
							suseOrder.setOrderId(mainOrder.getId());
							suseOrder.setTimeStart(stimeStart);
							suseOrder.setTimeExpire(stimeExpire);
							suseOrder.setTradeType(22);
							suseOrder.setFeeTotal(searn);
							suseOrder.setFeeLeft(0);
							suseOrder.setBody("付费组合系统收费");
							suseOrder.setDetail("系统收费");
							suseOrder.setStatus(0);
							cashService.initOrder(suseOrder);
							
							systemCash.setTimeTill(now);
							systemCash.setTotalCash(systemCash.getTotalCash() + searn);
							cashService.updateCashAccount(systemCash);
						}
						
						Relation relation = new Relation();
						relation.setGid(gid);
						List<Relation> relationList = groupService.getAllRelation(relation);
						if(!relationList.isEmpty()) {
							String accountsName = mainAccounts.getGname();
							User user = userService.selectUserById(mainAccounts.getUserid());
							String from = "lbh3zyi";
					        String targetTypeus = "users";
					        ObjectNode ext = factory.objectNode();
					        ArrayNode targetusers = factory.arrayNode();
					        targetusers.add(user.getEasemobId());
					        for(int r = 0; r < relationList.size(); r++) {
					        	User followUser = userService.selectUserById(relationList.get(r).getUid());
					        	if(null != followUser) {
					        		targetusers.add(followUser.getEasemobId());
					        	}
					        }
					        ObjectNode txtmsg = factory.objectNode();
							txtmsg.put("msg", "组合#" + accountsName +"("+gid+ ")# 成功 ");
					        txtmsg.put("type","txt");
					        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
						}
					}
				}
				logger.debug("~~~~~~~~~~~~~~updateOrder执行中，处理完订单 " + mainOrder.getId()+"~~~~~~~~~~~~~~");
			}
			
			logger.debug("~~~~~~~~~~~~~~ updateOrder执行完毕，共处理：" + orderList.size() + " 个订单~~~~~~~~~~~~~~");
			
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
