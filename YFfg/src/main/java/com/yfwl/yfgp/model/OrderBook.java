package com.yfwl.yfgp.model;

import java.util.Date;

public class OrderBook {
	Integer orderid;
	Integer gid;
	String stockid;
	double price;
	Integer vol;
	Integer action;
	Date inserttime;
	String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}
	public Integer getOrderid() {
		return orderid;
	}
	
	public void setGid(Integer gid){
		this.gid = gid;
	}
	public Integer getGid() {
		return gid;
	}
	
	public void setStockid(String stockid) {
		this.stockid = stockid;
	}
	
	public String getStockid() {
		return stockid;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}
	public double getPrice() {
		return price;
	}
	
	public void setVol(Integer vol) {
		this.vol = vol;
	}
	public Integer getVol() {
		return vol;
	}
	
	public void setAction(Integer action) {
		this.action = action;
	}
	public Integer getAction() {
		return action;
	}
	
	public void setInserttime(Date inserttime) {
		this.inserttime = inserttime;
	}
	public Date getInserttime() {
		return inserttime;
	}
	
	
}