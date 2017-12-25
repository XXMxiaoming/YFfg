package com.yfwl.yfgp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserAttention;

public interface UserAttentionMapper {
	
	/**
	 * 检测是不是第一次用关注功能
	 * @param userId
	 * @return
	 */
	Integer checkIsFirst(Integer userId);
	
	
	/**
	 * 查询是否已经关注过
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	UserAttention selectIsAttention(@Param(value = "userId") Integer userId,
			@Param(value = "attUserName") String attUserName);
	
	/**
	 * 第一次关注此人
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	Integer insertAttention(
			@Param(value = "userId") Integer userId,
			@Param(value = "attUserName") String attUserName,
			@Param(value = "isFirst") String isFirst);
	/**
	 * 之前关注过，但取消了，现在重新关注
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	Integer insertAttentionAgain(
			@Param(value = "userId") Integer userId,
			@Param(value = "attUserName") String attUserName);
			
	/**
	 * 取消关注此人
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	Integer deleteAttention(@Param(value = "userId") Integer userId,
			@Param(value = "attUserName") String attUserName);
	
	
	List<User> getMyAttentionList(@Param(value = "userId") Integer userId,
			@Param(value = "pageCount") Integer pageCount);
	
	List<User> getAttentMineList(@Param(value = "userId") Integer userId,
			@Param(value = "pageCount") Integer pageCount);

	Integer getMyAttentionCount(Integer userId);

	Integer getAttentMineCount(Integer userId);

	Integer getAttentRelation(UserAttention userAttention);
	
}
