package com.yfwl.yfgp.easemodrest.method;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class EasemobIMUsersMethod {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EasemobIMUsers.class);
	private static final String APPKEY = Constants.APPKEY;
	private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    // 通过app的client_id和client_secret来获取app管理员token
    private static Credential credential = new ClientSecretCredential(Constants.APP_CLIENT_ID,
            Constants.APP_CLIENT_SECRET, Roles.USER_ROLE_APPADMIN);
	
    /**
   	 * 注册IM用户[单个]
   	 * 
   	 * 给指定AppKey创建一个新的用户
   	 * 
   	 * @param dataNode
   	 * @return
   	 */
   	public static ObjectNode createNewIMUserSingle(ObjectNode dataNode) {
   		
   		ObjectNode objectNode = factory.objectNode();

   		// check appKey format
   		if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
   			LOGGER.error("Bad format of Appkey: " + APPKEY);

   			objectNode.put("message", "Bad format of Appkey");

   			return objectNode;
   		}

   		objectNode.removeAll();

   		// check properties that must be provided
   		if (null != dataNode && !dataNode.has("username")) {
   			LOGGER.error("Property that named username must be provided .");

   			objectNode.put("message",
   					"Property that named username must be provided .");

   			return objectNode;
   		}
   		if (null != dataNode && !dataNode.has("password")) {
   			LOGGER.error("Property that named password must be provided .");

   			objectNode.put("message",
   					"Property that named password must be provided .");

   			return objectNode;
   		}

   		try {
   			JerseyWebTarget webTarget = EndPoints.USERS_TARGET.resolveTemplate("org_name",
   					APPKEY.split("#")[0]).resolveTemplate("app_name",
   					APPKEY.split("#")[1]);

   			objectNode = JerseyUtils.sendRequest(webTarget, dataNode, credential, HTTPMethod.METHOD_POST, null);

   		} catch (Exception e) {
   			e.printStackTrace();
   		}

   		return objectNode;
   	}


	/**
	 * IM用户登录
	 * 
	 * @param ownerUserName
	 * @param password
     *
	 * @return
	 */
	public static ObjectNode imUserLogin(String ownerUserName, String password) {

		ObjectNode objectNode = factory.objectNode();

		// check appKey format
		if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);

			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}
		if (StringUtils.isEmpty(ownerUserName)) {
			LOGGER.error("Your userName must be provided，the value is username or uuid of imuser.");

			objectNode.put("message",
							"Your userName must be provided，the value is username or uuid of imuser.");

			return objectNode;
		}
		if (StringUtils.isEmpty(password)) {
			LOGGER.error("Your password must be provided，the value is username or uuid of imuser.");

			objectNode.put("message",
							"Your password must be provided，the value is username or uuid of imuser.");

			return objectNode;
		}

		try {
			ObjectNode dataNode = factory.objectNode();
			dataNode.put("grant_type", "password");
			dataNode.put("username", ownerUserName);
			dataNode.put("password", password);

			List<NameValuePair> headers = new ArrayList<NameValuePair>();
			headers.add(new BasicNameValuePair("Content-Type", "application/json"));

			objectNode = JerseyUtils.sendRequest(EndPoints.TOKEN_APP_TARGET
					.resolveTemplate("org_name", APPKEY.split("#")[0])
					.resolveTemplate("app_name", APPKEY.split("#")[1]),
					dataNode, null, HTTPMethod.METHOD_POST, headers);

		} catch (Exception e) {
			throw new RuntimeException(	"Some errors ocuured while fetching a token by usename and passowrd .");
		}

		return objectNode;
	}
	
	/**
	 * 添加好友[单个]
	 * 
	 * @param ownerUserName
	 * @param friendUserName
	 * 
	 * @return
	 */
	public static ObjectNode addFriendSingle(String ownerUserName,
			String friendUserName) {
		ObjectNode objectNode = factory.objectNode();

		// check appKey format
		if (!JerseyUtils.match("^(?!-)[0-9a-zA-Z\\-]+#[0-9a-zA-Z]+", APPKEY)) {
			LOGGER.error("Bad format of Appkey: " + APPKEY);

			objectNode.put("message", "Bad format of Appkey");

			return objectNode;
		}

		if (StringUtils.isEmpty(ownerUserName)) {
			LOGGER.error("Your userName must be provided，the value is username or uuid of imuser.");

			objectNode
					.put("message",
							"Your userName must be provided，the value is username or uuid of imuser.");

			return objectNode;
		}

		if (StringUtils.isEmpty(friendUserName)) {
			LOGGER.error("The userName of friend must be provided，the value is username or uuid of imuser.");

			objectNode
					.put("message",
							"The userName of friend must be provided，the value is username or uuid of imuser.");

			return objectNode;
		}

		try {
			JerseyWebTarget webTarget = EndPoints.USERS_ADDFRIENDS_TARGET
					.resolveTemplate("org_name", APPKEY.split("#")[0])
					.resolveTemplate("app_name", APPKEY.split("#")[1])
					.resolveTemplate("ownerUserName", ownerUserName)
					.resolveTemplate("friendUserName",
							friendUserName);

			ObjectNode body = factory.objectNode();
			objectNode = JerseyUtils.sendRequest(webTarget, body, credential,
					HTTPMethod.METHOD_POST, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return objectNode;
	}
}
