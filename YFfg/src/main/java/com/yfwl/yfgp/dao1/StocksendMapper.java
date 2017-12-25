package com.yfwl.yfgp.dao1;


import java.util.List;

import com.yfwl.yfgp.model.Stocksend;

public interface StocksendMapper {
	Integer insertStocksend(Stocksend stocksend);
	
	Integer updateStocksend(Stocksend stocksend);
	
	List<Stocksend> selectStocksend(int userid);
	
	Integer updateStocksendStatus(int userid);
}
