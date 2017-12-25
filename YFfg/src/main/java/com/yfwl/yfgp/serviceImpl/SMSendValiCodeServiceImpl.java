package com.yfwl.yfgp.serviceImpl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.yfwl.yfgp.model.SmsResult;
import com.yfwl.yfgp.model.ValidateCode;
import com.yfwl.yfgp.service.SMSendValiCodeService;
import com.yfwl.yfgp.service.ValidateCodeService;
import com.yfwl.yfgp.utils.RandomStringUtil;
import com.yfwl.yfgp.utils.SendSmsUtils;

@Service
public class SMSendValiCodeServiceImpl implements SMSendValiCodeService {
	
	@Autowired
	private ValidateCodeService validateCodeService;
	private static final String apikey = "1860ca8654d60eb22499790cb0c301ad";
	
	@Override
	public boolean sendSMS(String phone, String type) {
		// TODO Auto-generated method stub
		boolean isOk = false;
		if(phone != null && !phone.isEmpty() && type != null && !type.isEmpty()){
			String randomNum = RandomStringUtil.getRandomNum();
			String text = "";
			switch (type) {
			case "DSFDL":
				text = "【宜发网络】感谢您注册股哥，您的验证码是" + randomNum;
				break;
			case "ZC":
				text = "【宜发网络】感谢您注册股哥，您的验证码是" + randomNum;
				break;
			case "BP":
				text = "【宜发网络】感谢您使用股哥，您的验证码是" + randomNum;
				break;
			case "ZHMM":
				text = "【宜发网络】正在找回密码，您的验证码是" + randomNum;
				break;
			case "ZFMM":
				text = "【宜发网络】您的账户支付密码设置验证码为" + randomNum;
				break;	
			default:
				break;
			}
			
			try {
				String jsonString = SendSmsUtils.sendSms(apikey, text, phone);
				Gson gson = new Gson();
				SmsResult smsResult = gson.fromJson(jsonString,SmsResult.class);
				Integer resultCode = Integer.parseInt(smsResult.getCode());
				if(resultCode == 0){
					ValidateCode validateCode = new ValidateCode();
					validateCode.setPhone(phone);
					validateCode.setMarker(type);
					validateCode.setRandomNum(randomNum);
					ValidateCode validateCode2 = validateCodeService.getCode(validateCode);
					if(validateCode2 != null){
						validateCodeService.updateCode(validateCode);
					}else{
						validateCodeService.initCode(validateCode);
					}
					isOk = true;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isOk;
	}


	@Override
	public boolean validataCode(ValidateCode validateCode) {
		// TODO Auto-generated method stub
		boolean isOk = false;
		ValidateCode validateCode2 = validateCodeService.getCode(validateCode);
		String dbNum = validateCode2.getRandomNum();
		String paramNum = validateCode.getRandomNum();
		if(dbNum.equals(paramNum)){
			isOk = true;
		}
		return isOk;
	}

}
