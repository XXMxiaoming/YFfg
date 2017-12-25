package com.yfwl.yfgp.model;

public class Posi {
	private Integer id;
	private Integer gid;
	private String stockid;
	private Integer vol;
	private Integer available;
	private Integer frozen;
	private double price;
	private Integer onway;
	private String name;
	
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
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
	
	public void setVol(Integer vol) {
		this.vol = vol;
	}
	
	public Integer getVol() {
		return vol;
	}
	
	public void setAvailable(Integer available) {
		this.available = available;
	}
	
	public Integer getAvailable() {
		return available;
	}
	
	public void setFrozen(Integer frozen) {
		this.frozen = frozen;
	}
	
	public Integer getFrozen() {
		return frozen;
	}
	
	public void setPrice(double d) {
		this.price = d;
	}
	
	public double getPrice() {
		return price;
	}
	
	public void setOnway(Integer onway) {
		this.onway = onway;
	}
	
	public Integer getOnway() {
		return onway;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

}
