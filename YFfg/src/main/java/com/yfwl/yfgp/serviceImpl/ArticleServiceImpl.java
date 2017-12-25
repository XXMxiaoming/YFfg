package com.yfwl.yfgp.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.ArticleMapper;
import com.yfwl.yfgp.dao.ArticlePhotoMapper;
import com.yfwl.yfgp.dao.CollectionMapper;
import com.yfwl.yfgp.dao.PraiseMapper;
import com.yfwl.yfgp.model.Article;
import com.yfwl.yfgp.model.ArticlePhoto;
import com.yfwl.yfgp.model.Collect;
import com.yfwl.yfgp.model.Praise;
import com.yfwl.yfgp.service.ArticleService;

@Service
public class ArticleServiceImpl implements ArticleService{
	
	@Autowired
	private ArticleMapper articleMapper;
	@Autowired
	private PraiseMapper praiseMapper;
	@Autowired
	private CollectionMapper collectionMapper;
	@Autowired
	private ArticlePhotoMapper articlePhotoMapper;
	/**
	 * 查询资讯列表（分登陆和未登陆两种情况）
	 * @return
	 */
	@Override
	public List<Article> selectNewsList(String type,Integer pageCount) {
		// TODO Auto-generated method stub
		List<Article> list = articleMapper.selectNewsList(type,pageCount);
		if(list.size()>0){
			for(int i = 0; i < list.size(); i++){
				
				Article article = list.get(i);
				Integer articleId = article.getArticleId();
				
				Praise praise = new Praise();
				praise.setPraiseContent(articleId);
				praise.setContentType(2);
				Integer count = praiseMapper.selectCountPraise(praise);
				article.setPraiseCount(count);
				
				List<ArticlePhoto> listArtPhoto = articlePhotoMapper.selectArtPhotoUrlList(articleId);
				if(listArtPhoto.size()>0){
					article.setArticlePhoto(listArtPhoto);
				}
			}
		}
		return list;
	}
	@Override
	public List<Article> selectNewsListLogin(String type, Integer pageCount, Integer userId) {
		List<Article> list = articleMapper.selectNewsList(type,pageCount);
		if(list.size()>0){
			for(int i = 0; i < list.size(); i++)
			{	
				
				Article article = list.get(i);
				Integer articleId = article.getArticleId();
				
				Praise praise = new Praise();
				praise.setPraiseContent(articleId);
				praise.setContentType(2);
				praise.setUserId(userId);
				Integer count = praiseMapper.selectCountPraise(praise);
				article.setPraiseCount(count);
				Praise userPraise = praiseMapper.selectPraise(praise);
				article.setPraise(userPraise);
				
				Collect collect = new Collect();
				collect.setCId(articleId);
				collect.setUserId(userId);
				collect.setCType(0);
				Collect resultCollect = collectionMapper.getCollect(collect);
				article.setCollect(resultCollect);
				
				List<ArticlePhoto> listArtPhoto = articlePhotoMapper.selectArtPhotoUrlList(articleId);
				if(listArtPhoto.size()>0){
					article.setArticlePhoto(listArtPhoto);
				}
				
			}
		}
		return list;
	}
	
	/**
	 * 查置顶资讯（分登陆和未登陆两种情况）
	 * @param type
	 * @return
	 */
	@Override
	public List<Article> selectStickNews(String type) {
		// TODO Auto-generated method stub
		List<Article> list = articleMapper.selectStickNews(type);
		if(list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				
				Article article = list.get(i);
				Integer articleId = article.getArticleId();
				
				Praise praise = new Praise();
				praise.setPraiseContent(articleId);
				praise.setContentType(2);
				Integer count = praiseMapper.selectCountPraise(praise);
				article.setPraiseCount(count);
				
				List<ArticlePhoto> listArtPhoto = articlePhotoMapper.selectArtPhotoUrlList(articleId);
				if(listArtPhoto.size()>0){
					article.setArticlePhoto(listArtPhoto);
				}
			}
		}
		return list;
	}
	@Override
	public List<Article> selectStickNewsLogin(String type,Integer userId) {
		// TODO Auto-generated method stub
		List<Article> list = articleMapper.selectStickNews(type);
		if(list.size()>0){
			for(int i = 0; i < list.size(); i++)
			{	
				Article article = list.get(i);
				Integer articleId = article.getArticleId();
				
				Praise praise = new Praise();
				praise.setPraiseContent(articleId);
				praise.setContentType(2);
				praise.setUserId(userId);
				Integer count = praiseMapper.selectCountPraise(praise);
				article.setPraiseCount(count);
				Praise userPraise = praiseMapper.selectPraise(praise);
				article.setPraise(userPraise);
				
				Collect collect = new Collect();
				collect.setCId(articleId);
				collect.setUserId(userId);
				collect.setCType(0);
				Collect resultCollect = collectionMapper.getCollect(collect);
				article.setCollect(resultCollect);
				
				List<ArticlePhoto> listArtPhoto = articlePhotoMapper.selectArtPhotoUrlList(articleId);
				if(listArtPhoto.size()>0){
					article.setArticlePhoto(listArtPhoto);
				}
			}
		}
		return list;
	}
	
	/**
	 * 查头条资讯（分登陆和未登陆两种情况）
	 * @return
	 */
	@Override
	public Map<String, Object> selectBigNewsList() {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		List<Article> list = articleMapper.selectBigNewsList();
		
		if(list.size()>0){
			for(int i = 0; i < list.size(); i++)
			{	
				Praise praise = new Praise();
				praise.setPraiseContent(list.get(i).getArticleId());
				praise.setContentType(2);
				Integer count = praiseMapper.selectCountPraise(praise);
				list.get(i).setPraiseCount(count);
			}
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", list);
			map.put("time", new Date());
		}else{
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	@Override
	public Map<String, Object> selectBigNewsListLogin(Integer userId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		List<Article> list = articleMapper.selectBigNewsList();
		
		if(list.size()>0){
			for(int i = 0; i < list.size(); i++)
			{
				Praise praise = new Praise();
				praise.setPraiseContent(list.get(i).getArticleId());
				praise.setContentType(2);
				praise.setUserId(userId);
				Integer count = praiseMapper.selectCountPraise(praise);
				list.get(i).setPraiseCount(count);
				Praise userPraise = praiseMapper.selectPraise(praise);
				list.get(i).setPraise(userPraise);
			}
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", list);
			map.put("time", new Date());
		}else{
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	@Override
	public Integer increaseCommentCount(Integer articleId) {
		// TODO Auto-generated method stub
		return articleMapper.increaseCommentCount(articleId);
	}

	@Override
	public Article getArticle(Integer articleId) {
		// TODO Auto-generated method stub
		return articleMapper.getArticle(articleId);
	}
	@Override
	public Article selectArticleById(Integer articleId) {
		// TODO Auto-generated method stub
		return articleMapper.selectArticleById(articleId);
	}
	@Override
	public Integer updateArtStickStatus(String type) {
		// TODO Auto-generated method stub
		return articleMapper.updateArtStickStatus(type);
	}

}
