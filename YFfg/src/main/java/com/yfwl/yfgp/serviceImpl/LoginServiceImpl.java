package com.yfwl.yfgp.serviceImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.service.LoginService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;

@Service
public class LoginServiceImpl implements LoginService{
	
	@Autowired
	private TokenService tokenService;
	
	@Override
	public Map<String, Object> getTokenWhenLogin(Integer userId) {
		// TODO Auto-generated method stub
		// 判断是否请求过token
		boolean isHaven = tokenService.checkUserIdIsRequested(userId);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		if (isHaven == true) {
			// 之前已经请求过token
			Token token = tokenService.selectTokenByUserId(userId);
			Date expiresTime = token.getExpiresTime();
			Date nowDate = new Date();
			if (nowDate.before(expiresTime)) {
				// 当前时间在过期时间前面（token还未过期）
				dataMap.put("token", token.getAccessToken());
				dataMap.put("expiresTime", token.getExpiresTime());
				dataMap.put("tokenType", token.getTokenType());

			} else {
				String refresh_token = token.getRefreshToken();

				String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("grant_type", "client_credentials");
				paramMap.put("refresh_token", refresh_token);
				String tokenResult = GetHSTokenUtils.sendPost(url,
						paramMap, GetHSTokenUtils.CHARSET,
						GetHSTokenUtils.CHARSET, null,
						GetHSTokenUtils.BASIC, "获取令牌");
				// 新的token
				AccessToken accesstoken = JacksonUtils.json2Object(
						tokenResult, AccessToken.class);

				// 更新数据库表的token记录
				Calendar c = Calendar.getInstance();
				c.add(Calendar.SECOND,
						Integer.parseInt(accesstoken.getExpires_in()));

				token.setExpiresTime(c.getTime());
				token.setAccessToken(accesstoken.getAccess_token());
				token.setTokenType(accesstoken.getToken_type());
				token.setRefreshToken(accesstoken.getRefresh_token());
				token.setExpiresIn(accesstoken.getExpires_in());
				token.setUserId(userId);

				tokenService.updateTokenLoginOn(token);

				dataMap.put("token", accesstoken.getAccess_token());
				dataMap.put("expiresTime", c.getTime());
				dataMap.put("tokenType", accesstoken.getToken_type());
			}

		} else {
			// 第一次请求token
			String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("grant_type", "client_credentials");
			paramMap.put("open_id", userId.toString());
			String tokenResult = GetHSTokenUtils.sendPost(url,
					paramMap, GetHSTokenUtils.CHARSET,
					GetHSTokenUtils.CHARSET, null,
					GetHSTokenUtils.BASIC, "获取令牌");

			// 新的token
			AccessToken accesstoken = JacksonUtils.json2Object(
					tokenResult, AccessToken.class);

			Token token = new Token();

			Calendar c = Calendar.getInstance();
			c.add(Calendar.SECOND,
					Integer.parseInt(accesstoken.getExpires_in()));

			Date expiresTime = c.getTime();// 计算出过期时间

			token.setExpiresTime(expiresTime);
			token.setAccessToken(accesstoken.getAccess_token());
			token.setTokenType(accesstoken.getToken_type());
			token.setRefreshToken(accesstoken.getRefresh_token());
			token.setExpiresIn(accesstoken.getExpires_in());
			token.setUserId(userId);

			tokenService.insertTokenLoginOn(token);

			dataMap.put("token", accesstoken.getAccess_token());
			dataMap.put("expiresTime", c.getTime());
			dataMap.put("tokenType", accesstoken.getToken_type());
		}
		return dataMap;
	}

}
