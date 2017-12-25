package com.yfwl.yfgp.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.easemodrest.demo.EasemobMessages;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.Time;

@Component
public class ClearAccounts {

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
	private static Logger logger = OrderBookSchedule.getLogger("d:/logs/yfgp/schedule/ClearAccounts_debug_");
	
	public void bindSchedule() {
		logger.debug("============================ClearAccounts 开始执行============================");
		
		logger.debug("====================== updateOrder方法  开始执行=====================");
		updateOrder();
		logger.debug("====================== updateOrder方法  结束执行======================");
		
		logger.debug("============================ClearAccounts 结束执行============================");
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
				Integer n2ed = Time.daysBetween(now, mainAccounts.getEndtime());
				Integer r2n = Time.daysBetween(mainAccounts.getRaisetime(), now);
				
				double profit = (mainAccounts.getTotal() -1000000) / 10000;
				double goal = Double.parseDouble(mainOrder.getBody());
				float bonus = mainOrder.getFeeLeft();
				if(mainAccounts.getDel().equals(0) && n2e <= 0 && n2ed == 0 && (profit < goal)) {
					if(r2n <= 0) {
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
				if(mainAccounts.getDel().equals(0) && n2e <= 0 && profit > goal) {
					if(r2n <= 0) {
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
				
				if(mainAccounts.getDel().equals(1) || mainAccounts.getDel().equals(3)) {
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
		}
		
		logger.debug("~~~~~~~~~~~~~~ updateOrder执行完毕，共处理：" + orderList.size() + " 个订单~~~~~~~~~~~~~~");
	}
}
