package com.yfwl.yfgp.model;

import java.util.Date;

public class Traderec {
	Integer rid;
	Integer gid;
	String stockid;
	double orderprice;
	double tradeprice;
	double charge;
	double stamp_tax;
	Integer vol;
	Integer action;
	Integer status;
	Date inserttime;
	Date tradetime;
	String name;
	
	public void setRid(Integer rid) {
		this.rid = rid;
	}
	public Integer getRid() {
		return rid;
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
	
	public void setOrderprice(double orderprice) {
		this.orderprice = orderprice;
	}
	public double getOrderprice() {
		return orderprice;
	}
	
	public void setTradeprice(double tradeprice) {
		this.tradeprice = tradeprice;
	}
	public double getTradeprice() {
		return tradeprice;
	}
	
	public void setCharge(double charge) {
		this.charge = charge;
	}
	
	public double getCharge() {
		return charge;
	}
	
	public void setStamp_tax(double stamp_tax) {
		this.stamp_tax = stamp_tax;
	}
	
	public double getStamp_tax() {
		return stamp_tax;
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
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatus() {
		return status;
	}
	
	public void setInserttime(Date inserttime) {
		this.inserttime = inserttime;
	}
	public Date getInserttime() {
		return inserttime;
	}
	
	public void setTradetime(Date tradetime) {
		this.tradetime = tradetime;
	}
	public Date getTradetime() {
		return tradetime;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
