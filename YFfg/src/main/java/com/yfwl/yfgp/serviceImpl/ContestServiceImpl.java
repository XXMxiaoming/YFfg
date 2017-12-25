package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.ContestMapper;
import com.yfwl.yfgp.dao1.WebAccountMapper;
import com.yfwl.yfgp.dao1.WebInvitationMapper;
import com.yfwl.yfgp.dao1.WebRecordMapper;
import com.yfwl.yfgp.model.Contest;
import com.yfwl.yfgp.model.WebAccount;
import com.yfwl.yfgp.model.WebInvitation;
import com.yfwl.yfgp.model.WebRecord;
import com.yfwl.yfgp.service.ContestService;

@Service
public class ContestServiceImpl implements ContestService {

	@Autowired
	ContestMapper contestMapper;
	@Autowired
	WebInvitationMapper webInvitationMapper;
	@Autowired
	WebAccountMapper webAccountMapper;
	@Autowired
	WebRecordMapper webRecordMapper;
	
	@Override
	public int createContest(Contest contest) {
		return contestMapper.createContest(contest);
	}

	@Override
	public int cnameUseCount(String cname) {
		// TODO Auto-generated method stub
		return contestMapper.cnameUseCount(cname);
	}

	@Override
	public Contest getContestById(String id) {
		// TODO Auto-generated method stub
		return contestMapper.getContestById(id);
	}
	
	@Override
	public Contest getContestByCid(int cid) {
		// TODO Auto-generated method stub
		return contestMapper.getContestByCid(cid);
	}

	@Override
	public int updateContest(Contest contest) {
		// TODO Auto-generated method stub
		return contestMapper.updateContest(contest);
	}

	@Override
	public List<Contest> getUserContest(int user_id) {
		// TODO Auto-generated method stub
		return contestMapper.getUserContest(user_id);
	}
	
	@Override
	public List<Contest> allContest() {
		// TODO Auto-generated method stub
		return contestMapper.allContest();
	}

	@Override
	public List<Contest> searchContest(String cname) {
		// TODO Auto-generated method stub
		return contestMapper.searchContest(cname);
	}

	@Override
	public Integer deleteContest(Contest c) {
		// TODO Auto-generated method stub
		return contestMapper.deleteContest(c);
	}

	@Override
	public WebInvitation getWebInvitation(WebInvitation invitation) {
		// TODO Auto-generated method stub
		return webInvitationMapper.getWebInvitation(invitation);
	}

	@Override
	public Integer initWebInvitation(WebInvitation invitation) {
		// TODO Auto-generated method stub
		return webInvitationMapper.initWebInvitation(invitation);
	}

	@Override
	public Integer updateWebInvitation(WebInvitation invitation) {
		// TODO Auto-generated method stub
		return webInvitationMapper.updateWebInvitation(invitation);
	}

	@Override
	public void updateWebAccount(WebAccount webAccount) {
		// TODO Auto-generated method stub
		webAccountMapper.updateWebAccount(webAccount);
	}

	@Override
	public WebAccount getWebAccount(WebAccount webAccount) {
		// TODO Auto-generated method stub
		return webAccountMapper.getWebAccount(webAccount);
	}

	@Override
	public void initWebAccount(WebAccount webAccount) {
		// TODO Auto-generated method stub
		webAccountMapper.initWebAccount(webAccount);
	}

	@Override
	public WebRecord getWebRecord(WebRecord webRecord) {
		// TODO Auto-generated method stub
		return webRecordMapper.getWebRecord(webRecord);
	}

	@Override
	public void initWebRecord(WebRecord webRecord) {
		// TODO Auto-generated method stub
		webRecordMapper.initWebRecord(webRecord);
	};

}
