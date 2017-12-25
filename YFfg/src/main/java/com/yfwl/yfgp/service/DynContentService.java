package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.DynContent;

public interface DynContentService {

	
	/**
	 * 点赞数加1
	 * @param id
	 * @return
	 */
	public boolean updateGoodCount(Integer contentId);
	
	/**
	 * 点赞数-1
	 * @param id
	 * @return
	 */
	public boolean cutGoodCount(Integer contentId);
	
	/**
	 * 点赞数查询
	 * @param id
	 * @return
	 */
	public Integer selectGoodCount(Integer contentId);
	
	/**
	 * 转发数加1
	 * @param id
	 * @return
	 */
	public boolean updateForwordCount(Integer contentId);
	
	/**
	 * 评论数加1
	 * @param id
	 * @return
	 */
	public boolean updateComCount(Integer contentId);
	
	/**
     * 评论数减1
     * @param contentId
     * @return
     */
	public boolean cutComCount(Integer contentId);
	
	 /**
     * 评论数查询
     * @param contentId
     * @return
     */
    public int selectComCount(Integer contentId);
	
	/**
	 * 查找自己的动态
	 * @param id
	 * @return
	 */
	public List<DynContent> selectOwnContent(Integer userId,Integer pageCount);
	
	/**
	 * 删除自己的动态
	 * @param id
	 * @return
	 */
	public boolean deleteOwnContent(Integer id);
	public boolean deleteContentPhoto(Integer id);
	public boolean deleteContentComment(Integer contentId);
	
	/**
     * 新增一条纯文字状态
     * @param record
     * @return
     */
    public int addDynContentOnlyWord(DynContent record);
   
    /**
	 * 根据userName查找动态
	 * @param userName
	 * @param pageCount
	 * @return
	 */
	public List<DynContent> selectDynContentByUserName(String userName,Integer pageCount);
	
	
	
	public DynContent selectDynContentByConId(Integer conId);
	
	
	
	/**
	 * 转发一条动态
	 */
	public Integer insertDynContent(DynContent dynContent);
	
	
	/*
	 * 好友动态查询
	 */
	/*public List<DynContent> selectFriendDynContent(Integer userId,Integer pageCount);*/
	
	public List<DynContent> selectFriendDynContentByList(List<Integer> list,Integer pageCount);		
			
	public Integer getDynCount(Integer userId);
	
	
	public List<DynContent> selectNewOwnContent(Integer userId,Integer pageCount);
	
	
	public List<DynContent> selectNewDynContentByUserName(String userName,Integer pageCount);
	
	/*public List<DynContent> selectNewFriendDynContent(Integer userId,Integer pageCount);*/
	
	List<DynContent> selectFriendDynContent(Integer userId,Integer pageCount);
	List<DynContent> selectNewFriendDynContent(Integer userId,Integer pageCount,Integer pageSize);
	
}
