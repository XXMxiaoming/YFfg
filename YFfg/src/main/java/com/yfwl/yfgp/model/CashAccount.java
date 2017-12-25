package com.yfwl.yfgp.model;

import java.util.Date;

public class CashAccount {
	private Integer id;
	private Integer userId;
	private String password;
	private float totalCash;
	private String attachAccount;
	private Date timeTill;
	private Integer status;
	private String expand;
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId(){
		return id;
	}
	
	public void setUserId(Integer userId){
		this.userId = userId;
	}
	
	public Integer getUserId(){
		return userId;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setTotalCash(float totalCash){
		this.totalCash = totalCash;
	}
	
	public float getTotalCash()	{
		return totalCash;
	}
	
	public void setAttachAccount(String attachAccount) {
		this.attachAccount = attachAccount;
	}
	
	public String getAttachAccount() {
		return attachAccount;
	}
	
	public void setTimeTill(Date timeTill){
		this.timeTill = timeTill;
	}
	
	public Date getTimeTill(){
		return timeTill;
	}
	
	public void setStatus(Integer status){
		this.status = status;
	}
	
	public Integer getStatus(){
		return status;
	}
	
	public void setExpand(String expand) {
		this.expand = expand;
	}
	
	public String getExpand() {
		return expand;
	}
}
