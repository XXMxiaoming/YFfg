package com.yfwl.yfgp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
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
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.Advance;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Home;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Pool;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.RaiseInfo;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Score;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.Traderec;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserMeta;
import com.yfwl.yfgp.model.WebAccount;
import com.yfwl.yfgp.service.AccountsService2;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.HomeService;
import com.yfwl.yfgp.service.StockInfoService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.Time;

@Controller
@RequestMapping("/zuhe")
public class GroupController extends BaseController {
	// private static final JsonNodeFactory factory = new
	// JsonNodeFactory(false);
	private static final double INIT_TOTAL = 1000000.f; // 设置初始金额
	private static final int PAGE_SIZE = 20;
	private static final int RAISE_SIZE = 8;
	private static final int POOL_SIZE = 10;
	private Object lock = new Object();

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
	AccountsService2 accountsService2;
	@Autowired
	StockInfoService stockInfoService;

	@RequestMapping(value = "/account", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> account(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token = request.getParameter("token");
		String useridString = request.getParameter("userid");
		Accounts accounts = new Accounts();
		accounts.setGid(gid);
		Accounts mainAccounts = groupService.getAccounts(accounts);
		if (null != useridString) {
			Integer userid = Integer.parseInt(useridString);
			Relation relation = new Relation();
			relation.setGid(gid);
			relation.setUid(userid);
			relation.setSubgid(0);
			relation.setDel(0);
			relation.setAttr(1);
			Relation mainRelation = groupService.getRelation(relation);
			if (null != mainRelation) {
				mainAccounts.setFollow(1);
			} else {
				mainAccounts.setFollow(0);
			}
		} else {
			mainAccounts.setFollow(0);
		}
		User user = userService.selectUserById(mainAccounts.getUserid());
		if (null != user) {
			RaiseInfo raiseInfo = new RaiseInfo();
			mainAccounts.setRaiseInfo(raiseInfo);
			mainAccounts.setUser(user);
			// mainAccounts.setInit(mainAccounts.getInit()); //设置初始金额
			mainAccounts.getRaiseInfo().setStimes(user.getStimes());
			mainAccounts.getRaiseInfo().setLtimes(user.getLtimes());
			mainAccounts.getRaiseInfo().setStop(mainAccounts.getOptigid());
			// mainAccounts.setOptigid(mainAccounts.getOptigid());
			if (mainAccounts.getAttr().equals(7)) {
				Order order = new Order();
				order.setOutTradeNo(mainAccounts.getGid().toString());
				Order mainOrder = cashService.getOrderByOutTradeNo(order);

				mainAccounts.getRaiseInfo().setFee(mainOrder.getFeeTotal());
				mainAccounts.getRaiseInfo().setFeeOrigin(
						mainOrder.getFeeOrigin());
				mainAccounts.getRaiseInfo().setBonus(mainOrder.getFeeLeft());
				mainAccounts.getRaiseInfo().setNum(
						Integer.parseInt(mainOrder.getDetail()));
				mainAccounts.getRaiseInfo().setProfit(
						Float.parseFloat(mainOrder.getBody()));
				try {
					mainAccounts.getRaiseInfo().setFloor(
							Integer.parseInt(mainOrder.getExpand()));
				} catch (Exception e) {
					mainAccounts.getRaiseInfo().setFloor(0);
				}
				// System.out.println(mainAccounts.getRaiseInfo().getFloor());
				order.setOrderId(mainOrder.getId());
				// List<Order> orderList =
				// cashService.getFollowListOrder(order);
				Integer orderListSize = cashService
						.getFollowListOrderSize(order);
				mainAccounts.getRaiseInfo().setJoinNum(orderListSize);
				mainAccounts.getRaiseInfo().setPay(0);

				if (null != useridString) {
					Integer userid = Integer.parseInt(useridString);
					order.setUserId(userid);
					Order userOrder = cashService.hasGetOrder(order);
					if (null != userOrder) {
						mainAccounts.getRaiseInfo().setPay(1);
					}
				}
				// mainAccounts.setRank(0);
			} else {
				// Integer rank = groupService.getAccountsRank(mainAccounts);
				// mainAccounts.setRank(rank + 1);
			}
		}

		Posi posi = new Posi();
		posi.setGid(gid);
		List<Posi> posiList = groupService.getPosi(posi);
		if (!posiList.isEmpty()) {
			String stockStr = "";
			for (int i = 0; i < posiList.size(); i++) {
				String stockString = posiList.get(i).getStockid();
				String stock = stockString.startsWith("6") ? stockString
						+ ".SS" : stockString + ".SZ";
				if (i == posiList.size() - 1) {
					stockStr += stock;
				} else {
					stockStr += stock + ",";
				}
			}
		//	String result = GetHSTokenUtils.getReal(stockStr);
		String result = GetHSTokenUtils.getReal(stockStr, token);
			JSONObject jsonObject = new JSONObject(result);
			try {
				JSONObject snapshot = jsonObject.getJSONObject("data")
						.getJSONObject("snapshot");
				double realPrice;
				double totalStock = 0;
				for (int i = 0; i < posiList.size(); i++) {
					String stockString = posiList.get(i).getStockid();
					String stock = stockString.startsWith("6") ? stockString
							+ ".SS" : stockString + ".SZ";
					double last_px = snapshot.getJSONArray(stock).getDouble(2);
					double preclose_px = snapshot.getJSONArray(stock)
							.getDouble(3);
					if (last_px < 0.01) {
						realPrice = preclose_px;
					} else {
						realPrice = last_px;
					}
					totalStock += realPrice * posiList.get(i).getVol() * 100;
				}
				double total = mainAccounts.getAvailable()
						+ mainAccounts.getFrozen() + totalStock;
				mainAccounts.setStock(totalStock);
				mainAccounts.setTotal(total);
				Accounts latestMainAccounts = groupService
						.getAccounts(accounts);
				mainAccounts.setFrozen(latestMainAccounts.getFrozen());
				
				
				map.put("userid", mainAccounts.getUserid());
				map.put("gid", mainAccounts.getGid());
				map.put("total", mainAccounts.getTotal());
				map.put("stock", mainAccounts.getStock());
				map.put("available", mainAccounts.getAvailable());
				map.put("attr", mainAccounts.getAttr());
				List<Map<String,Object>> arr=new ArrayList<Map<String,Object>>();
				arr.add(map);
				map = rspFormat(arr, SUCCESS);
				if (!mainAccounts.getAttr().equals(7)) {
					// groupService.updateAccounts(mainAccounts); //获取帐户信息的时候不更新
				}
				/*
				 * Integer updateAccounts =
				 * groupService.updateAccounts(mainAccounts); if(updateAccounts
				 * > 0) { map = rspFormat(mainAccounts, SUCCESS); } else { map =
				 * rspFormat("", WRONG_MYSQL_OPERATION); }
				 */
			} catch (Exception e) {
				map = rspFormat("", WRONG_TOKEN);
			}

		} else {
			if (mainAccounts.getAttr().equals(7)
					&& (!mainAccounts.getDel().equals(0))) {
				
	
				map.put("userid", mainAccounts.getUserid());
				map.put("gid", mainAccounts.getGid());
				map.put("total", mainAccounts.getTotal());
				map.put("stock", mainAccounts.getStock());
				map.put("available", mainAccounts.getAvailable());
				map.put("attr", mainAccounts.getAttr());
				List<Map<String,Object>> arr=new ArrayList<Map<String,Object>>();
				arr.add(map);
				map = rspFormat(arr, SUCCESS);
			} else {
				double total = mainAccounts.getAvailable()
						+ mainAccounts.getFrozen();
				mainAccounts.setStock(0);
				mainAccounts.setTotal(total);
				mainAccounts.setFrozen(mainAccounts.getFrozen());
				
				
				map.put("userid", mainAccounts.getUserid());
				map.put("gid", mainAccounts.getGid());
				map.put("total", mainAccounts.getTotal());
				map.put("stock", mainAccounts.getStock());
				map.put("available", mainAccounts.getAvailable());
				map.put("attr", mainAccounts.getAttr());
				List<Map<String,Object>> arr=new ArrayList<Map<String,Object>>();
				arr.add(map);
				map = rspFormat(arr, SUCCESS);
				if (!mainAccounts.getAttr().equals(7)) {

					// 获取帐户信息时候不更新
					// groupService.updateAccounts(mainAccounts);

				}
			}
		}
		return map;
	}

	@RequestMapping(value = "/createOpti", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> createOpti(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token = request.getParameter("token");
		// System.out.println(token);
		if (validateToken(userId, token)) {
			Accounts accounts = new Accounts();
			accounts.setGid(gid);
			accounts.setUserid(userId);
			List<Accounts> optiList = groupService
					.getOptiAccountsList(accounts);
			if (!optiList.isEmpty()) {
				map = rspFormat("", HAVE_PORTFOLIO);//已创建优化组合
			} else {
				Accounts mainAccounts = groupService.getAccounts(accounts);//这里需要gid
				Accounts optiAccounts = new Accounts();
				Date createtime = new Date();
				optiAccounts.setGname(mainAccounts.getGname() + "的优化组合");
				optiAccounts.setAvailable(INIT_TOTAL);
				optiAccounts.setStock(0);
				optiAccounts.setTotal(INIT_TOTAL);
				optiAccounts.setInit(INIT_TOTAL);
				optiAccounts.setUserid(userId);
				optiAccounts.setAttr(5);
				optiAccounts.setOptigid(mainAccounts.getGid());
				optiAccounts.setCreatetime(createtime);
				Integer hasInsert = groupService.initAccounts(optiAccounts);
				Integer optiId = optiAccounts.getGid();
				if (hasInsert > 0 && optiId != gid) {
					mainAccounts.setAttr(6);
					mainAccounts.setOptigid(optiId);
					groupService.updateAccounts(mainAccounts);
					// Posi posi = new Posi();
					// posi.setGid(gid);
					// List<Posi> posiList = groupService.getPosi(posi);
					// if(!posiList.isEmpty()) {
					// for(int i = 0; i < posiList.size(); i++) {
					// Posi posiItem = posiList.get(i);
					// posiItem.setGid(optiId);
					// posiItem.setAvailable(posiItem.getAvailable() +
					// posiItem.getFrozen());
					// posiItem.setFrozen(0);
					// groupService.initPosi(posiItem);
					// }
					// }
					// Traderec traderec = new Traderec();
					// traderec.setGid(gid);
					// List<Traderec> traderecList =
					// groupService.getTraderecList(traderec);
					// if(!traderecList.isEmpty()) {
					// for(Traderec t:traderecList) {
					// t.setGid(optiId);
					// groupService.initTraderec(t);
					// }
					// }
					// //拷贝历史收益曲线
					// Score score = new Score();
					// score.setGid(gid);
					// List<Score> scoreList = groupService.getScoreList(score);
					// if(!scoreList.isEmpty()){
					// for(int i = 0; i < scoreList.size(); i++) {
					// Score item = scoreList.get(i);
					// item.setGid(optiId);
					// groupService.initScore(item);
					// }
					// }

					Stat stat = new Stat();
					stat.setGid(optiId);
					stat.setR7(0);
					stat.setR30(0);
					stat.setR6m(0);
					stat.setR1y(0);
					groupService.initStat(stat);
					map = rspFormat(optiAccounts, SUCCESS);
				} else {
					map = rspFormat("", WRONG_MYSQL_OPERATION);
				}

			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}

		return map;
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> delete(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token = request.getParameter("token");
		if (validateToken(userId, token)) {
			Accounts accounts = new Accounts();
			accounts.setGid(gid);
			Accounts mainAccounts = groupService.getAccounts(accounts);
			if (null != mainAccounts) {
				
				Integer attr = mainAccounts.getAttr();
				if(attr == 15){
					Accounts paramAcc = new Accounts();
					paramAcc.setOptigid(gid);
					paramAcc.setUserid(userId);
					Accounts acc14 =  groupService.getAccByOptiGid(paramAcc);
					Date deltime = new Date();
					acc14.setDel(1);
					acc14.setDeltime(deltime);
					groupService.updateAccounts(acc14);
					OrderBook orderbook = new OrderBook();
					orderbook.setGid(acc14.getGid());
					groupService.deleteAccountsOrder(orderbook);
					Posi posi = new Posi();
					posi.setGid(acc14.getGid());
					groupService.deletePosi(posi);
					Relation relation = new Relation();
					relation.setGid(acc14.getGid());
					groupService.deleteRelation(relation);
					Stat stat = new Stat();
					stat.setGid(acc14.getGid());
					groupService.deleteStat(stat);
					
					
					mainAccounts.setDel(1);
					mainAccounts.setDeltime(deltime);
					groupService.updateAccounts(mainAccounts);
					
					map = rspFormat("", SUCCESS);
				}else{
					Date deltime = new Date();
					mainAccounts.setDel(1);
					mainAccounts.setDeltime(deltime);
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
					if (mainAccounts.getAttr() == 5 || mainAccounts.getAttr() == 6) {
						accounts.setGid(mainAccounts.getOptigid());
						Accounts optiAccounts = groupService.getAccounts(accounts);
						optiAccounts.setAttr(1);
						optiAccounts.setOptigid(0);
						if (mainAccounts.getAttr() == 6) {
							optiAccounts.setDel(1);
						}
						groupService.updateAccounts(optiAccounts);
					}
					
					map = rspFormat("", SUCCESS);
				}

			} else {
				map = rspFormat("", SUCCESS);
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/updateinfo", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateInfo(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token = request.getParameter("token");
		if (validateToken(userId, token)) {
			String info = "";
			if (null != request.getParameter("info")) {
				info = request.getParameter("info");
			}
			Accounts accounts = new Accounts();
			accounts.setGid(gid);
			Accounts mainAccounts = groupService.getAccounts(accounts);
			mainAccounts.setInfo(info);
			System.out.println(mainAccounts.getOptigid());
			Integer hasUpdate = groupService.updateAccounts(mainAccounts);
			if (hasUpdate > 0) {
				map = rspFormat(mainAccounts, SUCCESS);
			} else {
				map = rspFormat("", WRONG_MYSQL_OPERATION);
			}

		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	
	
	@RequestMapping(value = "/queryzuhe", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryZuhe(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = null;
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		if(validateToken(userid, token)){
			Accounts accounts = new Accounts();
			accounts.setUserid(userid);
			List<Accounts> accountsList = groupService.getAccountsList(accounts);
			List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
			if (!accountsList.isEmpty()) {
				for (Accounts accounts2 : accountsList) {
					map=new HashMap<String, Object>();
					map.put("userid", accounts2.getUserid());
					map.put("gid", accounts2.getGid());
					map.put("total", accounts2.getTotal());
					map.put("stock", accounts2.getStock());
					map.put("available", accounts2.getAvailable());
					map.put("frozen", accounts2.getFrozen());
					map.put("attr", accounts2.getAttr());
					
					arr.add(map);
				}
				map = rspFormat(arr, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
		}else{
			map=rspFormat("", WRONG_TOKEN);
		}
		

		return map;
	}

	
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> create(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		if (validateToken(userid, token)) {
			User user=userService.selectUserById(userid);
			String gname=user.getUserName();
					Accounts accounts = new Accounts();
					accounts.setUserid(userid);
					List<Accounts> listAccounts = groupService
							.getAccountsList(accounts);
					accounts.setGname(gname+"的组合");
					if (!listAccounts.isEmpty()) {
						map = rspFormat("", HAVA_SAME_PROTFOLIO);
					} else {
							Date createtime = new Date();
							accounts.setCreatetime(createtime);
							accounts.setAttr(6);
							accounts.setOptigid(0);
							double giveTotal = INIT_TOTAL;
							accounts.setAvailable(giveTotal);
							accounts.setInit(giveTotal);
							accounts.setTotal(giveTotal);
							accounts.setGtnum(0);
							accounts.setGznum(0);
							accounts.setDel(0);
							Integer hasInsert = groupService.initAccounts(accounts);
							if (hasInsert > 0) {
								Integer gid = accounts.getGid();
								Stat stat = new Stat();
								stat.setGid(gid);
								stat.setR7(0);
								stat.setR30(0);
								stat.setR6m(0);
								stat.setR1y(0);
								groupService.initStat(stat);

								
								//调用优化组合方法(createOpti)
								Accounts accounts2 = new Accounts();
								accounts2.setGid(gid);
								accounts2.setUserid(userid);
								List<Accounts> optiList = groupService
										.getOptiAccountsList(accounts2);
//								if (!optiList.isEmpty()) {
//								map = rspFormat("", HAVE_PORTFOLIO);
//								} else {
									Accounts mainAccounts = groupService.getAccounts(accounts2);
									Accounts optiAccounts = new Accounts();
									Date createtime2 = new Date();
									optiAccounts.setGname(gname + "的优化组合");
									optiAccounts.setAvailable(INIT_TOTAL);
									optiAccounts.setStock(0);
									optiAccounts.setTotal(INIT_TOTAL);
									optiAccounts.setInit(INIT_TOTAL);
									optiAccounts.setUserid(userid);
									optiAccounts.setAttr(5);
									optiAccounts.setOptigid(mainAccounts.getGid());
									optiAccounts.setCreatetime(createtime2);
									Integer hasInsert2 = groupService.initAccounts(optiAccounts);
									Integer optiId = optiAccounts.getGid();
									if (hasInsert2 > 0 && optiId != gid) {
										mainAccounts.setOptigid(optiId);
										groupService.updateAccounts(mainAccounts);
										Stat stat2 = new Stat();
										stat2.setGid(optiId);
										stat2.setR7(0);
										stat2.setR30(0);
										stat2.setR6m(0);
										stat2.setR1y(0);
										groupService.initStat(stat2);
										//map = rspFormat("", SUCCESS);
										Object[] a=new Object[2];
										a[0]=mainAccounts;
										a[1]=optiAccounts;
										map=rspFormat(a, SUCCESS);
									} else {
										map = rspFormat("", WRONG_MYSQL_OPERATION);
									}
							}
							else {
								map = rspFormat("", WRONG_MYSQL_OPERATION);
							}
				} 
		}else {
					map = rspFormat("", WRONG_TOKEN);
				}

		return map;
	}

	@RequestMapping(value = "/insertorder", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertOrder(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		synchronized (lock) {
			Integer userid = Integer.parseInt(request.getParameter("userid"));
			Integer gid = Integer.parseInt(request.getParameter("gid"));
			String stockid = request.getParameter("stock");
			double price = Double.parseDouble(request.getParameter("price"));
			Integer vol = Integer.parseInt(request.getParameter("vol"));
			Integer action = Integer.parseInt(request.getParameter("act"));
			String token = request.getParameter("token");
			String stockName = "";
			String accountsName = "";
			String buyOrSall = "";
			String stock = "";
			String atype = "";
		//	System.out.println("gjfdoksgl");
			if (validateToken(userid, token)) {
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int week = c.get(Calendar.DAY_OF_WEEK);
				SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
				Date datenow = new Date();
				String dateString = dateSdf.format(datenow);
				// allen
				if (week == 1 || week == 7
						|| AccountUtil.HOLIDAY_STRING.contains(dateString)
						|| hour < 8 || hour >= 15) {
					map = rspFormat("", NOT_TRADE_TIME);
					return map;
				} else {
					Stockinfo stockinfo = new Stockinfo();
					stockinfo.setStockid(stockid);
					Stockinfo info = groupService.getStockinfo(stockinfo);
					if (null == info) {
						map = rspFormat("", NOT_SUPPORT_STOCK);
						return map;
					}
					Integer stockInteger = Integer.parseInt(stockid);
					if (!((stockInteger >= 600000 && stockInteger <= 699999)
							|| (stockInteger >= 0 && stockInteger <= 9999) || (stockInteger >= 300000 && stockInteger <= 399999))) {
						map = rspFormat("", NOT_SUPPORT_STOCK);
						return map;
					} else {
						stock = stockid.startsWith("6") ? stockid + ".SS"
								: stockid + ".SZ";
						//System.out.println(stock+"gfkgjfdkgsfksjs");
						String result = GetHSTokenUtils.getReal(stock, token);
						//System.out.println(result+"fgkfdhsgjkfljs");
						JSONObject jsonObject = new JSONObject(result);
						try {
							JSONObject snapshot = jsonObject.getJSONObject(
									"data").getJSONObject("snapshot");
							double last_px = snapshot.getJSONArray(stock)
									.getDouble(2);
							double up_px = snapshot.getJSONArray(stock)
									.getDouble(4);
							double down_px = snapshot.getJSONArray(stock)
									.getDouble(5);
							// String trade_status =
							// snapshot.getJSONArray(stock).getString(9);
							// if(last_px < 0.01) {
							// map = rspFormat("", NOT_ALLOWED);
							// return map;
							// }
							// if(trade_status.equals("STOPT"))
							// {
							// map = rspFormatMsg("",MSG, "股票停牌");
							// return map;
							// }
							stockName = snapshot.getJSONArray(stock).getString(
									6);
							double price_px;
							Date inserttime = new Date();
							OrderBook orderbook = new OrderBook();
							orderbook.setGid(gid);
							orderbook.setAction(action);
							orderbook.setStockid(stockid);
							orderbook.setVol(vol);
							orderbook.setInserttime(inserttime);

							Traderec traderec = new Traderec();
							traderec.setGid(gid);
							traderec.setStockid(stockid);
							traderec.setVol(vol);
							traderec.setAction(action);
							traderec.setStatus(1);
							traderec.setInserttime(inserttime);

							Accounts accounts = new Accounts();
							accounts.setGid(gid);
							Accounts mainAccounts = groupService
									.getAccounts(accounts);
							accountsName = mainAccounts.getGname();
							// if(mainAccounts.getAttr().equals(1) ||
							// mainAccounts.getAttr().equals(5) ||
							// mainAccounts.getAttr().equals(6)) {
							// atype = ",1";
							// }

							// attr=7表示收费组合 atype=""
							if (!mainAccounts.getAttr().equals(7)) {
								atype = ",1";// 普通组合标志
							}
							if (mainAccounts.getAttr().equals(12)) {
								atype = ",2";
							}
							if (action == 1) {
								if (mainAccounts.getAttr().equals(7)) {
									Integer d2e = Time.daysBetween(inserttime,
											mainAccounts.getEndtime());
									if (d2e.equals(0)) {
										map = rspFormatMsg("", MSG, "组合已结束");
										return map;
									}
									if (!mainAccounts.getDel().equals(0)) {
										map = rspFormatMsg("", MSG, "组合已结束");
										return map;
									}
								}
								buyOrSall = "下单买入";
								price_px = Math.min(up_px, price);

								double money = vol * 100 * 1.0003 * price_px;
								if (mainAccounts.getAvailable() >= money) {
									mainAccounts.setAvailable(mainAccounts
											.getAvailable() - money);
									mainAccounts.setFrozen(mainAccounts
											.getFrozen() + money);
									groupService.updateAccounts(mainAccounts);

									orderbook.setPrice(price_px);
									groupService.initOrderBook(orderbook);

									traderec.setOrderprice(price_px);
									groupService.initTraderec(traderec);
									map = rspFormat(orderbook, SUCCESS);
								} else {
									map = rspFormat("", NO_ENOUGH_MONEY);
									return map;
								}
							} else if (action == 3) {
								buyOrSall = "下单卖出";
								price_px = Math.max(down_px, price);
								Posi posi = new Posi();
								posi.setGid(gid);
								posi.setStockid(stockid);
								Posi mainPosi = groupService.getStockPosi(posi);
								
								if(mainPosi==null){
									map.put("msg", "您的持仓中没有该股票不能卖出");
									map.put("status", "1");
									map.put("data", "");
								}else{
									if (mainPosi.getAvailable() >= vol) {
										mainPosi.setAvailable(mainPosi
												.getAvailable() - vol);
										mainPosi.setFrozen(mainPosi.getFrozen()
												+ vol);
										groupService.updatePosi(mainPosi);

										orderbook.setPrice(price_px);
										groupService.initOrderBook(orderbook);

										traderec.setOrderprice(price_px);
										groupService.initTraderec(traderec);
										map = rspFormat(orderbook, SUCCESS);
									} else {
										map = rspFormat("", NOT_ENOUGH_STOCK);
										return map;
									}
									
								}
								
								
							}

						} catch (JSONException e) {
							map = rspFormat("", WRONG_TOKEN);
							return map;
						}
					}
				}

			} else {
				map = rspFormat("", WRONG_TOKEN);
				return map;
			}
			// Relation relation = new Relation();
			// relation.setGid(gid);
			// List<Relation> relationList =
			// groupService.getAllRelation(relation);
			// if(!relationList.isEmpty()) {
			// User user = userService.selectUserById(userid);
			// List<String> pushUserList = new ArrayList<String>();
			// for(int i = 0; i < relationList.size(); i++) {
			// User followUser =
			// userService.selectUserById(relationList.get(i).getUid());
			// if(null != followUser) {
			// if(followUser.getUserStatus().equals(4)) {
			// continue;
			// }
			// pushUserList.add(followUser.getEasemobId());
			// }
			// }
			// String msg = "&" + user.getUserName() + "&在组合#" + accountsName
			// +"("+ gid + atype +")#" + buyOrSall + "$" + stockName + "(" +
			// stock + ")$" + vol*100 + "股,下单价格:" + price;
			// ObjectNode ext = JsonNodeFactory.instance.objectNode();
			// sendEaseMobMsg(pushUserList, ext, msg);
			// }
			return map;
		}
	}

	@RequestMapping(value = "/queryorder", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryOrder(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid=Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token=request.getParameter("token");
		if(validateToken(userid, token)){
			OrderBook orderbook = new OrderBook();
			orderbook.setGid(gid);
			List<OrderBook> orderList = groupService.getOrderBookList(orderbook);
			if (!orderList.isEmpty()) {
				map = rspFormat(orderList, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
		}else{
			map=rspFormat("", WRONG_TOKEN);
		}
		
		return map;
	}

	@RequestMapping(value = "/deleteorder", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> deleteOrder(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		Integer orderid = Integer.parseInt(request.getParameter("orderid"));
		String token = request.getParameter("token");
		Integer gid = 0;
		Integer vol = 0;
		String stock = "";
		String accountsName = "";
		String stockName = "";
		String buyOrSall = "";
		String atype = "";
		if (validateToken(userid, token)) {
			OrderBook orderbook = new OrderBook();
			orderbook.setOrderid(orderid);
			OrderBook mainOrder = groupService.getOrderBook(orderbook);
			if (null != mainOrder) {
				Integer hasDelete = groupService.deleteOrderBook(orderbook);
				if (hasDelete > 0) {

				} else {
					map = rspFormat("", HAVE_TRADE);
					return map;
				}
				Accounts accounts = new Accounts();
				accounts.setGid(mainOrder.getGid());
				Accounts mainAccounts = groupService.getAccounts(accounts);
				gid = mainOrder.getGid();
				accountsName = mainAccounts.getGname();
				if (mainAccounts.getAttr().equals(1)
						|| mainAccounts.getAttr().equals(5)
						|| mainAccounts.getAttr().equals(6)) {
					atype = ",1";
				}
				if (mainOrder.getAction() == 1) {
					buyOrSall = "撤销买入";
					double money = mainOrder.getVol() * mainOrder.getPrice()
							* 100 * 1.0003;
					double frozen = 0;

					mainAccounts.setAvailable(mainAccounts.getAvailable()
							+ money);
					if (mainAccounts.getFrozen() - money > 0.01) {
						frozen = mainAccounts.getFrozen() - money;
					}
					mainAccounts.setFrozen(frozen);
					groupService.updateAccounts(mainAccounts);
				} else if (mainOrder.getAction() == 3) {
					buyOrSall = "撤销卖出";
					Posi posi = new Posi();
					posi.setGid(mainOrder.getGid());
					posi.setStockid(mainOrder.getStockid());
					Posi mainPosi = groupService.getStockPosi(posi);
					Integer frozen_stock = mainPosi.getFrozen()
							- mainOrder.getVol();
					frozen_stock = frozen_stock <= 0 ? 0 : frozen_stock;
					mainPosi.setFrozen(frozen_stock);
					mainPosi.setAvailable(mainPosi.getAvailable()
							+ mainOrder.getVol());
					groupService.updatePosi(mainPosi);
				}
				Date date = new Date();
				Traderec traderec = new Traderec();
				traderec.setGid(mainOrder.getGid());
				traderec.setStockid(mainOrder.getStockid());
				traderec.setOrderprice(mainOrder.getPrice());
				traderec.setVol(mainOrder.getVol());
				traderec.setAction(mainOrder.getAction());
				traderec.setStatus(2);
				traderec.setInserttime(mainOrder.getInserttime());
				traderec.setTradetime(date);
				groupService.initTraderec(traderec);

				stock = mainOrder.getStockid();
				stock = stock.startsWith("6") ? stock + ".SS" : stock + ".SZ";
				vol = mainOrder.getVol();
				//stockName = mainOrder.getName();

				map = rspFormat("", SUCCESS);
			} else {
				map = rspFormat("", HAVE_TRADE);
				return map;
			}

		} else {
			map = rspFormat("", WRONG_TOKEN);
			return map;
		}

		// Relation relation = new Relation();
		// relation.setGid(gid);
		// List<Relation> relationList = groupService.getAllRelation(relation);
		// if(!relationList.isEmpty()) {
		// User user = userService.selectUserById(userid);
		// // String from = "lbh3zyi";
		// // String targetTypeus = "users";
		// // ObjectNode ext = factory.objectNode();
		// // ArrayNode targetusers = factory.arrayNode();
		// List<String> pushUserList = new ArrayList<String>();
		// for(int i = 0; i < relationList.size(); i++) {
		// User followUser =
		// userService.selectUserById(relationList.get(i).getUid());
		// if(null != followUser) {
		// if(followUser.getUserStatus().equals(4)) {
		// continue;
		// }
		// pushUserList.add(followUser.getEasemobId());
		// }
		// }
		// // ObjectNode txtmsg = factory.objectNode();
		// // txtmsg.put("msg", "#" + accountsName +"(" + gid + atype + ")#" +
		// buyOrSall + "$" + stockName + "(" + stock + ")$" + vol*100 + "股" );
		// // txtmsg.put("type","txt");
		// // ObjectNode sendTxtMessageusernode =
		// EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from,
		// ext);
		// String msg = "#" + accountsName +"(" + gid + atype + ")#" + buyOrSall
		// + "$" + stockName + "(" + stock + ")$" + vol*100 + "股";
		// ObjectNode ext = JsonNodeFactory.instance.objectNode();
		// sendEaseMobMsg(pushUserList, ext, msg);
		// }
		return map;
	}

	@RequestMapping(value = "/position", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> position(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = null;
		List<Map<String, Object>> arr= new ArrayList<Map<String,Object>>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token =request.getParameter("token");
		if(validateToken(userid, token)){
			Posi posi = new Posi();
			posi.setGid(gid);
			List<Posi> posiList = groupService.getPosi(posi);
			if (!posiList.isEmpty()) {
				String stockid=null;
				Stockinfo stockinfo=null;
				Stockinfo stockinfo2=null;
				Integer bspoint=null;
				String stock=null;
				for (Posi posi2 : posiList) {
					stockid=posi2.getStockid();
					stock = stockid.startsWith("6") ? stockid + ".SS"
							: stockid + ".SZ";
					stockinfo=new Stockinfo();
					stockinfo.setStockid(stockid);
					stockinfo2=	stockInfoService.getStockinfo(stockinfo);
					bspoint=stockinfo2.getBspoint();
					map=new HashMap<String, Object>();
					map.put("bspoint", bspoint);
					map.put("id", posi2.getId());
					map.put("gid", posi2.getGid());
					map.put("stockid", stockid);
					map.put("stock", stock);
					map.put("vol", posi2.getVol());
					map.put("available", posi2.getAvailable());
					map.put("frozen", posi2.getFrozen());
					map.put("price", posi2.getPrice());
					map.put("onway", posi2.getOnway());
					map.put("name", posi2.getName());
					arr.add(map);
					map = rspFormat(arr, SUCCESS);
				}
			} else {
				map = rspFormat("", SUCCESS);
			}
		}else{
			map=rspFormat("", WRONG_TOKEN);
		}
		
		return map;
	}

	@RequestMapping(value = "/records", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> records(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String id = request.getParameter("gid");
		String token=request.getParameter("token");
		if(validateToken(userid, token)){
			if (id != null && !id.isEmpty()) {
				Integer gid = Integer.parseInt(id);
				String pN = request.getParameter("pageNow");
				List<Traderec> traderecList = new ArrayList<Traderec>();
				if (pN != null && !pN.isEmpty()) {
					Integer pageNow = Integer.parseInt(pN);
					Integer pageCount = (pageNow - 1) * 10;
					traderecList = groupService.getTraderecList2(gid, pageCount);
				} else {
					Traderec traderec = new Traderec();
					traderec.setGid(gid);
					traderecList = groupService.getTraderecList(traderec);
				}

				if (!traderecList.isEmpty()) {
					map = rspFormat(traderecList, SUCCESS);
				} else {
					map = rspFormat("", SUCCESS);
				}
			} else {
				map = rspFormat("", WRONG_PARAM);
			}
		}else{
			map=rspFormat("", WRONG_TOKEN);
		}
		
		return map;
	}

	@RequestMapping(value = "/queryscore", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryScore(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map =null; 
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token =request.getParameter("token");
		if(validateToken(userid, token)){
			Score score = new Score();
			score.setGid(gid);
			List<Score> scoreList = groupService.getScoreList(score);
			List<Map<String,Object>> arr=new ArrayList<Map<String,Object>>();
			if (!scoreList.isEmpty()) {
				for (Score score2 : scoreList) {
					map=new HashMap<String, Object>();
					map.put("rid", score2.getRid());
					map.put("total", score2.getTotal());
					map.put("date", score2.getDate());
					arr.add(map);
				}
				map = rspFormat(arr, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
		}else{
			map=rspFormat("", WRONG_TOKEN);
		}
		
		return map;
	}

	@RequestMapping(value = "/querystat", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryStat(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String useridString = request.getParameter("userid");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Stat stat = new Stat();
		stat.setGid(gid);
		Stat mainStat = groupService.getStat(stat);
		if (null != mainStat) {
			User user = userService.selectUserById(mainStat.getAccounts()
					.getUserid());
			if (null != user) {
				mainStat.getAccounts().setUser(user);
			}
			if (null != useridString) {
				Integer userid = Integer.parseInt(useridString);
				Relation relation = new Relation();
				relation.setGid(mainStat.getGid());
				relation.setUid(userid);
				relation.setSubgid(0);
				relation.setDel(0);
				relation.setAttr(1);
				Relation mainRelation = groupService.getRelation(relation);
				if (null != mainRelation) {
					mainStat.getAccounts().setFollow(1);
				} else {
					mainStat.getAccounts().setFollow(0);
				}
			} else {
				mainStat.getAccounts().setFollow(0);
			}
			map = rspFormat(mainStat, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}

	@RequestMapping(value = "/querystatrank", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryStatRank(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String useridString = request.getParameter("userid");
		Integer start = Integer.parseInt(request.getParameter("start"));
		String sortby = request.getParameter("sortby");
		Stat stat = new Stat();
		stat.setDel(start);
		stat.setSort("stat." + sortby);
		List<Stat> mainList = groupService.getRankStatList(stat);
		if (!mainList.isEmpty()) {
			for (int i = 0; i < mainList.size(); i++) {
				User user = userService.selectUserById(mainList.get(i)
						.getAccounts().getUserid());
				mainList.get(i).getAccounts().setUser(user);
				mainList.get(i).setSort(sortby);
				if (null != useridString) {
					Integer userid = Integer.parseInt(useridString);
					Relation relation = new Relation();
					relation.setGid(mainList.get(i).getGid());
					relation.setUid(userid);
					relation.setSubgid(0);
					relation.setDel(0);
					relation.setAttr(1);
					Relation mainRelation = groupService.getRelation(relation);
					if (null != mainRelation) {
						mainList.get(i).getAccounts().setFollow(1);
					} else {
						mainList.get(i).getAccounts().setFollow(0);
					}
				} else {
					mainList.get(i).getAccounts().setFollow(0);
				}
			}
			map = rspFormat(mainList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}

	@RequestMapping(value = "/follow", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> follow(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		User mainuser = userService.selectUserById(userid);
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Integer action = Integer.parseInt(request.getParameter("act"));
		Integer subgid = Integer.parseInt(request.getParameter("subgid"));
		double amount = Double.parseDouble(request.getParameter("amount"));
		String token = request.getParameter("token");
		String atype = "";
		Date createtime = new Date();
		Relation relation = new Relation();
		Relation mainRelation = new Relation();
		relation.setGid(gid);
		relation.setSubgid(subgid);
		relation.setUid(userid);
		relation.setDel(0);
		relation.setAmount(amount);
		relation.setCreatetime(createtime);
		if (validateToken(userid, token)) {
			switch (action) {
			case 1:
				relation.setAttr(1);
				mainRelation = groupService.getRelation(relation);
				if (null != mainRelation) {
					map = rspFormat("", SUCCESS);
				} else {
					Integer hasFocus = groupService.initRelation(relation);
					if (hasFocus > 0) {
						Accounts accounts = new Accounts();
						accounts.setGid(gid);
						Accounts mainAccounts = groupService
								.getAccounts(accounts);
						if (mainAccounts.getAttr().equals(1)
								|| mainAccounts.getAttr().equals(5)
								|| mainAccounts.getAttr().equals(6)) {
							atype = ",1";
						}
						if (mainAccounts.getAttr().equals(12)) {
							atype = ",2";
						}
						mainAccounts.setGznum(mainAccounts.getGznum() + 1);
						groupService.updateAccounts(mainAccounts);
						map = rspFormat("", SUCCESS);

						User user = userService.selectUserById(mainAccounts
								.getUserid());
						if (null != user) {
							List<String> pushUserList = new ArrayList<>();
							// String from = "lbh3zyi";
							// String targetTypeus = "users";
							// ObjectNode ext = factory.objectNode();
							// ArrayNode targetusers = factory.arrayNode();
							// targetusers.add(user.getEasemobId());
							// ObjectNode txtmsg = factory.objectNode();
							// txtmsg.put("msg", "&" + mainuser.getUserName() +
							// "&订阅了您的组合#" + mainAccounts.getGname() +"(" +
							// mainAccounts.getGid() + atype+")#");
							// txtmsg.put("type","txt");
							// ObjectNode sendTxtMessageusernode =
							// EasemobMessages.sendMessages(targetTypeus,
							// targetusers, txtmsg, from, ext);
							pushUserList.add(user.getEasemobId());
							String msg = "&" + mainuser.getUserName()
									+ "&订阅了您的组合#" + mainAccounts.getGname()
									+ "(" + mainAccounts.getGid() + atype
									+ ")#";
							ObjectNode ext = JsonNodeFactory.instance
									.objectNode();
							sendEaseMobMsg(pushUserList, ext, msg);

						}
					} else {
						map = rspFormat("", WRONG_MYSQL_OPERATION);
					}
				}
				break;
			case 2:
				relation.setAttr(2);
				mainRelation = groupService.getRelation(relation);
				if (null != mainRelation) {
					mainRelation.setAmount(amount);
					mainRelation.setCreatetime(createtime);
					groupService.updateRelation(mainRelation);
					map = rspFormat("", SUCCESS);
				} else {
					Integer hasFocus = groupService.initRelation(relation);
					if (hasFocus > 0) {
						Accounts accounts = new Accounts();
						accounts.setGid(gid);
						Accounts mainAccounts = groupService
								.getAccounts(accounts);
						if (mainAccounts.getAttr().equals(1)
								|| mainAccounts.getAttr().equals(5)
								|| mainAccounts.getAttr().equals(6)) {
							atype = ",1";
						}
						if (mainAccounts.getAttr().equals(12)) {
							atype = ",2";
						}
						mainAccounts.setGtnum(mainAccounts.getGtnum() + 1);
						groupService.updateAccounts(mainAccounts);
						map = rspFormat("", SUCCESS);

						User user = userService.selectUserById(mainAccounts
								.getUserid());
						if (null != user) {
							// String from = "lbh3zyi";
							// String targetTypeus = "users";
							// ObjectNode ext = factory.objectNode();
							// ArrayNode targetusers = factory.arrayNode();
							// targetusers.add(user.getEasemobId());
							// ObjectNode txtmsg = factory.objectNode();
							// txtmsg.put("msg", "&"+mainuser.getUserName() +
							// "&跟投了您的组合#" + mainAccounts.getGname() +"(" +
							// mainAccounts.getGid() + atype +")#");
							// txtmsg.put("type","txt");
							// ObjectNode sendTxtMessageusernode =
							// EasemobMessages.sendMessages(targetTypeus,
							// targetusers, txtmsg, from, ext);
							List<String> pushUserList = new ArrayList<>();
							pushUserList.add(user.getEasemobId());
							String msg = "&" + mainuser.getUserName()
									+ "&跟投了您的组合#" + mainAccounts.getGname()
									+ "(" + mainAccounts.getGid() + atype
									+ ")#";
							ObjectNode ext = JsonNodeFactory.instance
									.objectNode();
							sendEaseMobMsg(pushUserList, ext, msg);
						}
					} else {
						map = rspFormat("", WRONG_MYSQL_OPERATION);
					}
				}
				break;
			case 3:
				relation.setAttr(1);
				mainRelation = groupService.getRelation(relation);
				if (null != mainRelation) {
					groupService.deleteFollowRelation(mainRelation);
					Accounts accounts = new Accounts();
					accounts.setGid(gid);
					Accounts mainAccounts = groupService.getAccounts(accounts);
					mainAccounts.setGznum(mainAccounts.getGznum() - 1);
					groupService.updateAccounts(mainAccounts);
					map = rspFormat("", SUCCESS);
				} else {
					map = rspFormat("", SUCCESS);
				}
				break;
			case 4:
				relation.setAttr(2);
				mainRelation = groupService.getRelation(relation);
				if (null != mainRelation) {
					groupService.deleteFollowRelation(mainRelation);
					Accounts accounts = new Accounts();
					accounts.setGid(gid);
					Accounts mainAccounts = groupService.getAccounts(accounts);
					mainAccounts.setGtnum(mainAccounts.getGtnum() - 1);
					groupService.updateAccounts(mainAccounts);
					map = rspFormat("", SUCCESS);
				} else {
					map = rspFormat("", SUCCESS);
				}
				break;
			}

		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/queryfollow", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryFollow(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Integer type = Integer.parseInt(request.getParameter("type"));
		Relation relation = new Relation();
		relation.setGid(gid);
		relation.setDel(0);
		switch (type) {
		case 0:
			relation.setAttr(2);
			List<Relation> mainRelation = groupService
					.selectFollowRelation(relation);
			if (!mainRelation.isEmpty()) {
				for (int i = 0; i < mainRelation.size(); i++) {
					User user = userService.selectUserById(mainRelation.get(i)
							.getAccounts().getUserid());
					mainRelation.get(i).getAccounts().setUser(user);
				}
				map = rspFormat(mainRelation, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
			break;
		case 1:
			relation.setAttr(2);
			List<Relation> followedRelation = groupService
					.selectFollowedRelation(relation);
			if (!followedRelation.isEmpty()) {
				for (int i = 0; i < followedRelation.size(); i++) {
					User user = userService.selectUserById(followedRelation
							.get(i).getAccounts().getUserid());
					followedRelation.get(i).getAccounts().setUser(user);
				}
				map = rspFormat(followedRelation, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
			break;
		case 2:
			relation.setAttr(1);
			List<Integer> interestedRelation = groupService
					.selectInterestedRelation(relation);
			if (!interestedRelation.isEmpty()) {
				List<User> userList = new ArrayList<User>();
				Order o = new Order();
				o.setDetail(gid.toString());
				Order order = cashService.getZuheOrderByDetail(o);
				for (int i = 0; i < interestedRelation.size(); i++) {
					Integer userId = interestedRelation.get(i);
					User user = userService.selectUserById(userId);
					user.setMember(0);
					if (null != order) {
						if (order.getUserId().equals(userId)) {
							user.setMember(1);
						}
					}
					userList.add(user);
				}
				map = rspFormat(userList, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
			break;
		default:
			break;
		}
		return map;
	}

	@RequestMapping(value = "/search", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> search(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String key = request.getParameter("key");
		Accounts accounts = new Accounts();
		accounts.setGname(key);
		List<Accounts> accountList = groupService.searchAccounts(accounts);
		if (!accountList.isEmpty()) {
			map = rspFormat(accountList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}

	// 2016-09-22增加查找12类型组合
	@RequestMapping(value = "/getbyname", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> search2(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String key = request.getParameter("key");
		Accounts account = groupService.searchAccountsByName(key);
		if (account != null) {
			map = rspFormat(account, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}

	@RequestMapping(value = "/queryinterest", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryInterest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer uid = Integer.parseInt(request.getParameter("userid"));
		Relation relation = new Relation();
		relation.setAttr(1);
		relation.setDel(0);
		relation.setUid(uid);
		List<Relation> mainRelation = groupService
				.selectInterestRelation(relation);
		if (!mainRelation.isEmpty()) {
			for (int i = 0; i < mainRelation.size(); i++) {
				User user = userService.selectUserById((Integer) mainRelation
						.get(i).getAccounts().getUserid());
				if (null != user) {
					mainRelation.get(i).getAccounts().setUser(user);
					RaiseInfo raiseInfo = new RaiseInfo();
					mainRelation.get(i).getAccounts().setRaiseInfo(raiseInfo);
					mainRelation.get(i).getAccounts().setUser(user);
					// mainRelation.get(i).getAccounts().setInit(INIT_TOTAL);
					// //设置初始金额
					mainRelation.get(i).getAccounts().getRaiseInfo()
							.setStimes(user.getStimes());
					mainRelation.get(i).getAccounts().getRaiseInfo()
							.setLtimes(user.getLtimes());
					mainRelation
							.get(i)
							.getAccounts()
							.getRaiseInfo()
							.setStop(
									mainRelation.get(i).getAccounts()
											.getOptigid());
					if (mainRelation.get(i).getAccounts().getAttr().equals(7)) {
						Order order = new Order();
						order.setOutTradeNo(mainRelation.get(i).getAccounts()
								.getGid().toString());
						Order mainOrder = cashService
								.getOrderByOutTradeNo(order);

						mainRelation.get(i).getAccounts().getRaiseInfo()
								.setFee(mainOrder.getFeeTotal());
						mainRelation.get(i).getAccounts().getRaiseInfo()
								.setBonus(mainOrder.getFeeLeft());
						mainRelation
								.get(i)
								.getAccounts()
								.getRaiseInfo()
								.setNum(Integer.parseInt(mainOrder.getDetail()));
						mainRelation
								.get(i)
								.getAccounts()
								.getRaiseInfo()
								.setProfit(
										Float.parseFloat(mainOrder.getBody()));

						order.setOrderId(mainOrder.getId());
						Integer orderListSize = cashService
								.getFollowListOrderSize(order);
						mainRelation.get(i).getAccounts().getRaiseInfo()
								.setJoinNum(orderListSize);
						mainRelation.get(i).getAccounts().getRaiseInfo()
								.setPay(1);
					}
				}
			}
			map = rspFormat(mainRelation, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}

		return map;
	}

	@RequestMapping(value = "/createraise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> createRaise(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		float fee = Float.parseFloat(request.getParameter("fee"));
		float bonus = Float.parseFloat(request.getParameter("bonus"));
		float profit = Float.parseFloat(request.getParameter("profit"));
		float feeOrigin = 0;
		if (null != request.getParameter("feeOrigin")) {
			feeOrigin = Float.parseFloat(request.getParameter("feeOrigin"));
		}
		Integer stop = 0;
		Integer num = Integer.parseInt(request.getParameter("num"));
		Integer floor = 0;
		if (null != request.getParameter("floor")) {
			floor = Integer.parseInt(request.getParameter("floor"));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
		String gname = request.getParameter("gname");
		String info = request.getParameter("info");
		String token = request.getParameter("token");
		if (validateToken(userid, token)) {
			Accounts accounts = new Accounts();
			accounts.setGname(gname);
			List<Accounts> listAccounts = groupService
					.getAccountsList(accounts);
			if (!listAccounts.isEmpty()) {
				map = rspFormat("", HAVA_SAME_PROTFOLIO);
				return map;
			} else {
				accounts.setUserid(userid);
				List<Accounts> listExistAccounts = groupService
						.getAccountsList(accounts);
				if (!listExistAccounts.isEmpty()
						&& listExistAccounts.size() >= 10) {
					map = rspFormat("", HAVE_ENOUGH_PROTFOLIO);
					return map;
				} else {
					try {
						Date raisetime = sdf.parse(request
								.getParameter("raisetime"));
						Date endtime = sdf.parse(request
								.getParameter("endtime"));

						String raiseDate = dateSdf.format(raisetime);
						String endDate = dateSdf.format(endtime);
						if (AccountUtil.HOLIDAY_STRING.contains(raiseDate)
								|| AccountUtil.HOLIDAY_STRING.contains(endDate)) {
							map = rspFormat("", HOLIDAY);
							return map;
						}

						Date createtime = new Date();
						Integer c2r = Time.daysBetween(createtime, raisetime);
						Integer r2e = Time.daysBetween(raisetime, endtime);
						Integer n2r = Time.compareTime(createtime, raisetime);
						Calendar c = Calendar.getInstance();
						c.setTime(raisetime);
						c.add(Calendar.HOUR, 9);
						raisetime = c.getTime();
						c.setTime(endtime);
						c.add(Calendar.HOUR, 15);
						endtime = c.getTime();
						if (!(n2r < 0 && c2r <= 30 && r2e >= 1 && r2e <= 90)) {
							map = rspFormat("", WRONG_PARAM);
							return map;
						} else {
							if (profit >= 3 && profit <= 50) {
								if (bonus > 0) {
									String password = request
											.getParameter("password");
									CashAccount cashAccount = cashService
											.getCashAccountByUserId(userid);
									if (null != password) {
										if (cashAccount.getPassword().equals(
												MD5Util.string2MD5(password))) {
											if (cashAccount.getTotalCash()
													- bonus >= 0) {
												cashAccount
														.setTotalCash(cashAccount
																.getTotalCash()
																- bonus);
												cashAccount
														.setTimeTill(createtime);
												cashService
														.updateCashAccount(cashAccount);
											} else {
												map = rspFormat("",
														NO_ENOUGH_MONEY);
												return map;
											}
										} else {
											map = rspFormat("", WRONG_PASSWORD);
											return map;
										}
									} else {
										if (cashAccount.getTotalCash() - bonus >= 0) {
											cashAccount
													.setTotalCash(cashAccount
															.getTotalCash()
															- bonus);
											cashAccount.setTimeTill(createtime);
											cashService
													.updateCashAccount(cashAccount);
										} else {
											map = rspFormat("", NO_ENOUGH_MONEY);
											return map;
										}
									}
								}

							} else {
								map = rspFormat("", WRONG_PARAM);
								return map;
							}
							if (null != request.getParameter("stop")) {
								stop = Integer.parseInt(request
										.getParameter("stop"));
							}
							accounts.setInfo(info);
							accounts.setCreatetime(createtime);
							accounts.setRaisetime(raisetime);
							accounts.setEndtime(endtime);
							accounts.setAttr(7);
							accounts.setOptigid(stop);
							double giveTotal = INIT_TOTAL;
							accounts.setAvailable(giveTotal);
							accounts.setInit(giveTotal);
							accounts.setTotal(giveTotal);
							accounts.setGtnum(0);
							accounts.setGznum(0);
							accounts.setDel(0);
							Integer hasInsert = groupService
									.initAccounts(accounts);
							if (hasInsert > 0) {
								Integer gid = accounts.getGid();
								Stat stat = new Stat();
								stat.setGid(gid);
								stat.setR7(0);
								stat.setR30(0);
								stat.setR6m(0);
								stat.setR1y(0);
								groupService.initStat(stat);
								map = rspFormat(accounts, SUCCESS);

								String transactionId = createtime.getTime()
										+ "";
								Integer tradeType = 16;
								Integer status = 5;

								Order useOrder = new Order();
								useOrder.setTransactionId(transactionId);
								useOrder.setOutTradeNo(accounts.getGid()
										.toString());
								useOrder.setUserId(userid);
								useOrder.setOrderId(0);
								useOrder.setTimeStart(createtime);
								useOrder.setTimeExpire(endtime);
								useOrder.setTradeType(tradeType);
								useOrder.setFeeTotal(fee);
								useOrder.setFeeOrigin(feeOrigin);
								useOrder.setFeeLeft(bonus);
								useOrder.setBody(profit + "");
								useOrder.setDetail(num.toString());
								useOrder.setExpand(floor.toString());
								useOrder.setStatus(status);
								cashService.initOrder(useOrder);
							} else {
								map = rspFormat("", WRONG_MYSQL_OPERATION);
								return map;
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						map = rspFormat("", WRONG_PARAM_FORMAT);
						return map;
					}
				}
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
			return map;
		}
		return map;
	}

	@RequestMapping(value = "/cancelraise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> cancelRaise(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String token = request.getParameter("token");
		if (validateToken(userId, token)) {
			Accounts accounts = new Accounts();
			accounts.setGid(gid);
			Accounts mainAccounts = groupService.getAccounts(accounts);
			Date deltime = new Date();
			if (null != mainAccounts) {
				User user = userService.selectUserById(userId);
				Order order = new Order();
				order.setOutTradeNo(gid.toString());
				Order mainOrder = cashService.getOrderByOutTradeNo(order);
				order.setOrderId(mainOrder.getId());
				// List<Order> orderList = cashService.getListOrder(order);
				Integer orderListSize = cashService
						.getFollowListOrderSize(order);
				if (orderListSize.equals(0)) {
					mainAccounts.setDel(1);
					mainAccounts.setDeltime(deltime);
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
				} else if (orderListSize > 0) {
					mainAccounts.setDel(3);
					mainAccounts.setDeltime(deltime);
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
					user.setLtimes(user.getLtimes() + 1);
					userService.updateUserSuper(user);
				}
				map = rspFormat("", SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/followraise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> followRaise(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String password = request.getParameter("password");
		String token = request.getParameter("token");
		Date createtime = new Date();
		User u = userService.selectUserById(userid);
		if (validateToken(userid, token)) {
			CashAccount cashAccount = cashService
					.getCashAccountByUserId(userid);
			Order order = new Order();
			order.setOutTradeNo(gid.toString());
			Order mainOrder = cashService.getOrderByOutTradeNo(order);
			order.setOrderId(mainOrder.getId());
			// List<Order> orderList = cashService.getFollowListOrder(order);
			Integer orderListSize = cashService.getFollowListOrderSize(order);
			order.setUserId(userid);
			Order userOrder = cashService.hasGetOrder(order);
			if (null != userOrder) {
				map = rspFormat("", HAVE_FOLLOW);
				return map;
			}
			if (true) {
				if (orderListSize < Integer.parseInt(mainOrder.getDetail())) {
					float fee = mainOrder.getFeeTotal();
					// System.out.println(fee + "====" +
					// cashAccount.getTotalCash());
					if (cashAccount.getTotalCash() >= fee) {
						Accounts a = new Accounts();
						a.setGid(gid);
						Accounts ma = groupService.getAccounts(a);
						if (null == ma) {
							map = rspFormat("", FAIL);
							return map;
						}
						int dayleft = Time.daysBetween(createtime,
								ma.getRaisetime());
						if (dayleft < 0) {
							map = rspFormat("", BEYOND_TIME);
							return map;
						}
						if (fee > 0) {
							if (null != password) {
								if (cashAccount.getPassword().equals(
										MD5Util.string2MD5(password))) {
									cashAccount.setTotalCash(cashAccount
											.getTotalCash() - fee);
									cashAccount.setTimeTill(createtime);
									cashService.updateCashAccount(cashAccount);
									map = rspFormat("", SUCCESS);
								} else {
									map = rspFormat("", WRONG_PASSWORD);
									return map;
								}
							} else {
								cashAccount.setTotalCash(cashAccount
										.getTotalCash() - fee);
								cashAccount.setTimeTill(createtime);
								cashService.updateCashAccount(cashAccount);
								map = rspFormat("", SUCCESS);
							}
						} else {
							map = rspFormat("", FAIL);
							return map;
						}
						Date timeStart = new Date();
						String transactionId = timeStart.getTime() + "";
						String outTradeNo = transactionId;
						order.setTransactionId(transactionId);
						order.setOutTradeNo(outTradeNo);
						order.setUserId(userid);
						order.setTimeStart(timeStart);
						order.setTimeExpire(ma.getEndtime());
						order.setTradeType(17);
						order.setFeeTotal(fee);
						order.setFeeLeft(0);
						order.setBody("组合付费");
						order.setDetail("0");
						order.setStatus(5);
						cashService.initOrder(order);

						Relation relation = new Relation();
						relation.setGid(gid);
						relation.setSubgid(0);
						relation.setUid(userid);
						relation.setDel(0);
						relation.setAmount(0);
						relation.setCreatetime(createtime);
						relation.setAttr(1);
						Integer hasFocus = groupService.initRelation(relation);
						if (hasFocus > 0) {
							Accounts accounts = new Accounts();
							accounts.setGid(gid);
							Accounts mainAccounts = groupService
									.getAccounts(accounts);
							mainAccounts.setGznum(mainAccounts.getGznum() + 1);
							groupService.updateAccounts(mainAccounts);

							User user = userService.selectUserById(ma
									.getUserid());
							// String from = "lbh3zyi";
							// String targetTypeus = "users";
							// ObjectNode ext = factory.objectNode();
							// ArrayNode targetusers = factory.arrayNode();
							// targetusers.add(user.getEasemobId());
							// ObjectNode txtmsg = factory.objectNode();
							// txtmsg.put("msg", "&" + u.getUserName() +
							// "&订阅了您的收费组合#" + mainAccounts.getGname() +"(" +
							// mainAccounts.getGid() + ")#");
							// txtmsg.put("type","txt");
							// ObjectNode sendTxtMessageusernode =
							// EasemobMessages.sendMessages(targetTypeus,
							// targetusers, txtmsg, from, ext);

							List<String> pushUserList = new ArrayList<>();
							pushUserList.add(user.getEasemobId());
							String msg = "&" + u.getUserName() + "&订阅了您的收费组合#"
									+ mainAccounts.getGname() + "("
									+ mainAccounts.getGid() + ")#";
							ObjectNode ext = JsonNodeFactory.instance
									.objectNode();
							sendEaseMobMsg(pushUserList, ext, msg);
						}
					} else {
						map = rspFormat("", NO_ENOUGH_MONEY);
					}
				} else {
					map = rspFormat("", BEYOND_LIMIT);
				}
			} else {
				map = rspFormat("", FAIL);
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/getraise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getRaise(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String useridString = request.getParameter("userid");
		Integer start = 0;
		String startString = request.getParameter("start");
		if (null != startString) {
			start = Integer.parseInt(startString);
		}
		String sort = "DESC";
		Integer limit = PAGE_SIZE;
		List<Order> rankOrderList = cashService.getRankZuheOrder(start, limit,
				sort);
		ArrayList<Accounts> list = new ArrayList<Accounts>();
		for (Order mainOrder : rankOrderList) {
			Accounts a = new Accounts();
			a.setGid(Integer.parseInt(mainOrder.getOutTradeNo()));
			Accounts accounts = groupService.getAccounts(a);
			if (null != accounts) {
				RaiseInfo raiseInfo = new RaiseInfo();
				accounts.setRaiseInfo(raiseInfo);
				User user = userService.selectUserById(mainOrder.getUserId());
				if (null != user) {
					accounts.getRaiseInfo().setStimes(user.getStimes());
					accounts.getRaiseInfo().setLtimes(user.getLtimes());
					accounts.getRaiseInfo().setStop(accounts.getOptigid());
					accounts.setUser(user);
					// accounts.setInit(INIT_TOTAL);
				}
				accounts.getRaiseInfo().setFee(mainOrder.getFeeTotal());
				accounts.getRaiseInfo().setBonus(mainOrder.getFeeLeft());
				accounts.getRaiseInfo().setNum(
						Integer.parseInt(mainOrder.getDetail()));
				accounts.getRaiseInfo().setProfit(
						Float.parseFloat(mainOrder.getBody()));
				accounts.getRaiseInfo().setFeeOrigin(mainOrder.getFeeOrigin());
				Order order = new Order();
				order.setOrderId(mainOrder.getId());
				// List<Order> orderList =
				// cashService.getFollowListOrder(order);
				// Integer orderListSize =
				// cashService.getFollowListOrderSize(order);
				accounts.getRaiseInfo().setJoinNum(accounts.getGznum());
				accounts.getRaiseInfo().setPay(0);

				// if(null != useridString) {
				// Integer userid = Integer.parseInt(useridString);
				// order.setUserId(userid);
				// Order userOrder = cashService.hasGetOrder(order);
				// if(null != userOrder) {
				// accounts.getRaiseInfo().setPay(1);
				// }
				// }
				list.add(accounts);
			}
		}
		if (list.isEmpty()) {
			map = rspFormat("", SUCCESS);
		} else {
			// System.out.println(list.size());
			map = rspFormat(list, SUCCESS);
		}
		return map;
	}

	/*
	 * @RequestMapping(value = "/getraise", method = { RequestMethod.POST})
	 * 
	 * @ResponseBody public Map<String, Object> getRaise(HttpServletRequest
	 * request, HttpServletResponse response) { Map<String, Object>map = new
	 * HashMap<String, Object>(); String useridString =
	 * request.getParameter("userid"); Integer start = 0; String startString =
	 * request.getParameter("start"); if(null != startString) { start =
	 * Integer.parseInt(startString); } List<Accounts> raiseList =
	 * groupService.getAllRaiseAccountsList(); ArrayList<Accounts> list = new
	 * ArrayList<Accounts>(); if(!raiseList.isEmpty()) { if(raiseList.size() >
	 * start) { for(int i = 0; i < raiseList.size(); i++) { Accounts
	 * mainAccounts = raiseList.get(i); RaiseInfo raiseInfo = new RaiseInfo();
	 * raiseList.get(i).setRaiseInfo(raiseInfo);
	 * 
	 * Order order = new Order();
	 * order.setOutTradeNo(mainAccounts.getGid().toString()); Order mainOrder =
	 * cashService.getOrderByOutTradeNo(order); if(null != mainOrder) {
	 * raiseList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
	 * raiseList.get(i).getRaiseInfo().setBonus(mainOrder.getFeeLeft());
	 * raiseList
	 * .get(i).getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail()));
	 * raiseList
	 * .get(i).getRaiseInfo().setProfit(Float.parseFloat(mainOrder.getBody()));
	 * raiseList.get(i).getRaiseInfo().setFeeOrigin(mainOrder.getFeeOrigin());
	 * 
	 * order.setOrderId(mainOrder.getId()); //List<Order> orderList =
	 * cashService.getFollowListOrder(order); //Integer orderListSize =
	 * cashService.getFollowListOrderSize(order);
	 * raiseList.get(i).getRaiseInfo().setJoinNum(mainAccounts.getGznum());
	 * raiseList.get(i).getRaiseInfo().setPay(0);
	 * 
	 * if(null != useridString) { Integer userid =
	 * Integer.parseInt(useridString); order.setUserId(userid); Order userOrder
	 * = cashService.hasGetOrder(order); if(null != userOrder) {
	 * raiseList.get(i).getRaiseInfo().setPay(1); } } } }
	 * Collections.sort(raiseList, (Accounts o1, Accounts o2) -> { float
	 * profit1= o1.getRaiseInfo().getProfit(); float profit2=
	 * o2.getRaiseInfo().getProfit(); if(profit1 > profit2) { return -1; } else
	 * if(profit1 > profit2) { return 1; } else { return 0; } }); for(int i =
	 * start; i < raiseList.size(); i++) { if(i < start + 20 ) {
	 * System.out.println(start); list.add(raiseList.get(i)); } else { break; }
	 * } } if(raiseList.size() < start + 20 ){ List<Accounts> runlist =
	 * groupService.getAllRunAccountsList(); if(start >= runlist.size() +
	 * raiseList.size()) { map = rspFormat("", SUCCESS); return map; } for(int i
	 * = 0; i < runlist.size(); i++) { Accounts mainAccounts = runlist.get(i);
	 * RaiseInfo raiseInfo = new RaiseInfo();
	 * runlist.get(i).setRaiseInfo(raiseInfo);
	 * 
	 * Order order = new Order();
	 * order.setOutTradeNo(mainAccounts.getGid().toString()); Order mainOrder =
	 * cashService.getOrderByOutTradeNo(order); if(null != mainOrder) {
	 * runlist.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
	 * runlist.get(i).getRaiseInfo().setBonus(mainOrder.getFeeLeft());
	 * runlist.get
	 * (i).getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail()));
	 * runlist
	 * .get(i).getRaiseInfo().setProfit(Float.parseFloat(mainOrder.getBody()));
	 * runlist.get(i).getRaiseInfo().setFeeOrigin(mainOrder.getFeeOrigin());
	 * 
	 * order.setOrderId(mainOrder.getId()); //List<Order> orderList =
	 * cashService.getFollowListOrder(order); //Integer orderListSize =
	 * cashService.getFollowListOrderSize(order);
	 * runlist.get(i).getRaiseInfo().setJoinNum(mainAccounts.getGznum());
	 * runlist.get(i).getRaiseInfo().setPay(0);
	 * 
	 * if(null != useridString) { Integer userid =
	 * Integer.parseInt(useridString); order.setUserId(userid); Order userOrder
	 * = cashService.hasGetOrder(order); if(null != userOrder) {
	 * runlist.get(i).getRaiseInfo().setPay(1); } } } }
	 * Collections.sort(runlist, (Accounts o1, Accounts o2) -> { float profit1=
	 * o1.getRaiseInfo().getProfit(); float profit2=
	 * o2.getRaiseInfo().getProfit(); if(profit1 > profit2) { return -1; } else
	 * if(profit1 > profit2) { return 1; } else { return 0; } });
	 * 
	 * 
	 * if(raiseList.size() > start) { Integer leftSize = start + 20 -
	 * raiseList.size(); for(int i = 0; i < leftSize; i++) { if(i <
	 * runlist.size()) { list.add(runlist.get(i)); } } } else { Integer newstart
	 * = start - raiseList.size(); for(int i = newstart; i < runlist.size();
	 * i++) { if(i < newstart + 20 ) { list.add(runlist.get(i)); } else { break;
	 * } } } }
	 * 
	 * for(Accounts accounts: list) { User user =
	 * userService.selectUserById(accounts.getUserid()); if(null != user) {
	 * accounts.getRaiseInfo().setStimes(user.getStimes());
	 * accounts.getRaiseInfo().setLtimes(user.getLtimes());
	 * accounts.getRaiseInfo().setStop(accounts.getOptigid());
	 * accounts.setUser(user); accounts.setInit(INIT_TOTAL); } } map =
	 * rspFormat(list, SUCCESS); } else { map = rspFormat("", SUCCESS); } return
	 * map; }
	 */

	@RequestMapping(value = "/gethomeraise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getHomeRaise(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String useridString = request.getParameter("userid");
		List<Home> homeRaiseList = homeService.getRaiseInfo();
		List<Accounts> raiseList = new ArrayList<>();
		Date now = new Date();
		if (!homeRaiseList.isEmpty()) {
			for (int i = 0; i < homeRaiseList.size(); i++) {
				Accounts a = new Accounts();
				a.setGid(Integer.parseInt(homeRaiseList.get(i)
						.getInfoContentId()));
				Accounts hAccounts = groupService.getAccounts(a);
				if (hAccounts != null) {
					raiseList.add(hAccounts);
				}
			}
			for (int i = 0; i < raiseList.size(); i++) {
				Accounts mainAccounts = raiseList.get(i);
				RaiseInfo raiseInfo = new RaiseInfo();
				raiseList.get(i).setRaiseInfo(raiseInfo);
				User user = userService
						.selectUserById(mainAccounts.getUserid());
				raiseList.get(i).getRaiseInfo().setStimes(user.getStimes());
				raiseList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
				raiseList.get(i).getRaiseInfo()
						.setStop(raiseList.get(i).getOptigid());
				raiseList.get(i).setUser(user);
				// raiseList.get(i).setInit(INIT_TOTAL);

				Order order = new Order();
				order.setOutTradeNo(mainAccounts.getGid().toString());
				Order mainOrder = cashService.getOrderByOutTradeNo(order);

				raiseList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
				raiseList.get(i).getRaiseInfo()
						.setBonus(mainOrder.getFeeLeft());
				raiseList.get(i).getRaiseInfo()
						.setNum(Integer.parseInt(mainOrder.getDetail()));
				raiseList.get(i).getRaiseInfo()
						.setProfit(Float.parseFloat(mainOrder.getBody()));
				raiseList.get(i).getRaiseInfo()
						.setFeeOrigin(mainOrder.getFeeOrigin());

				order.setOrderId(mainOrder.getId());
				Integer orderListSize = cashService
						.getFollowListOrderSize(order);
				raiseList.get(i).getRaiseInfo().setJoinNum(orderListSize);
				raiseList.get(i).getRaiseInfo().setPay(0);

				if (null != useridString) {
					Integer userid = Integer.parseInt(useridString);
					order.setUserId(userid);
					Order userOrder = cashService.hasGetOrder(order);
					if (null != userOrder) {
						raiseList.get(i).getRaiseInfo().setPay(1);
					}
				}
			}
			map = rspFormat(raiseList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}

		/*
		 * ArrayList<Accounts> list = new ArrayList<Accounts>(); if(null !=
		 * raiseList) { for(int i = 0; i < raiseList.size(); i++) { Accounts
		 * mainAccounts = raiseList.get(i); RaiseInfo raiseInfo = new
		 * RaiseInfo(); raiseList.get(i).setRaiseInfo(raiseInfo); User user =
		 * userService.selectUserById(mainAccounts.getUserid());
		 * raiseList.get(i).getRaiseInfo().setStimes(user.getStimes());
		 * raiseList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
		 * raiseList
		 * .get(i).getRaiseInfo().setStop(raiseList.get(i).getOptigid());
		 * raiseList.get(i).setUser(user); raiseList.get(i).setInit(INIT_TOTAL);
		 * 
		 * Order order = new Order();
		 * order.setOutTradeNo(mainAccounts.getGid().toString()); Order
		 * mainOrder = cashService.getOrderByOutTradeNo(order);
		 * 
		 * raiseList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
		 * raiseList.get(i).getRaiseInfo().setBonus(mainOrder.getFeeLeft());
		 * raiseList
		 * .get(i).getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail
		 * ()));
		 * raiseList.get(i).getRaiseInfo().setProfit(Float.parseFloat(mainOrder
		 * .getBody()));
		 * 
		 * order.setOrderId(mainOrder.getId()); List<Order> orderList =
		 * cashService.getFollowListOrder(order);
		 * raiseList.get(i).getRaiseInfo().setJoinNum(orderList.size());
		 * raiseList.get(i).getRaiseInfo().setPay(0);
		 * 
		 * if(null != useridString) { Integer userid =
		 * Integer.parseInt(useridString); order.setUserId(userid); Order
		 * userOrder = cashService.hasGetOrder(order); if(null != userOrder) {
		 * raiseList.get(i).getRaiseInfo().setPay(1); } }
		 * if(raiseList.get(i).getDel().equals(0) &&
		 * Time.compareTime(raiseList.get(i).getRaisetime(), now) > 0) {
		 * list.add(raiseList.get(i)); } } if(list.size() < RAISE_SIZE) {
		 * Integer left = RAISE_SIZE - list.size(); List<Accounts> leftList =
		 * groupService.getLeftRaiseAccountsList(left); if(!leftList.isEmpty())
		 * { for(int i = 0; i < leftList.size(); i++) { Accounts mainAccounts =
		 * leftList.get(i); RaiseInfo raiseInfo = new RaiseInfo();
		 * leftList.get(i).setRaiseInfo(raiseInfo); User user =
		 * userService.selectUserById(mainAccounts.getUserid());
		 * leftList.get(i).getRaiseInfo().setStimes(user.getStimes());
		 * leftList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
		 * leftList.get(i).getRaiseInfo().setStop(leftList.get(i).getOptigid());
		 * leftList.get(i).setUser(user); leftList.get(i).setInit(INIT_TOTAL);
		 * 
		 * Order order = new Order();
		 * order.setOutTradeNo(mainAccounts.getGid().toString()); Order
		 * mainOrder = cashService.getOrderByOutTradeNo(order); if(null !=
		 * mainOrder) {
		 * leftList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
		 * leftList.get(i).getRaiseInfo().setBonus(mainOrder.getFeeLeft());
		 * leftList
		 * .get(i).getRaiseInfo().setNum(Integer.parseInt(mainOrder.getDetail
		 * ()));
		 * leftList.get(i).getRaiseInfo().setProfit(Float.parseFloat(mainOrder
		 * .getBody()));
		 * 
		 * order.setOrderId(mainOrder.getId()); List<Order> orderList =
		 * cashService.getFollowListOrder(order);
		 * leftList.get(i).getRaiseInfo().setJoinNum(orderList.size());
		 * leftList.get(i).getRaiseInfo().setPay(0);
		 * 
		 * if(null != useridString) { Integer userid =
		 * Integer.parseInt(useridString); order.setUserId(userid); Order
		 * userOrder = cashService.hasGetOrder(order); if(null != userOrder) {
		 * leftList.get(i).getRaiseInfo().setPay(1); } }
		 * list.add(leftList.get(i)); } } }
		 * 
		 * }
		 */

		/**
		 * 首页组合按照收益排序
		 */
		/*
		 * // Collections.sort(list, (Accounts o1, Accounts o2) -> { // float
		 * profit1= o1.getRaiseInfo().getProfit(); // float profit2=
		 * o2.getRaiseInfo().getProfit(); // return profit1 > profit2 ? -1 : 1;
		 * // }); map = rspFormat(list, SUCCESS); } else { map = rspFormat("",
		 * SUCCESS); }
		 */
		return map;
	}

	@RequestMapping(value = "/gethomesuccess", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getHomeSuccess(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String useridString = request.getParameter("userid");
		List<Home> homeRaiseList = homeService.getSuccessInfo();
		List<Accounts> raiseList = new ArrayList<>();
		Date now = new Date();
		if (!homeRaiseList.isEmpty()) {
			for (int i = 0; i < homeRaiseList.size(); i++) {
				Accounts a = new Accounts();
				a.setGid(Integer.parseInt(homeRaiseList.get(i)
						.getInfoContentId()));
				Accounts hAccounts = groupService.getAccounts(a);
				if (hAccounts != null) {
					raiseList.add(hAccounts);
				}
			}
			for (int i = 0; i < raiseList.size(); i++) {
				Accounts mainAccounts = raiseList.get(i);
				RaiseInfo raiseInfo = new RaiseInfo();
				raiseList.get(i).setRaiseInfo(raiseInfo);
				User user = userService
						.selectUserById(mainAccounts.getUserid());
				raiseList.get(i).getRaiseInfo().setStimes(user.getStimes());
				raiseList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
				raiseList.get(i).getRaiseInfo()
						.setStop(raiseList.get(i).getOptigid());
				raiseList.get(i).setUser(user);
				// raiseList.get(i).setInit(INIT_TOTAL);

				Order order = new Order();
				order.setOutTradeNo(mainAccounts.getGid().toString());
				Order mainOrder = cashService.getOrderByOutTradeNo(order);

				raiseList.get(i).getRaiseInfo().setFee(mainOrder.getFeeTotal());
				raiseList.get(i).getRaiseInfo()
						.setBonus(mainOrder.getFeeLeft());
				raiseList.get(i).getRaiseInfo()
						.setNum(Integer.parseInt(mainOrder.getDetail()));
				raiseList.get(i).getRaiseInfo()
						.setProfit(Float.parseFloat(mainOrder.getBody()));
				raiseList.get(i).getRaiseInfo()
						.setFeeOrigin(mainOrder.getFeeOrigin());

				order.setOrderId(mainOrder.getId());
				Integer orderListSize = cashService
						.getFollowListOrderSize(order);
				raiseList.get(i).getRaiseInfo().setJoinNum(orderListSize);
				raiseList.get(i).getRaiseInfo().setPay(0);

				if (null != useridString) {
					Integer userid = Integer.parseInt(useridString);
					order.setUserId(userid);
					Order userOrder = cashService.hasGetOrder(order);
					if (null != userOrder) {
						raiseList.get(i).getRaiseInfo().setPay(1);
					}
				}
			}
			map = rspFormat(raiseList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}

	@RequestMapping(value = "/queryfreezuhe", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryFreeZuhe(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		Accounts accounts = new Accounts();
		accounts.setUserid(userid);
		List<Accounts> accountsList = groupService
				.getFreeAccountsList(accounts);
		if (!accountsList.isEmpty()) {
			// for(int i = 0; i < accountsList.size(); i++) {
			// accountsList.get(i).setInit(INIT_TOTAL);
			// }
			map = rspFormat(accountsList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}

		return map;
	}

	@RequestMapping(value = "/queryraisezuhe", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryRaiseZuhe(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		Accounts accounts = new Accounts();
		accounts.setUserid(userid);
		List<Accounts> raiseList = groupService.getRaiseAccountsList(accounts);
		if (!raiseList.isEmpty()) {
			for (int i = 0; i < raiseList.size(); i++) {
				Accounts mainAccounts = raiseList.get(i);
				User user = userService
						.selectUserById(mainAccounts.getUserid());
				raiseList.get(i).setUser(user);

				/*
				 * RaiseInfo raiseInfo = new RaiseInfo();
				 * raiseList.get(i).setRaiseInfo(raiseInfo);
				 * 
				 * raiseList.get(i).getRaiseInfo().setStimes(user.getStimes());
				 * raiseList.get(i).getRaiseInfo().setLtimes(user.getLtimes());
				 * raiseList
				 * .get(i).getRaiseInfo().setStop(raiseList.get(i).getOptigid
				 * ());
				 * 
				 * // raiseList.get(i).setInit(INIT_TOTAL);
				 * 
				 * Order order = new Order();
				 * order.setOutTradeNo(mainAccounts.getGid().toString()); Order
				 * mainOrder = cashService.getOrderByOutTradeNo(order); if(null
				 * != mainOrder) {
				 * raiseList.get(i).getRaiseInfo().setFee(mainOrder
				 * .getFeeTotal());
				 * raiseList.get(i).getRaiseInfo().setBonus(mainOrder
				 * .getFeeLeft());
				 * raiseList.get(i).getRaiseInfo().setNum(Integer
				 * .parseInt(mainOrder.getDetail()));
				 * raiseList.get(i).getRaiseInfo
				 * ().setProfit(Float.parseFloat(mainOrder.getBody()));
				 * raiseList
				 * .get(i).getRaiseInfo().setFeeOrigin(mainOrder.getFeeOrigin
				 * ()); order.setOrderId(mainOrder.getId()); //List<Order>
				 * orderList = cashService.getFollowListOrder(order); Integer
				 * orderListSize = cashService.getFollowListOrderSize(order);
				 * raiseList.get(i).getRaiseInfo().setJoinNum(orderListSize);
				 * raiseList.get(i).getRaiseInfo().setPay(0);
				 * order.setUserId(userid); Order userOrder =
				 * cashService.hasGetOrder(order); if(null != userOrder) {
				 * raiseList.get(i).getRaiseInfo().setPay(1); } }
				 */
			}
			map = rspFormat(raiseList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}

		return map;
	}

	@RequestMapping(value = "/querygname", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryGname(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String gname = request.getParameter("gname");
		Integer userid = 0;
		if (null != request.getParameter("userid")) {
			userid = Integer.parseInt(request.getParameter("userid"));
		}
		Accounts accounts = new Accounts();
		accounts.setGname(gname);
		List<Accounts> listAccounts = groupService.getAccountsList(accounts);
		if (!listAccounts.isEmpty()) {
			map = rspFormat("", HAVA_SAME_PROTFOLIO);
			return map;
		} else {
			accounts.setUserid(userid);
			List<Accounts> listExistAccounts = groupService
					.getAccountsList(accounts);
			if (!listExistAccounts.isEmpty() && listExistAccounts.size() >= 10) {
				map = rspFormat("", HAVE_ENOUGH_PROTFOLIO);
				return map;
			} else {
				map = rspFormat("", SUCCESS);
			}
		}
		return map;
	}

	@RequestMapping(value = "/createraisenocharge", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> createRaiseNoCharge(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		float fee = Float.parseFloat(request.getParameter("fee"));
		float bonus = Float.parseFloat(request.getParameter("bonus"));
		float profit = Float.parseFloat(request.getParameter("profit"));
		Integer stop = 0;
		Integer num = Integer.parseInt(request.getParameter("num"));
		Integer floor = 0;
		if (null != request.getParameter("floor")) {
			floor = Integer.parseInt(request.getParameter("floor"));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String gname = request.getParameter("gname");
		String info = request.getParameter("info");
		String token = request.getParameter("token");
		if (validateToken(userid, token)) {
			Accounts accounts = new Accounts();
			accounts.setGname(gname);
			List<Accounts> listAccounts = groupService
					.getAccountsList(accounts);
			if (!listAccounts.isEmpty()) {
				map = rspFormat("", HAVA_SAME_PROTFOLIO);
				return map;
			} else {
				accounts.setUserid(userid);
				List<Accounts> listExistAccounts = groupService
						.getAccountsList(accounts);
				if (!listExistAccounts.isEmpty()
						&& listExistAccounts.size() >= 10) {
					map = rspFormat("", HAVE_ENOUGH_PROTFOLIO);
					return map;
				} else {
					try {
						Date raisetime = sdf.parse(request
								.getParameter("raisetime"));
						Date endtime = sdf.parse(request
								.getParameter("endtime"));
						Date createtime = new Date();
						Integer c2r = Time.daysBetween(createtime, raisetime);
						Integer r2e = Time.daysBetween(raisetime, endtime);
						Integer n2r = Time.compareTime(createtime, raisetime);
						Calendar c = Calendar.getInstance();
						c.setTime(raisetime);
						c.add(Calendar.HOUR, 9);
						raisetime = c.getTime();
						c.setTime(endtime);
						c.add(Calendar.HOUR, 16);
						endtime = c.getTime();
						if (!(n2r < 0 && c2r <= 30 && r2e >= 1 && r2e <= 90)) {
							map = rspFormat("", WRONG_PARAM);
							return map;
						} else {
							if (profit >= 3 && profit <= 50) {
								if (bonus > 0) {
									accounts.setAttr(8);
								} else {
									accounts.setAttr(7);
								}

							} else {
								map = rspFormat("", WRONG_PARAM);
								return map;
							}
							if (null != request.getParameter("stop")) {
								stop = Integer.parseInt(request
										.getParameter("stop"));
							}
							accounts.setInfo(info);
							accounts.setCreatetime(createtime);
							accounts.setRaisetime(raisetime);
							accounts.setEndtime(endtime);
							accounts.setOptigid(stop);
							double giveTotal = INIT_TOTAL;
							accounts.setAvailable(giveTotal);
							accounts.setTotal(giveTotal);
							accounts.setGtnum(0);
							accounts.setGznum(0);
							accounts.setDel(0);
							Integer hasInsert = groupService
									.initAccounts(accounts);
							if (hasInsert > 0) {
								Integer gid = accounts.getGid();
								Stat stat = new Stat();
								stat.setGid(gid);
								stat.setR7(0);
								stat.setR30(0);
								stat.setR6m(0);
								stat.setR1y(0);
								groupService.initStat(stat);
								map = rspFormat(accounts, SUCCESS);

								String transactionId = createtime.getTime()
										+ "";
								Integer tradeType = 16;
								Integer status = 5;

								Order useOrder = new Order();
								useOrder.setTransactionId(transactionId);
								useOrder.setOutTradeNo(accounts.getGid()
										.toString());
								useOrder.setUserId(userid);
								useOrder.setOrderId(0);
								useOrder.setTimeStart(createtime);
								useOrder.setTimeExpire(endtime);
								useOrder.setTradeType(tradeType);
								useOrder.setFeeTotal(fee);
								useOrder.setFeeLeft(bonus);
								useOrder.setBody(profit + "");
								useOrder.setDetail(num.toString());
								useOrder.setExpand(floor.toString());
								useOrder.setStatus(status);
								cashService.initOrder(useOrder);
							} else {
								map = rspFormat("", WRONG_MYSQL_OPERATION);
								return map;
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						map = rspFormat("", WRONG_PARAM_FORMAT);
						return map;
					}
				}
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
			return map;
		}
		return map;
	}

	@RequestMapping(value = "/payraise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> payRaise(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();

		Integer userid = Integer.parseInt(request.getParameter("userid"));
		float bonus = Float.parseFloat(request.getParameter("bonus"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		String password = request.getParameter("password");
		CashAccount cashAccount = cashService.getCashAccountByUserId(userid);
		Date createtime = new Date();
		Accounts accounts = new Accounts();
		accounts.setGid(gid);
		Accounts mainAccounts = groupService.getAccounts(accounts);
		if (null != password) {
			if (cashAccount.getPassword().equals(MD5Util.string2MD5(password))) {
				if (cashAccount.getTotalCash() - bonus >= 0) {
					cashAccount
							.setTotalCash(cashAccount.getTotalCash() - bonus);
					cashAccount.setTimeTill(createtime);
					cashService.updateCashAccount(cashAccount);
				} else {
					map = rspFormat("", NO_ENOUGH_MONEY);
					return map;
				}
			} else {
				map = rspFormat("", WRONG_PASSWORD);
				return map;
			}
		} else {
			if (cashAccount.getTotalCash() - bonus >= 0) {
				cashAccount.setTotalCash(cashAccount.getTotalCash() - bonus);
				cashAccount.setTimeTill(createtime);
				cashService.updateCashAccount(cashAccount);
			} else {
				map = rspFormat("", NO_ENOUGH_MONEY);
				return map;
			}
		}
		mainAccounts.setAttr(7);
		groupService.updateAccounts(mainAccounts);
		map = rspFormat("", SUCCESS);
		return map;
	}

	@RequestMapping(value = "/getpayuser", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getpayRaise(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Integer start = 0;
		Integer limit = PAGE_SIZE;
		if (null != request.getParameter("start")) {
			start = Integer.parseInt(request.getParameter("start"));
		}
		Order on = new Order();
		on.setOutTradeNo(gid.toString());
		Order mainOrder = cashService.getOrderByOutTradeNo(on);
		List<User> userList = new ArrayList<User>();
		mainOrder.setOrderId(mainOrder.getId());
		Order bonusOrder = cashService.getBonusOrder(mainOrder);
		List<Order> orderList = cashService.getFollowListOrderLimit(
				mainOrder.getId(), start, limit);
		if (!orderList.isEmpty()) {
			for (Order order : orderList) {
				if (null != bonusOrder) {
					bonusOrder.setOrderId(bonusOrder.getId());
					bonusOrder.setUserId(order.getUserId());
					Order hasOrder = cashService.hasGetBonusOrder(bonusOrder);
					order.getUser().setUserTitle("");
					if (null != hasOrder) {
						order.getUser().setUserTitle(
								hasOrder.getFeeTotal() + "");
					}
				}
				userList.add(order.getUser());
			}
			map = rspFormat(userList, SUCCESS);
		} else {
			map = rspFormat(userList, SUCCESS);
		}

		return map;

		/*
		 * Integer gid = Integer.parseInt(request.getParameter("gid")); Order on
		 * = new Order(); on.setOutTradeNo(gid.toString()); Order mainOrder =
		 * cashService.getOrderByOutTradeNo(on);
		 * mainOrder.setOrderId(mainOrder.getId()); List<Order> orderList =
		 * cashService.getFollowListOrder(mainOrder); Order bonusOrder =
		 * cashService.getBonusOrder(mainOrder); List<User> userList = new
		 * ArrayList<User>(); List<Order> bonusOrderList = new ArrayList<>();
		 * if(null != bonusOrder) { bonusOrder.setOrderId(bonusOrder.getId());
		 * bonusOrderList = cashService.getBonusListOrder(bonusOrder);
		 * 
		 * } // Order o = new Order(); // o.setDetail(gid.toString()); // Order
		 * order = cashService.getZuheOrderByDetail(o); for(int i = 0; i <
		 * orderList.size(); i++) { Integer userId =
		 * orderList.get(i).getUserId(); User user =
		 * userService.selectUserById(userId); user.setMember(0);
		 * user.setUserTitle("");; // if(null != order) { //
		 * if(order.getUserId().equals(userId)) { // user.setMember(1); // } //
		 * } if(!bonusOrderList.isEmpty()) { for(int j =0; j <
		 * bonusOrderList.size(); j++) {
		 * if(bonusOrderList.get(j).getUserId().equals(userId)) {
		 * user.setUserTitle((double)bonusOrderList.get(j).getFeeTotal() + "");
		 * } } } userList.add(user); } if(null != request.getParameter("start"))
		 * { Integer start = Integer.parseInt(request.getParameter("start"));
		 * if(start >= userList.size()) { map = rspFormat("", SUCCESS); return
		 * map; } Integer end = start + PAGE_SIZE <= userList.size()?start +
		 * PAGE_SIZE:userList.size(); List<User> list = new ArrayList<User>();
		 * for(int i= start; i < end; i++) { list.add(userList.get(i)); } map =
		 * rspFormat(list, SUCCESS); return map; } map = rspFormat(userList,
		 * SUCCESS); return map;
		 */
	}

	@RequestMapping(value = "/createNew", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> createNew(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		// System.out.println(request.getParameter("jsonData"));
		JSONObject jsonObject = new JSONObject(request.getParameter("jsonData"));
		JSONObject dataObject = jsonObject.getJSONObject("data");
		JSONArray snapshot = dataObject.getJSONArray("snapshot");
		JSONArray accountsArray = dataObject.getJSONArray("accounts");
		String gname = accountsArray.getString(0);
		double feeLeft = accountsArray.getDouble(1);
		Integer userid = accountsArray.getInt(2);
		String token = accountsArray.getString(3);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();

		// Integer userid = Integer.parseInt(request.getParameter("userid"));
		// String gname = request.getParameter("gname");
		// String token = request.getParameter("token");
		if (validateToken(userid, token)) {

			Accounts accounts = new Accounts();
			accounts.setUserid(userid);
			accounts.setAttr(14);
			Accounts acc14 = groupService.get15Accounts(accounts);

			if (acc14 == null) {
				// 还未创建过类型为14的组合

				double giveTotal = 0;
				giveTotal += feeLeft;
				Integer size = snapshot.length();
				if (size != 0) {
					for (int i = 0; i < size; i++) {
						Integer vol = snapshot.getJSONArray(i).getInt(1);
						double price = snapshot.getJSONArray(i).getDouble(2);
						giveTotal += vol * price * 100;
					}
				}

				Date createtime = new Date();
				accounts.setGname(gname);
				accounts.setCreatetime(createtime);
				accounts.setOptigid(0);
				accounts.setInit(giveTotal);
				accounts.setAvailable(feeLeft);
				accounts.setTotal(giveTotal);
				accounts.setGtnum(0);
				accounts.setGznum(0);
				accounts.setDel(0);
				Integer hasInsert = groupService.initAccounts(accounts);
				if (hasInsert > 0) {
					Integer gid = accounts.getGid();
					if (size != 0) {
						for (int i = 0; i < size; i++) {
							String stockid = snapshot.getJSONArray(i)
									.getString(0);
							Integer vol = snapshot.getJSONArray(i).getInt(1);
							double price = snapshot.getJSONArray(i)
									.getDouble(2);
							String date = snapshot.getJSONArray(i).getString(3);
							try {
								Date buyDate = sdf.parse(date);
								c.setTime(buyDate);
								c.add(Calendar.HOUR, 9);
								buyDate = c.getTime();
								double fee = price * vol * 100 * 0.0003;
								Traderec traderec = new Traderec();
								traderec.setGid(gid);
								traderec.setStockid(stockid);
								traderec.setVol(vol);
								traderec.setAction(1);
								traderec.setStatus(3);
								traderec.setCharge(fee);
								traderec.setOrderprice(price);
								traderec.setTradeprice(price);
								traderec.setInserttime(buyDate);
								traderec.setTradetime(buyDate);
								groupService.initTraderec(traderec);

								Posi posi = new Posi();
								posi.setGid(gid);
								posi.setStockid(stockid);
								posi.setVol(vol);
								posi.setPrice(price);
								posi.setFrozen(0);
								posi.setAvailable(vol);
								posi.setOnway(0);
								groupService.initPosi(posi);

							} catch (ParseException e) {
								e.printStackTrace();
								continue;
							}
						}
					}

					Stat stat = new Stat();
					stat.setGid(gid);
					stat.setR7(0);
					stat.setR30(0);
					stat.setR6m(0);
					stat.setR1y(0);
					groupService.initStat(stat);

					Accounts optimizeAccounts = new Accounts();
					optimizeAccounts.setCreatetime(createtime);

					optimizeAccounts.setAttr(15);
					optimizeAccounts.setOptigid(0);
					optimizeAccounts.setInit(giveTotal);
					optimizeAccounts.setAvailable(feeLeft);
					optimizeAccounts.setTotal(giveTotal);
					optimizeAccounts.setGtnum(0);
					optimizeAccounts.setGznum(0);
					optimizeAccounts.setDel(0);
					optimizeAccounts.setUserid(userid);
					optimizeAccounts.setGname(gname + "的优化组合");
					Integer hasInsert2 = groupService
							.initAccounts(optimizeAccounts);
					if (hasInsert2 > 0) {
						Integer opiGid = optimizeAccounts.getGid();
						if (size != 0) {
							for (int i = 0; i < size; i++) {
								String stockid = snapshot.getJSONArray(i)
										.getString(0);
								Integer vol = snapshot.getJSONArray(i)
										.getInt(1);
								double price = snapshot.getJSONArray(i)
										.getDouble(2);
								String date = snapshot.getJSONArray(i)
										.getString(3);
								try {
									Date buyDate = sdf.parse(date);
									c.setTime(buyDate);
									c.add(Calendar.HOUR, 9);
									buyDate = c.getTime();
									double fee = price * vol * 100 * 0.0003;
									Traderec traderec = new Traderec();
									traderec.setGid(opiGid);
									traderec.setStockid(stockid);
									traderec.setVol(vol);
									traderec.setAction(1);
									traderec.setStatus(3);
									traderec.setCharge(fee);
									traderec.setOrderprice(price);
									traderec.setTradeprice(price);
									traderec.setInserttime(buyDate);
									traderec.setTradetime(buyDate);
									groupService.initTraderec(traderec);

									Posi posi = new Posi();
									posi.setGid(opiGid);
									posi.setStockid(stockid);
									posi.setVol(vol);
									posi.setPrice(price);
									posi.setFrozen(0);
									posi.setAvailable(vol);
									posi.setOnway(0);
									groupService.initPosi(posi);

								} catch (ParseException e) {
									e.printStackTrace();
									continue;
								}
							}
						}

						Stat opiStat = new Stat();
						opiStat.setGid(opiGid);
						opiStat.setR7(0);
						opiStat.setR30(0);
						opiStat.setR6m(0);
						opiStat.setR1y(0);
						groupService.initStat(opiStat);

						accounts.setOptigid(opiGid);
						groupService.updateAccounts(accounts);
					}

					map = rspFormat(optimizeAccounts, SUCCESS);
				} else {
					map = rspFormat("", WRONG_MYSQL_OPERATION);
				}
			} else {
				// 已经创建过类型为14的组合
				map = rspFormat("", HAVEN_14ACCOUNTS);
			}
			// }
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/createNewOpti", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> createNewOpti(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userid"));
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		Calendar c = Calendar.getInstance();
		String token = request.getParameter("token");
		if (validateToken(userId, token)) {
			Accounts accounts = new Accounts();
			accounts.setGid(gid);
			accounts.setUserid(userId);
			List<Accounts> optiList = groupService
					.getOptiAccountsList(accounts);
			if (!optiList.isEmpty()) {
				map = rspFormat("", HAVE_PORTFOLIO);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Accounts mainAccounts = groupService.getAccounts(accounts);
				Accounts optiAccounts = new Accounts();
				Date createtime = new Date();
				optiAccounts.setGname(mainAccounts.getGname() + "的优化组合");
				optiAccounts.setAvailable(mainAccounts.getAvailable()
						+ mainAccounts.getFrozen());
				optiAccounts.setStock(mainAccounts.getStock());
				optiAccounts.setTotal(mainAccounts.getTotal());
				optiAccounts.setInit(mainAccounts.getInit());
				optiAccounts.setUserid(userId);
				optiAccounts.setGznum(0);
				optiAccounts.setGtnum(0);
				optiAccounts.setDel(0);
				optiAccounts.setAttr(5);
				optiAccounts.setOptigid(mainAccounts.getGid());
				optiAccounts.setCreatetime(createtime);
				Integer hasInsert = groupService.initAccounts(optiAccounts);
				Integer optiId = optiAccounts.getGid();
				if (hasInsert > 0 && optiId != gid) {
					mainAccounts.setAttr(6);
					mainAccounts.setOptigid(optiId);
					groupService.updateAccounts(mainAccounts);

					Traderec traderec = new Traderec();
					traderec.setGid(gid);
					List<Traderec> traderecList = groupService
							.getTraderecList(traderec);
					if (!traderecList.isEmpty()) {
						for (Traderec t : traderecList) {
							t.setGid(optiId);
							groupService.initTraderec(t);
						}
					}

					Posi posi = new Posi();
					posi.setGid(gid);
					List<Posi> posiList = groupService.getPosi(posi);
					if (!posiList.isEmpty()) {
						for (int i = 0; i < posiList.size(); i++) {
							Posi posiItem = posiList.get(i);
							String stockid = posiItem.getStockid();
							Traderec traderecf = new Traderec();
							traderecf.setGid(gid);
							traderecf.setStockid(stockid);
							Traderec t = groupService
									.getFirstTraderec(traderecf);
							if (null == t) {
								continue;
							}
							Integer vol = t.getVol();
							Integer volInit = 0 + vol;
							double stockprice = 0;
							double lastPrice = 0;
							Date buyDate = t.getInserttime();
							try {
								String stock = stockid.startsWith("6") ? stockid
										+ ".SS"
										: stockid + ".SZ";
								String result = GetHSTokenUtils
										.getBuySallPricePoint(stock);
								JSONObject jsonObject = new JSONObject(result);
								JSONObject candle = jsonObject.getJSONObject(
										"data").getJSONObject("candle");
								int len = candle.getJSONArray(stock).length();
								for (int j = 0; j < len; j++) {
									JSONArray stockArray = candle.getJSONArray(
											stock).getJSONArray(j);
									Integer time = stockArray.getInt(0);
									int buyorsall = stockArray.getInt(1); // 1是买点，
																			// 2是卖点
									double price = stockArray.getDouble(2);
									lastPrice = price;
									Date date;
									date = sdf.parse(time.toString());

									if (Time.compareTime(date, buyDate) < 0) {
										continue;
									} else {
										c.setTime(date);
										c.add(Calendar.HOUR, 9);
										date = c.getTime();
										if (buyorsall == 1) {
											if (volInit >= vol) {
												continue;
											} else {
												volInit += vol;
												stockprice -= price * 100 * vol;
												double fee = price * 100 * vol
														* 0.0003;
												stockprice -= fee;
												Traderec ht = new Traderec();
												ht.setGid(optiId);
												ht.setStockid(stockid);
												ht.setOrderprice(price);
												ht.setTradeprice(price);
												ht.setCharge(fee);
												ht.setVol(vol);
												ht.setAction(1);
												ht.setStatus(3);
												ht.setInserttime(date);
												ht.setTradetime(date);
												groupService.initTraderec(ht);

											}
										} else if (buyorsall == 2) {
											if (volInit <= 0) {
												continue;
											} else {
												volInit -= vol;
												stockprice += price * 100 * vol;
												double fee = price * 100 * vol
														* 0.0003;
												double stamp = price * 100
														* vol * 0.001;
												stockprice = stockprice - fee
														- stamp;
												Traderec ht = new Traderec();
												ht.setGid(optiId);
												ht.setStockid(stockid);
												ht.setOrderprice(price);
												ht.setTradeprice(price);
												ht.setCharge(fee);
												ht.setStamp_tax(stamp);
												ht.setVol(vol);
												ht.setAction(3);
												ht.setStatus(3);
												ht.setInserttime(date);
												ht.setTradetime(date);
												groupService.initTraderec(ht);

											}
										}
									}

								}
							} catch (Exception e) {

							}
							if (volInit > 0) {
								posiItem.setGid(optiId);
								posiItem.setAvailable(volInit);
								posiItem.setFrozen(0);
								posiItem.setPrice(lastPrice);
								groupService.initPosi(posiItem);
							}
							double available = optiAccounts.getAvailable()
									+ stockprice;
							optiAccounts.setAvailable(available);
						}
						groupService.updateAccounts(optiAccounts);
					}

					Stat stat = new Stat();
					stat.setGid(optiId);
					stat.setR7(0);
					stat.setR30(0);
					stat.setR6m(0);
					stat.setR1y(0);
					groupService.initStat(stat);
					map = rspFormat(optiAccounts, SUCCESS);
				} else {
					map = rspFormat("", WRONG_MYSQL_OPERATION);
				}

			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}

		return map;
	}

	@RequestMapping(value = "/createAdvance", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> createHigh(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		// JSONObject jsonObject = new
		// JSONObject(request.getParameter("jsonData"));
		// JSONObject dataObject = jsonObject.getJSONObject("data");
		// JSONArray snapshot = dataObject.getJSONArray("snapshot");
		// JSONArray accountsArray = dataObject.getJSONArray("accounts");
		String stockStr = request.getParameter("stockArray");
		String[] stockArray = stockStr.split(",");
		// String gname = request.getParameter("gname");
		int userid = Integer.parseInt(request.getParameter("userid"));
		int type = 0;
		if (null != request.getParameter("type")) {
			type = Integer.parseInt(request.getParameter("type"));
		}
		String token = request.getParameter("token");
		if (validateToken(userid, token)) {
			List<Accounts> list = groupService.getAllAdvanceAccounts();
			Accounts accounts = new Accounts();
			accounts.setGname("股哥智能配置" + list.size() + "号股票组合");
			List<Accounts> listAccounts = groupService
					.getAccountsList(accounts);
			if (!listAccounts.isEmpty()) {
				map = rspFormat("", HAVA_SAME_PROTFOLIO);
			} else {
				accounts.setUserid(userid);
				List<Accounts> listExistAccounts = groupService
						.getAccountsList(accounts);
				if (false) {
					map = rspFormat("", HAVE_ENOUGH_PROTFOLIO);
				} else {
					// Integer size = snapshot.length();
					Integer size = stockArray.length;
					Date createtime = new Date();
					accounts.setCreatetime(createtime);
					accounts.setAttr(9);
					accounts.setOptigid(0);
					accounts.setInit(INIT_TOTAL);
					accounts.setAvailable(INIT_TOTAL);
					accounts.setTotal(INIT_TOTAL);
					accounts.setGtnum(0);
					accounts.setGznum(0);
					accounts.setDel(0);
					Integer hasInsert = groupService.initAccounts(accounts);
					if (hasInsert > 0) {
						Integer gid = accounts.getGid();
						if (size != 0) {
							for (int i = 0; i < size; i++) {
								String stockid = stockArray[i];
								if (null != stockid && stockid != "") {
									Stockinfo stockinfo = new Stockinfo();
									stockinfo.setStockid(stockid);
									Stockinfo info = groupService
											.getStockinfo(stockinfo);

									Advance advance = new Advance();
									advance.setGid(gid);
									advance.setUserid(userid);
									advance.setStockId(stockid);
									if (null != info) {
										advance.setStockName(info.getName());
									}
									advance.setType(0);
									advance.setStatus(0);
									groupService.insertAdvance(advance);
								}
							}
						}
						if (type == 1) {
							fillAdvance(userid, gid);
						}
						Stat stat = new Stat();
						stat.setGid(gid);
						stat.setR7(0);
						stat.setR30(0);
						stat.setR6m(0);
						stat.setR1y(0);
						groupService.initStat(stat);
						map = rspFormat(accounts, SUCCESS);
					} else {
						map = rspFormat("", WRONG_MYSQL_OPERATION);
					}
				}
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	private void fillAdvance(int user_id, int gid) {
		List<Advance> advanceList = groupService.getAccountsAdvance(gid);
		if (advanceList.size() < POOL_SIZE) {
			Integer left = POOL_SIZE - advanceList.size();
			User user = userService.selectUserById(user_id);
			UserMeta userMeta = new UserMeta();
			userMeta.setUserId(user_id);
			UserMeta meta = groupService.getUserMeta(userMeta);
			List<Pool> poolList = new ArrayList<>();
			List<Pool> worthPool = new ArrayList<>();
			List<Pool> growPool = new ArrayList<>();
			List<Pool> themePool = new ArrayList<>();
			switch (user.getTzType()) {
			case "稳健型":
				if (null == meta) {
					worthPool = getPool(0, 0, 8);
					growPool = getPool(1, 0, 2);
					poolList.addAll(worthPool);
					poolList.addAll(growPool);
				} else {
					if (meta.getType() == 0) {
						switch (meta.getGtype()) {
						case 0:
							worthPool = getPool(0, 0, 10);
							poolList.addAll(worthPool);
							break;
						case 1:
							growPool = getPool(1, 0, 10);
							poolList.addAll(growPool);
							break;
						case 2:
							themePool = getPool(2, 0, 10);
							poolList.addAll(themePool);
							break;
						case 3:
							worthPool = getPool(0, 0, 4);
							growPool = getPool(1, 0, 3);
							themePool = getPool(2, 0, 3);
							poolList.addAll(worthPool);
							poolList.addAll(growPool);
							poolList.addAll(themePool);
							break;
						case 4:
							break;
						}
					}
				}
				break;
			case "增长型":
				if (null == meta) {
					worthPool = getPool(0, 0, 6);
					growPool = getPool(1, 0, 2);
					themePool = getPool(2, 0, 2);
					poolList.addAll(worthPool);
					poolList.addAll(growPool);
					poolList.addAll(themePool);
				} else {
					if (meta.getType() == 0) {
						switch (meta.getGtype()) {
						case 0:
							worthPool = getPool(0, 0, 10);
							poolList.addAll(worthPool);
							break;
						case 1:
							growPool = getPool(1, 0, 10);
							poolList.addAll(growPool);
							break;
						case 2:
							themePool = getPool(2, 0, 10);
							poolList.addAll(themePool);
							break;
						case 3:
							worthPool = getPool(0, 0, 4);
							growPool = getPool(1, 0, 3);
							themePool = getPool(2, 0, 3);
							poolList.addAll(worthPool);
							poolList.addAll(growPool);
							poolList.addAll(themePool);
							break;
						case 4:
							break;
						}
					}
				}
				break;
			case "进取型":
				if (null == meta) {
					growPool = getPool(1, 0, 5);
					themePool = getPool(2, 0, 5);
					poolList.addAll(growPool);
					poolList.addAll(growPool);
				} else {
					if (meta.getType() == 0) {
						switch (meta.getGtype()) {
						case 0:
							worthPool = getPool(0, 0, 10);
							poolList.addAll(worthPool);
							break;
						case 1:
							growPool = getPool(1, 0, 10);
							poolList.addAll(growPool);
							break;
						case 2:
							themePool = getPool(2, 0, 10);
							poolList.addAll(themePool);
							break;
						case 3:
							worthPool = getPool(0, 0, 4);
							growPool = getPool(1, 0, 3);
							themePool = getPool(2, 0, 3);
							poolList.addAll(worthPool);
							poolList.addAll(growPool);
							poolList.addAll(themePool);
							break;
						case 4:
							worthPool = getPool(0, 0, 4);
							growPool = getPool(1, 0, 3);
							themePool = getPool(2, 0, 3);
							poolList.addAll(worthPool);
							poolList.addAll(growPool);
							poolList.addAll(themePool);
							break;
						}
					}
				}
				break;
			default:
				break;
			}
			for (Pool pool : poolList) {
				Stockinfo stockinfo = new Stockinfo();
				stockinfo.setStockid(pool.getStockId());
				Stockinfo info = groupService.getStockinfo(stockinfo);

				Advance advance = new Advance();
				advance.setGid(gid);
				advance.setUserid(user_id);
				advance.setStockId(pool.getStockId());
				if (null != info) {
					advance.setStockName(info.getName());
				}
				advance.setType(1);
				advance.setStatus(0);
				groupService.insertAdvance(advance);
			}
		}
	}

	private List<Pool> getPool(int type, int property, int size) {
		List<Pool> poolList = new ArrayList<>();
		if (property == 0) {
			poolList = groupService.getTypePool(type, size);
		} else {

		}
		return poolList;
	}

	@RequestMapping(value = "/queryAdvanceAccounts", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryAdvanceAccounts(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		Accounts accounts = new Accounts();
		accounts.setUserid(userid);
		List<Accounts> accountsList = groupService
				.getUserAdvanceAccounts(accounts);
		if (!accountsList.isEmpty()) {
			map = rspFormat(accountsList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}

		return map;
	}

	@RequestMapping(value = "/queryAdvance", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> queryAdvance(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		String token = request.getParameter("token");
		Integer gid = Integer.parseInt(request.getParameter("gid"));
		List<Advance> advanceList = groupService.getAccountsAdvance(gid);
		if (!advanceList.isEmpty()) {
			map = rspFormat(advanceList, SUCCESS);
		} else {
			map = rspFormat("", SUCCESS);
		}

		return map;
	}

	@RequestMapping(value = "/createUserMeta", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> createUserMeta(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		Integer type = Integer.parseInt(request.getParameter("type"));
		Integer gtype = Integer.parseInt(request.getParameter("gtype"));
		String paramToken = request.getParameter("token");
		if (validateToken(user_id, paramToken)) {
			UserMeta userMeta = new UserMeta();
			userMeta.setUserId(user_id);
			userMeta.setType(type);
			userMeta.setGtype(gtype);
			UserMeta meta = groupService.getUserMeta(userMeta);
			if (null != meta) {
				meta.setType(type);
				meta.setGtype(gtype);
				groupService.updateUserMeta(meta);
				map = rspFormat(meta, SUCCESS);
			} else {
				groupService.initUserMeta(userMeta);
				map = rspFormat(userMeta, SUCCESS);
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/getUserMeta", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getWebAccount(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		String paramToken = request.getParameter("token");
		if (validateToken(user_id, paramToken)) {
			UserMeta userMeta = new UserMeta();
			userMeta.setUserId(user_id);
			UserMeta meta = groupService.getUserMeta(userMeta);
			if (null != meta) {
				map = rspFormat(meta, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/updateUserMeta", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateUserMeta(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		Integer type = Integer.parseInt(request.getParameter("type"));
		Integer gtype = Integer.parseInt(request.getParameter("gtype"));
		String paramToken = request.getParameter("token");
		if (validateToken(user_id, paramToken)) {
			UserMeta userMeta = new UserMeta();
			userMeta.setUserId(user_id);
			UserMeta meta = groupService.getUserMeta(userMeta);
			if (null != meta) {
				meta.setType(type);
				meta.setGtype(gtype);
				groupService.updateUserMeta(meta);
				map = rspFormat(meta, SUCCESS);
			} else {
				map = rspFormat("", SUCCESS);
			}
		} else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}

	@RequestMapping(value = "/getPosi", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getPosi(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String stockid = request.getParameter("stockid");
		int page = Integer.parseInt(request.getParameter("page"));
		int pageSize = 10;
		Posi posi = new Posi();
		posi.setStockid(stockid);
		List<Posi> posiList = groupService.getAllStockidPosi(posi);
		if (!posiList.isEmpty()) {
			int size = (int) Math.ceil((double) posiList.size() / pageSize);
			List<Posi> list = new ArrayList<>();
			for (int i = (page - 1) * 10; i < page * 10; i++) {
				if (i == posiList.size()) {
					break;
				} else {
					list.add(posiList.get(i));
				}
			}
			map.put("list", list);
			map.put("curPage", page);
			map.put("size", size);
		} else {
			map.put("list", "");
			map.put("curPage", page);
			map.put("size", 0);
		}
		return map;
	}

	@RequestMapping(value = "/exrights", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> exrights(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String stockid = request.getParameter("stockid");
		double allotted_ps = Double.parseDouble(request
				.getParameter("allotted_ps"));
		double rationed_ps = Double.parseDouble(request
				.getParameter("rationed_ps"));
		double rationed_px = Double.parseDouble(request
				.getParameter("rationed_px"));
		double bonus_ps = Double.parseDouble(request.getParameter("bonus_ps"));
		Posi posi = new Posi();
		posi.setStockid(stockid);
		// String stockStr = stockid.startsWith("6")?stockid + ".SS":stockid +
		// ".SZ";
		List<Posi> posiList = groupService.getAllStockidPosi(posi);
		if (!posiList.isEmpty()) {
			// String result = GetHSTokenUtils.getReal(stockStr, "");
			// JSONObject jsonObject = new JSONObject(result);
			// JSONObject snapshot =
			// jsonObject.getJSONObject("data").getJSONObject("snapshot");
			// double preclose = snapshot.getJSONArray(stockStr).getDouble(3);
			double newScale = 1 + allotted_ps + rationed_ps;
			// double newPrice = (preclose - bonus_ps + rationed_ps *
			// rationed_px) / newScale;
			for (Posi eposi : posiList) {
				if (bonus_ps > 0) {
					double bonus = eposi.getVol() * 100 * bonus_ps;
					Accounts a = new Accounts();
					a.setGid(eposi.getGid());
					Accounts accounts = groupService.getAccounts(a);
					accounts.setAvailable(accounts.getAvailable() + bonus);
					groupService.updateAccounts(accounts);
				}
				double newPrice = (eposi.getPrice() - bonus_ps + rationed_ps
						* rationed_px)
						/ newScale;
				Integer amount = (int) (newScale * eposi.getVol());
				eposi.setVol(amount);
				eposi.setAvailable(amount);
				eposi.setPrice(newPrice);
				groupService.updatePosi(eposi);
				map = rspFormat("", SUCCESS);
			}
		} else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}

	@RequestMapping(value = "/recommend", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getTJAccounts(HttpServletRequest request,
			HttpServletResponse response) {

		response.addHeader("Access-Control-Allow-origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET,POST");

		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> list = accountsService2.getTJAccounts();
		if (list.size() > 0) {
			map = rspFormat(list, SUCCESS);
		} else {
			map = rspFormat("", FAIL);
		}
		return map;
	}

	@RequestMapping(value = "/addmorerela", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertBatchRelation() {
		boolean isok = groupService.insertBatchRelation();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msg", isok);
		return map;
	}
	
	@RequestMapping(value = "/analysis", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> analysis(Double main_rate,Double optimize_rate,Double market_rate,HttpServletResponse response){
		//分析比较,裁判评语
		response.addHeader("Access-Control-Allow-origin", "*");
		Map<String, Object> map = new HashMap<String, Object>();
		String resultLabeltext="";
        if (main_rate < optimize_rate && optimize_rate < market_rate) {
            if (market_rate < 0) {
                // 1-3
                resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较差，选股能力较差，优化后还落后于大盘。";
            	map.put("msg", resultLabeltext);
            	map.put("data", "");
            	map.put("status", 0);
            }else{
                if (optimize_rate < 0) {
                    // 1-1
                    resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较差，选股能力较差。";
                    map.put("msg", resultLabeltext);
                	map.put("data", "");
                	map.put("status", 0);
                   
                }else{
                    // 1-2
                    resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力一般，选股能力较差。";
                    map.put("msg", resultLabeltext);
                	map.put("data", "");
                	map.put("status", 0);
                }
            }
        }else if (main_rate > optimize_rate && optimize_rate >= market_rate){
            if (main_rate < 0) {
                //  2-1
                resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较好，选股能力较好，但有些激进。";
                map.put("msg", resultLabeltext);
            	map.put("data", "");
            	map.put("status", 0);
            }else{
                // 2-2
                resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较好，选股能力较好！";
                map.put("msg", resultLabeltext);
            	map.put("data", "");
            	map.put("status", 0);
            }
        }else if (main_rate == optimize_rate && optimize_rate > market_rate){
            // 4-1
            resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较好，选股能力较好。";
            map.put("msg", resultLabeltext);
        	map.put("data", "");
        	map.put("status", 0);

        }else if (main_rate == optimize_rate && optimize_rate < market_rate){
            // 5-1
            resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较好，选股能力一般。";
            map.put("msg", resultLabeltext);
        	map.put("data", "");
        	map.put("status", 0);

        }else if (market_rate > main_rate && main_rate > optimize_rate){
            // 3-1
            resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较好，选股能力较好，但有些激进";
            map.put("msg", resultLabeltext);
        	map.put("data", "");
        	map.put("status", 0);

        }else if (optimize_rate > market_rate && main_rate < market_rate){
            // 6-1
            resultLabeltext ="操盘分析： 您近期择时和资金仓位管理能力较差，选股能力较差";
            map.put("msg", resultLabeltext);
        	map.put("data", "");
        	map.put("status", 0);
            
        }else if (main_rate > market_rate && main_rate >= optimize_rate){
            // 未知
            resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力较好，选股能力较好";
            map.put("msg", resultLabeltext);
        	map.put("data", "");
        	map.put("status", 0);

        }else{
            // 7-1
            resultLabeltext = "操盘分析： 您近期择时和资金仓位管理能力一般，选股能力一般。";
            map.put("msg", resultLabeltext);
        	map.put("data", "");
        	map.put("status", 0);
        }

		return map;
		
	}
	
	
	@RequestMapping(value = "/checktoken",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> checktoken(HttpServletRequest request,
			HttpServletResponse response){
		Map<String,Object> map=new HashMap<String,Object>();
		Integer userid=Integer.parseInt(request.getParameter("userid"));
		String token=request.getParameter("token");
		if (validateToken(userid, token)) {
			map = rspFormat("", SUCCESS);
			}
		else{
			map = rspFormat("", WRONG_TOKEN);
		}
		map=rspFormat("", WRONG_TOKEN);
		return map;
	}
}