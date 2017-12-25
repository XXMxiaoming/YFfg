package com.yfwl.yfgp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.ArticleComment;

public interface ArticleCommentMapper {

	List<ArticleComment> getCommentList(@Param(value="articleId")Integer articleId, @Param(value="page")Integer page);

	Integer insertComment(ArticleComment articleComment);

	Integer deleteComment(Integer commentId);

	ArticleComment getComment(Integer commentID);

}
