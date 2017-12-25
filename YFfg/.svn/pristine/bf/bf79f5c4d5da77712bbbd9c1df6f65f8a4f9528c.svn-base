package com.yfwl.yfgp.schedule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.easemob.server.method.SendMessageMethods;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.controller.CashController;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.Advance;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Score;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.Traderec;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserMeta;
import com.yfwl.yfgp.model.Optitask;
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
public class OptiSchedule {
	@Resource
	GroupService groupService;
	@Resource
	TokenService tokenService;
	@Resource
	UserService userService;
	@Resource
	CashService cashService;
	
	
	private static final double STOCK_AMOUNT = 1000000.f;
	
	private static Logger logger = OrderBookSchedule.getLogger("d:/logs/yfgp/schedule/OptiSchedule_debug_");
	
	private static boolean isSend = false;  //是否已经发送了上证指数的升跌信号提示
	
	private static boolean isSendOptiProfit = false;  //是否已经发送了每月优化组合提示
	
	/**
	 * 优化组合定时操作
	 */
	public void bindSchedule() {
		Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK);
		int hour = c.get(Calendar.HOUR_OF_DAY); 
		int minute = c.get(Calendar.MINUTE);
		
		SimpleDateFormat dateSdf =   new SimpleDateFormat("yyyyMMdd");
		Date datenow = new Date();
		String dateString = dateSdf.format(datenow);
		if(!(week == 1 || week ==7 || AccountUtil.HOLIDAY_STRING.contains(dateString))) {
		
			if(hour == 9 && minute >=25 && minute < 30) {
				
				//控制每天开盘前只运行一次
				if(minute == 25 && !isSend) {
					isSend = true;
					sendToAllUser();
				}
				
				if(minute == 27) {
					logger.debug("============================盘前9:27(type1) OptiSchedule 开始执行============================");
					
					optiAccountDown();
					
					optiAccountUp();
					
					logger.debug("======================(type1) optiAccounts方法  开始执行=====================");
					optiAccounts2();
					logger.debug("======================(type1) optiAccounts方法  结束执行=====================");
					
					logger.debug("======================(type1) advanceAccounts方法  开始执行=====================");
					advanceAccounts();
					logger.debug("======================(type1) advanceAccounts方法  结束执行=====================");
					
					logger.debug("============================盘前9:27(type1) OptiSchedule 结束执行============================");
				}
				
			}
			
			if((hour == 9 && minute > 30) || hour == 10 || (hour == 11 && minute <= 30) || hour == 13 || hour == 14) {
				logger.debug("============================盘中(type2) OptiSchedule 开始执行============================");
				
				logger.debug("======================(type2) updateWebStat方法  开始执行=====================");
				updateWebStat();
				logger.debug("======================(type2) updateWebStat方法  结束执行=====================");
				
				optiAccountsellOperation();
				
//				logger.debug("======================(type2) optiAccounts方法  开始执行=====================");
//				optiAccounts();
//				logger.debug("======================(type2) optiAccounts方法  结束执行=====================");
//				
//				logger.debug("======================(type2) advanceAccounts方法  开始执行=====================");
//				advanceAccounts();
//				logger.debug("======================(type2) advanceAccounts方法  结束执行=====================");
				
				logger.debug("============================盘中(type2) OptiSchedule 结束执行============================");
				if(isSend) {
					isSend = false;
				}
				if(isSendOptiProfit) {
					isSendOptiProfit = false;
				}
			}
		}
		
