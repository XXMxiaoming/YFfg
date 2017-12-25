package com.yfwl.yfgp.model;

import java.util.Date;

public class Stockinfo {
	Integer id;
	String stockid;
	String name;
	Integer market;
	Integer bspoint;
	Integer date;
	Date updatetime;
	Date createtime;
	
	
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public Integer getDate() {
		return date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	public Integer getBspoint() {
		return bspoint;
	}
	public void setBspoint(Integer bspoint) {
		this.bspoint = bspoint;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	
	public void setStockid(String stockid) {
		this.stockid = stockid;
	}
	public String getStockid() {
		return stockid;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setMarket(Integer market) {
		this.market = market;
	}
	public Integer getMarket() {
		return market;
	}
}
