package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.StocksendMapper;
import com.yfwl.yfgp.model.Stocksend;
import com.yfwl.yfgp.service.StocksendService;

@Service
public class StocksendServiceImpl implements StocksendService {
	
	@Autowired
	private StocksendMapper stocksendMapper;

	@Override
	public Integer insertStocksend(Stocksend stocksend) {
		// TODO Auto-generated method stub
		return stocksendMapper.insertStocksend(stocksend);
	}

	@Override
	public Integer updateStocksend(Stocksend stocksend) {
		// TODO Auto-generated method stub
		return stocksendMapper.updateStocksend(stocksend);
	}

	@Override
	public List<Stocksend> selectStocksend(int userid) {
		// TODO Auto-generated method stub
		return stocksendMapper.selectStocksend(userid);
	}

	@Override
	public Integer updateStocksendStatus(int userid) {
		// TODO Auto-generated method stub
		return stocksendMapper.updateStocksendStatus(userid);
	}

}
