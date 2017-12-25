package com.yfwl.yfgp.controller;

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

import com.yfwl.yfgp.service.DynVIPService;

@Controller
@RequestMapping("/dynvip")
public class DynVipController extends BaseController{
	
	@Autowired
	private DynVIPService dynVIPService;
	
	@RequestMapping(value = "/getmydynvip", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getMydynVip(HttpServletRequest request,
			HttpServletResponse response) {
		
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		Map<String, Object>map = new HashMap<String, Object>();
		String uid = request.getParameter("userId");
		String pNow = request.getParameter("pageNow");
		//type: subme 查谁订阅了我；mesub 查我订阅了谁
		String type = request.getParameter("type");
		if(uid!=null && !uid.isEmpty() && pNow!=null && !pNow.isEmpty() && type!=null && !type.isEmpty()){
			Integer userId = Integer.parseInt(uid);
			Integer pageNow = Integer.parseInt(pNow);
			Integer pageCount = (pageNow - 1) * 20;
			List<Map<String,Object>> list = dynVIPService.getVIPOrder(userId, pageCount, type);
			if(list.size()<=0){
				map = rspFormat("", SUCCESS);
			}else{
				map = rspFormat(list, SUCCESS);
			}
		}else{
			map = rspFormat("", WRONG_PARAM);
		}
		return map;
	}
			
}
