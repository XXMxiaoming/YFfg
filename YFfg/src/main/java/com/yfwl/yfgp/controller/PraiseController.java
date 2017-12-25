package com.yfwl.yfgp.controller;

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

import com.yfwl.yfgp.model.Praise;
import com.yfwl.yfgp.service.PraiseService;

@Controller
@RequestMapping("/praise")
public class PraiseController {
	@Autowired
	PraiseService praiseService;
	
	@RequestMapping(value = "/praise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> praise(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer praiseContent = Integer.parseInt(request.getParameter("praiseContent"));
		Integer contentType = Integer.parseInt(request.getParameter("contentType"));
		Date praiseTime = new Date();
		Praise praise = new Praise();
		praise.setUserId(userId);
		praise.setPraiseContent(praiseContent);
		praise.setContentType(contentType);
		praise.setPraiseTime(praiseTime);
		Praise existPraise = praiseService.selectPraise(praise);
		if(null == existPraise) {
			int hasInsert = praiseService.insertPraise(praise);
			if(hasInsert == 1) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", praise);
			}
			else {
				map.put("status", 4);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", existPraise);
		}
		return map;
	}
	
	@RequestMapping(value = "/cancelpraise", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> cancelPraise(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer praiseId = Integer.parseInt(request.getParameter("praiseId"));
		Praise praise = new Praise();
		praise.setPraiseId(praiseId);
		int hasDelete = praiseService.deletePraise(praise);
		if(hasDelete == 1) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", "");
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	
	}
}
