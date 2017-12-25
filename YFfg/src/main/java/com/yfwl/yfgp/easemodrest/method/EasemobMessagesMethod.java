package com.yfwl.yfgp.easemodrest.method;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.easemodrest.comm.Constants;
import com.yfwl.yfgp.easemodrest.comm.HTTPMethod;
import com.yfwl.yfgp.easemodrest.comm.Roles;
import com.yfwl.yfgp.easemodrest.demo.EasemobIMUsers;
import com.yfwl.yfgp.easemodrest.utils.JerseyUtils;
import com.yfwl.yfgp.easemodrest.vo.ClientSecretCredential;
import com.yfwl.yfgp.easemodrest.vo.Credential;
import com.yfwl.yfgp.easemodrest.vo.EndPoints;

/**
 * 发消息
 * @author Luzhq
 *
 */
public class EasemobMessagesMethod {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EasemobIMUsers.class);
	private static final String APPKEY = Constants.APPKEY;
	public static final JsonNodeFactory factory = new JsonNodeFactory(false);

    // 通过app的client_id和client_secret来获取app管理员token
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);
    
    /**
	 * 检测用户是否在线
	 * 
	 * @param targetUserName
	 * @return
	 */
	public static ObjectNode getUserStatus(String targetUserName) {

		ObjectNode objectNode = factory.objectNode();

		// check appKey format
		if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);

			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}

		// check properties that must be provided
		if (StringUtils.isEmpty(targetUserName)) {
			LOGGER.error("You must provided a targetUserName .");

			objectNode.put("message", "You must provided a targetUserName .");

			return objectNode;
		}

		try {

			JerseyWebTarget webTarget = EndPoints.USERS_TARGET.resolveTemplate("org_name", APPKEY.split("#")[0])
					.resolveTemplate("app_name", APPKEY.split("#")[1]).path(targetUserName).path("status");

			objectNode = JerseyUtils.sendRequest(webTarget, null, credential, HTTPMethod.METHOD_GET, null);

			String userStatus = objectNode.get("data").path(targetUserName).asText();
			if ("online".equals(userStatus)) {
				LOGGER.error(String.format("The status of user[%s] is : [%s] .", targetUserName, userStatus));
			} else if ("offline".equals(userStatus)) {
				LOGGER.error(String.format("The status of user[%s] is : [%s] .", targetUserName, userStatus));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}

	/**
	 * 发送消息
	 * 
	 * @param targetType
	 *            消息投递者类型：users 用户, chatgroups 群组
	 * @param target
	 *            接收者ID 必须是数组,数组元素为用户ID或者群组ID
	 * @param msg
	 *            消息内容
	 * @param from
	 *            发送者
	 * @param ext
	 *            扩展字段
	 * 
	 * @return 请求响应
	 */
	public static ObjectNode sendMessages(String targetType, ArrayNode target, ObjectNode msg, String from,
			ObjectNode ext) {

		ObjectNode objectNode = factory.objectNode();

		ObjectNode dataNode = factory.objectNode();

		// check appKey format
		if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);

			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}

		// check properties that must be provided
		if (!("users".equals(targetType) || "chatgroups".equals(targetType))) {
			LOGGER.error("TargetType must be users or chatgroups .");

			objectNode.put("message", "TargetType must be users or chatgroups .");

			return objectNode;
		}

		try {
			// 构造消息体
			dataNode.put("target_type", targetType);
			dataNode.put("target", target);
			dataNode.put("msg", msg);
			dataNode.put("from", from);
			dataNode.put("ext", ext);

			JerseyWebTarget webTarget = EndPoints.MESSAGES_TARGET.resolveTemplate("org_name", APPKEY.split("#")[0]).resolveTemplate(
					"app_name", APPKEY.split("#")[1]);

			objectNode = JerseyUtils.sendRequest(webTarget, dataNode, credential, HTTPMethod.METHOD_POST, null);

			objectNode = (ObjectNode) objectNode.get("data");
			for (int i = 0; i < target.size(); i++) {
				String resultStr = objectNode.path(target.path(i).asText()).asText();
				if ("success".equals(resultStr)) {
					LOGGER.error(String.format("Message has been send to user[%s] successfully .", target.path(i).asText()));
				} else if (!"success".equals(resultStr)) {
					LOGGER.error(String.format("Message has been send to user[%s] failed .", target.path(i).asText()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
}
