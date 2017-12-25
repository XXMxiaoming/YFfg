package com.yfwl.yfgp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.UserAttentionService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.PropertiesUtils;

@Controller
@RequestMapping("/riskassess")
public class RiskAssessController {
	
	@Resource
	private UserService userService;
	@Resource
	private UserAttentionService userAttentionService;
	
	@RequestMapping(value = "/getQuestion", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getQuestion(HttpServletRequest request,
			 HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		
		String versionCode = PropertiesUtils.getQuestionProperties().getProperty("versionCode");
		String questionDownloadUrl = PropertiesUtils.getProperties().getProperty("QutionDownloadUrl");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		if(versionCode!=null && !versionCode.isEmpty() && questionDownloadUrl!=null && !questionDownloadUrl.isEmpty()){
			resultMap.put("versionCode", versionCode);
			resultMap.put("downloadUrl", questionDownloadUrl);
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", resultMap);
		}else{
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		
		return map;
	}
	
	@RequestMapping(value = "/checkIsAssessed", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> checkIsAssessed(HttpServletRequest request,
			 HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String id = request.getParameter("userId");
		if(id != null && !id.isEmpty()){
			Integer userId = Integer.parseInt(id);
			User user = userService.selectUserById(userId);
			if(user != null){
				String tzType = user.getTzType();
				if(tzType != null && !tzType.isEmpty()){
					
					User tzuser = userService.selectUserByUsername(tzType+"机器人配置");
					
					resultMap.put("tzType", tzType);
					resultMap.put("tzUserId", tzuser.getUserId());
					
					map.put("status", 0);
					map.put("message", "已经测评过");
					map.put("data", resultMap);
				}else{
					map.put("status", 5);
					map.put("message", "还未测评过");
					map.put("data", "");
				}
			}
		}else{
			map.put("status", 4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		
		return map;
	}
	
	@RequestMapping(value = "/getAssessResult", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getAssessResult(HttpServletRequest request,
			 HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		String options = request.getParameter("options");
		String id = request.getParameter("userId");
		
		if(options != null && !options.isEmpty() && id != null && !id.isEmpty()){
			
			Integer userId = Integer.parseInt(id);
			userService.updateTZtype(options, userId);
			
			User tzuser = userService.selectUserByUsername(options+"机器人配置");
			
			userAttentionService.attented(userId, options+"机器人配置");
			
			resultMap.put("tzType", options);
			resultMap.put("tzUserId", tzuser.getUserId());
			
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", resultMap);
			
		}else{
			map.put("status", 4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		
		return map;
	}
	
}
