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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.yfwl.yfgp.easemodrest.demo.EasemobMessages;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.AccountsMeta;
import com.yfwl.yfgp.model.Advance;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.RaiseInfo;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Strategy;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.HomeService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.Time;

@Controller
@RequestMapping("/portforlio")
public class PortforlioController extends BaseController{
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
	
	private static final double INIT_TOTAL = 1000000;		//设置初始金额
	
	@RequestMapping(value = "/getportforlio", method = { RequestMethod.GET})
	@ResponseBody
	public void getRaisePortforlio(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		  
	    List<Accounts> raiseList = groupService.getAllRaiseAccountsList();
		ArrayList<Accounts> list = new ArrayList<Accounts>();
		if(!raiseList.isEmpty()) {
			for(int i = 0; i < raiseList.size(); i++) {
				Accounts mainAccounts = raiseList.get(i);
				RaiseInfo raiseInfo = new RaiseInfo();
				raiseList.get(i).setRaiseInfo(raiseInfo);
				User user = userService.selectUserById(mainAccounts.getUserid());
				if(null != user) {
					raiseList.get(i).getRaiseInfo().setStimes(user.getStimes());
					raiseList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
					raiseList.get(i).getRaiseInfo().setStop(raiseList.get(i).getOptigid());
					raiseList.get(i).setUser(user);
					raiseList.get(i).setInit(INIT_TOTAL);
				}
				
				Order order = new Order();
				order.setOutTradeNo(mainAccounts.getGid().toString());
				Order mainOrder = cashService.getOrderByOutTradeNo(order);
				if(null != mainOrder) {
					raiseList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
					raiseList.get(i).getRaiseInfo().setBonus(mainOrder.getFeeLeft());
					raiseList.get(i).getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail()));
					raiseList.get(i).getRaiseInfo().setProfit(Float.parseFloat(mainOrder.getBody()));
					try {
						raiseList.get(i).getRaiseInfo().setFloor(Integer.parseInt(mainOrder.getExpand()));
					}catch (Exception e) {
						raiseList.get(i).getRaiseInfo().setFloor(0);
					}
					order.setOrderId(mainOrder.getId());
					//List<Order> orderList = cashService.getFollowListOrder(order);
					Integer orderListSize = cashService.getFollowListOrderSize(order);
					raiseList.get(i).getRaiseInfo().setJoinNum(orderListSize);
					raiseList.get(i).getRaiseInfo().setPay(0);		
				}
			}
			map = rspFormat(raiseList, SUCCESS);
			Gson gson = new Gson();
		    String retStr = callback + "(" + gson.toJson(map) + ")";  
		    response.getWriter().print(retStr);
		    return;
		}
	}
	
	
	@RequestMapping(value = "/updateportforlio", method = { RequestMethod.GET})
	@ResponseBody
	public void updatePortforlio(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Accounts  accounts = new Accounts();
		accounts.setGid(gid);
		Integer hasUpdate = groupService.updateSpreadAccounts(accounts);
		if(hasUpdate > 0) {
			Date createtime = new Date();
			AccountsMeta accountsMeta = new AccountsMeta();
			accountsMeta.setGid(gid);
			accountsMeta.setBonus(0);
			accountsMeta.setTask(0);
			accountsMeta.setType(0);
			accountsMeta.setCreatetime(createtime);
			accountsMeta.setStatus(0);
			Integer hasInit = groupService.initAccountsMeta(accountsMeta);
			if(hasInit > 0) {
				map = rspFormat("", SUCCESS);
			}
			else {
				map = rspFormat("", FAIL);
			}
		}
		else {
			map = rspFormat("", FAIL);
		}
		Gson gson = new Gson();
		String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	@RequestMapping(value = "/getpublishportforlio", method = { RequestMethod.GET})
	@ResponseBody
	public void getPublishPortforlio(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		  
	    List<Accounts> raiseList = groupService.getPublishAccountsList();
		ArrayList<Accounts> list = new ArrayList<Accounts>();
		if(!raiseList.isEmpty()) {
			for(int i = 0; i < raiseList.size(); i++) {
				Accounts mainAccounts = raiseList.get(i);
				RaiseInfo raiseInfo = new RaiseInfo();
				raiseList.get(i).setRaiseInfo(raiseInfo);
				User user = userService.selectUserById(mainAccounts.getUserid());
				raiseList.get(i).getRaiseInfo().setStimes(user.getStimes());
				raiseList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
				raiseList.get(i).getRaiseInfo().setStop(raiseList.get(i).getOptigid());
				raiseList.get(i).setUser(user);
				raiseList.get(i).setInit(INIT_TOTAL);
				
				Order order = new Order();
				order.setOutTradeNo(mainAccounts.getGid().toString());
				Order mainOrder = cashService.getOrderByOutTradeNo(order);
				if(null != mainOrder) {
					raiseList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
					raiseList.get(i).getRaiseInfo().setBonus(mainOrder.getFeeLeft());
					raiseList.get(i).getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail()));
					raiseList.get(i).getRaiseInfo().setProfit(Float.parseFloat(mainOrder.getBody()));
					try {
						raiseList.get(i).getRaiseInfo().setFloor(Integer.parseInt(mainOrder.getExpand()));
					}catch (Exception e) {
						raiseList.get(i).getRaiseInfo().setFloor(0);
					}
					order.setOrderId(mainOrder.getId());
					//List<Order> orderList = cashService.getFollowListOrder(order);
					Integer orderListSize = cashService.getFollowListOrderSize(order);
					raiseList.get(i).getRaiseInfo().setJoinNum(orderListSize);
					raiseList.get(i).getRaiseInfo().setPay(0);		
				}
				
				AccountsMeta accountsMeta = new AccountsMeta();
				accountsMeta.setGid(mainAccounts.getGid());
				AccountsMeta mainMeta = groupService.getAccountsMeta(accountsMeta);
				if(null != mainMeta) {
					raiseList.get(i).setAccountsMeta(mainMeta);
					if(null != mainOrder) {
						order.setOrderId(mainOrder.getId());
//						List<Order> realOrderList = cashService.getRealFollowListOrder(order);
//						List<Order> sysOrderList = cashService.getSysFollowListOrder(order);
						Integer realOrderListSize = cashService.getRealFollowListOrderSize(order);
						Integer sysOrderListSize = cashService.getSysFollowListOrderSize(order);
						if(realOrderListSize >= 0 ) {
							raiseList.get(i).getAccountsMeta().setRealnum(realOrderListSize);
						}
						if(sysOrderListSize >= 0) {
							raiseList.get(i).getAccountsMeta().setSysnum(sysOrderListSize);
						}
					}
				}
			}
			map = rspFormat(raiseList, SUCCESS);
			Gson gson = new Gson();
		    String retStr = callback + "(" + gson.toJson(map) + ")";  
		    response.getWriter().print(retStr);
		    return;
		}
	}
	
	@RequestMapping(value = "/updateaccountsmeta", method = { RequestMethod.GET})
	@ResponseBody
	public void updateAccountsMeta(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Integer type = Integer.parseInt(request.getParameter("type"));
		Integer task = 0;
		if(type.equals(0) && null!=request.getParameter("task")) {
			task = Integer.parseInt(request.getParameter("task"));
		}
		double bonus = Double.parseDouble(request.getParameter("bonus"));
		AccountsMeta am = new AccountsMeta();
		am.setGid(gid);
		AccountsMeta accountsMeta = groupService.getAccountsMeta(am);
		if(null != accountsMeta) {
			accountsMeta.setType(type);
			accountsMeta.setTask(task);
			accountsMeta.setBonus(bonus);
			Integer hasUpdate = groupService.updateAccountsMeta(accountsMeta);
			if(hasUpdate > 0) {
				map = rspFormat("", SUCCESS);
			}
			else {
				map = rspFormat("", FAIL);
			}
			
		}
		else {
			map = rspFormat("", FAIL);
		}
		
		Gson gson = new Gson();
	    String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/getrealuser", method = { RequestMethod.GET})
	@ResponseBody
	public void getRealUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String callback = request.getParameter("callback");
		Map<String, Object>map = new HashMap<String, Object>();
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Order on = new Order();
		on.setOutTradeNo(gid.toString());
		Order mainOrder = cashService.getOrderByOutTradeNo(on);
		mainOrder.setOrderId(mainOrder.getId());
		List<Order> orderList = cashService.getRealFollowListOrder(mainOrder);
		Order bonusOrder = cashService.getBonusOrder(mainOrder);
		List<User> userList = new ArrayList<User>();
		List<Order> bonusOrderList = new ArrayList<>();
		if(null != bonusOrder) {
			bonusOrder.setOrderId(bonusOrder.getId());
			bonusOrderList = cashService.getBonusListOrder(bonusOrder);
		}
		for(int i = 0; i < orderList.size(); i++) {
			Integer userId = orderList.get(i).getUserId();
			User user = userService.selectUserById(userId);
			user.setMember(0);
			user.setUserTitle("");;
			if(!bonusOrderList.isEmpty()) {
				for(int j =0; j < bonusOrderList.size(); j++) {
					if(bonusOrderList.get(j).getUserId().equals(userId)) {
						user.setUserTitle((double)bonusOrderList.get(j).getFeeTotal() + "");
					}
				}
			}
			userList.add(user);
		}
		map = rspFormat(userList, SUCCESS);
		Gson gson = new Gson();
	    String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/gethistoryportforlio", method = { RequestMethod.GET})
	@ResponseBody
	public void getHistoryPortforlio(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		  
	    List<Accounts> raiseList = groupService.getHistoryAccountsList();
		ArrayList<Accounts> list = new ArrayList<Accounts>();
		if(!raiseList.isEmpty()) {
			for(int i = 0; i < raiseList.size(); i++) {
				Accounts mainAccounts = raiseList.get(i);
				RaiseInfo raiseInfo = new RaiseInfo();
				raiseList.get(i).setRaiseInfo(raiseInfo);
				User user = userService.selectUserById(mainAccounts.getUserid());
				raiseList.get(i).getRaiseInfo().setStimes(user.getStimes());
				raiseList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
				raiseList.get(i).getRaiseInfo().setStop(raiseList.get(i).getOptigid());
				raiseList.get(i).setUser(user);
				raiseList.get(i).setInit(INIT_TOTAL);
				
				Order order = new Order();
				order.setOutTradeNo(mainAccounts.getGid().toString());
				Order mainOrder = cashService.getOrderByOutTradeNo(order);
				if(null != mainOrder) {
					raiseList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
					raiseList.get(i).getRaiseInfo().setBonus(mainOrder.getFeeLeft());
					raiseList.get(i).getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail()));
					raiseList.get(i).getRaiseInfo().setProfit(Float.parseFloat(mainOrder.getBody()));
					try {
						raiseList.get(i).getRaiseInfo().setFloor(Integer.parseInt(mainOrder.getExpand()));
					}catch (Exception e) {
						raiseList.get(i).getRaiseInfo().setFloor(0);
					}
					order.setOrderId(mainOrder.getId());
					//List<Order> orderList = cashService.getFollowListOrder(order);
					Integer orderListSize = cashService.getFollowListOrderSize(order);
					raiseList.get(i).getRaiseInfo().setJoinNum(orderListSize);
					raiseList.get(i).getRaiseInfo().setPay(0);		
				}
				
				AccountsMeta accountsMeta = new AccountsMeta();
				accountsMeta.setGid(mainAccounts.getGid());
				AccountsMeta mainMeta = groupService.getAccountsMeta(accountsMeta);
				if(null != mainMeta) {
					raiseList.get(i).setAccountsMeta(mainMeta);
					if(null != mainOrder) {
						order.setOrderId(mainOrder.getId());
//						List<Order> realOrderList = cashService.getRealFollowListOrder(order);
//						List<Order> sysOrderList = cashService.getSysFollowListOrder(order);
						Integer realOrderListSize = cashService.getRealFollowListOrderSize(order);
						Integer sysOrderListSize = cashService.getSysFollowListOrderSize(order);
						if(realOrderListSize >= 0 ) {
							raiseList.get(i).getAccountsMeta().setRealnum(realOrderListSize);
						}
						if(sysOrderListSize >= 0) {
							raiseList.get(i).getAccountsMeta().setSysnum(sysOrderListSize);
						}
					}
				}
			}
			map = rspFormat(raiseList, SUCCESS);
			Gson gson = new Gson();
		    String retStr = callback + "(" + gson.toJson(map) + ")";  
		    response.getWriter().print(retStr);
		    return;
		}
	}
	
	@RequestMapping(value = "/getpreportforlio", method = { RequestMethod.GET})
	@ResponseBody
	public void getPrePortforlio(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		  
	    List<Accounts> raiseList = groupService.getAllPreList();
	    map = rspFormat(raiseList, SUCCESS);
		Gson gson = new Gson();
	    String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/updatepreportforlio", method = { RequestMethod.GET})
	@ResponseBody
	public void updatePrePortforlio(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Date now = new Date();
		Accounts  accounts = new Accounts();
		accounts.setGid(gid);
		Accounts mainAccounts = groupService.getAccounts(accounts);
		mainAccounts.setDeltime(now);
		Integer hasUpdate = groupService.updateAccounts(mainAccounts);
		if(hasUpdate > 0) {
			map = rspFormat("", SUCCESS);
		}
		else {
			map = rspFormat("", FAIL);
		}
		Gson gson = new Gson();
	    String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/updatestrategy", method = { RequestMethod.GET})
	@ResponseBody
	public void updateStrategy(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		try {
			int type= Integer.parseInt(request.getParameter("type")); 
			String nameString = request.getParameter("nameString");
			String idString = request.getParameter("idString");
			String priceString = request.getParameter("priceString");
			System.out.println(nameString + idString + priceString);
			String[] nameArr = nameString.split(",");
			String[] idArr = idString.split(",");
			String[] priceArr = priceString.split(",");
			Integer hasDelete = groupService.deleteAllStrategy(type);
			for(int i = 0; i < nameArr.length; i++){
				Strategy strategy = new Strategy();
				strategy.setStockName(nameArr[i]);
				strategy.setStockId(idArr[i]);
				strategy.setPrice(Double.parseDouble(priceArr[i]));
				strategy.setType(type);
				Integer hasInsert = groupService.insertStrategy(strategy);
			}
			map = rspFormat("", SUCCESS);
		} catch(Exception e) {
			map = rspFormat("", FAIL);
		}
		Gson gson = new Gson();
		String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/getstrategy", method = { RequestMethod.GET})
	@ResponseBody
	public void getStrategy(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		int type= Integer.parseInt(request.getParameter("type")); 
	    List<Strategy> raiseList = groupService.getAllStrategy(type);
	    map = rspFormat(raiseList, SUCCESS);
		Gson gson = new Gson();
	    String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/querystrategy", method = { RequestMethod.POST})
	
	@ResponseBody
	public  Map<String, Object> postStrategy(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		int type= Integer.parseInt(request.getParameter("type"));
	    List<Strategy> strategyList = groupService.getAllStrategy(type);
	    if(strategyList.isEmpty()) {
	    	map = rspFormat("", SUCCESS);
		    return map;
	    }
	    map = rspFormat(strategyList, SUCCESS);
	    return map;
	}
	
	
//	@RequestMapping(value = "/usestrategy", method = { RequestMethod.GET})
//	@ResponseBody
//	public  Map<String, Object> useStrategy(HttpServletRequest request,
//			HttpServletResponse response) throws IOException {
//		Map<String, Object>map = new HashMap<String, Object>();
//		int type= Integer.parseInt(request.getParameter("type"));
//	    List<Strategy> strategyList = groupService.getAllStrategy(type);
//	    if(strategyList.isEmpty()) {
//	    	map = rspFormat("", SUCCESS);
//		    return map;
//	    }
//	    map = rspFormat(strategyList, SUCCESS);
//	    return map;
//	}
	

	@RequestMapping(value = "/queryQuantify", method = { RequestMethod.GET})
	@ResponseBody
	public  void queryQuantify(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String callback = request.getParameter("callback");
		Map<String, Object>map = new HashMap<String, Object>();
	    List<Accounts> accountsList = groupService.getAllAdvanceAccounts();
	    if(accountsList.isEmpty()) {
	    	map = rspFormat("", SUCCESS);
	    }
	    map = rspFormat(accountsList, SUCCESS);
	    Gson gson = new Gson();
	    String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/queryAdvance", method = { RequestMethod.GET})
	@ResponseBody
	public  void queryAdvance(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String callback = request.getParameter("callback");
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Map<String, Object>map = new HashMap<String, Object>();
		List<Advance> advanceList = groupService.getAccountsAdvance(gid);
		if(!advanceList.isEmpty()) {
			map = rspFormat(advanceList, SUCCESS);
		}
		else {
			map = rspFormat("", SUCCESS);
		}
	    Gson gson = new Gson();
	    String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
	
	
	@RequestMapping(value = "/updateAdvance", method = { RequestMethod.GET})
	@ResponseBody
	public void updateAdvance(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		Map<String, Object>map = new HashMap<String, Object>();
		String callback = request.getParameter("callback");
		try {
			int gid= Integer.parseInt(request.getParameter("gid")); 
			String nameString = request.getParameter("nameString");
			String idString = request.getParameter("idString");
			System.out.println(nameString);
			System.out.println(idString);
			
			String[] nameArr = nameString.split(",");
			String[] idArr = idString.split(",");
			
			Accounts accounts = new Accounts();
			accounts.setGid(gid);
			Accounts mainAccounts = groupService.getAccounts(accounts);
			
			Integer hasDelete = groupService.deleteAllSystemAdvance(gid);
			for(int i = 0; i < idArr.length; i++){
				Stockinfo stockinfo = new Stockinfo();
				stockinfo.setStockid(idArr[i]);
				Stockinfo info = groupService.getStockinfo(stockinfo);
				
				Advance advance = new Advance();
				advance.setGid(gid);
				advance.setUserid(mainAccounts.getUserid());
				advance.setStockId(idArr[i]);
				if(null != info) {
					advance.setStockName(info.getName());
				}
				advance.setType(1);
				advance.setStatus(0);
				groupService.insertAdvance(advance);
			}
			map = rspFormat("", SUCCESS);
		} catch(Exception e) {
			map = rspFormat("", FAIL);
		}
		Gson gson = new Gson();
		String retStr = callback + "(" + gson.toJson(map) + ")";  
	    response.getWriter().print(retStr);
	    return;
	}
}
