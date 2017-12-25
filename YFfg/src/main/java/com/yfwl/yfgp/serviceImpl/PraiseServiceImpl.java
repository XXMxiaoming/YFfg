package com.yfwl.yfgp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.PraiseMapper;
import com.yfwl.yfgp.model.Praise;
import com.yfwl.yfgp.service.PraiseService;

@Service
public class PraiseServiceImpl implements PraiseService {

	@Autowired
	PraiseMapper praiseMapper;
	
	@Override
	public Integer insertPraise(Praise praise) {
		// TODO Auto-generated method stub
		return praiseMapper.insertPraise(praise);
	}

	@Override
	public Integer deletePraise(Praise praise) {
		// TODO Auto-generated method stub
		return praiseMapper.deletePraise(praise);
	}

	@Override
	public Praise selectPraise(Praise praise) {
		// TODO Auto-generated method stub
		return praiseMapper.selectPraise(praise);
	}

	@Override
	public Integer selectCountPraise(Praise praise) {
		// TODO Auto-generated method stub
		return praiseMapper.selectCountPraise(praise);
	}

}
