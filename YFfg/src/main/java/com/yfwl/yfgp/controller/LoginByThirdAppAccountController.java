package com.yfwl.yfgp.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.annotation.SensitivewordInterceptorAnnotation;
import com.yfwl.yfgp.easemodrest.demo.EasemobIMUsers;
import com.yfwl.yfgp.easemodrest.method.EasemobIMUsersMethod;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Identity;
import com.yfwl.yfgp.model.Integral;
import com.yfwl.yfgp.model.ThirdAppAccount;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.ValidateCode;
import com.yfwl.yfgp.service.AccountService;
import com.yfwl.yfgp.service.FutureService;
import com.yfwl.yfgp.service.IntegralService;
import com.yfwl.yfgp.service.LoginByThirdAppAccountService;
import com.yfwl.yfgp.service.LoginService;
import com.yfwl.yfgp.service.SMSendValiCodeService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.service.ValidateCodeService;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.RandomStringUtil;

@Controller
@RequestMapping("/loginByThirdAppAccount")
public class LoginByThirdAppAccountController {

	@Autowired
	private LoginByThirdAppAccountService loginByThirdAppAccountService;
	@Autowired
	private UserService userService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private ValidateCodeService validateCodeService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private IntegralService integralService;
	@Autowired
	private FutureService futureService;
	@Autowired
	private SMSendValiCodeService sMSandValiCodeService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EasemobIMUsers.class);

	/**
	 * 判断第三方Id是否存在关联表中 1，若存在，直接返回用户信息登陆 2，若不存在，需要调用手机验证接口绑定手机
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/thirdAccountIdIsHaven", method = { RequestMethod.POST })
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
				map.put("message", "操作成功，该第三方账号可登陆");
				map.put("data", dataMap);
			} else {
				map.put("status", 0);
				map.put("message", "操作成功，该第三方账号第一次登陆，需绑定手机");
				map.put("data", "");
			}
		} else {
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 第三方账号第一次登陆，需要绑定手机，先发验证码到手机验证
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/sendBindValidateCode", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> sendBindValidateCode(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String phone = request.getParameter("phone");
		Map<String, Object> map = new HashMap<String, Object>();

		if (phone != null && !phone.isEmpty()) {
			// 发送验证码给用户
			boolean isOk = sMSandValiCodeService.sendSMS(phone, "DSFDL");
			if (isOk == true) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", "");
			} else {
				map.put("status", -5);
				map.put("message", "");
				map.put("data", "验证码保存失败");
			}			
		} else {
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 第三方登录绑定账号时比对验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/validateCodeWhenDSFDL", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> validateCodeWhenDSFDL(
			HttpServletRequest request, HttpServletResponse response) {
		String phone = request.getParameter("phone");
		String paramRandomNum = request.getParameter("randomNum");
		String marker = "DSFDL";
		
		Map<String, Object> map = new HashMap<String, Object>();
		if (phone != null && !phone.isEmpty() && paramRandomNum != null && !paramRandomNum.isEmpty()) {
			
			ValidateCode validateCode = new ValidateCode();
			validateCode.setPhone(phone);
			validateCode.setMarker(marker);
			validateCode.setRandomNum(paramRandomNum);
			boolean isOk = sMSandValiCodeService.validataCode(validateCode);
			
			if (isOk == true) {
				map.put("status", 0);
				map.put("message", "验证码比对成功");
				map.put("data", "");

			} else {
				map.put("status", -5);
				map.put("message", "验证码比对错误");
				map.put("data", "");
			}
			
		} else {
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 检验手机号是否存在用户表 1，若存在，直接与第三方ID做关联 2，若不存在，走注册流程，注册到用户表，再做关联
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/validatePhoneWhenDSFDL", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> validatePhoneWhenDSFDL(
			HttpServletRequest request, HttpServletResponse response) {
		String phone = request.getParameter("phone");
		String thirdAccountId = request.getParameter("thirdAccountId");
		String type = request.getParameter("type");
		String thirdAccountName = request.getParameter("thirdAccountName");
		
		Map<String, Object> map = new HashMap<String, Object>();

		if (phone != null && !phone.isEmpty()) {
			boolean isHaven = userService.checkoutRegisterPhone(phone);
			if (isHaven == true) {
				// 该手机号码已存在用户表中，则在关联表中新增一条关联记录
				User user = userService.selectUserByLoginName(phone);
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
				ThirdAppAccount thirdAppAccount = new ThirdAppAccount();
				thirdAppAccount.setThirdAccountId(thirdAccountId);
				thirdAppAccount.setUserId(user.getUserId());
				thirdAppAccount.setType(type);
				thirdAppAccount.setThirdAccountName(thirdAccountName);
				
				boolean isInsertOk = loginByThirdAppAccountService
						.insertConnectRecord(thirdAppAccount);
				if (isInsertOk == true) {
					Map<String, Object> dataMap = loginService
							.getTokenWhenLogin(user.getUserId());
					dataMap.put("userId", user.getUserId());
					dataMap.put("userName", user.getUserName());
					dataMap.put("easemobId", user.getEasemobId());
					dataMap.put("easemobPassword", user.getEasemobPassword());
					dataMap.put("head_image", user.getHeadImage());
					dataMap.put("accountId", accountId);
					dataMap.put("integralId", integralId);
					map.put("status", 0);
					map.put("message", "操作成功，已绑定手机号");
					map.put("data", dataMap);
				} else {
					map.put("status", -5);
					map.put("message", "操作失败，服务器操作异常");
					map.put("data", "");
				}
			} else {
				// 该手机号码不存在用户表中，走注册流程
				map.put("status", 1);
				map.put("message", "操作成功，号码第一次申请，需走注册流程");
				map.put("data", "");
			}
			
		} else {
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 第一次绑定的用户走注册流程后更改用户名
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value = "/updateUsernameWhenDSFDL", method = { RequestMethod.POST })
	@ResponseBody
	@SensitivewordInterceptorAnnotation
	public Map<String, Object> updateUsernameWhenDSFDL(
			HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		//String userName = request.getParameter("userName");
		
		String userName = URLDecoder.decode((String) request.getAttribute("userName"), "utf-8");
		String phone = request.getParameter("phone");
		String thirdAccountId = request.getParameter("thirdAccountId");
		String headImage = request.getParameter("headImage");
		String type = request.getParameter("type");
		String thirdAccountName = request.getParameter("thirdAccountName");

		Map<String, Object> map = new HashMap<String, Object>();
		
		if (phone != null && !phone.isEmpty() && userName != null && !userName.isEmpty()) {
			
			if(userName.contains("*")){
				map.put("status", -7);
				map.put("message", "该名字包含敏感词，不能使用");
				map.put("data", "");
			}else{
				boolean isHaven = userService.checkoutUsername(userName);
				if (isHaven == true) {
					map.put("status", -5);
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
					user.setPhone(phone);
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
						map.put("message", "操作成功，已绑定手机号并已更改好用户名");
						map.put("data", dataMap);

					} else {
						map.put("status", -6);
						map.put("message", "操作失败，插入关联表失败");
						map.put("data", "");
					}
				}
			}
		} else {
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}
	
	/**
	 * 查询用户绑定了哪些第三方账号
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectThirdAccount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectThirdAccount(HttpServletRequest request,
			 HttpServletResponse response){
		
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Map<String, Object> map = loginByThirdAppAccountService.selectThirdAccount(userId);
		return map;
	}
	
}
