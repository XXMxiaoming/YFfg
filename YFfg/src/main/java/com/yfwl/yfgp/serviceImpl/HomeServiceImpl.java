package com.yfwl.yfgp.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.HomeMapper;
import com.yfwl.yfgp.model.Article;
import com.yfwl.yfgp.model.Home;
import com.yfwl.yfgp.service.HomeService;

@Service
public class HomeServiceImpl implements HomeService{
	
	@Autowired
	private HomeMapper homeMapper;

	@Override
	public Map<String, Object> getBannerInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Home> list = homeMapper.getBannerInfo();
		if(list.size()>0){
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", list);
		}else{
			map.put("status", -4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	@Override
	public Map<String, Object> getHotInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Home> list = homeMapper.getHotInfo();
		if(list.size()>0){
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", list);
		}else{
			map.put("status", -4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	@Override
	public Map<String, Object> getInformation() {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		List<Home> list = homeMapper.getInformation();
		if(list.size()>0){
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", list);
		}else{
			map.put("status", -4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	@Override
	public List<Home> getRaiseInfo() {
		// TODO Auto-generated method stub
		return homeMapper.getRaiseInfo();
	}

	@Override
	public List<Home> getRecommendList() {
		// TODO Auto-generated method stub
		return homeMapper.getRecommendList();
	}

	@Override
	public List<Home> getSuccessInfo() {
		// TODO Auto-generated method stub
		return homeMapper.getSuccessInfo();
	}

}
