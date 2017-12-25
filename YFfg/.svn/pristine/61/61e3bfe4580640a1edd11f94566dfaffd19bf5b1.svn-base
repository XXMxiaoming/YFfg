package com.yfwl.yfgp.dao1;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.Traderec;

public interface TraderecMapper {

	Integer initTraderec(Traderec traderec);

	List<Traderec> getTraderecList(Traderec traderec);
	List<Traderec> getTraderecList2(
			@Param(value="gid")Integer gid, 
			@Param(value="pageCount")Integer pageCount);

	Traderec getFirstTraderec(Traderec traderec);

}
