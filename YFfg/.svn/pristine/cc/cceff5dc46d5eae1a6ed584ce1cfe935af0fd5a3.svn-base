package com.yfwl.yfgp.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.CashAccount;
import com.yfwl.yfgp.model.Contact;
import com.yfwl.yfgp.model.Coupon;
import com.yfwl.yfgp.model.Identity;
import com.yfwl.yfgp.model.Invitation;
import com.yfwl.yfgp.model.Mem;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.OwnStock;
import com.yfwl.yfgp.model.Picture;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.ContactService;
import com.yfwl.yfgp.service.FutureService;
import com.yfwl.yfgp.service.StockInfoService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.utils.ControllerTest;
import com.yfwl.yfgp.utils.MD5Util;
import com.yfwl.yfgp.utils.PropertiesUtils;

@Controller
@RequestMapping("/future")
public class FutureController extends BaseController {
	@Autowired
	ContactService contactService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private FutureService futureService;
	@Autowired
	private CashService cashService;
	@Autowired
	private StockInfoService stockInfoService;
	
	@RequestMapping(value="/contactus",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> putContent(HttpServletRequest request,
			HttpServletResponse response){	
		Map<String,Object> map = new HashMap<String,Object>();
		Date timeDate = new Date();
		Contact contact = new Contact();
		contact.setUserId(request.getParameter("userId"));
		contact.setContactWay(request.getParameter("contactWay"));
		contact.setInfo(request.getParameter("info"));
		contact.setTime(timeDate);
		boolean isInsertOk = contactService.putContact(contact);
		if(isInsertOk == true){	
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", "");
		}else{
			map.put("status", -4);
			map.put("msg", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	
	@RequestMapping(value="/getimage", method = {RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> getImage(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		String paramToken = request.getParameter("token");
		String userId = request.getParameter("userId");
		Integer pictureType = Integer.parseInt(request.getParameter("pictureType"));
		
		String dbToken = tokenService.selectTokenByUserId2(Integer
				.parseInt(userId));
		if(dbToken.equals(paramToken)) {
			Picture picture = new Picture();
			Picture selectResult = new Picture();
			picture.setAttachId(userId);
			picture.setPictureType(pictureType);
			selectResult = futureService.selectImage(picture);
			if(selectResult != null) {
				map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", selectResult);
			}
			else {
				map.put("status", 4);
				map.put("msg", "请求参数为空");
				map.put("data", "");
			}
		}
		else {
			map.put("status", 7);
			map.put("msg", "token错误");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value="/uploadimage", method = {RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> uploadImage(MultipartHttpServletRequest request,
			HttpServletResponse response) {
		String paramToken = request.getParameter("token");
		String userId = request.getParameter("userId");
		Integer pictureType = Integer.parseInt(request.getParameter("pictureType"));
		String dbToken = tokenService.selectTokenByUserId2(Integer
				.parseInt(userId));

		Map<String, Object> resultMap = new HashMap<String, Object>();

		
		String photoRealPath = null;
		String photoUrl = null;
		
		String ip = PropertiesUtils.getServerIpConf().getProperty("ip");
		if(ip.equals("120.76.192.128")){
			//server_01
			photoRealPath = PropertiesUtils.getProperties().getProperty("server_dynPhotoPath");
			photoUrl = 	PropertiesUtils.getProperties().getProperty("server01_dynPhotoUrl");
			
		}else if(ip.equals("120.76.128.66")){
			//server_02
			photoRealPath = PropertiesUtils.getProperties().getProperty("server_dynPhotoPath");
			photoUrl = 	PropertiesUtils.getProperties().getProperty("server02_dynPhotoUrl");
			
		}else if(ip.equals("120.24.208.86")){
			//负载均衡前的server
			photoRealPath = PropertiesUtils.getProperties().getProperty("DynContentPhotoRealPath");
			photoUrl = 	PropertiesUtils.getProperties().getProperty("DynContentPhotoUrl");
			
		}else if(ip.equals("192.168.1.103")){
			photoRealPath = PropertiesUtils.getProperties().getProperty("HeadImageRealPath");
			photoUrl = 	PropertiesUtils.getProperties().getProperty("HeadImageUrl");
			
		}else{
			resultMap.put("status", 35);
			resultMap.put("msg", "ip获取错误");
			resultMap.put("data", ip);
			return resultMap;
		}
		
		
		if (dbToken.equals(paramToken)) {
			boolean isUpdateOk = false;
			String originImage = "";
			String compressImage = "";
			MultiValueMap<String, MultipartFile> multiMap = request
					.getMultiFileMap();

			if (multiMap != null) {
				Set<String> keys = multiMap.keySet();
				for (String key : keys) {
					List<MultipartFile> multiFile = multiMap.get(key);
					for (MultipartFile file : multiFile) {
						InputStream is = null;
						FileOutputStream fos = null;
						try {
							is = file.getInputStream();
							String originalFilename = file.getOriginalFilename();
							String newFileName = UUID.randomUUID()+ originalFilename.substring(originalFilename.lastIndexOf("."));
							
							originImage = photoUrl + newFileName;

							fos = new FileOutputStream(photoRealPath + newFileName);
							compressImage = originImage;
							Picture picture = new Picture();
							picture.setAttachId(userId);
							picture.setPictureType(pictureType);
							picture.setOriginPic(originImage);
							picture.setCompressPic(compressImage);

							futureService.insertImage(picture);

							byte[] buffer = new byte[1024];
							int len = 0;
							while ((len = is.read(buffer)) != -1) {
								fos.write(buffer, 0, len);
								isUpdateOk = true;
							}
							fos.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							try {
								if (fos != null) {
									fos.close();
								}
								if (is != null) {
									is.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

				if (isUpdateOk == true) {
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put("compressImage", compressImage);
					resultMap.put("status", 0);
					resultMap.put("msg", "操作成功");
					resultMap.put("data", dataMap);
				} else {
					resultMap.put("status", 4);
					resultMap.put("msg", "服务器读写失败");
					resultMap.put("data", "");
				}
			} else {
				resultMap.put("status", 4);
				resultMap.put("msg", "请求参数为空");
				resultMap.put("data", "");
			}
		} else {
			resultMap.put("status", 7);
			resultMap.put("msg", "token错误");
			resultMap.put("data", "");
		}
		return resultMap;
	}
	
	
	@RequestMapping(value="/setidentity", method = {RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> setIdentity(HttpServletRequest request,
			HttpServletResponse response) {
		String paramToken = request.getParameter("token");
		String userId = request.getParameter("userId");
		String userName = request.getParameter("userName");
		String userIdentity =request.getParameter("userIdentity");
		String dbToken = tokenService.selectTokenByUserId2(Integer
				.parseInt(userId));

		Map<String, Object> resultMap = new HashMap<String, Object>();

		if (dbToken.equals(paramToken)) {
			Identity identity = new Identity();
			identity.setUserId(Integer.parseInt(userId));
			identity.setUserName(userName);
			identity.setUserIdentity(userIdentity);
			identity.setMsg("正在审核中");
			identity.setStatus(1);
			Integer hasInsert = futureService.updateIdentity(identity);
			if(hasInsert != 0) {
				resultMap.put("status", 0);
				resultMap.put("msg", "操作成功");
				resultMap.put("data", identity);
			} else {
				resultMap.put("status", 4);
				resultMap.put("msg", "操作失败");
				resultMap.put("data", "");
			}
		} else {
			resultMap.put("status", 7);
			resultMap.put("msg", "token错误");
			resultMap.put("data", "");
		}
		return resultMap;
	}
	
	
	@RequestMapping(value="/getidentity", method = {RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getIdentity(HttpServletRequest request,
			HttpServletResponse response) {
		String paramToken = request.getParameter("token");
		String userId = request.getParameter("userId");
		String dbToken = tokenService.selectTokenByUserId2(Integer
				.parseInt(userId));

		Map<String, Object> resultMap = new HashMap<String, Object>();

		if (dbToken.equals(paramToken)) {
			Identity identity = futureService.getIdentity(Integer.parseInt(userId));
			if(identity != null) {
				Picture p = new Picture();
				p.setAttachId(userId);
				p.setPictureType(3);
				Picture facePicture = futureService.selectImage(p);
				String faceImage = "";
				if(null != facePicture) {
					faceImage = facePicture.getCompressPic();
				}
				identity.setFaceImage(faceImage);
				
				p.setPictureType(4);
				Picture identityPicture = futureService.selectImage(p);
				String identityImage = "";
				if(null != identityPicture) {
					identityImage = identityPicture.getCompressPic();
				}
				identity.setIdentityImage(identityImage);
				
				resultMap.put("status", 0);
				resultMap.put("msg", "操作成功");
				resultMap.put("data", identity);
			} else {
				resultMap.put("status", 4);
				resultMap.put("msg", "操作失败");
				resultMap.put("data", "");
			}
		} else {
			resultMap.put("status", 7);
			resultMap.put("msg", "token错误");
			resultMap.put("data", "");
		}
		return resultMap;
	}
	
	@RequestMapping(value="/getstock", method = {RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getstock(HttpServletRequest request,
			HttpServletResponse response) {
		//System.out.println("gdskgdfkj");
		Map<String,Object> map = null;
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		List<OwnStock> stockList = futureService.getOwnStock(userId);
		String stock = "";
		String stockid="";
		List<Map<String, Object>> arr=new ArrayList<Map<String,Object>>();
		 if(!stockList.isEmpty()) {
			for (int i = 0; i < stockList.size(); i++) {
				map= new HashMap<String,Object>();
				stock = stockList.get(i).getStockCode();
				if(stock!=null&&stock!=""){
					stockid=stock.substring(0,6);
					Stockinfo stockinfo=new Stockinfo();
					stockinfo.setStockid(stockid);
					Stockinfo stockinfo2=stockInfoService.getStockinfo(stockinfo);
					if(stockinfo2==null){
						continue;
					}					
					String stock2=stockinfo2.getStockid();
					stock2 = stock2.startsWith("6")?stock2 + ".SS":stockid + ".SZ";
					map.put("bspoint", stockinfo2.getBspoint());
					map.put("stock",stock2);
					arr.add(map);
				}else{
					map=rspFormat("", INSERT_STOCK);
				}
				
			}
			   
			map=rspFormat(arr,SUCCESS);
			
		}else{
			map= new HashMap<String,Object>();
			map.put("msg", "您还没有添加自选股");
		}

		
		return map;
	}
	
	@RequestMapping(value="/insertstock", method = {RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertStock(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 用户id
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer type = 0;
		// 1合并   0覆盖
		if(null != request.getParameter("type")) {
			type = Integer.parseInt(request.getParameter("type"));
		}
		Set<String> set = new HashSet<String>();
		if(type.equals(0)) {
			
		}
		else if(type.equals(1)) {
			List<OwnStock> stockList = futureService.getOwnStock(userId);
			for(OwnStock ownStock:stockList) {
				set.add(ownStock.getStockCode());
			}
		}
		
		// 代表一系列股票的字符串
		String codeStr = request.getParameter("codeStr");
		String[] codeArray = codeStr.split(",");
		OwnStock stock = new OwnStock();
		stock.setUserId(userId);
		stock.setStatus(0);
		futureService.deleteAllStock(userId);
		for(int i = 0; i < codeArray.length; i++) {
			if(codeArray[i].length()!=9){
				continue;
			}
			set.add(codeArray[i]);
		}
		Iterator it=set.iterator();
		while(it.hasNext())
		{
			Object o=it.next();
			stock.setStockCode(o.toString());
			futureService.insertOwnStock(stock);
		}

		map.put("status", 0);
		map.put("msg", "操作成功");
		map.put("data", "");
		
		return map;
	}
	
	@RequestMapping(value="/insertstock2", method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> insertStock2(HttpServletRequest request,
			HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		// 用户id
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		// 股票代码
		String stockCode = request.getParameter("stockCode");
		if(stockCode.length()<9||stockCode.equals("")||stockCode==null){
			map.put("msg", "不合法的股票代码");
			map.put("data", "");
			map.put("status", 1);
			return map;
		}
		List<String> list = new ArrayList<String>();
		List<OwnStock> stockList = futureService.getOwnStock(userId);
		for(OwnStock ownStock:stockList) {
			list.add(ownStock.getStockCode());
		}
		// 判断是否添加股票的信号灯  1：添加这支股票      0：不添加这支股票
		int flag = 1;
		for(String item:list){
			if(stockCode.equals(item)){// 数据库中有这支股票
				flag = 0;
				map=rspFormat("", FAIL2);
				break;
			}
		}
		if(flag == 1){
			// 添加这支股票
			OwnStock stock = new OwnStock();
			stock.setUserId(userId);
			stock.setStockCode(stockCode);
			stock.setStatus(0);
			futureService.insertOwnStock(stock);
			map=rspFormat("", SUCCESS);
		}
		return map;
	}
	
	@RequestMapping(value="/deletestock2", method = {RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> deleteStock(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = new HashMap<String,Object>();
		// 用户id
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		// 股票代码
		String stockCode = request.getParameter("stockCode");
		OwnStock stock = new OwnStock();
		stock.setUserId(userId);
		stock.setStockCode(stockCode);
		// 根据用户id和股票代码删除指定股票
		futureService.deleteOwnStock(stock);
		map.put("status", 0);
		map.put("msg", "操作成功");
		map.put("data", "");
		return map;
	}
	
	@RequestMapping(value="/deletestock", method = {RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateStock(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String codeStr = request.getParameter("codeStr");
		String[] codeArray = codeStr.split(",");
		OwnStock stock = new OwnStock();
		stock.setUserId(userId);
		stock.setStatus(0);
		for(int i = 0; i < codeArray.length; i++) {
			stock.setStockCode(codeArray[i]);
			futureService.deleteOwnStock(stock);
		}
		map.put("status", 0);
		map.put("msg", "操作成功");
		map.put("data", "");
		
		return map;
	}
	
	@RequestMapping(value="/getInvitationCode",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getInvitationCode(HttpServletRequest request,
			HttpServletResponse response){	
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Invitation invitation = new Invitation();
		invitation.setUserId(userId);
		Invitation resultInvitation = futureService.getInvitation(invitation);
		if(null != resultInvitation) {
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", resultInvitation);
		}
		else {
			//String invitationCode = RandomStringUtil.getInvitationCode(userId);
			String invitationCode = userId.toString() + (int)(Math.random() * 10);
			Integer status = 0;
			invitation.setInvitationCode(invitationCode);
			invitation.setStatus(status);
			Integer hasInsert = futureService.initInvitation(invitation);
			if(hasInsert > 0) {
				map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", invitation);
			}
			else {
				map.put("status", 4);
				map.put("msg", "操作失败");
				map.put("data", "");
			}
		}
		
		return map;
	}
	
	@SuppressWarnings("null")
	@RequestMapping(value="/fillInvitationCode",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> fillInvitationCode(HttpServletRequest request,
			HttpServletResponse response){	
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String fillCode = request.getParameter("fillCode");
		String paramToken = request.getParameter("token");
		String dbToken = tokenService.selectTokenByUserId2(userId);
		if(dbToken.equals(paramToken)) {
			Invitation u = new Invitation();
			u.setUserId(userId);
			Invitation invitation = futureService.getInvitation(u);
			if(null == invitation) {
				invitation = new Invitation();
				String invitationCode = userId.toString() + (int)(Math.random() * 10);
				Integer status = 0;
				invitation.setUserId(userId);
				invitation.setInvitationCode(invitationCode);
				invitation.setStatus(status);
				Integer hasInsert = futureService.initInvitation(invitation);
			}
			
			if(invitation.getStatus() ==1) {
				map.put("status", 5);
				map.put("msg", "已被邀请");
				map.put("data", "");
			}
			else {
				Invitation i = new Invitation();
				i.setInvitationCode(fillCode);
				Invitation targetInvitation = futureService.getInvitation(i);
				if(null != targetInvitation && !targetInvitation.getUserId().equals(invitation.getUserId())) {
					Date fillTime = new Date();
					Integer status = 1;
					invitation.setFillTime(fillTime);
					invitation.setFillCode(fillCode);
					invitation.setStatus(status);
					Integer hasUpdate = futureService.updateInvitation(invitation);
					if(hasUpdate == 1) {
						Date date = new Date();
					    Calendar c = Calendar.getInstance();
					    c.setTime(date);
					    c.add(Calendar.YEAR, 1);
					    Date expireTime = c.getTime();
						Coupon coupon = new Coupon();
						coupon.setUserId(userId);
						coupon.setCouponValue(80);
						coupon.setExpireTime(expireTime);
						coupon.setStatus(0);
						futureService.initCoupon(coupon);
						coupon.setUserId(targetInvitation.getUserId());
						futureService.initCoupon(coupon);
						targetInvitation.setInviteCount(targetInvitation.getInviteCount() + 1);
						futureService.updateInvitation(targetInvitation);
						map.put("status", 0);
						map.put("msg", "操作成功");
						map.put("data", invitation);
					}
					else {
						map.put("status", 4);
						map.put("msg", "操作失败");
						map.put("data", "");
					}
					
				}
				else {
					map.put("status", 6);
					map.put("msg", "无效邀请码");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 7);
			map.put("msg", "token错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	

	@RequestMapping(value="/getCoupon",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getCoupon(HttpServletRequest request,
			HttpServletResponse response){	
		
		response.addHeader("Access-Control-Allow-origin","*");
		response.addHeader("Access-Control-Allow-Methods","GET,POST");
		
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Coupon coupon = new Coupon();
		coupon.setUserId(userId);
		coupon.setStatus(0);
		List<Coupon>  couponList = futureService.getCoupon(coupon);
		if(null != couponList && couponList.size() > 0) {
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", couponList);
		}
		else {
			map.put("status", 2);
			map.put("msg", "操作成功");
			map.put("data", couponList);
		}
		return map;
	}
	
	@RequestMapping(value="/useCoupon",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> useCoupon(HttpServletRequest request,
			HttpServletResponse response){	
		Map<String,Object> map = new HashMap<String,Object>();
		String couponIdStr = request.getParameter("couponIdStr");
		String[] couponIdStrArr = couponIdStr.split(",");
		List<String> couponIdList = java.util.Arrays.asList(couponIdStrArr);
		String consumeInfo = request.getParameter("consumeInfo");
		Integer consumeType = Integer.parseInt(request.getParameter("consumeType"));
		Date useTime = new Date();
		if (couponIdList.size() < 0) {
			map.put("status", 4);
			map.put("msg", "参数错误");
			map.put("data", "");
		}
		else {
			Coupon coupon = new Coupon();
			coupon.setUseTime(useTime);
			coupon.setConsumeInfo(consumeInfo);
			coupon.setConsumeType(consumeType);
			coupon.setStatus(1);
			for(int i = 0; i < couponIdList.size(); i ++) {
				coupon.setId(Integer.parseInt(couponIdList.get(i)));
				futureService.updateCoupon(coupon);
			}
			map.put("status", 0);
			map.put("msg", "操作成功");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value="/updateMem",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateMem(HttpServletRequest request,
			HttpServletResponse response){	
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer monthAdd = Integer.parseInt(request.getParameter("monthAdd"));
		Mem mem = new Mem();
		mem.setUserId(userId);
		Mem userMem = futureService.getMem(mem);
		userMem = futureService.getMem(mem);
		Date currentDate = new Date();
		Date consumeTime;
		Date expireTime = userMem.getExpireTime();
		if(expireTime.getTime() > currentDate.getTime()) {
			Calendar c = Calendar.getInstance();
		    c.setTime(expireTime);
		    c.add(Calendar.MONTH, monthAdd);
		    expireTime = c.getTime();
		    userMem.setExpireTime(expireTime);
		    Integer hasUpdate = futureService.updateMem(userMem);
		    if(hasUpdate >0) {
		    	map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", userMem);
		    }
		    else {
				map.put("status", 4);
				map.put("msg", "操作失败");
				map.put("data", "");
		    }
		}
		else {
			consumeTime = currentDate;
			Calendar c = Calendar.getInstance();
		    c.setTime(consumeTime);
		    c.add(Calendar.MONTH, monthAdd);
		    expireTime = c.getTime();
		    userMem.setConsumeTime(consumeTime);
		    userMem.setExpireTime(expireTime);
		    Integer hasUpdate = futureService.updateMem(userMem);
		    if(hasUpdate >0) {
		    	map.put("status", 0);
				map.put("msg", "操作成功");
				map.put("data", userMem);
		    }
		    else {
				map.put("status", 4);
				map.put("msg", "操作失败");
				map.put("data", "");
		    }
		}
		
		return map;
	}
	
	
	
	@RequestMapping(value="/useCouponToUser",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> useCouponToUser(HttpServletRequest request,
			HttpServletResponse response){	
		Map<String,Object> map = new HashMap<String,Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String password = request.getParameter("password");
		Integer id = Integer.parseInt(request.getParameter("id"));
		String couponIdStr = request.getParameter("couponIdStr");
		String[] couponIdStrArr = couponIdStr.split(",");
		List<String> couponIdList = java.util.Arrays.asList(couponIdStrArr);
		String consumeInfo = request.getParameter("rewardUserId");
		Integer consumeType = Integer.parseInt(request.getParameter("consumeType"));
		Date useTime = new Date();
		CashAccount userAccount = cashService.getCashAccountByUserId(userId);
		if(userAccount.getPassword().equals(MD5Util.string2MD5(password))) {
			if (couponIdList.size() < 0) {
				map.put("status", 4);
				map.put("msg", "参数错误");
				map.put("data", "");
			}
			else {
				Coupon coupon = new Coupon();
				coupon.setUseTime(useTime);
				coupon.setConsumeInfo(consumeInfo);
				coupon.setConsumeType(consumeType);
				coupon.setStatus(1);
				
				Date date = new Date();
			    Calendar c = Calendar.getInstance();
			    c.setTime(date);
			    c.add(Calendar.YEAR, 1);
			    Date expireTime = c.getTime();
				Coupon couponNew = new Coupon();
				couponNew.setUserId(Integer.parseInt(consumeInfo));
				couponNew.setStatus(0);
				//couponNew.setExpireTime(expireTime);
				Order order = cashService.getOrderById(id);
				order.setTimeExpire(expireTime);
				order.setFeeLeft(0);
				order.setStatus(0);
				Integer hasInsertRewardUserOrder = cashService.updateOrder(order);
				if(hasInsertRewardUserOrder > 0) {
					for(int i = 0; i < couponIdList.size(); i ++) {
						coupon.setId(Integer.parseInt(couponIdList.get(i)));
						futureService.updateCoupon(coupon);
						
						Coupon result  = futureService.getCouponById(Integer.parseInt(couponIdList.get(i)));
						couponNew.setConsumeInfo(couponIdList.get(i));
						couponNew.setCouponValue(result.getCouponValue());
						couponNew.setExpireTime(result.getExpireTime());
						futureService.initCoupon(couponNew);
						
					}
					map.put("status", 0);
					map.put("msg", "操作成功");
					map.put("data", "");
				}
				else {
					map.put("status", 4);
					map.put("msg", "参数错误");
					map.put("data", "");
				}
			}
		}
		else {
			map.put("status", 8);
			map.put("msg", "密码错误");
			map.put("data", "");
		}
		return map;
	}
	
//	@RequestMapping(value="/getstock2", method = {RequestMethod.POST })
//	@ResponseBody
//	public Map<String, Object> getstock2(HttpServletRequest request,
//			HttpServletResponse response) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		String codeStr = request.getParameter("codeStr");
//		String[] codeArray = codeStr.split(",");
//		List<Map<String, String>> arr=new ArrayList<Map<String,String>>();
//		for(int i = 0; i < codeArray.length; i++) {
//			HashMap<String, String> map2=new HashMap<String, String>();
//			String stockid=codeArray[i].substring(0, 6);
//			Integer bspoint=null;
//			try {
//				Stockinfo stockinfo=stockInfoService.getStock(stockid);
//				bspoint= stockinfo.getBspoint();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			if(bspoint==null||bspoint.equals("")){
//				continue;
//			}else{
//				stockid=stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
//				map2.put("bspoint", bspoint.toString());
//				map2.put("stock", stockid);
//				arr.add(map2);
//				map=rspFormat(arr, SUCCESS);
//			}
//			
//		}
//		
//		return map;
//	}
	@RequestMapping(value="/getstock2", method = {RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getstock2(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		String codeStr = request.getParameter("codeStr");
		String[] codeArray = codeStr.split(",");
		List<Map<String, String>> arr=new ArrayList<Map<String,String>>();
		for(int i = 0; i < codeArray.length; i++) {
			HashMap<String, String> map2=new HashMap<String, String>();
			String stockid=codeArray[i].substring(0, 6);
			if(!stockid.equals("399001")&&!stockid.equals("399006")&&!stockid.equals("000001")){
				Integer bspoint=null;
				try {
					Stockinfo stockinfo=stockInfoService.getStock(stockid);
					bspoint= stockinfo.getBspoint();
				} catch (Exception e) {
					// TODO: handle exception
				}
				if(bspoint==null||bspoint.equals("")){
					continue;
				}else{
					stockid=stockid.startsWith("6")?stockid + ".SS":stockid + ".SZ";
					map2.put("bspoint", bspoint.toString());
					map2.put("stock", stockid);
					arr.add(map2);
					map=rspFormat(arr, SUCCESS);
				}
			}else{
				try {
					Integer bspoint=null;
					stockid=codeArray[i].substring(0, 9);
					String url = "http://guge007.cn:8081/quote/v1/bs";
					HashMap<String, String> map3 = new HashMap<String, String>();
					map3.put("get_type", "offset");
					map3.put("prod_code", stockid);
					map3.put("candle_period", "6");
					String haha = ControllerTest.doGet(url, map3);
					JSONObject json=new JSONObject(haha);
					JSONObject candle =json.getJSONObject("data").getJSONObject("candle");
					JSONArray array=candle.getJSONArray(stockid);
					if(!(array.length()>0)){
						bspoint=1;
						//date=Integer.parseInt(dateString);
					}
					else{
						 Object x=array.get(array.length()-1);
						 bspoint=Integer.parseInt(x.toString().substring(10,11));
						// date=Integer.parseInt(x.toString().substring(1,9));
					}
					map2.put("bspoint", bspoint.toString());
					map2.put("stock", stockid);
					arr.add(map2);
					map=rspFormat(arr, SUCCESS);
				} catch (Exception e) {
					map.put("msg", "上传代码错误");
					map.put("data", "");
					map.put("status", "1");
					// TODO: handle exception
				}
				
			}
			
			
		}
		
		return map;
	}
}
