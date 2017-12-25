package com.yfwl.yfgp.dao;

import com.yfwl.yfgp.model.Account;

public interface AccountMapper {

	Integer initAccount(Account account);
	
	Integer updateAccount(Account account);

	Account getAccount(Integer userId);

	Account getExistAccount(Integer accountId);
	

}
