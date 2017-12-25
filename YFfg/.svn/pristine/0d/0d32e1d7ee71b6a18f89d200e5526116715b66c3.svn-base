package com.yfwl.yfgp.schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.service.CashService;

@Component
public class OrderSchedule {
	@Resource
	CashService cashService;;
	private static Logger logger = OrderBookSchedule.getLogger("d:/logs/yfgp/schedule/OrderSchedule_debug_");
	
	/**
	 * 订单定时操作
	 */
	
	public void bindSchedule() {
		logger.debug("============================ OrderSchedule 开始执行============================");
		
		logger.debug("====================== clearOrder方法  开始执行=====================");
		clearOrder();
		logger.debug("====================== clearOrder方法  结束执行=====================");
		
		logger.debug("====================== clearKeepOrder方法  开始执行=====================");
		clearKeepOrder();
		logger.debug("====================== clearKeepOrder方法  结束执行=====================");
		
		logger.debug("============================ OrderSchedule 结束执行============================");
	}
	
	
	public void clearOrder() {
		List<Order> orderList = cashService.getExpireBonusOrder();
		Date updateTime = new Date();
		if(!orderList.isEmpty()) {
			for(int i = 0; i < orderList.size(); i++) {
				Order order = orderList.get(i);
				
				logger.debug("~~~~~~~~~~~~~~ clearOrder执行中，正在处理订单 " + order.getId()+"~~~~~~~~~~~~~~");
				
				CashAccount mainAccount = cashService.getCashAccountByUserId(order.getUserId());
				if(order.getFeeLeft() > 0 ) {
					mainAccount.setTotalCash(mainAccount.getTotalCash() + order.getFeeLeft());
					mainAccount.setTimeTill(updateTime);
					cashService.updateCashAccount(mainAccount);
					
					order.setStatus(0);
					cashService.updateOrder(order);
					
					Calendar c = Calendar.getInstance();
					Date timeStart = c.getTime();
					String transactionId = timeStart.getTime() + "";
					String outTradeNo = transactionId;
					order.setTransactionId(transactionId);
					order.setOutTradeNo(outTradeNo);
					order.setUserId(order.getUserId());
					order.setOrderId(order.getId());
					order.setFeeTotal(order.getFeeLeft());
					order.setFeeLeft(0);
					order.setTimeStart(updateTime);
					order.setTimeExpire(updateTime);
					order.setTradeType(14);
					order.setBody("系统返回");
					cashService.initOrder(order);
					
				}
				logger.debug("~~~~~~~~~~~~~~ clearOrder执行中，处理完订单 " + order.getId()+"~~~~~~~~~~~~~~");
			}
		}
		logger.debug("~~~~~~~~~~~~~~ clearOrder执行完毕，共处理：" + orderList.size() + " 个订单~~~~~~~~~~~~~~");
	}
	
	public void clearKeepOrder() {
		List<Order> keepOrderList = cashService.getKeepOrderList();
		if(!keepOrderList.isEmpty()) {
			for(int i = 0; i < keepOrderList.size(); i++) {
				Order mainOrder = cashService.getOrderById(keepOrderList.get(i).getOrderId());
				if(null != mainOrder) {
					
					logger.debug("~~~~~~~~~~~~~~ clearKeepOrder执行中，正在处理订单 " + mainOrder.getId()+"~~~~~~~~~~~~~~");
					
					if(mainOrder.getStatus().equals(0)) {
						keepOrderList.get(i).setStatus(0);
						cashService.updateOrder(keepOrderList.get(i));
					}
				}
				
				logger.debug("~~~~~~~~~~~~~~ clearOrder执行中，处理完订单 " + mainOrder.getId()+"~~~~~~~~~~~~~~");
			}
		}
		
		logger.debug("~~~~~~~~~~~~~~ clearOrder执行完毕，共处理：" + keepOrderList.size() + " 个订单~~~~~~~~~~~~~~");
	}

}
