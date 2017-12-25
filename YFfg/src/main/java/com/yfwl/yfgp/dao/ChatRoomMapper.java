package com.yfwl.yfgp.dao;

import java.util.List;

import com.yfwl.yfgp.model.ChatRoom;

public interface ChatRoomMapper {
	
	Integer insertChatRoom (ChatRoom chatRoom);
	Integer deleteChatRoom (String easeCrId);
	ChatRoom getCRByEaseId(String easeCrId);
	Integer updateCR(ChatRoom chatRoom);
	List<String> getCRPaging(Integer pageCount);
	List<ChatRoom> getCRSimpleInfo(Integer pageCount);
	
	ChatRoom getSYCR(Integer sequence);
}
