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

import com.yfwl.yfgp.model.HomeArticle;
import com.yfwl.yfgp.service.HomeArticleService;

@Controller
@RequestMapping("/homeArticle")
public class HomeArticleController {
	
	@Resource
	private HomeArticleService homeArticleService;	
	
	@RequestMapping(value = "/getHomeArticle", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getHomeArticle(HttpServletRequest request,
			HttpServletResponse response){
		
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		HomeArticle homeArticle1 = homeArticleService.selectHomeArticle(1);
		HomeArticle homeArticle2 = homeArticleService.selectHomeArticle(2);
		HomeArticle homeArticle3 = homeArticleService.selectHomeArticle(3);
		HomeArticle homeArticle4 = homeArticleService.selectHomeArticle(4);
		HomeArticle homeArticle5 = homeArticleService.selectHomeArticle(5);
		
		dataMap.put("homeArticle1", homeArticle1);
		dataMap.put("homeArticle2", homeArticle2);
		dataMap.put("homeArticle3", homeArticle3);
		dataMap.put("homeArticle4", homeArticle4);
		dataMap.put("homeArticle5", homeArticle5);
		
		map.put("status", 0);
		map.put("message", "操作成功");
		map.put("data", dataMap);
		
		return map;
		
	}
	
	@RequestMapping(value = "/updateScanCount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> updateScanCount(HttpServletRequest request,
			HttpServletResponse response){
		
		Map<String,Object> map = new HashMap<String,Object>();
		String homeIdStr = request.getParameter("homeId");
		if(homeIdStr == null || homeIdStr.isEmpty()){
			map.put("status", 4);
			map.put("message", "操作失败，homeId为空");
			map.put("data", "");
		}else{
			Integer homeId = Integer.parseInt(homeIdStr);
			boolean isUpdateOk = homeArticleService.updateScanCount(homeId);
			if(isUpdateOk == true){
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", "");
			}else{
				map.put("status", 5);
				map.put("message", "操作失败，程序崩溃");
				map.put("data", "");
			}
		}
		return map;
	}
	
}
