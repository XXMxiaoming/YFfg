package com.yfwl.yfgp.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.ThirdAppAccount;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.service.LoginByThirdAppAccountService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;

@Controller
@RequestMapping("/HSToken")
public class TokenController {
	
	@Autowired
	private TokenService tokenService;
	@Autowired
	private LoginByThirdAppAccountService loginByThirdAppAccountService;
	
	/**
	 * 通过uuid获取token
	 * @return
	 */
	@RequestMapping(value = "/getHSToken",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getAccessToken(HttpServletRequest request,
			HttpServletResponse response){
	
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		String openId = request.getParameter("openId");
		//System.out.println(openId+"hfgjd");
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(openId == null || openId == ""){			
			map.put("status", 4);  //请求失败
			map.put("message", "操作失败，openId为空");
			map.put("data", "");
		}else{
			//首先判断之前是否请求过token
			boolean isHaven = tokenService.checkOpenIdIsRequested(openId);
			
			if(isHaven == true){
				//之前已经请求过token
				Token token = tokenService.selectTokenByOpenId(openId);
				Date expiresTime = token.getExpiresTime();
				Date nowDate = new Date();
				
				if(nowDate.before(expiresTime)){
					//当前时间在过期时间前面（token还未过期）
					/**
					 * 返回:
					 * 状态码 0，操作成功
					 * ExpiresTime  过期时间
					 * AccessToken  未过期的token
					 * TokenType
					 */
					Map<String, Object> dataMap = new HashMap<String, Object>();
					
					dataMap.put("expiresTime", token.getExpiresTime());
					dataMap.put("accessToken", token.getAccessToken());
					dataMap.put("tokenType", token.getTokenType());
					
					map.put("status", 0);  //请求成功
					map.put("message", "操作成功");
					map.put("data", dataMap);
					
					return map;
				}else{
					//当前时间在过期时间后面（token已过期,需要用刷新token去重新获取）
					
					String refresh_token = token.getRefreshToken();
									
					String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("grant_type", "client_credentials");
					paramMap.put("refresh_token", refresh_token);
					String tokenResult = GetHSTokenUtils.sendPost(url, paramMap,
							GetHSTokenUtils.CHARSET, GetHSTokenUtils.CHARSET, null,
							GetHSTokenUtils.BASIC, "获取令牌");
					
					System.out.println("token==" + tokenResult);
					// 解析返回数据json
					AccessToken accesstoken = JacksonUtils.json2Object(tokenResult,
							AccessToken.class);
					System.out.println("accesstoken==" + accesstoken);
					
					//更新数据库表的token记录
					Calendar c = Calendar.getInstance(); 
					c.add(Calendar.SECOND, Integer.parseInt(accesstoken.getExpires_in()));
					
					token.setExpiresTime(c.getTime());
					token.setAccessToken(accesstoken.getAccess_token());
					token.setTokenType(accesstoken.getToken_type());
					token.setRefreshToken(accesstoken.getRefresh_token());
					token.setExpiresIn(accesstoken.getExpires_in());
					token.setOpenId(openId);
					
					tokenService.updateToken(token);
										
					/**
					 * 返回:
					 * 状态码 0，操作成功
					 * ExpiresTime  过期时间
					 * AccessToken  未过期的token
					 * TokenType
					 */
					Map<String, Object> dataMap = new HashMap<String, Object>();				
					dataMap.put("expiresTime", c.getTime());
					dataMap.put("accessToken", accesstoken.getAccess_token());
					dataMap.put("tokenType", accesstoken.getToken_type());
					
					map.put("status", 0);  //请求成功
					map.put("message", "操作成功");
					map.put("data", dataMap);
					
					return map;		
					
				}
				
			}else{
				//第一次请求token
				String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("grant_type", "client_credentials");
				paramMap.put("open_id", openId); 
				String tokenResult = GetHSTokenUtils.sendPost(url, paramMap,
						GetHSTokenUtils.CHARSET, GetHSTokenUtils.CHARSET, null,
						GetHSTokenUtils.BASIC, "获取令牌");
				
				System.out.println("token==" + tokenResult);
				// 解析返回数据json
				AccessToken accesstoken = JacksonUtils.json2Object(tokenResult,
						AccessToken.class);
				System.out.println("accesstoken==" + accesstoken);
				
				Token token = new Token();
				
				//DateFormat df=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss"); 
				Calendar c = Calendar.getInstance(); 
				c.add(Calendar.SECOND, Integer.parseInt(accesstoken.getExpires_in()));
				
				Date expiresTime = c.getTime();//计算出过期时间
				
				token.setExpiresTime(expiresTime);			
				token.setAccessToken(accesstoken.getAccess_token());
				token.setTokenType(accesstoken.getToken_type());
				token.setRefreshToken(accesstoken.getRefresh_token());
				token.setExpiresIn(accesstoken.getExpires_in());
				token.setOpenId(openId);
				
				tokenService.insertNewToken(token);
				
				/**
				 * 返回:
				 * 状态码 0，操作成功
				 * ExpiresTime  过期时间
				 * AccessToken  未过期的token
				 * TokenType
				 */
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("expiresTime", expiresTime);
				dataMap.put("accessToken", accesstoken.getAccess_token());
				dataMap.put("tokenType", accesstoken.getToken_type());
				
				map.put("status", 0);  //请求成功
				map.put("message", "操作成功");
				map.put("data", dataMap);
												
			}		
		}		
		return map;		
	}
	
	/**
	 * 通过userId获取token
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getTokenByUserId",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getTokenByUserId(HttpServletRequest request,
			HttpServletResponse response){
		
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		System.out.println(userId);
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		
		if(userId == null){
			map.put("status", 4);  //请求失败
			map.put("message", "操作失败，userId为空");
			map.put("data", "");
		}else{
			// 判断是否请求过token
			boolean isHaven = tokenService.checkUserIdIsRequested(userId);
			if (isHaven == true) {
				// 之前已经请求过token
				Token token = tokenService.selectTokenByUserId(userId);
				Date expiresTime = token.getExpiresTime();
				Date nowDate = new Date();
				if (nowDate.before(expiresTime)) {
					// 当前时间在过期时间前面（token还未过期）
					dataMap.put("accessToken", token.getAccessToken());
					dataMap.put("expiresTime", token.getExpiresTime());
					dataMap.put("tokenType", token.getTokenType());
					
					map.put("status", 0);// 登录成功
					//map.put("message", "操作成功,token未过期");
					map.put("message", "操作成功");
					map.put("data", dataMap);

					return map;
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
					System.out.println(tokenResult);
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

					dataMap.put("accessToken", accesstoken.getAccess_token());
					dataMap.put("expiresTime", c.getTime());
					dataMap.put("tokenType", accesstoken.getToken_type());
					
					map.put("status", 0);// 登录成功
					//map.put("message", "操作成功,token过期，用刷新token获取的新token");
					map.put("message", "操作成功");
					map.put("data", dataMap);

					return map;
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

				dataMap.put("accessToken", accesstoken.getAccess_token());
				dataMap.put("expiresTime", c.getTime());
				dataMap.put("tokenType", accesstoken.getToken_type());
				
				map.put("status", 0);// 登录成功
				//map.put("message", "操作成功,第一次获取的token");
				map.put("message", "操作成功");
				map.put("data", dataMap);

				return map;
			}
		}
		return map;
		
		
	}
	
	
	/**
	 * 未登录出现异常情况时强制获取token
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getTokenLoginOffWhenException",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getTokenLoginOffWhenException(HttpServletRequest request,
			HttpServletResponse response){
		
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		String openId = request.getParameter("openId");
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(openId == null || openId == ""){
			map.put("status", 4);  //请求失败
			map.put("message", "操作失败，openId为空");
			map.put("data", "");
		}else{
			//第一次请求token
			String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("grant_type", "client_credentials");
			paramMap.put("open_id", openId); 
			String tokenResult = GetHSTokenUtils.sendPost(url, paramMap,
					GetHSTokenUtils.CHARSET, GetHSTokenUtils.CHARSET, null,
					GetHSTokenUtils.BASIC, "获取令牌");
			
			System.out.println("token==" + tokenResult);
			// 解析返回数据json
			AccessToken accesstoken = JacksonUtils.json2Object(tokenResult,
					AccessToken.class);
			System.out.println("accesstoken==" + accesstoken);
			
			Token token = new Token();
			
			//DateFormat df=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss"); 
			Calendar c = Calendar.getInstance(); 
			c.add(Calendar.SECOND, Integer.parseInt(accesstoken.getExpires_in()));
			
			Date expiresTime = c.getTime();//计算出过期时间
			
			token.setExpiresTime(expiresTime);			
			token.setAccessToken(accesstoken.getAccess_token());
			token.setTokenType(accesstoken.getToken_type());
			token.setRefreshToken(accesstoken.getRefresh_token());
			token.setExpiresIn(accesstoken.getExpires_in());
			token.setOpenId(openId);
			
			boolean isHaven = tokenService.checkOpenIdIsRequested(openId);
			if(isHaven == true){
				tokenService.updateToken(token);
			}else{
				tokenService.insertNewToken(token);
			}
						
			/**
			 * 返回:
			 * 状态码 0，操作成功
			 * ExpiresTime  过期时间
			 * AccessToken  未过期的token
			 * TokenType
			 */
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("expiresTime", expiresTime);
			dataMap.put("accessToken", accesstoken.getAccess_token());
			dataMap.put("tokenType", accesstoken.getToken_type());
			
			map.put("status", 0);  //请求成功
			map.put("message", "操作成功");
			map.put("data", dataMap);
		}		
		return map;
		
	}
	
	/**
	 * 登录出现异常情况时强制获取token
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getTokenLoginOnWhenException",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getTokenLoginOnWhenException(HttpServletRequest request,
			HttpServletResponse response){
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		String userId = request.getParameter("userId");
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(userId == null || userId == ""){
			map.put("status", 4);  //请求失败
			map.put("message", "操作失败，userId为空");
			map.put("data", "");
		}else{
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
			token.setUserId(Integer.parseInt(userId));
			
			boolean isHaven = tokenService.checkUserIdIsRequested(Integer.parseInt(userId));
			if(isHaven == true){
				tokenService.updateTokenLoginOn(token);
			}else{
				tokenService.insertTokenLoginOn(token);
			}
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("expiresTime", expiresTime);
			dataMap.put("accessToken", accesstoken.getAccess_token());
			dataMap.put("tokenType", accesstoken.getToken_type());
			
			map.put("status", 0);  //请求成功
			map.put("message", "操作成功");
			map.put("data", dataMap);
		}	
		return map;
	}
	
	@RequestMapping(value = "/getTokenLoginOnWhenExceptionWeb",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getTokenLoginOnWhenExceptionWeb(HttpServletRequest request,
			HttpServletResponse response){
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		String thirdaccountId = request.getParameter("openId");
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(thirdaccountId != null && !thirdaccountId.isEmpty()){
			ThirdAppAccount thirdaccount = loginByThirdAppAccountService.getThirdAccount(thirdaccountId);
			if(thirdaccount != null){
				Integer userId = thirdaccount.getUserId();
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
				
				boolean isHaven = tokenService.checkUserIdIsRequested(userId);
				if(isHaven == true){
					tokenService.updateTokenLoginOn(token);
				}else{
					tokenService.insertTokenLoginOn(token);
				}
				
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("expiresTime", expiresTime);
				dataMap.put("accessToken", accesstoken.getAccess_token());
				dataMap.put("tokenType", accesstoken.getToken_type());
				
				map.put("status", 0);  //请求成功
				map.put("message", "操作成功");
				map.put("data", dataMap);
			}else{
				map.put("status", 4);  //请求失败
				map.put("message", "操作失败，用户不存在");
				map.put("data", "");
			}
		}else{
			map.put("status", 4);  //请求失败
			map.put("message", "操作失败，openId为空");
			map.put("data", "");
		}	
		return map;
	}
	

	
	
}
