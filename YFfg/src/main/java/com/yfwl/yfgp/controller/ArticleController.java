package com.yfwl.yfgp.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.Article;
import com.yfwl.yfgp.service.ArticleService;

@Controller
@RequestMapping("/article")
public class ArticleController {
	
	@Autowired
	private ArticleService articleService;
	
	/**
	 * 资讯查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/selectNewsList",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectNewsList(HttpServletRequest request,
			HttpServletResponse response){
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		String type = request.getParameter("type");
		Integer pageNow = Integer.parseInt(request.getParameter("pageNow"));
		String userId = request.getParameter("userId");
		Integer pageCount = (pageNow - 1) * 10;
		
		//是否登录
		if(null != request.getParameter("userId")){
			//已登录
			Integer userId2 = Integer.parseInt(userId);
			//是否第一页
			if(pageNow == 1){
				//是在第一页
				List<Article> stickNewsList = articleService.selectStickNewsLogin(type,userId2);
				articleService.updateArtStickStatus(type);
				List<Article> list = articleService.selectNewsListLogin(type,pageCount,userId2);
				
				if(stickNewsList.size() > 0 ){
					if(list.size() > 0){
						stickNewsList.addAll(list);
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", stickNewsList);
						map.put("time", new Date());
					}else{
						map.put("status", 0);
						map.put("message", "操作成功，只有置顶资讯，没有普通资讯");
						map.put("data", stickNewsList);
						map.put("time", new Date());
					}
				}else{
					if(list.size() > 0){
						map.put("status", 0);
						map.put("message", "操作成功，没有置顶资讯，只有普通资讯");
						map.put("data", list);
						map.put("time", new Date());
					}else{
						map.put("status", 0);
						map.put("message", "操作成功，没有资讯数据");
						map.put("data", "");
						map.put("time", new Date());
					}
				}
			}else{
				//不在第一页
				List<Article> list = articleService.selectNewsListLogin(type,pageCount,userId2);
				if(list.size() > 0){
					map.put("status", 0);
					map.put("message", "操作成功，非第一页资讯数据");
					map.put("data", list);
					map.put("time", new Date());
				}else{
					map.put("status", 0);
					map.put("message", "操作成功，没有资讯数据");
					map.put("data", list);
					map.put("time", new Date());
				}
			}
		}else {
			//未登录
			//是否在第一页
			if(pageNow == 1){
				//在第一页
				List<Article> stickNewsList = articleService.selectStickNews(type);
				articleService.updateArtStickStatus(type);
				List<Article> list = articleService.selectNewsList(type,pageCount);	
				
				if(stickNewsList.size() > 0 ){
					if(list.size() > 0){
						stickNewsList.addAll(list);
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", stickNewsList);
						map.put("time", new Date());
					}else{
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", stickNewsList);
						map.put("time", new Date());
					}
				}else{
					if(list.size() > 0){
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", list);
						map.put("time", new Date());
					}else{
						map.put("status", 0);
						map.put("message", "操作成功");
						map.put("data", "");
						map.put("time", new Date());
					}
				}
			}else{
				//不在第一页
				List<Article> list = articleService.selectNewsList(type,pageCount);
				if(list.size() > 0){
					map.put("status", 0);
					map.put("message", "操作成功，非第一页资讯数据");
					map.put("data", list);
					map.put("time", new Date());
				}else{
					map.put("status", 0);
					map.put("message", "操作成功，没有资讯数据");
					map.put("data", list);
					map.put("time", new Date());
				}
			}
		}
		return map;
	}
	
	/**
	 * 头条查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/selectBigNewsList",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> selectBigNewsList(HttpServletRequest request,
			HttpServletResponse response){
		if(null != request.getParameter("userId"))
		{
			Map<String, Object> map = articleService.selectBigNewsListLogin(Integer.parseInt(request.getParameter("userId")));
			return map;
		}
		else {
			Map<String, Object> map = articleService.selectBigNewsList();
			return map;
		}
	}
}
