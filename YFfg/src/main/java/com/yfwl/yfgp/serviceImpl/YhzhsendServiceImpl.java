package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.YhzhsendMapper;
import com.yfwl.yfgp.model.Stocksend;
import com.yfwl.yfgp.model.Yhzhsend;
import com.yfwl.yfgp.service.StocksendService;
import com.yfwl.yfgp.service.YhzhsendService;

@Service
public class YhzhsendServiceImpl implements YhzhsendService {
	@Autowired
	private YhzhsendMapper yhzhsendMapper;
	@Override
	public Integer insertYhzhsend(Yhzhsend yhzhsend) {
		// TODO Auto-generated method stub
		return yhzhsendMapper.insertYhzhsend(yhzhsend);
	}

	@Override
	public Integer updateYhzhsend(Yhzhsend yhzhsend) {
		// TODO Auto-generated method stub
		return yhzhsendMapper.updateYhzhsend(yhzhsend);
	}

	@Override
	public List<Yhzhsend> selectYhzhsend(int userid) {
		// TODO Auto-generated method stub
		return yhzhsendMapper.selectYhzhsend(userid);
	}

	@Override
	public Integer updateYhzhsendStatus(int userid) {
		// TODO Auto-generated method stub
		return yhzhsendMapper.updateYhzhsendStatus(userid);
	}
	
	
	

}
