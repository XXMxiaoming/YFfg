package com.yfwl.yfgp.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.Integral;
import com.yfwl.yfgp.model.IntegralTrade;
import com.yfwl.yfgp.model.IntegralTradeCount;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.IntegralService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.AccountUtil;

@Controller
@RequestMapping("/integral")
public class IntegralController {
	@Autowired
	IntegralService integralService;
	@Autowired
	UserService userService;
	
	
	@RequestMapping(value = "/initintegral", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> initAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String userId = request.getParameter("userId");
		
		Integral existIntegral = integralService.getIntegral(Integer.parseInt(userId));
		if(null != existIntegral) {
			map.put("status", 3);
			map.put("message", "该账户已存在");
			map.put("data", "");
		}
		else {
			Integral account = new Integral();
			account.setUserId(Integer.parseInt(userId));
			int hasInit = integralService.initIntegral(account);
			if( hasInit == 1) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", integralService.getIntegral(Integer.parseInt(userId)));
			}
			else{
				map.put("status", 4);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		return map;
	}

	@RequestMapping(value = "/getintegral", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String userId = request.getParameter("userId");
		
		Integral existIntegral = integralService.getIntegral(Integer.parseInt(userId));
		if(null != existIntegral) {
			Integer currentLevel = existIntegral.getIntegralLevel();
			existIntegral.setIntegralNeed(AccountUtil.INTEGRAL_LEVEL[currentLevel + 1]);
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", existIntegral);
		}
		else {
			map.put("status", 4);
			map.put("message", "不存在此积分账户");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/consume", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> consume(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String integralId = request.getParameter("integralId");
		String tradeInfo = request.getParameter("tradeInfo");
		String tradeType = request.getParameter("tradeType");
		String integralChange = request.getParameter("integralChange");
		Date tradeTime= new Date();
		Integer tradeState = 0; 
		Integral existAccount = integralService.getExistIntegral(Integer.parseInt(integralId));
		if(null != existAccount) {
			if(existAccount.getIntegral() + Integer.parseInt(integralChange) < 0) {
				map.put("status", 3);
				map.put("message", "账户积分不足");
				map.put("data", "");
			}
			else {
				IntegralTrade trade = new IntegralTrade();
				trade.setIntegralId(Integer.parseInt(integralId));
				trade.setTradeInfo(tradeInfo);
				trade.setTradeType(Integer.parseInt(tradeType));
				trade.setIntegralChange(Integer.parseInt(integralChange));
				trade.setTradeTime(tradeTime);
				trade.setTradeState(tradeState);
				Integer level = existAccount.getIntegralLevel() ;
				for(int i = 0; i < AccountUtil.INTEGRAL_LEVEL.length; i++){
					if(existAccount.getIntegral() + Integer.parseInt(integralChange) > AccountUtil.INTEGRAL_LEVEL[i]) {
						level = i ;
					}
					else {
						break;
					}
				}
				int integralleft = integralService.getTradeCount(trade);
				int integralAllleft = integralService.getAllTradeCount(trade);
				if(AccountUtil.INTEGRAL[Integer.parseInt(tradeType)] > integralleft ||AccountUtil.INTEGRAL[Integer.parseInt(tradeType)] ==-1)
				{
					int hasInsert = integralService.insertIntegralTrade(trade);
					if (hasInsert == 1) {
						existAccount.setIntegral(existAccount.getIntegral() + Integer.parseInt(integralChange));
						existAccount.setIntegralLevel(level);
						
						User user = userService.selectUserById(existAccount.getUserId());
						user.setUserLevel(level);
						int hasUpdateLevel = userService.updateUserSuper(user);
						int hasUpdate = integralService.updateIntegral(existAccount);
						if(hasUpdate == 1) {
							map.put("status", 0);
							map.put("message", "操作成功");
							map.put("data", trade);
							map.put("level", level);
						}
						else {
							map.put("status", 7);
							map.put("message", "账户操作失败，请重新操作");
							map.put("data", "");
						}
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else if(AccountUtil.INTEGRAL[Integer.parseInt(tradeType)] ==0 && AccountUtil.INTEGRAL[Integer.parseInt(tradeType)] >= integralAllleft){
					int hasInsert = integralService.insertIntegralTrade(trade);
					if (hasInsert == 1) {
						existAccount.setIntegral(existAccount.getIntegral() + Integer.parseInt(integralChange));
						existAccount.setIntegralLevel(level);
						int hasUpdate = integralService.updateIntegral(existAccount);
						if(hasUpdate == 1) {
							map.put("status", 0);
							map.put("message", "操作成功");
							map.put("data", trade);
							map.put("level", level);
						}
						else {
							map.put("status", 7);
							map.put("message", "账户操作失败，请重新操作");
							map.put("data", "");
						}
					}
					else {
						map.put("status", 4);
						map.put("message", "操作失败");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 7);
					map.put("message", "今日积分已达上限");
					map.put("data", "");
				}
			}
			
		}
		else {
			map.put("status", 4);
			map.put("message", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/gettradelist", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getTradeList(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String integralId = request.getParameter("integralId");
		Integral existAccount = integralService.getExistIntegral(Integer.parseInt(integralId));
		if(null != existAccount) {
			List<IntegralTrade> list = integralService.getListIntegralTrade(Integer.parseInt(integralId));
			if(list.size() !=0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", list);
			}
			else {
				map.put("status", 7);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 4);
			map.put("message", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/gettodaytradelist", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getTodayTradeList(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String integralId = request.getParameter("integralId");
		Integral existAccount = integralService.getExistIntegral(Integer.parseInt(integralId));
		if(null != existAccount) {
			List<IntegralTrade> list = integralService.getTodayListIntegralTrade(Integer.parseInt(integralId));
			if(list.size() !=0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", list);
			}
			else {
				map.put("status", 7);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 4);
			map.put("message", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/gettodaytradecountlist", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getTodayTradeCountList(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String integralId = request.getParameter("integralId");
		Integral existAccount = integralService.getExistIntegral(Integer.parseInt(integralId));
		if(null != existAccount) {
			List<IntegralTradeCount> list = integralService.getTodayListCountIntegralTrade(Integer.parseInt(integralId));
			if(list.size() !=0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", list);
			}
			else {
				map.put("status", 7);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 4);
			map.put("message", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	
	
	
}
