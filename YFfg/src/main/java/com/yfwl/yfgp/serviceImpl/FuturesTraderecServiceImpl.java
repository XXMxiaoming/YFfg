package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao1.FuturesTraderecMapper;
import com.yfwl.yfgp.model.FuturesTraderec;
import com.yfwl.yfgp.service.FuturesTraderecService;

@Service
public class FuturesTraderecServiceImpl implements FuturesTraderecService {
	@Autowired
	FuturesTraderecMapper futuresTraderecMapper;

	@Override
	public Integer insertFuturesTraderec(FuturesTraderec futuresTraderec) {
		// TODO Auto-generated method stub
		return futuresTraderecMapper.insertFuturesTraderec(futuresTraderec);
	}

	@Override
	public List<FuturesTraderec> getAllFutures() {
		// TODO Auto-generated method stub
		return futuresTraderecMapper.getAllFutures();
	}

	@Override
	public List<FuturesTraderec> getAllShares() {
		// TODO Auto-generated method stub
		return futuresTraderecMapper.getAllShares();
	}

	@Override
	public List<FuturesTraderec> getAllFutures2(Integer pageCount) {
		// TODO Auto-generated method stub
		return futuresTraderecMapper.getAllFutures2(pageCount);
	}

	@Override
	public List<FuturesTraderec> getAllShares2(Integer pageCount) {
		// TODO Auto-generated method stub
		return futuresTraderecMapper.getAllShares2(pageCount);
	}

	@Override
	public List<FuturesTraderec> getAllFutures3(Integer pageCount, Integer count) {
		// TODO Auto-generated method stub
		return futuresTraderecMapper.getAllFutures3(pageCount, count);
	}

	@Override
	public List<FuturesTraderec> getAllShares3(Integer pageCount, Integer count) {
		// TODO Auto-generated method stub
		return futuresTraderecMapper.getAllShares3(pageCount, count);
	}

	

}
