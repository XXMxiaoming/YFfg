package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Integral;
import com.yfwl.yfgp.model.IntegralTrade;
import com.yfwl.yfgp.model.IntegralTradeCount;


public interface IntegralService {
	public Integer initIntegral(Integral integral);
	
	public Integer updateIntegral(Integral integral);
	
	public Integral getIntegral(Integer userId);
	
	public Integral getExistIntegral(Integer integralId);
	
	public List<IntegralTrade> getListIntegralTrade(Integer integralId);
	
	public Integer insertIntegralTrade(IntegralTrade trade);
	
	public Integer getTradeCount(IntegralTrade trade);
	
	public Integer getAllTradeCount(IntegralTrade trade);
	
	public List<IntegralTrade> getTodayListIntegralTrade(Integer integralId);
	
	public List<IntegralTradeCount> getTodayListCountIntegralTrade(Integer integralId);

}
