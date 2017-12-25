package com.yfwl.yfgp.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.UserAttentionMapper;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.UserAttention;
import com.yfwl.yfgp.service.UserAttentionService;

@Service
public class UserAttentionServiceImpl implements UserAttentionService{
	
	@Autowired
	private UserAttentionMapper userAttentionMapper;
	
	/**
	 * 查询是否关注过
	 * @param userId
	 * @param attUserName
	 * @return
	 */
	@Override
	public Map<String, Object> isAttented(Integer userId, String attUserName) {
		// TODO Auto-generated method stub
		
		Map<String,Object> map = new HashMap<String,Object>();	
		
		if(userId == null || attUserName == null || attUserName == ""){
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
			
		}else{
			UserAttention userAttention = userAttentionMapper.selectIsAttention(userId, attUserName);
			if(userAttention != null){
				String isDisabled = userAttention.getIsDisabled();
				if(isDisabled.equals("Y")){
					map.put("status", 1);
					map.put("message", "还没有关注过");
					map.put("data", "");
				}else{
					map.put("status", 0);
					map.put("message", "已经关注过");
					map.put("data", "");
				}
			}else{
				map.put("status", 1);
				map.put("message", "还没有关注过");
				map.put("data", "");
			}
		}		
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
	@Override
	public Map<String, Object> attendOperation(Integer userId, String attUserName, boolean b) {
		// TODO Auto-generated method stub				
		Map<String,Object> map = new HashMap<String,Object>();		
		if(userId == null || attUserName == null || attUserName == ""){
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}else{
			UserAttention userAttention = userAttentionMapper.selectIsAttention(userId, attUserName);
			if(userAttention != null){
				String isDisabled = userAttention.getIsDisabled();
				if(isDisabled.equals("Y")){
					//之前关注过，但取消了，现在重新关注
					Integer insertVal = userAttentionMapper.insertAttentionAgain(userId, attUserName);
					if(insertVal >0 ){
						map.put("status", 2);
						map.put("message", "已添加关注");
						map.put("data", "");
					}
				}else{
					//现在是已关注状态（点击就是取消关注）
					if(b) {
						Integer deleteValue = userAttentionMapper.deleteAttention(userId, attUserName);
						if(deleteValue >0 ){
							map.put("status", 1);
							map.put("message", "已取消关注");
							map.put("data", "");
						}
					}
					else {
						map.put("status", 2);
						map.put("message", "已添加关注");
						map.put("data", "");
					}
				}
			}else{
				//之前没有关注过，第一次关注该用户
				String isFirst;
				Integer count = userAttentionMapper.checkIsFirst(userId);
				if(count > 0){
					isFirst = "N";
				}else{
					isFirst = "Y";
				}
				Integer insertVal = userAttentionMapper.insertAttention(userId, attUserName, isFirst);
				if(insertVal >0 ){
					map.put("status", 2);
					map.put("message", "已添加关注");
					map.put("data", "");
				}
			}
		}			
		return map;
	}


	@Override
	public Map<String, Object> getMyAttentionList(Integer userId, Integer pageCount) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		if(userId == null) {
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		else {
			List<User> list = userAttentionMapper.getMyAttentionList(userId, pageCount);
			if(list !=null && list.size() > 0 ){
				for(int i = 0; i < list.size(); i++ ){
					list.get(i).setAmCount(userAttentionMapper.getAttentMineCount(list.get(i).getUserId()));
					list.get(i).setMaCount(userAttentionMapper.getMyAttentionCount(list.get(i).getUserId()));
				}
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", list);
			}else{
				map.put("status", 1);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		return map;
	}


	@Override
	public Map<String, Object> getAttentMineList(Integer userId, Integer pageCount) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		if(userId == null) {
			map.put("status", -4);
			map.put("message", "参数为空");
			map.put("data", "");
		}
		else {
			List<User> list = userAttentionMapper.getAttentMineList(userId, pageCount);
			if(list !=null && list.size() > 0){
				for(int i = 0; i < list.size(); i++ ){
					list.get(i).setAmCount(userAttentionMapper.getAttentMineCount(list.get(i).getUserId()));
					list.get(i).setMaCount(userAttentionMapper.getMyAttentionCount(list.get(i).getUserId()));
				}
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", list);
			}else{
				map.put("status", 1);
				map.put("message", "操作失败");
				map.put("data", "");
			}
		}
		return map;
	}


	@Override
	public Integer getMyAttentionCount(Integer userId) {
		// TODO Auto-generated method stub
		return userAttentionMapper.getMyAttentionCount(userId);
	}


	@Override
	public Integer getAttentMineCount(Integer userId) {
		// TODO Auto-generated method stub
		return userAttentionMapper.getAttentMineCount(userId);
	}


	@Override
	public Integer getAttentRelation(UserAttention userAttention) {
		// TODO Auto-generated method stub
		return userAttentionMapper.getAttentRelation(userAttention);
	}


	@Override
	public boolean attented(Integer userId, String attUserName) {
		// TODO Auto-generated method stub
		boolean isOk = false;
		String isFirst = "Y";
		Integer count = userAttentionMapper.checkIsFirst(userId);
		if(count>0){
			isFirst = "N";
		}
		
		UserAttention userAttention = userAttentionMapper.selectIsAttention(userId, attUserName);
		Integer insertVal = 0;
		if(userAttention != null){
			insertVal = userAttentionMapper.insertAttentionAgain(userId, attUserName);
		}else{
			insertVal = userAttentionMapper.insertAttention(userId, attUserName, isFirst);
		}
		
		if(insertVal>0){
			isOk = true;
		}
		
		return isOk;
	}

	

}
