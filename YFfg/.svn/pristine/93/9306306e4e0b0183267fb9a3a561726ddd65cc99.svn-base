package com.yfwl.yfgp.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.OrderMapper;
import com.yfwl.yfgp.dao.UserMapper;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.DynVIPService;

@Service
public class DynVIPServiceImpl implements DynVIPService {
	
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private OrderMapper orderMapper;

	@Override
	public Integer getMyVIPNnm(Integer userId) {
		// TODO Auto-generated method stub
		return orderMapper.getMyVIPNnm(userId);
	}

	@Override
	public Integer getMySubscribeVIPNum(Integer userId) {
		// TODO Auto-generated method stub
		return orderMapper.getMySubscribeVIPNum(userId);
	}

	@Override
	public List<Map<String, Object>> getVIPOrder(Integer userId,
			Integer pageCount, String type) {
		// TODO Auto-generated method stub
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Order> orderList = new ArrayList<Order>();
		if(type.equals("subme")){
			//谁订阅我
			orderList = orderMapper.getMyVIPOrder(userId, pageCount);
			if(orderList.size()>0){
				for(Order order:orderList){
					Map<String, Object> map = new HashMap<String, Object>();
					Integer vipuserId = order.getUserId();
					User user = userMapper.selectUserById(vipuserId);
					
					String timeStart = orderMapper.getTimeStart(vipuserId,userId);
					String timeExpire = orderMapper.getTimeExpire(vipuserId,userId);
					
					map.put("userId", user.getUserId());
					map.put("userName", user.getUserName());
					map.put("headImage", user.getHeadImage());
					map.put("breif", user.getUserBrief());
					map.put("timeStart", timeStart);
					map.put("timeExpire", timeExpire);
					list.add(map);
				}
			}
		}else if(type.equals("mesub")){
			//我订阅谁
			orderList = orderMapper.getMySubscribeVIPOrder(userId, pageCount);
			if(orderList.size()>0){
				for(Order order:orderList){
					Map<String, Object> map = new HashMap<String, Object>();
					Integer vipuserId = order.getUserId();
					User user = userMapper.selectUserById(vipuserId);
					
					String timeStart = orderMapper.getTimeStart(userId,vipuserId);
					String timeExpire = orderMapper.getTimeExpire(userId,vipuserId);
					
					map.put("userId", user.getUserId());
					map.put("userName", user.getUserName());
					map.put("headImage", user.getHeadImage());
					map.put("breif", user.getUserBrief());
					map.put("timeStart", timeStart);
					map.put("timeExpire", timeExpire);
					list.add(map);
				}
			}
		}else{
			return null;
		}
		return list;
	}
}
