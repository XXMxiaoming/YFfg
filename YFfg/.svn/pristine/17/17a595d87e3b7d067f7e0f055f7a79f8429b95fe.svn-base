package com.yfwl.yfgp.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Trade {
	private Integer tradeId;
	private Integer userId;
	private Integer tradeUserId;
	private String tradeInfo;
	private Integer tradeType;
	private float moneyChange;
	private Date tradeTime;
	private Integer tradeState;
	
	public void setTradeId(Integer tradeId){
		this.tradeId = tradeId;
	}
	
	public Integer getTradeId(){
		return tradeId;
	}
	
//	public void setAccountId(Integer accountId)	{
//		this.accountId = accountId;
//	}
//	
//	public Integer getAccountId(){
//		return this.accountId;
//	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setTradeUserId(Integer tradeUserId) {
		this.tradeUserId = tradeUserId;
	}
	
	public Integer getTradeUserId() {
		return tradeUserId;
	}
	
	public void setTradeInfo(String tradeInfo){
		this.tradeInfo = tradeInfo;
	}
	
	public String getTradeInfo(){
		return tradeInfo;
	}
	
	public void setTradeType(Integer tradeType)	{
		this.tradeType = tradeType;
	}
	
	public Integer getTradeType(){
		return tradeType;
	}
	
	public void setMoneyChange(float moneyChange) {
		this.moneyChange = moneyChange;
	}
	
	public float getMoneyChange(){
		return moneyChange;
	}
	
	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	public Date getTradeTime(){
		return tradeTime;
	}
	
	public void setTradeState(Integer tradeState)	{
		this.tradeState = tradeState;
	}
	
	public Integer getTradeState(){
		return tradeState;
	}
}
