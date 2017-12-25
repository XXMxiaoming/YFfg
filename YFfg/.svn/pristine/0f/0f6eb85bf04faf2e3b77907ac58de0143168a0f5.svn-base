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

import com.yfwl.yfgp.model.ValidateCode;
import com.yfwl.yfgp.service.SMSendValiCodeService;

@Controller
@RequestMapping("/validateCode")
public class ValidateCodeController {

	@Autowired
	private SMSendValiCodeService sMSandValiCodeService;

	/**
	 * 注册时比对验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/validateCodeWhenRegister", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> validateCodeWhenRegister(
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		String phone = request.getParameter("phone");
		String paramRandomNum = request.getParameter("randomNum");
		if (phone != null && !phone.isEmpty() && paramRandomNum != null && !paramRandomNum.isEmpty()) {
			
			ValidateCode validateCode = new ValidateCode();
			validateCode.setPhone(phone);
			validateCode.setMarker("ZC");
			validateCode.setRandomNum(paramRandomNum);
			boolean isOk = sMSandValiCodeService.validataCode(validateCode);
			if (isOk == true) {
				map.put("status", 0);
				map.put("message", "验证码比对成功");
				map.put("data", "");
			} else {
				map.put("status", 5);
				map.put("message", "验证码比对错误");
				map.put("data", "");
			}
		} else {			
			map.put("status", 4);
			map.put("message", "参数为空");
			map.put("data", "");
		}

		return map;
	}

	/**
	 * 找回密码时比对验证码
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/validateCodeWhenZHMM", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> validateCodeWhenZHMM(HttpServletRequest request,
			HttpServletResponse response) {
		String phone = request.getParameter("phone");
		String paramRandomNum = request.getParameter("randomNum");

		Map<String, Object> map = new HashMap<String, Object>();
		if (phone != null && !phone.isEmpty() && paramRandomNum != null && !paramRandomNum.isEmpty()) {			
			ValidateCode validateCode = new ValidateCode();
			validateCode.setPhone(phone);
			validateCode.setMarker("ZHMM");
			validateCode.setRandomNum(paramRandomNum);
			boolean isOk = sMSandValiCodeService.validataCode(validateCode);
			if (isOk == true) {
				map.put("status", 0);
				map.put("message", "验证码比对成功");
				map.put("data", "");
			} else {
				map.put("status", 5);
				map.put("message", "验证码比对错误");
				map.put("data", "");
			}
		} else {			
			map.put("status", 4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		return map;
	}
	
	
}
