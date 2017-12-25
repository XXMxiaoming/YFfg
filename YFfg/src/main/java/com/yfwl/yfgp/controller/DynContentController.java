package com.yfwl.yfgp.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tencent.xinge.XingeApp;
import com.yfwl.yfgp.model.Accounts2;
import com.yfwl.yfgp.model.DynContent;
import com.yfwl.yfgp.model.DynContentPhoto;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.Picture;
import com.yfwl.yfgp.model.Praise;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.AccountsService2;
import com.yfwl.yfgp.service.ArticleService;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.DynContentCommentService;
import com.yfwl.yfgp.service.DynContentPhotoService;
import com.yfwl.yfgp.service.DynContentService;
import com.yfwl.yfgp.service.FutureService;
import com.yfwl.yfgp.service.PraiseService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.utils.ImageUtil;
import com.yfwl.yfgp.utils.PropertiesUtils;
import com.yfwl.yfgp.utils.Time;

@Controller
@RequestMapping("/dyncontent")
public class DynContentController extends BaseController {

	@Autowired
	private DynContentService dynContentService;
	@Autowired
	private DynContentCommentService dynContentCommentService;
	@Autowired
	private DynContentPhotoService dynContentPhotoService;
	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ArticleService articleService;
	@Autowired
	private FutureService futureService;
	@Autowired
	private PraiseService praiseService;
	@Autowired
	private AccountsService2 accountsService2;
	@Autowired
	private CashService cashService;

