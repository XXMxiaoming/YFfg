package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.AccountsMapper2;
import com.yfwl.yfgp.dao.VideoMapper;
import com.yfwl.yfgp.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideoMapper videoMapper;
	
	@Override
	public List<Integer> getUserIdList() {
		List<Integer> list = videoMapper.getUserIdList();
		return list;
	}
	
}
