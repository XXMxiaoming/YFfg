package com.yfwl.yfgp.dao1;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yfwl.yfgp.model.Stat;

public interface StatMapper {

	Integer initStat(Stat stat);

	Integer deleteStat(Stat stat);

	Stat getStat(Stat stat);

	List<Stat> getRankStatList(Stat stat);

	List<Stat> getAllStatList();

	Integer updateStat(Stat stat);

	List<Stat> getContestRankStatList(
			@Param(value="cite_id") int cid, 
			@Param(value="start") Integer start, 
			@Param(value="limit") Integer limit, 
			@Param(value="sortby") String sortby);

	List<Stat> getAllContestRankStatList(
			@Param(value="start") Integer start,
			@Param(value="limit") Integer limit,
			@Param(value="sortby") String sortby);

}