	@RequestMapping(value = "/test", method = { RequestMethod.GET })
	@ResponseBody
	public Map<String, Object> test(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<DynContent> list = dynContentService.selectNewFriendDynContent(10044, 0,10);
		map.put("test", list);
		return map;
	}
	/**
	 * 根据user_id查找相应的好友动态，按时间顺序排好
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectFriendContentList", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectFriendContentList(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();

		String paramToken = request.getParameter("token");
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;

		if (userId != null && pageNow != null && paramToken != null) {
			String dbToken = tokenService.selectTokenByUserId2(userId);
			if (true||dbToken.equals(paramToken) ) {
				List<DynContent> listOfFriendContent = dynContentService
						.selectFriendDynContent(userId, pageCount);
				if (listOfFriendContent.size() > 0) {

					for (int i = 0; i < listOfFriendContent.size(); i++) {

						DynContent dynContent = listOfFriendContent.get(i);

						// 组装user
						Integer friendUserId = dynContent.getUserId();
						User friend = userService.selectUserById(friendUserId);
						dynContent.setUser(friend);

						// 组装photolist
						Integer conId = dynContent.getConId();
						List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService
								.selectDynContentPhoto(conId);
						if (listOfPhotoAddress.size() != 0) {
							dynContent.setPhotoList(listOfPhotoAddress);
						}

						// 设置点赞数
						Praise praise = new Praise();
						praise.setPraiseContent(conId);
						praise.setContentType(0);
						praise.setUserId(userId);
						Integer goodCount = praiseService.selectCountPraise(praise);
						dynContent.setGoodCount(goodCount);

						Praise userPraise = praiseService.selectPraise(praise);
						dynContent.setPraise(userPraise);

						Integer parentId = dynContent.getParentId();
						Integer conType = dynContent.getConType();
						if (null != parentId) {
							if (conType == 10) {
								dynContent.setArticle(articleService.getArticle(parentId));
										
							} else if(conType == 11){
								dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
							} else {
								
								DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
								if (parentContent != null) {
									
									Integer userId2 = parentContent.getUserId();
									if (userId2 != null) {
										parentContent.setUser(userService.selectUserById(userId2));
										dynContent.setParentUser(userService.selectUserById(userId2));
									}
									Integer conId2 = parentContent.getConId();
									if(conId2 != null){
										List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
										if (listOfPhotoAddress2.size() > 0) {
											parentContent.setPhotoList(listOfPhotoAddress2);
										}
									}
									
									dynContent.setDynContent(parentContent);
								}
							}
						}
					}
					map.put("status", 0);
					map.put("message", "操作成功");
					map.put("data", listOfFriendContent);
					System.out.println("===");
				} else {
					map.put("status", 5);
					map.put("message", "操作成功，但用户的好友动态为空");
					map.put("data", listOfFriendContent);
				}
			} else {
				map.put("status", 7);
				map.put("message", "token错误");
				map.put("data", "");
			}
		} else {
			map.put("status", 4);
			map.put("message", "参数有误");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 点赞数加1或减1
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/operateGoodCount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> operateGoodCount(HttpServletRequest request,
			HttpServletResponse response) {
		Integer contentId = Integer.parseInt(request.getParameter("contentId"));
		boolean flag = Boolean.parseBoolean(request.getParameter("flag"));

		if (flag == true) {
			dynContentService.updateGoodCount(contentId);
		} else {
			dynContentService.cutGoodCount(contentId);
		}
		Integer goodCount = dynContentService.selectGoodCount(contentId);
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("goodCount", goodCount);
		map.put("status", 0);
		map.put("message", "操作成功");
		map.put("data", dataMap);
		return map;
	}

	/**
	 * 评论数加1
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/updateComCount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateComCount(HttpServletRequest request,
			HttpServletResponse response) {
		Integer contentId = Integer.parseInt(request.getParameter("contentId"));
		boolean isOk = dynContentService.updateComCount(contentId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("return", isOk);
		return map;
	}

	/**
	 * 根据自己的user_id查自己的动态，按时间顺序排好
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectOwnContent", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectOwnContent(HttpServletRequest request,
			HttpServletResponse response) {
		String paramToken = request.getParameter("token");
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String dbToken = tokenService.selectTokenByUserId2(userId);

		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;

		Map<String, Object> map = new HashMap<String, Object>();

		if (true||dbToken.equals(paramToken)) {
			List<DynContent> listOfDyncontent = dynContentService.selectOwnContent(userId, pageCount);
					
			for (int i = 0; i < listOfDyncontent.size(); i++) {
				
				DynContent dynContent = listOfDyncontent.get(i);
				
				// 组装user
				Integer friendUserId = dynContent.getUserId();
				User friend = userService.selectUserById(friendUserId);
				dynContent.setUser(friend);
				
				Integer parentId = dynContent.getParentId();
				Integer conType = dynContent.getConType();
				
				// 组装photolist
				Integer conId = dynContent.getConId();
				List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
				if (listOfPhotoAddress.size() > 0) {
					dynContent.setPhotoList(listOfPhotoAddress);
				}
				
				Praise praise = new Praise();
				praise.setPraiseContent(conId);
				praise.setContentType(0);
				praise.setUserId(userId);
				Integer count = praiseService.selectCountPraise(praise);
				dynContent.setGoodCount(count);
				Praise userPraise = praiseService.selectPraise(praise);
				dynContent.setPraise(userPraise);
				
				if (null != parentId) {
					if (conType == 10) {
						dynContent.setArticle(articleService.getArticle(parentId));
								
					} else if(conType == 11){
						dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
					} else {
						
						DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
						if (parentContent != null) {
							
							Integer userId2 = parentContent.getUserId();
							if (userId2 != null) {
								parentContent.setUser(userService.selectUserById(userId2));
								dynContent.setParentUser(userService.selectUserById(userId2));
							}
							Integer conId2 = parentContent.getConId();
							if(conId2 != null){
								List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
								if (listOfPhotoAddress2.size() > 0) {
									parentContent.setPhotoList(listOfPhotoAddress2);
								}
							}
							
							dynContent.setDynContent(parentContent);
						}
					}
				}
			}
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", listOfDyncontent);

		} else {
			map.put("status", 7);
			map.put("message", "token错误");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 根据自己的user_id查别人的动态，按时间顺序排好
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectOtherContent", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectOtherContent(HttpServletRequest request,
			HttpServletResponse response) {

		Integer userId = Integer.parseInt(request.getParameter("userId"));

		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;

		Map<String, Object> map = new HashMap<String, Object>();

		List<DynContent> listOfDyncontent = dynContentService.selectOwnContent(userId, pageCount);
				
		for (int i = 0; i < listOfDyncontent.size(); i++) {
			
			DynContent dynContent = listOfDyncontent.get(i);
			
			// 组装user
			Integer friendUserId = dynContent.getUserId();
			User friend = userService.selectUserById(friendUserId);
			dynContent.setUser(friend);
			
			Integer parentId = listOfDyncontent.get(i).getParentId();
			Integer conType = listOfDyncontent.get(i).getConType();
			
			// 组装photolist
			Integer conId = dynContent.getConId();
			List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
			if (listOfPhotoAddress.size() > 0) {
				dynContent.setPhotoList(listOfPhotoAddress);
			}
			
			Praise praise = new Praise();
			praise.setPraiseContent(conId);
			praise.setContentType(0);
			praise.setUserId(userId);
			Integer count = praiseService.selectCountPraise(praise);
			listOfDyncontent.get(i).setGoodCount(count);
			Praise userPraise = praiseService.selectPraise(praise);
			dynContent.setPraise(userPraise);
			
			if (null != parentId) {
				if (conType == 10) {
					dynContent.setArticle(articleService.getArticle(parentId));
							
				} else if(conType == 11){
					dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
				} else {
					
					DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
					if (parentContent != null) {
						
						Integer userId2 = parentContent.getUserId();
						if (userId2 != null) {
							parentContent.setUser(userService.selectUserById(userId2));
							dynContent.setParentUser(userService.selectUserById(userId2));
						}
						Integer conId2 = parentContent.getConId();
						if(conId2 != null){
							List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
							if (listOfPhotoAddress2.size() > 0) {
								parentContent.setPhotoList(listOfPhotoAddress2);
							}
						}
						
						dynContent.setDynContent(parentContent);
					}
				}
			}
		}
		map.put("status", 0);
		map.put("message", "操作成功");
		map.put("data", listOfDyncontent);

		return map;
	}
	
	
	/**
	 * 根据userName查找动态
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectDynContentByUserName", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectDynContentByUserName(
			HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter("userName");
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;

		Map<String, Object> map = new HashMap<String, Object>();

		if (userName == null || userName == "" || pageCount == null) {
			map.put("status", -4);
			map.put("message", "参数错误");
			map.put("data", "");
		} else {
			// Map<String, Object> dataMap = new HashMap<String, Object>();
			List<DynContent> listOfDyncontent = dynContentService.selectDynContentByUserName(userName, pageCount);
					
			for (int i = 0; i < listOfDyncontent.size(); i++) {
				
				DynContent dynContent = listOfDyncontent.get(i);
				
				// 组装user
				Integer friendUserId = dynContent.getUserId();
				User friend = userService.selectUserById(friendUserId);
				dynContent.setUser(friend);
				
				Integer parentId = dynContent.getParentId();
				Integer conType = dynContent.getConType();
				
				// 组装photolist
				Integer conId = dynContent.getConId();
				List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
				if (listOfPhotoAddress.size() != 0) {
					dynContent.setPhotoList(listOfPhotoAddress);
				}
				
				if (null != parentId) {
					if (conType == 10) {
						dynContent.setArticle(articleService.getArticle(parentId));
								
					} else if(conType == 11){
						dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
					} else {
						
						DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
						if (parentContent != null) {
							
							Integer userId2 = parentContent.getUserId();
							if (userId2 != null) {
								parentContent.setUser(userService.selectUserById(userId2));
								dynContent.setParentUser(userService.selectUserById(userId2));
							}
							Integer conId2 = parentContent.getConId();
							if(conId2 != null){
								List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
								if (listOfPhotoAddress2.size() > 0) {
									parentContent.setPhotoList(listOfPhotoAddress2);
								}
							}
							
							dynContent.setDynContent(parentContent);
						}
					}
				}
			}
			// dataMap.put("dynContentList", list);

			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", listOfDyncontent);
		}
		return map;
	}

	/**
	 * 删除自己的某条动态
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/deleteOwnContent", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> deleteOwnContent(HttpServletRequest request,
			HttpServletResponse response) {
		Integer contentId = Integer.parseInt(request.getParameter("contentId"));

		Map<String, Object> map = new HashMap<String, Object>();
		boolean isDeleteConOk = dynContentService.deleteOwnContent(contentId);
		
		if (isDeleteConOk == true) {
			List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(contentId);
			Integer count = dynContentCommentService.selectCountOfComment(contentId);
			
			if(listOfPhotoAddress.size() > 0 && count >0){
				boolean isDeletePhotoOk = dynContentService.deleteContentPhoto(contentId);
				boolean isDeleteCommentOk = dynContentService.deleteContentComment(contentId);
				if(isDeletePhotoOk == true && isDeleteCommentOk == true){
					map.put("status", 0);
					map.put("message", "操作成功");
					map.put("data", "");
				}else{
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
				return map;
			}
			
			if(listOfPhotoAddress.size() > 0 && count == 0){
				boolean isDeletePhotoOk = dynContentService.deleteContentPhoto(contentId);
				if(isDeletePhotoOk == true){
					map.put("status", 0);
					map.put("message", "操作成功");
					map.put("data", "");
				}else{
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
				return map;
			}
			
			if(listOfPhotoAddress.size() == 0 && count > 0){
				boolean isDeleteCommentOk = dynContentService.deleteContentComment(contentId);
				if(isDeleteCommentOk == true){
					map.put("status", 0);
					map.put("message", "操作成功");
					map.put("data", "");
				}else{
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
				return map;
			}
			
			if(listOfPhotoAddress.size() == 0 && count == 0){
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", "");
				
				return map;
			}
		}else{
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 转发数加1
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/updateForwordCount", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateForwordCount(HttpServletRequest request,
			HttpServletResponse response) {
		Integer contentId = Integer.parseInt(request.getParameter("contentId"));
		boolean isOk = dynContentService.updateForwordCount(contentId);
		Map<String, Object> map = new HashMap<String, Object>();
		if (isOk == true) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", "");
		} else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 新增一条状态(纯文字)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/addDynContentOnlyWord", method = { RequestMethod.POST })
	@ResponseBody
	// @SensitivewordInterceptorAnnotation
	public Map<String, Object> addDynContentOnlyWord(
			HttpServletRequest request, HttpServletResponse response) {
		// 获取request中的参数
		String paramToken = request.getParameter("token");
		// 经过拦截器过滤里面的敏感词

		// String conWord = (String) request.getAttribute("conWord");

		String conWord = request.getParameter("conWord");

		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String dbToken = tokenService.selectTokenByUserId2(userId);
		String conTypeString = request.getParameter("conType");
		Integer status = 0;
		if(null != request.getParameter("status")) {
			status = Integer.parseInt(request.getParameter("status"));
		}
		int conType = 0;
		if (null != conTypeString) {
			conType = Integer.parseInt(conTypeString);
		}

		DynContent dynContent = new DynContent();

		dynContent.setConWord(conWord);
		dynContent.setUserId(userId);
		Date publishTime = new Date();
		dynContent.setPublishTime(publishTime);
		dynContent.setConType(conType);
		dynContent.setStatus(status);
		Map<String, Object> map = new HashMap<String, Object>();

		if (dbToken.equals(paramToken)) {
			dynContentService.addDynContentOnlyWord(dynContent);

			/*
			 * // 获取插入后conid的值
			 * 
			 * // 插入图片表 DynContentPhoto dynContentPhoto = new DynContentPhoto();
			 * dynContentPhoto.setConId(conId);
			 * 
			 * dynContentPhotoService.onlyWordAdd2DynPhoto(dynContentPhoto);
			 * Integer photoId = dynContentPhoto.getPhotoId();
			 */

