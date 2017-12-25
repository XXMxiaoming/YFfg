package com.yfwl.yfgp.schedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONArray;
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
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.Traderec;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.Time;

@Component
public class ClearSchedule {
	
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
	private static Logger logger = OrderBookSchedule.getLogger("d:/logs/yfgp/schedule/ClearSchedule_debug_");
	
	public void bindSchedule() {
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK); 
		SimpleDateFormat dateSdf =   new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		if(!(week == 1 || week ==7 || AccountUtil.HOLIDAY_STRING.contains(dateString))) {
			
			logger.debug("============================ ClearSchedule 开始执行============================");
			
			logger.debug("====================== clearOrderBook方法  开始执行=====================");
			clearOrderBook();
			logger.debug("====================== clearOrderBook方法  结束执行=====================");
			
			logger.debug("====================== updatePosition方法  开始执行=====================");
			updatePosition();
			logger.debug("====================== updatePosition方法  结束执行=====================");
			
			logger.debug("====================== refreshScore方法  开始执行=====================");
			refreshScore();
			logger.debug("====================== refreshScore方法  结束执行=====================");
			
			logger.debug("====================== updateRank方法  开始执行=====================");
			updateRank();
			logger.debug("====================== updateRank方法  结束执行=====================");
			
			//exrights();
			
			logger.debug("============================ ClearSchedule 开始执行============================");
		}
		
	}
	
	
	public void clearOrderBook() {
		List<OrderBook> orderList = groupService.getAllOrderBookList();
		if(!orderList.isEmpty()) {
			for(int i = 0; i < orderList.size(); i++) {
				OrderBook  orderBook = orderList.get(i);
				
				logger.debug("~~~~~~~~~~~~~~ clearOrderBook执行中，正在处理订单 " + orderBook.getOrderid()+"~~~~~~~~~~~~~~");
				
				Integer hasDelete = groupService.deleteOrderBook(orderBook);
				if(hasDelete > 0) {
					switch(orderBook.getAction()) {
					case 1:
						double frozen = Math.floor(orderBook.getVol() * orderBook.getPrice() * 100 * 100) / 100;
						Accounts accounts = new Accounts();
						accounts.setGid(orderBook.getGid());
						Accounts mainAccounts = groupService.getAccounts(accounts);
						if(null != mainAccounts) {
							mainAccounts.setAvailable(mainAccounts.getAvailable() + frozen);
							mainAccounts.setFrozen(0);
							groupService.updateAccounts(mainAccounts);
						}
						break;
					case 3:
						Posi posi = new Posi();
						posi.setGid(orderBook.getGid());
						posi.setStockid(orderBook.getStockid());
						Posi mainPosi = groupService.getStockPosi(posi);
						if(null != mainPosi) {
							mainPosi.setAvailable(mainPosi.getAvailable() + orderBook.getVol());
							mainPosi.setFrozen(mainPosi.getFrozen() - orderBook.getVol());
							groupService.updatePosi(mainPosi);
						}
						break;
					default:
						break;
						
					}
					
					Date inserttime = new Date();
					Traderec traderec = new Traderec();
					traderec.setGid(orderBook.getGid());
					traderec.setAction(orderBook.getAction());
					traderec.setInserttime(orderBook.getInserttime());
					traderec.setStockid(orderBook.getStockid());
					traderec.setOrderprice(orderBook.getPrice());
					traderec.setVol(orderBook.getVol());
					traderec.setTradetime(inserttime);
					traderec.setStatus(2);
					groupService.initTraderec(traderec);
					
				}
				
				logger.debug("~~~~~~~~~~~~~~ clearOrderBook执行中，处理完订单 " + orderBook.getOrderid()+"~~~~~~~~~~~~~~");
			}
		}
		logger.debug("~~~~~~~~~~~~~~ clearOrder执行完毕，共处理：" + orderList.size() + " 个订单~~~~~~~~~~~~~~");
	}
	
	public void updatePosition() {
		groupService.clearPosi();
	}
	
	public void refreshScore() {
		List<Accounts> accountsList = groupService.getAllAccountsList();
		String token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
		Date updatetime = new Date();
		for(int i = 0 ;i < accountsList.size(); i++) {
			Accounts mainAccounts = accountsList.get(i);
			if(null != mainAccounts) {
				
				logger.debug("~~~~~~~~~~~~~~ refreshScore执行中，正在处理Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				
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
					int times = 3;
					while(times > 0){
						String resultStr = GetHSTokenUtils.getReal(stockStr, token);
						JSONObject jsonData = new JSONObject(resultStr);
						int k=0;
						while(jsonData.toString().contains("访问令牌无效或已过期!")){
							k++;
							token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
							resultStr = GetHSTokenUtils.getReal(stockStr, token);
							jsonData = new JSONObject(resultStr);
							if(k>3){
								break;
							}
						 }
						
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
							times = 0;
						}catch (Exception e) {
							times--;
						}
					}
					
				}
						
				Date date = new Date();
				Calendar c = Calendar.getInstance();
			    c.setTime(date);
			    c.add(Calendar.DAY_OF_YEAR, -7);
			    Date r7Date = c.getTime();
			    c.setTime(date);
			    c.add(Calendar.MONTH, -1);
				Date r30Date = c.getTime();
				
				c.setTime(date);
			    c.add(Calendar.MONTH, -6);
			    Date r6mDate = c.getTime();
			    
			    c.setTime(date);
			    c.add(Calendar.YEAR, -1);
			    Date r1yDate = c.getTime();
				
			    Score last = new Score();
			    last.setGid(mainAccounts.getGid());
			    double value;
				Score lastScore = groupService.getLastScore(last);
				if(null == lastScore) {
					value = mainAccounts.getInit();
				}
				else {
					value = lastScore.getTotal();
				}
				
				Stat s = new Stat();
				s.setGid(mainAccounts.getGid());
				Stat stat = groupService.getStat(s);
				
				Score score = new Score();
				score.setGid(mainAccounts.getGid());
				score.setTotal((int)mainAccounts.getTotal());
				score.setData(date);
				groupService.initScore(score);
				
				if(null != stat) {
					Accounts statAccounts = stat.getAccounts();
					double get = ((statAccounts.getTotal() / statAccounts.getInit()) - 1)  * 100;
					if( get < stat.getMaxLost()) {
						stat.setMaxLost(get);
					}
					if(get > stat.getMaxProfit()) {
						stat.setMaxProfit(get);
					}
					double r7rate;
					double r30rate;
					double r60rate;
					double r1yrate;
					Score statScore = new Score();
					statScore.setGid(stat.getGid());
//					Score r7Score = groupService.getr7Score(statScore);
//					r7rate = Time.daysBetween(r7Date, r7Score.getDate()) <= 0?r7Score.getTotal():statAccounts.getInit();
//					Score r30Score = groupService.getr30Score(statScore);
//					r30rate = Time.daysBetween(r30Date, r30Score.getDate()) <= 0?r30Score.getTotal():statAccounts.getInit();
//					Score r6mScore = groupService.getr6mScore(statScore);
//					r6mrate = Time.daysBetween(r6mDate, r6mScore.getDate()) <= 0?r6mScore.getTotal():statAccounts.getInit();
//					Score r1yScore = groupService.getr1yScore(statScore);
//					r1yrate = Time.daysBetween(r1yDate, r1yScore.getDate()) <= 0?r1yScore.getTotal():statAccounts.getInit();
					
					//2016-08-05更改7日30日60日1年收益方法
					List<Score> r7dayScoreList = groupService.getR7dayScoreList(statScore);
					int num = r7dayScoreList.size();  //统计个数 
					if(num != 7)
					{
						r7rate = statAccounts.getInit(); //初始金额1000000
					}else
					{
						r7rate = r7dayScoreList.get(num - 1).getTotal();//往前第七天的总金额,不够七天就获取最早的那天的金额
					}
					
					//30日收益
					List<Score> r30dayScoreList = groupService.getR30dayScoreList(statScore);
					num = r30dayScoreList.size();
					if(num != 30)
					{
						r30rate = statAccounts.getInit(); //初始金额1000000
					}else
					{
						r30rate = r30dayScoreList.get(num - 1).getTotal();
					}
					
					//60日收益
					List<Score> r60dayScoreList = groupService.getR60dayScoreList(statScore);
					num = r60dayScoreList.size();
					if(num != 60)
					{
						r60rate = statAccounts.getInit(); //初始金额1000000
					}else
					{
						r60rate = r60dayScoreList.get(num - 1).getTotal();
					}
					
					//1年(360天)收益
					List<Score> r1yearScoreList = groupService.getR1yearScoreList(statScore);
					num = r1yearScoreList.size();
					if(num != 360)
					{
						r1yrate = statAccounts.getInit(); //初始金额1000000
					}else
					{
						r1yrate = r1yearScoreList.get(num - 1).getTotal();
					}
					
					double rd = (statAccounts.getTotal() - value) * 100 / value;
					stat.setRd(rd);
					stat.setRa(((statAccounts.getTotal() / statAccounts.getInit()) - 1) * 100);
					stat.setR7(((statAccounts.getTotal() / r7rate) - 1) * 100);
					stat.setR30(((statAccounts.getTotal() / r30rate) - 1) * 100);
					stat.setR6m(((statAccounts.getTotal() / r60rate) - 1) * 100);
					stat.setR1y(((statAccounts.getTotal() / r1yrate) - 1) * 100);
					stat.setUpdatetime(updatetime);
					groupService.updateStat(stat);
					
				}
				
				logger.debug("~~~~~~~~~~~~~~ refreshScore执行中，处理完Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				mainAccounts = null;
			}
		}
		logger.debug("~~~~~~~~~~~~~~ refreshScore执行完毕，共处理：" + accountsList.size() + " 个Accounts~~~~~~~~~~~~~~");
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
