package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Fundassets;

public interface FundassetsService {

	Integer insertFundassets(Fundassets fundassets);
	
	
	List<Fundassets> getAssets(int fid);
	
	
	Fundassets getFirstdate(int fid);
	
	Fundassets getLastdate(int fid);
}
