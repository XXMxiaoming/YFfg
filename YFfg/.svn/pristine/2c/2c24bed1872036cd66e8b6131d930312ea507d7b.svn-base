package com.yfwl.yfgp.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.beans.factory.annotation.Autowired;

import com.easemob.server.method.SendMessageMethods;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.utils.Test;

public class BaseController {
	
	@Autowired
	private TokenService tokenService;
	
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	public static final int WRONG_MYSQL_OPERATION = 2;
	public static final int NO_ENOUGH_MONEY = 3;
	public static final int WRONG_PARAM = 4;
	public static final int WRONG_VALIDATE_CODE = 5;
	public static final int WRONG_PARAM_FORMAT = 6;
	public static final int WRONG_TOKEN = 7;
	public static final int WRONG_PASSWORD = 8;
	
	public static final int NOT_ALLOWED = 9;
	public static final int NO_BONUS = 10;
	public static final int HAVE_GOT_BONUS = 11;
	public static final int BONUS_STATUS = 12;
	public static final int NOT_GET_BONUS = 13;
	public static final int HAVE_FOLLOW = 14;
	
	public static final int HAVE_PORTFOLIO = 15;
	public static final int HAVA_SAME_PROTFOLIO = 16;
	public static final int HAVE_ENOUGH_PROTFOLIO =17;
	public static final int NOT_TRADE_TIME = 18;
	public static final int NOT_ENOUGH_STOCK = 19;
	public static final int HAVE_TRADE = 20;
	public static final int NOT_SUPPORT_STOCK = 21;
	public static final int BEYOND_LIMIT = 22;
	public static final int NOT_EXIST_ACCOUTS = 23;
	public static final int BEYOND_TIME = 24;
	
	public static final int HX_REQUEST_ERROR = 25;
	public static final int REPEAT_USERNAME = 26;
	public static final int ILLEGAL_USERNAME = 27;
	public static final int PHONE_BOUND = 28;
	public static final int NAME_PWD_WRONG = 29;
	public static final int NO_CHATROOM = 30;
	public static final int NO_USER = 31;
	public static final int HAVEN_PHONE = 32;
	public static final int HOLIDAY = 33;
	
	public static final int ERROR_ACCOUNTS_NAME = 34;
	
	public static final int MSG = 44;
	public static final int NO_MONEY = 45;
	public static final int NO_JQ = 46;
	public static final int HAS_TRY = 47;
	
	public static final int HAVEN_14ACCOUNTS = 48;
	
	public static final int USERNAME_ERROR = 49;
	public static final int PASSWORD_ERROR=50;
	public static final int FAIL2 =51;
	public static final int INSERT_STOCK =52;
	public static final int SELECT_STOCK =53;
	
	private Map<String,Object> map = new HashMap<String,Object>();
	
	
	
	public Map<String, Object> rspFormat (Object data, int code ) {
		String msg = "";
		map.put("data", data);
		map.put("status", code);
		switch(code) {
		case SUCCESS:
			msg = "操作成功";
			break;
		case FAIL:
			msg = "操作失败";
			break;
		case WRONG_MYSQL_OPERATION:
			msg = "数据库操作失败";
			break;
		case NO_ENOUGH_MONEY:
			msg = "余额不足";
			break;
		case WRONG_PARAM:
			msg = "参数错误";
			break;
		case WRONG_VALIDATE_CODE:
			msg = "验证码错误";
			break;
		case WRONG_PARAM_FORMAT:
			msg = "参数格式错误";
			break;
		case WRONG_TOKEN:
			msg = "token错误";
			break;
		case WRONG_PASSWORD:
			msg = "密码错误";
			break;
		case NOT_ALLOWED:
			msg = "不允许该操作";
			break;
		case NO_BONUS:
			msg = "红包已领完";
			break;
		case HAVE_GOT_BONUS:
			msg = "已领过该红包";
			break;
		case BONUS_STATUS:
			msg = "查看红包";
			break;
		case NOT_GET_BONUS:
			msg = "未领过该红包";
			break;
		case HAVE_PORTFOLIO:
			msg = "已创建优化组合";
			break;
		case HAVA_SAME_PROTFOLIO:
			msg = "该组合名已被使用";
			break;
		case HAVE_ENOUGH_PROTFOLIO:
			msg = "创建次数限制";
			break;
		case NOT_TRADE_TIME:
			msg = "非交易时间";
			break;
		case NOT_ENOUGH_STOCK:
			msg = "股票数目不足";
			break;
		case HAVE_TRADE:
			msg = "已交易成功";
			break;
		case NOT_SUPPORT_STOCK:
			msg = "不支持股票类型";
			break;
		case BEYOND_LIMIT:
			msg = "超过人数限制";
			break;
		case NOT_EXIST_ACCOUTS:
			msg = "该组合已被删除";
			break;
		case BEYOND_TIME:
			msg = "超过时间限制";
			break;
		case HX_REQUEST_ERROR:
			msg = "请求失败";
			break;
		case REPEAT_USERNAME:
			msg = "用户名重复";
			break;
		case ILLEGAL_USERNAME:
			msg = "用户名包含敏感词";
			break;
		case PHONE_BOUND:
			msg = "号码已经绑定过其他账户";
			break;
		case NAME_PWD_WRONG:
			msg = "用户名或密码错误";
			break;
		case NO_CHATROOM:
			msg = "没有聊天室";
			break;
		case HAVE_FOLLOW:
			msg = "已经订阅该组合";
			break;
		case NO_USER:
			msg = "用户不存在";
			break;
		case HAVEN_PHONE:
			msg = "您已绑定过手机号";
			break;
		case ERROR_ACCOUNTS_NAME:
			msg = "组合名称不合法";
			break;
		case HOLIDAY:
			msg = "启动或者结束时间为节假日";
			break;
		case NO_MONEY:
			msg = "余额不足";
			break;
		case NO_JQ:
			msg = "金券不足";
			break;
		case HAS_TRY:
			msg = "已经试用过";
			break;
		case HAVEN_14ACCOUNTS:
			msg = "已经创建过类型为14的组合";
			break;
		case USERNAME_ERROR:
			msg = "非法用户名";
			break;
		case PASSWORD_ERROR:
			msg="密码格式不正确";
			break;
		case FAIL2:
			msg="您已添加过此股票";
			break;	
		case INSERT_STOCK:
			msg="请先添加自选股";
			break;	
			
		case SELECT_STOCK:
		msg="查找成功";
		break;
		
		default:
			break;
		}
		map.put("msg", msg);
		return map;	
	}
	
	
	public Map<String, Object> rspFormatMsg (Object data, int code, String msg) {
		map.put("data", data);
		map.put("status", code);
		map.put("msg", msg);
		return map;	
	}
	
	public boolean validateToken(Integer userId, String token) {
		String dbToken = tokenService.selectTokenByUserId2(userId);
		if(dbToken.equals(token)) {
			return true;
		}
		return false;
	}
	
	public static void sendEaseMobMsg(List<String> list,ObjectNode ext,String msg)
	{
		String targetType = "users";
		String adminEasemobId = "lbh3zyi";
		SendMessageMethods.sendTxtMsg(targetType, list, adminEasemobId, ext, msg);
	}
	
	public static Logger getLogger(String filePath){
		Logger logger = Logger.getLogger(Test.class);
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%-d{yyyy-MM-dd HH:mm:ss} [ %t:%r ] - [ %p ] %m%n");
		DailyRollingFileAppender appender;
		try {
			appender = new DailyRollingFileAppender(layout,filePath,"yyyy-MM-dd'.txt'");
			logger.addAppender(appender);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logger;
	}
}
