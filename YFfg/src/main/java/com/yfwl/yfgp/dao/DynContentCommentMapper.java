package com.yfwl.yfgp.dao;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.DynContentComment;

public interface DynContentCommentMapper {

	/**
	 * 根据动态ID content_id 查找评论
	 * 
	 * @param con_id
	 * @return
	 */
	List<DynContentComment> selectCommentByConId(
			@Param(value = "contentId") Integer contentId,
			@Param(value = "pageCount") Integer pageCount);

	/**
	 * 新增一条评论
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	int insert(DynContentComment record);

	/**
	 * 根据评论ID 删除评论
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	int deleteByPrimaryKey(Integer id);
	
	Integer selectCountOfComment(Integer conId);

}
