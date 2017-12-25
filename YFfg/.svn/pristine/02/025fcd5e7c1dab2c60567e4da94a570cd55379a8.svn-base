package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.CashAccountMapper;
import com.yfwl.yfgp.dao.OrderMapper;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.CashService;

@Service
public class CashServiceImpl implements CashService {
	
	@Autowired
	CashAccountMapper cashAccountMapper;
	
	@Autowired
	OrderMapper orderMapper;

	@Override
	public Integer initCashAccount(CashAccount cashAccount) {
		// TODO Auto-generated method stub
		return cashAccountMapper.initCashAccount(cashAccount);
	}

	@Override
	public Integer updateCashAccount(CashAccount cashAccount) {
		// TODO Auto-generated method stub
		return cashAccountMapper.updateCashAccount(cashAccount);
	}

	@Override
	public CashAccount getCashAccount(CashAccount cashAccount) {
		// TODO Auto-generated method stub
		return cashAccountMapper.getCashAccount(cashAccount);
	}

	@Override
	public Integer initOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.initOrder(order);
	}

	@Override
	public Integer updateOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.updateOrder(order);
	}

	@Override
	public Order getOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getOrder(order);
	}

	@Override
	public List<Order> getListOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getListOrder(order);
	}

	@Override
	public CashAccount getCashAccountByUserId(Integer userId) {
		// TODO Auto-generated method stub
		
		CashAccount  ca = cashAccountMapper.getCashAccountByUserId(userId);
		if(null == ca) {
			cashAccountMapper.initCashAccountByUserId(userId);
		}
		ca = cashAccountMapper.getCashAccountByUserId(userId);
		return ca;
	}

	@Override
	public Order hasGetOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.hasGetOrder(order);
	}

	@Override
	public List<Order> getOwnOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getOwnOrder(order);
	}

	@Override
	public Order getMaxOrder(Integer orderId) {
		// TODO Auto-generated method stub
		return orderMapper.getMaxOrder(orderId);
	}

	@Override
	public Order getOrderById(Integer id) {
		// TODO Auto-generated method stub
		return orderMapper.getOrderById(id);
	}

	@Override
	public Order getOrderByOutTradeNo(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getOrderByOutTradeNo(order);
	}

	@Override
	public List<Order> getExpireBonusOrder() {
		// TODO Auto-generated method stub
		return orderMapper.getExpireBonusOrder();
	}

	@Override
	public List<Order> getZuheOrder() {
		// TODO Auto-generated method stub
		return orderMapper.getZuheOrder();
	}

	@Override
	public Order getZuheOrderByDetail(Order o) {
		// TODO Auto-generated method stub
		return orderMapper.getZuheOrderByDetail(o);
	}

	@Override
	public List<Order> getFollowListOrder(Order mainOrder) {
		// TODO Auto-generated method stub
		return orderMapper.getFollowListOrder(mainOrder);
	}

	@Override
	public List<Order> getBonusListOrder(Order mainOrder) {
		// TODO Auto-generated method stub
		return orderMapper.getBonusListOrder(mainOrder);
	}

	@Override
	public Order getBonusOrder(Order mainOrder) {
		// TODO Auto-generated method stub
		return orderMapper.getBonusOrder(mainOrder);
	}

	@Override
	public List<Order> getKeepOrderList() {
		// TODO Auto-generated method stub
		return orderMapper.getKeepOrderList();
	}

	@Override
	public List<Order> getRealFollowListOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getRealFollowListOrder(order);
	}

	@Override
	public List<Order> getSysFollowListOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getSysFollowListOrder(order);
	}

	@Override
	public List<Order> getRandSysFollowListOrder(Integer orderId, Integer start, Integer limit) {
		// TODO Auto-generated method stub
		return orderMapper.getRandSysFollowListOrder(orderId, start, limit);
	}

	@Override
	public List<Order> getAllPublishBonusOrder() {
		// TODO Auto-generated method stub
		return orderMapper.getAllPublishBonusOrder();
	}

	@Override
	public Integer getFollowListOrderSize(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getFollowListOrderSize(order);
	}

	@Override
	public Integer getRealFollowListOrderSize(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getRealFollowListOrderSize(order);
	}

	@Override
	public Integer getSysFollowListOrderSize(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getSysFollowListOrderSize(order);
	}

	@Override
	public List<Order> getFollowListOrderLimit(Integer orderId, Integer start, Integer limit) {
		// TODO Auto-generated method stub
		return orderMapper.getFollowListOrderLimit(orderId,start,limit);
	}

	@Override
	public Order hasGetBonusOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.hasGetBonusOrder(order);
	}

	@Override
	public List<Order> getListOrderLimit(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getListOrderLimit(order);
	}

	@Override
	public List<Order> getRankZuheOrder(Integer start, Integer limit, String sort) {
		// TODO Auto-generated method stub
		return orderMapper.getRankZuheOrder(start, limit, sort);
	}

	@Override
	public List<Order> getListBonusOrderLimit(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.getListBonusOrderLimit(order);
	}

	@Override
	public Order hasPayContentOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.hasPayContentOrder(order);
	}

	@Override
	public Order hasStrategyOrder(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.hasStrategyOrder(order);
	}

	@Override
	public List<User> getUserMessageById(Integer detail) {
		List<User> list = cashAccountMapper.getUserMessageById(detail);
		return list;
	}

	@Override
	public Integer updateOrderId(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.updateOrderId(order);
	}

	@Override
	public Order hasBuyHomeAccounts(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.hasBuyHomeAccounts(order);
	}

	@Override
	public Integer updateOrder2(Order order) {
		// TODO Auto-generated method stub
		return orderMapper.updateOrder2(order);
	}

	

}
