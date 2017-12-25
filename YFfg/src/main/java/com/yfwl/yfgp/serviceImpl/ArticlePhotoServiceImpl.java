package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.ArticlePhotoMapper;
import com.yfwl.yfgp.model.ArticlePhoto;
import com.yfwl.yfgp.service.ArticlePhotoService;

@Service
public class ArticlePhotoServiceImpl implements ArticlePhotoService{
	
	@Resource
	private ArticlePhotoMapper articlePhotoMapper;
	
	@Override
	public ArticlePhoto selectArtPhotoUrl(Integer articleId) {
		// TODO Auto-generated method stub
		return articlePhotoMapper.selectArtPhotoUrl(articleId);
	}

	@Override
	public List<ArticlePhoto> selectArtPhotoUrlList(Integer articleId) {
		// TODO Auto-generated method stub
		return articlePhotoMapper.selectArtPhotoUrlList(articleId);
	}

}
