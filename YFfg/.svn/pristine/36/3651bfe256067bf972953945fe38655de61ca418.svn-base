package com.yfwl.yfgp.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yfwl.yfgp.model.Account;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.Contest;
import com.yfwl.yfgp.model.Coupon;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.User;
import com.yfwl.yfgp.model.WebAccount;
import com.yfwl.yfgp.model.WebInvitation;
import com.yfwl.yfgp.model.WebRecord;
import com.yfwl.yfgp.service.AccountService;
import com.yfwl.yfgp.service.ContestService;
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.service.UserService;

@Controller
@RequestMapping("/contest")
public class ContestController extends BaseController{

	@Autowired
	ContestService contestService;
	@Autowired
	GroupService groupService;
	@Autowired
	UserService userService;
	@Autowired
	AccountService accountService;
	
	
	private static Random randGen = null;
	private static char[] numbersAndLetters = null;

	public static final String randomString(int length) {
		if (length < 1) {
			return null;
		}
		if (randGen == null) {
			randGen = new Random();
			numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
			"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
			//numbersAndLetters = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
		}
		char [] randBuffer = new char[length];
		for (int i=0; i<randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
			//randBuffer[i] = numbersAndLetters[randGen.nextInt(35)];
		}
		return new String(randBuffer);
	}
	
	@RequestMapping(value = "/create", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> createContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		try{
			String cname = request.getParameter("cname");
			int cnameCount = contestService.cnameUseCount(cname);
			if(cnameCount > 0)
			{
				return rspFormatMsg("", MSG, "交流群名称已存在");
			}
			int user_id = Integer.parseInt(request.getParameter("user_id"));
			int join_fee = Integer.parseInt(request.getParameter("join_fee"));
			int type = 0;
			if(null != request.getParameter("type")) {
				type = Integer.parseInt(request.getParameter("type"));
			}
			List<Contest> contestList = contestService.getUserContest(user_id);
			if(contestList.size() >=50 ) {
				return rspFormatMsg("", MSG, "超过创建限制个数");
			}
			String info = request.getParameter("info");
			String endtime = request.getParameter("deadline");
			SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
			String id = "";
			while(true) {
				id = randomString(16);
				Contest hasContest = contestService.getContestById(id);
				if(null==hasContest) {
					break;
				}
			}
			Date deadline = sdf.parse(endtime);
			Date now = new Date();
			Contest contest = new Contest();
			contest.setId(id);
			contest.setUserId(user_id);
			contest.setCname(cname);
			contest.setInfo(info);
			contest.setType(type);
			contest.setDeadline(deadline);
			contest.setJoinFee(join_fee);
			contest.setCreatedTime(now);
			contest.setStatus(0);
			int hasInsert = contestService.createContest(contest);
			if(hasInsert > 0) {
				return rspFormat(contest, SUCCESS);
			}
			else {
				return rspFormat("", FAIL);
			}
			
		} catch (Exception e) {
			return rspFormat("", FAIL);
		}
	}
	
	@RequestMapping(value = "/join", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> joinContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		try{
			int user_id = Integer.parseInt(request.getParameter("user_id"));
			//int cid = Integer.parseInt(request.getParameter("cid"));
			String id = request.getParameter("cid");
			Contest contest = contestService.getContestById(id);
			Accounts accounts = new Accounts();
			accounts.setUserid(user_id);
			accounts.setCiteId(contest.getCid());
			int accountsCount = groupService.joinAccountsCount(accounts);
			if(accountsCount > 0) {
				return rspFormat("", FAIL);
			}
			else {
				User user = userService.selectUserById(user_id);
//				Account account = accountService.getAccount(user_id);
//				Contest contest = contestService.getContestById(cid);
//				if(account.getMoney() < contest.getJoinFee()) {
//					return rspFormat("", NO_ENOUGH_MONEY);
//				}
				String cname = contest.getCname();
				accounts.setGname(cname+"-"+user.getUserName());
				Date createtime = new Date();
				accounts.setCreatetime(createtime);
				accounts.setAttr(8);
				accounts.setOptigid(0);
				double giveTotal = 1000000.00;
				accounts.setAvailable(giveTotal);
				accounts.setTotal(giveTotal);
				accounts.setInit(giveTotal);
				accounts.setGtnum(0);
				accounts.setGznum(0);
				accounts.setDel(0);
				Integer hasInsert = groupService.initAccounts(accounts);
				if(hasInsert > 0) {
//					account.setMoney(account.getMoney() - contest.getJoinFee());
//					accountService.updateAccount(account);
					
					contest.setJoinNum(contest.getJoinNum() + 1);
					contest.setPool(contest.getPool() + contest.getJoinFee());
					contest.setUpdatedTime(createtime);
					contestService.updateContest(contest);
					
					Integer gid = accounts.getGid();
					Stat stat = new Stat();
					stat.setGid(gid);
					stat.setR7(0);
					stat.setR30(0);
					stat.setR6m(0);
					stat.setR1y(0);
					stat.setCiteId(contest.getCid());
					groupService.initStat(stat);
					return rspFormat(accounts, SUCCESS);
				}
				else {
					return rspFormat("", FAIL);
				}
			}
		} catch (Exception e) {
			return rspFormat("", FAIL);
		}
	}
	
	
	@RequestMapping(value = "/index", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> indexContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		//int cid = Integer.parseInt(request.getParameter("cid"));
		String id = request.getParameter("cid");
		Contest contest = contestService.getContestById(id);
		Accounts accounts = new Accounts();
		accounts.setUserid(user_id);
		accounts.setCiteId(contest.getCid());
		int accountsCount = groupService.joinAccountsCount(accounts);
		User contestUser = userService.selectUserById(contest.getUserId());
		contest.setUser(contestUser);
		if(accountsCount > 0) {
			contest.setJoin(1);
		}
		else {
			contest.setJoin(0);
		}
		return rspFormat(contest, SUCCESS);
	}
	
