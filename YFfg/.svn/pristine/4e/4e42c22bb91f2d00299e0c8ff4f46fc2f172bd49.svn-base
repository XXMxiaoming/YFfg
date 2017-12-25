package com.yfwl.yfgp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.DynContent;
import com.yfwl.yfgp.model.DynContentPhoto;
import com.yfwl.yfgp.model.Order;
import com.yfwl.yfgp.model.Praise;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.AccountService;
import com.yfwl.yfgp.service.AccountsService2;
import com.yfwl.yfgp.service.ArticleService;
import com.yfwl.yfgp.service.CashService;
import com.yfwl.yfgp.service.DynContentPhotoService;
import com.yfwl.yfgp.service.DynContentService;
import com.yfwl.yfgp.service.PraiseService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;
import com.yfwl.yfgp.service.VideoService;
import com.yfwl.yfgp.utils.Time;

@Controller
@RequestMapping("/account")
public class VideoController {
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	VideoService videoService;
	
	@Autowired
	private DynContentService dynContentService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DynContentPhotoService dynContentPhotoService;
	
	@Autowired
	private PraiseService praiseService;
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private AccountsService2 accountsService2;
	
	@Autowired
	private CashService cashService;
	
	/**
	 * 获取直播老师动态
	 * @param request
	 * @param response
	 * @return 
	 */
	@RequestMapping(value = "/video", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getVideo(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		String pageNow_ = request.getParameter("pageNow");
		String selfUserId_ = request.getParameter("selfUserId");
		if (pageNow_ != null && selfUserId_ != null) {
			Integer pageNow = Integer.parseInt(pageNow_);
			Integer selfUserId = Integer.parseInt(selfUserId_);
			Integer pageCount = (pageNow - 1) * 10;
			List<Integer> list = videoService.getUserIdList();
			List<DynContent> listOfFriendContent = dynContentService.selectFriendDynContentByList(list, pageCount);
			
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
						praise.setUserId(selfUserId);
						Integer goodCount = praiseService.selectCountPraise(praise);
						dynContent.setGoodCount(goodCount);
						if(selfUserId == 0){ // 用户未登录 
							praise = null;
							dynContent.setPraise(praise);
						}else { // 用户已登录
							Praise userPraise = praiseService.selectPraise(praise);
							dynContent.setPraise(userPraise);
						}
						int ii = hasPay(selfUserId,dynContent.getUserId());
						dynContent.setHasPay(ii);
						
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
				} else {
					map.put("status", 5);
					map.put("message", "操作成功，但直播列表为空");
					map.put("data", listOfFriendContent);
				}
			
		} else {
			map.put("status", 4);
			map.put("message", "参数有误");
			map.put("data", "");
		}
		
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

}
