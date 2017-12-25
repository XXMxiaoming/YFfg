package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.OwnStockMapper;
import com.yfwl.yfgp.model.OwnStock;
import com.yfwl.yfgp.service.OwnStockService;
@Service
public class OwnStockSeerviceImpl implements OwnStockService {
	@Autowired
	OwnStockMapper ownStockMapper;
	@Override
	public List<OwnStock> getOwnStock(Integer userId) {
		// TODO Auto-generated method stub
		return ownStockMapper.getOwnStock(userId);
	}

	@Override
	public Integer insertOwnStock(OwnStock stock) {
		// TODO Auto-generated method stub
		return ownStockMapper.insertOwnStock(stock);
	}

	@Override
	public Integer deleteOwnStock(OwnStock stock) {
		// TODO Auto-generated method stub
		return ownStockMapper.deleteOwnStock(stock);
	}

	@Override
	public Integer getOwnStockCount(Integer userId) {
		// TODO Auto-generated method stub
		return ownStockMapper.getOwnStockCount(userId);
	}

	@Override
	public Integer deleteAllStock(Integer userId) {
		// TODO Auto-generated method stub
		return ownStockMapper.deleteAllStock(userId);
	}

	@Override
	public List<OwnStock> getOwnStock2(String stockid) {
		// TODO Auto-generated method stub
		return ownStockMapper.getOwnStock2(stockid);
	}

}
