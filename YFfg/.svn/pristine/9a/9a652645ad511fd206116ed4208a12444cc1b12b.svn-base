package com.yfwl.yfgp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.Order;

public interface OrderMapper {

	Integer initOrder(Order order);

	Integer updateOrder(Order order);

	Order getOrder(Order order);

	List<Order> getListOrder(Order order);

	Order hasGetOrder(Order order);

	List<Order> getOwnOrder(Order order);

	Order getMaxOrder(Integer orderId);

	Order getOrderById(Integer id);

	Order getOrderByOutTradeNo(Order order);

	List<Order> getExpireBonusOrder();

	List<Order> getZuheOrder();

	Order getZuheOrderByDetail(Order o);

	List<Order> getFollowListOrder(Order mainOrder);
	
	List<Order> getFollowListOrderLimit(
			@Param(value="orderId") Integer orderId, 
			@Param(value="start") Integer start, 
			@Param(value="limit") Integer limit);

	List<Order> getBonusListOrder(Order mainOrder);

	Order getBonusOrder(Order mainOrder);

	List<Order> getKeepOrderList();

	List<Order> getRealFollowListOrder(Order order);

	List<Order> getSysFollowListOrder(Order order);

	List<Order> getRandSysFollowListOrder(
			@Param(value="orderId") Integer orderId, 
			@Param(value="start") Integer start, 
			@Param(value="limit") Integer limit);

	List<Order> getAllPublishBonusOrder();

	Integer getFollowListOrderSize(Order order);

	Integer getRealFollowListOrderSize(Order order);

	Integer getSysFollowListOrderSize(Order order);

	Order hasGetBonusOrder(Order order);

	List<Order> getListOrderLimit(Order order);

	List<Order> getRankZuheOrder(
			@Param(value="start") Integer start, 
			@Param(value="limit") Integer limit, 
			@Param(value="sort") String sort);

	List<Order> getListBonusOrderLimit(Order order);

	Order hasPayContentOrder(Order order);
	
	Order getZJHBorder(Integer gid);

	Order hasStrategyOrder(Order order);
	
	Integer getMyVIPNnm(Integer userId);
	List<Order> getMyVIPOrder(
			@Param(value="userId") Integer userId, 
			@Param(value="pageCount") Integer pageCount);
	
	Integer getMySubscribeVIPNum(Integer userId);
	List<Order> getMySubscribeVIPOrder(
			@Param(value="userId") Integer userId, 
			@Param(value="pageCount") Integer pageCount);
	
	String getTimeStart(
			@Param(value="userId") Integer userId, 
			@Param(value="detail") Integer detail);
	
	String getTimeExpire(
			@Param(value="userId") Integer userId, 
			@Param(value="detail") Integer detail);
			

	List<String> getFollowFromOrderMapper(Order order);		

	Integer updateOrderId(Order order);	
	Integer updateOrder2(Order order);	
	Order hasBuyHomeAccounts(Order order);	
	
}
