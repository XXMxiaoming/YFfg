package com.yfwl.yfgp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.service.UserAttentionService;

@Controller
@RequestMapping("/userAttention")
public class UserAttentionController {

	@Autowired
	private UserAttentionService userAttentionService;
	
	/**
	 * 查询是否关注过
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	@RequestMapping(value="/isAttented",method={ RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> isAttented(HttpServletRequest request,
			HttpServletResponse response) {
		
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String attUserName = request.getParameter("attUserName");
		
		Map<String,Object> map = userAttentionService.isAttented(userId, attUserName);
		return map;
	}
	
	/**
	 * 关注操作：
	 * 1、先查询是否关注过
	 * 2、如果已经关注过，点击则为取消关注；如果没有关注过，点击则为添加关注
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	@RequestMapping(value="/attendOperation",method={ RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> attendOperation(HttpServletRequest request,
			HttpServletResponse response) {
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String attUserName = request.getParameter("attUserName");
		
		Map<String,Object> map = userAttentionService.attendOperation(userId, attUserName, true);
		
		return map;
	}
	
	@RequestMapping(value="/myattentionlist", method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getMyAttentionList(HttpServletRequest request,
			HttpServletResponse response) {
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer pageCount = 0;
		String page = request.getParameter("pageNow");
		if(null != page) {
			pageCount = (Integer.parseInt(page) - 1) * 25;
		}
		Map<String, Object> map = userAttentionService.getMyAttentionList(userId, pageCount);
		return map;
	}
	
	
	@RequestMapping(value="/attentminelist", method={RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getAttentMineList(HttpServletRequest request,
			HttpServletResponse response) {
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer pageCount = 0;
		String page = request.getParameter("pageNow");
		if(null != page) {
			pageCount = (Integer.parseInt(page) - 1) * 25;
		}
		Map<String, Object> map = userAttentionService.getAttentMineList(userId, pageCount);
		return map;
	}
	

}
