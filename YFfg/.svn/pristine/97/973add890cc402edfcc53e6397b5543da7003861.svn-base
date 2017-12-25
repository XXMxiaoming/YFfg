package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.ArticleCommentMapper;
import com.yfwl.yfgp.model.ArticleComment;
import com.yfwl.yfgp.service.ArticleCommentService;

@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {
	
	@Autowired
	ArticleCommentMapper articleCommentMapper;

	@Override
	public List<ArticleComment> getCommentList(Integer articleId, Integer page) {
		// TODO Auto-generated method stub
		return articleCommentMapper.getCommentList(articleId, page);
	}

	@Override
	public Integer insertComment(ArticleComment articleComment) {
		// TODO Auto-generated method stub
		return articleCommentMapper.insertComment(articleComment);
	}

	@Override
	public Integer deleteComment(Integer commentId) {
		// TODO Auto-generated method stub
		return articleCommentMapper.deleteComment(commentId);
	}

	@Override
	public ArticleComment getComment(Integer commentID) {
		// TODO Auto-generated method stub
		return articleCommentMapper.getComment(commentID);
	}

}
