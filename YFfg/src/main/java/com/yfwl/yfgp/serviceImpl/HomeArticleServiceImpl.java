package com.yfwl.yfgp.serviceImpl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.ArticleMapper;
import com.yfwl.yfgp.dao.HomeArticleMapper;
import com.yfwl.yfgp.dao.UserMapper;
import com.yfwl.yfgp.model.Article;
import com.yfwl.yfgp.model.ArticlePhoto;
import com.yfwl.yfgp.model.HomeArticle;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.ArticlePhotoService;
import com.yfwl.yfgp.service.HomeArticleService;

@Service
public class HomeArticleServiceImpl implements HomeArticleService {
	
	@Resource
	private HomeArticleMapper homeArticleMapper;
	@Resource
	private ArticleMapper articleMapper;
	@Resource
	private UserMapper userMapper;
	@Resource
	private ArticlePhotoService articlePhotoService;
	
	@Override
	public HomeArticle selectHomeArticle(Integer sequence) {
		// TODO Auto-generated method stub
		
		HomeArticle homeArticle = homeArticleMapper.selectHomeArticle(sequence);
		Integer articleId = homeArticle.getArticleId();
		
		Article article = articleMapper.selectArticleById(articleId);
		if(article != null){
			String artAbstract = article.getArtAbstract();
			String htmUrl = article.getHtmlUrl();
			String source = article.getSource();
			String title = article.getTitle();
			homeArticle.setTitle(title);
			homeArticle.setHtmlUrl(htmUrl);
			homeArticle.setSource(source);
			homeArticle.setArtAbstract(artAbstract);
		}
		
		ArticlePhoto articlePhoto = articlePhotoService.selectArtPhotoUrl(articleId);
		if(articlePhoto != null){
			String photoUrl = articlePhoto.getPhotoUrl();
			homeArticle.setPhotoUrl(photoUrl);
		}
		
		String author = article.getAuthor();
		if(author == null || author.isEmpty()){
			homeArticle.setAuthorId(0);
			homeArticle.setAuthorName("");
			homeArticle.setAuthorHeadImage("");
			return homeArticle;
		}else{
			Integer userId = Integer.parseInt(author);
			User user = userMapper.selectUserById(userId);
			String userName = user.getUserName();
			String headImage = user.getHeadImage();
			homeArticle.setAuthorId(userId);
			homeArticle.setAuthorName(userName);
			homeArticle.setAuthorHeadImage(headImage);
		}
		
		return homeArticle;
	}

	@Override
	public boolean updateScanCount(Integer homeId) {
		// TODO Auto-generated method stub
		Integer updateVal = homeArticleMapper.updateScanCount(homeId);
		boolean isUpdateOk = false;
		if(updateVal>0){
			isUpdateOk = true;
		}
		return isUpdateOk;
	}
	
	
}
