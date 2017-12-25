package com.yfwl.yfgp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
	
	//private static String filePath = "properties/local.properties";
	//private static String filePath = "properties/private_Net.properties";
	private static String filePath = "properties/public_Net.properties";
	
	public static Properties getProperties() {
		Properties p = new Properties();
		try {
			InputStream inputStream = PropertiesUtils.class.getClassLoader()
					.getResourceAsStream(filePath);
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p;
	}
	

	/**
	 * 读取版本号，版本名称，更新概要
	 * @return
	 */
	public static Properties getAppVersionProperties() {
		Properties p = new Properties();
		try {
			File file = new File(PropertiesUtils.getProperties()
					.getProperty("AppVersionFilePath")); 
			InputStream in = new FileInputStream(file);
			p.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p;
	}
	
	//读取服务器ip配置文件
	public static Properties getServerIpConf() {
		Properties p = new Properties();
		try {
			File file = new File(PropertiesUtils.getProperties()
					.getProperty("ipConfig")); 
			InputStream in = new FileInputStream(file);
			p.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p;
	}
	
	
	/**
	 * 读取测试题版本号
	 * @return
	 */
	public static Properties getQuestionProperties() {
		Properties p = new Properties();
		try {
			File file = new File(PropertiesUtils.getProperties()
					.getProperty("QuestionFilePath")); 
			InputStream in = new FileInputStream(file);
			p.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p;
	}
	
	
	public static String getServerUserString() {
		String userId = PropertiesUtils.getProperties().getProperty("userId");
		return userId;
	}
}
