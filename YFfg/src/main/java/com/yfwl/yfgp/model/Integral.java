package com.yfwl.yfgp.model;

public class Integral {
	private Integer integralId;
	private Integer userId;
	private Integer integral;
	private Integer integralLevel;
	private Integer integralState;
	private Integer integralNeed;
	
	public void setIntegralId(Integer integralId) {
		this.integralId = integralId;
	}
	
	public Integer getIntegralId(){
		return integralId;
	}
	
	public void setUserId(Integer userId){
		this.userId = userId;
	}
	
	public Integer getUserId(){
		return userId;
	}
	
	public void setIntegral(Integer integral){
		this.integral = integral;
	}
	
	public Integer getIntegral()	{
		return integral;
	}
	
	public void setIntegralLevel(Integer integralLevel){
		this.integralLevel = integralLevel;
	}
	
	public Integer getIntegralLevel(){
		return integralLevel;
	}
	
	public void setIntegralState(Integer integralState){
		this.integralState = integralState;
	}
	
	public Integer getAccountState(){
		return integralState;
	}
	
	public void setIntegralNeed(Integer integralNeed) {
		this.integralNeed = integralNeed;
	}
	
	public Integer getIntegralNeed() {
		return integralNeed;
	}

}
