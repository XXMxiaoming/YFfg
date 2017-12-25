package com.yfwl.yfgp.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.FriendRelationMapper;
import com.yfwl.yfgp.model.FriendRelation;
import com.yfwl.yfgp.service.FriendRelationService;

@Service
public class FriendRelationServiceImpl implements FriendRelationService {
	
	@Autowired
	private FriendRelationMapper friendRelationMapper;
	
	@Override
	public Map<String,Object> insertFriend(String loginName, String searchName) {
		// TODO Auto-generated method stub
		
		Map<String, Object> map = new HashMap<String, Object>();
		FriendRelation friendRelation = friendRelationMapper.validateIsAddBefore(loginName, searchName);
		if(friendRelation != null){
			String isDisabled = friendRelation.getIsDisabled();
			if(isDisabled.equals("Y")){
				//之前添加过，现在重新加
				Integer insertVal = friendRelationMapper.insertFriendAgain(loginName, searchName);
				Integer insertVal2 = friendRelationMapper.insertFriendAgain(searchName, loginName);
				
				if(insertVal > 0 && insertVal2 > 0){
					map.put("status", 0);
					map.put("message", "添加成功");
					map.put("data", "");
				}else{
					map.put("status", 5);
					map.put("message", "添加失败");
					map.put("data", "");
				}
			}else{
				//已经是好友
				map.put("status", 4);
				map.put("message", "早已经添加过该用户");
				map.put("data", "");
			}
		}else{
			//第一次添加
			String uIsFirst ;
			Integer uCount = friendRelationMapper.checkIsFirst(loginName);
			if(uCount > 0){
				uIsFirst = "N";
			}else{
				uIsFirst = "Y";
			}
			Integer insertVal = friendRelationMapper.insertFriend(loginName, searchName, uIsFirst);
			
			String fIsFirst ;
			Integer fCount = friendRelationMapper.checkIsFirst(searchName);
			if(fCount > 0){
				fIsFirst = "N";
			}else{
				fIsFirst = "Y";
			}
			Integer insertVal2 = friendRelationMapper.insertFriend(searchName, loginName, fIsFirst);
			if(insertVal > 0 && insertVal2 > 0){
				map.put("status", 0);
				map.put("message", "添加成功");
				map.put("data", "");
			}else{
				map.put("status", 5);
				map.put("message", "添加失败");
				map.put("data", "");
			}
		}
		return map;
	}
	
	@Override
	public boolean deleteFriend(String user, String friend) {
		// TODO Auto-generated method stub
		boolean isDeleteOk = false;
		Integer deleteVal = friendRelationMapper.deleteFriend(user, friend);
		if(deleteVal > 0){
			isDeleteOk = true;
		}
		return isDeleteOk;
	}

}
