package com.yfwl.yfgp.service;

import java.util.Map;

public interface FriendRelationService {

	/**
	 * 添加好友
	 * @param loginName
	 * @param searchName
	 * @return
	 */
	public Map<String,Object> insertFriend(String loginName,String searchName);
	
	/**
	 * 删除好友
	 * @param user
	 * @param friend
	 * @return
	 */
	public boolean deleteFriend(String user,String friend);
	
	
}
