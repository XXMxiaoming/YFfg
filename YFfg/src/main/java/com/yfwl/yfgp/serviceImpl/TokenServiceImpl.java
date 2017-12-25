package com.yfwl.yfgp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.TokenMapper;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.service.TokenService;

@Service
public class TokenServiceImpl implements TokenService {
	
	@Autowired
	private TokenMapper tokenMapper;
	
	@Override
	public Integer insertNewToken(Token token) {
		// TODO Auto-generated method stub
		return tokenMapper.insertNewToken(token);
	}

	@Override
	public boolean checkOpenIdIsRequested(String openId) {
		// TODO Auto-generated method stub
		boolean isHaven = false;
		Integer checkVal = tokenMapper.checkOpenIdIsRequested(openId);
		if(checkVal > 0){
			isHaven = true;
		}
		return isHaven;
	}

	@Override
	public Token selectTokenByOpenId(String openId) {
		// TODO Auto-generated method stub
		return tokenMapper.selectTokenByOpenId(openId);
	}

	@Override
	public Integer updateToken(Token token) {
		// TODO Auto-generated method stub
		return tokenMapper.updateToken(token);
	}

	@Override
	public Integer updateUserIdOfToken(Integer userId, String openId) {
		// TODO Auto-generated method stub
		return tokenMapper.updateUserIdOfToken(userId, openId);
	}

	@Override
	public boolean checkUserIdIsRequested(Integer userId) {
		// TODO Auto-generated method stub
		boolean isHaven = false;
		Integer checkVal = tokenMapper.checkUserIdIsRequested(userId);
		if(checkVal > 0){
			isHaven = true;
		}
		return isHaven;
	}

	@Override
	public Token selectTokenByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return tokenMapper.selectTokenByUserId(userId);
	}

	@Override
	public boolean insertTokenLoginOn(Token token) {
		// TODO Auto-generated method stub
		boolean isInsertOk = false;
		Integer insertVal = tokenMapper.insertTokenLoginOn(token);
		if(insertVal > 0){
			isInsertOk = true;
		}
		return isInsertOk;
	}

	@Override
	public boolean updateTokenLoginOn(Token token) {
		// TODO Auto-generated method stub
		boolean isUpdateOk = false;
		Integer updateVal = tokenMapper.updateTokenLoginOn(token);
		if(updateVal > 0){
			isUpdateOk = true;
		}
		return isUpdateOk;
	}
	
	@Override
	public String selectTokenByLoginName(String loginName) {
		// TODO Auto-generated method stub
		return tokenMapper.selectTokenByLoginName(loginName);
	}

	@Override
	public String selectTokenByUserId2(Integer userId) {
		// TODO Auto-generated method stub
		return tokenMapper.selectTokenByUserId2(userId);
	}
}
