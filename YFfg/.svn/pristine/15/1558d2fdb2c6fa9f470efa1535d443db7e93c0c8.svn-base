package com.yfwl.yfgp.service;

import java.util.Map;

import com.yfwl.yfgp.model.ThirdAppAccount;

public interface LoginByThirdAppAccountService {
	
	/**
	 * 查询第三方ID是否存在关联表中
	 * @param thirdAccountId
	 * @return
	 */
	public boolean thirdAccountIdIsHaven(String thirdAccountId);
	
	/**
	 * 插入一条关联记录
	 * @param thirdAppAccount
	 * @return
	 */
	public boolean insertConnectRecord(ThirdAppAccount thirdAppAccount);
	
	/**
	 * 查询用户绑定了哪些第三方账号
	 * @param userId
	 * @return
	 */
	public Map<String,Object> selectThirdAccount(Integer userId);
	
	ThirdAppAccount getThirdAccount(String thirdAccountId);
}