	@RequestMapping(value = "/user/accounts", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> userContestAccounts(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		//int cid = Integer.parseInt(request.getParameter("cid"));
		String id = request.getParameter("cid");
		Contest contest = contestService.getContestById(id);
		Accounts accounts = new Accounts();
		accounts.setUserid(user_id);
		accounts.setCiteId(contest.getCid());
		Accounts ma = groupService.userContestAccounts(accounts);
		if(ma != null) {
			return rspFormat(ma, SUCCESS);
		}
		else {
			return rspFormat("", WRONG_PARAM);
		}
		
	}
	
	@RequestMapping(value = "/all", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> allContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		//int user_id = Integer.parseInt(request.getParameter("user_id"));
		List<Contest> contestList = contestService.allContest();
		if(contestList.size() > 0) {
			for(Contest contest:contestList) {
				User contestUser = userService.selectUserById(contest.getUserId());
				contest.setUser(contestUser);
			}
			return rspFormat(contestList, SUCCESS);
		}
		else {
			return rspFormat("", SUCCESS);
		}
		
	}
	
	@RequestMapping(value = "/user/join", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> userJoinContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		List<Accounts> accountsList = groupService.userJoinContestAccounts(user_id);
		List<Contest> contestList = new ArrayList<>();
		for(Accounts accounts:accountsList) {
			Contest contest = contestService.getContestByCid(accounts.getCiteId());
			if(null != contest) {
				User contestUser = userService.selectUserById(contest.getUserId());
				contest.setUser(contestUser);
				contestList.add(contest);
			}
		}
		if(contestList.size() > 0) {
			return rspFormat(contestList, SUCCESS);
		}
		else {
			return rspFormat("", SUCCESS);
		}
		
	}
	
	
	@RequestMapping(value = "/user/have", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> userHaveContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		List<Contest> contestList = contestService.getUserContest(user_id);
		if(contestList.size() > 0) {
			return rspFormat(contestList, SUCCESS);
		}
		else {
			return rspFormat("", SUCCESS);
		}
	}
	
	@RequestMapping(value = "/rank", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> queryStatRank(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object>map = new HashMap<String, Object>();
		//int cid = Integer.parseInt(request.getParameter("cid"));
		String id = request.getParameter("cid");
		Contest contest = contestService.getContestById(id);
		Integer start = Integer.parseInt(request.getParameter("start"));
		Integer limit = Integer.parseInt(request.getParameter("limit"));
		if(limit > 100) {
			map = rspFormat("", FAIL);
			return map;
		}
		String sortby = request.getParameter("sortby");
		List<Stat> mainList = groupService.getContestRankStatList(contest.getCid(), start, limit, sortby);
		if(!mainList.isEmpty()) {
			for(int i = 0; i < mainList.size(); i++) {
				User user = userService.selectUserById(mainList.get(i).getAccounts().getUserid());
				mainList.get(i).getAccounts().setUser(user);
				mainList.get(i).setSort(sortby);
			}
			map = rspFormat(mainList, SUCCESS);
		}
		else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}
	
	@RequestMapping(value = "/allrank", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> queryAllStatRank(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object>map = new HashMap<String, Object>();
		Integer start = Integer.parseInt(request.getParameter("start"));
		Integer limit = Integer.parseInt(request.getParameter("limit"));
		String sortby = request.getParameter("sortby");
		List<Stat> mainList = groupService.getAllContestRankStatList(start, limit, sortby);
		if(!mainList.isEmpty()) {
			for(int i = 0; i < mainList.size(); i++) {
				User user = userService.selectUserById(mainList.get(i).getAccounts().getUserid());
				mainList.get(i).getAccounts().setUser(user);
				mainList.get(i).setSort(sortby);
			}
			map = rspFormat(mainList, SUCCESS);
		}
		else {
			map = rspFormat("", SUCCESS);
		}
		return map;
	}
	
	
	@RequestMapping(value = "/search", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> Contest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		//int user_id = Integer.parseInt(request.getParameter("user_id"));
		String cname = request.getParameter("cname");
		List<Contest> contestList = contestService.searchContest(cname);
		if(contestList.size() > 0) {
			for(Contest contest:contestList) {
				User contestUser = userService.selectUserById(contest.getUserId());
				contest.setUser(contestUser);
			}
			return rspFormat(contestList, SUCCESS);
		}
		else {
			return rspFormat("", SUCCESS);
		}
		
	}
	
	
	
	@RequestMapping(value = "/update", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> updateContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		try{
			//int cid = Integer.parseInt(request.getParameter("cid"));
			String id = request.getParameter("cid");
			Contest contest = contestService.getContestById(id);
			String info = request.getParameter("info");
			contest.setInfo(info);
			int hasInsert = contestService.updateContest(contest);
			if(hasInsert > 0) {
				return rspFormat(contest, SUCCESS);
			}
			else {
				return rspFormat("", FAIL);
			}
			
		} catch (Exception e) {
			return rspFormat("", FAIL);
		}
	}
	
	
	@RequestMapping(value = "/delete", method = { RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> deleteContest(HttpServletRequest request,
			HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
//		int cid = Integer.parseInt(request.getParameter("cid"));
		
		String id = request.getParameter("cid");
		Contest contest = contestService.getContestById(id);
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		String token = request.getParameter("token");
		if(!validateToken(user_id, token)) {
			return rspFormat("", WRONG_TOKEN);
		}
		
		Accounts accounts = new Accounts();
		accounts.setUserid(user_id);
		accounts.setCiteId(contest.getCid());
		Accounts ma = groupService.userContestAccounts(accounts);
		if(null != ma) {
			Integer gid= ma.getGid();
			Date deltime = new Date();
			ma.setDel(1);
			ma.setDeltime(deltime);
			groupService.updateAccounts(ma);
			Posi posi = new Posi();
			posi.setGid(gid);
			groupService.deletePosi(posi);
			Relation relation = new Relation();
			relation.setGid(gid);
			groupService.deleteRelation(relation);
			Stat stat = new Stat();
			stat.setGid(gid);
			groupService.deleteStat(stat);
			
//			Contest contest = contestService.getContestById(cid);
			contest.setJoinNum(contest.getJoinNum() - 1);
			contest.setPool(contest.getPool() + contest.getJoinFee());
			contest.setUpdatedTime(deltime);
			contestService.updateContest(contest);
			return rspFormat("", SUCCESS);
		}
		else {
			return rspFormat("", FAIL);
		}
		
//		Contest contest = contestService.getContestById(cid);
//		if(null == contest || !(contest.getUserId()==user_id)) {
//			return rspFormat("", FAIL);
//		}
//		Contest c = new Contest();
//		c.setId(cid);
//		Integer hasDelete = contestService.deleteContest(c);
//		if(hasDelete > 0) {
//			return rspFormat("", SUCCESS);
//		}
//		else {
//			return rspFormat("", FAIL);
//		}
	}
	
	
	@RequestMapping(value="/getInvitationCode",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getInvitationCode(HttpServletRequest request,
			HttpServletResponse response){
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String,Object> map = new HashMap<String,Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		WebInvitation invitation = new WebInvitation();
		invitation.setUserId(user_id);
		WebInvitation resultInvitation = contestService.getWebInvitation(invitation);
		if(null != resultInvitation) {
			map.put("status", 0);
			map.put("message", "操作成功");
			map.put("data", resultInvitation);
		}
		else {
			while(true) {
				invitation = new WebInvitation();
				String invitationCode = randomString(10);
				Integer status = 0;
				invitation.setUserId(user_id);
				invitation.setInvitationCode(invitationCode);
				invitation.setStatus(status);
				WebInvitation hasInvitation = contestService.getWebInvitation(invitation);
				if(null == hasInvitation) {
					break;
				}
			}
			Integer hasInsert = contestService.initWebInvitation(invitation);
			if(hasInsert > 0) {
				map.put("status", 0);
				map.put("message", "操作成功");
				map.put("data", invitation);
			}
			else {
				map.put("status", 4);
				map.put("message", "操作失败");
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
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String,Object> map = new HashMap<String,Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		String fillCode = request.getParameter("fill_code");
		String paramToken = request.getParameter("token");
		if(validateToken(user_id, paramToken)) {
			WebInvitation u = new WebInvitation();
			u.setUserId(user_id);
			WebInvitation invitation = contestService.getWebInvitation(u);
			if(null == invitation) {
				
				while(true) {
					invitation = new WebInvitation();
					String invitationCode = randomString(10);
					Integer status = 0;
					invitation.setUserId(user_id);
					invitation.setInvitationCode(invitationCode);
					invitation.setStatus(status);
					WebInvitation hasInvitation = contestService.getWebInvitation(invitation);
					if(null == hasInvitation) {
						break;
					}
				}
				Integer hasInsert = contestService.initWebInvitation(invitation);
			}
			
			if(invitation.getStatus() ==1) {
				map.put("status", 5);
				map.put("msg", "已被邀请");
				map.put("data", "");
			}
			else {
				WebInvitation i = new WebInvitation();
				i.setInvitationCode(fillCode);
				WebInvitation targetInvitation = contestService.getWebInvitation(i);
				if(null != targetInvitation && targetInvitation.getUserId() != invitation.getUserId()) {
					Integer status = 1;
					invitation.setFillCode(fillCode);
					invitation.setStatus(status);
					Integer hasUpdate = contestService.updateWebInvitation(invitation);
					if(hasUpdate == 1) {
						
						WebAccount webAccount = new WebAccount();
						webAccount.setUserId(targetInvitation.getUserId());
						webAccount = contestService.getWebAccount(webAccount);
						webAccount.setPoint(webAccount.getPoint() + 10);
						contestService.updateWebAccount(webAccount);
						
						targetInvitation.setCount(targetInvitation.getCount() + 1);
						contestService.updateWebInvitation(targetInvitation);
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
	
	
	@RequestMapping(value="/getWebAccount",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> getWebAccount(HttpServletRequest request,
			HttpServletResponse response){	
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String,Object> map = new HashMap<String,Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		String paramToken = request.getParameter("token");
		if(validateToken(user_id, paramToken)) {
			WebAccount webAccount = new WebAccount();
			webAccount.setUserId(user_id);
			WebAccount account = contestService.getWebAccount(webAccount);
			if(null != account) {
				map = rspFormat(account, SUCCESS);
			}
			else {
				webAccount.setPoint(20);
				webAccount.setStatus(0);
				contestService.initWebAccount(webAccount);
				map = rspFormat(webAccount, SUCCESS);
			}
		}
		else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}
	
	
	@RequestMapping(value="/updateWebAccount",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> updateWebAccount(HttpServletRequest request,
			HttpServletResponse response){	
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String,Object> map = new HashMap<String,Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		Integer cost = Integer.parseInt(request.getParameter("cost"));
		String paramToken = request.getParameter("token");
		if(validateToken(user_id, paramToken)) {
			WebAccount webAccount = new WebAccount();
			webAccount.setUserId(user_id);
			WebAccount account = contestService.getWebAccount(webAccount);
			if(null != account) {
				account.setPoint(account.getPoint() + cost);
				contestService.updateWebAccount(account);
				map = rspFormat(account, SUCCESS);
			}
		}
		else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}
	
	
	
	@RequestMapping(value="/upgradeWebAccount",method = { RequestMethod.POST })
	@ResponseBody
	public Map<String, Object> upgradeWebAccount(HttpServletRequest request,
			HttpServletResponse response){	
		response.addHeader("Access-Control-Allow-Origin", "*");
		Map<String,Object> map = new HashMap<String,Object>();
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		Integer type = Integer.parseInt(request.getParameter("type"));
		String paramToken = request.getParameter("token");
		if(validateToken(user_id, paramToken)) {
			WebRecord webRecord = new WebRecord();
			webRecord.setUserId(user_id);
			webRecord.setType(type);
			WebRecord hasRecord = contestService.getWebRecord(webRecord);
			if(null != hasRecord) {
				map = rspFormat("", SUCCESS);
			}
			else {
				webRecord.setInfo("每日登录");
				webRecord.setCount(1);
				webRecord.setStatus(0);
				contestService.initWebRecord(webRecord);
				
				WebAccount webAccount = new WebAccount();
				webAccount.setUserId(user_id);
				WebAccount account = contestService.getWebAccount(webAccount);
				if(null != account) {
					account.setPoint(account.getPoint() + 5);
					contestService.updateWebAccount(account);
					map = rspFormat(webRecord, SUCCESS);
				}
			}
			
			
		}
		else {
			map = rspFormat("", WRONG_TOKEN);
		}
		return map;
	}
}
