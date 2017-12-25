package com.yfwl.yfgp.service;

import com.yfwl.yfgp.model.Fund;

public interface FundService {
	
	
	Integer insertFund(Fund fund);
	
	Fund getFundByFid(int fid);
	
	Fund getFundByStatus();
	
	Integer setDefaultFund(int fid);
	
	Integer setInitialFund(int fid);
}
