package com.yfwl.yfgp.dao;

import com.yfwl.yfgp.model.DynContent;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface DynContentMapper {

	/**
	 * 根据id删除动态
	 * 
	 * @param id
	 * @return
	 */
	int deleteOwnContent(Integer contentId);
	int deleteContentPhoto(Integer contentId);
	int deleteContentComment(Integer contentId);


	/**
	 * 点赞数加1
	 * 
	 * @param id
	 * @return
	 */
	int updateGoodCount(Integer contentId);

	/**
	 * 点赞数-1
	 * 
	 * @param id
	 * @return
	 */
	int cutGoodCount(Integer contentId);

	/**
	 * 点赞数查询
	 * 
	 * @param id
	 * @return
	 */
	int selectGoodCount(@Param(value="contentId") Integer contentId);

	/**
	 * 转发数加1
	 * 
	 * @param id
	 * @return
	 */
	int updateForwordCount(Integer contentId);

	/**
	 * 评论数加1
	 * 
	 * @param id
	 * @return
	 */
	int updateComCount(Integer contentId);

	/**
	 * 评论数减1
	 * 
	 * @param contentId
	 * @return
	 */
	int cutComCount(Integer contentId);

	/**
	 * 评论数查询
	 * 
	 * @param contentId
	 * @return
	 */
	int selectComCount(Integer contentId);

	/**
	 * 查找自己的动态
	 * 
	 * @param id
	 * @return
	 */
	List<DynContent> selectOwnContent(
			@Param(value = "userId") Integer userId,
			@Param(value = "pageCount") Integer pageCount);

	/**
	 * 新增一条纯文字状态
	 * 
	 * @param record
	 * @return
	 */
	int addDynContentOnlyWord(DynContent record);
	
	/**
	 * 根据userName查找动态
	 * @param userName
	 * @param pageCount
	 * @return
	 */
	List<DynContent> selectDynContentByUserName(
			@Param(value = "userName") String userName,
			@Param(value = "pageCount") Integer pageCount);

	DynContent selectDynContentByConId(Integer conId);

	Integer insertDynContent(DynContent dynContent);
	
	
	
	/*
	 * 好友动态查询
	 */
	/*List<DynContent> selectFriendDynContent(
			@Param(value = "userId") Integer userId,
			@Param(value = "pageCount") Integer pageCount);*/

	Integer getDynCount(Integer userId);
	
	
	List<DynContent> selectNewOwnContent(
			@Param(value = "userId") Integer userId,
			@Param(value = "pageCount") Integer pageCount);
	
	List<DynContent> selectNewDynContentByUserName(
			@Param(value = "userName") String userName,
			@Param(value = "pageCount") Integer pageCount);
	
	/*List<DynContent> selectNewFriendDynContent(
			@Param(value = "userId") Integer userId,
			@Param(value = "pageCount") Integer pageCount);*/
	
	/**
	 * 获取符合要求的好友Id
	 * @param userId
	 * @return
	 */
	List<Integer> getFriendId(Integer userId);
	List<DynContent> selectFriendDynContent(Map<String,Object> map);
	List<DynContent> selectNewFriendDynContent(Map<String,Object> map);
			
}