package com.yfwl.yfgp.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.UserMapper;
import com.yfwl.yfgp.dao1.AccountsMapper;
import com.yfwl.yfgp.dao1.AccountsMetaMapper;
import com.yfwl.yfgp.dao1.AdvanceMapper;
import com.yfwl.yfgp.dao1.OrderBookMapper;
import com.yfwl.yfgp.dao1.PoolMapper;
import com.yfwl.yfgp.dao1.PosiMapper;
import com.yfwl.yfgp.dao1.RelationMapper;
import com.yfwl.yfgp.dao1.ScoreMapper;
import com.yfwl.yfgp.dao1.StatMapper;
import com.yfwl.yfgp.dao1.StockinfoMapper;
import com.yfwl.yfgp.dao1.StrategyMapper;
import com.yfwl.yfgp.dao1.TraderecMapper;
import com.yfwl.yfgp.dao1.UserMetaMapper;
import com.yfwl.yfgp.dao1.OptitaskMapper;
import com.yfwl.yfgp.model.Accounts;
import com.yfwl.yfgp.model.AccountsMeta;
import com.yfwl.yfgp.model.Advance;
import com.yfwl.yfgp.model.Optitask;
import com.yfwl.yfgp.model.Order;
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
import com.yfwl.yfgp.service.GroupService;
import com.yfwl.yfgp.dao.OrderMapper;


@Service
public class GroupServiceImpl implements GroupService {

	@Resource
	PosiMapper posiMapper;
	@Resource
	AccountsMapper accountsMapper;
	@Resource
	StatMapper statMapper;
	@Resource
	RelationMapper relationMapper;
	@Resource
	OrderBookMapper orderbookMapper;
	@Resource
	TraderecMapper traderecMapper;
	@Resource
	ScoreMapper scoreMapper;
	@Resource
	StockinfoMapper stockinfoMapper;
	@Resource
	AccountsMetaMapper accountsMetaMapper;
	@Resource
	StrategyMapper strategyMapper;
	@Resource
	AdvanceMapper advanceMapper;
	@Resource
	UserMetaMapper userMetaMapper;
	@Resource
	PoolMapper poolMapper;
	@Resource 
	OptitaskMapper optitaskMapper;
	@Resource
	OrderMapper orderMapper;
	@Resource 
	UserMapper userMapper;
	
