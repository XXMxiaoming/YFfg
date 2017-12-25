package com.yfwl.yfgp.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.LoginByThirdAppAccountMapper;
import com.yfwl.yfgp.model.ThirdAppAccount;
import com.yfwl.yfgp.service.LoginByThirdAppAccountService;

@Service
public class LoginByThirdAppAccountServiceImpl implements LoginByThirdAppAccountService{
	
	@Autowired
	private LoginByThirdAppAccountMapper loginByThirdAppAccountMapper;
	
	@Override
	public boolean thirdAccountIdIsHaven(String thirdAccountId) {
		// TODO Auto-generated method stub
		boolean isHaven = false;		
		Integer selectVal = loginByThirdAppAccountMapper.thirdAccountIdIsHaven(thirdAccountId);
		if(selectVal >0){
			isHaven = true;
		}
		return isHaven;
	}

	@Override
	public boolean insertConnectRecord(ThirdAppAccount thirdAppAccount) {
		// TODO Auto-generated method stub
		boolean isInsertOk = false;
		Integer inserVal = loginByThirdAppAccountMapper.insertConnectRecord(thirdAppAccount);
		if(inserVal >0){
			isInsertOk = true;
		}
		return isInsertOk;
	}

	@Override
	public Map<String, Object> selectThirdAccount(Integer userId) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		List<ThirdAppAccount> list = loginByThirdAppAccountMapper.selectThirdAccount(userId);
		
		List<ThirdAppAccount> list2 = new ArrayList<ThirdAppAccount>();
		
		if(list.size()>0){
			for(int i=0;i<list.size();i++){
				ThirdAppAccount thirdAppAccount = list.get(i);
				list2.add(thirdAppAccount);
			}
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", list2);
		}else if(list.size()== 0){
			
			map.put("status", 2);
			map.put("message", "操作成功，该用户没有绑定第三方账号");
			map.put("data", "");
		}else{
			map.put("status", -4);
			map.put("message", "操作失败");
			map.put("data", "");
		}	
		return map;
	}

	@Override
	public ThirdAppAccount getThirdAccount(String thirdAccountId) {
		// TODO Auto-generated method stub
		return loginByThirdAppAccountMapper.getThirdAccount(thirdAccountId);
	}

}
