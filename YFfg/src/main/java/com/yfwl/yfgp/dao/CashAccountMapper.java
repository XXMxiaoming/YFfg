package com.yfwl.yfgp.dao;

import java.util.List;

import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.User;

public interface CashAccountMapper {

	Integer initCashAccount(CashAccount cashAccount);

	Integer updateCashAccount(CashAccount cashAccount);

	CashAccount getCashAccount(CashAccount cashAccount);

	CashAccount getCashAccountByUserId(Integer userId);

	Integer initCashAccountByUserId(Integer userId);
	
	/*
	 * 根据文章作者ID获取订阅者信息
	 */
	List<User> getUserMessageById(Integer userId);
	
}
