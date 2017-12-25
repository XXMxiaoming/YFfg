package com.yfwl.yfgp.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easemob.server.method.SendMessageMethods;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.service.UserService;

@Controller
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/sendMassMessage", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> sendMassMessage(HttpServletRequest request,
			HttpServletResponse response) {

		String auth = request.getHeader("Authorization");

		System.out.println(auth);

		// 返回结果的Map
		Map<String, Object> map = new HashMap<String, Object>();

		if (null != auth && !auth.isEmpty()) {
			if (auth.equals("28349cd2226f305e0b1ed3b72793cefb")) {
				// 要发送的消息内容
				String message = request.getParameter("message");

				// 系统发送员ID
				Integer adminId = Integer.parseInt(request
						.getParameter("adminId"));
				// 根据系统发送员ID查出它的环信ID
				String adminEasemobId = userService.selectUserById(adminId)
						.getEasemobId();

				// 目标发送用户的ID，string串转换成list，再循环查找出对应的环信ID
				String strOfId = request.getParameter("userIds");
				String[] arrayOfStrOfId = strOfId.split(",");
				List<String> listOfId = java.util.Arrays.asList(arrayOfStrOfId);

				// user环信ID的list
				List<String> userEasemobIdList = new ArrayList<String>();

				if (listOfId.size() > 0) {
					// 根据userId循环查找出对应的环信ID
					for (int i = 0; i < listOfId.size(); i++) {
						Integer userId = Integer.parseInt(listOfId.get(i));
						String userEasemobId = userService.selectUserById(
								userId).getEasemobId();
						userEasemobIdList.add(userEasemobId);
					}

					
					String targetType = "users";
					ObjectNode ext = JsonNodeFactory.instance.objectNode();
					SendMessageMethods.sendTxtMsg(targetType, userEasemobIdList, adminEasemobId, ext, message);
					
					/*// 检测用户是否在线
					String targetUserName = adminEasemobId;
					ObjectNode usernode = EasemobMessagesMethod
							.getUserStatus(targetUserName);
					if (null != usernode) {
						LOGGER.info("检测用户是否在线: " + usernode.toString());
					}

					String from = adminEasemobId;
					String targetTypeus = "users";
					ObjectNode ext = EasemobMessagesMethod.factory.objectNode();
					ArrayNode targetusers = EasemobMessagesMethod.factory
							.arrayNode();
					// 循环遍历 user环信ID的list
					for (String userEasemobId : userEasemobIdList) {
						targetusers.add(userEasemobId);
					}
					ObjectNode txtmsg = EasemobMessagesMethod.factory
							.objectNode();
					txtmsg.put("msg", message);
					txtmsg.put("type", "txt");
					ObjectNode sendTxtMessageusernode = EasemobMessagesMethod
							.sendMessages(targetTypeus, targetusers, txtmsg,
									from, ext);
					if (null != sendTxtMessageusernode) {
						LOGGER.info("给用户发一条文本消息: "
								+ sendTxtMessageusernode.toString());
						map.put("status", 0);
						map.put("message", "发送成功");
						map.put("data", "");
					}*/

				} else {
					map.put("status", 400);
					map.put("message", "参数错误");
					map.put("data", "");
				}
			} else {
				map.put("status", 401);
				map.put("message", "token错误");
				map.put("data", "");
			}
		}else{
			map.put("status", 402);
			map.put("message", "token为空");
			map.put("data", "");
		}

		return map;
	}

	public static String createYFToken(String key, String secret)
			throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("key", key);
		map.put("timestamp", String.valueOf(System.currentTimeMillis()));

		// 先将参数以其参数名的字典序升序进行排序
		Map<String, String> sortedParams = new TreeMap<String, String>(map);
		Set<Entry<String, String>> entrys = sortedParams.entrySet();

		// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
		StringBuilder basestring = new StringBuilder();
		for (Entry<String, String> param : entrys) {
			basestring.append(param.getKey()).append("=")
					.append(param.getValue());
		}
		basestring.append(secret);

		// 使用MD5对待签名串求签
		byte[] bytes = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
		} catch (GeneralSecurityException ex) {
			throw new IOException(ex);
		}

		// 将MD5输出的二进制结果转换为小写的十六进制
		StringBuilder token = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				token.append("0");
			}
			token.append(hex);
		}
		return token.toString();

	}

}
