package com.yfwl.yfgp.model;

import java.util.Date;

public class SYChatRoom {
	
	private Integer syCRId;
	private Integer crId;
	private Integer sequence;
	private Date addTime;
	public Integer getSyCRId() {
		return syCRId;
	}
	public void setSyCRId(Integer syCRId) {
		this.syCRId = syCRId;
	}
	public Integer getCrId() {
		return crId;
	}
	public void setCrId(Integer crId) {
		this.crId = crId;
	}
	public Integer getSequence() {
		return sequence;
	}
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	
	

}
