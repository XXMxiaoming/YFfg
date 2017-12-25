package com.yfwl.yfgp.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
public class Article {

	private Integer articleId;
	private String title;
	private String artAbstract;
	private String source;
	private String type;
	private Date addTime;
	private Integer photoType;
	private String htmlUrl;
	private Integer commentCount;
	private String sourceUrl;
	private List<ArticlePhoto> articlePhoto;
	private String isStick;
	private String author;
	private String status;
	
	private Integer praiseCount;
	private Praise praise;
	private Collect collect;
	
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtAbstract() {
		return artAbstract;
	}
	public void setArtAbstract(String artAbstract) {
		this.artAbstract = artAbstract;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public Integer getPhotoType() {
		return photoType;
	}
	public void setPhotoType(Integer photoType) {
		this.photoType = photoType;
	}
	public String getHtmlUrl() {
		return htmlUrl;
	}
	public void setHtmlUrl(String htmlUrl) {
		this.htmlUrl = htmlUrl;
	}
	public List<ArticlePhoto> getArticlePhoto() {
		return articlePhoto;
	}
	public void setArticlePhoto(List<ArticlePhoto> articlePhoto) {
		this.articlePhoto = articlePhoto;
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
	public String getIsStick() {
		return isStick;
	}
	public void setIsStick(String isStick) {
		this.isStick = isStick;
	}
	
	public void setCollect(Collect collect) {
		this.collect = collect;
	}
	
	public Collect getCollect() {
		return collect;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
