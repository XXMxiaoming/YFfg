package com.yfwl.yfgp.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.Collect;
import com.yfwl.yfgp.service.CollectionService;

@Controller
@RequestMapping("/collection")
public class CollectionController {
	
	@Autowired
	CollectionService collectionService;
	
	@RequestMapping(value="/getcollection",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getCollection(HttpServletRequest request,
			HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		List<Collect> list = collectionService.getCollection(userId);
		if(list == null) {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		else if(list.size() == 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", "");
		}
		else if(list.size() > 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", list);
		}
		
		
		return map;
	}
	
	@RequestMapping(value="/deletecollection",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> deleteCollection(HttpServletRequest request,
			HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		Integer id = Integer.parseInt(request.getParameter("id"));
		Integer hasDelete = collectionService.deleteCollection(id);
		if(hasDelete > 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", "");
		}
		else{
			map.put("status", 0);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value="/insertcollection",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertCollection(HttpServletRequest request,
			HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer cId = Integer.parseInt(request.getParameter("cId"));
		Integer cType = Integer.parseInt(request.getParameter("cType"));
		String cTitle = request.getParameter("cTitle");
		String cSrc = request.getParameter("cSrc");
		Date cTime = new Date(); 
		Integer cStatus = 0;
		
		Collect collection = new Collect();
		collection.setUserId(userId);
		collection.setCId(cId);
		collection.setCType(cType);
		collection.setCTitle(cTitle);
		collection.setCSrc(cSrc);
		collection.setCTime(cTime);
		collection.setCStatus(cStatus);
		
		Collect resultCollect = collectionService.getCollect(collection);
		if(null != resultCollect) {
			map.put("status", 0);
			map.put("message", "已收藏");
			map.put("data", resultCollect);
		}
		else {
			Integer hasInsert = collectionService.insertCollection(collection);
			if(hasInsert > 0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", collection);
			}
			else{
				map.put("status", 0);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		return map;
	}
	

}
