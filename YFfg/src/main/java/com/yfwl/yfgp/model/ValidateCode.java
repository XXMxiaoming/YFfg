package com.yfwl.yfgp.model;

import java.util.Date;

public class ValidateCode {

	private Integer validateId;
	private String phone;
	private String randomNum;
	private String marker;
	private Date time;
	public Integer getValidateId() {
		return validateId;
	}
	public void setValidateId(Integer validateId) {
		this.validateId = validateId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRandomNum() {
		return randomNum;
	}
	public void setRandomNum(String randomNum) {
		this.randomNum = randomNum;
	}
	public String getMarker() {
		return marker;
	}
	public void setMarker(String marker) {
		this.marker = marker;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
