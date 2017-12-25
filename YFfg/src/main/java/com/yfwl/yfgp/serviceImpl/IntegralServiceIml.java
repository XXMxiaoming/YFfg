package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.IntegralMapper;
import com.yfwl.yfgp.dao.IntegralTradeMapper;
import com.yfwl.yfgp.model.Integral;
import com.yfwl.yfgp.model.IntegralTrade;
import com.yfwl.yfgp.model.IntegralTradeCount;
import com.yfwl.yfgp.service.IntegralService;

@Service
public class IntegralServiceIml implements IntegralService {
	
	@Autowired
	IntegralMapper integralMapper;
	@Autowired
	IntegralTradeMapper integralTradeMapper;
	
	@Override
	public Integer initIntegral(Integral integral) {
		// TODO Auto-generated method stub
		return integralMapper.initIntegral(integral);
	}

	@Override
	public Integer updateIntegral(Integral integral) {
		// TODO Auto-generated method stub
		return integralMapper.updateIntegral(integral);
	}

	@Override
	public Integral getIntegral(Integer userId) {
		// TODO Auto-generated method stub
		return integralMapper.getIntegral(userId);
	}

	@Override
	public Integral getExistIntegral(Integer integralId) {
		// TODO Auto-generated method stub
		return integralMapper.getExistIntegral(integralId);
	}

	@Override
	public List<IntegralTrade> getListIntegralTrade(Integer integralId) {
		// TODO Auto-generated method stub
		return integralTradeMapper.getListIntegralTrade(integralId);
	}

	@Override
	public Integer insertIntegralTrade(IntegralTrade trade) {
		// TODO Auto-generated method stub
		return integralTradeMapper.insertIntegralTrade(trade);
	}

	@Override
	public Integer getTradeCount(IntegralTrade trade) {
		// TODO Auto-generated method stub
		return integralTradeMapper.getTradeCount(trade);
	}

	@Override
	public Integer getAllTradeCount(IntegralTrade trade) {
		// TODO Auto-generated method stub
		return integralTradeMapper.getAllTradeCount(trade);
	}

	@Override
	public List<IntegralTrade> getTodayListIntegralTrade(Integer integralId) {
		// TODO Auto-generated method stub
		return integralTradeMapper.getTodayListIntegralTrade(integralId);
	}

	@Override
	public List<IntegralTradeCount> getTodayListCountIntegralTrade(Integer integralId) {
		// TODO Auto-generated method stub
		return integralTradeMapper.getTodayListCountIntegralTrade(integralId);
	}

}
