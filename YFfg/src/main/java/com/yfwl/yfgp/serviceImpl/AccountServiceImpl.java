package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.AccountMapper;
import com.yfwl.yfgp.dao.TradeMapper;
import com.yfwl.yfgp.dao1.AccountsMapper;
import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.Trade;
import com.yfwl.yfgp.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService{
	@Autowired
	AccountMapper accountMapper;
	@Autowired
	TradeMapper tradeMapper;
	
	@Override
	public Integer initAccount(Account account) {
		// TODO Auto-generated method stub
		return accountMapper.initAccount(account);
	}

	@Override
	public Integer updateAccount(Account account) {
		// TODO Auto-generated method stub
		return accountMapper.updateAccount(account);
	}

	@Override
	public Account getAccount(Integer userId) {
		// TODO Auto-generated method stub
		return accountMapper.getAccount(userId);
	}

	@Override
	public Account getExistAccount(Integer accountId) {
		// TODO Auto-generated method stub
		return accountMapper.getExistAccount(accountId);
	}

	@Override
	public List<Trade> getListTrade(Integer userId) {
		// TODO Auto-generated method stub
		return tradeMapper.getListTrade(userId);
	}

	@Override
	public Integer insertTrade(Trade trade) {
		// TODO Auto-generated method stub
		return tradeMapper.insertTrade(trade);
	}

	
}
