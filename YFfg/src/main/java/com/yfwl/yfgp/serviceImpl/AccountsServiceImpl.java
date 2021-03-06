package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.AccountsMapper;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.service.AccountsService;
@Service
public class AccountsServiceImpl implements AccountsService {
	@Autowired
	AccountsMapper accountsMapper;
	@Override
	public Accounts getAccountsExist(int userid) {
		// TODO Auto-generated method stub
		return accountsMapper.getAccountsExist(userid);
	}
	@Override
	public List<Accounts> getAccountsByUserid(int userid) {
		// TODO Auto-generated method stub
		return accountsMapper.getAccountsByUserid(userid);
	}

}
