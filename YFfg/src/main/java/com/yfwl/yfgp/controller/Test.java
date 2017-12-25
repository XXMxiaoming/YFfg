package com.yfwl.yfgp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easemob.server.method.SendMessageMethods;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.Accounts2;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Score;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.StockXml;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.ValidateCode;
import com.yfwl.yfgp.service.AccountsService2;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.HomeService;
import com.yfwl.yfgp.service.SMSendValiCodeService;
import com.yfwl.yfgp.service.StockInfoService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserIpService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.StockUpDownUtils;
import com.yfwl.yfgp.utils.Time;

@Controller
@RequestMapping("/test22")
public class Test extends BaseController {
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	@Autowired
	private SMSendValiCodeService sMSandValiCodeService;
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
	
	@Resource
	UserIpService userIpService;
	
	
	
	@RequestMapping(value = "/test8",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> testr8(HttpServletRequest request){
			
		Map<String, Object> map = new HashMap<String, Object>();
		List<StockXml> list = StockUpDownUtils.analyzeUpDownXml();
		if(list.size() > 0){
			for(StockXml stockXml:list){
				
				String stockName = stockXml.getZqjc();
				//String stockCjj = stockXml.getDay_Cjj();
				String upDown = stockXml.getUp_Down();
				//String info = stockXml.getInfo();
				String stockCode = stockXml.getZqdm();
				String operate = stockXml.getOperate();
				String market;
				
				Stockinfo stockinfo = stockInfoService.getStock(stockCode);
				Integer marketNum = stockinfo.getMarket();
				if(marketNum == 1){
					market = ".SS";
				}else{
					market = ".SZ";
				}
				
				List<String> easemobIdList = userService.getStockAttUser(stockCode);
				if(easemobIdList.size() > 0){
					String targetType = "users";
					String from = "lbh3zyi";
					String stock = " $"+stockName + "(" + stockCode + market + ")$";
					ObjectNode ext = factory.objectNode();
					String msg = "您关注的" + stock + "，已出现中期" + upDown + "信号，请及时" + operate +"。";
					SendMessageMethods.sendTxtMsg(targetType, easemobIdList, from, ext, msg);
				}
			}
		}
		return map;
	}
	
	
	
	@RequestMapping(value = "/testr7",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> testr7(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		
		//2016-08-05更改7日30日60日1年收益方法
		Stat s = new Stat();
		s.setGid(4954);
		Stat stat = groupService.getStat(s);
		
		Accounts statAccounts = stat.getAccounts();
		
		double r7rate;
		Score statScore = new Score();
		statScore.setGid(4954);
		List<Score> r7dayScoreList = groupService.getR7dayScoreList(statScore);
		int num = r7dayScoreList.size();  //统计个数 
		if(num != 7)
		{
			r7rate = statAccounts.getInit(); //初始金额1000000
		}else
		{
			r7rate = r7dayScoreList.get(num - 1).getTotal();//往前第七天的总金额,不够七天就获取最早的那天的金额
		}
		
		
		double rd = (statAccounts.getTotal() - 1000000) * 100 / 1000000;
		double ra = ((statAccounts.getTotal() / statAccounts.getInit()) - 1) * 100;
		double r7 = ((statAccounts.getTotal() / r7rate) - 1) * 100;
		//stat.setRd(rd);
		//stat.setRa(((statAccounts.getTotal() / statAccounts.getInit()) - 1) * 100);
		//stat.setR7(((statAccounts.getTotal() / r7rate) - 1) * 100);
		map.put("rd", rd);
		map.put("ra", ra);
		map.put("r7", r7);
		
		return map;
	}
	
	
	
	
	@RequestMapping(value = "/test1",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> test1(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> datamap = new HashMap<String, Object>();
		
		String stockDownloadUrl = PropertiesUtils.getProperties().getProperty("AppDownloadUrl") + "stocklist.txt";
		String secidDownloadUrl = PropertiesUtils.getProperties().getProperty("AppDownloadUrl") + "secid.txt";
		String holidayDownloadUrl = PropertiesUtils.getProperties().getProperty("AppDownloadUrl") + "holiday.txt";
		datamap.put("stockDownloadUrl", stockDownloadUrl);
		datamap.put("secidDownloadUrl", secidDownloadUrl);
		datamap.put("holidayDownloadUrl", holidayDownloadUrl);
		
		URL url = null;
		/** http连接 */
		HttpURLConnection httpConn = null;
		/**//** 输入流 */
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();
		try {
			url = new URL("http://120.24.208.86:30555/apk/yfStock.properties");
			in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8"));
			String str = null;
			while ((str = in.readLine()) != null) {
				//sb.append(str).append("\n");
				String[] s1 = str.split(" = ");
				datamap.put(s1[0], s1[1]);
			}
			
			String appName = (String) datamap.get("appName");
			String appDownloadUrl = PropertiesUtils.getProperties().getProperty("AppDownloadUrl")+appName;
			datamap.put("appDownloadUrl", appDownloadUrl);
			
			datamap.remove("appName");
			datamap.remove("secid");
			datamap.remove("stocklist");
			datamap.remove("holiday");
			
			map.put("data", datamap);
			map.put("status", 0);
			map.put("message", "操作成功");
			
		} catch (Exception ex) {

		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			}
		}
		return map;
	}
	
	
	
	
	@RequestMapping(value = "/test",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> test(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		Accounts2 accounts2 = accountsService2.getFeeAccounts(2680);
		map.put("accounts2", accounts2);
		return map;
	}
	
	
	@RequestMapping(value = "/testSMS",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> testSMS(HttpServletRequest request,
			HttpServletResponse response){
		
		String phone = "18129948342";
		String type = "ZHMM";
		Map<String, Object> map = new HashMap<String, Object>();
		boolean isOk = sMSandValiCodeService.sendSMS(phone, type);
		map.put("status", isOk);
		
		return map;
	}
	
	@RequestMapping(value = "/testVC",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> testVC(HttpServletRequest request,
			HttpServletResponse response){
		
		String phone = "18129948342";
		String marker = "ZFMM";
		String randomNum = "5483";
		
		ValidateCode validateCode = new ValidateCode();
		validateCode.setPhone(phone);
		validateCode.setMarker(marker);
		validateCode.setRandomNum(randomNum);
		Map<String, Object> map = new HashMap<String, Object>();
		boolean isOk = sMSandValiCodeService.validataCode(validateCode);
		map.put("status", isOk);
		
		return map;
	}
	
	@RequestMapping(value = "/sendbonus",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> sendbonus(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		Date timeStart = new Date();
		Calendar c = Calendar.getInstance();
	    c.setTime(timeStart);
	    c.add(Calendar.DAY_OF_MONTH, 1);
		Date timeExpire = c.getTime();
		String transactionId = timeStart.getTime() + "";
		String outTradeNo = transactionId;
		Order useOrder = new Order();
		useOrder.setTransactionId(transactionId);
		useOrder.setOutTradeNo(outTradeNo);
		useOrder.setUserId(85);
		useOrder.setOrderId(0);
		useOrder.setTimeStart(timeStart);
		useOrder.setTimeExpire(timeExpire);
		useOrder.setTradeType(23);
		useOrder.setFeeTotal(50);
		useOrder.setFeeLeft(50);
		useOrder.setBody("红包");
		useOrder.setDetail("5");
		useOrder.setStatus(1);
		cashService.initOrder(useOrder);
		
		List<String> pushUserList = new ArrayList<String>();
		pushUserList.add("8iuavkhm");
		String msg = "[股哥红包]"+ useOrder.getBody();
        ObjectNode ext = JsonNodeFactory.instance.objectNode();
        ext.put("is_red_paper_message", String.valueOf(1));
        ext.put("id", String.valueOf(useOrder.getId()));
        ext.put("transactionId", useOrder.getTransactionId());
        ext.put("outTradeNo", useOrder.getOutTradeNo());
        ext.put("userId", String.valueOf(useOrder.getUserId()));
        ext.put("orderId", String.valueOf(useOrder.getOrderId()));
        ext.put("timeStart", useOrder.getTimeStart().toString());
        ext.put("timeExpire", useOrder.getTimeExpire().toString());
        ext.put("tradeType", useOrder.getTradeType());
        ext.put("feeLeft", useOrder.getFeeLeft());
        ext.put("body", useOrder.getBody());
        ext.put("detail", useOrder.getDetail());
        ext.put("status", useOrder.getStatus());
        sendEaseMobMsg(pushUserList, ext, msg);
        
		 // 给用户发一条文本消息
        /*String from = "lbh3zyi";
        String targetTypeus = "users";
        ObjectNode ext = factory.objectNode();
        ArrayNode targetusers = factory.arrayNode();
        targetusers.add("yxz3969");
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
        if (null != sendTxtMessageusernode) {
            
        }*/
		return map;
	}
	
	@RequestMapping(value = "/updateorder",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> updateorder(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		//updateOrder();
		return map;
	}
	
	
	public void updateOrder() {
		List<Order> orderList = cashService.getZuheOrder();
		Date now = new Date();
		Order order = new Order();
		if(!orderList.isEmpty()) {
			for(int i = 0; i < orderList.size(); i++) {
				Order mainOrder = orderList.get(i);
				Integer gid = Integer.parseInt(mainOrder.getOutTradeNo());
				Accounts a = new Accounts();
				a.setGid(gid);
				Accounts mainAccounts = groupService.getAccounts(a);
				Integer n2e = Time.compareTime(now, mainAccounts.getEndtime());
				double profit = (mainAccounts.getTotal() -1000000) / 10000;
				double goal = Double.parseDouble(mainOrder.getBody());
				float bonus = mainOrder.getFeeLeft();
				if(mainAccounts.getDel().equals(0) && n2e >= 0 && profit < goal) {
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
						float average_bonus = bonus / followOrderList.size();
						for(int j = 0; j < followOrderList.size(); j++) {
							
							Date timeStart = new Date();
							Date timeExpire = timeStart;
							String transactionId = timeStart.getTime() + "";
							String outTradeNo = transactionId;
							Order useOrder = new Order();
							useOrder.setTransactionId(transactionId);
							useOrder.setOutTradeNo(outTradeNo);
							useOrder.setUserId(followOrderList.get(j).getUserId());
							useOrder.setOrderId(followOrderList.get(j).getId());
							useOrder.setTimeStart(timeStart);
							useOrder.setTimeExpire(timeExpire);
							useOrder.setTradeType(19);
							useOrder.setFeeTotal(average_bonus + mainOrder.getFeeTotal());
							useOrder.setFeeLeft(0);
							useOrder.setBody("付费组合失败");
							useOrder.setDetail("系统返回");
							useOrder.setStatus(0);
							cashService.initOrder(useOrder);
							
							
							
							CashAccount followCash = cashService.getCashAccountByUserId(followOrderList.get(j).getUserId());
							followCash.setTotalCash(followCash.getTotalCash() + mainOrder.getFeeTotal() + average_bonus);
							cashService.updateCashAccount(followCash);
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
						useOrder.setDetail("系统返回");
						useOrder.setStatus(0);
						cashService.initOrder(useOrder);
						mainCash.setTotalCash(mainCash.getTotalCash() + bonus);
						mainCash.setTimeTill(now);
						cashService.updateCashAccount(mainCash);
					}
				}
				
				if(mainAccounts.getDel().equals(2)) {
					CashAccount mainCash = cashService.getCashAccountByUserId(mainAccounts.getUserid());
					mainOrder.setStatus(0);
					cashService.updateOrder(mainOrder);
					order.setOrderId(mainOrder.getId());
					List<Order> followOrderList = cashService.getListOrder(order);
					if(!followOrderList.isEmpty()) {
						float earn = mainOrder.getFeeTotal() * followOrderList.size() * 0.8f + bonus;
						mainCash.setTimeTill(now);
						mainCash.setTotalCash(mainCash.getTotalCash() + earn);
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
					}
				}
			}
		}
	}
	
	@RequestMapping(value = "/getStatus",method = { RequestMethod.GET })
	@ResponseBody
	public void getStatus(HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "0");
		String callback = (String)request.getParameter("callback");  
		//JSONArray json = JSONArray.toJSONObject(map); 
		Gson gson = new Gson();
		String string = gson.toJson(map);
	    String retStr = callback + "(" + string + ")";  
	    response.getWriter().print(retStr);
	}
	
	
	@RequestMapping(value = "/clearorder",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> clearorder(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		//updateOrder();
		List<Order> keepOrderList = cashService.getKeepOrderList();
		//Order o = new Order();
		if(!keepOrderList.isEmpty()) {
			for(int i = 0; i < keepOrderList.size(); i++) {
				//o.setId(keepOrderList.get(i).getOrderId());
				Order mainOrder = cashService.getOrderById(keepOrderList.get(i).getOrderId());
				if(null != mainOrder) {
					if(mainOrder.getStatus().equals(0)) {
						keepOrderList.get(i).setStatus(0);
						cashService.updateOrder(keepOrderList.get(i));
					}
				}
			}
		}
		return map;
	}
	
	

	@RequestMapping(value = "/initorder",method = { RequestMethod.GET })
	@ResponseBody
	public Map<String,Object> initorder(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Order> orderList = cashService.getZuheOrder();
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
					List<Order> followOrderList = cashService.getFollowListOrder(order);
					if(followOrderList.isEmpty()) {
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
					else if(followOrderList.size() < floor) {
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
								txtmsg.put("msg", "付费组合#" + accountsName +"("+gid+")#已启动");
						        txtmsg.put("type","txt");
						        //EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
							}
						}
					}
				}
			}
		}
		return map;
	}
}
	
