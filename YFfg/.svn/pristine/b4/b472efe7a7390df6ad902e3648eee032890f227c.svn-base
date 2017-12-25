package com.yfwl.yfgp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.easemodrest.demo.EasemobMessages;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Coupon;
import com.yfwl.yfgp.model.Mem;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.ServicePrice;
import com.yfwl.yfgp.model.SystemService;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.ValidateCode;
import com.yfwl.yfgp.service.AccountsService2;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.FutureService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.SMSendValiCodeService;
import com.yfwl.yfgp.service.ServicePriceService;
import com.yfwl.yfgp.service.UserAttentionService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.service.ValidateCodeService;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.Time;

@Controller
@RequestMapping("/cash")
public class CashController extends BaseController{
	@Autowired
	CashService cashService;
	@Autowired
	UserService userService;
	@Autowired
	ValidateCodeService validateCodeService;
	@Autowired
	FutureService futureService;
	@Autowired
	UserAttentionService userAttentionService;
	@Autowired
	private SMSendValiCodeService sMSandValiCodeService;
	@Autowired
	private ServicePriceService servicePriceService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private AccountsService2 accountsService2;
	
	
	private Object lock = new Object();
	private static final float INIT_PAY = 980.f;
	private static final int PAY_CONTENT = 25;
	private static final int PAY_USER = 26;
	private static final int PAY_SYSTEM = 27;
	private static final int PAY_STRATEGY = 41;
	private static final int TRY_STRATEGY = 42;
	private static final int PAY_ADVANCE = 43;
	private static final int TRY_ADVANCE = 44;
	public static final int PAY_CONFIGURATION = 45;
	public static final int TRY_CONFIGURATION = 46;
	
