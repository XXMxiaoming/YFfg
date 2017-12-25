package com.yfwl.yfgp.model;

import java.util.Date;

public class Collect {
	Integer id;
	Integer userId;
	Integer cId;
	Integer cType;
	String cTitle;
	Date cTime;
	String cSrc;
	Integer cStatus;
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setCId(Integer cId) {
		this.cId = cId;
	}
	
	public Integer getCId() {
		return cId;
	}
	
	public void setCType(Integer cType) {
		this.cType = cType;
	}
	
	public Integer getCType() {
		return cType;
	}
	
	public void setCTitle(String cTitle) {
		this.cTitle = cTitle;
	}
	
	public String getCTitle() {
		return cTitle;
	}
	
	public void setCTime(Date cTime) {
		this.cTime = cTime;
	}
	
	public Date getCTime() {
		return cTime;
	}
	
	public void setCSrc(String cSrc) {
		this.cSrc = cSrc;
	}
	
	public String getCSrc() {
		return cSrc;
	}
	
	public void setCStatus(Integer cStatus) {
		this.cStatus = cStatus;
	}
	
	public Integer getCStatus() {
		return cStatus;
	}
	
	

}
