package com.yfwl.yfgp.easemodrest.vo;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyWebTarget;

import com.yfwl.yfgp.easemodrest.comm.Constants;
import com.yfwl.yfgp.easemodrest.utils.JerseyUtils;

/**
 * JerseyWebTarget EndPoints
 * 
 * @author Lynch 2014-09-15
 *
 */
public interface EndPoints {

	final JerseyClient CLIENT = JerseyUtils.getJerseyClient(true);

	final JerseyWebTarget ROOT_TARGET = CLIENT
			.target(Constants.API_HTTP_SCHEMA + "://"
					+ Constants.API_SERVER_HOST + "/");

	JerseyWebTarget APPLICATION_TEMPLATE = ROOT_TARGET
			.path("{org_name}").path("{app_name}");

	JerseyWebTarget TOKEN_APP_TARGET = APPLICATION_TEMPLATE
			.path("token");

	JerseyWebTarget USERS_TARGET = APPLICATION_TEMPLATE.path("users");

	JerseyWebTarget USERS_ADDFRIENDS_TARGET = APPLICATION_TEMPLATE
			.path("users").path("{ownerUserName}").path("contacts")
			.path("users").path("{friendUserName}");

	JerseyWebTarget MESSAGES_TARGET = APPLICATION_TEMPLATE
			.path("messages");

	JerseyWebTarget CHATMESSAGES_TARGET = APPLICATION_TEMPLATE
			.path("chatmessages");

	JerseyWebTarget CHATGROUPS_TARGET = APPLICATION_TEMPLATE
			.path("chatgroups");

	JerseyWebTarget CHATFILES_TARGET = APPLICATION_TEMPLATE
			.path("chatfiles");
}
