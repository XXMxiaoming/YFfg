package com.yfwl.yfgp.model;

import java.util.Date;


public class Optitask {
	private Integer id;
	private Integer gid;
	private String stockid;
	private Integer operation;
	private Integer type;
	private Integer status;
	private Date createtime;
	private Date updatetime;
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public void setGid(Integer gid) {
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
	
	public void setOperation(Integer operation) {
		this.operation = operation;
	}
	
	public Integer getOperation() {
		return this.operation;
	}
	
	public void setType(Integer type) {
		this.type = type;
	}
	
	public Integer getType() {
		return this.type;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getStatus() {
		return this.status;
	}
	
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public Date getCreatetime() {
		return this.createtime;
	}
	
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public Date getUpdatetime() {
		return this.updatetime;
	}

}
