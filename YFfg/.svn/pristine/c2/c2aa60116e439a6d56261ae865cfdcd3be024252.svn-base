package com.yfwl.yfgp.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tencent.xinge.XingeApp;
import com.yfwl.yfgp.model.DynContent;
import com.yfwl.yfgp.model.DynContentComment;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.DynContentCommentService;
import com.yfwl.yfgp.service.DynContentService;
import com.yfwl.yfgp.service.TokenService;
import com.yfwl.yfgp.service.UserService;

@Controller
@RequestMapping("/dyncontentcomment")
public class DynContentCommentController {
	
	@Autowired
	private DynContentCommentService dynContentCommentService;
	@Autowired
	private DynContentService dynContentService;
	@Autowired
	private UserService userService;
	@Autowired
	private TokenService tokenService;

	
	/**
	 * 根据动态ID content_id 查找评论
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/selectCommentByConId",method = { RequestMethod.POST })
	@ResponseBody
	public  Map<String,Object> selectCommentByConId(HttpServletRequest request,HttpServletResponse response){
		
		Integer contentId = Integer.parseInt(request.getParameter("contentId"));		
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		Integer pageCount = (pageNow - 1) * 10;
		
		List<DynContentComment> listOfComment = dynContentCommentService.selectCommentByConId(contentId,pageCount);
		
		Map<String,Object>	map = new HashMap<String,Object>();	
		map.put("status", 0);
		map.put("message", "操作成功");
		map.put("data", listOfComment);
		return map;		
	}
	
	/**
	 * 新增一条评论
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/insertComment",method = { RequestMethod.POST })
	@ResponseBody
	//@SensitivewordInterceptorAnnotation
	public Map<String,Object> insertComment(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		
		Integer contentId = Integer.parseInt(request.getParameter("contentId"));
		Integer parentId = 0;
		String parentString = request.getParameter("parentId");
		if(null != parentString	) {
			parentId = Integer.parseInt(parentString);
		}
		String user_name = "";
		String nameString = request.getParameter("name");
		if(null != nameString) {
			user_name = nameString;
		}

		//String comContent = (String) request.getAttribute("comContent");

		String comContent = request.getParameter("comContent");
		
		String paramToken = request.getParameter("token");
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		String dbToken = tokenService.selectTokenByUserId2(userId);
		Date comTime = new Date();	
		
		DynContentComment dynContentComment = new DynContentComment();
		
		//dynContentComment.setId(id);		
		dynContentComment.setConId(contentId);
		dynContentComment.setComContent(comContent);
		dynContentComment.setUserId(userId);
		dynContentComment.setComTime(comTime);
		dynContentComment.setParentId(parentId);
		dynContentComment.setName(user_name);
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		if(dbToken.equals(paramToken)){
			DynContent dynContent = dynContentService.selectDynContentByConId(contentId);
			if(dynContent != null){
				boolean isInsertOk = dynContentCommentService.insertComment(dynContentComment);
				if(isInsertOk == true){
					
					Map<String,Object> dataMap = new HashMap<String,Object>();
					dataMap.put("id", dynContentComment.getId());
					map.put("status", 0);
					map.put("message", "操作成功");
					map.put("data", dataMap);
					
					User user = userService.selectUserByUsername(user_name);
					User commentUser = userService.selectUserById(userId);
					String content = commentUser.getUserName() + "回复了您:" + comContent;
					Map<String,Object> custom = new HashMap<String,Object>();
					custom.put("id", contentId);
					custom.put("userName", commentUser.getUserName());
					custom.put("headImage", commentUser.getHeadImage());
					custom.put("type", 1);
					custom.put("commentId", dynContentComment.getId());
					XingeApp.pushAccountAndroidMessage(2100170039L, "42a5e3e650f7a0bb05cb18b1f8992d6b", "新回复", content, (user.getUserId()).toString(), custom);
					XingeApp.pushAccountIosMessage(2200170079L, "4f6b915bdfcb9bdba09de7a30c967faf", content, (user.getUserId()).toString(), XingeApp.IOSENV_DEV, custom);
					Pattern pattern = Pattern.compile("@(.*?)\\s");//编译正则
					Matcher matcher = pattern.matcher(comContent);
					List<String> strs = new ArrayList<String>();
				    while (matcher.find()) {
				    	String m = matcher.group(1);
				    	System.out.println(m);
				    	User mUser = userService.selectUserByUsername(m);
				    	if(null != mUser) {
				    		strs.add(mUser.getUserId().toString());
				    	}
				    }
				    custom.remove("type");
				    custom.put("type", 2);
				    content = commentUser.getUserName() + "@了您:";
				    XingeApp.pushAccountListAndroidMessage(2100170039L, "42a5e3e650f7a0bb05cb18b1f8992d6b", "@消息", content, strs, custom);
				    XingeApp.pushAccountListIosMessage(2200170079L, "4f6b915bdfcb9bdba09de7a30c967faf", content, strs, XingeApp.IOSENV_DEV, custom);
					
				}else{
					map.put("status", 4);
					map.put("message", "操作失败");
					map.put("data", "");
				}
			}else{
				map.put("status", 5);
				map.put("message", "该条动态已删除");
				map.put("data", "");
			}
		}else{
			map.put("status", 7);
			map.put("message", "token错误");
			map.put("data", "");
		}	
		return map;
	}	

	
	/**
	 * 根据评论ID 删除评论
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/deleteComment",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String,Object> deleteComment(HttpServletRequest request,HttpServletResponse response){
		
		Integer comId = Integer.parseInt(request.getParameter("comId"));
		Map<String,Object> map = new HashMap<String,Object>();
		boolean isOk = dynContentCommentService.deleteComment(comId);
		if(isOk == true){
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
}
