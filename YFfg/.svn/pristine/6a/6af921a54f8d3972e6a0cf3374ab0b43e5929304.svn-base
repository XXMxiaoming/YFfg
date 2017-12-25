package com.yfwl.yfgp.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Contact {
	private Integer contactId;
	private String userId;
	private String contactWay;
	private String info;
	private Date timeDate;
	
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	
	public Integer getContactId() {
		return contactId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}
	
	public String getContactWay() {
		return contactWay;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getInfo() {
		return info;
	}
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getTime() {
		return timeDate;
	}
	public void setTime(Date comTime) {
		this.timeDate = comTime;
	}

}
