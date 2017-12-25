package com.yfwl.yfgp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Identity;
import com.yfwl.yfgp.model.Integral;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.AccountService;
import com.yfwl.yfgp.service.FutureService;
import com.yfwl.yfgp.service.IntegralService;
import com.yfwl.yfgp.service.LoginService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;

@Controller
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private IntegralService integralService;
	@Autowired
	private FutureService futureService;

	/**
	 * 登录（用用户名或手机号）
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request,
			HttpServletResponse response) {

		String loginName = request.getParameter("phone");
		String password = request.getParameter("password");
		Map<String, Object> map = new HashMap<String, Object>();

		boolean isHavenName = userService.validateLonginName(loginName);
		if (isHavenName == true) {
			Integer count = userService.validatePassword(loginName,password);
			if (count > 0) {

				User user = userService.selectUserByLoginName(loginName);
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
				
				Map<String, Object> dataMap =  loginService.getTokenWhenLogin(userId);
				dataMap.put("userId", user.getUserId());
				dataMap.put("userName", user.getUserName());
				dataMap.put("easemobId", user.getEasemobId());
				dataMap.put("easemobPassword", user.getEasemobPassword());
				dataMap.put("head_image", user.getHeadImage());
				dataMap.put("accountId", accountId);
				dataMap.put("integralId", integralId);
				
				map.put("status", 0);// 登录成功
				map.put("message", "操作成功");
				map.put("data", dataMap);

			} else {
				map.put("status", 4);// 密码输入错误
				map.put("message", "密码输入错误");
				map.put("data", "");
			}
		} else {
			map.put("status", 5);// 用户名或手机号输入错误
			map.put("message", "该手机号未注册");
			map.put("data", "");
		}
		return map;
	}

}
