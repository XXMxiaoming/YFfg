package com.yfwl.yfgp.service;

import com.yfwl.yfgp.model.Token;

public interface TokenService {
	
	/**
	 * 新增一条token信息
	 * @param token
	 * @return
	 */
	public Integer insertNewToken(Token token);
	
	/**
	 * 判断这个openid有没有申请过token
	 * @param openId
	 * @return
	 */
	public boolean checkOpenIdIsRequested(String openId); 
	
	/**
	 * 根据open_id查找token
	 * @param openId
	 * @return
	 */
	public Token selectTokenByOpenId(String openId);
	
	/**
	 * 更新token记录
	 * @param openId
	 * @return
	 */
	public Integer updateToken(Token token);
	
	/**
	 * 更新token的userId字段
	 * @param userId
	 * @param openId
	 * @return
	 */
	public Integer updateUserIdOfToken(Integer userId,String openId);
	
	/**
	 * 查找这个userId有没有申请过token
	 * @param userId
	 * @return
	 */
	public boolean checkUserIdIsRequested(Integer userId);
	
	/**
	 * 根据userId查找token
	 * @param userId
	 * @return
	 */
	public Token selectTokenByUserId(Integer userId);
	
	/**
	 * 已登录时插入获取到的token
	 * @param token
	 * @return
	 */
	public boolean insertTokenLoginOn(Token token);
	
	/**
	 * 登录时更新token记录
	 * @param token
	 * @return
	 */
	public boolean updateTokenLoginOn(Token token);
	
	/**
	 * 查找用户对应的token
	 * @param loginName
	 * @return
	 */
	public String selectTokenByLoginName(String loginName);
	
	/**
	 * 根据userId查找用户对应的token
	 * @param userId
	 * @return
	 */
	public String selectTokenByUserId2(Integer userId);
}
