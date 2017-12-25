package com.yfwl.yfgp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.FundMapper;
import com.yfwl.yfgp.model.Fund;
import com.yfwl.yfgp.service.FundService;
@Service
public class FunServiceImpl implements FundService {
	@Autowired
	private FundMapper fundMapper;
	
	
	@Override
	public Integer insertFund(Fund fund) {
		// TODO Auto-generated method stub
		return fundMapper.insertFund(fund);
	}


	@Override
	public Fund getFundByFid(int fid) {
		// TODO Auto-generated method stub
		return fundMapper.getFundByFid(fid);
	}


	@Override
	public Fund getFundByStatus() {
		// TODO Auto-generated method stub
		return fundMapper.getFundByStatus();
	}


	@Override
	public Integer setDefaultFund(int fid) {
		// TODO Auto-generated method stub
		return fundMapper.setDefaultFund(fid);
	}


	@Override
	public Integer setInitialFund(int fid) {
		// TODO Auto-generated method stub
		return fundMapper.setInitialFund(fid);
	}

}
