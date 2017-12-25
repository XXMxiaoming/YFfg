package com.yfwl.yfgp.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ArticleComment {
	private Integer commentId;
	private Integer articleId;
	private Integer parentId;
	private String commentContent;
	private Integer userId;
	private Date commentTime;
	private User user;
	private ArticleComment articleComment;
	private Integer praiseCount;
	private Praise praise;
	
	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}
	
	public Integer getCommentId() {
		return commentId;
	}
	
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	
	public Integer getArticleId() {
		return articleId;
	}
	
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public Integer getParentId() {
		return this.parentId;
	}
	
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	
	public String getCommentContent(){
		return commentContent;
	}
	
	public void setUserId(Integer userId){
		this.userId = userId;
	}
	
	public Integer getUserId(){
		return userId;
	}
	
	public void setCommentTime(Date commentTime) {
		this.commentTime = commentTime;
	}
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getCommentTime(){
		return commentTime;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setArticleComment(ArticleComment articleComment) {
		this.articleComment = articleComment;
	}
	
	public ArticleComment getArticleComment(){
		return articleComment;
	}
	
	public void setPraiseCount(Integer praiseCount) {
		this.praiseCount = praiseCount;
	}
	
	public Integer getPraiseCount(){
		return praiseCount;
	}
	
	public void setPraise(Praise praise) {
		this.praise = praise;
	}
	
	public Praise getPraise(){
		return praise;
	}
	
}
