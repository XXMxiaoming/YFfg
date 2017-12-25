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
import com.yfwl.yfgp.model.AccountsMeta;
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
public class AutoSchedule {
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	private static final Integer AUTO_START = 20000;
	private static final Integer AUTO_SIZE = 10;
	private static final Integer AUTO_GET = 20;
	private final static int MIN_SIZE = 100;
	private final static int MIN_LEFT = 10;
	@Resource
	GroupService groupService;
	@Resource
	TokenService tokenService;
	@Resource
	CashService cashService;
	@Resource
	UserService userService;
	
	private static Logger logger = OrderBookSchedule.getLogger("d:/logs/yfgp/schedule/AutoSchedule_debug_");
	
	public void bindSchedule() {
		
		logger.debug("============================AutoSchedule 开始执行============================");
		
		logger.debug("====================== autoFollow方法  开始执行=====================");
		autoFollow();
		logger.debug("====================== autoFollow方法  结束执行======================");
		
		logger.debug("====================== autoRecive方法  开始执行=====================");
		autoRecive();
		logger.debug("====================== autoRecive方法  结束执行======================");
		
		logger.debug("============================AutoSchedule 结束执行============================");
	}
	
	public void autoFollow() {
		List<Accounts> accountsList = groupService.getPublishRaiseAccountsList();
		if(!accountsList.isEmpty()) {
			AccountsMeta am = new AccountsMeta();
			for(Accounts accounts: accountsList) {
				am.setGid(accounts.getGid());
				
				logger.debug("~~~~~~~~~~~~~~autoFollow执行中，正在处理AccountsMeta：" + accounts.getGid()+"~~~~~~~~~~~~~~");
				
				AccountsMeta accountsMeta = groupService.getAccountsMeta(am);
				if(null == accountsMeta) {
					continue;
				}
				else {
					Order on = new Order();
					on.setOutTradeNo(accounts.getGid().toString());
					Order mainOrder = cashService.getOrderByOutTradeNo(on);
					mainOrder.setOrderId(mainOrder.getId());
					//List<Order> orderList = cashService.getFollowListOrder(mainOrder);
					Integer orderListSize = cashService.getFollowListOrderSize(mainOrder);
					if(accountsMeta.getTask().equals(0) && accountsMeta.getType().equals(0)) {
						continue;
					}
					else if(accountsMeta.getType().equals(1)) {
						if(orderListSize >= Integer.parseInt(mainOrder.getExpand())) {
							accountsMeta.setType(0);
							groupService.updateAccountsMeta(accountsMeta);
							continue;
						}
						else {
							Random rand = new Random();
							Integer randStart = rand.nextInt(AUTO_START);
							Integer randLimit = rand.nextInt(AUTO_SIZE);
							if(randLimit.equals(0)) {
								continue;
							}
							else {
								List<User> userList = userService.selectRandSysUseList(randStart, randLimit);
								if(mainOrder.getFeeTotal() > 1) {
									randStart = rand.nextInt(MIN_SIZE);
									userList = userService.selectRandHighSysUseList(randStart, randLimit);
								}
								Order followOrder = new Order();
								followOrder.setOrderId(mainOrder.getId());
								for(User user: userList) {
									followOrder.setUserId(user.getUserId());
									Order hasFollowOrder = cashService.hasGetOrder(followOrder);
									if(null != hasFollowOrder) {
										continue;
									}
									else {
										Date timeStart = new Date();
										
										CashAccount cashAccount = cashService.getCashAccountByUserId(user.getUserId());
										if(cashAccount.getTotalCash() - mainOrder.getFeeTotal() < 0) {
											continue;
										}
										cashAccount.setTotalCash(cashAccount.getTotalCash() - mainOrder.getFeeTotal());
										
										//系统用户订阅不需要花钱
										//cashAccount.setTotalCash(cashAccount.getTotalCash());
										cashAccount.setTimeTill(timeStart);
										cashService.updateCashAccount(cashAccount);
										
										orderListSize = cashService.getFollowListOrderSize(mainOrder);
										if(orderListSize >= Integer.parseInt(mainOrder.getDetail())) {
											break;
										}
										String transactionId = timeStart.getTime() + "";
										String outTradeNo = transactionId;
										followOrder.setTransactionId(transactionId);
										followOrder.setOutTradeNo(outTradeNo);
										followOrder.setTimeStart(timeStart);
										followOrder.setTimeExpire(accounts.getEndtime());
										followOrder.setTradeType(17);
										followOrder.setFeeTotal(mainOrder.getFeeTotal());
										
										//系统用户订阅不需要花钱
										//followOrder.setFeeTotal(0);
										
										
										followOrder.setFeeLeft(0);
										followOrder.setBody("组合付费");
										followOrder.setDetail("1");
										followOrder.setStatus(5);
										cashService.initOrder(followOrder);
										
										Relation relation = new Relation();
										relation.setGid(accounts.getGid());
										relation.setSubgid(0);
										relation.setUid(user.getUserId());
										relation.setDel(0);
										relation.setAmount(0);
										relation.setCreatetime(timeStart);
										relation.setAttr(1);
										Integer hasFocus = groupService.initRelation(relation);
										if(hasFocus > 0) {
											accounts.setGznum(accounts.getGznum() + 1);
											groupService.updateAccounts(accounts);
										}
											
									}
								}
							}
						}
					}
					else if(!accountsMeta.getTask().equals(0)) {
						Integer task = accountsMeta.getTask();
						Random rand = new Random();
						Integer randStart = rand.nextInt(AUTO_START);
						Integer randLimit = rand.nextInt(AUTO_SIZE);
						if(randLimit.equals(0)) {
							continue;
						}
						else {
							List<User> userList = userService.selectRandSysUseList(randStart, randLimit);
							if(mainOrder.getFeeTotal() > 1) {
								randStart = rand.nextInt(MIN_SIZE);
								userList = userService.selectRandHighSysUseList(randStart, randLimit);
							}
							if(userList.size() + orderListSize > Integer.parseInt(mainOrder.getDetail())) {
								continue;
							}
							Order followOrder = new Order();
							followOrder.setOrderId(mainOrder.getId());
							for(User user: userList) {
								followOrder.setUserId(user.getUserId());
								Order hasFollowOrder = cashService.hasGetOrder(followOrder);
								if(null != hasFollowOrder) {
									continue;
								}
								else {
									if(task.equals(0)) {
										break;
									}
									Date timeStart = new Date();
									
									CashAccount cashAccount = cashService.getCashAccountByUserId(user.getUserId());
									if(cashAccount.getTotalCash() - mainOrder.getFeeTotal() < 0) {
										continue;
									}
									cashAccount.setTotalCash(cashAccount.getTotalCash() - mainOrder.getFeeTotal());
									cashAccount.setTimeTill(timeStart);
									cashService.updateCashAccount(cashAccount);
									
									
									String transactionId = timeStart.getTime() + "";
									String outTradeNo = transactionId;
									followOrder.setTransactionId(transactionId);
									followOrder.setOutTradeNo(outTradeNo);
									followOrder.setTimeStart(timeStart);
									followOrder.setTimeExpire(accounts.getEndtime());
									followOrder.setTradeType(17);
									followOrder.setFeeTotal(mainOrder.getFeeTotal());
									followOrder.setFeeLeft(0);
									followOrder.setBody("组合付费");
									followOrder.setDetail("1");
									followOrder.setStatus(5);
									cashService.initOrder(followOrder);
									
									Relation relation = new Relation();
									relation.setGid(accounts.getGid());
									relation.setSubgid(0);
									relation.setUid(user.getUserId());
									relation.setDel(0);
									relation.setAmount(0);
									relation.setCreatetime(timeStart);
									relation.setAttr(1);
									Integer hasFocus = groupService.initRelation(relation);
									if(hasFocus > 0) {
										accounts.setGznum(accounts.getGznum() + 1);
										groupService.updateAccounts(accounts);
									}
									task -= 1;
								}
							}
							accountsMeta.setTask(task);
							groupService.updateAccountsMeta(accountsMeta);
						}
					}
				}
				
				logger.debug("~~~~~~~~~~~~~~ autoFollow执行中，处理完AccountsMeta：" + accounts.getGid()+"~~~~~~~~~~~~~~");
			}
			
			logger.debug("~~~~~~~~~~~~~~ autoFollow执行完毕，共处理：" + accountsList.size() + " 个AccountsMeta~~~~~~~~~~~~~~");
		}
		
	}
	
