package com.yfwl.yfgp.service;

import java.util.List;


import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.AccountsMeta;
import com.yfwl.yfgp.model.Advance;
import com.yfwl.yfgp.model.OrderBook;
import com.yfwl.yfgp.model.Pool;
import com.yfwl.yfgp.model.Posi;
import com.yfwl.yfgp.model.Relation;
import com.yfwl.yfgp.model.Score;
import com.yfwl.yfgp.model.Stat;
import com.yfwl.yfgp.model.Stockinfo;
import com.yfwl.yfgp.model.Strategy;
import com.yfwl.yfgp.model.Traderec;
import com.yfwl.yfgp.model.UserMeta;
import com.yfwl.yfgp.model.Optitask;
import com.yfwl.yfgp.model.Order;

public interface GroupService {
	Accounts getAccounts(Accounts accounts);
	List<Accounts> getAccountsList(Accounts accounts);
	List<Accounts> getOptiAccountsList(Accounts accounts);
	Integer initAccounts(Accounts accounts);
	Integer updateAccounts(Accounts accounts);
	List<Accounts> getAllAccountsList();
	List<Accounts> getAllOptiedAccountsList();
	List<Accounts> getAllRaiseAccountsList();
	List<Accounts> searchAccounts(Accounts accounts);
	List<Accounts> getFreeAccountsList(Accounts accounts);
	List<Accounts> getRaiseAccountsList(Accounts accounts);
	List<Accounts> getAllRunAccountsList();
	Integer updateSpreadAccounts(Accounts accounts);
	
	
	
	
	
	List<Posi> getPosi(Posi posi);
	Posi getStockPosi(Posi posi);
	Integer initPosi(Posi posi);
	Integer deletePosi(Posi posi);
	Integer updatePosi(Posi posi);
	Integer deleteStockPosi(Posi posi);
	Integer clearPosi();
	
	List<Optitask> getAllRunOptitask();
	List<Optitask> getOptitask(Optitask opti);
	Optitask getOptibuytask(Optitask opti);
	Optitask getOptiselltask(Optitask opti);
	Integer insertOptitask(Optitask opti);
	Integer deleteOptitask(Optitask opti);
	Integer updateOptitask(Optitask opti);
	
	Integer initStat(Stat stat);
	Integer deleteStat(Stat stat);
	Stat getStat(Stat stat);
	List<Stat> getRankStatList(Stat stat);
	List<Stat> getAllStatList();
	Integer updateStat(Stat stat);
	
	Integer deleteOrderBook(OrderBook orderbook);
	List<OrderBook> getOrderBookList(OrderBook orderbook);
	Integer initOrderBook(OrderBook orderbook);
	OrderBook getOrderBook(OrderBook orderbook);
	List<OrderBook> getAllOrderBookList();
	List<OrderBook> getOrderBookByGidAndStockid(OrderBook orderbook);
	
	
	Integer initTraderec(Traderec traderec);
	List<Traderec> getTraderecList(Traderec traderec);
	List<Traderec> getTraderecList2(Integer gid,Integer pageCount);

	
	Integer deleteRelation(Relation relation);
	Integer deleteFollowRelation(Relation relation);
	Relation getRelation(Relation relation);
	Integer initRelation(Relation relation);
	Integer updateRelation(Relation relation);
	List<Relation> selectFollowRelation(Relation relation);
	List<Relation> selectFollowedRelation(Relation relation);
	List<Integer> selectInterestedRelation(Relation relation);
	List<Relation> selectInterestRelation(Relation relation);
	List<Relation> getAllRelation(Relation relation);
	
	
	List<Score> getScoreList(Score score);
	Integer initScore(Score score);
	Score getr7Score(Score score);
	Score getr30Score(Score score);
	Score getr6mScore(Score score);
	Score getr1yScore(Score score);
	List<Score> getR7dayScoreList(Score score);  //2016-08-05 获取7日收益的最近7天数据
	List<Score> getr7ScoreList(Score score);
	List<Score> getr30ScoreList(Score score);
	List<Score> getR30dayScoreList(Score score);  //2016-08-06 获取30日收益的最近30天数据
	List<Score> getr6mScoreList(Score score);
	List<Score> getR60dayScoreList(Score score);  //2016-08-06 获取60日收益的最近60天数据
	List<Score> getr1yScoreList(Score score);
	List<Score> getR1yearScoreList(Score score);  //2016-08-06 获取1年收益的最近1年(360天)数据
	Integer deleteAccountsOrder(OrderBook orderbook);
	List<Accounts> getLeftRaiseAccountsList(Integer left);
	
	Stockinfo getStockinfo(Stockinfo stockinfo);
	List<Accounts> getPublishAccountsList();
	List<Accounts> getPublishRaiseAccountsList();

	AccountsMeta getAccountsMeta(AccountsMeta accountsMeta);
	Integer initAccountsMeta(AccountsMeta accountsMeta);
	Integer updateAccountsMeta(AccountsMeta accountsMeta);
	List<Accounts> getHistoryAccountsList();
	List<Accounts> getAllPreList();
	Integer getAccountsRank(Accounts mainAccounts);
	List<Accounts> getAllFreeAccounts();
	List<Stockinfo> getAllStockinfo();
	List<Posi> getAllStockidPosi(Posi posi);
	int joinAccountsCount(Accounts accounts);
	List<Accounts> userJoinContestAccounts(Integer userid);
	List<Stat> getContestRankStatList(int cid, Integer start, Integer limit, String sortby);
	List<Stat> getAllContestRankStatList(Integer start, Integer limit, String sortby);
	Accounts userContestAccounts(Accounts accounts);
	
	Traderec getFirstTraderec(Traderec traderec);
	
	List<Accounts> getAllWebAccounts();
	Score getLastScore(Score score);
	Integer deleteAllStrategy(int type);
	Integer insertStrategy(Strategy strategy);
	List<Strategy> getAllStrategy(int type);
	
	Integer insertAdvance(Advance advance);
	List<Accounts> getUserAdvanceAccounts(Accounts accounts);
	List<Advance> getAccountsAdvance(Integer gid);
	
	List<Accounts> getAllAdvanceAccounts();
	Integer deleteAllSystemAdvance(int gid);
	void initUserMeta(UserMeta userMeta);
	UserMeta getUserMeta(UserMeta userMeta);
	void updateUserMeta(UserMeta userMeta);
	List<Pool> getTypePool(int type, int size);
	
	Accounts searchAccountsByName(String gname);
	
	List<String> getFollowFromOrderMapper(Order order);
	
	//首页五个组合插入假的关注量 20161011
	boolean insertBatchRelation();
	
	Accounts get15Accounts(Accounts accounts);
	
	Accounts getAccByOptiGid(Accounts accounts);
	
	
	List<OrderBook> getOrderBookList2(OrderBook orderbook);
}
