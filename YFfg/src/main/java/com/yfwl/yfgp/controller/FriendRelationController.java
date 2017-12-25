package com.yfwl.yfgp.controller;

import java.io.IOException;
import java.util.ArrayList;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserAttention;
import com.yfwl.yfgp.service.FriendRelationService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserAttentionService;
import com.yfwl.yfgp.service.UserService;

@Controller
@RequestMapping("/friendRelation")
public class FriendRelationController {

	@Autowired
	private FriendRelationService friendRelationService;
	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private UserAttentionService userAttentionService;

	/**
	 * 查找有没有该用户
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/searchUser",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> searchUser(HttpServletRequest request,
			HttpServletResponse response) {
		Integer postUserId = 0;
		String Id = request.getParameter("userId");
		Integer userId = 0;
		if(null != Id) {
			userId = Integer.parseInt(Id);
			postUserId = 1;
		}
		String searchName = request.getParameter("searchName");
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;

		Map<String, Object> map = new HashMap<String, Object>();

		List<User> userList = userService.selectUserByLike(searchName,
				pageCount);

		List<Object> resultList = new ArrayList<Object>();
		if (userList.size() != 0) {

			for (int i = 0; i < userList.size(); i++) {

				User user = userList.get(i);
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("user", user);
				if(postUserId == 1) {
					Integer attUserId = user.getUserId();
					UserAttention attention = new UserAttention();
					attention.setUserId(userId);
					attention.setAttUserId(attUserId);
					Integer isAttented = userAttentionService.getAttentRelation(attention);
					if(isAttented == 1) {
						userMap.put("attent", 1);
					}
					else {
						userMap.put("attent", 0);
					}
				}
				
				resultList.add(userMap);
			}

			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", resultList);
		} else {
			map.put("status", 4);
			map.put("message", "不存在该用户");
			map.put("data", "");
		}
		return map;

	}

	/**
	 * 添加好友
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@RequestMapping(value = "/insertFriend",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertFriend(HttpServletRequest request,
			HttpServletResponse response) throws JsonParseException,
			JsonMappingException, IOException {
		String paramToken = request.getParameter("token");

		String loginName = request.getParameter("loginName");
		String dbToken = tokenService.selectTokenByLoginName(loginName);
		String searchName = request.getParameter("searchName");

		Map<String, Object> map = new HashMap<String, Object>();

		if (dbToken.equals(paramToken)) {
			map = friendRelationService.insertFriend(loginName, searchName);
		} else {
			map.put("status", 7);
			map.put("message", "token错误");
			map.put("data", "");
		}

		return map;
	}

	/**
	 * 删除好友
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteFriend",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> deleteFriend(HttpServletRequest request,
			HttpServletResponse response) {

		String user = request.getParameter("user");
		String friend = request.getParameter("friend");
		String paramToken = request.getParameter("token");
		String dbToken = tokenService.selectTokenByLoginName(user);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(dbToken.equals(paramToken)){
			boolean isDeleteOk = friendRelationService.deleteFriend(user, friend);
			if (isDeleteOk == true) {
				boolean isDeleteOk2 = friendRelationService.deleteFriend(friend,user);
						
				if (isDeleteOk2 == true) {
					map.put("status", 0);
					map.put("message", "删除成功");
					map.put("data", "");
				} else {
					map.put("status", 4);
					map.put("message", "删除失败");
					map.put("data", "");
				}
			} else {
				map.put("status", 4);
				map.put("message", "删除失败");
				map.put("data", "");
			}
		}else{
			map.put("status", 7);
			map.put("message", "token错误");
			map.put("data", "");
		}		
		return map;
	}

}