	@Override
	public Accounts getAccounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getAccounts(accounts);
	}

	@Override
	public List<Accounts> getAccountsList(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getAccountsList(accounts);
	}

	@Override
	public List<Accounts> getOptiAccountsList(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getOptiAccountsList(accounts);
	}

	@Override
	public Integer initAccounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.initAccounts(accounts);
	}

	@Override
	public Integer updateAccounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.updateAccounts(accounts);
	}

	@Override
	public List<Posi> getPosi(Posi posi) {
		// TODO Auto-generated method stub
		return posiMapper.getPosi(posi);
	}

	@Override
	public Integer initPosi(Posi posi) {
		// TODO Auto-generated method stub
		return posiMapper.initPosi(posi);
	}

	@Override
	public Integer initStat(Stat stat) {
		// TODO Auto-generated method stub
		return statMapper.initStat(stat);
	}

	@Override
	public Integer deletePosi(Posi posi) {
		// TODO Auto-generated method stub
		return posiMapper.deletePosi(posi);
	}
	
	@Override
	public List<Optitask> getAllRunOptitask() {
		return optitaskMapper.getAllRunOptitask();
	}
	
	@Override
	public List<Optitask> getOptitask(Optitask opti) {
		return optitaskMapper.getOptitask(opti);
	}
	
	@Override
	public Optitask getOptibuytask(Optitask opti) {
		return optitaskMapper.getOptibuytask(opti);
	}
	
	@Override
	public Optitask getOptiselltask(Optitask opti) {
		return optitaskMapper.getOptiselltask(opti);
	}
	
	@Override
	public Integer insertOptitask(Optitask opti) {
		return optitaskMapper.insertOptitask(opti);
	}
	
	@Override 
	public Integer deleteOptitask(Optitask opti) {
		return optitaskMapper.deleteOptitask(opti);
	}
	
	@Override
	public Integer updateOptitask(Optitask opti) {
		return optitaskMapper.updateOptitask(opti);
	}

	@Override
	public Integer deleteStat(Stat stat) {
		// TODO Auto-generated method stub
		return statMapper.deleteStat(stat);
	}

	@Override
	public Integer deleteOrderBook(OrderBook orderbook) {
		// TODO Auto-generated method stub
		return orderbookMapper.deleteOrderBook(orderbook);
	}

	@Override
	public Integer deleteRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.deleteRelation(relation);
	}

	@Override
	public Integer initOrderBook(OrderBook orderbook) {
		// TODO Auto-generated method stub
		return orderbookMapper.initOrderBook(orderbook);
	}

	@Override
	public Integer initTraderec(Traderec traderec) {
		// TODO Auto-generated method stub
		return traderecMapper.initTraderec(traderec);
	}

	@Override
	public Posi getStockPosi(Posi posi) {
		// TODO Auto-generated method stub
		return posiMapper.getStockPosi(posi);
	}

	@Override
	public synchronized Integer updatePosi(Posi posi) {
		// TODO Auto-generated method stub
		return posiMapper.updatePosi(posi);
	}

	@Override
	public List<OrderBook> getOrderBookList(OrderBook orderbook) {
		// TODO Auto-generated method stub
		return orderbookMapper.getOrderBookList(orderbook);
	}

	@Override
	public OrderBook getOrderBook(OrderBook orderbook) {
		// TODO Auto-generated method stub
		return orderbookMapper.getOrderBook(orderbook);
	}

	@Override
	public List<OrderBook> getOrderBookByGidAndStockid(OrderBook orderbook) {
		return orderbookMapper.getOrderBookByGidAndStockid(orderbook);
	}
	
	@Override
	public List<Traderec> getTraderecList(Traderec traderec) {
		// TODO Auto-generated method stub
		return traderecMapper.getTraderecList(traderec);
	}

	@Override
	public List<Score> getScoreList(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getScoreList(score);
	}

	@Override
	public Stat getStat(Stat stat) {
		// TODO Auto-generated method stub
		return statMapper.getStat(stat);
	}

	@Override
	public List<Stat> getRankStatList(Stat stat) {
		// TODO Auto-generated method stub
		return statMapper.getRankStatList(stat);
	}

	@Override
	public Relation getRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.getRelation(relation);
	}

	@Override
	public Integer deleteFollowRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.deleteFollowRelation(relation);
	}

	@Override
	public Integer initRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.initRelation(relation);
	}

	@Override
	public synchronized Integer updateRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.updateRelation(relation);
	}

	@Override
	public List<Relation> selectFollowRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.selectFollowRelation(relation);
	}

	@Override
	public List<Relation> selectFollowedRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.selectFollowedRelation(relation);
	}

	@Override
	public List<Integer> selectInterestedRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.selectInterestedRelation(relation);
	}

	@Override
	public List<OrderBook> getAllOrderBookList() {
		// TODO Auto-generated method stub
		return orderbookMapper.getAllOrderBookList();
	}

	@Override
	public Integer deleteStockPosi(Posi posi) {
		// TODO Auto-generated method stub
		return posiMapper.deleteStockPosi(posi);
	}

	@Override
	public Integer clearPosi() {
		// TODO Auto-generated method stub
		return posiMapper.clearPosi();
	}

	@Override
	public List<Accounts> getAllAccountsList() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllAccountsList();
	}

	@Override
	public Integer initScore(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.initScore(score);
	}

	@Override
	public List<Stat> getAllStatList() {
		// TODO Auto-generated method stub
		return statMapper.getAllStatList();
	}

	@Override
	public Score getr7Score(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr7Score(score);
	}
	
	//2016-08-05 获取七日收益的最近7七天数据
		@Override
		public List<Score> getR7dayScoreList(Score score){
			return scoreMapper.getR7dayScoreList(score);
		}

	@Override
	public Score getr30Score(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr30Score(score);
	}
	
	//2016-08-06 获取30日收益的最近30天数据
	@Override
	public List<Score> getR30dayScoreList(Score score) {
		return scoreMapper.getR30dayScoreList(score);
	}

	@Override
	public Score getr6mScore(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr6mScore(score);
	}

	//2016-08-06 获取60日收益的最近60天数据
	@Override
	public List<Score> getR60dayScoreList(Score score) {
		return scoreMapper.getR60dayScoreList(score);
	}
	
	//2016-08-06 获取1年收益的最近1年(360天)数据
	@Override
	public List<Score> getR1yearScoreList(Score score) {
		return scoreMapper.getR1yearScoreList(score);
	}
	
	@Override
	public Score getr1yScore(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr1yScore(score);
	}

	@Override
	public List<Score> getr7ScoreList(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr7ScoreList(score);
	}

	@Override
	public List<Score> getr30ScoreList(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr30ScoreList(score);
	}

	@Override
	public List<Score> getr6mScoreList(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr6mScoreList(score);
	}

	@Override
	public List<Score> getr1yScoreList(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getr1yScoreList(score);
	}

	@Override
	public Integer updateStat(Stat stat) {
		// TODO Auto-generated method stub
		return statMapper.updateStat(stat);
	}

	@Override
	public Integer deleteAccountsOrder(OrderBook orderbook) {
		// TODO Auto-generated method stub
		return orderbookMapper.deleteAccountsOrder(orderbook);
	}

	@Override
	public List<Accounts> searchAccounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.searchAccounts(accounts);
	}

	@Override
	public List<Accounts> getAllOptiedAccountsList() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllOptiedAccountsList();
	}

	@Override
	public List<Relation> selectInterestRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.selectInterestRelation(relation);
	}

	@Override
	public List<Accounts> getAllRaiseAccountsList() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllRaiseAccountsList();
	}

	@Override
	public List<Accounts> getFreeAccountsList(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getFreeAccountsList(accounts);
	}

	@Override
	public List<Accounts> getRaiseAccountsList(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getRaiseAccountsList(accounts);
	}

	@Override
	public List<Relation> getAllRelation(Relation relation) {
		// TODO Auto-generated method stub
		return relationMapper.getAllRelation(relation);
	}

	@Override
	public List<Accounts> getLeftRaiseAccountsList(Integer left) {
		// TODO Auto-generated method stub
		return accountsMapper.getLeftRaiseAccountsList(left);
	}

	@Override
	public List<Accounts> getAllRunAccountsList() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllRunAccountsList();
	}

	@Override
	public Stockinfo getStockinfo(Stockinfo stockinfo) {
		// TODO Auto-generated method stub
		return stockinfoMapper.getStockinfo(stockinfo);
	}

	@Override
	public Integer updateSpreadAccounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.updateSpreadAccounts(accounts);
	}

	@Override
	public List<Accounts> getPublishAccountsList() {
		// TODO Auto-generated method stub
		return accountsMapper.getPublishAccountsList();
	}

	@Override
	public AccountsMeta getAccountsMeta(AccountsMeta accountsMeta) {
		// TODO Auto-generated method stub
		return accountsMetaMapper.getAccountsMeta(accountsMeta);
	}

	@Override
	public Integer initAccountsMeta(AccountsMeta accountsMeta) {
		// TODO Auto-generated method stub
		return accountsMetaMapper.initAccountsMeta(accountsMeta);
	}

	@Override
	public Integer updateAccountsMeta(AccountsMeta accountsMeta) {
		// TODO Auto-generated method stub
		return accountsMetaMapper.updateAccountsMeta(accountsMeta);
	}

	@Override
	public List<Accounts> getPublishRaiseAccountsList() {
		// TODO Auto-generated method stub
		return accountsMapper.getPublishRaiseAccountsList();
	}

	@Override
	public List<Accounts> getHistoryAccountsList() {
		// TODO Auto-generated method stub
		return accountsMapper.getHistoryAccountsList();
	}

	@Override
	public List<Accounts> getAllPreList() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllPreList();
	}

	@Override
	public Integer getAccountsRank(Accounts mainAccounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getAccountsRank(mainAccounts);
	}

	@Override
	public List<Accounts> getAllFreeAccounts() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllFreeAccounts();
	}

	@Override
	public List<Stockinfo> getAllStockinfo() {
		// TODO Auto-generated method stub
		return stockinfoMapper.getAllStockinfo();
	}

	@Override
	public List<Posi> getAllStockidPosi(Posi posi) {
		// TODO Auto-generated method stub
		return posiMapper.getAllStockidPosi(posi);
	}

	@Override
	public int joinAccountsCount(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.joinAccountsCount(accounts);
	}

	@Override
	public List<Accounts> userJoinContestAccounts(Integer userid) {
		// TODO Auto-generated method stub
		return accountsMapper.userJoinContestAccounts(userid);
	}
	
	@Override
	public List<Stat> getContestRankStatList(int cid, Integer start, Integer limit, String sortby) {
		return statMapper.getContestRankStatList(cid, start, limit, sortby);
	}

	@Override
	public List<Stat> getAllContestRankStatList(Integer start, Integer limit, String sortby) {
		// TODO Auto-generated method stub
		return statMapper.getAllContestRankStatList(start,limit,sortby);
	}

	@Override
	public Accounts userContestAccounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.userContestAccounts(accounts);
	}

	@Override
	public Traderec getFirstTraderec(Traderec traderec) {
		// TODO Auto-generated method stub
		return traderecMapper.getFirstTraderec(traderec);
	}

	@Override
	public List<Accounts> getAllWebAccounts() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllWebAccounts();
	}

	@Override
	public Score getLastScore(Score score) {
		// TODO Auto-generated method stub
		return scoreMapper.getLastScore(score);
	}

	@Override
	public Integer deleteAllStrategy(int type) {
		// TODO Auto-generated method stub
		return strategyMapper.deleteAllStrategy(type);
	}

	@Override
	public Integer insertStrategy(Strategy strategy) {
		// TODO Auto-generated method stub
		return strategyMapper.insertStrategy(strategy);
	}

	@Override
	public List<Strategy> getAllStrategy(int type) {
		// TODO Auto-generated method stub
		return strategyMapper.getAllStrategy(type);
	}

	@Override
	public Integer insertAdvance(Advance advance) {
		// TODO Auto-generated method stub
		return advanceMapper.insertAdvance(advance);
	}

	@Override
	public List<Accounts> getUserAdvanceAccounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getUserAdvanceAccounts(accounts);
	}

	@Override
	public List<Advance> getAccountsAdvance(Integer gid) {
		// TODO Auto-generated method stub
		return advanceMapper.getAccountsAdvance(gid);
	}

	@Override
	public List<Accounts> getAllAdvanceAccounts() {
		// TODO Auto-generated method stub
		return accountsMapper.getAllAdvanceAccounts();
	}

	@Override
	public Integer deleteAllSystemAdvance(int gid) {
		// TODO Auto-generated method stub
		return advanceMapper.deleteAllSystemAdvance(gid);
	}

	@Override
	public void initUserMeta(UserMeta userMeta) {
		// TODO Auto-generated method stub
		userMetaMapper.initUserMeta(userMeta);
	}

	@Override
	public UserMeta getUserMeta(UserMeta userMeta) {
		// TODO Auto-generated method stub
		return userMetaMapper.getUserMeta(userMeta);
	}

	@Override
	public void updateUserMeta(UserMeta userMeta) {
		// TODO Auto-generated method stub
		userMetaMapper.updateUserMeta(userMeta);
	}

	@Override
	public List<Pool> getTypePool(int type, int size) {
		// TODO Auto-generated method stub
		return poolMapper.getTypePool(type, size);
	}

	@Override
	public List<Traderec> getTraderecList2(Integer gid, Integer pageCount) {
		// TODO Auto-generated method stub
		return traderecMapper.getTraderecList2(gid, pageCount);
	}

	@Override
	public Accounts searchAccountsByName(String gname) {
		// TODO Auto-generated method stub
		return accountsMapper.searchAccountsByName(gname);
	}

	@Override
	public boolean insertBatchRelation() {
		// TODO Auto-generated method stub
		
		List<Integer> gidlist = new ArrayList<Integer>();
		gidlist.add(6548);
		gidlist.add(6547);
		gidlist.add(6550);
		gidlist.add(6552);
		gidlist.add(6549);
		
		Integer start = 63;
		Integer end = 656;
		for(Integer gid:gidlist){
			List<Integer> uidlist = userMapper.getXNuserid(start, end);
			List<Relation> relationlist = new ArrayList<Relation>();
			for(Integer uid:uidlist){
				Relation relation = new Relation();
				relation.setGid(gid);
				relation.setUid(uid);
				relation.setSubgid(0);
				relation.setAmount(0.00);
				relation.setAttr(1);
				relationlist.add(relation);
			}
			relationMapper.insertBatchRelation(relationlist);
			start = start + (new Random().nextInt(300) + 200);
			end = end + (new Random().nextInt(400) +200);
		}
		
		return true;
	}
	
	@Override
	public List<String> getFollowFromOrderMapper(Order order) {
		return orderMapper.getFollowFromOrderMapper(order);
	}

	@Override
	public Accounts get15Accounts(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.get15Accounts(accounts);
	}

	@Override
	public Accounts getAccByOptiGid(Accounts accounts) {
		// TODO Auto-generated method stub
		return accountsMapper.getAccByOptiGid(accounts);
	}

	@Override
	public List<OrderBook> getOrderBookList2(OrderBook orderbook) {
		// TODO Auto-generated method stub
		return orderbookMapper.getOrderBookList2(orderbook);
	}


	
	
	
	
	
}
