package com.yfwl.yfgp.serviceImpl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.UserLogMapper;
import com.yfwl.yfgp.service.UserLogService;

@Service
public class UserLogServiceImpl implements UserLogService {
	
	@Resource
	private UserLogMapper userLogMapper;

	@Override
	public Integer insertUserLog(Integer userId, String deviceUuid) {
		// TODO Auto-generated method stub
		return userLogMapper.insertUserLog(userId, deviceUuid);
	}

}
