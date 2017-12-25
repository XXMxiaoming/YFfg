package com.yfwl.yfgp.dao;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.Token;

public interface TokenMapper {

	/**
	 * 新增一条token信息
	 * 
	 * @param token
	 * @return
	 */
	Integer insertNewToken(Token token);

	/**
	 * 判断这个openid有没有申请过token
	 * 
	 * @param openId
	 * @return
	 */
	Integer checkOpenIdIsRequested(String openId);

	/**
	 * 根据open_id查找token
	 * 
	 * @param openId
	 * @return
	 */
	Token selectTokenByOpenId(String openId);

	/**
	 * 更新token记录
	 * 
	 * @param openId
	 * @return
	 */
	Integer updateToken(Token token);

	/**
	 * 更新token的userId字段
	 * 
	 * @param userId
	 * @param openId
	 * @return
	 */
	Integer updateUserIdOfToken(@Param(value = "userId") Integer userId,
			@Param(value = "openId") String openId);
	
	/**
	 * 查找这个userId有没有申请过token
	 * @param userId
	 * @return
	 */
	Integer checkUserIdIsRequested(Integer userId);
	
	/**
	 * 根据userId查找token
	 * @param userId
	 * @return
	 */
	Token selectTokenByUserId(Integer userId);
	
	/**
	 * 已登录时插入获取到的token
	 * @param token
	 * @return
	 */
	Integer insertTokenLoginOn(Token token);
	
	/**
	 * 登录时更新token记录
	 * @param token
	 * @return
	 */
	Integer updateTokenLoginOn(Token token);
	
	/**
	 * 查找用户对应的token
	 * @param loginName
	 * @return
	 */
	String selectTokenByLoginName(String loginName);
	
	/**
	 * 根据userId查找用户对应的token
	 * @param userId
	 * @return
	 */
	String selectTokenByUserId2(Integer userId);
}
