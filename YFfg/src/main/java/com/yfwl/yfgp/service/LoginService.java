package com.yfwl.yfgp.service;

import java.util.Map;

public interface LoginService {
	
	/**
	 * 登录时获取token
	 * @return
	 */
	public Map<String,Object> getTokenWhenLogin(Integer userId);
		
}
