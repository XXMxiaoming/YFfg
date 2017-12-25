package com.yfwl.yfgp.dao;

import java.util.List;

import com.yfwl.yfgp.model.ArticlePhoto;

public interface ArticlePhotoMapper {
	
	ArticlePhoto selectArtPhotoUrl(Integer articleId);
	
	List<ArticlePhoto> selectArtPhotoUrlList(Integer articleId);
}
