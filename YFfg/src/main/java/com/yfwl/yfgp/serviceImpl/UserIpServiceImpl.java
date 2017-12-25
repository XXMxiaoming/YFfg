package com.yfwl.yfgp.serviceImpl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.UserIpMapper;
import com.yfwl.yfgp.model.UserIp;
import com.yfwl.yfgp.service.UserIpService;

@Service
public class UserIpServiceImpl implements UserIpService {

	@Resource
	private UserIpMapper userIpMapper;
	
	@Override
	public Integer insertIp(UserIp userIp) {
		// TODO Auto-generated method stub
		return userIpMapper.insertIp(userIp);
	}

}
