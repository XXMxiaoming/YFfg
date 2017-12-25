package com.yfwl.yfgp.service;

import java.util.Map;

import com.yfwl.yfgp.model.UserAttention;

public interface UserAttentionService {
	
	/**
	 * 查询是否关注过
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	public Map<String,Object> isAttented(Integer userId, String attUserName);
	
	/**
	 * 关注操作：
	 * 1、先查询是否关注过
	 * 2、如果已经关注过，点击则为取消关注；如果没有关注过，点击则为添加关注
	 * @param userId
	 * @param attUserName
	 * @param b 
	 * @return
	 */
	public Map<String, Object> attendOperation(Integer userId, String attUserName, boolean b);	
	
	
	/**
	 * 查询我关注的人的列表
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getMyAttentionList(Integer userId, Integer pageCount);
	
	/**
	 * 查询关注我的人的列表
	 * @param userId
	 * @return
	 */
	public Map<String, Object> getAttentMineList(Integer userId, Integer pageCount);
	
	
	public Integer getMyAttentionCount(Integer userId);
	
	public Integer getAttentMineCount(Integer userId);
	
	public Integer getAttentRelation(UserAttention userAttention);
	
	boolean attented(Integer userId, String attUserName);
	
}
