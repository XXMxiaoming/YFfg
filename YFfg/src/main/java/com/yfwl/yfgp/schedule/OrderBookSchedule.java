package com.yfwl.yfgp.schedule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tencent.xinge.ClickAction;
import com.tencent.xinge.Message;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.Style;
import com.tencent.xinge.XingeApp;
import com.yfwl.yfgp.controller.BaseController;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.AccountsMeta;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.Traderec;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.Optitask;
import com.yfwl.yfgp.model.Yhzhsend;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.service.YhzhsendService;
import com.yfwl.yfgp.utils.AccountUtil;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.Time;

@Component
public class OrderBookSchedule extends BaseController{
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);
	@Resource
	GroupService groupService;
	@Resource
	TokenService tokenService;
	@Resource
	CashService cashService;
	@Resource
	UserService userService;
	@Resource
	YhzhsendService yhzhsendService;
	private final static float PERCENT = 0.8f;
	
	private final static int SIZE = 60;
	private final static int MIN_SIZE = 10;
	private final static int MIN_LEFT = 10;
	//初始金额
	private final static double INITIAL_AMOUNT = 1000000.f;
	private static Logger logger = OrderBookSchedule.getLogger("d:/logs/yfgp/schedule/OrderBookSchedule_debug_");
	/**
	 * 订单定时操作
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
			if((hour == 9 && minute > 30) || ( hour == 11 && minute <30) || (hour == 10 || (hour >=13 && hour < 15))) {
				
				logger.debug("============================orderbook type1 开始执行============================");
				
				logger.debug("======================clearOrder方法  开始执行=====================");
				clearOrder();
				logger.debug("======================clearOrder方法  结束执行======================");
				
				logger.debug("======================updateSingleAccounts方法  开始执行======================");
				updateSingleAccounts();
				logger.debug("======================updateSingleAccounts方法  结束执行======================");
				
				logger.debug("======================updateAccounts方法  开始执行======================");
				updateAccounts();
				logger.debug("======================updateAccounts方法  结束执行======================");
				
				logger.debug("======================updateOrder方法  开始执行======================");
				updateOrder();
				logger.debug("======================updateOrder方法  结束执行======================");
				
				logger.debug("============================orderbook type1 结束执行============================");
			}
			else if(hour == 9 && minute <= 30){
				
			}
			else {
				logger.debug("============================orderbook type2 开始执行============================");
				
				logger.debug("======================updateOrder方法  开始执行======================");
				updateOrder();
				logger.debug("======================updateOrder方法  结束执行======================");
				
				logger.debug("============================orderbook type2 结束执行============================");
			}
			
		}
	}
	
	public void clearOrder() {
		List<OrderBook> orderList = groupService.getAllOrderBookList();
		String token = getDefaultToken();
		if(!orderList.isEmpty()) {
			for(int i = 0; i < orderList.size(); i++) {
				OrderBook  orderBook = orderList.get(i);
				
				logger.debug("~~~~~~~~~~~~~~clearOrder执行中，正在处理订单 " + orderBook.getOrderid()+"~~~~~~~~~~~~~~");
				
				String stockid = orderBook.getStockid();
				String stock = stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
				String result = GetHSTokenUtils.getReal(stock, token);
				JSONObject jsonObject = new JSONObject(result);
				String stockName = "";
				String atype = "";
				int z=0;
				while(jsonObject.toString().contains("访问令牌无效或已过期!")){
					z++;
					token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
					result = GetHSTokenUtils.getReal(stock, token);
					jsonObject = new JSONObject(result);
					if(z>3){
						break;
					}
				 }
				try {
					JSONObject snapshot = jsonObject.getJSONObject("data").getJSONObject("snapshot");
					double last_px = snapshot.getJSONArray(stock).getDouble(2);//最新价
					double up_px = snapshot.getJSONArray(stock).getDouble(4);//涨停价
					double down_px = snapshot.getJSONArray(stock).getDouble(5);//跌停价
					stockName = snapshot.getJSONArray(stock).getString(6);
					if(last_px < 0.01) {
						continue;
					}
					Date inserttime = new Date();
					double percent = 0;
					switch(orderBook.getAction()) {
						case 1:
							if(orderBook.getPrice() >= last_px && last_px != up_px) {
								/**
								 * 删除订单
								 */
								
								Integer hasDelete = groupService.deleteOrderBook(orderBook);
								if(hasDelete > 0) {
									
								}
								else {
									continue;
								}
								double fee = orderBook.getVol() * last_px * 100 * 0.0003; //万分之三的手续费
								Posi posi = new Posi();
								posi.setGid(orderBook.getGid());
								posi.setStockid(stockid);
								Posi mainPosi = groupService.getStockPosi(posi);
								if(null != mainPosi) {
									mainPosi.setPrice((mainPosi.getVol() * mainPosi.getPrice() + orderBook.getVol() * last_px) / (mainPosi.getVol() + orderBook.getVol()));
									mainPosi.setVol(mainPosi.getVol() + orderBook.getVol());
									mainPosi.setOnway(mainPosi.getOnway() + orderBook.getVol());
									groupService.updatePosi(mainPosi);
								}
								else {
									posi.setVol(orderBook.getVol());
									posi.setPrice(last_px);
									posi.setFrozen(0);
									posi.setAvailable(0);
									posi.setOnway(orderBook.getVol());
									groupService.initPosi(posi);
								}
								
								/**
								 * 更新账户信息
								 */
								Accounts accounts = new Accounts();
								accounts.setGid(orderBook.getGid());
								Accounts mainAccounts = groupService.getAccounts(accounts);
								if(null != mainAccounts) {
									double totalFrozen = mainAccounts.getFrozen();
									double stockFrozen = orderBook.getVol() * orderBook.getPrice() * 100 * 1.0003;
									double orderTrade =  orderBook.getVol() * last_px * 100;
									double leftFrozen = totalFrozen - stockFrozen;
									//System.out.println(leftFrozen);
									percent = orderTrade / mainAccounts.getTotal();	//购买比例
									if(leftFrozen < 0.02) {
										leftFrozen = 0;
									}
									mainAccounts.setTotal(mainAccounts.getTotal() - fee);
									mainAccounts.setAvailable(mainAccounts.getAvailable() + stockFrozen - orderTrade -fee);
									mainAccounts.setStock(mainAccounts.getStock() + orderTrade);
									mainAccounts.setFrozen(leftFrozen);
									groupService.updateAccounts(mainAccounts);
									
									//记录有优化组合的主组合的买操作记录
									if(mainAccounts.getAttr() == 6) {
										Optitask optitask = new Optitask();
										optitask.setGid(mainAccounts.getGid());
										optitask.setStockid(stockid);
										optitask.setOperation(1);
										optitask.setType(0);
										optitask.setStatus(0);
										optitask.setCreatetime(inserttime);
										Optitask existOptitask = groupService.getOptibuytask(optitask);
										if(existOptitask == null) {
											groupService.insertOptitask(optitask);
										}
									}
								
									//如果是优化组合(attr=5)进行买入操作，要发送消息给拥有该优化组合的人
									//“X年X月X日X时X分，股哥智能机器人启动，对XXXX（代码）进行了优化操作，操作方向为买入/卖出，买入/卖出数量为：YY股。目前股哥智能机器人的整体收益率为X%，您自己的操盘收益率为Y%”
									if(mainAccounts.getAttr() == 5)
									{
										Calendar cal = Calendar.getInstance();
										int y = cal.get(Calendar.YEAR);
										int m = cal.get(Calendar.MONTH) + 1;
										int d = cal.get(Calendar.DAY_OF_MONTH);
										int h = cal.get(Calendar.HOUR_OF_DAY);
										int mi = cal.get(Calendar.MINUTE);
										String dateStr = y + "年" + m + "月" + d + "日" + h + "时" + mi + "分";
						
										//优化组合 原组合收益
										double yhzhProfit = (mainAccounts.getTotal() - INITIAL_AMOUNT) / 10000;
										Accounts temp = new Accounts();
										temp.setGid(mainAccounts.getOptigid());
										Accounts yzh = groupService.getAccounts(temp);
										double yzhProfit = (yzh.getTotal() - INITIAL_AMOUNT) / 10000;
										System.out.println(yzhProfit+"ghdskjghkjfd");
										//保留两位小数 四舍五入
										BigDecimal bgYhzh = new BigDecimal(yhzhProfit).setScale(2, RoundingMode.UP);
										BigDecimal bgYzh = new BigDecimal(yzhProfit).setScale(2, RoundingMode.UP);
										atype = ",1";
										User user = userService.selectUserById(mainAccounts.getUserid());
//										ObjectNode txtmsg = factory.objectNode();
//										List<String> pushUserList = new ArrayList<String>();
//										pushUserList.add(user.getEasemobId());
//										//"您的优化组合买入提示:#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "买入" + "$" + stockName + "(" + stock + ")$"+ orderBook.getVol() * 100 + "股,成交价格:" + last_px
//										String msg = dateStr + ",股哥智能机器人启动,对您 #"+ mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#"+" 组合中的" + " $" + stockName + "(" + stock + ")$ " +"进行了优化操作,操作方向为买入," + "买入数量为" + orderBook.getVol() * 100 + "股,交易价格为" + last_px + "元。目前股哥智能机器人的整体收益率为" +bgYhzh.doubleValue()+ "%,您自己的操盘收益率为"+bgYzh.doubleValue()+"%";
//								        txtmsg.put("type","txt");
//								        ObjectNode ext = JsonNodeFactory.instance.objectNode();
//								        sendEaseMobMsg(pushUserList, ext, msg);
								      

										SimpleDateFormat xgsdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
										Date datenow = new Date();
										String xgtime = xgsdf.format(datenow);
										
								        XingeApp push=new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
								        Map<String,Object> map2=new HashMap<String, Object>();
										MessageIOS IosMess=new MessageIOS();
										Yhzhsend yhzhsend=new Yhzhsend();
										yhzhsend.setUserid(mainAccounts.getUserid());
										yhzhsend.setBgYhzh(bgYhzh.doubleValue());
										yhzhsend.setBgYzh(bgYzh.doubleValue());
										yhzhsend.setBuyOrSell("1");
										yhzhsend.setDatestr(dateStr);
										yhzhsend.setGid(mainAccounts.getGid().toString());
										yhzhsend.setGname(mainAccounts.getGname());
										yhzhsend.setLast_px(last_px);
										yhzhsend.setStock(stock);
										yhzhsend.setStockName(stockName);
										yhzhsend.setTime(xgtime);
										yhzhsend.setType("2");
										yhzhsend.setVol(orderBook.getVol() * 100);
										
										Integer count = yhzhsendService.insertYhzhsend(yhzhsend);
										Integer id=yhzhsend.getId();
									
											map2.put("time", xgtime);
											map2.put("type", "2");//2代表优化组合
									        map2.put("dateStr", dateStr);
									        map2.put("gname", mainAccounts.getGname());
									        map2.put("gid", mainAccounts.getGid().toString());
									        map2.put("stockName", stockName);
									        map2.put("stock", stock);
									        map2.put("vol", orderBook.getVol() * 100);
									        map2.put("last_px", last_px);
									        map2.put("buyOrSell", "1");
									        map2.put("bgYhzh", bgYhzh.doubleValue());
									        map2.put("bgYzh", bgYzh.doubleValue());
									        map2.put("id", id);
									     // System.out.println(map2+"gjfdlgkjfdl");
								        IosMess.setAlert("您有一条新消息");
								    
								        IosMess.setCustom(map2);
									
										push.pushSingleAccount(0,mainAccounts.getUserid().toString(),IosMess,XingeApp.IOSENV_PROD);
										
										
										
										XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID, XingeApp.ANDRIOD_MYKEY);
										Message mess=new Message();
										mess.setTitle("优化组合");
										mess.setStyle(new Style(0,1,1,0,0));
										mess.setType(Message.TYPE_NOTIFICATION);
										mess.setContent("您有一条新消息");
										mess.setCustom(map2);
										ClickAction action=new ClickAction();
										action.setActivity("com.yfnetwork.yffg.xgpush.PushMsgActivity");
										mess.setAction(action);
										push2.pushSingleAccount(0,mainAccounts.getUserid().toString(),mess);
								
									}
									User user = userService.selectUserById(mainAccounts.getUserid());
									if(user.getUserId() == 36165 || user.getUserId() ==38779 || user.getUserId()==38492 || user.getUserId()==127885) {
										ObjectNode txtmsg = factory.objectNode();
										List<String> pushUserList = new ArrayList<String>();
										pushUserList.add("ipvnp5j");
										String msg = "&" + user.getUserName() + "&在组合#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "买入" + "$" + stockName + "(" + stock + ")$,成交价格:" + last_px;
								        txtmsg.put("type","txt");
								        ObjectNode ext = JsonNodeFactory.instance.objectNode();
								        sendEaseMobMsg(pushUserList, ext, msg);
									}
									
									List<Relation> relationList = null;
									if(mainAccounts.getAttr() == 12) {
										relationList = new ArrayList<Relation>();
										Order order = new Order();
										order.setDetail(orderBook.getGid().toString());
										List<String> temp = groupService.getFollowFromOrderMapper(order);
										if(!temp.isEmpty()) {
											for(int k = 0; k < temp.size(); k++) {
												Relation rt = new Relation();
												rt.setUid(Integer.parseInt(temp.get(k)));
												relationList.add(rt);
											}
										
										}
									} else {
										Relation relation = new Relation();
										relation.setGid(orderBook.getGid());
										relationList = groupService.getAllRelation(relation);
									}
									
									
									if(!relationList.isEmpty()) {
								        List<String> pushUserList = new ArrayList<String>();
								        for(int r = 0; r < relationList.size(); r++) {
								        	User followUser = userService.selectUserById(relationList.get(r).getUid());
								        	if(null != followUser) {
								        		if(followUser.getUserStatus().equals(4)) {
								        			continue;
								        		}
								        		pushUserList.add(followUser.getEasemobId());
								        	}
								        }
								        if(!mainAccounts.getAttr().equals(7)) {
								        	atype = ",1";
								        }
								        if(mainAccounts.getAttr().equals(12)) {
								        	atype = ",2";
								        }
								        double zb = (double)(Math.round(orderBook.getVol() * last_px * 100 * 100  / mainAccounts.getTotal() * 100)) / 100;
								        double[] zbArray = {99.34,98.99,99.45,99.33};
								        if(zb >= 100){
								        	int index = (int)((Math.random())*4);
								        	zb = zbArray[index];
								        }
								        
								        ObjectNode txtmsg = factory.objectNode();
										String msg = "&" + user.getUserName() + "&在组合#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "买入" + "$" + stockName + "(" + stock + ")$ " + orderBook.getVol() * 100 + " 股,仓位占比" + zb + "%,成交价格:" + last_px;
								        txtmsg.put("type","txt");
								        if(mainAccounts.getAttr().equals(12)) {
								        	msg = "#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "买入" + "$" + stockName + "(" + stock + ")$ "+ orderBook.getVol() * 100 +" 股,仓位占比" + zb + "%,成交价格:" + last_px;
								        }
								        ObjectNode ext = JsonNodeFactory.instance.objectNode();
								        sendEaseMobMsg(pushUserList, ext, msg);
									}
								}
								
								
								
								/**
								 * 更新交易纪录
								 */
								Traderec traderec = new Traderec();
								traderec.setGid(orderBook.getGid());
								traderec.setAction(orderBook.getAction());
								traderec.setInserttime(orderBook.getInserttime());
								traderec.setStockid(orderBook.getStockid());
								traderec.setOrderprice(orderBook.getPrice());
								traderec.setVol(orderBook.getVol());
								traderec.setTradetime(inserttime);
								traderec.setTradeprice(last_px);
								traderec.setCharge(fee);
								traderec.setStatus(3);
								groupService.initTraderec(traderec);
								
								
								
								
								/**
								 * 更新跟投组合
								 */
								double tradeAmount;
								Integer tradeVol;
								Integer tradeGid;
								Relation relation = new Relation();
								relation.setAttr(2);
								relation.setDel(0);
								relation.setGid(orderBook.getGid());
								List<Relation> relationList = groupService.selectFollowedRelation(relation);
								if(!relationList.isEmpty()) {
									for(int j = 0; j < relationList.size(); j++ ) {
										Relation mainRelation = relationList.get(j);
										tradeAmount = mainRelation.getAmount() * percent;
										tradeVol = (int)Math.floor((tradeAmount/1.0003)/last_px/100);
										tradeGid = mainRelation.getSubgid();
										if(tradeAmount > mainRelation.getAccounts().getAvailable() || tradeVol == 0) {
											continue;
										}
										else {
											fee = tradeVol * last_px * 100 * 0.0003;
											
											Posi subposi = new Posi();
											subposi.setGid(tradeGid);
											subposi.setStockid(stockid);
											Posi submainPosi = groupService.getStockPosi(subposi);
											if(null != submainPosi) {
												submainPosi.setPrice((submainPosi.getVol() * submainPosi.getPrice() + tradeVol * last_px) / (submainPosi.getVol() + tradeVol));
												submainPosi.setVol(submainPosi.getVol() + tradeVol);
												submainPosi.setOnway(submainPosi.getOnway() + tradeVol);
												groupService.updatePosi(submainPosi);
											}
											else {
												subposi.setVol(tradeVol);
												subposi.setPrice(last_px);
												subposi.setFrozen(0);
												subposi.setAvailable(0);
												subposi.setOnway(tradeVol);
												groupService.initPosi(subposi);
											}
											
											
											/**
											 * 更新账户信息
											 */
											Accounts subaccounts = new Accounts();
											subaccounts.setGid(tradeGid);
											Accounts submainAccounts = groupService.getAccounts(subaccounts);
											if(null != submainAccounts) {
												double orderTrade =  tradeVol * last_px * 100;
												submainAccounts.setTotal(submainAccounts.getTotal() - fee);
												submainAccounts.setAvailable(submainAccounts.getAvailable() -orderTrade -fee);
												submainAccounts.setStock(submainAccounts.getStock() + orderTrade);
												groupService.updateAccounts(submainAccounts);
											}
											
											/**
											 * 更新交易纪录
											 */
											Traderec subtraderec = new Traderec();
											subtraderec.setGid(tradeGid);
											subtraderec.setCharge(fee);
											subtraderec.setAction(orderBook.getAction());
											subtraderec.setInserttime(orderBook.getInserttime());
											subtraderec.setStockid(orderBook.getStockid());
											subtraderec.setOrderprice(orderBook.getPrice());
											subtraderec.setVol(tradeVol);
											subtraderec.setTradetime(inserttime);
											subtraderec.setTradeprice(last_px);
											subtraderec.setStatus(3);
											groupService.initTraderec(subtraderec);
										}
									}
								}
							}
							break;
						case 3:
							if(orderBook.getPrice() <= last_px && last_px != down_px) {
								
								/**
								 * 删除订单
								 */
								Integer hasDelete = groupService.deleteOrderBook(orderBook);
								if(hasDelete > 0) {
									
								}
								else {
									continue;
								}
								
								double fee = orderBook.getVol() * last_px * 100 * 0.0013; //万分之十三的手续费
								double charge = orderBook.getVol() * last_px * 100 * 0.0003; //万分之三的手续费
								double stamp_tax = orderBook.getVol() * last_px * 100 * 0.001; //千分之一的印花税
								BigDecimal  b = new BigDecimal(stamp_tax);  
								stamp_tax = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
								//System.out.println(stamp_tax);
								
								
								/**
								 * 更新交易纪录
								 */
								Traderec traderec = new Traderec();
								traderec.setGid(orderBook.getGid());
								traderec.setAction(orderBook.getAction());
								traderec.setInserttime(orderBook.getInserttime());
								traderec.setStockid(orderBook.getStockid());
								traderec.setOrderprice(orderBook.getPrice());
								traderec.setCharge(charge);
								traderec.setStamp_tax(stamp_tax);
								traderec.setVol(orderBook.getVol());
								traderec.setTradetime(inserttime);
								traderec.setTradeprice(last_px);
								traderec.setStatus(3);
								groupService.initTraderec(traderec);
								
								Posi posi = new Posi();
								posi.setGid(orderBook.getGid());
								posi.setStockid(stockid);
								Posi mainPosi = groupService.getStockPosi(posi);
								if(null != mainPosi) {
									Accounts accounts = new Accounts();
									accounts.setGid(orderBook.getGid());
									percent = (double)orderBook.getVol() / mainPosi.getVol();
									Accounts mainAccounts = groupService.getAccounts(accounts);
									if(mainPosi.getVol() > orderBook.getVol() && mainPosi.getFrozen() >= orderBook.getVol()) {
										mainPosi.setPrice((mainPosi.getVol() * mainPosi.getPrice() - orderBook.getVol() * last_px) / (mainPosi.getVol() - orderBook.getVol()));
										mainPosi.setVol(mainPosi.getVol() - orderBook.getVol());
										mainPosi.setFrozen(mainPosi.getFrozen() - orderBook.getVol());
										groupService.updatePosi(mainPosi);
									}
									else if(mainPosi.getVol().equals(orderBook.getVol()) && mainPosi.getFrozen().equals(orderBook.getVol())) {
										groupService.deleteStockPosi(mainPosi);
//										
//										/**
//										 * 主组合清空持仓，优化组合清空
//										 */
//										if(null != mainAccounts && mainAccounts.getAttr() == 6) {
//											Posi op= new Posi(); 
//											op.setGid(mainAccounts.getOptigid());
//											op.setStockid(stockid);
//											Posi optiPosi = groupService.getStockPosi(op);
//											if(null != optiPosi) {
//												if(optiPosi.getAvailable() > 0) {
//													int vol = optiPosi.getAvailable();
//													OrderBook optiorderbook = new OrderBook();
//													optiorderbook.setGid(optiPosi.getGid());
//													optiorderbook.setAction(3);
//													optiorderbook.setStockid(stockid);
//													optiorderbook.setVol(vol);
//													optiorderbook.setInserttime(inserttime);
//													optiorderbook.setPrice(down_px);
//													groupService.initOrderBook(optiorderbook);
//													
//													Traderec optitraderec = new Traderec();
//													optitraderec.setGid(optiPosi.getGid());
//													optitraderec.setStockid(stockid);
//													optitraderec.setVol(vol);
//													optitraderec.setAction(3);
//													optitraderec.setStatus(1);
//													optitraderec.setInserttime(inserttime);
//													optitraderec.setOrderprice(down_px);
//													groupService.initTraderec(optitraderec);
//													
//													optiPosi.setAvailable(optiPosi.getAvailable() - vol);
//													optiPosi.setFrozen(optiPosi.getFrozen() + vol);
//													groupService.updatePosi(optiPosi);
//												}
//											}
//											
//										}
									}
									
									/**
									 * 消息推送
									 */
									
									if(null != mainAccounts) {
//										Relation relation2 = new Relation();
//										relation2.setGid(orderBook.getGid());
//										List<Relation> relationList2 = groupService.getAllRelation(relation2);
										
										List<Relation> relationList2 = null;
										if(mainAccounts.getAttr() == 12) {
											relationList2 = new ArrayList<Relation>();
											Order order = new Order();
											order.setDetail(orderBook.getGid().toString());
											List<String> temp = groupService.getFollowFromOrderMapper(order);
											if(!temp.isEmpty()) {
												for(int k = 0; k < temp.size(); k++) {
													Relation rt = new Relation();
													rt.setUid(Integer.parseInt(temp.get(k)));
													relationList2.add(rt);
												}
											
											}
										} else {
											Relation relation = new Relation();
											relation.setGid(orderBook.getGid());
											relationList2 = groupService.getAllRelation(relation);
										}
										
										//记录有优化组合的主组合的卖操作记录
										if(mainAccounts.getAttr() == 6) {
											Optitask optitask = new Optitask();
											optitask.setGid(mainAccounts.getGid());
											optitask.setStockid(stockid);
											optitask.setOperation(3);
											optitask.setType(0);
											optitask.setStatus(0);
											optitask.setCreatetime(inserttime);
											Accounts t = new Accounts();
											t.setGid(mainAccounts.getOptigid());
											Accounts optiAccount = groupService.getAccounts(t);
											if(optiAccount != null) {
												Posi p = new Posi();
												p.setGid(optiAccount.getGid());
												p.setStockid(stockid);
												Posi mainposi  = groupService.getStockPosi(p);
												if(mainposi != null) {
													groupService.insertOptitask(optitask);
//													Optitask existOptitask = groupService.getOptiselltask(optitask);
//													if(existOptitask == null) {
//														groupService.insertOptitask(optitask);
//													}
												}
											}
											
										}
										
										//如果是优化组合(attr=5)进行卖出操作，要发送消息给拥有该优化组合的人
										if(mainAccounts.getAttr() == 5)
										{
											Calendar cal = Calendar.getInstance();
											int y = cal.get(Calendar.YEAR);
											int m = cal.get(Calendar.MONTH) + 1;
											int d = cal.get(Calendar.DAY_OF_MONTH);
											int h = cal.get(Calendar.HOUR_OF_DAY);
											int mi = cal.get(Calendar.MINUTE);
											String dateStr = y + "年" + m + "月" + d + "日" + h + "时" + mi + "分";
											//优化组合 原组合收益
											double yhzhProfit = (mainAccounts.getTotal() - INITIAL_AMOUNT) / 10000;
											Accounts temp = new Accounts();
											temp.setGid(mainAccounts.getOptigid());
											Accounts yzh = groupService.getAccounts(temp);
											double yzhProfit = (yzh.getTotal() - INITIAL_AMOUNT) / 10000;
											//保留两位小数 四舍五入
											BigDecimal bgYhzh = new BigDecimal(yhzhProfit).setScale(2, RoundingMode.UP);
											BigDecimal bgYzh = new BigDecimal(yzhProfit).setScale(2, RoundingMode.UP);
											
											atype = ",1";
											User user = userService.selectUserById(mainAccounts.getUserid());
//											ObjectNode txtmsg = factory.objectNode();
//											List<String> pushUserList = new ArrayList<String>();
//											pushUserList.add(user.getEasemobId());
//											//"您的优化组合卖出提示:#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "卖出" + "$" + stockName + "(" + stock + ")$" + orderBook.getVol()*100 + "股,成交价格:" + last_px
//											String msg = dateStr + ",股哥智能机器人启动,对您 #"+ mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#"+" 组合中的" + " $" + stockName + "(" + stock + ")$ " + "进行了优化操作,操作方向为卖出," + "卖出数量为" + orderBook.getVol() * 100 + "股,交易价格为" + last_px + "元。目前股哥智能机器人的整体收益率为" +bgYhzh.doubleValue()+ "%,您自己的操盘收益率为"+bgYzh.doubleValue()+"%";
//									        txtmsg.put("type","txt");
//									        ObjectNode ext = JsonNodeFactory.instance.objectNode();
//									        sendEaseMobMsg(pushUserList, ext, msg);
									     
									        
									       
									     
											SimpleDateFormat xgsdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
											Date datenow = new Date();
											String xgtime = xgsdf.format(datenow);
											
											
											 Map<String,Object> map =new HashMap<String, Object>();
									        XingeApp push=new XingeApp(XingeApp.IOS_ID, XingeApp.IOS_MYKEY);
											MessageIOS iosMess=new MessageIOS();
											 Yhzhsend yhzhsend=new Yhzhsend();
												yhzhsend.setUserid(mainAccounts.getUserid());
												yhzhsend.setBgYhzh( bgYhzh.doubleValue());
												yhzhsend.setBgYzh(bgYzh.doubleValue());
												yhzhsend.setBuyOrSell("2");
												yhzhsend.setDatestr(dateStr);
												yhzhsend.setGid(mainAccounts.getGid().toString());
												yhzhsend.setGname(mainAccounts.getGname());
												yhzhsend.setLast_px(last_px);
												yhzhsend.setStock(stock);
												yhzhsend.setStockName(stockName);
												yhzhsend.setTime(xgtime);
												yhzhsend.setType("2");
												yhzhsend.setVol(orderBook.getVol() * 100);
												
												Integer count = yhzhsendService.insertYhzhsend(yhzhsend);
												Integer id=yhzhsend.getId();
									       // List<String> accountList = new ArrayList<String>();
									        iosMess.setAlert("您有一条新消息");
									    	map.put("time", xgtime);
									        map.put("type", "2");
									        map.put("dateStr", dateStr);
									        map.put("gname", mainAccounts.getGname());
									        map.put("gid", mainAccounts.getGid().toString());
									        map.put("stockName", stockName);
									        map.put("stock", stock);
									        map.put("vol", orderBook.getVol() * 100);
									        map.put("last_px", last_px);
									        map.put("buyOrSell", "2");
									        map.put("bgYhzh", bgYhzh.doubleValue());
									        map.put("bgYzh", bgYzh.doubleValue());
									        map.put("id", id);
									        
									        iosMess.setCustom(map);
									       // accountList.add(mainAccounts.getUserid().toString());
									        push.pushSingleAccount(0,mainAccounts.getUserid().toString(),iosMess,XingeApp.IOSENV_PROD);
											
											
											
											XingeApp push2 = new XingeApp(XingeApp.ANDRIOD_MAX_ID, XingeApp.ANDRIOD_MYKEY);
											Message mess=new Message();
											mess.setTitle("优化组合");
											mess.setStyle(new Style(0,1,1,0,0));
											mess.setType(Message.TYPE_NOTIFICATION);
											mess.setContent("您有一条新消息");
											Map<String, Object> map2=new HashMap<String, Object>();
											mess.setCustom(map);
											ClickAction action=new ClickAction();
											action.setActivity("com.yfnetwork.yffg.xgpush.PushMsgActivity");
											mess.setAction(action);
											push2.pushSingleAccount(0,mainAccounts.getUserid().toString(),mess);

										}
										
										User user = userService.selectUserById(mainAccounts.getUserid());
										if(user.getUserId() == 36165 || user.getUserId() ==38779 || user.getUserId()==38492 || user.getUserId()==127885) {
											ObjectNode txtmsg = factory.objectNode();
											List<String> pushUserList = new ArrayList<String>();
											pushUserList.add("ipvnp5j");
											String msg = "&" + user.getUserName() + "&在组合#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "卖出" + "$" + stockName + "(" + stock + ")$" + orderBook.getVol()*100 + "股,成交价格:" + last_px;
									        txtmsg.put("type","txt");
									        ObjectNode ext = JsonNodeFactory.instance.objectNode();
									        sendEaseMobMsg(pushUserList, ext, msg);
										}
										if(!relationList2.isEmpty()) {
											List<String> pushUserList = new ArrayList<String>();
									        for(int r = 0; r < relationList2.size(); r++) {
									        	User followUser = userService.selectUserById(relationList2.get(r).getUid());
									        	if(null != followUser) {
									        		if(followUser.getUserStatus().equals(4)) {
									        			continue;
									        		}
									        		pushUserList.add(followUser.getEasemobId());
									        	}
									        }
									        //attr=7表示收费组合 atype=""
									        if(!mainAccounts.getAttr().equals(7)) {
									        	atype = ",1";//普通组合标志
									        }
									        String msg = "&" + user.getUserName() + "&在组合#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "卖出" + "$" + stockName + "(" + stock + ")$" + orderBook.getVol()*100 + "股,成交价格:" + last_px;
									        if(mainAccounts.getAttr().equals(12)) {
									        	atype = ",2";
									        	msg = "#" + mainAccounts.getGname() +"("+ mainAccounts.getGid() + atype +")#" + "卖出" + "$" + stockName + "(" + stock + ")$" + orderBook.getVol()*100 + "股,成交价格:" + last_px;
									 
									        }
									        ObjectNode txtmsg = factory.objectNode();
											
									        txtmsg.put("type","txt");
									        ObjectNode ext = JsonNodeFactory.instance.objectNode();
									        sendEaseMobMsg(pushUserList, ext, msg);
										}
									}
									
									/**
									 * 更新账户信息
									 */
									if(null != mainAccounts) {
										try {
											double totalStock = 0;
											Posi posiNow = new Posi();
											posiNow.setGid(orderBook.getGid());
											List<Posi> posiList = groupService.getPosi(posiNow);
											if(!posiList.isEmpty()) {
												String stockStr = "";
												for(int j = 0; j < posiList.size(); j++ ) {
													String stockString = posiList.get(j).getStockid();
													String stocklist = stockString.startsWith("6")?stockString + ".SS":stockString + ".SZ";
													if(i == posiList.size() -1) {
														stockStr += stocklist;
													}
													else {
														stockStr += stocklist + ",";
													}
												}
												String resultStr = GetHSTokenUtils.getReal(stockStr, token);
												//System.out.println(resultStr);
												JSONObject jsonData = new JSONObject(resultStr);
												JSONObject snapshotData = jsonData.getJSONObject("data").getJSONObject("snapshot");
												double realPrice;
												
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
												/*double total = mainAccounts.getAvailable() + mainAccounts.getFrozen() + totalStock;
												mainAccounts.setStock(totalStock);
												mainAccounts.setTotal(total);*/
											}
											
											double total = mainAccounts.getAvailable() + mainAccounts.getFrozen() + totalStock + orderBook.getVol() * last_px * 100 -fee;
											mainAccounts.setStock(totalStock);
											mainAccounts.setTotal(total);
										} catch (Exception e) {
											throw e;
										} finally {
											mainAccounts.setAvailable(mainAccounts.getAvailable() + orderBook.getVol() * last_px * 100 -fee);
											groupService.updateAccounts(mainAccounts);
										}
														
										
										
										/**
										 * 更新跟投组合
										 */
										Integer tradeVol;
										Integer tradeGid;
										Relation relation = new Relation();
										relation.setAttr(2);
										relation.setDel(0);
										relation.setGid(orderBook.getGid());
										List<Relation> relationList = groupService.selectFollowedRelation(relation);
										if(!relationList.isEmpty()) {
											for(int j = 0; j < relationList.size(); j++ ) {
												Relation mainRelation = relationList.get(j);
												tradeGid = mainRelation.getSubgid();
												Posi subposi = new Posi();
												subposi.setGid(tradeGid);
												subposi.setStockid(stockid);
												Posi submainPosi = groupService.getStockPosi(subposi);
												if(null != submainPosi) {
													tradeVol = Math.min((int)(submainPosi.getVol() * percent), submainPosi.getAvailable());
													if(tradeVol > 0) {
														fee = tradeVol * last_px * 100 * 0.0013;
														charge = tradeVol * last_px * 100 * 0.0003;
														stamp_tax = tradeVol * last_px * 100 * 0.001;
														BigDecimal  c = new   BigDecimal(stamp_tax);  
														stamp_tax = c.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();  
														if(submainPosi.getAvailable() > tradeVol) {
															submainPosi.setPrice((submainPosi.getVol() * submainPosi.getPrice() - tradeVol * last_px) / (submainPosi.getVol() - tradeVol));
															submainPosi.setAvailable(submainPosi.getAvailable() - tradeVol);
															submainPosi.setVol(submainPosi.getVol() - tradeVol);
															groupService.updatePosi(submainPosi);
														}
														else {
															groupService.deleteStockPosi(submainPosi);
														}
														
														
														/**
														 * 更新账户信息
														 */
														Accounts subaccounts = new Accounts();
														subaccounts.setGid(tradeGid);
														Accounts submainAccounts = groupService.getAccounts(subaccounts);
														if(null != submainAccounts) {
															double subtotalStock = 0;
															Posi subposiNow = new Posi();
															subposiNow.setGid(orderBook.getGid());
															List<Posi> subposiList = groupService.getPosi(subposiNow);
															if(!subposiList.isEmpty()) {
																String substockStr = "";
																for(int m = 0; m < subposiList.size(); m++ ) {
																	String stockString = subposiList.get(m).getStockid();
																	String stocklist = stockString.startsWith("6")?stockString + ".SS":stockString + ".SZ";
																	if(i == subposiList.size() -1) {
																		substockStr += stocklist;
																	}
																	else {
																		substockStr += stocklist + ",";
																	}
																}
																String subresultStr = GetHSTokenUtils.getReal(substockStr, token);
																JSONObject subjsonData = new JSONObject(subresultStr);
																JSONObject subsnapshotData = subjsonData.getJSONObject("data").getJSONObject("snapshot");
																double subrealPrice;
																
																for(int m = 0; m < subposiList.size(); m++ ) {
																	String stockString = subposiList.get(m).getStockid();
																	String substock = stockString.startsWith("6")?stockString + ".SS":stockString + ".SZ";
																	double sub_last = subsnapshotData.getJSONArray(substock).getDouble(2);
																	double sub_preclose = subsnapshotData.getJSONArray(substock).getDouble(3);
																	if(sub_last < 0.01) {
																		subrealPrice = sub_preclose;
																	}
																	else {
																		subrealPrice = sub_last;
																	}
																	subtotalStock += subrealPrice * subposiList.get(m).getVol() * 100;
																}
															}
															submainAccounts.setAvailable(submainAccounts.getAvailable() + tradeVol * last_px * 100 -fee);
															double subtotal = submainAccounts.getAvailable() + submainAccounts.getFrozen() + subtotalStock;
															submainAccounts.setStock(subtotalStock);
															submainAccounts.setTotal(subtotal);
															groupService.updateAccounts(submainAccounts);
															
															
															/**
															 * 更新交易纪录
															 */
															Traderec subtraderec = new Traderec();
															subtraderec.setGid(tradeGid);
															subtraderec.setAction(orderBook.getAction());
															subtraderec.setInserttime(orderBook.getInserttime());
															subtraderec.setStockid(orderBook.getStockid());
															subtraderec.setOrderprice(orderBook.getPrice());
															subtraderec.setCharge(charge);
															subtraderec.setStamp_tax(stamp_tax);
															subtraderec.setVol(tradeVol);
															subtraderec.setTradetime(inserttime);
															subtraderec.setTradeprice(last_px);
															subtraderec.setStatus(3);
															groupService.initTraderec(subtraderec);
														}
													}
												}
											}
										}
									}
								}
							}
							break;
						default:
							break;
					}
					
				}catch(JSONException e) {
					e.printStackTrace();
					//token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
				}
				logger.debug("~~~~~~~~~~~~~~clearOrder执行中，处理完订单 " + orderBook.getOrderid()+"~~~~~~~~~~~~~~");
			}
			
			
		}
		
		else {
			
		}
		logger.debug("~~~~~~~~~~~~~~ cleanOrder执行完毕，共处理：" + orderList.size() + " 个订单~~~~~~~~~~~~~~");
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
						mainAccounts = groupService.getAccounts(mainAccounts);
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
	
	
	public void updateSingleAccounts() {
		List<Accounts> accountsList = groupService.getAllRunAccountsList();
		String token = updateToken(Integer.parseInt(PropertiesUtils.getServerUserString()));
		Date updatetime = new Date();
		for(int i = 0 ;i < accountsList.size(); i++) {
			Accounts mainAccounts = accountsList.get(i);
			if(null != mainAccounts) {
				
				logger.debug("~~~~~~~~~~~~~~updateSingleAccounts执行中，正在处理SingleAccounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				
				Posi posiNow = new Posi();
				posiNow.setGid(mainAccounts.getGid());
				List<Posi> posiList = groupService.getPosi(posiNow);
				if(!posiList.isEmpty() && posiList.size()==1) {
					Integer gid = mainAccounts.getGid();
					Order order = new Order();
					order.setOutTradeNo(mainAccounts.getGid().toString());
					Order mainOrder = cashService.getOrderByOutTradeNo(order);
					Integer r2n = Time.daysBetween(mainAccounts.getRaisetime(), updatetime);
					if(r2n.equals(0)) {
						continue;
					}
					if(posiList.get(0).getOnway() > 0) {
						continue;
					}
					Integer n2e = Time.compareTime(updatetime, mainAccounts.getEndtime());
					double goal = Double.parseDouble(mainOrder.getBody());
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
						double lowStock = 0;
						double highStock = 0;
						for(int j = 0; j < posiList.size(); j++ ) {
							String stockString = posiList.get(j).getStockid();
							String stocklist = stockString.startsWith("6")?stockString + ".SS":stockString + ".SZ";
							double last = snapshotData.getJSONArray(stocklist).getDouble(2);
							double preclose = snapshotData.getJSONArray(stocklist).getDouble(3);//收盘价
							double highPrice = snapshotData.getJSONArray(stocklist).getDouble(7);
							double lowPrice = snapshotData.getJSONArray(stocklist).getDouble(8);
							if(last < 0.01 || highPrice <= 0.01 || lowPrice <= 0.01) { 
								realPrice = preclose;
								highPrice = preclose;
								lowPrice = preclose;
							}
							else {
								realPrice = last;
							}
							totalStock += realPrice * posiList.get(j).getVol() * 100;
							lowStock += lowPrice * posiList.get(j).getVol() * 100;
							highStock += highPrice * posiList.get(j).getVol() * 100;
						}
						if(null != mainAccounts.getDeltime()) {
							Integer n2d = Time.compareTime(updatetime, mainAccounts.getDeltime());
							if(n2d < 0) {
								continue;
							}
						}
						double profit = (mainAccounts.getAvailable() + mainAccounts.getFrozen() + lowStock -1000000) / 10000;
						if(mainAccounts.getDel().equals(0) && n2e <= 0 && profit <= 0 - mainAccounts.getOptigid()) {
							if(null == mainAccounts.getDeltime()) {
								String msg =  "组合#" + mainAccounts.getGname() +"("+gid+ ")# 止损失败，注意检查是否正确 ";
								checkAccounts(msg);
//								String from = "lbh3zyi";
//						        String targetTypeus = "users";
//						        ObjectNode ext = factory.objectNode();
//						        ArrayNode targetusers = factory.arrayNode();
//						        if(true) {
//						        	targetusers.add("vtqs2u2");
//						        	targetusers.add("4mod0dn");
//						        	targetusers.add("ipvnp5j");
//						        	ObjectNode txtmsg = factory.objectNode();
//							        
//							        txtmsg.put("msg", "组合#" + mainAccounts.getGname() +"("+gid+ ")# 止损失败，注意检查是否正确 ");
//							        
//							        txtmsg.put("type","txt");
//							        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
//						        }
								Calendar c = Calendar.getInstance();
								c.add(Calendar.HOUR, 1);
								Date deltime = c.getTime();
								mainAccounts.setDeltime(deltime);
								groupService.updateAccounts(mainAccounts);
								continue;
							}
							mainAccounts.setDeltime(updatetime);
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
							mainAccounts.setTotal(mainAccounts.getAvailable() + mainAccounts.getFrozen() + lowStock);
							mainAccounts.setStock(lowStock);
							mainAccounts.setUpdatetime(updatetime);
							groupService.updateAccounts(mainAccounts);
							continue;
						}
						profit = (mainAccounts.getAvailable() + mainAccounts.getFrozen() + highStock -1000000) / 10000;
						if(mainAccounts.getDel().equals(0) && n2e <= 0 && profit > goal) {
							if(null == mainAccounts.getDeltime()) {
								String msg =  "组合#" + mainAccounts.getGname() +"("+gid+ ")# 达成目标成功，注意检查是否正确 ";
								checkAccounts(msg);
//								String from = "lbh3zyi";
//						        String targetTypeus = "users";
//						        ObjectNode ext = factory.objectNode();
//						        ArrayNode targetusers = factory.arrayNode();
//						        if(true) {
//						        	targetusers.add("vtqs2u2");
//						        	targetusers.add("4mod0dn");
//						        	targetusers.add("ipvnp5j");
//						        	ObjectNode txtmsg = factory.objectNode();
//							        
//							        txtmsg.put("msg", "组合#" + mainAccounts.getGname() +"("+gid+ ")# 达成目标成功，注意检查是否正确 ");
//							        
//							        txtmsg.put("type","txt");
//							        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
//						        }
								Calendar c = Calendar.getInstance();
								c.add(Calendar.HOUR, 1);
								Date deltime = c.getTime();
								mainAccounts.setDeltime(deltime);
								groupService.updateAccounts(mainAccounts);
								continue;
							}
							mainAccounts.setDeltime(updatetime);
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
							mainAccounts.setTotal(mainAccounts.getAvailable() + mainAccounts.getFrozen() + highStock);
							mainAccounts.setStock(highStock);
							mainAccounts.setUpdatetime(updatetime);
							groupService.updateAccounts(mainAccounts);
							continue;
						}
						}catch (Exception e) {
						continue;
					}
				}
				
				logger.debug("~~~~~~~~~~~~~~updateSingleAccounts执行中，处理完SingleAccounts：" + mainAccounts.getGid()+"~~~~~~~~~~~~~~");
				mainAccounts = null;
			}
		}
		logger.debug("~~~~~~~~~~~~~~ updateSingleAccounts执行完毕，共处理：" + accountsList.size() + " 个SingleAccounts~~~~~~~~~~~~~~");
	}
	
	public void updateOrder() {
		List<Order> orderList = cashService.getZuheOrder();
		Date now = new Date();
		Order order = new Order();
		if(!orderList.isEmpty()) {
			for(int i = 0; i < orderList.size(); i++) {
				
				Order mainOrder = orderList.get(i);
				
				logger.debug("~~~~~~~~~~~~~~updateOrder执行中，正在更新订单 " + mainOrder.getId() + "~~~~~~~~~~~~~~");
				
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
					Posi posiNow = new Posi();
					posiNow.setGid(mainAccounts.getGid());
					List<Posi> posiList = groupService.getPosi(posiNow);
					if(!posiList.isEmpty()) {
						boolean hasOnway = false;
						for(Posi eposi: posiList) {
							if(eposi.getOnway() > 0) {
								hasOnway = true;
								break;
							}
						}
						if(hasOnway) {
							continue;
						}
					}
					
					if(null != mainAccounts.getDeltime()) {
						Integer n2d = Time.compareTime(now, mainAccounts.getDeltime());
						if(n2d < 0) {
							continue;
						}
					}
					
					if(null == mainAccounts.getDeltime()) {
						String msg =  "组合#" + mainAccounts.getGname() +"("+gid+ ")# 止损失败，注意检查是否正确 ";
						checkAccounts(msg);
//						String from = "lbh3zyi";
//				        String targetTypeus = "users";
//				        ObjectNode ext = factory.objectNode();
//				        ArrayNode targetusers = factory.arrayNode();
//				        if(true) {
//				        	targetusers.add("vtqs2u2");
//				        	targetusers.add("4mod0dn");
//				        	targetusers.add("ipvnp5j");
//				        	ObjectNode txtmsg = factory.objectNode();
//					        
//					        txtmsg.put("msg", "组合#" + mainAccounts.getGname() +"("+gid+ ")# 止损失败，注意检查是否正确 ");
//					        
//					        txtmsg.put("type","txt");
//					        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
//				        }
						Calendar c = Calendar.getInstance();
						c.add(Calendar.HOUR, 1);
						Date deltime = c.getTime();
						mainAccounts.setDeltime(deltime);
						groupService.updateAccounts(mainAccounts);
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
					Posi posiNow = new Posi();
					posiNow.setGid(mainAccounts.getGid());
					List<Posi> posiList = groupService.getPosi(posiNow);
					if(!posiList.isEmpty()) {
						boolean hasOnway = false;
						for(Posi eposi: posiList) {
							if(eposi.getOnway() > 0) {
								hasOnway = true;
								break;
							}
						}
						if(hasOnway) {
							continue;
						}
					}
					
					if(null != mainAccounts.getDeltime()) {
						Integer n2d = Time.compareTime(now, mainAccounts.getDeltime());
						if(n2d < 0) {
							continue;
						}
					}
					
					if(null == mainAccounts.getDeltime()) {
						String msg =  "组合#" + mainAccounts.getGname() +"("+gid+ ")# 达成目标成功，注意检查是否正确 ";
						checkAccounts(msg);
//						String from = "lbh3zyi";
//				        String targetTypeus = "users";
//				        ObjectNode ext = factory.objectNode();
//				        ArrayNode targetusers = factory.arrayNode();
//				        if(true) {
//				        	targetusers.add("vtqs2u2");
//				        	targetusers.add("4mod0dn");
//				        	targetusers.add("ipvnp5j");
//				        	ObjectNode txtmsg = factory.objectNode();
//					        
//					        txtmsg.put("msg", "组合#" + mainAccounts.getGname() +"("+gid+ ")# 达成目标成功，注意检查是否正确 ");
//					        
//					        txtmsg.put("type","txt");
//					        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
//				        }
						Calendar c = Calendar.getInstance();
						c.add(Calendar.HOUR, 1);
						Date deltime = c.getTime();
						mainAccounts.setDeltime(deltime);
						groupService.updateAccounts(mainAccounts);
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
						//Random rand = new Random();
						//Integer rand_int = rand.nextInt(followOrderList.size());
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
							useOrder.setDetail("系统返回");
							useOrder.setStatus(0);
							cashService.initOrder(useOrder);
							
							CashAccount followCash = cashService.getCashAccountByUserId(followOrderList.get(j).getUserId());
							followCash.setTotalCash(followCash.getTotalCash() + mainOrder.getFeeTotal());
							followCash.setTimeTill(timeStart);
							cashService.updateCashAccount(followCash);
						}
						
						List<Order> realFollowOrderList = cashService.getRealFollowListOrder(order);
						if(!realFollowOrderList.isEmpty()) {
							List<String> pushUserList = new ArrayList<String>();
					        ObjectNode ext = JsonNodeFactory.instance.objectNode();
					        String msg = "";
							String accountsName = mainAccounts.getGname();
							User user = userService.selectUserById(mainAccounts.getUserid());
					        if(true) {
					        	pushUserList.add(user.getEasemobId());
						        if(mainAccounts.getDel().equals(1)) {
						        	msg = "组合#" + accountsName +"("+gid+ ")# 未达到最低人数取消 ";
						        }
						        else {
						        	msg = "组合#" + accountsName +"("+gid+ ")# 失败 ";
						        }
						        sendEaseMobMsg(pushUserList, ext, msg);
					        }
					        if(true) {
					        	pushUserList.clear();
					        	for(int r = 0; r < realFollowOrderList.size(); r++) {
						        	User followUser = userService.selectUserById(realFollowOrderList.get(r).getUserId());
						        	if(null != followUser) {
						        		pushUserList.add(followUser.getEasemobId());
						        	}
						        }
						        if(mainAccounts.getDel().equals(1)) {
						        	msg = "组合#" + accountsName +"("+gid+ ")# 未达到最低人数取消,您的订阅费用已经退还到个人钱包中。 ";
						        }
						        else {
						        	msg = "组合#" + accountsName +"("+gid+ ")# 失败,您的订阅费用已经退还到个人钱包中。 ";
						        }
						        sendEaseMobMsg(pushUserList, ext, msg);
					        }
						}
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
							useOrder.setBody("组合取消召集红包返还");
							useOrder.setDetail("系统返回");
							useOrder.setStatus(0);
							cashService.initOrder(useOrder);
							mainCash.setTotalCash(mainCash.getTotalCash() + bonus);
							mainCash.setTimeTill(now);
							cashService.updateCashAccount(mainCash);
						}
						if(bonus > 0 && !mainAccounts.getDel().equals(1)) {
							
							Integer size = followOrderList.size();
							Date timeStart = new Date();
							Calendar c1 = Calendar.getInstance();
						    c1.setTime(timeStart);
						    c1.add(Calendar.DATE, 3);
							Date timeExpire = c1.getTime();
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
							if(mainAccounts.getStatus().equals(1)) {
								AccountsMeta am = new AccountsMeta();
								am.setGid(mainAccounts.getGid());
								AccountsMeta accountsMeta = groupService.getAccountsMeta(am);
								double giveBonus = accountsMeta.getBonus() >= realFollowOrderList.size() * 0.01?accountsMeta.getBonus():realFollowOrderList.size() * 0.01;
								useOrder.setExpand(giveBonus + "");
								useOrder.setExpandDetail(realFollowOrderList.size() + "");
								accountsMeta.setBonus(giveBonus);
								groupService.updateAccountsMeta(accountsMeta);
							}
							cashService.initOrder(useOrder);
							if(mainAccounts.getStatus().equals(1)) {
								sysGetBonus(useOrder);	//系统默认领红包
							}
							
							List<String> pushUserList = new ArrayList<String>();
							for(int j = 0; j < realFollowOrderList.size(); j++) {
					        	User followUser = userService.selectUserById(realFollowOrderList.get(j).getUserId());
					        	if(null != followUser) {
					        		pushUserList.add(followUser.getEasemobId());
					        	}
					        }
					        String msg = "[股哥红包]"+ useOrder.getBody();
					        ObjectNode ext = JsonNodeFactory.instance.objectNode();
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
					        sendEaseMobMsg(pushUserList, ext, msg);
												
//							String from = "lbh3zyi";
//					        String targetTypeus = "users";
//					        ObjectNode ext = factory.objectNode();
//					        ArrayNode targetusers = factory.arrayNode();
//					        for(int j = 0; j < realFollowOrderList.size(); j++) {
//					        	User followUser = userService.selectUserById(realFollowOrderList.get(j).getUserId());
//					        	if(null != followUser) {
//					        		targetusers.add(followUser.getEasemobId());
//					        	}
//					        }
//					        ext.put("is_red_paper_message", 1);
//					        ext.put("id", useOrder.getId());
//					        ext.put("transactionId", useOrder.getTransactionId());
//					        ext.put("outTradeNo", useOrder.getOutTradeNo());
//					        ext.put("userId", useOrder.getUserId());
//					        ext.put("orderId", useOrder.getOrderId());
//					        ext.put("timeStart", useOrder.getTimeStart().toString());
//					        ext.put("timeExpire", useOrder.getTimeExpire().toString());
//					        ext.put("tradeType", useOrder.getTradeType());
//					        ext.put("feeLeft", useOrder.getFeeLeft());
//					        ext.put("body", useOrder.getBody());
//					        ext.put("detail", useOrder.getDetail());
//					        ext.put("status", useOrder.getStatus());
//					     
//					        ObjectNode txtmsg = factory.objectNode();
//					        txtmsg.put("msg", "[股哥红包]"+ useOrder.getBody());
//					        txtmsg.put("type","txt");
//					        ObjectNode sendTxtMessageusernode = EasemobMessages.sendMessages(targetTypeus, targetusers, txtmsg, from, ext);
					               
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
						
						String accountsName = mainAccounts.getGname();
						User user = userService.selectUserById(mainAccounts.getUserid());
	
				        List<String> pushUserList = new ArrayList<String>();
						pushUserList.add(user.getEasemobId());
				        String msg = "组合#" + accountsName +"("+gid+ ")# 已被取消 ";
				        ObjectNode ext = JsonNodeFactory.instance.objectNode();
				        sendEaseMobMsg(pushUserList, ext, msg);
				        
					}
				}
				
				if(mainAccounts.getDel().equals(2)) {
					CashAccount mainCash = cashService.getCashAccountByUserId(mainAccounts.getUserid());
					mainOrder.setStatus(0);
					cashService.updateOrder(mainOrder);
					order.setOrderId(mainOrder.getId());
					//List<Order> followOrderList = cashService.getListOrder(order);
					//Integer realFollowOrderListSize = cashService.getRealFollowListOrderSize(order);
					Integer realFollowOrderListSize = cashService.getFollowListOrderSize(order);
					if(realFollowOrderListSize > 0) {
						float earn = mainOrder.getFeeTotal() * realFollowOrderListSize * PERCENT;
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
							float searn = mainOrder.getFeeTotal() * realFollowOrderListSize *(1- PERCENT);
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
						
						List<Order> realFollowOrderList = cashService.getRealFollowListOrder(order);
						if(!realFollowOrderList.isEmpty()) {
							String accountsName = mainAccounts.getGname();
							User user = userService.selectUserById(mainAccounts.getUserid());
							
							List<String> pushUserList = new ArrayList<String>();
							pushUserList.add(user.getEasemobId());
							for(int r = 0; r < realFollowOrderList.size(); r++) {
					        	User followUser = userService.selectUserById(realFollowOrderList.get(r).getUserId());
					        	if(null != followUser) {
					        		pushUserList.add(followUser.getEasemobId());
					        	}
					        }
					        String msg = "组合#" + accountsName +"("+gid+ ")# 达成目标";
					        ObjectNode ext = JsonNodeFactory.instance.objectNode();
					        sendEaseMobMsg(pushUserList, ext, msg);
						}
					}
				}
				logger.debug("~~~~~~~~~~~~~~updateOrder执行中，更新完订单: " + mainOrder.getId() + "~~~~~~~~~~~~~~");
			}
		}
		
		logger.debug("~~~~~~~~~~~~~~updateOrder执行完毕，共处理: " + orderList.size() + " 个订单~~~~~~~~~~~~~~");
	}
	
	
	public void sysGetBonus(Order order) {
//		Order mainOrder = cashService.getOrderById(order.getOrderId());
//		List<Order> realFollowOrderList = cashService.getRealFollowListOrder(order);
//		List<Order> sysFollowOrderList = cashService.getSysFollowListOrder(order);
//		double giveBonus = Double.parseDouble(order.getExpand());
		Integer sysFollowOrderListSize = cashService.getRealFollowListOrderSize(order);
		double average = order.getFeeTotal() / Integer.parseInt(order.getDetail());
		double min = (average / 10 > 0.01)?average / 10 : 0.01;
		double max = (average * 3 > 0.03)?average * 3 : 0.01;
		Random rand = new Random();
		Integer randStart = rand.nextInt(sysFollowOrderListSize);
		Integer size = SIZE;
		List<Order> randSysFollowList = cashService.getRandSysFollowListOrder(order.getOrderId(), randStart, size);
		while(randSysFollowList.size() < MIN_SIZE) {
			rand = new Random();
			randStart = rand.nextInt(sysFollowOrderListSize);
			randSysFollowList = cashService.getRandSysFollowListOrder(order.getOrderId(), randStart, size);
		}
		float amount = 0;
		Integer left = Integer.parseInt(order.getDetail());
		Integer realleft = Integer.parseInt(order.getExpandDetail());
		if(!randSysFollowList.isEmpty()) {
			for(Order o:randSysFollowList) {
				Integer userId = o.getUserId();
				Date timeStart = new Date();
				Date timeExpire = timeStart;
				String transactionId = timeStart.getTime() + "";
				String outTradeNo = transactionId;
				Integer tradeType = 6;
				Integer status = 0;
				float feeTotal = (float)(Math.round(((float)Math.random() * (max-min) + min)*100)) / 100;
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
				Integer hasInsert = cashService.initOrder(bonusOrder);	
				if(hasInsert > 0) {
					CashAccount userAccount = cashService.getCashAccountByUserId(userId);
					userAccount.setTotalCash(userAccount.getTotalCash() + feeTotal);
					userAccount.setTimeTill(timeStart);
					cashService.updateCashAccount(userAccount);
					amount += feeTotal;
					left --;
				}
			}
			order.setDetail(left.toString());
			order.setFeeLeft(order.getFeeLeft() - amount);
			cashService.updateOrder(order);
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
	
	public void checkAccounts(String msg) {
		List<String> pushUserList = new ArrayList<String>();
		pushUserList.add("vtqs2u2");
		pushUserList.add("4mod0dn");
		pushUserList.add("ipvnp5j");
		pushUserList.add("lhiy4zv");
        ObjectNode txtmsg = factory.objectNode();
        txtmsg.put("type","txt");
        ObjectNode ext = JsonNodeFactory.instance.objectNode();
        sendEaseMobMsg(pushUserList, ext, msg);
	}
}
