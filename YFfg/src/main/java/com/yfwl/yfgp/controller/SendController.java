package com.yfwl.yfgp.controller;

import java.util.ArrayList;
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

import com.yfwl.yfgp.model.Stocksend;
import com.yfwl.yfgp.model.Yhzhsend;
import com.yfwl.yfgp.service.StocksendService;
import com.yfwl.yfgp.service.YhzhsendService;

@Controller
@RequestMapping("/send")
public class SendController extends BannerController {
	@Autowired
	StocksendService stocksendService;
	@Autowired
	YhzhsendService  yhzhsendService;
	
	@RequestMapping(value = "/stockOrYhzhReaded", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> stockOrYhzhReaded(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer id = Integer.parseInt(request.getParameter("id"));
		String type = request.getParameter("type");
		if (type.equals("1")) {
			Stocksend stocksend = new Stocksend();
			stocksend.setId(id);
			stocksend.setType(type);
			stocksendService.updateStocksend(stocksend);
			map = rspFormat("", SUCCESS);
		} else if (type.equals("2")) {
			Yhzhsend yhzhsend=new Yhzhsend();
			yhzhsend.setId(id);
			yhzhsend.setType(type);
			yhzhsendService.updateYhzhsend(yhzhsend);
			map = rspFormat("", SUCCESS);
		} else {
			map.put("msg", "type或id传值错误");
			map.put("data", "");
			map.put("status", 1);
		}
		return map;
	}

//	@RequestMapping(value = "/stockunread", method = { RequestMethod.POST })
//	@ResponseBody
//	public Map<String, Object> stockunread(HttpServletRequest request,
//			HttpServletResponse response) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		Integer userid = Integer.parseInt(request.getParameter("userid"));
//		List<Stocksend> stockSendList = stocksendService.selectStocksend(userid);
//		if(!stockSendList.isEmpty()){
//			map=rspFormat(stockSendList, SUCCESS);
//			stocksendService.updateStocksendStatus(userid);//消息发送一次之后设为已读
//		}else{
//			map.put("msg", "没有未读消息");
//			map.put("data", "");
//			map.put("status", 0);
//		}
//		return map;
//	}
//	@RequestMapping(value = "/yhzhunread", method = { RequestMethod.POST })
//	@ResponseBody
//	public Map<String, Object> yhzhunread(HttpServletRequest request,
//			HttpServletResponse response) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		Integer userid = Integer.parseInt(request.getParameter("userid"));
//		List<Yhzhsend> yhzhSendList = yhzhsendService.selectYhzhsend(userid);
//		if(!yhzhSendList.isEmpty()){
//			map=rspFormat(yhzhSendList, SUCCESS);
//			yhzhsendService.updateYhzhsendStatus(userid);//消息发送一次之后设为已读
//		}else{
//			map.put("msg", "没有未读消息");
//			map.put("data", "");
//			map.put("status", 0);
//		}
//		return map;
//	}
	
	
	@RequestMapping(value = "/stockandyhzhunread", method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> stockandyhzhunread(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer userid = Integer.parseInt(request.getParameter("userid"));
		List<Stocksend> stockSendList = stocksendService.selectStocksend(userid);
		List<Yhzhsend> yhzhSendList = yhzhsendService.selectYhzhsend(userid);
		List<Object> ob=new ArrayList<Object>();
		if(!stockSendList.isEmpty()){
			for (Stocksend stocksend : stockSendList) {
				ob.add(stocksend);
			}
			stocksendService.updateStocksendStatus(userid);//消息发送一次之后设为已读
		}
		if(!yhzhSendList.isEmpty()){
			for (Yhzhsend yhzhsend : yhzhSendList) {
				ob.add(yhzhsend);
			}
			yhzhsendService.updateYhzhsendStatus(userid);//消息发送一次之后设为已读
		}
		map=rspFormat(ob, SUCCESS);
		if(yhzhSendList.isEmpty()&&stockSendList.isEmpty()){
			map.put("msg", "没有未读消息");
			map.put("data", "");
			map.put("status", 0);
		}
		return map;
	}
}
