package com.yfwl.yfgp.controller;

import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.ArticleComment;
import com.yfwl.yfgp.model.Praise;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.service.ArticleCommentService;
import com.yfwl.yfgp.service.ArticleService;
import com.yfwl.yfgp.service.PraiseService;
import com.yfwl.yfgp.service.UserService;


@Controller
@RequestMapping("/articlecomment")
public class ArticleCommentController {
	@Autowired
	ArticleCommentService articleCommentService;
	
	@Autowired
	PraiseService praiseService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ArticleService articleService;

	@RequestMapping(value = "/getcomment", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getComment(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer articleId = Integer.parseInt(request.getParameter("articleId"));
		Integer pagenow = Integer.parseInt(request.getParameter("pageNow"));
		Integer page = (pagenow - 1) * 10;
		if(null != request.getParameter("userId")) {
			Integer userId = Integer.parseInt(request.getParameter("userId"));
			List<ArticleComment> list = articleCommentService.getCommentList(articleId, page);
			if(list.size() != 0) {
				for(int i = 0; i < list.size(); i++)
				{
					Praise praise = new Praise();
					praise.setPraiseContent(list.get(i).getCommentId());
					praise.setContentType(3);
					praise.setUserId(userId);
					Integer count = praiseService.selectCountPraise(praise);
					count = count==null?0:count;
					list.get(i).setPraiseCount(count);
					Praise userPraise = praiseService.selectPraise(praise);
					list.get(i).setPraise(userPraise);
					Integer parentId = list.get(i).getParentId();
					ArticleComment articleComment = articleCommentService.getComment(parentId);
					list.get(i).setArticleComment(articleComment);
				}
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", list);
			}
			else
			{
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", "");
			}
		}
		else {
			List<ArticleComment> list = articleCommentService.getCommentList(articleId, page);
			if(list.size() != 0) {
				for(int i = 0; i < list.size(); i++)
				{
					Praise praise = new Praise();
					praise.setPraiseContent(list.get(i).getCommentId());
					praise.setContentType(3);
					Integer count = praiseService.selectCountPraise(praise);
					list.get(i).setPraiseCount(count);
					Integer parentId = list.get(i).getParentId();
					ArticleComment articleComment = articleCommentService.getComment(parentId);
					list.get(i).setArticleComment(articleComment);
				}
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", list);
			}
			else
			{
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", "");
			}
		}
		return map;
	}
	
	@RequestMapping(value = "/insertcomment", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> insertComment(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer articleId = Integer.parseInt(request.getParameter("articleId"));
		Integer userId = Integer.parseInt(request.getParameter("userId"));
		Integer parentId = Integer.parseInt(request.getParameter("parentId"));
		String commentContent = request.getParameter("commentContent");
		Date commentTime = new Date();
		
		ArticleComment articleComment = new ArticleComment();
		articleComment.setArticleId(articleId);
		articleComment.setParentId(parentId);
		articleComment.setCommentContent(commentContent);
		articleComment.setUserId(userId);
		articleComment.setCommentTime(commentTime);
		articleComment.setPraiseCount(0);
		User user = userService.selectUserById(userId);
		articleComment.setUser(user);
		int hasInsert = articleCommentService.insertComment(articleComment);
		if(hasInsert ==1) {
			ArticleComment articleParent = articleCommentService.getComment(parentId);
			if(null != articleParent) {
				articleComment.setArticleComment(articleParent);
			}
			//articleService.increaseCommentCount(articleId);
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", articleComment);
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
	
	@RequestMapping(value = "/deletecomment", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> deleteComment(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object>map = new HashMap<String, Object>();
		Integer commentId = Integer.parseInt(request.getParameter("commentId"));
		int hasDelete = articleCommentService.deleteComment(commentId);
		if(hasDelete == 1) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", "");
		}
		else {
			map.put("status", 4);
			map.put("message", "操作失败");
			map.put("data", "");
		}
		return map;
	}
}
