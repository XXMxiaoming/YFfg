package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.EverydayincomeMapper;
import com.yfwl.yfgp.model.Everydayincome;
import com.yfwl.yfgp.service.EverydayincomeService;
@Service
public class EverydayincomeServiceImpl implements EverydayincomeService {
	@Autowired
	EverydayincomeMapper  everydayincomeMapper;
	@Override
	public int insertEverydayincome(Everydayincome everydayincome) {
		// TODO Auto-generated method stub
		return everydayincomeMapper.insertEverydayincome(everydayincome);
	}
	@Override
	public List<Everydayincome> getAllEverydayincome() {
		// TODO Auto-generated method stub
		return everydayincomeMapper.getAllEverydayincome();
	}
	@Override
	public int updateEverydayincome(Everydayincome everydayincome) {
		// TODO Auto-generated method stub
		return everydayincomeMapper.updateEverydayincome(everydayincome);
	}
	@Override
	public List<Everydayincome> getDisEverydayincome() {
		// TODO Auto-generated method stub
		return everydayincomeMapper.getDisEverydayincome();
	}
	@Override
	public List<Everydayincome> getAllEverydayincomeByUserid(int userid) {
		// TODO Auto-generated method stub
		return everydayincomeMapper.getAllEverydayincomeByUserid(userid);
	}
	@Override
	public Integer deleteEverydayincome(int userid) {
		// TODO Auto-generated method stub
		return everydayincomeMapper.deleteEverydayincome(userid);
	}
	@Override
	public List<Everydayincome> getAllEverydayincomeByUserid2(int userid) {
		// TODO Auto-generated method stub
		return everydayincomeMapper.getAllEverydayincomeByUserid2(userid);
	}

}
