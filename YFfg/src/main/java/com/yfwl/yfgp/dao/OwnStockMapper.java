package com.yfwl.yfgp.dao;

import java.util.List;

import com.yfwl.yfgp.model.OwnStock;

public interface OwnStockMapper {

	List<OwnStock> getOwnStock(Integer userId);

	Integer insertOwnStock(OwnStock stock);

	Integer deleteOwnStock(OwnStock stock);

	Integer getOwnStockCount(Integer userId);

	Integer deleteAllStock(Integer userId);
	
	List<OwnStock> getOwnStock2(String stockid);

}
