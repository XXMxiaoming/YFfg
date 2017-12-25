package com.yfwl.yfgp.dao1;

import java.util.List;

import com.yfwl.yfgp.model.Fundassets;

public interface FundassetsMapper {

	
	Integer insertFundassets(Fundassets fundassets);
	
	List<Fundassets> getAssets(int fid);
	
	Fundassets getFirstdate(int fid);
	Fundassets getLastdate(int fid);
}
