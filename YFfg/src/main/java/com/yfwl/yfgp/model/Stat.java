package com.yfwl.yfgp.model;

import java.util.Date;

public class Stat {
	Integer gid;
	double rd;
	double ra;
	double r7;
	double r30;
	double r6m;
	double r1y;
	double maxProfit;
	double maxLost;
	Date updatetime;
	Integer del;
	String sort;
	Accounts accounts;
	Integer cite_id;
	
	public void setGid(Integer gid) {
		this.gid = gid;
	}
	public Integer getGid() {
		return gid;
	}
	
	public void setRd(double rd) {
		this.rd = rd;
	}
	public double getRd() {
		return rd;
	}
	
	public void setRa(double ra) {
		this.ra = ra;
	}
	public double getRa() {
		return ra;
	}
	
	public void setR7(double r7) {
		this.r7 = r7;
	}
	public double getR7() {
		return r7;
	}
	
	public void setR30(double r30) {
		this.r30 = r30;
	}
	public double getR30() {
		return r30;
	}
	
	public void setR6m(double r6m) {
		this.r6m = r6m;
	}
	public double getR6m() {
		return r6m;
	}
	
	public void setMaxProfit(double maxProfit) {
		this.maxProfit = maxProfit;
	}
	public double getMaxProfit() {
		return maxProfit;
	}
	
	public void setMaxLost(double maxLost) {
		this.maxLost = maxLost;
	}
	
	public double getMaxLost() {
		return maxLost;
	}
	
	public void setR1y(double r1y) {
		this.r1y = r1y;
	}
	public double getR1y() {
		return r1y;
	}
	
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	
	public void setDel(Integer del) {
		this.del = del;
	}
	public Integer getDel() {
		return del;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getSort() {
		return sort;
	}
	
	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}
	public Accounts getAccounts() {
		return accounts;
	}
	
	public void setCiteId(Integer cite_id) {
		this.cite_id = cite_id;
	}
	public Integer getCiteId() {
		return cite_id;
	}
}