			Integer conId = dynContent.getConId();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("conId", conId);
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", dataMap);
			
			
			User commentUser = userService.selectUserById(userId);
			String content = commentUser.getUserName() + "@您一条消息" ;
			Map<String,Object> custom = new HashMap<String,Object>();
			custom.put("id", conId);
			custom.put("userName", commentUser.getUserName());
			custom.put("headImage", commentUser.getHeadImage());
			custom.put("type", 3);
			custom.put("commentId", "");
			
			Pattern pattern = Pattern.compile("@(.*?)\\s");//编译正则
			Matcher matcher = pattern.matcher(conWord);
			List<String> strs = new ArrayList<String>();
		    while (matcher.find()) {
		    	String m = matcher.group(1);
		    	//System.out.println(m);
		    	User mUser = userService.selectUserByUsername(m);
		    	if(null != mUser) {
		    		strs.add(mUser.getUserId().toString());
		    	}
		    }
		    content = commentUser.getUserName() + "@了您:";
		    XingeApp.pushAccountListAndroidMessage(2100170039L, "42a5e3e650f7a0bb05cb18b1f8992d6b", "@消息", content, strs, custom);
		    XingeApp.pushAccountListIosMessage(2200170079L, "4f6b915bdfcb9bdba09de7a30c967faf", content, strs, XingeApp.IOSENV_DEV, custom);
		} else {
			map.put("status", 7);
			map.put("message", "token错误");
			map.put("data", "");
		}
		return map;
	}

	/**
	 * 新增一条带图片的状态
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnknownHostException 
	 */
	@SuppressWarnings("null")
	@RequestMapping(value = "/addDynContent", method = { RequestMethod.POST })
	@ResponseBody
	// @SensitivewordInterceptorAnnotation
	public Map<String, Object> addDynContent(
			MultipartHttpServletRequest request, HttpServletResponse response) throws UnknownHostException {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
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
			map.put("status", 35);
			map.put("message", "ip获取错误");
			map.put("data", ip);
			return map;
		}
		
		
		boolean isAddOk = false;
		String paramToken = request.getParameter("token");
		// String conWord = (String) request.getAttribute("conWord");
		String conWord = request.getParameter("conWord");
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String dbToken = tokenService.selectTokenByUserId2(userId);
		String conTypeString = request.getParameter("conType");
		Integer status = 0;
		if(null != request.getParameter("status")) {
			status = Integer.parseInt(request.getParameter("status"));
		}
		int conType = 0;
		if (null != conTypeString) {
			conType = Integer.parseInt(conTypeString);
		}

		DynContent dynContent = new DynContent();
		dynContent.setConWord(conWord);
		dynContent.setUserId(userId);
		Date publishTime = new Date();
		dynContent.setPublishTime(publishTime);
		dynContent.setConType(conType);
		dynContent.setStatus(status);

		List<String> fileTypes = new ArrayList<String>();  
        fileTypes.add("jpg");  
        fileTypes.add("jpeg");  
        fileTypes.add("png");
		
		if (dbToken.equals(paramToken)) {
			dynContentService.addDynContentOnlyWord(dynContent);
			// 获取插入后conid的值
			Integer conId = dynContent.getConId();

			MultiValueMap<String, MultipartFile> multiMap = request
					.getMultiFileMap();
			Set<String> keys = multiMap.keySet();
			for (String key : keys) {
				List<MultipartFile> multiFile = multiMap.get(key);
				for (MultipartFile file : multiFile) {
					InputStream is = null;
					FileOutputStream fos = null;
					try {
						is = file.getInputStream();
						String originalFilename = file.getOriginalFilename();
						//获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名  
						String ext = originalFilename.substring(originalFilename.lastIndexOf(".")+1,originalFilename.length());  
						//对扩展名进行小写转换  
				        ext = ext.toLowerCase();
				        if(fileTypes.contains(ext)){
				        	//原图
							String newFileName = UUID.randomUUID()+ originalFilename.substring(originalFilename.lastIndexOf("."));
							String photoAddress = photoUrl + newFileName;
							
							//压缩图
							String comFileName = "ex" + newFileName;
							String comPhotoAddress = photoUrl + comFileName;

							DynContentPhoto dynContentPhoto = new DynContentPhoto();
							dynContentPhoto.setConId(conId);
							dynContentPhoto.setPhotoAddress(comPhotoAddress);
							dynContentPhotoService.insertPhotoAddress(dynContentPhoto);
							
							Picture picture = new Picture();
							picture.setAttachId(conId + "");
							picture.setCompressPic(comPhotoAddress);
							picture.setOriginPic(photoAddress);
							picture.setPictureType(10);
							futureService.insertImage(picture);

							fos = new FileOutputStream(photoRealPath + newFileName);

							byte[] buffer = new byte[1024];
							int len = 0;
							while ((len = is.read(buffer)) != -1) {
								fos.write(buffer, 0, len);
								isAddOk = true;
							}
							fos.flush();
							try {
								ImageUtil imageUtil = new ImageUtil();
								imageUtil.saveImage(photoRealPath + newFileName, photoRealPath+ comFileName, 3);
							} catch (Exception e) {
								e.printStackTrace();
							}
				        }else{
				        	fos.close();
				        	is.close();
				        	map.put("status", 4);
				        	map.put("message", "图片格式不对");
				        	map.put("data", "");
							return map;
				        }
						
						
						
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

			if (isAddOk == true) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("conId", conId);
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", dataMap);
				
				User commentUser = userService.selectUserById(userId);
				String content = commentUser.getUserName() + "@您一条消息" ;
				Map<String,Object> custom = new HashMap<String,Object>();
				custom.put("id", conId);
				custom.put("userName", commentUser.getUserName());
				custom.put("headImage", commentUser.getHeadImage());
				custom.put("type", 3);
				custom.put("commentId", "");
				
				Pattern pattern = Pattern.compile("@(.*?)\\s");//编译正则
				Matcher matcher = pattern.matcher(conWord);
				List<String> strs = new ArrayList<String>();
			    while (matcher.find()) {
			    	String m = matcher.group(1);
			    	//System.out.println(m);
			    	User mUser = userService.selectUserByUsername(m);
			    	if(null != mUser) {
			    		strs.add(mUser.getUserId().toString());
			    	}
			    }
			    content = commentUser.getUserName() + "@了您:";
			    XingeApp.pushAccountListAndroidMessage(2100170039L, "42a5e3e650f7a0bb05cb18b1f8992d6b", "@消息", content, strs, custom);
			} 
		} else {
			map.put("status", 7);
			map.put("message", "token错误");
			map.put("data", "");
		}
		return map;
	}


	@RequestMapping(value = "/insertDynContent", method = { RequestMethod.POST })
	@ResponseBody
	// @SensitivewordInterceptorAnnotation
	public Map<String, Object> insertDynContent(HttpServletRequest request,
			HttpServletResponse response) {
		// 获取request中的参数
		Map<String, Object> map = new HashMap<String, Object>();
		//String paramToken = request.getParameter("token");
		Integer conId = Integer.parseInt(request.getParameter("conId"));
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer conType = Integer.parseInt(request.getParameter("conType"));
		Integer status = 0;
		if(null != request.getParameter("status")) {
			status = Integer.parseInt(request.getParameter("status"));
		}

		// String conWord = (String) request.getAttribute("conWord");

		String conWord = request.getParameter("conWord");

		DynContent dynContent = new DynContent();
		
		dynContent.setStatus(status);
		if (conType == 10 ) {
			dynContent.setArticle(articleService.getArticle(conId));
			dynContent.setParentId(conId);
			dynContent.setConType(conType);
			
		} else if(conType == 11 ){
			Accounts2 accounts = accountsService2.getFeeAccounts(conId);
			dynContent.setAccounts2(accounts);
			dynContent.setParentId(conId);
			dynContent.setConType(conType);
		}else {
		
			Integer newContype = 9 ;
			DynContent dynContent2 = dynContentService.selectDynContentByConId(conId);
			// 组装photolist
			List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
			if (listOfPhotoAddress.size() > 0) {
				dynContent2.setPhotoList(listOfPhotoAddress);
				newContype = 8;
			}
			
			dynContent.setDynContent(dynContent2);
			dynContent.setParentId(conId);
			dynContent.setParentUser(userService.selectUserById(dynContent2.getUserId()));
			dynContent.setConType(newContype);
					
		}
		
		dynContent.setUserId(userId);
		dynContent.setUser(userService.selectUserById(userId));
		dynContent.setConWord(conWord);
		Date publishTime = new Date();
		dynContent.setPublishTime(publishTime);
		Integer hasInsert = dynContentService.insertDynContent(dynContent); 
		if (hasInsert != 0) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", dynContent);
			/*
			 * Integer thisConId = dynContent.getConId(); DynContentPhoto
			 * dynContentPhoto = new DynContentPhoto();
			 * dynContentPhoto.setConId(thisConId);
			 * dynContentPhotoService.onlyWordAdd2DynPhoto(dynContentPhoto);
			 */
		} else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/selectDynContentByConId", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectDynContentByConId(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer conId = Integer.parseInt(request.getParameter("conId"));
		DynContent dynContent = dynContentService.selectDynContentByConId(conId);
		
		if(dynContent != null){
			Integer friendUserId = dynContent.getUserId();
			User friend = userService.selectUserById(friendUserId);
			dynContent.setUser(friend);
			Integer parentId = dynContent.getParentId();
			Integer conType = dynContent.getConType();
			List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
			if (listOfPhotoAddress.size() != 0) {
				dynContent.setPhotoList(listOfPhotoAddress);
			}
			if (null != parentId) {
				if (conType == 10) {
					dynContent.setArticle(articleService.getArticle(parentId));
							
				} else if (conType == 11){
					dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
				}else {
					
					DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
					
					if (parentContent != null) {
						
						Integer userId2 = parentContent.getUserId();
						if (userId2 != null) {
							parentContent.setUser(userService.selectUserById(userId2));
							dynContent.setParentUser(userService.selectUserById(userId2));
						}
						Integer conId2 = parentContent.getConId();
						if(conId2 != null){
							List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
							if (listOfPhotoAddress2.size() > 0) {
								parentContent.setPhotoList(listOfPhotoAddress2);
							}
						}
						
						dynContent.setDynContent(parentContent);
					}
					
							
				}
			}
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", dynContent);
		}else{
			map.put("status", 4);
			map.put("message", "操作失败，动态已不存在");
			map.put("data", "");
		}
		
		return map;
	}
	
	
	/**
	 * 根据user_id查找相应的好友动态，按时间顺序排好
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectNewFriendContentList", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectNewFriendContentList(
			HttpServletRequest request, HttpServletResponse response) {
		
		long t1 = System.currentTimeMillis();
		
		Map<String, Object> map = new HashMap<String, Object>();

		String paramToken = request.getParameter("token");
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		String pS = request.getParameter("pageSize");
		
		/*String paramToken = "F6D29A9C00F142B18BE43AC8302184FC2016053013312359235666";
		Integer userId = Integer.parseInt("10044");
		Integer pageNow = Integer.parseInt("1");*/
		
		Integer pageCount = (pageNow - 1) * 10;
		Integer selfUserId = 0;
		if(null != request.getParameter("selfUserId"))
		{
			selfUserId = Integer.parseInt(request.getParameter("selfUserId"));
		}

		if (userId != null && pageNow != null && paramToken != null) {
			String dbToken = tokenService.selectTokenByUserId2(userId);
			if (true||dbToken.equals(paramToken)) {
				
				List<DynContent> listOfFriendContent = new ArrayList<DynContent>();
				
				if(pS != null && !pS.isEmpty()){
					Integer pageSize = Integer.parseInt(pS);
					listOfFriendContent = dynContentService.selectNewFriendDynContent(userId, pageCount,pageSize);
					
				}else{
					listOfFriendContent = dynContentService.selectNewFriendDynContent(userId, pageCount,10);
				}
				
				if (listOfFriendContent.size() > 0) {

					for (int i = 0; i < listOfFriendContent.size(); i++) {

						DynContent dynContent = listOfFriendContent.get(i);

						// 组装user
						Integer friendUserId = dynContent.getUserId();
						User friend = userService.selectUserById(friendUserId);
						dynContent.setUser(friend);
						
						// 组装photolist
						Integer conId = dynContent.getConId();
						List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService
								.selectDynContentPhoto(conId);
						if (listOfPhotoAddress.size() != 0) {
							dynContent.setPhotoList(listOfPhotoAddress);
						}

						// 设置点赞数
						Praise praise = new Praise();
						praise.setPraiseContent(conId);
						praise.setContentType(0);
						praise.setUserId(userId);
						Integer goodCount = praiseService.selectCountPraise(praise);
						dynContent.setGoodCount(goodCount);

						Praise userPraise = praiseService.selectPraise(praise);
						dynContent.setPraise(userPraise);
						dynContent.setHasPay(hasPay(selfUserId,dynContent.getUserId()));
						
						Integer parentId = dynContent.getParentId();
						Integer conType = dynContent.getConType();
						if (null != parentId) {
							if (conType == 10) {
								dynContent.setArticle(articleService.getArticle(parentId));
										
							} else if(conType == 11){
								dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
							} else {
								
								DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
								if (parentContent != null) {
									
									Integer userId2 = parentContent.getUserId();
									if (userId2 != null) {
										parentContent.setUser(userService.selectUserById(userId2));
										dynContent.setParentUser(userService.selectUserById(userId2));
									}
									Integer conId2 = parentContent.getConId();
									if(conId2 != null){
										List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
										if (listOfPhotoAddress2.size() > 0) {
											parentContent.setPhotoList(listOfPhotoAddress2);
										}
									}
									
									dynContent.setDynContent(parentContent);
								}
							}
						}
					}
					map.put("status", 0);
					map.put("message", "操作成功");
					map.put("data", listOfFriendContent);
					System.out.println("===");
				} else {
					map.put("status", 5);
					map.put("message", "操作成功，但用户的好友动态为空");
					map.put("data", listOfFriendContent);
				}
			} else {
				map.put("status", 7);
				map.put("message", "token错误");
				map.put("data", "");
			}
		} else {
			map.put("status", 4);
			map.put("message", "参数有误");
			map.put("data", "");
		}
		
		long t2 = System.currentTimeMillis();
		System.out.println("++++++++++++++++++"+(t2-t1));
		return map;
	}
	
	
	/**
	 * 根据自己的user_id查自己的动态，按时间顺序排好
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectNewOwnContent", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectNewOwnContent(HttpServletRequest request,
			HttpServletResponse response) {
		String paramToken = request.getParameter("token");
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String dbToken = tokenService.selectTokenByUserId2(userId);
		Integer selfUserId = 0;
		if(null != request.getParameter("selfUserId"))
		{
			selfUserId = Integer.parseInt(request.getParameter("selfUserId"));
		}
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;

		Map<String, Object> map = new HashMap<String, Object>();

		if (true||dbToken.equals(paramToken)) {
			List<DynContent> listOfDyncontent = dynContentService.selectNewOwnContent(userId, pageCount);
					
			for (int i = 0; i < listOfDyncontent.size(); i++) {
				
				DynContent dynContent = listOfDyncontent.get(i);
				
				// 组装user
				Integer friendUserId = dynContent.getUserId();
				User friend = userService.selectUserById(friendUserId);
				dynContent.setUser(friend);
				
				Integer parentId = dynContent.getParentId();
				Integer conType = dynContent.getConType();
				
				// 组装photolist
				Integer conId = dynContent.getConId();
				List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
				if (listOfPhotoAddress.size() > 0) {
					dynContent.setPhotoList(listOfPhotoAddress);
				}
				
				Praise praise = new Praise();
				praise.setPraiseContent(conId);
				praise.setContentType(0);
				praise.setUserId(userId);
				Integer count = praiseService.selectCountPraise(praise);
				dynContent.setGoodCount(count);
				Praise userPraise = praiseService.selectPraise(praise);
				dynContent.setPraise(userPraise);
				dynContent.setHasPay(hasPay(selfUserId,dynContent.getUserId()));
				if (null != parentId) {
					if (conType == 10) {
						dynContent.setArticle(articleService.getArticle(parentId));
								
					} else if(conType == 11){
						dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
					} else {
						
						DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
						if (parentContent != null) {
							
							Integer userId2 = parentContent.getUserId();
							if (userId2 != null) {
								parentContent.setUser(userService.selectUserById(userId2));
								dynContent.setParentUser(userService.selectUserById(userId2));
							}
							Integer conId2 = parentContent.getConId();
							if(conId2 != null){
								List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
								if (listOfPhotoAddress2.size() > 0) {
									parentContent.setPhotoList(listOfPhotoAddress2);
								}
							}
							
							dynContent.setDynContent(parentContent);
						}
					}
				}
			}
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", listOfDyncontent);

		} else {
			map.put("status", 7);
			map.put("message", "token错误");
			map.put("data", "");
		}
		return map;
	}
	
	
	/**
	 * 根据userName查找动态
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectNewDynContentByUserName", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectNewDynContentByUserName(
			HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter("userName");
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;
		Integer selfUserId = 0;
		if(null != request.getParameter("selfUserId"))
		{
			selfUserId = Integer.parseInt(request.getParameter("selfUserId"));
		}
		Map<String, Object> map = new HashMap<String, Object>();

		if (userName == null || userName == "" || pageCount == null) {
			map.put("status", -4);
			map.put("message", "参数错误");
			map.put("data", "");
		} else {
			// Map<String, Object> dataMap = new HashMap<String, Object>();
			List<DynContent> listOfDyncontent = dynContentService.selectNewDynContentByUserName(userName, pageCount);
					
			for (int i = 0; i < listOfDyncontent.size(); i++) {
				
				DynContent dynContent = listOfDyncontent.get(i);
				
				// 组装user
				Integer friendUserId = dynContent.getUserId();
				User friend = userService.selectUserById(friendUserId);
				dynContent.setUser(friend);
				dynContent.setHasPay(hasPay(selfUserId,dynContent.getUserId()));
				
				Integer parentId = dynContent.getParentId();
				Integer conType = dynContent.getConType();
				
				// 组装photolist
				Integer conId = dynContent.getConId();
				List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
				if (listOfPhotoAddress.size() != 0) {
					dynContent.setPhotoList(listOfPhotoAddress);
				}
				
				if (null != parentId) {
					if (conType == 10) {
						dynContent.setArticle(articleService.getArticle(parentId));
								
					} else if(conType == 11){
						dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
					} else {
						
						DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
						if (parentContent != null) {
							
							Integer userId2 = parentContent.getUserId();
							if (userId2 != null) {
								parentContent.setUser(userService.selectUserById(userId2));
								dynContent.setParentUser(userService.selectUserById(userId2));
							}
							Integer conId2 = parentContent.getConId();
							if(conId2 != null){
								List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
								if (listOfPhotoAddress2.size() > 0) {
									parentContent.setPhotoList(listOfPhotoAddress2);
								}
							}
							
							dynContent.setDynContent(parentContent);
						}
					}
				}
			}
			// dataMap.put("dynContentList", list);

			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", listOfDyncontent);
		}
		return map;
	}
	
	/**
	 * 根据自己的user_id查别人的动态，按时间顺序排好
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/selectNewOtherContent", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectNewOtherContent(HttpServletRequest request,
			HttpServletResponse response) {

		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer selfUserId = 0;
		if(null != request.getParameter("selfUserId"))
		{
			selfUserId = Integer.parseInt(request.getParameter("selfUserId"));
		}
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;

		Map<String, Object> map = new HashMap<String, Object>();

		List<DynContent> listOfDyncontent = dynContentService.selectNewOwnContent(userId, pageCount);
				
		for (int i = 0; i < listOfDyncontent.size(); i++) {
			
			DynContent dynContent = listOfDyncontent.get(i);
			
			// 组装user
			Integer friendUserId = dynContent.getUserId();
			User friend = userService.selectUserById(friendUserId);
			dynContent.setUser(friend);
			
			Integer parentId = listOfDyncontent.get(i).getParentId();
			Integer conType = listOfDyncontent.get(i).getConType();
			
			// 组装photolist
			Integer conId = dynContent.getConId();
			List<DynContentPhoto> listOfPhotoAddress = dynContentPhotoService.selectDynContentPhoto(conId);
			if (listOfPhotoAddress.size() > 0) {
				dynContent.setPhotoList(listOfPhotoAddress);
			}
			
			Praise praise = new Praise();
			praise.setPraiseContent(conId);
			praise.setContentType(0);
			praise.setUserId(userId);
			Integer count = praiseService.selectCountPraise(praise);
			listOfDyncontent.get(i).setGoodCount(count);
			Praise userPraise = praiseService.selectPraise(praise);
			dynContent.setPraise(userPraise);
			dynContent.setHasPay(hasPay(selfUserId,dynContent.getUserId()));
			
			if (null != parentId) {
				if (conType == 10) {
					dynContent.setArticle(articleService.getArticle(parentId));
							
				} else if(conType == 11){
					dynContent.setAccounts2(accountsService2.getFeeAccounts(parentId));
				} else {
					
					DynContent parentContent = dynContentService.selectDynContentByConId(parentId);
					if (parentContent != null) {
						
						Integer userId2 = parentContent.getUserId();
						if (userId2 != null) {
							parentContent.setUser(userService.selectUserById(userId2));
							dynContent.setParentUser(userService.selectUserById(userId2));
						}
						Integer conId2 = parentContent.getConId();
						if(conId2 != null){
							List<DynContentPhoto> listOfPhotoAddress2 = dynContentPhotoService.selectDynContentPhoto(conId2);
							if (listOfPhotoAddress2.size() > 0) {
								parentContent.setPhotoList(listOfPhotoAddress2);
							}
						}
						
						dynContent.setDynContent(parentContent);
					}
				}
			}
		}
		map.put("status", 0);
		map.put("message", "操作成功");
		map.put("data", listOfDyncontent);

		return map;
	}
	
	
	public Integer hasPay(Integer userId, Integer payUserId) {
		if(userId.equals(payUserId)) {
			return 1;
		}
		Order order = new Order();
		order.setUserId(userId);
		order.setDetail(payUserId.toString());
		order.setTradeType(25);
		Order hasOrder = cashService.hasPayContentOrder(order);
		if(null == hasOrder) {
			return 0;
		}
		else {
			if(Time.compareTime(new Date(), hasOrder.getTimeExpire()) > 0) {
				return 0;
			}
			else {
				return 1;
			}
		}
	}
	
	
	
	@RequestMapping(value = "/selectPayOrder", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectPayOrder(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer selfUserId = 0;
		if(null != request.getParameter("selfUserId"))
		{
			selfUserId = Integer.parseInt(request.getParameter("selfUserId"));
		}
		Order order = new Order();
		order.setUserId(selfUserId);
		order.setDetail(userId.toString());
		order.setTradeType(25);
		Order hasOrder = cashService.hasPayContentOrder(order);
		if(null != hasOrder) {
			map = rspFormat(hasOrder, SUCCESS);
		}
		else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}
	
}
