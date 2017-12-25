package com.yfwl.yfgp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomStringUtil {

	public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String NUMCHAR = "0123456789";
	public static final String CHAR = "0123456789abcdefghijklmnopqrstuvwxyz";

	// 生成一个由7位小写字母数字随机字串（环信ID，环信密码）
	public static String getRandomCharNum() {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 7; i++) {
			sb.append(CHAR.charAt(random.nextInt(CHAR.length())));
		}
		return sb.toString();
	}

	// 生成一个四位数的验证码
	public static String getRandomNum() {
		String NUM = "123456789";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			sb.append(NUM.charAt(random.nextInt(NUM.length())));
		}
		return sb.toString();
	}
	
	public static String get5RandomNum() {
		String NUM = "0123456789";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			sb.append(NUM.charAt(random.nextInt(NUM.length())));
		}
		return sb.toString();
	}

	// 生成随机数串
	public static String getRandomNumMore() {

		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			sb.append(NUMCHAR.charAt(random.nextInt(NUMCHAR.length())));
		}
		SimpleDateFormat sdFormatter = new SimpleDateFormat("dHms");
		String strFormatNowDate = sdFormatter.format(new Date());
		Long l1 = Long.parseLong(strFormatNowDate)
				+ +Long.parseLong(sb.toString());
		return String.valueOf(l1);
	}

	
	
	public static String getRandomName() {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 6; i++) {
			sb.append(NUMCHAR.charAt(random.nextInt(NUMCHAR.length())));
		}
		return "用户" + sb.toString();
	}
	public static String getRandomPassword() {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 12; i++) {
			sb.append(CHAR.charAt(random.nextInt(CHAR.length())));
		}
		return sb.toString();
	}
	public static String getRandomPhone() {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 8; i++) {
			sb.append(NUMCHAR.charAt(random.nextInt(NUMCHAR.length())));
		}

		return "129" + sb.toString();
	}
	
	public static String getInvitationCode(Integer userId) {
		StringBuffer sb = new StringBuffer();
		Integer num = userId;
		Integer m;
		for (int i = 0; i < 4; i++) {
			m = num % ALLCHAR.length();
			num = num / ALLCHAR.length();
			sb.append(ALLCHAR.charAt(m));
		}
		return sb.toString();
	}
	
	/*public static void main(String[] args){
		String name = RandomStringUtil.getRandomName();
		String pass = RandomStringUtil.getRandomPassword();
		String phone = RandomStringUtil.getRandomPhone();
		System.out.println(name);
		System.out.println(pass);
		System.out.println(phone);
	}*/
}
