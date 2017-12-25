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

import com.easemob.server.method.ChatRoomMethods;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.model.ChatRoom;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.ChatRoomService;
import com.yfwl.yfgp.service.UserService;

@Controller
@RequestMapping("/chatroom")
public class ChatRoomController extends BaseController{
	
	@Autowired
	private ChatRoomService chatRoomService;
	@Autowired
	private UserService userService;
	
	//分页获取聊天室简要信息
	@RequestMapping(value = "/getCRSimpleInfo",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getCRSimpleInfo(HttpServletRequest request,
			HttpServletResponse response) {
		
		String pN = request.getParameter("pageNow");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(pN!=null && !pN.isEmpty()){
			Integer pageNow = Integer.parseInt(pN);
			Integer pageCount = (pageNow-1)*15;
			List<ChatRoom> listOfChatRoom = chatRoomService.getCRSimpleInfo(pageCount);
			if(listOfChatRoom.size()>0){
				List<Object> dataList = new ArrayList<Object>();
				for(int i=0; i<listOfChatRoom.size(); i++){
					ChatRoom cr = listOfChatRoom.get(i);
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("roomId", cr.getEaseCrId());
					
					User user = userService.selectUserByUsername(cr.getCrOwner());
					if(user != null){
						Map<String,Object> userMap = new HashMap<String,Object>();
						userMap.put("userName", user.getUserName());
						userMap.put("level", user.getUserLevel());
						userMap.put("headImage", user.getHeadImage());
						
						map.put("owner", userMap);
						map.put("name", cr.getCrName());
						
						dataList.add(map);
					}else{
						continue;
					}
				}
				
				Map<String,Object> dataMap = new HashMap<String,Object>();
				if(listOfChatRoom.size() == 15){
					dataMap.put("havaMore", "Y");
				}else{
					dataMap.put("havaMore", "N");
				}
				dataMap.put("chatrooms", dataList);
				
				resultMap = rspFormat(dataMap,SUCCESS);
			}else{
				resultMap = rspFormat("",NO_CHATROOM);
			}
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	
	
	//创建聊天室
	@RequestMapping(value = "/createCR",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> createChatRoom(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		Map<String,Object> resultMap = new HashMap<String,Object>();
		String name = request.getParameter("name");
		String desc = request.getParameter("desc");
		Long maxUsers = (long) 200;
		String owner = request.getParameter("owner");
		String paramMembers = request.getParameter("members");
		
		if(name!=null && !name.isEmpty() && desc!=null && !desc.isEmpty() && owner!=null && !owner.isEmpty()){
			
			String[] members;
			if(paramMembers!=null&&!paramMembers.isEmpty()){
				members = paramMembers.split(",");
			}else{
				members = new String[0];
			}
			
			Map<String,Object> map = ChatRoomMethods.createChatRoom(name, desc, maxUsers, owner, members);
			String status = (String) map.get("status");
			if(status.equals("200")){
				String returnStr = (String) map.get("return");
				ObjectMapper mapper = new ObjectMapper();  
			    JsonNode root = mapper.readTree(returnStr);
			    JsonNode data = root.path("data");
			    String easeCrId = data.get("id").textValue();
			   
			    ChatRoom cr = new ChatRoom(easeCrId,name,desc,owner,200);
			    
			    chatRoomService.insertChatRoom(cr);
			    Map<String,Object> dataMap = new HashMap<String,Object>();
			    dataMap.put("id", easeCrId);
			    dataMap.put("name", name);
			    resultMap = rspFormat(dataMap,SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
			
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	//修改
	@RequestMapping(value = "/modifyCR",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> modifyCR(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		
		String roomId = request.getParameter("roomId");
		String name = request.getParameter("name");
		String desc = request.getParameter("desc");
		String paramMaxUser = request.getParameter("maxUser");
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		if(roomId!=null && !roomId.isEmpty() && name!=null && !name.isEmpty() && desc!=null && !desc.isEmpty() && paramMaxUser!=null && !paramMaxUser.isEmpty()){
			Long maxUser = Long.parseLong(paramMaxUser);
			Map<String,Object> map = ChatRoomMethods.modifyChatRoom(roomId, name, desc, maxUser);
			String status = (String) map.get("status");
			if(status.equals("200")){
				ChatRoom cr = chatRoomService.getCRByEaseId(roomId);
				if(cr != null){
					ChatRoom chatRoom = new ChatRoom(roomId,name,desc,Integer.parseInt(paramMaxUser));
					chatRoomService.updateCR(chatRoom);
				}
				resultMap = rspFormat("",SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	//获取单个聊天室信息
	@RequestMapping(value = "/getSingleRoomDetail",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getSingleRoomDetail(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		
		String roomId = request.getParameter("roomId");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(roomId!=null && !roomId.isEmpty()){
			
			ChatRoom cr = chatRoomService.getCRByEaseId(roomId);
			
			Map<String,Object> map = ChatRoomMethods.getChatRoomDetail(roomId);
			String status = (String) map.get("status");
			if(status.equals("200")){
				String returnStr = (String) map.get("return");
				ObjectMapper mapper = new ObjectMapper();  
			    JsonNode root = mapper.readTree(returnStr);
			    JsonNode data = root.path("data");
			    
			    ObjectNode objectNode =  (ObjectNode) data.get(0);
			    if(cr != null){
			    	objectNode.put("owner", cr.getCrOwner());
			    }
			    
				resultMap = rspFormat(objectNode,SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		
		return resultMap;
	}
	
	
	//聊天室添加单个用户
	@RequestMapping(value = "/addSingleUser",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> addSingleUser(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		String roomId = request.getParameter("roomId");
		String userName = request.getParameter("userName");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(roomId!=null && !roomId.isEmpty() && userName!=null && !userName.isEmpty()){
			Map<String,Object> map = ChatRoomMethods.addSingleUserToChatRoom(roomId, userName);
			String status = (String) map.get("status");
			if(status.equals("200")){
				String returnStr = (String) map.get("return");
				ObjectMapper mapper = new ObjectMapper();  
			    JsonNode root = mapper.readTree(returnStr);
			    JsonNode data = root.path("data");
				resultMap = rspFormat(data,SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	//聊天室添加多个用户
	@RequestMapping(value = "/addManyUsers",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> addManyleUsers(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		String roomId = request.getParameter("roomId");
		String paramUserNames = request.getParameter("userNames");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(roomId!=null && !roomId.isEmpty() && paramUserNames!=null && !paramUserNames.isEmpty()){
			String[] userNames = paramUserNames.split(",");
			Map<String,Object> map = ChatRoomMethods.addBatchUsersToChatRoom(roomId, userNames);
			String status = (String) map.get("status");
			if(status.equals("200")){
				String returnStr = (String) map.get("return");
				ObjectMapper mapper = new ObjectMapper();  
			    JsonNode root = mapper.readTree(returnStr);
			    JsonNode data = root.path("data");
				resultMap = rspFormat(data,SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
 		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	//删除单个用户
	@RequestMapping(value = "/deleteSingleUser",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> deleteSingleUser(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		String roomId = request.getParameter("roomId");
		String userName = request.getParameter("userName");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(roomId!=null && !roomId.isEmpty() && userName!=null && !userName.isEmpty()){
			Map<String,Object> map = ChatRoomMethods.removeSingleUserFromChatRoom(roomId, userName);
			String status = (String) map.get("status");
			if(status.equals("200")){
				String returnStr = (String) map.get("return");
				ObjectMapper mapper = new ObjectMapper();  
			    JsonNode root = mapper.readTree(returnStr);
			    JsonNode data = root.path("data");
				resultMap = rspFormat(data,SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	//删除多个用户
	@RequestMapping(value = "/deleteManyUsers",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> deleteManyleUsers(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		String roomId = request.getParameter("roomId");
		String paramUserNames = request.getParameter("userNames");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(roomId!=null && !roomId.isEmpty() && paramUserNames!=null && !paramUserNames.isEmpty()){
			String[] userNames = paramUserNames.split(",");
			Map<String,Object> map = ChatRoomMethods.removeBatchUsersFromChatRoom(roomId, userNames);
			String status = (String) map.get("status");
			if(status.equals("200")){
				String returnStr = (String) map.get("return");
				ObjectMapper mapper = new ObjectMapper();  
			    JsonNode root = mapper.readTree(returnStr);
			    JsonNode data = root.path("data");
				resultMap = rspFormat(data,SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
 		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	//删除聊天室
	@RequestMapping(value = "/deleteCR",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> deleteCR(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		String roomId = request.getParameter("roomId");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(roomId!=null && !roomId.isEmpty()){
			Map<String,Object> map = ChatRoomMethods.deleteChatRoom(roomId);
			String status = (String) map.get("status");
			if(status.equals("200")){
				ChatRoom cr = chatRoomService.getCRByEaseId(roomId);
				if(cr != null){
					chatRoomService.deleteChatRoom(roomId);
				}
				resultMap = rspFormat("",SUCCESS);
			}else{
				resultMap = rspFormat("",HX_REQUEST_ERROR);
			}
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	
	//获取所有聊天室
	@RequestMapping(value = "/getAllCR",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getAllCR(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Map<String,Object> map = ChatRoomMethods.getAllChatRoom();
		String status = (String) map.get("status");
		if(status.equals("200")){
			String returnStr = (String) map.get("return");
			ObjectMapper mapper = new ObjectMapper();  
		    JsonNode root = mapper.readTree(returnStr);
		    JsonNode data = root.path("data");
			resultMap = rspFormat(data,SUCCESS);
		}else{
			resultMap = rspFormat("",HX_REQUEST_ERROR);
		}
		
		return resultMap;
	}
	
	
	//分页获取聊天室
	@RequestMapping(value = "/getCRPaging",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getCRPaging(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		String pN = request.getParameter("pageNow");
		Map<String,Object> resultMap = new HashMap<String,Object>();
		if(pN!=null && !pN.isEmpty()){
			Integer pageNow = Integer.parseInt(pN);
			Integer pageCount = (pageNow-1)*15;
			List<String> listOfRoomId = chatRoomService.getCRPaging(pageCount);
			if(listOfRoomId.size()>0){
				List<Object> dataList = new ArrayList<Object>();
				for(int i=0;i<listOfRoomId.size();i++){
					String roomId = listOfRoomId.get(i);
					Map<String,Object> map = ChatRoomMethods.getChatRoomDetail(roomId);
					String status = (String) map.get("status");
					if(status.equals("200")){
						String returnStr = (String) map.get("return");
						ObjectMapper mapper = new ObjectMapper();  
					    JsonNode root = mapper.readTree(returnStr);
					    JsonNode data = root.path("data");
					    dataList.add(data.get(0));
					}else{
						continue;
					}
				}
				resultMap = rspFormat(dataList,SUCCESS);
			}else{
				resultMap = rspFormat("",NO_CHATROOM);
			}
		}else{
			resultMap = rspFormat("",WRONG_PARAM);
		}
		return resultMap;
	}
	
	
	@RequestMapping(value = "/getSYCR",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> getSYCR(HttpServletRequest request,
			HttpServletResponse response) throws JsonProcessingException, IOException{
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		List<Object> list = chatRoomService.getSYCR();
		if(list.size() > 0){
			map = rspFormat(list,SUCCESS);
		}else{
			map = rspFormat("",FAIL);
		}
		
		return map;
	}
}
