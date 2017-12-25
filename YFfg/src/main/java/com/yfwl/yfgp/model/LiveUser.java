package com.yfwl.yfgp.model;

import java.util.Date;

public class LiveUser {
	
	private Integer liveId;
	private Integer userId;
	private Date addTime;
	
	public LiveUser() {
		super();
	}

	public Integer getLiveId() {
		return liveId;
	}

	public void setLiveId(Integer liveId) {
		this.liveId = liveId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
}
