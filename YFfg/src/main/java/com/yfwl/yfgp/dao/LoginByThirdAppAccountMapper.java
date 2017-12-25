package com.yfwl.yfgp.dao;

import java.util.List;

import com.yfwl.yfgp.model.ThirdAppAccount;

public interface LoginByThirdAppAccountMapper {
	
	/**
	 * 查询第三方ID是否存在关联表中
	 * @param thirdAccountId
	 * @return
	 */
	Integer thirdAccountIdIsHaven(String thirdAccountId);
	
	/**
	 * 插入一条关联记录
	 * @param thirdAppAccount
	 * @return
	 */
	Integer insertConnectRecord(ThirdAppAccount thirdAppAccount);
	
	/**
	 * 查询用户绑定了哪些第三方账号
	 * @param userId
	 * @return
	 */
	List<ThirdAppAccount> selectThirdAccount(Integer userId);

	ThirdAppAccount getThirdAccount(String thirdAccountId);
}
