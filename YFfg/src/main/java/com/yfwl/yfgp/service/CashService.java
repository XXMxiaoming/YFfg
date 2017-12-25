package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.User;

public interface CashService {
	Integer initCashAccount(CashAccount cashAccount);
	Integer updateCashAccount(CashAccount cashAccount);
	CashAccount getCashAccount(CashAccount cashAccount);
	CashAccount getCashAccountByUserId(Integer userId);
	
	Integer initOrder(Order order);
	Integer updateOrder(Order order);
	Order getOrder(Order order);
	List<Order> getListOrder(Order order);
	List<Order> getListOrderLimit(Order order);
	List<Order> getOwnOrder(Order order);
	Order hasGetOrder(Order order);
	Order hasGetBonusOrder(Order order);
	Order getMaxOrder(Integer orderId);
	Integer updateOrderId(Order order);	
	Order hasBuyHomeAccounts(Order order);	
	Order getOrderById(Integer id);
	Integer updateOrder2(Order order);
	
	Order getOrderByOutTradeNo(Order order);
	
	List<Order> getExpireBonusOrder();
	List<Order> getZuheOrder();
	Order getZuheOrderByDetail(Order o);
	List<Order> getFollowListOrder(Order mainOrder);
	List<Order> getFollowListOrderLimit(Integer orderId, Integer start, Integer limit);
	Integer getFollowListOrderSize(Order order);
	List<Order> getBonusListOrder(Order mainOrder);
	Order getBonusOrder(Order mainOrder);
	List<Order> getKeepOrderList();
	List<Order> getRealFollowListOrder(Order order);
	Integer getRealFollowListOrderSize(Order order);
	List<Order> getSysFollowListOrder(Order order);
	Integer getSysFollowListOrderSize(Order order);
	List<Order> getRandSysFollowListOrder(Integer orderId, Integer start, Integer limit);
	List<Order> getAllPublishBonusOrder();
	List<Order> getRankZuheOrder(Integer start, Integer limit, String sort);
	List<Order> getListBonusOrderLimit(Order order);
	Order hasPayContentOrder(Order order);
	Order hasStrategyOrder(Order order);
	/**
	 * 根据文章作者id获取订阅者信息
	 * @param : detail 文章作者ID 
	 * @return order 订阅者信息
	 */
	List<User> getUserMessageById(Integer detail);
}
