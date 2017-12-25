package com.yfwl.yfgp.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.yfwl.yfgp.dao.ChatRoomMapper;
import com.yfwl.yfgp.dao.UserMapper;
import com.yfwl.yfgp.model.ChatRoom;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.ChatRoomService;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

	@Autowired
	private ChatRoomMapper chatRoomMapper;
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public Integer insertChatRoom(ChatRoom chatRoom) {
		// TODO Auto-generated method stub
		return chatRoomMapper.insertChatRoom(chatRoom);
	}

	@Override
	public Integer deleteChatRoom(String easeCrId) {
		// TODO Auto-generated method stub
		return chatRoomMapper.deleteChatRoom(easeCrId);
	}

	@Override
	public ChatRoom getCRByEaseId(String easeCrId) {
		// TODO Auto-generated method stub
		return chatRoomMapper.getCRByEaseId(easeCrId);
	}

	@Override
	public Integer updateCR(ChatRoom chatRoom) {
		// TODO Auto-generated method stub
		return chatRoomMapper.updateCR(chatRoom);
	}

	@Override
	public List<String> getCRPaging(Integer pageCount) {
		// TODO Auto-generated method stub
		return chatRoomMapper.getCRPaging(pageCount);
	}

	@Override
	public List<Object> getSYCR() {
		// TODO Auto-generated method stub
		List<Object> resultList = new ArrayList<Object>();
		int[] sequenceArr = {1,2,3};
		for(int sequence : sequenceArr){
			ChatRoom cr = chatRoomMapper.getSYCR(sequence);
			if(cr != null){
				String easemodId = cr.getCrOwner();
				User user = userMapper.selectUsernameByeasemobId(easemodId);
				if(user != null){
					Map<String,Object> userMap = new HashMap<String,Object>();
					userMap.put("level", user.getUserLevel());
					userMap.put("userName", user.getUserName());
					userMap.put("headImage", user.getHeadImage());
					
					Map<String,Object> dataMap = new HashMap<String,Object>();
					dataMap.put("owner", userMap);
					dataMap.put("roomId", cr.getEaseCrId());
					dataMap.put("name", cr.getCrName());
					
					resultList.add(dataMap);
				}else{
					continue;
				}
			}else{
				continue;
			}
		}
		return resultList;
	}

	@Override
	public List<ChatRoom> getCRSimpleInfo(Integer pageCount) {
		// TODO Auto-generated method stub
		return chatRoomMapper.getCRSimpleInfo(pageCount);
	}

}
