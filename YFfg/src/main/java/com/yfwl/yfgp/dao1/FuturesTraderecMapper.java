package com.yfwl.yfgp.dao1;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.FuturesTraderec;

public interface FuturesTraderecMapper {

	public Integer insertFuturesTraderec(FuturesTraderec futuresTraderec);

	public List<FuturesTraderec> getAllFutures();

	public List<FuturesTraderec> getAllShares();

	public List<FuturesTraderec> getAllFutures2(Integer pageCount);

	public List<FuturesTraderec> getAllFutures3(
			@Param(value = "pageCount") Integer pageCount,
			@Param(value = "count") Integer count);

	public List<FuturesTraderec> getAllShares2(Integer pageCount);

	public List<FuturesTraderec> getAllShares3(
			@Param(value = "pageCount") Integer pageCount,
			@Param(value = "count") Integer count);
}
