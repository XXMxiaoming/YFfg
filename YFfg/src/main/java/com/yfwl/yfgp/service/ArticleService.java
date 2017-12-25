package com.yfwl.yfgp.service;

import java.util.List;
import java.util.Map;

import com.yfwl.yfgp.model.Article;

public interface ArticleService {
	
	/**
	 * 查询资讯列表（分登陆和未登陆两种情况）
	 * @return
	 */
	List<Article> selectNewsList(String type,Integer pageCount);
	List<Article> selectNewsListLogin(String type,Integer pageCount, Integer userId);
	
	/**
	 * 查置顶资讯（分登陆和未登陆两种情况）
	 * @param type
	 * @return
	 */
	List<Article> selectStickNews(String type);
	List<Article> selectStickNewsLogin(String type,Integer userId);
	/**
	 * 更新过期置顶资讯状态
	 * @param type
	 * @return
	 */
	Integer updateArtStickStatus(String type);
	
	/**
	 * 查询头条列表（分登陆和未登陆两种情况）
	 * @return
	 */
	Map<String,Object> selectBigNewsList();
	Map<String,Object> selectBigNewsListLogin(Integer userId);
	
	Integer increaseCommentCount(Integer articleId);
	
	Article getArticle(Integer articleId);
	
	Article selectArticleById(Integer articleId);
	
}
