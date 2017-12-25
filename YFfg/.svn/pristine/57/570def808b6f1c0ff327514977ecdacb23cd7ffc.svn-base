package com.yfwl.yfgp.controller;

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

import com.yfwl.yfgp.bean.UserBean;
import com.yfwl.yfgp.model.Home;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserAttention;
import com.yfwl.yfgp.service.HomeService;
import com.yfwl.yfgp.service.UserAttentionService;
import com.yfwl.yfgp.service.UserService;

@Controller
@RequestMapping("/homeinfo")
public class HomeController extends BaseController{
	@Autowired
	HomeService homeService;
	@Autowired
	UserService userService;
	@Autowired
	UserAttentionService userAttentionService;
	
	@RequestMapping(value="/bannerinfo",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getBannerInfoList(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = homeService.getBannerInfo();
		return map;
	}
	
	@RequestMapping(value="/hotinfo",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getHotInfoList(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = homeService.getHotInfo();
		return map;
	}
	
	@RequestMapping(value="/information",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getInformationList(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object> map = homeService.getInformation();
		return map;
	}
	
	
	@RequestMapping(value="/recommend",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getRecommendList(HttpServletRequest request,
			HttpServletResponse response){
		Map<String, Object>map = new HashMap<String, Object>();
		List<Home> recommendList = homeService.getRecommendList();
		List<UserBean> userList = new ArrayList<>();
		for(Home home: recommendList) {
			UserBean userBean = new UserBean();
			User user = userService.selectUserById(Integer.parseInt(home.getInfoContentId()));
			if(null != user) {
				if(null != request.getParameter("userId")) {
					Integer userId = Integer.parseInt(request.getParameter("userId"));
					UserAttention attention = new UserAttention();
					attention.setUserId(userId);
					attention.setAttUserId(Integer.parseInt(home.getInfoContentId()));
					user.setAttent(userAttentionService.getAttentRelation(attention));	
				}
				userBean.setUserId(user.getUserId());
				userBean.setUserName(user.getUserName());
				userBean.setHeadImage(user.getHeadImage());
				userBean.setUserStatus(user.getUserStatus());
				userBean.setAttent(user.getAttent());
				userList.add(userBean);
			}
		}
		map = rspFormat(userList, SUCCESS);
		return map;
	}

}
