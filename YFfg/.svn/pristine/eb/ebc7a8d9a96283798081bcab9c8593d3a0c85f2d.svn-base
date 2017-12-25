package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.DynContentComment;

public interface DynContentCommentService {

	/**
	 * 根据动态ID content_id 查找评论
	 * 
	 * @param con_id
	 * @return
	 */
	public List<DynContentComment> selectCommentByConId(Integer contentId,
			Integer pageCount);

	/**
	 * 插入一条评论
	 * 
	 * @param dynContentComment
	 * @return
	 */
	public boolean insertComment(DynContentComment dynContentComment);

	/**
	 * 根据评论ID 删除评论
	 * 
	 * @param com_id
	 * @return
	 */
	public boolean deleteComment(Integer com_id);
	
	Integer selectCountOfComment(Integer conId);
}
