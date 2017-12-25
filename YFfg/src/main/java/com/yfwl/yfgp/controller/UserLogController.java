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

import com.yfwl.yfgp.service.UserLogService;

@Controller
@RequestMapping("/userlog")
public class UserLogController {
	
	@Autowired
	private UserLogService userLogService;
	
	@RequestMapping(value="/setUserLog",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> setUserLog(HttpServletRequest request,
			HttpServletResponse response){
		String userId = request.getParameter("userId");
		String deviceUuid = request.getParameter("deviceUuid");
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(userId != null && !userId.isEmpty() && deviceUuid != null && !deviceUuid.isEmpty()){
			
			Integer insertVal = userLogService.insertUserLog(Integer.parseInt(userId), deviceUuid);
			if(insertVal>0){
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", "");
			}else{
				map.put("status", 4);
				map.put("message", "操作失败");
				map.put("data", "");
			}			
		}else{
			map.put("status", 4);
			map.put("message", "操作失败，参数为空");
			map.put("data", "");
		}
		return map;
	}
		
		
}
