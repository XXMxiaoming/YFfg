package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.FundassetsMapper;
import com.yfwl.yfgp.model.Fundassets;
import com.yfwl.yfgp.service.FundassetsService;
@Service
public class FundassetsServiceImpl implements FundassetsService {

	@Autowired
	private FundassetsMapper fundassetsMapper;
	@Override
	public Integer insertFundassets(Fundassets fundassets) {
		// TODO Auto-generated method stub
		return fundassetsMapper.insertFundassets(fundassets);
	}
	@Override
	public List<Fundassets> getAssets(int fid) {
		// TODO Auto-generated method stub
		return fundassetsMapper.getAssets(fid);
	}
	@Override
	public Fundassets getFirstdate(int fid) {
		// TODO Auto-generated method stub
		return fundassetsMapper.getFirstdate(fid);
	}
	@Override
	public Fundassets getLastdate(int fid) {
		// TODO Auto-generated method stub
		return fundassetsMapper.getLastdate(fid);
	}

}