		//判断是不是月初(1号)
		if(c.get(Calendar.DAY_OF_MONTH) == 1) {
			//收盘后 下午3:30发送优化组合收益提示
			if(hour == 15 && minute == 30 && !isSendOptiProfit) {
				isSendOptiProfit = true;
				sendOptiAccountsMonthProfit(c.getTime());
			}
		}
	}
	
	//盘中处理优化组合卖出处理, 仅处理卖出操作
	public void optiAccountsellOperation() {
		String token = getDefaultToken();
		Date now = new Date();
		List<Optitask> optitaskList = groupService.getAllRunOptitask();
		if(!optitaskList.isEmpty()) {
			for(int i = 0; i < optitaskList.size(); i++) {
				Optitask optitask = optitaskList.get(i);
				if(optitask.getOperation() == 3) {
					Accounts taccount = new Accounts();
					taccount.setGid(optitask.getGid());
					Accounts mainAccount = groupService.getAccounts(taccount);
					if(mainAccount != null) {
						taccount.setGid(mainAccount.getOptigid());
						Accounts optiAccount = groupService.getAccounts(taccount);
						if(optiAccount != null) {
							//获取实时价格和买卖点
							String stockid = optitask.getStockid();
							String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
							String result = GetHSTokenUtils.getBuySallPoint(stock);
							String real = GetHSTokenUtils.getReal(stock, token);
							JSONObject realObject = new JSONObject(real);
							int k=0;
							while(realObject.toString().contains("访问令牌无效或已过期!")){
								k++;
								token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
								real = GetHSTokenUtils.getReal(stock, token);
								realObject = new JSONObject(real);
								if(k>3){
									break;
								}
							 }
							try 
							{
								JSONObject snapshot = realObject.getJSONObject("data").getJSONObject("snapshot");
								double last = snapshot.getJSONArray(stock).getDouble(2);
								double preclose = snapshot.getJSONArray(stock).getDouble(3);
								double highPrice = snapshot.getJSONArray(stock).getDouble(7);
								double lowPrice = snapshot.getJSONArray(stock).getDouble(8);
								double up_px = snapshot.getJSONArray(stock).getDouble(4);
								double down_px = snapshot.getJSONArray(stock).getDouble(5);
								double realPrice = 0.0;
								
								JSONObject jsonObject = new JSONObject(result);
								JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
								int len = candle.getJSONArray(stock).length();
								JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
								int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
								
								if(last < 0.01 || highPrice <= 0.01 || lowPrice <= 0.01) { 
									realPrice = preclose;
									highPrice = preclose;
									lowPrice = preclose;
								}
								else {
									realPrice = last;
								}
								
								if(buyorsall == 1 || buyorsall == 2) {
									//主组合持仓比例
									Posi p = new Posi();
									p.setGid(mainAccount.getGid());
									p.setStockid(optitask.getStockid());
									Posi mainposi  = groupService.getStockPosi(p);
									Double mainscale = 0.0;
									if(mainposi != null) {
										mainscale = mainposi.getVol() * 100 * realPrice / mainAccount.getTotal();
									} 
									
									p.setGid(mainAccount.getOptigid());
									p.setStockid(optitask.getStockid());
									Posi optiposi = groupService.getStockPosi(p);
									double optiscale = 0.0;
									Integer vol = 0;
									if(optiposi != null) {
										if(optiposi.getAvailable() > 0 && optiposi.getOnway() == 0) {
											optiscale = optiposi.getAvailable() * 100 * realPrice / optiAccount.getTotal();
											if(mainscale == 0) {
												//主组合没有持仓了，优化组合全部卖掉
												vol = optiposi.getAvailable();
											} else if(optiscale > mainscale) {
												double dif = optiscale - mainscale;
												vol = (int)(optiAccount.getTotal() * dif / realPrice / 100);
											}
											//下单卖出
											if(vol > 0) {
												OrderBook orderbook = new OrderBook();
												orderbook.setGid(optiAccount.getGid());
												orderbook.setAction(3);
												orderbook.setStockid(stockid);
												orderbook.setVol(vol);
												orderbook.setInserttime(now);
												orderbook.setPrice(down_px);
												groupService.initOrderBook(orderbook);
												
												Traderec traderec = new Traderec();
												traderec.setGid(optiAccount.getGid());
												traderec.setStockid(stockid);
												traderec.setVol(vol);
												traderec.setAction(3);
												traderec.setStatus(1);
												traderec.setInserttime(now);
												traderec.setOrderprice(down_px);
												groupService.initTraderec(traderec);
												
												
												optiposi.setAvailable(optiposi.getAvailable() - vol);
												optiposi.setFrozen(optiposi.getFrozen() + vol);
												groupService.updatePosi(optiposi);
												
												//
												//删除任务
												optitask.setStatus(1);
												optitask.setUpdatetime(now);
												groupService.deleteOptitask(optitask);
											}
										}
										
									}
									
									
								}	
								
							} catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
					}
		
				}
			}
		}
		
	}
	
	public void optiAccounts2() {
		String token = getDefaultToken();
		Date now = new Date();
		List<Optitask> optitaskList = groupService.getAllRunOptitask();
		if(!optitaskList.isEmpty()) {
			for(int i = 0; i < optitaskList.size(); i++) {
				Optitask optitask = optitaskList.get(i);
				Accounts taccount = new Accounts();
				taccount.setGid(optitask.getGid());
				Accounts mainAccount = groupService.getAccounts(taccount);
				if(mainAccount != null) {
					taccount.setGid(mainAccount.getOptigid());
					Accounts optiAccount = groupService.getAccounts(taccount);
					if(optiAccount != null) {
						//获取实时价格和买卖点
						String stockid = optitask.getStockid();
						String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
						String result = GetHSTokenUtils.getBuySallPoint(stock);
						String real = GetHSTokenUtils.getReal(stock, token);
						JSONObject realObject = new JSONObject(real);
						int k=0;
						while(realObject.toString().contains("访问令牌无效或已过期!")){
							k++;
							token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
							real = GetHSTokenUtils.getReal(stock, token);
							realObject = new JSONObject(real);
							if(k>3){
								break;
							}
						 }
						try 
						{
							
							JSONObject snapshot = realObject.getJSONObject("data").getJSONObject("snapshot");
							double last = snapshot.getJSONArray(stock).getDouble(2);
							double preclose = snapshot.getJSONArray(stock).getDouble(3);
							double highPrice = snapshot.getJSONArray(stock).getDouble(7);
							double lowPrice = snapshot.getJSONArray(stock).getDouble(8);
							double up_px = snapshot.getJSONArray(stock).getDouble(4);
							double down_px = snapshot.getJSONArray(stock).getDouble(5);
							double realPrice = 0.0;
							
							JSONObject jsonObject = new JSONObject(result);
							JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
							int len = candle.getJSONArray(stock).length();
							JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
							int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
							
							//如果股票停牌，不下单
							if(last < 0.01 || highPrice < 0.01 || lowPrice < 0.01) { 
								continue;
							}
							else {
								realPrice = last;
							}
							
							//主组合的买卖动作
							switch(optitask.getOperation()) {
							case 1: //主组合进行了买入操作
								
								//跌信号优化组合全卖掉
								if(buyorsall == 2) {
									Integer vol = 0;
									Posi p = new Posi();
									p.setGid(optiAccount.getGid());
									p.setStockid(stockid);
									Posi optiPosi = groupService.getStockPosi(p);
									if(null != optiPosi) {
										if(optiPosi.getAvailable() > 0) {
											vol = optiPosi.getAvailable();
											OrderBook orderbook = new OrderBook();
											orderbook.setGid(optiAccount.getGid());
											orderbook.setAction(3);
											orderbook.setStockid(stockid);
											orderbook.setVol(vol);
											orderbook.setInserttime(now);
											orderbook.setPrice(down_px);
											groupService.initOrderBook(orderbook);
											
											Traderec traderec = new Traderec();
											traderec.setGid(optiAccount.getGid());
											traderec.setStockid(stockid);
											traderec.setVol(vol);
											traderec.setAction(3);
											traderec.setStatus(1);
											traderec.setInserttime(now);
											traderec.setOrderprice(down_px);
											groupService.initTraderec(traderec);
											
											
											optiPosi.setAvailable(optiPosi.getAvailable() - vol);
											optiPosi.setFrozen(optiPosi.getFrozen() + vol);
											groupService.updatePosi(optiPosi);
										}
									}
									
									//删除任务
									optitask.setStatus(1);
									optitask.setUpdatetime(now);
									groupService.deleteOptitask(optitask);
									break;
								}
								
								if(buyorsall == 1) {
									//主组合持仓比例
									Posi p = new Posi();
									p.setGid(mainAccount.getGid());
									p.setStockid(optitask.getStockid());
									Posi mainposi  = groupService.getStockPosi(p);
									Double mainscale = 0.0;
									if(mainposi != null) {
										mainscale = mainposi.getVol() * 100 * realPrice / mainAccount.getTotal();
									} else {
										//删除任务
										optitask.setStatus(1);
										optitask.setUpdatetime(now);
										groupService.deleteOptitask(optitask);
										break;
									}
									
									if(mainscale == 0) {
										//删除任务
										optitask.setStatus(1);
										optitask.setUpdatetime(now);
										groupService.deleteOptitask(optitask);
										break;
									}
					
									p.setGid(mainAccount.getOptigid());
									p.setStockid(optitask.getStockid());
									Posi optiposi = groupService.getStockPosi(p);
									Integer vol = 0;
									
									if(optiposi == null) {
										vol = (int)(optiAccount.getTotal() * mainscale / realPrice / 100);
									} else {
										//持仓不为空时，如果优化组合持仓比例小于主组合，就买入差额部分
										double optiscale = optiposi.getVol() * realPrice * 100 / optiAccount.getTotal();
										if(mainscale > optiscale) {
											double dif = mainscale - optiscale;
											vol = (int)(optiAccount.getTotal() * dif / realPrice / 100);
										}
										
									}
									double money = vol * (up_px - 0.01) * 1.0003 * 100;
									//如果优化组合可用资金不够买vol数量的股票，能买多少就买多少
									if(optiAccount.getAvailable() < money){
										vol = (int)(optiAccount.getAvailable() / (100 * 1.0003 * (up_px - 0.01)));
										money = vol * 100 * 1.0003 * (up_px - 0.01);
									}
									if(vol > 0){
	
										OrderBook orderbook = new OrderBook();
										orderbook.setGid(optiAccount.getGid());
										orderbook.setAction(1);
										orderbook.setStockid(stockid);
										orderbook.setVol(vol);
										orderbook.setInserttime(now);
										orderbook.setPrice(up_px - 0.01);
										groupService.initOrderBook(orderbook);
										
										Traderec traderec = new Traderec();
										traderec.setGid(optiAccount.getGid());
										traderec.setStockid(stockid);
										traderec.setVol(vol);
										traderec.setAction(1);
										traderec.setStatus(1);
										traderec.setInserttime(now);
										traderec.setOrderprice(up_px - 0.01);
										groupService.initTraderec(traderec);
										
										optiAccount.setAvailable(optiAccount.getAvailable() - money);
										optiAccount.setFrozen(optiAccount.getFrozen() + money);
										groupService.updateAccounts(optiAccount);
									}
									
								}
								
								//删除任务
								optitask.setStatus(1);
								optitask.setUpdatetime(now);
								groupService.deleteOptitask(optitask);
								break;
							case 3: //主组合进行了卖出操作,优化组合当天有在途股票的情况
								if(buyorsall == 1) {
									//主组合持仓比例
									Posi p = new Posi();
									p.setGid(mainAccount.getGid());
									p.setStockid(optitask.getStockid());
									Posi mainposi  = groupService.getStockPosi(p);
									Double mainscale = 0.0;
									if(mainposi != null) {
										mainscale = mainposi.getVol() * 100 * realPrice / mainAccount.getTotal();
									}
									
									p.setGid(optiAccount.getGid());
									Posi optiPosi = groupService.getStockPosi(p);
									Integer vol = 0;
									double optiscale = optiPosi.getAvailable() * realPrice * 100 / optiAccount.getTotal();
									
									if(optiPosi != null) {
										if(mainscale == 0) {
											vol = optiPosi.getAvailable();
										} else if(optiscale > mainscale) {
											double dif = optiscale - mainscale;
											vol = (int)(optiAccount.getTotal() * dif / realPrice / 100);
										}
									}
									
									if(vol > 0) {
										OrderBook orderbook = new OrderBook();
										orderbook.setGid(optiAccount.getGid());
										orderbook.setAction(3);
										orderbook.setStockid(stockid);
										orderbook.setVol(vol);
										orderbook.setInserttime(now);
										orderbook.setPrice(down_px);
										groupService.initOrderBook(orderbook);
										
										Traderec traderec = new Traderec();
										traderec.setGid(optiAccount.getGid());
										traderec.setStockid(stockid);
										traderec.setVol(vol);
										traderec.setAction(3);
										traderec.setStatus(1);
										traderec.setInserttime(now);
										traderec.setOrderprice(down_px);
										groupService.initTraderec(traderec);
										
										
										optiPosi.setAvailable(optiPosi.getAvailable() - vol);
										optiPosi.setFrozen(optiPosi.getFrozen() + vol);
										groupService.updatePosi(optiPosi);
									}
									
								}
								
								//跌信号优化组合全卖掉
								if(buyorsall == 2) {
									Integer vol = 0;
									Posi p = new Posi();
									p.setGid(optiAccount.getGid());
									p.setStockid(stockid);
									Posi optiPosi = groupService.getStockPosi(p);
									if(null != optiPosi) {
										if(optiPosi.getAvailable() > 0) {
											vol = optiPosi.getAvailable();
											OrderBook orderbook = new OrderBook();
											orderbook.setGid(optiAccount.getGid());
											orderbook.setAction(3);
											orderbook.setStockid(stockid);
											orderbook.setVol(vol);
											orderbook.setInserttime(now);
											orderbook.setPrice(down_px);
											groupService.initOrderBook(orderbook);
											
											Traderec traderec = new Traderec();
											traderec.setGid(optiAccount.getGid());
											traderec.setStockid(stockid);
											traderec.setVol(vol);
											traderec.setAction(3);
											traderec.setStatus(1);
											traderec.setInserttime(now);
											traderec.setOrderprice(down_px);
											groupService.initTraderec(traderec);

											optiPosi.setAvailable(optiPosi.getAvailable() - vol);
											optiPosi.setFrozen(optiPosi.getFrozen() + vol);
											groupService.updatePosi(optiPosi);
										}
									}
									
								}
								
								//删除任务
								optitask.setStatus(1);
								optitask.setUpdatetime(now);
								groupService.deleteOptitask(optitask);
								
								break;
							default:
									break;
							}
							
							
						} catch (Exception e)
						{
							e.printStackTrace();
						}

						
					} else {
						optitask.setStatus(1);
						optitask.setUpdatetime(now);
						groupService.deleteOptitask(optitask);
					}
					
				} else {
					optitask.setStatus(1);
					optitask.setUpdatetime(now);
					groupService.deleteOptitask(optitask);
				}
			}
		}
		
	}
	
	//当主组合没有操作，优化组合有转跌股票时，全部卖掉
	public void optiAccountDown() {
		String token = getDefaultToken();
		List<Accounts> accountsList = groupService.getAllOptiedAccountsList();
		Date now = new Date();
		
		if(!accountsList.isEmpty()) {
			for(int i = 0; i < accountsList.size(); i++) {
				//主组合
				Accounts mainAccounts = accountsList.get(i);
				
				//优化组合
				Accounts accounts =new Accounts();
				accounts.setGid(mainAccounts.getOptigid());
				Accounts optiAccounts = groupService.getAccounts(accounts);
				if(null == optiAccounts) {
					continue;
				}
			
				//优化组合持仓
				Posi p = new Posi();
				p.setGid(optiAccounts.getGid());
				List<Posi> optiPosiList = groupService.getPosi(p);
				if(!optiPosiList.isEmpty()) {
					for(int j = 0; j < optiPosiList.size(); j++) {
						Posi mainPosi = optiPosiList.get(j);
						String stockid = mainPosi.getStockid();
						
						//判断主组合有没有操作任务，有的话放到另外的方法去执行
						Optitask task = new Optitask();
						task.setGid(mainAccounts.getGid());
						task.setStockid(stockid);
						List<Optitask> taskList = groupService.getOptitask(task);
						if(!taskList.isEmpty()) {
							continue;
						}
						
						String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
						String result = GetHSTokenUtils.getBuySallPoint(stock);
						String real = GetHSTokenUtils.getReal(stock, token);
						JSONObject realObject = new JSONObject(real);
						int k=0;
						while(realObject.toString().contains("访问令牌无效或已过期!")){
							k++;
							token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
							real = GetHSTokenUtils.getReal(stock, token);
							realObject = new JSONObject(real);
							if(k>3){
								break;
							}
						 }
						try
						{
							JSONObject snapshot = realObject.getJSONObject("data").getJSONObject("snapshot");
							double last = snapshot.getJSONArray(stock).getDouble(2);
							double preclose = snapshot.getJSONArray(stock).getDouble(3);
							double highPrice = snapshot.getJSONArray(stock).getDouble(7);
							double lowPrice = snapshot.getJSONArray(stock).getDouble(8);
							double down_px = snapshot.getJSONArray(stock).getDouble(5);
							
							//股票停牌不下单
							if(last < 0.01 || highPrice < 0.01 || lowPrice < 0.01) {
								continue;
							} 
						
							JSONObject jsonObject = new JSONObject(result);
							JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
							int len = candle.getJSONArray(stock).length();
							JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
							int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
							
							Integer vol = 0;
							//跌信号全部卖出
							if(buyorsall == 2) {
								
								if(null != mainPosi) {
									if(mainPosi.getAvailable() > 0) {
										vol = mainPosi.getAvailable();
										OrderBook orderbook = new OrderBook();
										orderbook.setGid(optiAccounts.getGid());
										orderbook.setAction(3);
										orderbook.setStockid(stockid);
										orderbook.setVol(vol);
										orderbook.setInserttime(now);
										orderbook.setPrice(down_px);
										groupService.initOrderBook(orderbook);
										
										Traderec traderec = new Traderec();
										traderec.setGid(optiAccounts.getGid());
										traderec.setStockid(stockid);
										traderec.setVol(vol);
										traderec.setAction(3);
										traderec.setStatus(1);
										traderec.setInserttime(now);
										traderec.setOrderprice(down_px);
										groupService.initTraderec(traderec);
										
										
										mainPosi.setAvailable(mainPosi.getAvailable() - vol);
										mainPosi.setFrozen(mainPosi.getFrozen() + vol);
										groupService.updatePosi(mainPosi);
									}
								}
								
							}
							
							
						} catch(Exception e)
						{
							e.printStackTrace();
						}
						
						
					}
				}
				
				
			}
		}
		
	}
	
	//1.主组合买入跌股票，过段时间转升。2.主组合没创建优化组合前已经买了一些升信号的股票。3.关键条件:只有优化组合持仓为空才下单
		public void optiAccountUp() {
			String token = getDefaultToken();
			List<Accounts> accountsList = groupService.getAllOptiedAccountsList();//attr=6
			Date now = new Date();
			if(!accountsList.isEmpty()) {
				for(int i = 0; i < accountsList.size(); i++) {
					Accounts mainAccounts = accountsList.get(i);
					
					Posi p = new Posi();
					p.setGid(mainAccounts.getGid());
					Accounts accounts =new Accounts();
					accounts.setGid(mainAccounts.getOptigid());
					Accounts optiAccounts = groupService.getAccounts(accounts);
					if(null == optiAccounts) {
						continue;
					}
					List<Posi> posiList = groupService.getPosi(p);
					if(!posiList.isEmpty()) {
						for(int j = 0; j < posiList.size(); j++) {
							Posi mainPosi = posiList.get(j);
							String stockid = mainPosi.getStockid();
							
							//判断主组合有没有操作任务，有的话放到另外的方法去执行
							Optitask task = new Optitask();
							task.setGid(mainAccounts.getGid());
							task.setStockid(stockid);
							List<Optitask> taskList = groupService.getOptitask(task);
							if(!taskList.isEmpty()) {
								continue;
							}
							
							String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
							String result = GetHSTokenUtils.getBuySallPoint(stock);
							String real = GetHSTokenUtils.getReal(stock, token);
							JSONObject realObject = new JSONObject(real);
							int k=0;
							while(realObject.toString().contains("访问令牌无效或已过期!")){
								k++;
								token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
								real = GetHSTokenUtils.getReal(stock, token);
								realObject = new JSONObject(real);
								if(k>3){
									break;
								}
							 }
							try {
								JSONObject snapshot = realObject.getJSONObject("data").getJSONObject("snapshot");
								double last = snapshot.getJSONArray(stock).getDouble(2);
								double preclose = snapshot.getJSONArray(stock).getDouble(3);
								double highPrice = snapshot.getJSONArray(stock).getDouble(7);
								double lowPrice = snapshot.getJSONArray(stock).getDouble(8);
								double up_px = snapshot.getJSONArray(stock).getDouble(4);
								double realPrice = 0;
								
								//股票停牌不下单
								if(last < 0.01 || highPrice < 0.01 || lowPrice < 0.01) {
									continue;
								} else {
									realPrice = last;
								}
					
								JSONObject jsonObject = new JSONObject(result);
								JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
								int len = candle.getJSONArray(stock).length();
								JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
								int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
						
								p.setGid(mainAccounts.getOptigid());
								p.setStockid(stockid);
								Posi optiPosi = groupService.getStockPosi(p);
								switch(buyorsall) {
								case 1:	
										
									if(optiPosi == null) {
										
										//判断有没有挂单，如果有挂单就不再下单
										OrderBook ob = new OrderBook();
										ob.setGid(optiAccounts.getGid());
										ob.setStockid(stockid);
										List<OrderBook> obList = groupService.getOrderBookByGidAndStockid(ob);
										if(!obList.isEmpty() && obList.get(0).getAction() == 1) {
											break;
										}
										
										int vol = 0;
										double mainscale = 0;
										
										mainscale = mainPosi.getVol() * 100 * realPrice / mainAccounts.getTotal();
										vol = (int)(optiAccounts.getTotal() * mainscale / realPrice / 100);
					
										double money = vol * (up_px - 0.01) * 1.0003 * 100;
										//如果优化组合可用资金不够买vol数量的股票，能买多少就买多少
										if(optiAccounts.getAvailable() < money){
											vol = (int)(optiAccounts.getAvailable() / (100 * 1.0003 * (up_px - 0.01)));
											money = vol * 100 * 1.0003 * (up_px - 0.01);
										}
										if(vol > 0){
		
											OrderBook orderbook = new OrderBook();
											orderbook.setGid(optiAccounts.getGid());
											orderbook.setAction(1);
											orderbook.setStockid(stockid);
											orderbook.setVol(vol);
											orderbook.setInserttime(now);
											orderbook.setPrice(up_px - 0.01);
											groupService.initOrderBook(orderbook);
											
											Traderec traderec = new Traderec();
											traderec.setGid(optiAccounts.getGid());
											traderec.setStockid(stockid);
											traderec.setVol(vol);
											traderec.setAction(1);
											traderec.setStatus(1);
											traderec.setInserttime(now);
											traderec.setOrderprice(up_px - 0.01);
											groupService.initTraderec(traderec);
											
											optiAccounts.setAvailable(optiAccounts.getAvailable() - money);
											optiAccounts.setFrozen(optiAccounts.getFrozen() + money);
											groupService.updateAccounts(optiAccounts);
										}
									}
									break;
								default:
									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
					}
				}
			}
		}
	
	public void optiAccounts() {
		String token = getDefaultToken();
		List<Accounts> accountsList = groupService.getAllOptiedAccountsList();
		Date now = new Date();
		if(!accountsList.isEmpty()) {
			for(int i = 0; i < accountsList.size(); i++) {
				Accounts mainAccounts = accountsList.get(i);
				
				logger.debug("~~~~~~~~~~~~~~optiAccounts执行中，正在处理Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				
				Posi p = new Posi();
				p.setGid(mainAccounts.getGid());
				Accounts accounts =new Accounts();
				accounts.setGid(mainAccounts.getOptigid());
				Accounts optiAccounts = groupService.getAccounts(accounts);
				if(null == optiAccounts) {
					continue;
				}
				List<Posi> posiList = groupService.getPosi(p);
				if(!posiList.isEmpty()) {
					for(int j = 0; j < posiList.size(); j++) {
						Posi mainPosi = posiList.get(j);
						String stockid = mainPosi.getStockid();
						String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
						String result = GetHSTokenUtils.getBuySallPoint(stock);
						String real = GetHSTokenUtils.getReal(stock, token);
						JSONObject realObject = new JSONObject(real);
						try {
							JSONObject snapshot = realObject.getJSONObject("data").getJSONObject("snapshot");
							double up_px = snapshot.getJSONArray(stock).getDouble(4);
							double down_px = snapshot.getJSONArray(stock).getDouble(5);
						
							JSONObject jsonObject = new JSONObject(result);
							JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
							int len = candle.getJSONArray(stock).length();
							JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
							Integer time = stockArray.getInt(0);
							int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
							SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
							Date date = sdf.parse(time.toString());
							int days = Time.daysBetween(date, now);
							if((days <= 1 && days >= -1) || true) {					//区间买卖
								p.setGid(mainAccounts.getOptigid());
								p.setStockid(stockid);
								Posi optiPosi = groupService.getStockPosi(p);
								switch(buyorsall) {
								case 1:	
									int vol = 0;
									
									if(null != optiPosi) {
										if(mainPosi.getVol() > optiPosi.getVol()) {
											 vol = mainPosi.getVol() - optiPosi.getVol();
										}
										else {
											break;
										}
									}
									else {
										vol = mainPosi.getVol();
									}			
									double money = vol * 100 * 1.0003 * (up_px - 0.01);
									//如果优化组合可用资金不够买vol数量的股票，能买多少就买多少
									if(optiAccounts.getAvailable() < money){
										vol = (int)(optiAccounts.getAvailable() / (100 * 1.0003 * (up_px - 0.01)));
										money = vol * 100 * 1.0003 * (up_px - 0.01);
									}
									if(vol > 0){
										
										//判断有没有挂单，如果挂单的股票数量等于原组合股票数量后就不再下单
										OrderBook ob = new OrderBook();
										ob.setGid(optiAccounts.getGid());
										ob.setStockid(stockid);
										List<OrderBook> obList = groupService.getOrderBookByGidAndStockid(ob);
										if(obList != null) {
											int countVol = 0;
											for(OrderBook tob : obList) {
												countVol += tob.getVol();
											}
											//有持仓的话要加上
											if(optiPosi != null) {
												countVol += optiPosi.getVol();
											}
											//挂单数量+持仓数量+准备下单股票数量  不能超过  原组合持仓股数
											if(countVol + vol > mainPosi.getVol()) {
												break;
											}
										}
										
										OrderBook orderbook = new OrderBook();
										orderbook.setGid(optiAccounts.getGid());
										orderbook.setAction(1);
										orderbook.setStockid(stockid);
										orderbook.setVol(vol);
										orderbook.setInserttime(now);
										orderbook.setPrice(up_px - 0.01);
										groupService.initOrderBook(orderbook);
										
										Traderec traderec = new Traderec();
										traderec.setGid(optiAccounts.getGid());
										traderec.setStockid(stockid);
										traderec.setVol(vol);
										traderec.setAction(1);
										traderec.setStatus(1);
										traderec.setInserttime(now);
										traderec.setOrderprice(up_px - 0.01);
										groupService.initTraderec(traderec);
										
										optiAccounts.setAvailable(optiAccounts.getAvailable() - money);
										optiAccounts.setFrozen(optiAccounts.getFrozen() + money);
										groupService.updateAccounts(optiAccounts);
									}
									break;
								case 2:
									if(null != optiPosi) {
										if(optiPosi.getAvailable() > 0) {
											vol = optiPosi.getAvailable();
											OrderBook orderbook = new OrderBook();
											orderbook.setGid(optiAccounts.getGid());
											orderbook.setAction(3);
											orderbook.setStockid(stockid);
											orderbook.setVol(vol);
											orderbook.setInserttime(now);
											orderbook.setPrice(down_px);
											groupService.initOrderBook(orderbook);
											
											Traderec traderec = new Traderec();
											traderec.setGid(optiAccounts.getGid());
											traderec.setStockid(stockid);
											traderec.setVol(vol);
											traderec.setAction(3);
											traderec.setStatus(1);
											traderec.setInserttime(now);
											traderec.setOrderprice(down_px);
											groupService.initTraderec(traderec);
											
											
											optiPosi.setAvailable(optiPosi.getAvailable() - vol);
											optiPosi.setFrozen(optiPosi.getFrozen() + vol);
											groupService.updatePosi(optiPosi);
										}
									}
									break;
								default:
									break;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
				logger.debug("~~~~~~~~~~~~~~optiAccounts执行中，处理完Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
			}
		}
		logger.debug("~~~~~~~~~~~~~~ optiAccounts执行完毕，共处理：" + accountsList.size() + " 个Accounts~~~~~~~~~~~~~~");
	}
	
	public void updateWebStat() {
		List<Accounts> accountsList = groupService.getAllWebAccounts();
		Date now = new Date();
		for(Accounts accounts:accountsList)
		{
			String token = getDefaultToken();
			if(null != accounts) {
				
				logger.debug("~~~~~~~~~~~~~~updateWebStat执行中，正在处理Accounts：" + accounts.getGid()+"~~~~~~~~~~~~~~");
				
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
						accounts = groupService.getAccounts(accounts);
						accounts.setTotal(accounts.getAvailable() + accounts.getFrozen() + totalStock);
						accounts.setStock(totalStock);
		
					}catch(JSONException e) {
						token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
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
				updateState.setRa(((accounts.getTotal() / accounts.getInit()) - 1) * 100);
				groupService.updateStat(updateState);
			}
			
			logger.debug("~~~~~~~~~~~~~~ updateWebStat执行中，处理完Accounts：" + accounts.getGid()+"~~~~~~~~~~~~~~");
		}
		
		logger.debug("~~~~~~~~~~~~~~ updateWebStat执行完毕，共处理：" + accountsList.size() + " 个Accounts~~~~~~~~~~~~~~");
	}
	
	
	public void advanceAccounts() {
		List<Accounts> accountsList = groupService.getAllAdvanceAccounts();//attr=9//attr=12
		Date now = new Date();
		if(!accountsList.isEmpty()) {
			for(int i = 0; i < accountsList.size(); i++) {
				String token = getDefaultToken();
				Accounts mainAccounts = accountsList.get(i);
				
				//量化短线王1号组合（gid=6552）是手动操作，要跳过（attr=12）
				if(mainAccounts.getAttr() == 12 && mainAccounts.getGid() == 6552) {
					continue;
				}
				
				logger.debug("~~~~~~~~~~~~~~ advanceAccounts执行中，正在处理Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				
				if(mainAccounts.getAttr() == 9) {
					Order order = new Order();
					order.setUserId(mainAccounts.getUserid());
					order.setTradeType(CashController.PAY_CONFIGURATION);
					Order payOrder = cashService.hasStrategyOrder(order);
					order.setTradeType(CashController.TRY_CONFIGURATION);
					Order tryOrder = cashService.hasStrategyOrder(order);
					if(null == payOrder) {
						if(null == tryOrder) {
							continue;
						}
						else {
							if(Time.compareTime(now, tryOrder.getTimeExpire()) > 0) {
								continue;
							}
						}
					}
					else {
						if(null == tryOrder) {
							if(Time.compareTime(now, payOrder.getTimeExpire()) > 0) {
								continue;
							}
						}
						else {
							if(Time.compareTime(now, tryOrder.getTimeExpire()) > 0 && Time.compareTime(now, payOrder.getTimeExpire()) > 0) {
								continue;
							}
						}
					}
				}
				
//				User user = userService.selectUserById(mainAccounts.getUserid());
//				UserMeta userMeta = new UserMeta();
//				userMeta.setUserId(mainAccounts.getUserid());
//				UserMeta meta = groupService.getUserMeta(userMeta);
//				double init_total = STOCK_AMOUNT / 10;
//				if(null != meta) {
//					if(meta.getGtype() == 0) {
//						init_total = STOCK_AMOUNT * 0.5 / 10;
//					}
//					else if(meta.getGtype() == 1) {
//						init_total = STOCK_AMOUNT * 0.7 / 10;
//					}
//					else if(meta.getGtype() == 2){
//						init_total = STOCK_AMOUNT / 10;
//					}
//					
//				}
				
				Posi t = new Posi();
				t.setGid(mainAccounts.getGid());
				List<Posi> posiList = groupService.getPosi(t); //获取持仓个数size
				double init_total = 0;
				int count = 0;         //统计下单数量
				List<Advance> advanceList = groupService.getAccountsAdvance(mainAccounts.getGid());
				Posi p = new Posi();
				//List<Posi> posiList = groupService.getPosi(p);
				for(Advance advance:advanceList) {
					String stockid = advance.getStockId();
					String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
					String result = GetHSTokenUtils.getBuySallPoint(stock);
					String real = GetHSTokenUtils.getReal(stock, token);
					JSONObject realObject = new JSONObject(real);
					int k=0;
					while(realObject.toString().contains("访问令牌无效或已过期!")){
						k++;
						token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
						real = GetHSTokenUtils.getReal(stock, token);
						realObject = new JSONObject(real);
						if(k>3){
							break;
						}
					 }
					try {
						JSONObject snapshot = realObject.getJSONObject("data").getJSONObject("snapshot");
						double last_px = snapshot.getJSONArray(stock).getDouble(2);
						double up_px = snapshot.getJSONArray(stock).getDouble(4);
						double down_px = snapshot.getJSONArray(stock).getDouble(5);
						double highPrice = snapshot.getJSONArray(stock).getDouble(7);
						double lowPrice = snapshot.getJSONArray(stock).getDouble(8);
					
						JSONObject jsonObject = new JSONObject(result);
						JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
						int len = candle.getJSONArray(stock).length();
						JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
						int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
						//停牌股票跳过
						if(last_px < 0.01 || highPrice < 0.01 || lowPrice < 0.01) {
							continue;
						}
						
						p.setGid(mainAccounts.getGid());
						p.setStockid(stockid);
						Posi posi = groupService.getStockPosi(p);
						int vol = 0;
						switch(buyorsall) {
						case 1:
							
							//判断有没有挂单，如果存在挂单就不再下单
							OrderBook ob = new OrderBook();
							ob.setGid(mainAccounts.getGid());
							ob.setStockid(stockid);
							List<OrderBook> obList = groupService.getOrderBookByGidAndStockid(ob);
							if(!obList.isEmpty()) {
								break;
							}
							
							if(null == posi) {
								int fenmu = 10 - posiList.size() - count;  //最多有10只股票
								if(fenmu <= 0 || fenmu > 10) {
									break;
								}
								init_total = mainAccounts.getAvailable() / fenmu;
								vol = (int)((init_total / (up_px * 1.0003 * 100)));
								double money = vol * up_px * 100 * 1.0003;
								if(vol > 0) {
									count++;   //下单数量加1
									OrderBook orderbook = new OrderBook();
									orderbook.setGid(mainAccounts.getGid());
									orderbook.setAction(1);
									orderbook.setStockid(stockid);
									orderbook.setVol(vol);
									orderbook.setInserttime(now);
									orderbook.setPrice(up_px);
									groupService.initOrderBook(orderbook);
									
									Traderec traderec = new Traderec();
									traderec.setGid(mainAccounts.getGid());
									traderec.setStockid(stockid);
									traderec.setVol(vol);
									traderec.setAction(1);
									traderec.setStatus(1);
									traderec.setInserttime(now);
									traderec.setOrderprice(up_px);
									groupService.initTraderec(traderec);
									
									mainAccounts.setAvailable(mainAccounts.getAvailable() - money);
									mainAccounts.setFrozen(mainAccounts.getFrozen() + money);
									groupService.updateAccounts(mainAccounts);
								}
							}
							break;
						case 2:
							if(null != posi) {
								if(posi.getAvailable() > 0) {
									vol = posi.getAvailable();
									OrderBook orderbook = new OrderBook();
									orderbook.setGid(mainAccounts.getGid());
									orderbook.setAction(3);
									orderbook.setStockid(stockid);
									orderbook.setVol(vol);
									orderbook.setInserttime(now);
									orderbook.setPrice(down_px);
									groupService.initOrderBook(orderbook);
									
									Traderec traderec = new Traderec();
									traderec.setGid(mainAccounts.getGid());
									traderec.setStockid(stockid);
									traderec.setVol(vol);
									traderec.setAction(3);
									traderec.setStatus(1);
									traderec.setInserttime(now);
									traderec.setOrderprice(down_px);
									groupService.initTraderec(traderec);
									
									
									posi.setAvailable(posi.getAvailable() - vol);
									posi.setFrozen(posi.getFrozen() + vol);
									groupService.updatePosi(posi);
								}
							}
							break;
						default:
							break;
						}
					

						
					}catch (Exception e) {
						e.printStackTrace();
						token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
					}
				}
				
				logger.debug("~~~~~~~~~~~~~~ advanceAccounts执行中，处理完Accounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
			}
		}
		
		logger.debug("~~~~~~~~~~~~~~ advanceAccounts执行完毕，共处理：" + accountsList.size() + " 个Accounts~~~~~~~~~~~~~~");
	}
	
	//上证指数出现“升/跌”信号时，向所有用户推送
	public void sendToAllUser() {
		
		String stock = "000001.SS";
		String result = GetHSTokenUtils.getBuySallPoint(stock);
		JSONObject jsonObject = new JSONObject(result);
		JSONObject candle = jsonObject.getJSONObject("data").getJSONObject("candle");
		int len = candle.getJSONArray(stock).length();
		JSONArray stockArray = candle.getJSONArray(stock).getJSONArray(len -1);
		Integer time = stockArray.getInt(0);    //时间
		int buyorsall = stockArray.getInt(1);	//1是买点， 2是卖点
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);    //获取前一天日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String yestodayStr = sdf.format(cal.getTime());
		
		//如果昨日上证指数出现买卖点 就发送消息给所有用户
		if(time.toString().equals(yestodayStr)) {
		
			Integer total = userService.getUserCount();
			Integer forIndex = getForIndex(total);
			List<List<String>> listOfTask = new ArrayList<List<String>>();
			
			if(forIndex > 0){
				for(int i=1; i<=forIndex; i++){
					Integer pageCount = (i-1)*100;
					List<String> listOfEasemodId = userService.selectUserEasemodId(pageCount);
					listOfTask.add(listOfEasemodId);
//					List<String> testListModId = new ArrayList<String>();
//					testListModId.add("p4phili");
//					testListModId.add("lhiy4zv");
//					testListModId.add("dp8l1syy");
//					testListModId.add("3bx4fzxt");
//					testListModId.add("vtqs2u2");
//					listOfTask.add(testListModId);
				}
				
				ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);  
				for (int i = 0; i < listOfTask.size(); i++) {  
					final int index = i;  
					fixedThreadPool.execute(new Runnable() {
						List<String> list = listOfTask.get(index);
						@Override
						public void run() {
							try {
								String message = "";
								if(buyorsall == 1) {
									message = "$上证指数(1A0001.SS)$" + " 已出现中期上升信号，请注意及时加仓。";
								} else if(buyorsall == 2) {
									message = "$上证指数(1A0001.SS)$" + " 已出现中期下跌信号，请注意及时减仓";
								} else {
									return;
								}
								sendMessage(list, message);
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}  
						}
						
					});
				}
				fixedThreadPool.shutdown();
			}else{
				
			}
		}
		
		
	}
	
	//当用户使用“组合优化”功能后，每隔1个月(每个月1号)，主动在“私人助手”中推送
	public void sendOptiAccountsMonthProfit(Date date) {
		
		List<Accounts> accountsList = groupService.getAllOptiedAccountsList();
		if(accountsList != null) {
			for(int i = 0; i < accountsList.size(); i++) {   //accountsList.size()
				//i = 6;
				Accounts mainAccount = accountsList.get(i);
				Accounts accounts =new Accounts();
				accounts.setGid(mainAccount.getOptigid());
				Accounts optiAccount = groupService.getAccounts(accounts);
				if(optiAccount == null) {
					continue;
				}
				int between = Time.daysBetween(optiAccount.getCreatetime(), date);
				if(between == 0) {
					continue;
				}
				
				//原组合、优化组合收益计算
				double mainProfit = (mainAccount.getTotal() - STOCK_AMOUNT) / 10000;
				double optiProfit = (optiAccount.getTotal() - STOCK_AMOUNT) / 10000;
				
				//保留两位小数 四舍五入
				BigDecimal bgMain = new BigDecimal(mainProfit).setScale(2, RoundingMode.UP);
				BigDecimal bgOpti = new BigDecimal(optiProfit).setScale(2, RoundingMode.UP);
				
				//原组合、优化组合风险比计算，周期30天(最高收益/最大回撤)
				double mainRisk = 0;
				double optiRisk = 0;
				double mainMaxProfit = 0;
				double optiMaxProfit = 0;
				double highPoint = 0;
				double lowPoint = 0;
				Score statScore = new Score();
				statScore.setGid(mainAccount.getGid());
				//原组合30日收益
				List<Score> r30dayScoreList = groupService.getR30dayScoreList(statScore);
				if(r30dayScoreList != null) {
					int num = r30dayScoreList.size() - 1;
					int startIndex = 0;
					Score tempScore = r30dayScoreList.get(num);
					highPoint = tempScore.getTotal();
					lowPoint = tempScore.getTotal();
				
					//求最高收益
					for(int k = 0; k <= num; k ++) {
						mainMaxProfit = Math.max(mainMaxProfit, r30dayScoreList.get(k).getTotal());
					}
					
					for(int j = num - 1; j >= 0 ; j--) {
						tempScore = r30dayScoreList.get(j);
						if(tempScore.getTotal() >= highPoint) {
							highPoint = tempScore.getTotal();
							lowPoint = tempScore.getTotal();
							continue;
						} else {
							startIndex = j + 1;
							for(int k = j ; k >= 0; k--) {
								if(k == 0) {
									mainRisk =Math.max(mainRisk, r30dayScoreList.get(startIndex).getTotal() - r30dayScoreList.get(0).getTotal());
									j = 0;
									break;
								}
								tempScore = r30dayScoreList.get(k);
								if(tempScore.getTotal() <= lowPoint) {
									lowPoint = tempScore.getTotal();
									continue;
								} else {
									double t = r30dayScoreList.get(startIndex).getTotal() - r30dayScoreList.get(k + 1).getTotal();
									if(mainRisk < t) {
										mainRisk = t;
									}
									highPoint = tempScore.getTotal();
									lowPoint = tempScore.getTotal();
									j = k;
									break;
								}
							}
						}
					}
				}
				
				//优化组合30日风险比
				highPoint = 0;
				lowPoint = 0;
				statScore.setGid(optiAccount.getGid());
				List<Score> optir30dayScoreList = groupService.getR30dayScoreList(statScore);
				if(optir30dayScoreList != null) {
					int num = optir30dayScoreList.size() - 1;
					int startIndex = 0;
					Score tempScore = optir30dayScoreList.get(num);
					highPoint = tempScore.getTotal();
					lowPoint = tempScore.getTotal();
				
					//求最高收益
					for(int k = 0; k <= num; k ++) {
						optiMaxProfit = Math.max(optiMaxProfit, optir30dayScoreList.get(k).getTotal());
					}
					
					for(int j = num - 1; j >= 0 ; j--) {
						tempScore = optir30dayScoreList.get(j);
						if(tempScore.getTotal() >= highPoint) {
							highPoint = tempScore.getTotal();
							lowPoint = tempScore.getTotal();
							continue;
						} else {
							startIndex = j + 1;
							for(int k = j ; k >= 0; k--) {
								if(k == 0) {
									optiRisk =Math.max(optiRisk, optir30dayScoreList.get(startIndex).getTotal() - optir30dayScoreList.get(0).getTotal());
									j = 0;
									break;
								}
								tempScore = optir30dayScoreList.get(k);
								if(tempScore.getTotal() <= lowPoint) {
									lowPoint = tempScore.getTotal();
									continue;
								} else {
									double t = optir30dayScoreList.get(startIndex).getTotal() - optir30dayScoreList.get(k + 1).getTotal();
									if(optiRisk < t) {
										optiRisk = t;
									}
									highPoint = tempScore.getTotal();
									lowPoint = tempScore.getTotal();
									j = k;
									break;
								}
							}
						}
					}
				}
				
				String mainRiskStr = "";
				String optiRiskStr = "";
				
				if(mainRisk != 0) {
					mainRisk = mainMaxProfit / mainRisk / 100;
				}
				
				if(optiRisk != 0) {
					optiRisk = optiMaxProfit / optiRisk / 100;
				}
				
				
				//风险收益比保留两位小数
				BigDecimal bgmain = new BigDecimal(mainRisk).setScale(2, RoundingMode.UP);
				BigDecimal bgopti = new BigDecimal(optiRisk).setScale(2, RoundingMode.UP);
				
				if(mainRisk == 0) {
					mainRiskStr = "∞";
				} else {
					mainRiskStr = Double.toString(bgmain.doubleValue()) + "%";
				}
				
				if(optiRisk == 0) {
					optiRiskStr = "∞";
				} else {
					optiRiskStr = Double.toString(bgopti.doubleValue()) + "%";
				}
				
				//发送消息
				User user = userService.selectUserById(mainAccount.getUserid());
				List<String> pushUserList = new ArrayList<String>();
				pushUserList.add(user.getEasemobId());
				
				String msg = "目前距离您启用股哥智能机器人对组合进行优化已有"+ (between + 1) + "天,股哥智能机器人的整体收益率为"+ bgOpti +"%,您自己的操盘收益率为"+ bgMain + "%。" + "本月您的收益风险比为" + mainRiskStr +",股哥智能机器人的收益风险比为" + optiRiskStr;
				sendMessage(pushUserList, msg);
			}
		}
		
	}
	
	//发送消息方法
	private static void sendMessage(List<String> list, String msg) {
		ObjectNode ext = JsonNodeFactory.instance.objectNode();
		String targetType = "users";
		String adminEasemobId = "lbh3zyi";
		SendMessageMethods.sendTxtMsg(targetType, list, adminEasemobId, ext, msg);
	}
	
	public static Integer getForIndex(Integer total){
		Integer forIndex = 1;
		if(total > 0){
			if(total >= 100){
				Integer value = total%100;
				if(value>0){
					forIndex = total/100 + 1;
				}else{
					forIndex = total/100 ;
				}
			}
		}else{
			forIndex = 0;
		}
		return forIndex;
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
