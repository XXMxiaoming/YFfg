package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Contest;
import com.yfwl.yfgp.model.WebAccount;
import com.yfwl.yfgp.model.WebInvitation;
import com.yfwl.yfgp.model.WebRecord;

public interface ContestService {
	public int createContest(Contest contest);
	public int cnameUseCount(String cname);
	public Contest getContestById(String id);
	public Contest getContestByCid(int cid);
	public int updateContest(Contest contest);
	public List<Contest> getUserContest(int user_id);
	public List<Contest> allContest();
	public List<Contest> searchContest(String cname);
	public Integer deleteContest(Contest c);
	public WebInvitation getWebInvitation(WebInvitation invitation);
	public Integer initWebInvitation(WebInvitation invitation);
	public Integer updateWebInvitation(WebInvitation invitation);
	public void updateWebAccount(WebAccount webAccount);
	public WebAccount getWebAccount(WebAccount webAccount);
	public void initWebAccount(WebAccount webAccount);
	public WebRecord getWebRecord(WebRecord webRecord);
	public void initWebRecord(WebRecord webRecord);
}
