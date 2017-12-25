package com.yfwl.yfgp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easemob.server.method.IMuserMethods;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.annotation.SensitivewordInterceptorAnnotation;
import com.yfwl.yfgp.easemodrest.demo.EasemobIMUsers;
import com.yfwl.yfgp.easemodrest.method.EasemobIMUsersMethod;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Identity;
import com.yfwl.yfgp.model.Integral;
import com.yfwl.yfgp.model.ThirdAppAccount;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserIp;
import com.yfwl.yfgp.service.AccountService;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.FutureService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.IntegralService;
import com.yfwl.yfgp.service.LoginByThirdAppAccountService;
import com.yfwl.yfgp.service.LoginService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserIpService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.CusAccessObjectUtil;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.RandomStringUtil;

@Controller
@RequestMapping("/DSFlogin")
public class LoginByThirdAppAccountController2 extends BaseController{
	
	@Autowired
	private LoginByThirdAppAccountService loginByThirdAppAccountService;
	@Autowired
	private UserService userService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private IntegralService integralService;
	@Autowired
	private FutureService futureService;
	@Autowired
	private CashService cashService;
	@Autowired
	UserIpService userIpService;
	@Autowired
	GroupService groupService;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(EasemobIMUsers.class);
	
	
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> login(
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		String thirdAccountId = request.getParameter("thirdAccountId");
		String thirdAccountName = request.getParameter("thirdAccountName");
		String headImage = request.getParameter("headImage");
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (thirdAccountId != null && !thirdAccountId.isEmpty()) {
			boolean isHaven = loginByThirdAppAccountService.thirdAccountIdIsHaven(thirdAccountId);
			if (isHaven == true) {
				User user = userService.selectUserByThirdID(thirdAccountId);
				String accountId = "";
				String integralId = "";
				Integer userId = user.getUserId();
				if(null == futureService.getIdentity(userId)) {
					Identity identity = new Identity();
					identity.setUserId(userId);
					futureService.setIdentity(identity);
				}
				if(null != accountService.getAccount(userId) && null != accountService.getAccount(userId).getAccountId()) {
					accountId = accountService.getAccount(userId).getAccountId() + "";
				}
				else {
					Account account = new Account();
					account.setUserId(userId);
					account.setPassword("");
					accountService.initAccount(account);
					accountId = accountService.getAccount(userId).getAccountId() + "";
				}
				if(null != integralService.getIntegral(userId) && null != integralService.getIntegral(userId).getIntegralId()){
					integralId = integralService.getIntegral(userId).getIntegralId() + "";
				}
				else
				{
					Integral account = new Integral();
					account.setUserId(userId);
					integralService.initIntegral(account);
					integralId = integralService.getIntegral(userId).getIntegralId() + "";
				}
				
				Map<String, Object> dataMap = loginService
						.getTokenWhenLogin(user.getUserId());
				dataMap.put("userId", user.getUserId());
				dataMap.put("easemobId", user.getEasemobId());
				dataMap.put("easemobPassword", user.getEasemobPassword());
				//接收的第三方用户名与数据库比较，相同(包含关系)则返回，不同则更新数据库再返回
				if(user.getUserName().startsWith(thirdAccountName)) {
					dataMap.put("userName", user.getUserName());
				} else {
					User u = new User();
					u.setUserId(user.getUserId());
					u.setUserName(thirdAccountName + "_" + RandomStringUtil.get5RandomNum());
					userService.updateUserInfo(u);  //更新用户名
					dataMap.put("userName", thirdAccountName);
				}
				//接收的第三方头像链接与数据库比较，相同则返回，不同则更新数据库再返回
				if(headImage.equals(user.getHeadImage())){
					dataMap.put("head_image", user.getHeadImage());
				} else {
					userService.updatHeadImage(headImage, userId.toString());
					dataMap.put("head_image", headImage);
				}
				
				String phone = user.getPhone();
				if(phone!=null&&!phone.isEmpty()){
					dataMap.put("phone", phone);
				}else{
					dataMap.put("phone", "");
				}
				dataMap.put("accountId", accountId);
				dataMap.put("integralId", integralId);
				
				CashAccount ca = cashService.getCashAccountByUserId(userId);
				if(ca != null){
					String pwd = ca.getPassword();
					if(pwd != null && !pwd.isEmpty()){
						dataMap.put("cashAccountPwd", "Y");
					}else{
						dataMap.put("cashAccountPwd", "N");
					}
				}else{
					dataMap.put("cashAccountPwd", "N");
				}
				
				Accounts accounts = new Accounts();
				accounts.setUserid(userId);
				accounts.setAttr(15);
				Accounts acc = groupService.get15Accounts(accounts);
				if(acc != null){
					dataMap.put("opiGid", acc.getGid());
				}else{
					dataMap.put("opiGid", 0);
				}
				
				map = rspFormat(dataMap, SUCCESS);
			}else{
				//String headImage = request.getParameter("headImage");
				String type = request.getParameter("type");
				//String thirdAccountName = request.getParameter("thirdAccountName");
				//String paramThirdAccountName = request.getParameter("thirdAccountName");
				String pathway = request.getParameter("pathway");
				if(type != null && !type.isEmpty() && thirdAccountName != null && !thirdAccountName.isEmpty()){
					
					//String thirdAccountName = URLDecoder.decode(paramThirdAccountName, "utf-8");
					boolean isHaven2 = userService.checkoutUsername(thirdAccountName);
					String name;
					if(isHaven2 == true){
						name = thirdAccountName + "_" + RandomStringUtil.get5RandomNum();
					}else{
						name = thirdAccountName;
					}
					
					String easemobId = RandomStringUtil.getRandomCharNum();
					String easemobPassword = RandomStringUtil.getRandomCharNum();
					User user = new User();
					user.setEasemobId(easemobId);
					user.setEasemobPassword(easemobPassword);
					user.setHeadImage(headImage);
					user.setUserName(name);
					user.setPathway(pathway);
					userService.insertUserWhenDSFDL(user);
					Integer userId = user.getUserId();
					
					// 注册环信IM用户[单个]
					IMuserMethods.createUser(easemobId,easemobPassword,thirdAccountName);
					
					// 插入关联表
					ThirdAppAccount thirdAppAccount = new ThirdAppAccount();
					thirdAppAccount.setThirdAccountId(thirdAccountId);
					thirdAppAccount.setUserId(userId);
					thirdAppAccount.setType(type);
					thirdAppAccount.setThirdAccountName(thirdAccountName);
					loginByThirdAppAccountService.insertConnectRecord(thirdAppAccount);
					
					//插入IP表
					String ip = CusAccessObjectUtil.getIpAddress(request);
					if(ip != null && !ip.isEmpty()){
						UserIp userIp = new UserIp();
						userIp.setIp(ip);
						userIp.setUserId(userId);
						userIpService.insertIp(userIp);
					}
					
					User user2 = userService.selectUserById(userId);
					String accountId = "";
					String integralId = "";
					if(null != accountService.getAccount(userId) && null != accountService.getAccount(userId).getAccountId()) {
						accountId = accountService.getAccount(userId).getAccountId() + "";
					}
					else {
						Account account = new Account();
						account.setUserId(userId);
						account.setPassword("");
						accountService.initAccount(account);
						accountId = accountService.getAccount(userId).getAccountId() + "";
					}
					if(null != integralService.getIntegral(userId) && null != integralService.getIntegral(userId).getIntegralId()){
						integralId = integralService.getIntegral(userId).getIntegralId() + "";
					}
					else
					{
						Integral account = new Integral();
						account.setUserId(userId);
						integralService.initIntegral(account);
						integralId = integralService.getIntegral(userId).getIntegralId() + "";
					}
					
					// 注册完直接登录了，返回获取到的新token
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
					Map<String, Object> dataMap = loginService
							.getTokenWhenLogin(user.getUserId());

					dataMap.put("token", accesstoken.getAccess_token());
					dataMap.put("tokenType", accesstoken.getToken_type());
					dataMap.put("expiresTime", expiresTime);
					dataMap.put("userId", userId);
					dataMap.put("userName", user2.getUserName());
					dataMap.put("phone","");
					dataMap.put("easemobId", user2.getEasemobId());
					dataMap.put("easemobPassword", user2.getEasemobPassword());
					dataMap.put("head_image", user2.getHeadImage());
					dataMap.put("accountId", accountId);
					dataMap.put("integralId", integralId);
					
					CashAccount ca = cashService.getCashAccountByUserId(userId);
					if(ca != null){
						String pwd = ca.getPassword();
						if(pwd != null && !pwd.isEmpty()){
							dataMap.put("cashAccountPwd", "Y");
						}else{
							dataMap.put("cashAccountPwd", "N");
						}
					}else{
						dataMap.put("cashAccountPwd", "N");
					}
					
					Accounts accounts = new Accounts();
					accounts.setUserid(userId);
					accounts.setAttr(15);
					Accounts acc = groupService.get15Accounts(accounts);
					if(acc != null){
						dataMap.put("opiGid", acc.getGid());
					}else{
						dataMap.put("opiGid", 0);
					}
					map = rspFormat(dataMap, SUCCESS);
					
				}else{
					map = rspFormat("", WRONG_PARAM);
				}
			}
			
		}else{
			map = rspFormat("", WRONG_PARAM);
		}
		
		return map;
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/login2", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> login2(
			HttpServletRequest request, HttpServletResponse response) {

		String thirdAccountId = request.getParameter("thirdAccountId");
		Map<String, Object> map = new HashMap<String, Object>();

		if (thirdAccountId != null && !thirdAccountId.isEmpty()) {
			boolean isHaven = loginByThirdAppAccountService.thirdAccountIdIsHaven(thirdAccountId);
					
			if (isHaven == true) {
				User user = userService.selectUserByThirdID(thirdAccountId);
				String accountId = "";
				String integralId = "";
				Integer userId = user.getUserId();
				if(null == futureService.getIdentity(userId)) {
					Identity identity = new Identity();
					identity.setUserId(userId);
					futureService.setIdentity(identity);
				}
				if(null != accountService.getAccount(userId) && null != accountService.getAccount(userId).getAccountId()) {
					accountId = accountService.getAccount(userId).getAccountId() + "";
				}
				else {
					Account account = new Account();
					account.setUserId(userId);
					account.setPassword("");
					accountService.initAccount(account);
					accountId = accountService.getAccount(userId).getAccountId() + "";
				}
				if(null != integralService.getIntegral(userId) && null != integralService.getIntegral(userId).getIntegralId()){
					integralId = integralService.getIntegral(userId).getIntegralId() + "";
				}
				else
				{
					Integral account = new Integral();
					account.setUserId(userId);
					integralService.initIntegral(account);
					integralId = integralService.getIntegral(userId).getIntegralId() + "";
				}
				Map<String, Object> dataMap = loginService
						.getTokenWhenLogin(user.getUserId());
				dataMap.put("userId", user.getUserId());
				dataMap.put("userName", user.getUserName());
				dataMap.put("easemobId", user.getEasemobId());
				dataMap.put("easemobPassword", user.getEasemobPassword());
				dataMap.put("head_image", user.getHeadImage());
				String phone = user.getPhone();
				if(phone!=null&&!phone.isEmpty()){
					dataMap.put("phone", phone);
				}else{
					dataMap.put("phone", "");
				}
				dataMap.put("accountId", accountId);
				dataMap.put("integralId", integralId);
				
				CashAccount ca = cashService.getCashAccountByUserId(userId);
				if(ca != null){
					String pwd = ca.getPassword();
					if(pwd != null && !pwd.isEmpty()){
						dataMap.put("cashAccountPwd", "Y");
					}else{
						dataMap.put("cashAccountPwd", "N");
					}
				}else{
					dataMap.put("cashAccountPwd", "N");
				}
				
				map = rspFormat(dataMap, SUCCESS);
				
			} else {
				
				String headImage = request.getParameter("headImage");
				String type = request.getParameter("type");
				String thirdAccountName = request.getParameter("thirdAccountName");
				
				if(type != null && !type.isEmpty() && thirdAccountName != null && !thirdAccountName.isEmpty()){
					
					boolean isHaven2 = userService.checkoutUsername(thirdAccountName);
					if(isHaven2 == true){
						//需要改名字
						String easemobId = RandomStringUtil.getRandomCharNum();
						String easemobPassword = RandomStringUtil.getRandomCharNum();
						User user = new User();
						user.setEasemobId(easemobId);
						user.setEasemobPassword(easemobPassword);
						user.setHeadImage(headImage);
						userService.insertUserWhenDSFDL(user);
						Integer userId = user.getUserId();
						
						// 注册环信IM用户[单个]
						IMuserMethods.createUser(easemobId,easemobPassword,thirdAccountName);
						
						// 插入关联表
						ThirdAppAccount thirdAppAccount = new ThirdAppAccount();
						thirdAppAccount.setThirdAccountId(thirdAccountId);
						thirdAppAccount.setUserId(userId);
						thirdAppAccount.setType(type);
						thirdAppAccount.setThirdAccountName(thirdAccountName);
						loginByThirdAppAccountService.insertConnectRecord(thirdAppAccount);
						
						Map<String, Object> datamap = new HashMap<String, Object>();
						datamap.put("userId", userId);
						map = rspFormat(datamap, REPEAT_USERNAME);
					
					}else{
						
						String easemobId = RandomStringUtil.getRandomCharNum();
						String easemobPassword = RandomStringUtil.getRandomCharNum();
						User user = new User();
						user.setEasemobId(easemobId);
						user.setEasemobPassword(easemobPassword);
						user.setHeadImage(headImage);
						user.setUserName(thirdAccountName);
						userService.insertUserWhenDSFDL(user);
						Integer userId = user.getUserId();
						String accountId = "";
						String integralId = "";
						if(null != accountService.getAccount(userId) && null != accountService.getAccount(userId).getAccountId()) {
							accountId = accountService.getAccount(userId).getAccountId() + "";
						}
						else {
							Account account = new Account();
							account.setUserId(userId);
							account.setPassword("");
							accountService.initAccount(account);
							accountId = accountService.getAccount(userId).getAccountId() + "";
						}
						if(null != integralService.getIntegral(userId) && null != integralService.getIntegral(userId).getIntegralId()){
							integralId = integralService.getIntegral(userId).getIntegralId() + "";
						}
						else
						{
							Integral account = new Integral();
							account.setUserId(userId);
							integralService.initIntegral(account);
							integralId = integralService.getIntegral(userId).getIntegralId() + "";
						}
						// 插入关联表
						ThirdAppAccount thirdAppAccount = new ThirdAppAccount();
						thirdAppAccount.setThirdAccountId(thirdAccountId);
						thirdAppAccount.setUserId(user.getUserId());
						thirdAppAccount.setType(type);
						thirdAppAccount.setThirdAccountName(thirdAccountName);
						
						boolean isInsertOk = loginByThirdAppAccountService
								.insertConnectRecord(thirdAppAccount);
						if (isInsertOk == true) {
							// 注册环信IM用户[单个]
							IMuserMethods.createUser(easemobId,easemobPassword,thirdAccountName);

							// 注册完直接登录了，返回获取到的新token
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
							Map<String, Object> dataMap = loginService
									.getTokenWhenLogin(user.getUserId());

							dataMap.put("token", accesstoken.getAccess_token());
							dataMap.put("tokenType", accesstoken.getToken_type());
							dataMap.put("expiresTime", expiresTime);
							dataMap.put("userId", user.getUserId());
							dataMap.put("userName", user.getUserName());
							dataMap.put("phone","");
							dataMap.put("easemobId", user.getEasemobId());
							dataMap.put("easemobPassword", user.getEasemobPassword());
							dataMap.put("head_image", user.getHeadImage());
							dataMap.put("accountId", accountId);
							dataMap.put("integralId", integralId);
							
							CashAccount ca = cashService.getCashAccountByUserId(userId);
							if(ca != null){
								String pwd = ca.getPassword();
								if(pwd != null && !pwd.isEmpty()){
									dataMap.put("cashAccountPwd", "Y");
								}else{
									dataMap.put("cashAccountPwd", "N");
								}
							}else{
								dataMap.put("cashAccountPwd", "N");
							}
							
							map = rspFormat(dataMap, SUCCESS);
							
						} else {
							map = rspFormat("", FAIL);
						}
					}
					
				}else{
					map = rspFormat("", WRONG_PARAM);
				}
			}
		} else {
			map = rspFormat("", WRONG_PARAM);
		}
		return map;
	}
	
	
	@RequestMapping(value = "/setname", method = { RequestMethod.POST })
	@ResponseBody
	@SensitivewordInterceptorAnnotation
	public Map<String, Object> setUsername(
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		
		String id = request.getParameter("userId");
		String userName = URLDecoder.decode((String) request.getAttribute("userName"), "utf-8");
		Map<String, Object> map = new HashMap<String, Object>();
		if(id!=null && !id.isEmpty() && userName!=null && !userName.isEmpty()){
			
			if(userName.contains("*")){
				map = rspFormat("", ILLEGAL_USERNAME);
			}else{
				
				boolean isHaven = userService.checkoutUsername(userName);
				if(isHaven == true){
					map = rspFormat("", REPEAT_USERNAME);
				}else{
					Integer userId = Integer.parseInt(id);
					User user = new User();
					user.setUserId(userId);
					user.setUserName(userName);
					boolean isOk = userService.updateUserInfo(user);
					if(isOk == true){
						
						User user2 = userService.selectUserById(userId);
						String accountId = "";
						String integralId = "";
						if(null != accountService.getAccount(userId) && null != accountService.getAccount(userId).getAccountId()) {
							accountId = accountService.getAccount(userId).getAccountId() + "";
						}
						else {
							Account account = new Account();
							account.setUserId(userId);
							account.setPassword("");
							accountService.initAccount(account);
							accountId = accountService.getAccount(userId).getAccountId() + "";
						}
						if(null != integralService.getIntegral(userId) && null != integralService.getIntegral(userId).getIntegralId()){
							integralId = integralService.getIntegral(userId).getIntegralId() + "";
						}
						else
						{
							Integral account = new Integral();
							account.setUserId(userId);
							integralService.initIntegral(account);
							integralId = integralService.getIntegral(userId).getIntegralId() + "";
						}
						
						// 注册完直接登录了，返回获取到的新token
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
						Map<String, Object> dataMap = loginService
								.getTokenWhenLogin(user.getUserId());

						dataMap.put("token", accesstoken.getAccess_token());
						dataMap.put("tokenType", accesstoken.getToken_type());
						dataMap.put("expiresTime", expiresTime);
						dataMap.put("userId", userId);
						dataMap.put("userName", user2.getUserName());
						dataMap.put("phone","");
						dataMap.put("easemobId", user2.getEasemobId());
						dataMap.put("easemobPassword", user2.getEasemobPassword());
						dataMap.put("head_image", user2.getHeadImage());
						dataMap.put("accountId", accountId);
						dataMap.put("integralId", integralId);
						
						CashAccount ca = cashService.getCashAccountByUserId(userId);
						if(ca != null){
							String pwd = ca.getPassword();
							if(pwd != null && !pwd.isEmpty()){
								dataMap.put("cashAccountPwd", "Y");
							}else{
								dataMap.put("cashAccountPwd", "N");
							}
						}else{
							dataMap.put("cashAccountPwd", "N");
						}
						
						map = rspFormat(dataMap, SUCCESS);
						
					}else{
						map = rspFormat("", FAIL);
					}
				}
			}
			
		}else{
			map = rspFormat("", WRONG_PARAM);
		}
		return map;
	}
	
	
	@RequestMapping(value = "/DSFaccIsHaven", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> thirdAccountIdIsHaven(
			HttpServletRequest request, HttpServletResponse response) {

		String thirdAccountId = request.getParameter("thirdAccountId");
		Map<String, Object> map = new HashMap<String, Object>();

		if (thirdAccountId != null && !thirdAccountId.isEmpty()) {
			boolean isHaven = loginByThirdAppAccountService
					.thirdAccountIdIsHaven(thirdAccountId);
			if (isHaven == true) {
				User user = userService.selectUserByThirdID(thirdAccountId);
				String accountId = "";
				String integralId = "";
				Integer userId = user.getUserId();
				if(null == futureService.getIdentity(userId)) {
					Identity identity = new Identity();
					identity.setUserId(userId);
					futureService.setIdentity(identity);
				}
				if(null != accountService.getAccount(userId) && null != accountService.getAccount(userId).getAccountId()) {
					accountId = accountService.getAccount(userId).getAccountId() + "";
				}
				else {
					Account account = new Account();
					account.setUserId(userId);
					account.setPassword("");
					accountService.initAccount(account);
					accountId = accountService.getAccount(userId).getAccountId() + "";
				}
				if(null != integralService.getIntegral(userId) && null != integralService.getIntegral(userId).getIntegralId()){
					integralId = integralService.getIntegral(userId).getIntegralId() + "";
				}
				else
				{
					Integral account = new Integral();
					account.setUserId(userId);
					integralService.initIntegral(account);
					integralId = integralService.getIntegral(userId).getIntegralId() + "";
				}
				Map<String, Object> dataMap = loginService
						.getTokenWhenLogin(user.getUserId());
				dataMap.put("userId", user.getUserId());
				dataMap.put("userName", user.getUserName());
				dataMap.put("easemobId", user.getEasemobId());
				dataMap.put("easemobPassword", user.getEasemobPassword());
				dataMap.put("head_image", user.getHeadImage());
				dataMap.put("phone", user.getPhone());
				dataMap.put("accountId", accountId);
				dataMap.put("integralId", integralId);
				map.put("status", 1);
				map.put("message", "操作成功，该第三方账号已登陆");
				map.put("data", dataMap);
			} else {
				map.put("status", 0);
				map.put("message", "操作成功，该第三方账号第一次登陆，走注册流程");
				map.put("data", "");
			}
		} else {
			map.put("status", 4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/insertUserDSFDL", method = { RequestMethod.POST })
	@ResponseBody
	@SensitivewordInterceptorAnnotation
	public Map<String, Object> insertUserWhenDSFDL(
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		//String userName = request.getParameter("userName");
		
		String userName = URLDecoder.decode((String) request.getAttribute("userName"), "utf-8");
		//String phone = request.getParameter("phone");
		String thirdAccountId = request.getParameter("thirdAccountId");
		String headImage = request.getParameter("headImage");
		String type = request.getParameter("type");
		String thirdAccountName = request.getParameter("thirdAccountName");

		Map<String, Object> map = new HashMap<String, Object>();
		
		if(userName != null && !userName.isEmpty() && thirdAccountId != null && !thirdAccountId.isEmpty() 
				&& type != null && !type.isEmpty() && thirdAccountName != null && !thirdAccountName.isEmpty()){
			
			if(userName.contains("*")){
				map.put("status", 5);
				map.put("message", "该名字包含敏感词，不能使用");
				map.put("data", "");
			}else{
				boolean isHaven = userService.checkoutUsername(userName);
				if (isHaven == true) {
					map.put("status", 7);
					map.put("message", "用户名已存在，重新输入");
					map.put("data", "");
				} else {

					String easemobId = RandomStringUtil.getRandomCharNum();
					String easemobPassword = RandomStringUtil.getRandomCharNum();
					User user = new User();
					user.setEasemobId(easemobId);
					user.setEasemobPassword(easemobPassword);
					user.setHeadImage(headImage);
					user.setUserName(userName);
					//user.setPhone(phone);
					userService.insertUserWhenDSFDL(user);
					Integer userId = user.getUserId();
					String accountId = "";
					String integralId = "";
					if(null != accountService.getAccount(userId) && null != accountService.getAccount(userId).getAccountId()) {
						accountId = accountService.getAccount(userId).getAccountId() + "";
					}
					else {
						Account account = new Account();
						account.setUserId(userId);
						account.setPassword("");
						accountService.initAccount(account);
						accountId = accountService.getAccount(userId).getAccountId() + "";
					}
					if(null != integralService.getIntegral(userId) && null != integralService.getIntegral(userId).getIntegralId()){
						integralId = integralService.getIntegral(userId).getIntegralId() + "";
					}
					else
					{
						Integral account = new Integral();
						account.setUserId(userId);
						integralService.initIntegral(account);
						integralId = integralService.getIntegral(userId).getIntegralId() + "";
					}
					// 插入关联表
					ThirdAppAccount thirdAppAccount = new ThirdAppAccount();
					thirdAppAccount.setThirdAccountId(thirdAccountId);
					thirdAppAccount.setUserId(user.getUserId());
					thirdAppAccount.setType(type);
					thirdAppAccount.setThirdAccountName(thirdAccountName);
					
					boolean isInsertOk = loginByThirdAppAccountService
							.insertConnectRecord(thirdAppAccount);
					if (isInsertOk == true) {
						// 注册环信IM用户[单个]
						ObjectNode datanode = JsonNodeFactory.instance.objectNode();
						datanode.put("username", easemobId);
						datanode.put("password", easemobPassword);
						datanode.put("nickname", userName);
						ObjectNode createNewIMUserSingleNode = EasemobIMUsersMethod
								.createNewIMUserSingle(datanode);
						if (null != createNewIMUserSingleNode) {
							LOGGER.info("注册IM用户[单个]: "
									+ createNewIMUserSingleNode.toString());
						}

						// 注册完直接登录了，返回获取到的新token
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
						Map<String, Object> dataMap = loginService
								.getTokenWhenLogin(user.getUserId());

						dataMap.put("token", accesstoken.getAccess_token());
						dataMap.put("tokenType", accesstoken.getToken_type());
						dataMap.put("expiresTime", expiresTime);
						dataMap.put("userId", user.getUserId());
						dataMap.put("userName", user.getUserName());
						dataMap.put("easemobId", user.getEasemobId());
						dataMap.put("easemobPassword", user.getEasemobPassword());
						dataMap.put("head_image", user.getHeadImage());
						dataMap.put("accountId", accountId);
						dataMap.put("integralId", integralId);
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", dataMap);

					} else {
						map.put("status", 6);
						map.put("message", "操作失败，插入关联表失败");
						map.put("data", "");
					}
				}
			}
		}else{
			map.put("status", 4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}
}
