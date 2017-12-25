package com.yfwl.yfgp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.Article;

public interface ArticleMapper {
	
	/**
	 * 查询资讯列表
	 * @return
	 */
	List<Article> selectNewsList(@Param(value="type")String type,
			@Param(value="pageCount")Integer pageCount);
	
	/**
	 * 查置顶资讯
	 * @param type
	 * @return
	 */
	List<Article> selectStickNews(@Param(value="type")String type);
	/**
	 * 更新过期置顶资讯状态
	 * @param type
	 * @return
	 */
	Integer updateArtStickStatus(@Param(value="type")String type);
	
	/**
	 * 查询头条列表
	 * @return
	 */
	List<Article> selectBigNewsList();

	Integer increaseCommentCount(Integer articleId);

	Article getArticle(Integer articleId);
	
	Article selectArticleById(Integer articleId);
}
