package com.yfwl.yfgp.dao;


import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.FriendRelation;

public interface FriendRelationMapper {
	
	/**
	 * 检测是否第一次使用加好友功能
	 * @param loginName
	 * @return
	 */
	Integer checkIsFirst(String loginName);
	
	/**
	 * 添加好友
	 * 
	 * @param loginName
	 * @param searchName
	 * @return
	 */
	Integer insertFriend(
			@Param(value = "loginName") String loginName,
			@Param(value = "searchName") String searchName,
			@Param(value = "isFirst") String isFirst);
	
	/**
	 * 添加好友，之前添加过，但删除了（is_disabled为Y），现在重新添加
	 * @param user
	 * @param friend
	 * @return
	 */
	Integer insertFriendAgain(@Param(value = "user") String user,
			@Param(value = "friend")String friend);
	
	
	/**
	 * 查询是否早已经为好友关系
	 * 
	 * @param loginName
	 * @param searchName
	 * @return
	 */
	FriendRelation validateIsAddBefore(@Param(value = "loginName") String loginName,
			@Param(value = "searchName") String searchName);
	
	/**
	 * 删除好友
	 * @param user
	 * @param friend
	 * @return
	 */
	Integer deleteFriend(@Param(value = "user") String user,
			@Param(value = "friend")String friend);
	
	
}
