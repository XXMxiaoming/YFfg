package com.yfwl.yfgp.model;

import java.util.Date;

public class Pool {
	int id;
	String stock_id;
	String stock_name;
	int type;
	int property;
	String info;
	Date created_at;
	Date updated_at;
	int status;
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	public void setStockId(String stock_id) {
		this.stock_id = stock_id;
	}
	public String getStockId() {
		return stock_id;
	}
	
	public void setStockName(String stock_name) {
		this.stock_name = stock_name;
	}
	public String getStockName() {
		return stock_name;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
	
	public void setProperty(int property) {
		this.property = property;
	}
	public int getProperty() {
		return property;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	public String getInfo() {
		return info;
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
