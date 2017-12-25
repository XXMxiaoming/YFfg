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

import com.yfwl.yfgp.model.DynContentMeta;
import com.yfwl.yfgp.service.DynContentMetaService;

@Controller
@RequestMapping("/dyncontentmeta")
public class DynContentMetaController {
	@Autowired
	DynContentMetaService dynContentMetaService;
	
	
	
	@RequestMapping(value = "/updatemeta", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateMeta(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer userIdMeta = Integer.parseInt(request.getParameter("userIdMeta"));
		Integer metaValue = Integer.parseInt(request.getParameter("metaValue"));
		Integer status = 0;
		DynContentMeta dynContentMeta = new DynContentMeta();
		dynContentMeta.setUserId(userId);
		dynContentMeta.setUserIdMeta(userIdMeta);
		dynContentMeta.setMetaValue(metaValue);
		dynContentMeta.setStatus(status);
		dynContentMeta.setId(0);
		DynContentMeta existMeta = dynContentMetaService.getMeta(dynContentMeta);
		if(null != existMeta) {
			Integer hasUpdate = dynContentMetaService.updateMeta(dynContentMeta);
			if(hasUpdate != 0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", dynContentMeta);
			}
			else {
				map.put("status", 4);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		else {
			Integer hasUpdate = dynContentMetaService.insertMeta(dynContentMeta);
			if(hasUpdate != 0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", dynContentMeta);
			}
			else {
				map.put("status", 0);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		return map;
	}

	@RequestMapping(value = "/getmeta", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getMeta(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer userIdMeta = Integer.parseInt(request.getParameter("userIdMeta"));
		
		DynContentMeta dynContentMeta = new DynContentMeta();
		dynContentMeta.setUserId(userId);
		dynContentMeta.setUserIdMeta(userIdMeta);
		dynContentMeta.setMetaValue(0);
		dynContentMeta.setStatus(0);
		dynContentMeta.setId(0);
		DynContentMeta existMeta = dynContentMetaService.getMeta(dynContentMeta);
		if(null != existMeta) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", existMeta);
		}
		else {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", dynContentMeta);
		}
		return map;
	}

}
