package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Trade;


public interface AccountService {
	public Integer initAccount(Account account);
	
	public Integer updateAccount(Account account);
	
	public Account getAccount(Integer userId);
	
	public Account getExistAccount(Integer accountId);
	
	public List<Trade> getListTrade(Integer userId);
	
	public Integer insertTrade(Trade trade);
	

}