	//现金或者金券购买首页推荐组合
	public static final int PAY_HOME_ACCOUTS= 47;
	//现金或者金券试用首页推荐组合
	public static final int TRY_HOME_ACCOUTS= 48;
	
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	
	@RequestMapping(value = "/hasusehomeaccounts", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> hasBuyOrTryHomeAccounts(HttpServletRequest request,
			HttpServletResponse response) {
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		
		String uid = request.getParameter("userId");
		String gidStr = request.getParameter("gid");
		if(uid != null && !uid.isEmpty() && gidStr != null && !gidStr.isEmpty()){
			Integer userId = Integer.parseInt(uid);
			Order paramOrder = new Order();
			paramOrder.setUserId(userId);
			paramOrder.setDetail(gidStr);
			paramOrder.setTradeType(TRY_HOME_ACCOUTS);
			Order tryOrder = cashService.hasPayContentOrder(paramOrder);
			Map<String, Object> trymap = new HashMap<String, Object>();

			if(tryOrder !=  null){
				trymap.put("userId", userId);
				trymap.put("gid", gidStr);
				trymap.put("timeStart", tryOrder.getTimeStart());
				trymap.put("timeExpire", tryOrder.getTimeExpire());
			}
			
			Order paramOrder2 = new Order();
			paramOrder2.setUserId(userId);
			paramOrder2.setDetail(gidStr);
			paramOrder2.setTradeType(PAY_HOME_ACCOUTS);
			Order buyOrder = cashService.hasBuyHomeAccounts(paramOrder2);
			Map<String, Object> paymap = new HashMap<String, Object>();

			if(buyOrder !=  null){
				paymap.put("userId", userId);
				paymap.put("gid", gidStr);
				paymap.put("timeStart", buyOrder.getTimeStart());
				paymap.put("timeExpire", buyOrder.getTimeExpire());
			}
			
			Map<String, Object> resultmap = new HashMap<String, Object>();
			resultmap.put("try", trymap);
			resultmap.put("pay", paymap);
			map = rspFormat(resultmap, SUCCESS);
			
		}else{
			map = rspFormat("", WRONG_PARAM);
		}
		
		return map;
	}
	
	
	//获取服务价格
	@RequestMapping(value = "/getprice", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getServicePrice(HttpServletRequest request,
			HttpServletResponse response) {
		
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object>map = new HashMap<String, Object>();
		String contentidStr = request.getParameter("contentid");
		String code = request.getParameter("code");
		if( contentidStr != null && !contentidStr.isEmpty()){
			Integer contentid = Integer.parseInt(contentidStr);
			SystemService servicePrice = servicePriceService.getServicePriceByContentId(contentid);
			map = rspFormat(servicePrice, SUCCESS);
		}else{
			if( code != null && !code.isEmpty()){
				ServicePrice servicePrice = new ServicePrice();
				servicePrice.setCode(code);
				ServicePrice sp = servicePriceService.getServicePriceByCode(servicePrice);
				map = rspFormat(sp, SUCCESS);
			}else{
				map = rspFormat("", WRONG_PARAM);
			}
		}
		return map;
	}	
	
	//试用首页推荐组合
	@RequestMapping(value = "/tryhomeaccounts", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> tryForHomeAccounts(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String uid = request.getParameter("userId");
		String gidStr = request.getParameter("gid");
		if(uid != null && !uid.isEmpty() && gidStr != null && !gidStr.isEmpty()){
			Integer userId = Integer.parseInt(uid);
			Integer gid = Integer.parseInt(gidStr);
			Order paramOrder = new Order();
			paramOrder.setUserId(userId);
			paramOrder.setDetail(gidStr);
			paramOrder.setTradeType(TRY_HOME_ACCOUTS);
			Order order = cashService.hasPayContentOrder(paramOrder);
			if(order == null){
				
				Date timeStart = new Date();
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.MONTH, 1);
			    //过期时间
				Date timeExpire = c.getTime();
				
				//用户账号付款
				Order tryOrder = new Order();
				tryOrder.setTransactionId(new Date().getTime()+"");
				tryOrder.setOutTradeNo(new Date().getTime()+"");
				tryOrder.setUserId(userId);
				tryOrder.setOrderId(0);
				tryOrder.setTimeStart(timeStart);
				tryOrder.setTimeExpire(timeExpire);
				tryOrder.setTradeType(TRY_HOME_ACCOUTS);
				tryOrder.setFeeTotal(0);
				tryOrder.setFeeOrigin(0);
				tryOrder.setFeeLeft(0);
				tryOrder.setBody("试用首页推荐组合，组合id为"+gidStr);
				tryOrder.setDetail(gidStr);
				tryOrder.setExpand("试用首页推荐组合，组合id为"+gidStr);
				tryOrder.setExpandDetail("试用首页推荐组合，组合id为"+gidStr);
				tryOrder.setStatus(0);
				cashService.initOrder(tryOrder);

				//添加到已订阅
				Relation relation = new Relation();
				relation.setGid(gid);
				relation.setUid(userId);
				relation.setSubgid(0);
				relation.setAmount(0.00);
				relation.setAttr(1);
				relation.setCreatetime(new Date());
				groupService.initRelation(relation);

				//更新组合订阅数
				accountsService2.updateGZnum(gid);
				
				Map<String, Object> datamap = new HashMap<String, Object>();
				datamap.put("userId", userId);
				datamap.put("gid", gidStr);
				datamap.put("timeStart", timeStart);
				datamap.put("timeExpire", timeExpire);
				
				map = rspFormat(datamap, SUCCESS);
			}else{
				map = rspFormat("", HAS_TRY);
			}
			
		}else{
			map = rspFormat("", WRONG_PARAM);
		}
		return map;
	}
	
	
	
	//购买首页推荐组合
	@RequestMapping(value = "/payhomeaccounts", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payForHomeAccounts(HttpServletRequest request,
			HttpServletResponse response) {
		
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		String uid = request.getParameter("userId");
		String gidStr = request.getParameter("gid");
		//钱或者金券
		String currency = request.getParameter("currency");
		String monthStr = request.getParameter("month");
		String password = request.getParameter("password");
		
		if(uid != null && !uid.isEmpty() && gidStr != null && !gidStr.isEmpty()&& currency != null && !currency.isEmpty()&& monthStr != null && !monthStr.isEmpty()){
			
			Integer userId = Integer.parseInt(uid);
			Integer gid = Integer.parseInt(gidStr);
			Integer month = Integer.parseInt(monthStr);
			
			Integer moneyMonth = month;
			if(month >= 6){
				moneyMonth = month - 1;
			}
			if(month >= 12){
				moneyMonth = month - 2;
			}
			
			if(currency.equals("money")){
				//用钱买
				CashAccount userCashAccount = cashService.getCashAccountByUserId(userId);
				if(password == null || userCashAccount.getPassword().equals(MD5Util.string2MD5(password))){
					SystemService servicePrice = servicePriceService.getServicePriceByContentId(gid);
					float price = servicePrice.getPrice();
					float needPay = moneyMonth * price;
					float dbMoney = userCashAccount.getTotalCash();
					
					if(dbMoney >= needPay){
						Order order = new Order();
						order.setUserId(userId);
						order.setDetail(gid.toString());
						Order hasBuyOrder = cashService.hasBuyHomeAccounts(order);
						if(hasBuyOrder == null){
							//第一次购买
							Date timeStart = new Date();
							Date timeExpire;
							String transactionId = timeStart.getTime() + "";
							//判断有没有试用过
							Order paramOrder = new Order();
							paramOrder.setUserId(userId);
							paramOrder.setDetail(gidStr);
							paramOrder.setTradeType(TRY_HOME_ACCOUTS);
							Order tryOrder = cashService.hasPayContentOrder(paramOrder);
							if(tryOrder == null){
								//没有试用过
								Calendar c = Calendar.getInstance();
							    c.setTime(timeStart);
							    c.add(Calendar.MONTH, month);
							    //过期时间
								timeExpire = c.getTime();
							}else{
								//试用过
								//判断试用是否过期
								Date tryTimeExopire = tryOrder.getTimeExpire();
								if(tryTimeExopire.after(new Date())){
									//还未过试用期
									//过期时间 = 试用订单过期时间 + 购买时间
									Calendar c = Calendar.getInstance();
								    c.setTime(tryTimeExopire);
								    c.add(Calendar.MONTH, month);
									timeExpire = c.getTime();
									
								}else{
									//已过试用期
									Calendar c = Calendar.getInstance();
								    c.setTime(timeStart);
								    c.add(Calendar.MONTH, month);
									timeExpire = c.getTime();
								}
							}
							
							//用户账号付款
							Order payOrder = new Order();
							payOrder.setTransactionId(new Date().getTime()+"");
							payOrder.setOutTradeNo(new Date().getTime()+"");
							payOrder.setUserId(userId);
							payOrder.setOrderId(0);
							payOrder.setTimeStart(timeStart);
							payOrder.setTimeExpire(timeExpire);
							payOrder.setTradeType(PAY_HOME_ACCOUTS);
							payOrder.setFeeTotal(needPay);
							payOrder.setFeeOrigin(0);
							payOrder.setFeeLeft(0);
							payOrder.setBody("现金购买首页推荐组合，组合id为"+gid);
							payOrder.setDetail(gid+"");
							payOrder.setExpand("现金购买首页推荐组合，组合id为"+gid);
							payOrder.setExpandDetail("现金购买首页推荐组合，组合id为"+gid);
							payOrder.setStatus(0);
							cashService.initOrder(payOrder);
							Integer payOrderId = payOrder.getId();
							userCashAccount.setTotalCash(dbMoney-needPay);
							cashService.updateCashAccount(userCashAccount);
							
							//系统账号收款
							Order acceptOrder = new Order();
							acceptOrder.setTransactionId(transactionId);
							acceptOrder.setOutTradeNo(transactionId);
							acceptOrder.setUserId(0);
							acceptOrder.setOrderId(payOrderId);
							acceptOrder.setTimeStart(timeStart);
							acceptOrder.setTimeExpire(timeStart);
							acceptOrder.setTradeType(22);
							acceptOrder.setFeeTotal(needPay);
							acceptOrder.setFeeOrigin(0);
							acceptOrder.setFeeLeft(0);
							acceptOrder.setBody("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setDetail("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setExpand("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setExpandDetail("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setStatus(0);
							cashService.initOrder(acceptOrder);
							CashAccount systemCashAccount = cashService.getCashAccountByUserId(0);
							float systemCashAccountTotal = systemCashAccount.getTotalCash();
							systemCashAccount.setTotalCash(systemCashAccountTotal+needPay);
							cashService.updateCashAccount(systemCashAccount);
							payOrder.setOrderId(acceptOrder.getId());
							cashService.updateOrderId(payOrder);
							
							//添加到已订阅
							Relation relation = new Relation();
							relation.setGid(gid);
							relation.setUid(userId);
							relation.setSubgid(0);
							relation.setAmount(0.00);
							relation.setAttr(1);
							relation.setCreatetime(new Date());
							groupService.initRelation(relation);
							
							//更新组合订阅数
							accountsService2.updateGZnum(gid);
							
							Map<String, Object> datamap = new HashMap<String, Object>();
							datamap.put("userId", userId);
							datamap.put("gid", gid);
							datamap.put("timeStart", timeStart);
							datamap.put("timeExpire", timeExpire);
							
							map = rspFormat(datamap, SUCCESS);
						}else{
							//非第一次购买
							Date expireTime = hasBuyOrder.getTimeExpire();
							if(expireTime.after(new Date())){
								//还未过期，续时间
								Calendar c = Calendar.getInstance();
							    c.setTime(expireTime);
							    c.add(Calendar.MONTH, month);
							    //新过期时间
							    Date newExpireTime = c.getTime();
							    hasBuyOrder.setTimeExpire(newExpireTime);
							}else{
								//早已过期
								//新开始时间
								Date timeStart = new Date();
								Calendar c = Calendar.getInstance();
							    c.setTime(timeStart);
							    c.add(Calendar.MONTH, month);
							    //新过期时间
								Date newExpireTime = c.getTime();
								hasBuyOrder.setTimeStart(timeStart);
								hasBuyOrder.setTimeExpire(newExpireTime);
							}
						    hasBuyOrder.setFeeTotal(needPay);
						    hasBuyOrder.setBody("现金购买首页推荐组合，组合id为"+gid);
						    hasBuyOrder.setExpand("现金购买首页推荐组合，组合id为"+gid);
						    hasBuyOrder.setExpandDetail("现金购买首页推荐组合，组合id为"+gid);
						    cashService.updateOrder2(hasBuyOrder);
						    
							userCashAccount.setTotalCash(dbMoney-needPay);
							cashService.updateCashAccount(userCashAccount);
							
							//系统账号收款
							Order acceptOrder = new Order();
							String transactionId = new Date().getTime() + "";
							acceptOrder.setTransactionId(transactionId);
							acceptOrder.setOutTradeNo(transactionId);
							acceptOrder.setUserId(0);
							acceptOrder.setOrderId(hasBuyOrder.getId());
							acceptOrder.setTimeStart(new Date());
							acceptOrder.setTimeExpire(new Date());
							acceptOrder.setTradeType(22);
							acceptOrder.setFeeTotal(needPay);
							acceptOrder.setFeeOrigin(0);
							acceptOrder.setFeeLeft(0);
							acceptOrder.setBody("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setDetail("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setExpand("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setExpandDetail("收款，用户id为"+userId+"的用户购买首页推荐组合，组合id为"+gid);
							acceptOrder.setStatus(0);
							cashService.initOrder(acceptOrder);
							CashAccount systemCashAccount = cashService.getCashAccountByUserId(0);
							float systemCashAccountTotal = systemCashAccount.getTotalCash();
							systemCashAccount.setTotalCash(systemCashAccountTotal+needPay);
							cashService.updateCashAccount(systemCashAccount);
							hasBuyOrder.setOrderId(acceptOrder.getId());
							cashService.updateOrderId(hasBuyOrder);
							
							Map<String, Object> datamap = new HashMap<String, Object>();
							datamap.put("userId", userId);
							datamap.put("gid", gid);
							datamap.put("timeStart", hasBuyOrder.getTimeStart());
							datamap.put("timeExpire", hasBuyOrder.getTimeExpire());
							
							map = rspFormat(datamap, SUCCESS);
						}
						
					}else{
						//余额不足不能购买
						map = rspFormat("", NO_MONEY);
					}
				}else{
					map = rspFormat("", WRONG_PASSWORD);
				}
				
			}else if(currency.equals("coupon")){
				//用金券买
				SystemService servicePrice = servicePriceService.getServicePriceByContentId(gid);
				Integer couponNumNeed = servicePrice.getCoupon();
				
				Coupon cp = new Coupon();
				cp.setUserId(userId);
				List<Coupon> listOfCoupon = futureService.getCoupon(cp);
				Integer needNum = couponNumNeed * month;
				if(listOfCoupon.size() >= needNum){
					
					Order order = new Order();
					order.setUserId(userId);
					order.setDetail(gid.toString());
					Order hasBuyOrder = cashService.hasBuyHomeAccounts(order);
					if(hasBuyOrder == null){
						//第一次购买
						//起始时间
						Date timeStart = new Date();
						Date timeExpire;
						//判断有没有试用过
						Order paramOrder = new Order();
						paramOrder.setUserId(userId);
						paramOrder.setDetail(gidStr);
						paramOrder.setTradeType(TRY_HOME_ACCOUTS);
						Order tryOrder = cashService.hasPayContentOrder(paramOrder);
						if(tryOrder == null){
							//没有试用过
							Calendar c = Calendar.getInstance();
						    c.setTime(timeStart);
						    c.add(Calendar.MONTH, month);
						    //过期时间
							timeExpire = c.getTime();
						}else{
							//试用过
							//判断试用是否过期
							Date tryTimeExopire = tryOrder.getTimeExpire();
							if(tryTimeExopire.after(new Date())){
								//还未过试用期
								//过期时间 = 试用订单过期时间 + 购买时间
								Calendar c = Calendar.getInstance();
							    c.setTime(tryTimeExopire);
							    c.add(Calendar.MONTH, month);
								timeExpire = c.getTime();
								
							}else{
								//已过试用期
								Calendar c = Calendar.getInstance();
							    c.setTime(timeStart);
							    c.add(Calendar.MONTH, month);
								timeExpire = c.getTime();
							}
						}
						
						//生成一条金券购买订单
						Order payOrder = new Order();
						payOrder.setTransactionId(new Date().getTime()+"");
						payOrder.setOutTradeNo(new Date().getTime()+"");
						payOrder.setUserId(userId);
						payOrder.setOrderId(0);
						payOrder.setTimeStart(timeStart);
						payOrder.setTimeExpire(timeExpire);
						payOrder.setTradeType(PAY_HOME_ACCOUTS);
						payOrder.setFeeTotal(0);
						payOrder.setFeeOrigin(0);
						payOrder.setFeeLeft(0);
						payOrder.setBody("金券购买首页推荐组合，组合id为"+gid);
						payOrder.setDetail(gid+"");
						payOrder.setExpand("金券购买首页推荐组合，组合id为"+gid);
						payOrder.setExpandDetail("金券购买首页推荐组合，组合id为"+gid);
						payOrder.setStatus(0);
						cashService.initOrder(payOrder);
						
						//把金券置为已消费状态
						for(int i=0;i<needNum;i++){
							Coupon coupon = listOfCoupon.get(i);
							coupon.setUseTime(new Date());
							coupon.setConsumeType(4);
							coupon.setConsumeInfo("金券购买首页推荐组合，组合id为"+gid);
							coupon.setStatus(1);
							futureService.updateCoupon2(coupon);
						}
						
						//添加到已订阅
						Relation relation = new Relation();
						relation.setGid(gid);
						relation.setUid(userId);
						relation.setSubgid(0);
						relation.setAmount(0.00);
						relation.setAttr(1);
						relation.setCreatetime(new Date());
						groupService.initRelation(relation);

						//更新组合订阅数
						accountsService2.updateGZnum(gid);
						
						Map<String, Object> datamap = new HashMap<String, Object>();
						datamap.put("userId", userId);
						datamap.put("gid", gid);
						datamap.put("timeStart", timeStart);
						datamap.put("timeExpire", timeExpire);
						
						map = rspFormat(datamap, SUCCESS);
					}else{
						//非第一次购买
						Date expireTime = hasBuyOrder.getTimeExpire();
						if(expireTime.after(new Date())){
							//还未过期，续时间
							Calendar c = Calendar.getInstance();
						    c.setTime(expireTime);
						    c.add(Calendar.MONTH, month);
						    //新过期时间
						    Date newExpireTime = c.getTime();
						    hasBuyOrder.setTimeExpire(newExpireTime);
						}else{
							//早已过期
							Date timeStart = new Date();
							Calendar c = Calendar.getInstance();
						    c.setTime(timeStart);
						    c.add(Calendar.MONTH, month);
						    //过期时间
							Date newExpireTime = c.getTime();
							hasBuyOrder.setTimeStart(timeStart);
							hasBuyOrder.setTimeExpire(newExpireTime);
						}
					    hasBuyOrder.setFeeTotal(0);
					    hasBuyOrder.setBody("金券购买首页推荐组合，组合id为"+gid);
						hasBuyOrder.setExpand("金券购买首页推荐组合，组合id为"+gid);
						hasBuyOrder.setExpandDetail("金券购买首页推荐组合，组合id为"+gid);
					    cashService.updateOrder2(hasBuyOrder);
					    
					    //把张金券置为已消费状态
						for(int i=0;i<needNum;i++){
							Coupon coupon = listOfCoupon.get(i);
							coupon.setUseTime(new Date());
							coupon.setConsumeType(4);
							coupon.setConsumeInfo("金券购买首页推荐组合，组合id为"+gid);
							coupon.setStatus(1);
							futureService.updateCoupon2(coupon);
						}
						
						Map<String, Object> datamap = new HashMap<String, Object>();
						datamap.put("userId", userId);
						datamap.put("gid", gid);
						datamap.put("timeStart", hasBuyOrder.getTimeStart());
						datamap.put("timeExpire", hasBuyOrder.getTimeExpire());
						
						map = rspFormat(datamap, SUCCESS);
					}
					
				}else{
					map = rspFormat("", NO_JQ);
				}
			}else{
				map = rspFormat("", WRONG_PARAM);
			}
		}else{
			map = rspFormat("", WRONG_PARAM);
		}
		
		return map;
			
	}
	
	
	
	
	
	@RequestMapping(value = "/getcashaccount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getCashAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		CashAccount result = cashService.getCashAccountByUserId(userId);
		if(null != result) {
			result.setExpand("我们核实后，将在1-3个工作日内转账，收取2%的手续费");
			map = rspFormat(result, SUCCESS);
		}
		else {
			map = rspFormat(result, FAIL);
		}
		return map;
	}
	
	@RequestMapping(value = "/updatecashaccount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateCashAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String attachAccount = request.getParameter("attachAccount");
		String password = request.getParameter("password");
		User user = userService.selectUserById(userId);
		CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
		if(cashAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			cashAccount.setAttachAccount(attachAccount);
			cashAccount.setTimeTill(new Date());
			Integer hasUpdate = cashService.updateCashAccount(cashAccount);
			if(hasUpdate >0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", cashAccount);
			}
			else {
				map.put("status", 4);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/getCode", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getCode(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		User user = userService.selectUserById(userId);
		String phone = user.getPhone();
		String type = "ZFMM";
		
		boolean isOk = sMSandValiCodeService.sendSMS(phone, type);
		
		if(isOk == true){
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", "");
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/updatecashpassword", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateCashPassword(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer type = Integer.parseInt(request.getParameter("type"));
		String randomNum = request.getParameter("validateCode");
		String password = request.getParameter("password");
		User user = userService.selectUserById(userId);
		
		ValidateCode code = new ValidateCode();
		code.setPhone(user.getPhone());
		code.setMarker("ZFMM");
		code.setRandomNum(randomNum);
		
		boolean isOk = sMSandValiCodeService.validataCode(code);
		if(isOk == true){
			if(type == 0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", "");
			}
			else if(type == 1){
				if(password.length()==6){
					password = MD5Util.string2MD5(password);
					CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
					cashAccount.setPassword(password);
					cashAccount.setTimeTill(new Date());
					cashAccount.setStatus(1);
					Integer hasUpdate = cashService.updateCashAccount(cashAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", cashAccount);
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 5);
					map.put("message", "密码不为6位");
					map.put("data", "");
				}
			}
		}else{
			map.put("status", 8);
			map.put("message", "验证码错误");
			map.put("data", "");
		}
		
		return map;
	}
	
	@RequestMapping(value = "/cashtoaccount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> cashToAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		String attachAccount = request.getParameter("attachAccount");
		String password = request.getParameter("password");
		User user = userService.selectUserById(userId);
		CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
		if(cashAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			
			if(cashAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Float feeLeft = 0.00f;
				Date timeStart = new Date();
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.DAY_OF_MONTH, 1);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer tradeType = 3;
				Integer status = 1;
				
				Order useOrder = new Order();
				useOrder.setTransactionId(transactionId);
				useOrder.setOutTradeNo(outTradeNo);
				useOrder.setUserId(userId);
				useOrder.setOrderId(0);
				useOrder.setTimeStart(timeStart);
				useOrder.setTimeExpire(timeExpire);
				useOrder.setTradeType(tradeType);
				useOrder.setFeeTotal(feeTotal);
				useOrder.setFeeLeft(feeLeft);
				useOrder.setBody(attachAccount);
				useOrder.setDetail("");
				useOrder.setStatus(status);
				Integer hasInsert = cashService.initOrder(useOrder);
				if(hasInsert > 0) {
					cashAccount.setTotalCash(cashAccount.getTotalCash() - feeTotal);
					cashAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(cashAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", useOrder);
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/charge", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> charge(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		Float feeLeft = 0.00f;
		Date timeStart = new Date();
		Date timeExpire = timeStart;
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Integer tradeType = 0;
		Integer status = 0;
		if(feeTotal < 0) {
			map.put("status", 4);
			map.put("message", "参数错误");
			map.put("data", "");
		}
		else {
			Order order = new Order();
			order.setTransactionId(transactionId);
			order.setOutTradeNo(outTradeNo);
			order.setUserId(userId);
			order.setOrderId(0);
			order.setTimeStart(timeStart);
			order.setTimeExpire(timeExpire);
			order.setTradeType(tradeType);
			order.setFeeTotal(feeTotal);
			order.setFeeLeft(feeLeft);
			order.setBody("充值");
			order.setDetail("");
			order.setStatus(status);
			Integer hasInsert = cashService.initOrder(order);
			if(hasInsert > 0) {
				CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
				cashAccount.setTotalCash(cashAccount.getTotalCash() + feeTotal);
				cashAccount.setTimeTill(timeStart);
				Integer hasUpdate = cashService.updateCashAccount(cashAccount);
				if(hasUpdate >0) {
					map.put("status", 0);
					map.put("message", "操作成功");
					map.put("data", cashAccount);
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
			else {
				map.put("status", 4);
				map.put("message", "操作失败");
				map.put("data", "");
			}
			
		}
		
		
		
		return map;
	}
	
	@RequestMapping(value = "/reward", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> reward(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer rewardUserId = Integer.parseInt(request.getParameter("rewardUserId"));
		String password = request.getParameter("password");
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		Float feeLeft = 0.00f;
		Date timeStart = new Date();
		Date timeExpire = timeStart;
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Integer tradeType = 1;
		Integer status = 0;
		User user = userService.selectUserById(userId);
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if(userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Order useOrder = new Order();
				useOrder.setTransactionId(transactionId);
				useOrder.setOutTradeNo(outTradeNo);
				useOrder.setUserId(userId);
				useOrder.setOrderId(0);
				useOrder.setTimeStart(timeStart);
				useOrder.setTimeExpire(timeExpire);
				useOrder.setTradeType(tradeType);
				useOrder.setFeeTotal(feeTotal);
				useOrder.setFeeLeft(feeLeft);
				useOrder.setBody("打赏");
				useOrder.setDetail(rewardUserId.toString());
				useOrder.setStatus(status);
				Integer hasInsert = cashService.initOrder(useOrder);
				if(hasInsert > 0) {
					CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
					cashAccount.setTotalCash(cashAccount.getTotalCash() - feeTotal);
					cashAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(cashAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", cashAccount);
						
						timeStart = new Date();
						timeExpire = timeStart;
						transactionId = timeStart.getTime() + "";
						outTradeNo = transactionId;
						tradeType = 2;
						Order rewardUserOrder = new Order();
						rewardUserOrder.setTransactionId(transactionId);
						rewardUserOrder.setOutTradeNo(outTradeNo);
						rewardUserOrder.setUserId(rewardUserId);
						rewardUserOrder.setOrderId(useOrder.getId());
						rewardUserOrder.setTimeStart(timeStart);
						rewardUserOrder.setTimeExpire(timeExpire);
						rewardUserOrder.setTradeType(tradeType);
						rewardUserOrder.setFeeTotal(feeTotal);
						rewardUserOrder.setFeeLeft(feeLeft);
						rewardUserOrder.setBody("获得打赏");
						rewardUserOrder.setDetail(userId.toString());
						rewardUserOrder.setStatus(status);
						Integer hasInsertRewardUserOrder = cashService.initOrder(rewardUserOrder);
						if(hasInsertRewardUserOrder >0) {
							CashAccount rewardAccount = cashService.getCashAccountByUserId(rewardUserId);
							rewardAccount.setTotalCash(rewardAccount.getTotalCash() + feeTotal);
							rewardAccount.setTimeTill(timeStart);
							cashService.updateCashAccount(rewardAccount);
						}
						
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/givebonus", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> giveBonus(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("password");
		Integer tradeType = Integer.parseInt(request.getParameter("tradeType"));
		String body = request.getParameter("body");
		String detail = request.getParameter("detail");
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		Float feeLeft = feeTotal;
		User user = userService.selectUserById(userId);
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if(userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Date timeStart = new Date();
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.DAY_OF_MONTH, 1);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer status = 1;
				
				Order order = new Order();
				order.setTransactionId(transactionId);
				order.setOutTradeNo(outTradeNo);
				order.setUserId(userId);
				order.setOrderId(0);
				order.setTimeStart(timeStart);
				order.setTimeExpire(timeExpire);
				order.setTradeType(tradeType);
				order.setFeeTotal(feeTotal);
				order.setFeeLeft(feeLeft);
				order.setBody(body);
				order.setDetail(detail);
				order.setStatus(status);
				Integer hasInsert = cashService.initOrder(order);
				if(hasInsert > 0) {
					userAccount.setTotalCash(userAccount.getTotalCash() - feeTotal);
					userAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(userAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", order);
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/getbonus", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getBonus(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Map<String, Object> tmap = new HashMap<String, Object>();
		synchronized (lock)
		{
			Integer userId = Integer.parseInt(request.getParameter("userId"));
			Integer id = Integer.parseInt(request.getParameter("id"));
			Order order = new Order();
			order.setId(id);
			Order bonusOrder = cashService.getOrder(order);
			order.setOrderId(bonusOrder.getId());
			order.setUserId(userId);
			Order hasGetBonus = cashService.hasGetOrder(order);
			if(null != hasGetBonus) {
				tmap.put("self", hasGetBonus);
			}
			else {
				//tmap.put("self", "");
			}
			if(null !=bonusOrder) {
				if(bonusOrder.getStatus()==0 || Time.compareTime(bonusOrder.getTimeExpire(),new Date()) <= 0 ||
						Integer.parseInt(bonusOrder.getDetail())==0){
					List<Order> orderList = cashService.getListBonusOrderLimit(order);
					if(Integer.parseInt(bonusOrder.getDetail())==0) {
						Order maxOrder= cashService.getMaxOrder(id);
						for(int i=0; i < orderList.size(); i++) {
							if(orderList.get(i).getId().equals(maxOrder.getId())){
								orderList.get(i).setFeeMost(1);
							}
						}
					}
					tmap.put("user", bonusOrder);
					tmap.put("owner", orderList);
					map.put("status", 10);
					map.put("message", "红包已领完");
					map.put("data", tmap);
				}
				else if(bonusOrder.getTradeType().equals(4) && bonusOrder.getUserId().equals(userId)){
					
					List<Order> orderList = cashService.getListBonusOrderLimit(order);
					tmap.put("user", bonusOrder);
					tmap.put("owner", orderList);
					map.put("status", 12);
					map.put("message", "查看红包");
					map.put("data", tmap);
				}
				else {
					List<Order> orderList = cashService.getListBonusOrderLimit(order);
					tmap.put("user", bonusOrder);
					tmap.put("owner", orderList);
					if(null != hasGetBonus) {
						map.put("status", 11);
						map.put("message", "已领过该红包");
						map.put("data", tmap);
					}
					else {
						Float feeTotal = 0.00f;
						Float feeLeft = 0.00f;
						Date timeStart = new Date();
						Date timeExpire = timeStart;
						String transactionId = timeStart.getTime() + "";
						String outTradeNo = transactionId;
						Integer tradeType = 6;
						Integer status = 0;
						if(bonusOrder.getTradeType()==4) {
							feeTotal = bonusOrder.getFeeTotal();
							bonusOrder.setFeeLeft(0);
							bonusOrder.setDetail("0");
							bonusOrder.setTradeType(0);
							bonusOrder.setTimeExpire(timeExpire);
							bonusOrder.setStatus(0);
						}
						else if(bonusOrder.getTradeType()==5) {
							Integer leftNum = Integer.parseInt(bonusOrder.getDetail());
							float feeMin = 0.01f;
							float feeMax = bonusOrder.getFeeLeft() - (leftNum-1) * feeMin;
							feeTotal = (float)(Math.round(((float)Math.random() * (feeMax-feeMin) + feeMin)*100)) / 100;
							if(leftNum == 1) {
								feeTotal = bonusOrder.getFeeLeft();
							}
							leftNum -=1;
							bonusOrder.setFeeLeft(bonusOrder.getFeeLeft() - feeTotal);
							bonusOrder.setDetail(leftNum.toString());
							if(leftNum == 0) {
								bonusOrder.setStatus(0);
								bonusOrder.setTimeExpire(timeExpire);
							}
						}
						else if(bonusOrder.getTradeType()==24) {
							/*Integer leftNum = Integer.parseInt(bonusOrder.getDetail());
							
							feeTotal = (float)(Math.round(bonusOrder.getFeeLeft() / 2 * 100)) / 100;
							if(leftNum == 1 || bonusOrder.getFeeLeft() - feeTotal < 0.01) {
								feeTotal = bonusOrder.getFeeLeft();
							}
							leftNum -=1;
							bonusOrder.setFeeLeft(bonusOrder.getFeeLeft() - feeTotal);
							bonusOrder.setDetail(leftNum.toString());
							if(leftNum == 0) {
								bonusOrder.setStatus(0);
								bonusOrder.setDetail("0");
								bonusOrder.setTimeExpire(timeExpire);
							}*/
							String expand = bonusOrder.getExpand();
							if(expand != null && expand.length() != 0) {
								float giveBonus = (float)(Math.round(Float.parseFloat(expand)*100))/100;
								Integer leftNum = Integer.parseInt(bonusOrder.getExpandDetail());
								float average = giveBonus / leftNum;
								float feeMin = 0.01f;
								float feeMax = giveBonus - (leftNum-1) * feeMin;
								
								feeMin = feeMin > average/10?feeMin:average/10;
								feeMax = feeMax > average*3?average*3:feeMax;
								
								feeTotal = (float)(Math.round(((float)Math.random() * (feeMax-feeMin) + feeMin)*100)) / 100;
								if(leftNum == 1) {
									feeTotal = giveBonus;
								}
								leftNum -=1;
								float leftBonus = (float)(Math.round((float)(giveBonus - feeTotal)*100)) / 100;
								bonusOrder.setFeeLeft(bonusOrder.getFeeLeft() - feeTotal);
								Integer leftDetail = Integer.parseInt(bonusOrder.getDetail()) - 1;
								bonusOrder.setDetail(leftDetail.toString());
								bonusOrder.setExpand(leftBonus + "");
								bonusOrder.setExpandDetail(leftNum.toString());
								if(leftDetail == 0) {
									bonusOrder.setStatus(0);
									bonusOrder.setTimeExpire(timeExpire);
								}
							}
							else {
								Integer leftNum = Integer.parseInt(bonusOrder.getDetail());
								float average = bonusOrder.getFeeLeft() / leftNum;
								float feeMin = 0.01f;
								float feeMax = bonusOrder.getFeeLeft() - (leftNum-1) * feeMin;
								
								feeMin = feeMin > average/10?feeMin:average/10;
								feeMax = feeMax > average*3?average*3:feeMax;
								
								feeTotal = (float)(Math.round(((float)Math.random() * (feeMax-feeMin) + feeMin)*100)) / 100;
								if(leftNum == 1) {
									feeTotal = bonusOrder.getFeeLeft();
								}
								leftNum -=1;
								bonusOrder.setFeeLeft(bonusOrder.getFeeLeft() - feeTotal);
								bonusOrder.setDetail(leftNum.toString());
								if(leftNum == 0) {
									bonusOrder.setStatus(0);
									bonusOrder.setTimeExpire(timeExpire);
								}
							}
						}
						
						
						
						order.setTransactionId(transactionId);
						order.setOutTradeNo(outTradeNo);
						
						order.setTimeStart(timeStart);
						order.setTimeExpire(timeExpire);
						order.setTradeType(tradeType);
						order.setFeeTotal(feeTotal);
						order.setFeeLeft(feeLeft);
						order.setBody("领取红包");
						order.setDetail(bonusOrder.getUserId().toString());
						order.setStatus(status);
						Integer hasInsert = cashService.initOrder(order);
						if(hasInsert > 0) {
							
							CashAccount userAccount = cashService.getCashAccountByUserId(userId);
							userAccount.setTotalCash(userAccount.getTotalCash() + feeTotal);
							userAccount.setTimeTill(timeStart);
							Integer hasUpdate = cashService.updateCashAccount(userAccount);
							Integer hasUpdateBonus = cashService.updateOrder(bonusOrder);
							if(hasUpdate >0 && hasUpdateBonus > 0) {
								
								orderList = cashService.getListOrderLimit(order);
								if(Integer.parseInt(bonusOrder.getDetail())==0) {
									Order maxOrder= cashService.getMaxOrder(id);
									for(int i=0; i < orderList.size(); i++) {
										if(orderList.get(i).getId().equals(maxOrder.getId())){
											orderList.get(i).setFeeMost(1);
										}
									}
								}
								tmap.put("self", order);
								tmap.put("user", bonusOrder);
								tmap.put("owner", orderList);
								
								map.put("status", 0);
								map.put("message", "操作成功");
								map.put("data", tmap);
							}
							else {
								map.put("status", 4);
								map.put("message", "操作失败");
								map.put("data", "");
							}
						}
						else {
							map.put("status", 4);
							map.put("message", "操作失败");
							map.put("data", "");
						}
					}
				}
			}
			else {
				map.put("status", 4);
				map.put("message", "参数错误");
				map.put("data", "");
			}
	
			return map;
		}
	}
	
	@RequestMapping(value = "/payward", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payWard(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		synchronized (lock)
		{
			Integer id = Integer.parseInt(request.getParameter("id"));
			Integer userId = Integer.parseInt(request.getParameter("userId"));
			Integer rewardUserId = Integer.parseInt(request.getParameter("rewardUserId"));
			String password = request.getParameter("password");
			Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
			Float feeLeft = 0.00f;
			Date timeStart = new Date();
			Date timeExpire = timeStart;
			String transactionId = timeStart.getTime() + "";
			String outTradeNo = transactionId;
			Integer tradeType = 8;
			if(null != request.getParameter("tradeType")) {
				tradeType = Integer.parseInt(request.getParameter("tradeType"));
			}
			Integer status = 0;
			//User user = userService.selectUserById(userId);
			CashAccount userAccount = cashService.getCashAccountByUserId(userId);
			if(userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
				Order originOrder = cashService.getOrderById(id);
				if(originOrder.getStatus() == 0) {
					map.put("status", 5);
					map.put("message", "支付完成");
					map.put("data", "");
				}
				else if(userAccount.getTotalCash() < feeTotal){
					map.put("status", 3);
					map.put("message", "账户余额不足");
					map.put("data", "");
				}
				else {
					Order useOrder = new Order();
					useOrder.setTransactionId(transactionId);
					useOrder.setOutTradeNo(outTradeNo);
					useOrder.setUserId(userId);
					useOrder.setOrderId(id);
					useOrder.setTimeStart(timeStart);
					useOrder.setTimeExpire(timeExpire);
					useOrder.setTradeType(tradeType);
					useOrder.setFeeTotal(feeTotal);
					useOrder.setFeeLeft(feeLeft);
					useOrder.setBody("付款");
					useOrder.setDetail(rewardUserId.toString());
					useOrder.setStatus(status);
					Integer hasInsert = cashService.initOrder(useOrder);
					if(hasInsert > 0) {
						CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
						cashAccount.setTotalCash(cashAccount.getTotalCash() - feeTotal);
						cashAccount.setTimeTill(timeStart);
						Integer hasUpdate = cashService.updateCashAccount(cashAccount);
						if(hasUpdate >0) {
							map.put("status", 0);
							map.put("message", "操作成功");
							map.put("data", useOrder);
							
							timeStart = new Date();
							timeExpire = timeStart;
							transactionId = timeStart.getTime() + "";
							outTradeNo = transactionId;
							tradeType = 9;
							
							originOrder.setTimeExpire(timeExpire);
							originOrder.setFeeLeft(0);
							originOrder.setStatus(0);
							
							Integer hasInsertRewardUserOrder = cashService.updateOrder(originOrder);
							if(hasInsertRewardUserOrder >0) {
								CashAccount rewardAccount = cashService.getCashAccountByUserId(rewardUserId);
								rewardAccount.setTotalCash(rewardAccount.getTotalCash() + feeTotal);
								rewardAccount.setTimeTill(timeStart);
								cashService.updateCashAccount(rewardAccount);
							}
							
						}
						else {
							map.put("status", 4);
							map.put("message", "操作失败");
							map.put("data", "");
						}
					}
				}
			}
			else {
				map.put("status", 8);
				map.put("message", "密码错误");
				map.put("data", "");
			}
			return map;
		}
	}
	
	@RequestMapping(value = "/initpayward", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> initPayWard(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		
		String body = request.getParameter("body");
		String detail = request.getParameter("rewardUserId");
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		
		Date timeStart = new Date();
		Date timeExpire = timeStart;
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Integer tradeType = 10;
		Order rewardUserOrder = new Order();
		rewardUserOrder.setTransactionId(transactionId);
		rewardUserOrder.setOutTradeNo(outTradeNo);
		rewardUserOrder.setUserId(userId);
		rewardUserOrder.setOrderId(0);
		rewardUserOrder.setTimeStart(timeStart);
		rewardUserOrder.setTimeExpire(timeExpire);
		rewardUserOrder.setTradeType(tradeType);
		rewardUserOrder.setFeeTotal(feeTotal);
		rewardUserOrder.setFeeLeft(feeTotal);
		rewardUserOrder.setBody(body);
		rewardUserOrder.setDetail(detail);
		rewardUserOrder.setStatus(1);
		Integer hasInsertRewardUserOrder = cashService.initOrder(rewardUserOrder);
		if(hasInsertRewardUserOrder >0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", rewardUserOrder);
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/getpayward", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getPayWard(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		synchronized (lock)
		{
			Integer id = Integer.parseInt(request.getParameter("id"));
			Order order = cashService.getOrderById(id);
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", order);
			return map;
		}
	}
	

	@RequestMapping(value = "/getownorder", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getOwnOrder(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Order order = new Order();
		order.setUserId(userId);
		List<Order> ownOrder= cashService.getOwnOrder(order);
		if(ownOrder.size() == 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", "");
		}
		else {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", ownOrder);
		}
		return map;
	}
	
	
	
	@RequestMapping(value = "/givebonusnp", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> giveBonusNP(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("password");
		Integer tradeType = Integer.parseInt(request.getParameter("tradeType"));
		String body = request.getParameter("body");
		String detail = request.getParameter("detail");
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		Float feeLeft = feeTotal;
		User user = userService.selectUserById(userId);
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if(true||userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Date timeStart = new Date();
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.DAY_OF_MONTH, 1);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer status = 1;
				
				Order order = new Order();
				order.setTransactionId(transactionId);
				order.setOutTradeNo(outTradeNo);
				order.setUserId(userId);
				order.setOrderId(0);
				order.setTimeStart(timeStart);
				order.setTimeExpire(timeExpire);
				order.setTradeType(tradeType);
				order.setFeeTotal(feeTotal);
				order.setFeeLeft(feeLeft);
				order.setBody(body);
				order.setDetail(detail);
				order.setStatus(status);
				Integer hasInsert = cashService.initOrder(order);
				if(hasInsert > 0) {
					userAccount.setTotalCash(userAccount.getTotalCash() - feeTotal);
					userAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(userAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", order);
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	
	@RequestMapping(value = "/paywardnp", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payWardNP(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		synchronized (lock)
		{
			Integer id = Integer.parseInt(request.getParameter("id"));
			Integer userId = Integer.parseInt(request.getParameter("userId"));
			Integer rewardUserId = Integer.parseInt(request.getParameter("rewardUserId"));
			String password = request.getParameter("password");
			Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
			Float feeLeft = 0.00f;
			Date timeStart = new Date();
			Date timeExpire = timeStart;
			String transactionId = timeStart.getTime() + "";
			String outTradeNo = transactionId;
			Integer tradeType = 8;
			if(null != request.getParameter("tradeType")) {
				tradeType = Integer.parseInt(request.getParameter("tradeType"));
			}
			Integer status = 0;
			//User user = userService.selectUserById(userId);
			CashAccount userAccount = cashService.getCashAccountByUserId(userId);
			if(true||userAccount.getPassword().equals(MD5Util.string2MD5(password))) {	
				Order originOrder = cashService.getOrderById(id);
				if(originOrder.getStatus() == 0) {
					map.put("status", 5);
					map.put("message", "支付完成");
					map.put("data", "");
				}
				else if(userAccount.getTotalCash() < feeTotal){
					map.put("status", 3);
					map.put("message", "账户余额不足");
					map.put("data", "");
				}
				else {
					Order useOrder = new Order();
					useOrder.setTransactionId(transactionId);
					useOrder.setOutTradeNo(outTradeNo);
					useOrder.setUserId(userId);
					useOrder.setOrderId(id);
					useOrder.setTimeStart(timeStart);
					useOrder.setTimeExpire(timeExpire);
					useOrder.setTradeType(tradeType);
					useOrder.setFeeTotal(feeTotal);
					useOrder.setFeeLeft(feeLeft);
					useOrder.setBody("付款");
					useOrder.setDetail(rewardUserId.toString());
					useOrder.setStatus(status);
					Integer hasInsert = cashService.initOrder(useOrder);
					if(hasInsert > 0) {
						CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
						cashAccount.setTotalCash(cashAccount.getTotalCash() - feeTotal);
						cashAccount.setTimeTill(timeStart);
						Integer hasUpdate = cashService.updateCashAccount(cashAccount);
						if(hasUpdate >0) {
							map.put("status", 0);
							map.put("message", "操作成功");
							map.put("data", useOrder);
							
							timeStart = new Date();
							timeExpire = timeStart;
							transactionId = timeStart.getTime() + "";
							outTradeNo = transactionId;
							tradeType = 9;
							
							originOrder.setTimeExpire(timeExpire);
							originOrder.setFeeLeft(0);
							originOrder.setStatus(0);
							
							Integer hasInsertRewardUserOrder = cashService.updateOrder(originOrder);
							if(hasInsertRewardUserOrder >0) {
								CashAccount rewardAccount = cashService.getCashAccountByUserId(rewardUserId);
								rewardAccount.setTotalCash(rewardAccount.getTotalCash() + feeTotal);
								rewardAccount.setTimeTill(timeStart);
								cashService.updateCashAccount(rewardAccount);
							}
							
						}
						else {
							map.put("status", 4);
							map.put("message", "操作失败");
							map.put("data", "");
						}
					}
				}
			}
			else {
				map.put("status", 8);
				map.put("message", "密码错误");
				map.put("data", "");
			}
			return map;
		}
	}
	
	
	@RequestMapping(value = "/rewardnp", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> rewardNP(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer rewardUserId = Integer.parseInt(request.getParameter("rewardUserId"));
		String password = request.getParameter("password");
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		Float feeLeft = 0.00f;
		Date timeStart = new Date();
		Date timeExpire = timeStart;
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Integer tradeType = 1;
		Integer status = 0;
		User user = userService.selectUserById(userId);
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if(true||userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Order useOrder = new Order();
				useOrder.setTransactionId(transactionId);
				useOrder.setOutTradeNo(outTradeNo);
				useOrder.setUserId(userId);
				useOrder.setOrderId(0);
				useOrder.setTimeStart(timeStart);
				useOrder.setTimeExpire(timeExpire);
				useOrder.setTradeType(tradeType);
				useOrder.setFeeTotal(feeTotal);
				useOrder.setFeeLeft(feeLeft);
				useOrder.setBody("打赏");
				useOrder.setDetail(rewardUserId.toString());
				useOrder.setStatus(status);
				Integer hasInsert = cashService.initOrder(useOrder);
				if(hasInsert > 0) {
					CashAccount cashAccount = cashService.getCashAccountByUserId(userId);
					cashAccount.setTotalCash(cashAccount.getTotalCash() - feeTotal);
					cashAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(cashAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", cashAccount);
						
						timeStart = new Date();
						timeExpire = timeStart;
						transactionId = timeStart.getTime() + "";
						outTradeNo = transactionId;
						tradeType = 2;
						Order rewardUserOrder = new Order();
						rewardUserOrder.setTransactionId(transactionId);
						rewardUserOrder.setOutTradeNo(outTradeNo);
						rewardUserOrder.setUserId(rewardUserId);
						rewardUserOrder.setOrderId(useOrder.getId());
						rewardUserOrder.setTimeStart(timeStart);
						rewardUserOrder.setTimeExpire(timeExpire);
						rewardUserOrder.setTradeType(tradeType);
						rewardUserOrder.setFeeTotal(feeTotal);
						rewardUserOrder.setFeeLeft(feeLeft);
						rewardUserOrder.setBody("获得打赏");
						rewardUserOrder.setDetail(userId.toString());
						rewardUserOrder.setStatus(status);
						Integer hasInsertRewardUserOrder = cashService.initOrder(rewardUserOrder);
						if(hasInsertRewardUserOrder >0) {
							CashAccount rewardAccount = cashService.getCashAccountByUserId(rewardUserId);
							rewardAccount.setTotalCash(rewardAccount.getTotalCash() + feeTotal);
							rewardAccount.setTimeTill(timeStart);
							cashService.updateCashAccount(rewardAccount);
						}
						
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value="/updateMem",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateMem(HttpServletRequest request,
			HttpServletResponse response){	
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer monthAdd = Integer.parseInt(request.getParameter("monthAdd"));
		String password = request.getParameter("password");
		Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		Float feeLeft = feeTotal;
		User user = userService.selectUserById(userId);
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if(userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Date timeStart = new Date();
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.DAY_OF_MONTH, 1);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer status = 0;
				
				Order order = new Order();
				order.setTransactionId(transactionId);
				order.setOutTradeNo(outTradeNo);
				order.setUserId(userId);
				order.setOrderId(0);
				order.setTimeStart(timeStart);
				order.setTimeExpire(timeExpire);
				order.setTradeType(18);
				order.setFeeTotal(feeTotal);
				order.setFeeLeft(feeLeft);
				order.setBody("购买会员");
				order.setDetail(monthAdd.toString());
				order.setStatus(status);
				Integer hasInsert = cashService.initOrder(order);
				if(hasInsert > 0) {
					userAccount.setTotalCash(userAccount.getTotalCash() - feeTotal);
					userAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(userAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", order);
						Mem mem = new Mem();
						mem.setUserId(userId);
						Mem userMem = futureService.getMem(mem);
						userMem = futureService.getMem(mem);
						Date currentDate = new Date();
						Date consumeTime;
						Date expireTime = userMem.getExpireTime();
						if(expireTime.getTime() > currentDate.getTime()) {
							Calendar c1 = Calendar.getInstance();
						    c1.setTime(expireTime);
						    c1.add(Calendar.MONTH, monthAdd);
						    expireTime = c1.getTime();
						    userMem.setExpireTime(expireTime);
						    Integer hasUpdate1 = futureService.updateMem(userMem);
						}
						else {
							consumeTime = currentDate;
							Calendar c2 = Calendar.getInstance();
						    c2.setTime(consumeTime);
						    c2.add(Calendar.MONTH, monthAdd);
						    expireTime = c2.getTime();
						    userMem.setConsumeTime(consumeTime);
						    userMem.setExpireTime(expireTime);
						    Integer hasUpdate2 = futureService.updateMem(userMem);
						}
						
						
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/getbstatus", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getBstatus(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Map<String, Object> tmap = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer id = Integer.parseInt(request.getParameter("id"));
		Order order = new Order();
		order.setId(id);
		Order bonusOrder = cashService.getOrder(order);
		order.setOrderId(bonusOrder.getId());
		order.setUserId(userId);
		Order hasGetBonus = cashService.hasGetOrder(order);
		if(null != hasGetBonus) {
			tmap.put("self", hasGetBonus);
		}
		else {
			//tmap.put("self", "");
		}
		if(null !=bonusOrder) {
			order.setOrderId(bonusOrder.getId());
			if(bonusOrder.getStatus()==0 || bonusOrder.getTimeExpire().getTime() < (new Date()).getTime() ||
					Integer.parseInt(bonusOrder.getDetail())==0){
				
				List<Order> orderList = cashService.getListBonusOrderLimit(order);
				if(Integer.parseInt(bonusOrder.getDetail())==0) {
					Order maxOrder= cashService.getMaxOrder(id);
					for(int i=0; i < orderList.size(); i++) {
						if(orderList.get(i).getId().equals(maxOrder.getId())){
							orderList.get(i).setFeeMost(1);
						}
					}
				}
				tmap.put("user", bonusOrder);
				tmap.put("owner", orderList);
				map.put("status", 10);
				map.put("message", "红包已领完");
				map.put("data", tmap);
			}
			else if(bonusOrder.getTradeType().equals(4) && bonusOrder.getUserId().equals(userId)){
				List<Order> orderList = cashService.getListBonusOrderLimit(order);
				tmap.put("user", bonusOrder);
				tmap.put("owner", orderList);
				map.put("status", 12);
				map.put("message", "查看红包");
				map.put("data", tmap);
			}
			else {
				List<Order> orderList = cashService.getListBonusOrderLimit(order);
				tmap.put("user", bonusOrder);
				tmap.put("owner", orderList);
				if(null != hasGetBonus) {
					map.put("status", 11);
					map.put("message", "已领过该红包");
					map.put("data", tmap);
				}
				else {
					map.put("status", 13);
					map.put("message", "未领过该红包");
					map.put("data", tmap);
				}
			}
		}
		return map;
	}
	
	
	
	@RequestMapping(value = "/gettradestatus", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getTradestatus(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String outTradeNo = request.getParameter("outTradeNo");
		Order order = new Order();
		order.setOutTradeNo(outTradeNo);
		Order bonusOrder = cashService.getOrderByOutTradeNo(order);
		if(null !=bonusOrder) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", bonusOrder);
		}
		else {
			map.put("status", 4);
			map.put("message", "参数错误");
			map.put("data", "");
		}
		
		return map;
	}
	
	
	@RequestMapping(value = "/payforcontent", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payForContent(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		// 买家id
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		// 购买会员月数
		Integer month = Integer.parseInt(request.getParameter("month"));
		// 支付密码，可传可不传
		String password = request.getParameter("password");
		//Integer tradeType = Integer.parseInt(request.getParameter("tradeType"));
		// 描述购买会员的信息
		String body = request.getParameter("body");
		// 卖家id
		String detail = request.getParameter("payUserId");
		//Float feeTotal = Float.parseFloat(request.getParameter("feeTotal"));
		Float feeTotal = INIT_PAY * month;
		Float feeLeft = 0.f;
		User user = userService.selectUserById(userId);
		User payUser = userService.selectUserById(Integer.parseInt(detail));
		// 根据买家id获取买家账户信息
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if( null == request.getParameter("password") || userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Order order = new Order();
				order.setUserId(userId);
				order.setDetail(detail);
				order.setTradeType(PAY_CONTENT);
				Order hasPayOrder = cashService.hasPayContentOrder(order);
				
				Date timeStart = new Date();
				Date now = new Date();
				if(null != hasPayOrder) {
					Integer n2e = Time.compareTime(now, hasPayOrder.getTimeExpire());
					if(n2e < 0) {
						timeStart = hasPayOrder.getTimeExpire();
					}
				}
				
				if(month.equals(1)) {
					month += 0;
				}
				else if(month.equals(6)) {
					month += 1;
				}
				else if(month.equals(12)) {
					month += 2;
				}
			
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.MONTH, month);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer status = 0;
				
				
				order.setTransactionId(transactionId);
				order.setOutTradeNo(outTradeNo);
				
				order.setOrderId(0);
				order.setTimeStart(now);
				order.setTimeExpire(timeExpire);
				
				order.setFeeTotal(feeTotal);
				order.setFeeLeft(feeLeft);
				order.setBody(body);
				
				order.setStatus(status);
				Integer hasInsert = cashService.initOrder(order);
				
				{
					Order payOrder = new Order();
					payOrder.setTransactionId(transactionId + PAY_USER);
					payOrder.setOutTradeNo(outTradeNo + PAY_USER);
					payOrder.setUserId(payUser.getUserId());
					payOrder.setOrderId(order.getId());
					payOrder.setTimeStart(now);
					payOrder.setTimeExpire(timeExpire);
					payOrder.setTradeType(PAY_USER);
					payOrder.setFeeTotal(feeTotal/2);
					payOrder.setFeeLeft(feeLeft);
					payOrder.setBody("购买内容付费");
					payOrder.setDetail(userId+"");
					payOrder.setStatus(status);
					
					cashService.initOrder(payOrder);
					CashAccount payUserAccount = cashService.getCashAccountByUserId(payUser.getUserId());
					payUserAccount.setTotalCash(payUserAccount.getTotalCash() + (feeTotal/2));
					payUserAccount.setTimeTill(now);
					cashService.updateCashAccount(payUserAccount);
					
					String from = "lbh3zyi";
			        String targetTypeus = "users";
			        ObjectNode ext = factory.objectNode();
			        ArrayNode targetusers = factory.arrayNode();
			        targetusers.add(payUser.getEasemobId());
			        
			        ObjectNode txtmsg = factory.objectNode();
					txtmsg.put("msg","&" + user.getUserName() + "&购买了您内容付费服务");
			        txtmsg.put("type","txt");
			        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
				}
				{
					Order payOrder = new Order();
					payOrder.setTransactionId(transactionId + PAY_SYSTEM);
					payOrder.setOutTradeNo(outTradeNo + PAY_SYSTEM);
					payOrder.setUserId(0);
					payOrder.setOrderId(order.getId());
					payOrder.setTimeStart(now);
					payOrder.setTimeExpire(timeExpire);
					payOrder.setTradeType(PAY_SYSTEM);
					payOrder.setFeeTotal(feeTotal/2);
					payOrder.setFeeLeft(feeLeft);
					payOrder.setBody("购买内容付费");
					payOrder.setDetail(userId+"");
					payOrder.setStatus(status);
					
					cashService.initOrder(payOrder);
					CashAccount payUserAccount = cashService.getCashAccountByUserId(0);
					payUserAccount.setTotalCash(payUserAccount.getTotalCash() + (feeTotal/2));
					payUserAccount.setTimeTill(now);
					cashService.updateCashAccount(payUserAccount);
				}
				
				userAttentionService.attendOperation(userId, payUser.getUserName(), false);
				if(hasInsert > 0) {
					userAccount.setTotalCash(userAccount.getTotalCash() - feeTotal);
					userAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(userAccount);
					if(hasUpdate >0) {
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", order);
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/payforstrategy", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payForStrategy(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer month = Integer.parseInt(request.getParameter("month"));
		String password = request.getParameter("password");
		//String body = request.getParameter("body");
		String body="";
		Float feeTotal = INIT_PAY * month;
		Float feeLeft = 0.f;
		//User user = userService.selectUserById(userId);
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if( null == request.getParameter("password") || userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Order order = new Order();
				order.setUserId(userId);
				order.setDetail("购买策略选股");
				order.setTradeType(PAY_STRATEGY);
				Order hasPayOrder = cashService.hasStrategyOrder(order);
				
				Date timeStart = new Date();
				Date now = new Date();
				if(null != hasPayOrder) {
					Integer n2e = Time.compareTime(now, hasPayOrder.getTimeExpire());
					if(n2e < 0) {
						timeStart = hasPayOrder.getTimeExpire();
					}
				}
				
				if(month.equals(1)) {
					month += 0;
				}
				else if(month.equals(6)) {
					month += 1;
				}
				else if(month.equals(12)) {
					month += 2;
				}
			
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.MONTH, month);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer status = 0;
				
				
				order.setTransactionId(transactionId);
				order.setOutTradeNo(outTradeNo);
				
				order.setOrderId(0);
				order.setTimeStart(now);
				order.setTimeExpire(timeExpire);
				
				order.setFeeTotal(feeTotal);
				order.setFeeLeft(feeLeft);
				order.setBody(body);
				
				order.setStatus(status);
				Integer hasInsert = cashService.initOrder(order);
				if(hasInsert > 0) {
					userAccount.setTotalCash(userAccount.getTotalCash() - feeTotal);
					userAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(userAccount);
					if(hasUpdate >0) {
						//2016-8-15 10:15  更新内容：平台账号更新金额
						Order sysOrder = new Order();
						Date date = new Date();
						String transactionId2 = String.valueOf(date.getTime());
						sysOrder.setUserId(0);
						sysOrder.setBody("购买策略选股系统收费");
						sysOrder.setDetail("系统收费");
						sysOrder.setTimeStart(date);
						sysOrder.setTimeExpire(date);
						sysOrder.setTradeType(22);
						sysOrder.setOrderId(order.getId());
						sysOrder.setTransactionId(transactionId2);
						sysOrder.setOutTradeNo(transactionId2);
						sysOrder.setFeeTotal(feeTotal);
						sysOrder.setFeeLeft(feeLeft);
						sysOrder.setStatus(0);
						cashService.initOrder(sysOrder);
						
						CashAccount sysCashAccount = cashService.getCashAccountByUserId(0);
						Float newTotal = sysCashAccount.getTotalCash() + feeTotal;
						sysCashAccount.setTotalCash(newTotal);
						Integer hasUpdate2 = cashService.updateCashAccount(sysCashAccount);
						if(hasUpdate2 >0) {
							map.put("status", 0);
							map.put("message", "操作成功");
							map.put("data", order);
						}
						else {
							map.put("status", 4);
							map.put("message", "操作失败");
							map.put("data", "");
						}
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/tryforstrategy", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> tryForStrategy(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Order order = new Order();
		order.setUserId(userId);
		order.setDetail("试用策略选股");
		order.setTradeType(TRY_STRATEGY);
		Order hasPayOrder = cashService.hasStrategyOrder(order);
		Date timeStart = new Date();
		Date now = new Date();
		if(null != hasPayOrder) {
			map = rspFormatMsg("", MSG, "已试用");
			return map;
		}
		Calendar c = Calendar.getInstance();
	    c.setTime(timeStart);
	    c.add(Calendar.DAY_OF_YEAR, 14);
		Date timeExpire = c.getTime();
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Integer status = 0;
		
		
		order.setTransactionId(transactionId);
		order.setOutTradeNo(outTradeNo);
		
		order.setOrderId(0);
		order.setTimeStart(now);
		order.setTimeExpire(timeExpire);
		
		order.setFeeTotal(0);
		order.setFeeLeft(0);
		order.setBody("");
		
		order.setStatus(status);
		Integer hasInsert = cashService.initOrder(order);
		if(hasInsert > 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", order);
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/statusforstrategy", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> statusForStrategy(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Order order = new Order();
		order.setUserId(userId);
		order.setTradeType(PAY_STRATEGY);
		Order payOrder = cashService.hasStrategyOrder(order);
		order.setTradeType(TRY_STRATEGY);
		Order tryOrder = cashService.hasStrategyOrder(order);
		map.put("pay", payOrder);
		map.put("try", tryOrder);
		map = rspFormat(map, SUCCESS);
		return map;
	}	
	
	
	@RequestMapping(value = "/payforadvance", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payForAdvance(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer month = Integer.parseInt(request.getParameter("month"));
		String password = request.getParameter("password");
		String body="";
		Float feeTotal = INIT_PAY * month;
		Float feeLeft = 0.f;
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if( null == request.getParameter("password") || userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Order order = new Order();
				order.setUserId(userId);
				order.setDetail("购买量化");
				order.setTradeType(PAY_ADVANCE);
				Order hasPayOrder = cashService.hasStrategyOrder(order);
				
				Date timeStart = new Date();
				Date now = new Date();
				if(null != hasPayOrder) {
					Integer n2e = Time.compareTime(now, hasPayOrder.getTimeExpire());
					if(n2e < 0) {
						timeStart = hasPayOrder.getTimeExpire();
					}
				}
				
				if(month.equals(1)) {
					month += 0;
				}
				else if(month.equals(6)) {
					month += 1;
				}
				else if(month.equals(12)) {
					month += 2;
				}
			
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.MONTH, month);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer status = 0;
				
				
				order.setTransactionId(transactionId);
				order.setOutTradeNo(outTradeNo);
				
				order.setOrderId(0);
				order.setTimeStart(now);
				order.setTimeExpire(timeExpire);
				
				order.setFeeTotal(feeTotal);
				order.setFeeLeft(feeLeft);
				order.setBody(body);
				
				order.setStatus(status);
				Integer hasInsert = cashService.initOrder(order);
				if(hasInsert > 0) {
					userAccount.setTotalCash(userAccount.getTotalCash() - feeTotal);
					userAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(userAccount);
					if(hasUpdate >0) {
						
						//2016-8-15 10:15  更新内容：平台账号更新金额
						Order sysOrder = new Order();
						Date date = new Date();
						String transactionId2 = String.valueOf(date.getTime());
						sysOrder.setUserId(0);
						sysOrder.setBody("购买量化系统收费");
						sysOrder.setDetail("系统收费");
						sysOrder.setTimeStart(date);
						sysOrder.setTimeExpire(date);
						sysOrder.setTradeType(22);
						sysOrder.setOrderId(order.getId());
						sysOrder.setTransactionId(transactionId2);
						sysOrder.setOutTradeNo(transactionId2);
						sysOrder.setFeeTotal(feeTotal);
						sysOrder.setFeeLeft(feeLeft);
						sysOrder.setStatus(0);
						cashService.initOrder(sysOrder);
						
						CashAccount sysCashAccount = cashService.getCashAccountByUserId(0);
						Float newTotal = sysCashAccount.getTotalCash() + feeTotal;
						sysCashAccount.setTotalCash(newTotal);
						Integer hasUpdate2 = cashService.updateCashAccount(sysCashAccount);
						if(hasUpdate2 >0) {
							map.put("status", 0);
							map.put("message", "操作成功");
							map.put("data", order);
						}
						else {
							map.put("status", 4);
							map.put("message", "操作失败");
							map.put("data", "");
						}
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/statusforadvance", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> statusForAdvance(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Order order = new Order();
		order.setUserId(userId);
		order.setTradeType(PAY_ADVANCE);
		Order payOrder = cashService.hasStrategyOrder(order);
		order.setTradeType(TRY_ADVANCE);
		Order tryOrder = cashService.hasStrategyOrder(order);
		map.put("pay", payOrder);
		map.put("use", tryOrder);
		map = rspFormat(map, SUCCESS);
		return map;
	}
	
	
	@RequestMapping(value = "/tryforadvance", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> tryForAdvance(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Order order = new Order();
		order.setUserId(userId);
		order.setDetail("试用量化选股");
		order.setTradeType(TRY_ADVANCE);
		Order hasPayOrder = cashService.hasStrategyOrder(order);
		Date timeStart = new Date();
		Date now = new Date();
		if(null != hasPayOrder) {
			map = rspFormatMsg("", MSG, "已试用");
			return map;
		}
		Calendar c = Calendar.getInstance();
	    c.setTime(timeStart);
	    c.add(Calendar.DAY_OF_YEAR, 14);
		Date timeExpire = c.getTime();
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Integer status = 0;
		
		
		order.setTransactionId(transactionId);
		order.setOutTradeNo(outTradeNo);
		
		order.setOrderId(0);
		order.setTimeStart(now);
		order.setTimeExpire(timeExpire);
		
		order.setFeeTotal(0);
		order.setFeeLeft(0);
		order.setBody("");
		
		order.setStatus(status);
		Integer hasInsert = cashService.initOrder(order);
		if(hasInsert > 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", order);
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	
	
	@RequestMapping(value = "/payforconfiguration", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payForConfiguration(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer month = Integer.parseInt(request.getParameter("month"));
		String password = request.getParameter("password");
		String body="";
		Float feeTotal = INIT_PAY * month*2;
		Float feeLeft = 0.f;
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if( null == request.getParameter("password") || userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if(userAccount.getTotalCash() < feeTotal){
				map.put("status", 3);
				map.put("message", "账户余额不足");
				map.put("data", "");
			}
			else {
				Order order = new Order();
				order.setUserId(userId);
				order.setDetail("购买资产配置");
				order.setTradeType(PAY_CONFIGURATION);
				Order hasPayOrder = cashService.hasStrategyOrder(order);
				
				Date timeStart = new Date();
				Date now = new Date();
				if(null != hasPayOrder) {
					Integer n2e = Time.compareTime(now, hasPayOrder.getTimeExpire());
					if(n2e < 0) {
						timeStart = hasPayOrder.getTimeExpire();
					}
				}
				
				if(month.equals(1)) {
					month += 0;
				}
				else if(month.equals(6)) {
					month += 1;
				}
				else if(month.equals(12)) {
					month += 2;
				}
			
				Calendar c = Calendar.getInstance();
			    c.setTime(timeStart);
			    c.add(Calendar.MONTH, month);
				Date timeExpire = c.getTime();
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer status = 0;
				
				
				order.setTransactionId(transactionId);
				order.setOutTradeNo(outTradeNo);
				
				order.setOrderId(0);
				order.setTimeStart(now);
				order.setTimeExpire(timeExpire);
				
				order.setFeeTotal(feeTotal);
				order.setFeeLeft(feeLeft);
				order.setBody(body);
				
				order.setStatus(status);
				Integer hasInsert = cashService.initOrder(order);
				if(hasInsert > 0) {
					userAccount.setTotalCash(userAccount.getTotalCash() - feeTotal);
					userAccount.setTimeTill(timeStart);
					Integer hasUpdate = cashService.updateCashAccount(userAccount);
					if(hasUpdate >0) {
						
						//2016-8-15 10:15  更新内容：平台账号更新金额
						Order sysOrder = new Order();
						Date date = new Date();
						String transactionId2 = String.valueOf(date.getTime());
						sysOrder.setUserId(0);
						sysOrder.setBody("购买资产配置系统收费");
						sysOrder.setDetail("系统收费");
						sysOrder.setTimeStart(date);
						sysOrder.setTimeExpire(date);
						sysOrder.setTradeType(22);
						sysOrder.setOrderId(order.getId());
						sysOrder.setTransactionId(transactionId2);
						sysOrder.setOutTradeNo(transactionId2);
						sysOrder.setFeeTotal(feeTotal);
						sysOrder.setFeeLeft(feeLeft);
						sysOrder.setStatus(0);
						cashService.initOrder(sysOrder);
						
						CashAccount sysCashAccount = cashService.getCashAccountByUserId(0);
						Float newTotal = sysCashAccount.getTotalCash() + feeTotal;
						sysCashAccount.setTotalCash(newTotal);
						Integer hasUpdate2 = cashService.updateCashAccount(sysCashAccount);
						if(hasUpdate2 >0) {
							map.put("status", 0);
							map.put("message", "操作成功");
							map.put("data", order);
						}
						else {
							map.put("status", 4);
							map.put("message", "操作失败");
							map.put("data", "");
						}
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("message", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value = "/statusforconfiguration", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> statusForConfiguration(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Order order = new Order();
		order.setUserId(userId);
		order.setTradeType(PAY_CONFIGURATION);
		Order payOrder = cashService.hasStrategyOrder(order);
		order.setTradeType(TRY_CONFIGURATION);
		Order tryOrder = cashService.hasStrategyOrder(order);
		map.put("pay", payOrder);
		map.put("use", tryOrder);
		map = rspFormat(map, SUCCESS);
		return map;
	}
	
	
	@RequestMapping(value = "/tryforconfiguration", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> tryForConfiguation(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Order order = new Order();
		order.setUserId(userId);
		order.setDetail("试用资产配置");
		order.setTradeType(TRY_CONFIGURATION);
		Order hasPayOrder = cashService.hasStrategyOrder(order);
		Date timeStart = new Date();
		Date now = new Date();
		if(null != hasPayOrder) {
			map = rspFormatMsg("", MSG, "已试用");
			return map;
		}
		Calendar c = Calendar.getInstance();
	    c.setTime(timeStart);
	    c.add(Calendar.DAY_OF_YEAR, 14);
		Date timeExpire = c.getTime();
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Integer status = 0;
		
		
		order.setTransactionId(transactionId);
		order.setOutTradeNo(outTradeNo);
		
		order.setOrderId(0);
		order.setTimeStart(now);
		order.setTimeExpire(timeExpire);
		
		order.setFeeTotal(0);
		order.setFeeLeft(0);
		order.setBody("");
		
		order.setStatus(status);
		Integer hasInsert = cashService.initOrder(order);
		if(hasInsert > 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", order);
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	/**
	 * 根据文章作者id获取订阅者信息
	 * @param ：request
	 * @param ：response
	 * @return : map
	 */
	@RequestMapping(value = "/getusermessagebyid", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getUserMessage(HttpServletRequest request,
			HttpServletResponse response) {
		Integer detail = Integer.parseInt(request.getParameter("userId"));
		List<User> list = cashService.getUserMessageById(detail);
		Map<String, Object>map = new HashMap<String, Object>();
		map.put("list", list);
		return map;
	}
	
}
