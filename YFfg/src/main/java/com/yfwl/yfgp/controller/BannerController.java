package com.yfwl.yfgp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.Banner;
import com.yfwl.yfgp.service.BannerService;

@Controller
@RequestMapping("/banner")
public class BannerController extends BaseController {
	
	@Resource
	private BannerService bannerService;
	
	@RequestMapping(value = "/getBanner", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getBanner(HttpServletRequest request,
			HttpServletResponse response ){
		Map<String,Object> map =null;
		List<Map<String,Object>> arr=new ArrayList<Map<String,Object>>();
		for(int i=1; i<6; i++){
			map=new HashMap<String, Object>();
			Banner banner = bannerService.selectBanner(i);
			if(banner != null){
				map.put("bannerId", banner.getBannerId());
				map.put("bannerPicUrl", banner.getBannerPicUrl());
				map.put("sequence", banner.getSequence());
				map.put("clickUrl", banner.getClickUrl());
				map.put("addTime", banner.getAddTime());
				arr.add(map);
			}
		}
		map=rspFormat(arr, SUCCESS);
		return map;
	}

}
