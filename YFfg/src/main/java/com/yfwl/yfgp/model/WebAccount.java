package com.yfwl.yfgp.model;

import java.util.Date;

public class WebAccount {
	int id;
	int user_id;
	int point;
	Date created_at;
	Date updated_at;
	int status;
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	public void setUserId(int user_id) {
		this.user_id = user_id;
	}
	public int getUserId() {
		return user_id;
	}
	
	
	public void setPoint(int point) {
		this.point = point;
	}
	public int getPoint() {
		return point;
	}
	
	public void setCreatedAt(Date created_at) {
		this.created_at = created_at;
	}
	public Date getCreatedAt() {
		return created_at;
	}
	
	public void setUpdatedAt(Date updated_at) {
		this.updated_at = updated_at;
	}
	public Date getUpdatedAt() {
		return updated_at;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
}
