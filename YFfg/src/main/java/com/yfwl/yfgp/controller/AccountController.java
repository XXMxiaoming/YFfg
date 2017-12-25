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

import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Trade;
import com.yfwl.yfgp.service.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {
	@Autowired
	AccountService accountService;
	
	@RequestMapping(value = "/initaccount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> initAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		Account existAccount = accountService.getAccount(Integer.parseInt(userId));
		if(null != existAccount) {
			map.put("status", 3);
			map.put("msg", "该账户已存在");
			map.put("data", "");
		}
		else {
			Account account = new Account();
			account.setUserId(Integer.parseInt(userId));
			account.setPassword(password);
			int hasInit = accountService.initAccount(account);
			if( hasInit == 1) {
				map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", accountService.getAccount(Integer.parseInt(userId)));
			}
			else{
				map.put("status", 4);
				map.put("msg", "操作失败");
				map.put("data", "");
			}
		}
		return map;
	}

	@RequestMapping(value = "/getaccount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getAccount(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String userId = request.getParameter("userId");
		
		Account existAccount = accountService.getAccount(Integer.parseInt(userId));
		if(null != existAccount) {
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", existAccount);
		}
		else {
			map.put("status", 4);
			map.put("msg", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/consume", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> consume(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String userId = request.getParameter("userId");
		String tradeUserId = request.getParameter("tradeUserId");
		String tradeInfo = request.getParameter("tradeInfo");
		String tradeType = request.getParameter("tradeType");
		String moneyChange = request.getParameter("moneyChange");
		Date tradeTime= new Date();
		Integer tradeState = 0; 
		Account existAccount = accountService.getAccount(Integer.parseInt(userId));
		Account targetAccount = accountService.getAccount(Integer.parseInt(tradeUserId));
		if(null == targetAccount) {
			targetAccount = accountService.getAccount(0);
		}
		if(null != existAccount) {
			if(existAccount.getMoney() + Float.parseFloat(moneyChange) < 0) {
				map.put("status", 3);
				map.put("msg", "账户余额不足");
				map.put("data", "");
			}
			else {
				Trade trade = new Trade();
				trade.setUserId(Integer.parseInt(userId));
				trade.setTradeUserId(Integer.parseInt(tradeUserId));
				trade.setTradeInfo(tradeInfo);
				trade.setTradeType(Integer.parseInt(tradeType));
				trade.setMoneyChange(Float.parseFloat(moneyChange));
				trade.setTradeTime(tradeTime);
				trade.setTradeState(tradeState);
				int hasInsert = accountService.insertTrade(trade);
				if (hasInsert == 1) {
					existAccount.setMoney(existAccount.getMoney() + Float.parseFloat(moneyChange));
					targetAccount.setMoney(targetAccount.getMoney() - Float.parseFloat(moneyChange));
					int hasUpdate = accountService.updateAccount(existAccount);
					int hasUpdateTarget = accountService.updateAccount(targetAccount);
					if(hasUpdate == 1 && hasUpdateTarget == 1) {
						map.put("status", 0);
						map.put("msg", "操作成功");
						map.put("data", trade);
						map.put("money", existAccount.getMoney());
					}
					else {
						map.put("status", 7);
						map.put("msg", "账户操作失败，请重新操作");
						map.put("data", "");
					}
				}
				else {
					map.put("status", 4);
					map.put("msg", "操作失败");
					map.put("data", "");
				}
			}
			
		}
		else {
			map.put("status", 4);
			map.put("msg", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/gettradelist", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getTradeList(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String userId = request.getParameter("userId");
		Account existAccount = accountService.getAccount(Integer.parseInt(userId));
		if(null != existAccount) {
			List<Trade> list = accountService.getListTrade(Integer.parseInt(userId));
			if(list.size() !=0) {
				map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", list);
			}
			else {
				map.put("status", 7);
				map.put("msg", "操作失败");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 4);
			map.put("msg", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/changepassword", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> changePassword(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		String accountId = request.getParameter("accountId");
		String password = request.getParameter("password");
		Account existAccount = accountService.getExistAccount(Integer.parseInt(accountId));
		if(null != existAccount) {
			existAccount.setPassword(password);
			int hasUpdate = accountService.updateAccount(existAccount);
			if(hasUpdate ==1) {
				map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", "");
			}
			else {
				map.put("status", 7);
				map.put("msg", "操作失败");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 4);
			map.put("msg", "不存在此账户");
			map.put("data", "");
		}
		return map;
	}
	
	
}