	public void autoRecive() {
		List<Order> orderList = cashService.getAllPublishBonusOrder();
		for(Order order:orderList) {
			Order mainOrder = cashService.getOrderById(order.getOrderId());
			
			logger.debug("~~~~~~~~~~~~~~ autoRecive执行中，正在处理Order：" + mainOrder.getId()+"~~~~~~~~~~~~~~");
			
//			List<Order> realFollowOrderList = cashService.getRealFollowListOrder(order);
//			double giveBonus = Double.parseDouble(order.getExpand());
			double average = order.getFeeTotal() / Integer.parseInt(order.getDetail());
			double min;
			double max;
			Integer left = Integer.parseInt(order.getDetail());
			Integer realleft = Integer.parseInt(order.getExpandDetail());
			
			double feeMin = 0.01f;
			double feeMax = order.getFeeLeft() - (left-1) * feeMin;
			
			min = feeMin > average/10?feeMin:average/10;
			max = feeMax > average*3?average*3:feeMax;
			
			Random rand = new Random();
			Integer randStart = rand.nextInt(Integer.parseInt(mainOrder.getExpand()));
			Integer size = AUTO_GET;
			List<Order> randSysFollowList = cashService.getRandSysFollowListOrder(order.getOrderId(), randStart, size);
			float amount = 0;
			if(!randSysFollowList.isEmpty()) {
				for(Order o:randSysFollowList) {
					if(left - realleft == 0) {
						break;
					}
					
					Integer userId = o.getUserId();
					Date timeStart = new Date();
					Date timeExpire = timeStart;
					String transactionId = timeStart.getTime() + "";
					String outTradeNo = transactionId;
					Integer tradeType = 6;
					Integer status = 0;
					float feeTotal = (float)(Math.round(((float)Math.random() * (max-min) + min)*100)) / 100;
					if(feeTotal <= 0) {
						break;
					}
					if(left - realleft <= MIN_LEFT) {
						break;
					}
					if(order.getFeeLeft() - amount - feeTotal <= Float.parseFloat(order.getExpand())) {
						break;
					}
					
					Order bonusOrder = new Order();
					bonusOrder.setTransactionId(transactionId);
					bonusOrder.setOutTradeNo(outTradeNo);
					bonusOrder.setTimeStart(timeStart);
					bonusOrder.setOrderId(order.getId());
					bonusOrder.setUserId(userId);
					bonusOrder.setTimeExpire(timeExpire);
					bonusOrder.setTradeType(tradeType);
					bonusOrder.setFeeTotal(feeTotal);
					bonusOrder.setFeeLeft(0);
					bonusOrder.setBody("领取红包");
					bonusOrder.setDetail(order.getUserId().toString());
					bonusOrder.setStatus(status);
					Order hasGetBonus = cashService.hasGetBonusOrder(bonusOrder);
					if(null != hasGetBonus) {
						continue;
					}
					Integer hasInsert = cashService.initOrder(bonusOrder);	
					if(hasInsert > 0) {
						CashAccount userAccount = cashService.getCashAccountByUserId(userId);
						userAccount.setTotalCash(userAccount.getTotalCash() + feeTotal);
						userAccount.setTimeTill(timeStart);
						cashService.updateCashAccount(userAccount);
						amount += feeTotal;
						left --;
						if(left <=0 || order.getFeeLeft() - amount <=0) {
							break;
						}
					}
				}
				order.setDetail(left.toString());
				order.setFeeLeft(order.getFeeLeft() - amount);
				if(left == 0 || order.getFeeLeft() - amount == 0) {
					order.setStatus(0);
				}
				if(amount >0) {
					cashService.updateOrder(order);
				}
			}
			logger.debug("~~~~~~~~~~~~~~ autoRecive执行中，处理完Order：" + mainOrder.getId()+"~~~~~~~~~~~~~~");
		}
		
		logger.debug("~~~~~~~~~~~~~~ autoRecive执行完毕，共处理：" + orderList.size() + " 个Order~~~~~~~~~~~~~~");
	}

}
