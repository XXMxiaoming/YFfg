package com.yfwl.yfgp.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.easemob.server.method.IMuserMethods;
import com.yfwl.yfgp.annotation.SensitivewordInterceptorAnnotation;
import com.yfwl.yfgp.easemodrest.demo.EasemobIMUsers;
import com.yfwl.yfgp.model.AccessToken;
import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Identity;
import com.yfwl.yfgp.model.Integral;
import com.yfwl.yfgp.model.Mem;
import com.yfwl.yfgp.model.Picture;
import com.yfwl.yfgp.model.Token;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserAttention;
import com.yfwl.yfgp.service.AccountService;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.DynContentService;
import com.yfwl.yfgp.service.DynVIPService;
import com.yfwl.yfgp.service.FutureService;
import com.yfwl.yfgp.service.IntegralService;
import com.yfwl.yfgp.service.SMSendValiCodeService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserAttentionService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.service.ValidateCodeService;
import com.yfwl.yfgp.utils.ControllerTest;
import com.yfwl.yfgp.utils.GetHSTokenUtils;
import com.yfwl.yfgp.utils.ImageUtil;
import com.yfwl.yfgp.utils.JacksonUtils;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.RandomStringUtil;
import com.yfwl.yfgp.utils.SortByMap;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ValidateCodeService validateCodeService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private IntegralService integralService;
	@Autowired
	private FutureService futureService;
	@Autowired
	private UserAttentionService userAttentionService;
	@Autowired
	private DynContentService dynContentService;
	@Autowired
	private SMSendValiCodeService sMSandValiCodeService;
	@Autowired
	private CashService cashService;
	@Autowired
	private DynVIPService dynVIPService;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EasemobIMUsers.class);

	/**
	 * 查找user表中有没有该注册过的手机号码，没有的话发短信给用户
	 * 
	 * @param phone
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/checkoutRegisterPhone", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> checkoutRegisterPhone(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String phone = request.getParameter("phone");
		boolean isHaven = userService.checkoutRegisterPhone(phone);
		Map<String, Object> map = new HashMap<String, Object>();
		if (isHaven == true) {
			map.put("status", 4);// 该手机号已注册过
			map.put("msg", "该手机号已注册过");
			map.put("data", "");
		} else {
			// 手机号没有注册过，返回四位验证码
			boolean isOk = sMSandValiCodeService.sendSMS(phone, "ZC");
			if (isOk == true) {
				map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", "");
			} else {
				map.put("status", 4);
				map.put("msg", "短信发送失败");
				map.put("data", "");
			}
		}
		return map;
	}

	/**
	 * 新增一个用户
	 * 
	 * @param user
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/insertUser", method = { RequestMethod.POST })
	@ResponseBody
	@SensitivewordInterceptorAnnotation
	public Map<String, Object> insertUser(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		String userName = URLDecoder.decode(
				(String) request.getAttribute("userName"), "utf-8");
		// String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		String phone = request.getParameter("phone");
		Map<String, Object> map = new HashMap<String, Object>();

		if (userName.contains("*")) {
			map.put("status", 5);
			map.put("msg", "该名字包含敏感词，不能使用");
			map.put("data", "");
		} else {
			User user = new User();
			boolean isHaven = userService.checkoutUsername(userName);
			if (isHaven == true) {
				map.put("status", 4);// 该用户名已经存在
				map.put("msg", "该用户名已经存在");
				map.put("data", "");
			} else {
				user.setUserName(userName);
				user.setPassword(password);
				user.setPhone(phone);
				String easemobId = RandomStringUtil.getRandomCharNum();// 随机生成一个环信ID
				user.setEasemobId(easemobId);
				String easemobPassword = RandomStringUtil.getRandomCharNum();// 随机生成一个环信密码
				user.setEasemobPassword(easemobPassword);
				userService.insertUser(user);
				Integer userId = user.getUserId();
				String accountId = "";
				String integralId = "";
				if (null == futureService.getIdentity(userId)) {
					Identity identity = new Identity();
					identity.setUserId(userId);
					futureService.setIdentity(identity);
				}
				if (null != accountService.getAccount(userId)
						&& null != accountService.getAccount(userId)
								.getAccountId()) {
					accountId = accountService.getAccount(userId)
							.getAccountId() + "";
				} else {
					Account account = new Account();
					account.setUserId(userId);
					account.setPassword("");
					int hasInit = accountService.initAccount(account);
					accountId = accountService.getAccount(userId)
							.getAccountId() + "";
				}
				if (null != integralService.getIntegral(userId)
						&& null != integralService.getIntegral(userId)
								.getIntegralId()) {
					integralId = integralService.getIntegral(userId)
							.getIntegralId() + "";
				} else {
					Integral account = new Integral();
					account.setUserId(userId);
					int hasInit = integralService.initIntegral(account);
					integralId = integralService.getIntegral(userId)
							.getIntegralId() + "";
				}
				Map<String, Object> dataMap = new HashMap<String, Object>();

				dataMap.put("userId", userId);
				dataMap.put("userName", userName);
				dataMap.put("easemobId", easemobId);
				dataMap.put("easemobPassword", easemobPassword);
				dataMap.put("accountId", accountId);
				dataMap.put("integralId", integralId);

				/**
				 * 注册IM用户[单个]
				 */
				/*
				 * ObjectNode datanode = JsonNodeFactory.instance.objectNode();
				 * datanode.put("username", easemobId); datanode.put("password",
				 * easemobPassword); datanode.put("nickname", userName);
				 * ObjectNode createNewIMUserSingleNode = EasemobIMUsersMethod
				 * .createNewIMUserSingle(datanode); if (null !=
				 * createNewIMUserSingleNode) { LOGGER.info("注册IM用户[单个]: " +
				 * createNewIMUserSingleNode.toString()); }
				 */

				IMuserMethods.createUser(easemobId, easemobPassword, userName);

				/**
				 * 注册完直接登录了，返回获取到的新token
				 */
				String url = "http://sandbox.hscloud.cn/oauth2/oauth2/token";
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("grant_type", "client_credentials");
				paramMap.put("open_id", userId.toString());
				String tokenResult = GetHSTokenUtils.sendPost(url, paramMap,
						GetHSTokenUtils.CHARSET, GetHSTokenUtils.CHARSET, null,
						GetHSTokenUtils.BASIC, "获取令牌");

				// 新的token
				AccessToken accesstoken = JacksonUtils.json2Object(tokenResult,
						AccessToken.class);

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
				dataMap.put("tokenType", accesstoken.getToken_type());
				dataMap.put("expiresTime", expiresTime);

				map.put("status", 0);// 注册成功
				map.put("msg", "注册成功");
				map.put("data", dataMap);

			}
		}

		return map;
	}

	/**
	 * 更新用户信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/updateUserInfo", method = { RequestMethod.POST })
	@ResponseBody
	@SensitivewordInterceptorAnnotation
	public Map<String, Object> updateUserInfo(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String userName = URLDecoder.decode(
				(String) request.getAttribute("userName"), "utf-8");
		// String userName = request.getParameter("userName");

		String paramToken = request.getParameter("token");
		String dbToken = tokenService.selectTokenByUserId2(userId);

		Map<String, Object> map = new HashMap<String, Object>();

		if (userName.contains("*")) {
			map.put("status", 5);
			map.put("msg", "该名字包含敏感词，不能使用");
			map.put("data", "");
		} else {
			if (dbToken.equals(paramToken)) {
				boolean isHaven = userService.checkoutUsername(userName);
				if (isHaven == true) {
					map.put("status", 4);
					map.put("msg", "该用户名已存在");
					map.put("data", "");
				} else {
					User user = new User();
					user.setUserId(userId);
					user.setUserName(userName);
					// user.setPassword(password);
					boolean updateOk = userService.updateUserInfo(user);
					if (updateOk == true) {
						map.put("status", 0);
						map.put("msg", "操作成功");
						map.put("data", "");
					} else {
						map.put("status", 5);
						map.put("msg", "系统出现异常");
						map.put("data", "");
					}
				}
			} else {
				map.put("status", 7);
				map.put("msg", "token错误");
				map.put("data", "");
			}
		}
		return map;
	}

	/**
	 * 根据id查找user（可批量查找）
	 * 
	 * @param loginName
	 * @return
	 */
	@RequestMapping(value = "/selectUserById", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectUserById(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();

		String userIdStr = request.getParameter("userIdStr");
		String[] userIdStrArr = userIdStr.split(",");
		List<String> userIdList = java.util.Arrays.asList(userIdStrArr);
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();

		if (userIdList.size() < 0) {
			map.put("status", 4);
			map.put("msg", "参数错误");
			map.put("data", "");
		} else {
			for (int i = 0; i < userIdList.size(); i++) {
				Integer userId = Integer.parseInt(userIdList.get(i));
				User user = userService.selectUserById(userId);
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("userId", user.getUserId());
				userMap.put("userName", user.getUserName());
				userMap.put("headImage", user.getHeadImage());
				userMap.put("easemobId", user.getEasemobId());
				userMap.put("userSex", user.getUserSex());
				userMap.put("userLevel", user.getUserLevel());
				userMap.put("userStatus", user.getUserStatus());
				userList.add(userMap);
			}
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", userList);
		}
		return map;
	}

	/**
	 * 根据easemobId查找userName，头像
	 * 
	 * @param easemobId
	 * @return
	 */
	@RequestMapping(value = "/selectUsernameByeasemobId", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectUsernameByeasemobId(
			HttpServletRequest request, HttpServletResponse response) {

		String easemobId = request.getParameter("easemobId");
		User user = userService.selectUsernameByeasemobId(easemobId);

		Map<String, Object> map = new HashMap<String, Object>();
		if (user != null) {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("userId", user.getUserId());
			dataMap.put("userName", user.getUserName());
			dataMap.put("headImage", user.getHeadImage());
			String phone = user.getPhone();
			if (phone != null && !phone.isEmpty()) {
				dataMap.put("phone", phone);
			} else {
				dataMap.put("phone", "");
			}
			Integer userId = user.getUserId();
			CashAccount ca = cashService.getCashAccountByUserId(userId);
			if (ca != null) {
				String pwd = ca.getPassword();
				if (pwd != null && !pwd.isEmpty()) {
					dataMap.put("cashAccountPwd", "Y");
				} else {
					dataMap.put("cashAccountPwd", "N");
				}
			} else {
				dataMap.put("cashAccountPwd", "N");
			}

			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", dataMap);

		} else {
			map.put("status", 4);
			map.put("msg", "环信ID错误");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 找回密码时发验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/sendSmsUpdatePassword", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> sendSmsUpdatePassword(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		//
		// String phone = request.getParameter("phone");
		//
		// Map<String, Object> map = new HashMap<String, Object>();
		//
		// boolean isHaven = userService.checkoutRegisterPhone(phone);
		// if (isHaven == true) {
		//
		// boolean isOk = sMSandValiCodeService.sendSMS(phone, "ZHMM");
		// if (isOk == true) {
		// map = rspFormat("", SUCCESS);
		// } else {
		// map.put("status", 5);
		// map.put("msg", "短信发送失败");
		// map.put("data", "");
		//
		// }
		//
		// } else {
		// map.put("status", 4);// 该号码没注册过
		// map.put("msg", "该号码没注册过");
		// map.put("data", "");
		// }
		// return map;

		// 此处还没上线
		String mobile=request.getParameter("mobile");
		//String phone = request.getParameter("phone");
		Map<String,Object> map = new HashMap<String,Object>();
		HashMap<String, String> map2 = new HashMap<String, String>();
		boolean isHaven = userService.checkoutRegisterPhone(mobile);
		Object status = null;
		if (isHaven == true) {
			try {
				System.out.println("hdfijsk");
			map2.put("ctl", "api");
			map2.put("act", "get_re_pwd_verify_code");
			map2.put("mobile",mobile);
			String time = String.valueOf(new Date().getTime()).substring(0, 10);
			map2.put("time",time);
			//map2.put("signature",MD5Util.string2MD5(SortByMap.sort(map2)+"Yx3V27g4SckNJ1Zk"));
			map2.put("signature",MD5Util.getDigest(SortByMap.sort(map2)+"Yx3V27g4SckNJ1Zk"));
			System.out.println("hgfdhsklj");
				String res = ControllerTest.sentPost(map2);
				System.out.println("gfdjkljsgjlkf");
				System.out.println(res+"ghfdjkhgkjdfhsgk");
				
				
//				JSONObject js=new JSONObject(res);
//				Object status2=js.get("status");
//				System.out.println(status2.toString()+"hahahahaah");
				
				JSONObject json_test = (JSONObject) JSON.parse(res);
				System.out.println(json_test+"gfdjgkd");
				Map<String, Object> maps = (Map<String, Object>) JSON
						.parseObject(res);
				//Map maps = (Map)JSON.parse(res); 
			
				System.out.println(maps+"gfdjk;lgjdfkgkldbmcv");
				for (Object map3 : maps.entrySet()) {
					map.put(((Map.Entry) map3).getKey().toString(),
							((Map.Entry) map3).getValue());
				}
				System.out.println(map+"gnkfdm");
				status = new JSONObject(res).get("status");
				//System.out.println(status+"ghkldfhgjkshfks");
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (("0").equals(status.toString())){
				return map;
				//map = rspFormat("", SUCCESS);
			} else {
				return map;
				//map = rspFormat("", FAIL);
			}
		} else {
			map.put("status", 4);// 该号码没注册过
			map.put("msg", "该号码没注册过");
			map.put("data", "");
		}

		return map;

	}

	/**
	 * 找回密码第二步更改密码
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws IOException
	 */
	@RequestMapping(value = "/updatePassword", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updatePassword(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException, NoSuchAlgorithmException {

		// Map<String, Object> map = new HashMap<String, Object>();
		// String phone = request.getParameter("phone");
		// String password = request.getParameter("password");
		// String verify = request.getParameter("verify"); // 验证码
		// if (phone != null && !phone.isEmpty() && password != null
		// && !password.isEmpty() && verify != null && !verify.isEmpty()) {
		// ValidateCode validateCode = new ValidateCode();
		// validateCode.setPhone(phone);
		// validateCode.setMarker("ZHMM");
		// validateCode.setRandomNum(verify);
		// boolean isOk = sMSandValiCodeService.validataCode(validateCode);
		// if (isOk == false) {
		// map = rspFormat("", WRONG_VALIDATE_CODE);
		// } else {
		//
		// try {
		// password = new String(
		// new BASE64Decoder().decodeBuffer(password));// base64位解密
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// boolean isUpdateOk = userService
		// .updatePassword(password, phone);
		// if (isUpdateOk == true) {
		// map = rspFormat("", SUCCESS);
		// } else {
		// map = rspFormat("", WRONG_PARAM);
		// }
		// }
		//
		// } else {
		// map = rspFormat("", WRONG_PARAM);
		// }
		// return map;
		//

	
		Map<String, Object> map = new HashMap<String, Object>();
		String phone = request.getParameter("phone");
		String password = request.getParameter("password");
		String verify = request.getParameter("verify"); // 验证码
		HashMap<String, String> map2 = new HashMap<String, String>();
		if (phone != null && !phone.isEmpty() && password != null
				&& !password.isEmpty() && verify != null && !verify.isEmpty()) {
			map2.put("ctl", "api");
			map2.put("act", "phone_send_password");
			map2.put("mobile", phone);
			map2.put("pwd_m", password);
			map2.put("verify", verify);
			String time = String.valueOf(new Date().getTime()).substring(0, 10);
			map2.put("time", time);

			map2.put(
					"signature",
					MD5Util.getDigest(SortByMap.sort(map2)
							+ "Yx3V27g4SckNJ1Zk"));
			Object status = null;
			try {
				String res = ControllerTest.sentPost(map2);
				status = new JSONObject(res).get("status");
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (("0").equals(status.toString())) {
				try {
					password = new String(
							new BASE64Decoder().decodeBuffer(password));// base64位解密
				} catch (Exception e) {
					// TODO: handle exception
				}
				boolean isUpdateOk = userService
						.updatePassword(password, phone);
				if (isUpdateOk == true) {
					map = rspFormat("", SUCCESS);
				} else {
					map = rspFormat("", WRONG_PARAM);
				}
			} else {
				map = rspFormat("", FAIL);
			}

		} else {
			map = rspFormat("", WRONG_PARAM);
		}
		return map;

	}

	/**
	 * 普通修改密码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/updatePasswordGeneral", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updatePasswordGeneral(
			HttpServletRequest request, HttpServletResponse response) {
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("password");
		String paramToken = request.getParameter("token");
		String dbToken = tokenService.selectTokenByUserId2(userId);

		Map<String, Object> map = new HashMap<String, Object>();

		if (dbToken.equals(paramToken)) {
			boolean isUpdateOk = userService.updatePasswordGeneral(password,
					userId);
			if (isUpdateOk == true) {
				map.put("status", 0);// 操作成功
				map.put("msg", "操作成功");
				map.put("data", "");
			} else {
				map.put("status", 4);// 操作失败
				map.put("msg", "操作失败");
				map.put("data", "");
			}
		} else {
			map.put("status", 7);// 操作失败
			map.put("msg", "操作失败,token错误");
			map.put("data", "");
		}

		return map;
	}

	/**
	 * 查找好友列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectFriendList", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectFriendList(HttpServletRequest request,
			HttpServletResponse response) {

		String loginName = request.getParameter("loginName");
		String paramToken = request.getParameter("token");
		String dbToken = tokenService.selectTokenByLoginName(loginName);

		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 先检验存不存在该用户
		User vuser = userService.selectUserByUsername(loginName);
		if (vuser != null) {
			if (dbToken.equals(paramToken)) {
				List<User> friendList = userService.selectFriendList(loginName);
				if (friendList != null) {
					ArrayList<Map<String, Object>> friendListMap = new ArrayList<Map<String, Object>>();

					for (int i = 0; i < friendList.size(); i++) {
						User user = friendList.get(i);
						String userName = user.getUserName();
						String easemobId = user.getEasemobId();
						Map<String, Object> userMap = new HashMap<String, Object>();
						userMap.put("userId", user.getUserId());
						userMap.put("userName", userName);
						userMap.put("easemobId", easemobId);
						userMap.put("headImage", user.getHeadImage());
						userMap.put("userSex", user.getUserSex());
						userMap.put("userLevel", user.getUserLevel());
						userMap.put("userStatus", user.getUserStatus());
						friendListMap.add(userMap);
					}

					resultMap.put("status", 0);
					resultMap.put("msg", "操作成功");
					resultMap.put("data", friendListMap);
				} else {
					resultMap.put("status", 0);
					resultMap.put("msg", "操作成功，该用户没有好友");
					resultMap.put("data", "");
				}
			} else {

				resultMap.put("status", 7);
				resultMap.put("msg", "token错误");
				resultMap.put("data", "");
			}
		} else {
			resultMap.put("status", 4);
			resultMap.put("msg", "操作失败，不存在该用户");
			resultMap.put("data", "");

		}

		return resultMap;
	}

	/**
	 * 根据环信ID批量查找用户名
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectUsernameByeasemobIdList", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectUsernameByeasemobIdList(
			HttpServletRequest request, HttpServletResponse response) {

		// 获取批量环信ID字符串，根据逗号切割转成List
		String easemobIdStr = request.getParameter("easemobIdStr");
		String[] arrayEasemobIdStr = easemobIdStr.split(",");
		List<String> easemobIdList = java.util.Arrays.asList(arrayEasemobIdStr);

		// 返回的map
		Map<String, Object> resultMap = new HashMap<String, Object>();

		if (easemobIdList.size() > 0) {
			List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
			for (String easemobId : easemobIdList) {
				User user = userService.selectUsernameByeasemobId(easemobId);
				if (user != null) {
					Map<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("userId", user.getUserId());
					userMap.put("userName", user.getUserName());
					userMap.put("easemobId", user.getEasemobId());
					userMap.put("headImage", user.getHeadImage());
					userMap.put("userSex", user.getUserSex());
					userMap.put("userLevel", user.getUserLevel());
					userMap.put("userStatus", user.getUserStatus());
					userList.add(userMap);
				}

			}
			resultMap.put("status", 0);
			resultMap.put("msg", "操作成功");
			resultMap.put("data", userList);
		} else {
			resultMap.put("status", 4);
			resultMap.put("msg", "操作失败");
			resultMap.put("data", "");
		}
		return resultMap;
	}

	/**
	 * 上传头像
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownHostException
	 */
	@SuppressWarnings("null")
	@RequestMapping(value = "/updateHeadImag", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateHeadImag(
			MultipartHttpServletRequest request, HttpServletResponse response)
			throws UnknownHostException {

		String paramToken = request.getParameter("token");
		String userId = request.getParameter("userId");
		String dbToken = tokenService.selectTokenByUserId2(Integer
				.parseInt(userId));

		Map<String, Object> resultMap = new HashMap<String, Object>();

		String photoRealPath = null;
		String photoUrl = null;

		String ip = PropertiesUtils.getServerIpConf().getProperty("ip");
		if (ip.equals("120.76.192.128")) {
			// server_01
			photoRealPath = PropertiesUtils.getProperties().getProperty(
					"server_headImagePath");
			photoUrl = PropertiesUtils.getProperties().getProperty(
					"server01_headImageUrl");

		} else if (ip.equals("120.76.128.66")) {
			// server_02
			photoRealPath = PropertiesUtils.getProperties().getProperty(
					"server_headImagePath");
			photoUrl = PropertiesUtils.getProperties().getProperty(
					"server02_headImageUrl");

		} else if (ip.equals("120.24.208.86")) {
			// 负载均衡前的server
			photoRealPath = PropertiesUtils.getProperties().getProperty(
					"HeadImageRealPath");
			photoUrl = PropertiesUtils.getProperties().getProperty(
					"HeadImageUrl");

		} else if (ip.equals("192.168.1.103")) {
			photoRealPath = PropertiesUtils.getProperties().getProperty(
					"HeadImageRealPath");
			photoUrl = PropertiesUtils.getProperties().getProperty(
					"HeadImageUrl");

		} else {
			resultMap.put("status", 35);
			resultMap.put("msg", "ip获取错误");
			resultMap.put("data", "");
			return resultMap;
		}

		List<String> fileTypes = new ArrayList<String>();
		fileTypes.add("jpg");
		fileTypes.add("jpeg");
		fileTypes.add("png");

		if (dbToken.equals(paramToken)) {
			boolean isUpdateOk = false;
			String headImage = "";
			MultiValueMap<String, MultipartFile> multiMap = request
					.getMultiFileMap();

			if (multiMap != null) {
				Set<String> keys = multiMap.keySet();
				for (String key : keys) {
					List<MultipartFile> multiFile = multiMap.get(key);
					for (MultipartFile file : multiFile) {
						InputStream is = null;
						FileOutputStream fos = null;
						try {
							is = file.getInputStream();
							String originalFilename = file
									.getOriginalFilename();

							// 获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名
							String ext = originalFilename.substring(
									originalFilename.lastIndexOf(".") + 1,
									originalFilename.length());
							// 对扩展名进行小写转换
							ext = ext.toLowerCase();
							if (fileTypes.contains(ext)) {
								// 如果扩展名属于允许上传的类型，则创建文件
								String newFileName = UUID.randomUUID()
										+ originalFilename
												.substring(originalFilename
														.lastIndexOf("."));

								headImage = photoUrl + newFileName;

								String comFileName = "ex" + newFileName;
								String comPhotoAddress = photoUrl + comFileName;

								Picture picture = new Picture();
								picture.setAttachId(userId);
								picture.setCompressPic(comPhotoAddress);
								picture.setOriginPic(headImage);
								picture.setPictureType(9);
								futureService.insertImage(picture);

								fos = new FileOutputStream(photoRealPath
										+ newFileName);

								userService.updatHeadImage(comPhotoAddress,
										userId);

								byte[] buffer = new byte[1024];
								int len = 0;
								while ((len = is.read(buffer)) != -1) {
									fos.write(buffer, 0, len);
									isUpdateOk = true;
								}
								fos.flush();
								try {
									ImageUtil imageUtil = new ImageUtil();
									imageUtil.saveImage(photoRealPath
											+ newFileName, photoRealPath
											+ comFileName, 8);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								fos.close();
								is.close();
								resultMap.put("status", 4);
								resultMap.put("msg", "图片格式不对");
								resultMap.put("data", "");
								return resultMap;
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							try {
								if (fos != null) {
									fos.close();
								}
								if (is != null) {
									is.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

				if (isUpdateOk == true) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("headImage", headImage);
					resultMap.put("status", 0);
					resultMap.put("msg", "操作成功");
					resultMap.put("data", dataMap);
				} else {
					resultMap.put("status", 4);
					resultMap.put("msg", "服务器读写失败");
					resultMap.put("data", "");
				}
			} else {
				resultMap.put("status", 4);
				resultMap.put("msg", "请求参数为空");
				resultMap.put("data", "");
			}
		} else {
			resultMap.put("status", 7);
			resultMap.put("msg", "token错误");
			resultMap.put("data", "");
		}
		return resultMap;
	}

	@RequestMapping(value = "/updateUserSexInfo", method = { RequestMethod.POST })
	@ResponseBody
	@SensitivewordInterceptorAnnotation
	public Map<String, Object> updateUserSexInfo(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {

		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer userSex = Integer.parseInt(request.getParameter("userSex"));

		String paramToken = request.getParameter("token");
		String dbToken = tokenService.selectTokenByUserId2(userId);

		Map<String, Object> map = new HashMap<String, Object>();

		if (dbToken.equals(paramToken)) {

			User user = new User();
			user.setUserId(userId);
			user.setUserSex(userSex);
			// user.setPassword(password);
			boolean updateOk = userService.updateUserSexInfo(user);
			if (updateOk == true) {
				map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", "");
			} else {
				map.put("status", 5);
				map.put("msg", "系统出现异常");
				map.put("data", "");
			}
		} else {
			map.put("status", 7);
			map.put("msg", "token错误");
			map.put("data", "");
		}

		return map;
	}

	@RequestMapping(value = "/selectUserSuper", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectUserSuper(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String user_id = request.getParameter("userId");
		String user_name = request.getParameter("userName");
		String phone = request.getParameter("phone");
		String easemob_id = request.getParameter("easemobId");
		Integer userId;
		if (null != user_id) {
			userId = Integer.parseInt(user_id);
		} else {
			userId = -1;
		}

		if (null == user_name) {
			user_name = "$#@*(. .)*@#$";
		}

		if (null == phone) {
			phone = "-1";
		}

		if (null == easemob_id) {
			easemob_id = "-1";
		}
		User user = new User();
		user.setUserId(userId);
		user.setUserName(user_name);
		user.setPhone(phone);
		user.setEasemobId(easemob_id);
		User resultUser = userService.selectUserSuper(user);
		if (null != resultUser) {
			userId = resultUser.getUserId();
			Integer ma = userAttentionService.getMyAttentionCount(userId);
			Integer am = userAttentionService.getAttentMineCount(userId);
			Integer ms = futureService.getOwnStockCount(userId);
			Mem mem = new Mem();
			mem.setUserId(userId);
			Mem resultMem = futureService.getMem(mem);
			resultMem = futureService.getMem(mem);
			Date date = new Date();
			if (date.getTime() < resultMem.getExpireTime().getTime()) {
				resultUser.setMember(1);
			} else {
				resultUser.setMember(0);
			}

			Integer subMeVIPNum = dynVIPService.getMyVIPNnm(userId);
			Integer meSubVIPNum = dynVIPService.getMySubscribeVIPNum(userId);

			if (subMeVIPNum == null) {
				subMeVIPNum = 0;
			}
			if (meSubVIPNum == null) {
				meSubVIPNum = 0;
			}
			resultMap.put("user", resultUser);
			resultMap.put("member", resultMem);
			resultMap.put("am", am);
			resultMap.put("ma", ma);
			resultMap.put("ms", ms);
			resultMap.put("subMeVIPNum", subMeVIPNum);
			resultMap.put("meSubVIPNum", meSubVIPNum);
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", resultMap);
		} else {
			map.put("status", 4);
			map.put("msg", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	@RequestMapping(value = "/updateUserSuper", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateUserSuper(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));

		String password = request.getParameter("password");
		String user_sex = request.getParameter("userSex");
		String user_level = request.getParameter("userLevel");
		String user_status = request.getParameter("userStatus");
		String user_title = request.getParameter("userTitle");
		String user_brief = request.getParameter("userBrief");

		User currentUser = userService.selectUserById(userId);

		if (null != password) {
			currentUser.setPassword(password);
		}
		if (null != user_sex) {
			currentUser.setUserSex(Integer.parseInt(user_sex));
		}
		if (null != user_level) {
			currentUser.setUserLevel(Integer.parseInt(user_level));
		}
		if (null != user_status) {
			currentUser.setUserStatus(Integer.parseInt(user_status));
		}
		if (null != user_title) {
			currentUser.setUserTitle(user_title);
		}
		if (null != user_brief) {
			currentUser.setUserBrief(user_brief);
		}

		Integer hasUpdate = userService.updateUserSuper(currentUser);
		if (hasUpdate != 0) {
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", currentUser);
		} else {
			map.put("status", 4);
			map.put("msg", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	@RequestMapping(value = "/getvuserlist", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getVUserList(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = 0;
		String idString = request.getParameter("userId");
		if (null != idString) {
			userId = Integer.parseInt(idString);
		}
		Integer limit = Integer.parseInt(request.getParameter("limit"));
		List<User> userList = userService.getVUserList(userId, limit);
		if (userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				Integer vuserId = userList.get(i).getUserId();
				userList.get(i).setAmCount(
						userAttentionService.getAttentMineCount(vuserId));
				userList.get(i).setDynCount(
						dynContentService.getDynCount(vuserId));
				UserAttention attention = new UserAttention();
				attention.setUserId(userId);
				attention.setAttUserId(vuserId);
				userList.get(i).setAttent(
						userAttentionService.getAttentRelation(attention));
			}
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", userList);
		} else if (userList.size() == 0) {
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", "");
		} else {
			map.put("status", 4);
			map.put("msg", "操作失败");
			map.put("data", "");
		}

		return map;
	}

	@RequestMapping(value = "/getusersuper", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> getusersuper(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		String user_id = request.getParameter("userId");
		String user_name = request.getParameter("userName");
		String phone = request.getParameter("phone");
		String easemob_id = request.getParameter("easemobId");
		Integer userId;
		if (null != user_id) {
			userId = Integer.parseInt(user_id);
		} else {
			userId = 0;
		}

		if (null == user_name) {
			user_name = "";
		}

		if (null == phone) {
			phone = "-1";
		}

		if (null == easemob_id) {
			easemob_id = "-1";
		}
		User user = new User();
		user.setUserId(userId);
		user.setUserName(user_name);
		user.setPhone(phone);
		user.setEasemobId(easemob_id);
		User resultUser = userService.selectUserSuper(user);
		map.put("data", resultUser);
		return map;
	}

}
