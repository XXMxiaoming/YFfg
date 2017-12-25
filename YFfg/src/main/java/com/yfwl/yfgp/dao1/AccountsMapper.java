package com.yfwl.yfgp.dao1;

import java.util.List;


import com.yfwl.yfgp.model.Accounts;

public interface AccountsMapper {
	
	Accounts getAccounts(Accounts accounts);

	List<Accounts> getAccountsList(Accounts accounts);

	List<Accounts> getOptiAccountsList(Accounts accounts);

	Integer initAccounts(Accounts accounts);

	Integer updateAccounts(Accounts accounts);

	List<Accounts> getAllAccountsList();

	List<Accounts> searchAccounts(Accounts accounts);

	List<Accounts> getAllOptiedAccountsList();

	List<Accounts> getAllRaiseAccountsList();

	List<Accounts> getFreeAccountsList(Accounts accounts);

	List<Accounts> getRaiseAccountsList(Accounts accounts);

	List<Accounts> getLeftRaiseAccountsList(Integer left);

	List<Accounts> getAllRunAccountsList();

	Integer updateSpreadAccounts(Accounts accounts);

	List<Accounts> getPublishAccountsList();

	List<Accounts> getPublishRaiseAccountsList();

	List<Accounts> getHistoryAccountsList();

	List<Accounts> getAllPreList();

	Integer getAccountsRank(Accounts mainAccounts);

	List<Accounts> getAllFreeAccounts();

	int joinAccountsCount(Accounts accounts);

	List<Accounts> userJoinContestAccounts(Integer userid);

	Accounts userContestAccounts(Accounts accounts);

	List<Accounts> getAllWebAccounts();

	List<Accounts> getUserAdvanceAccounts(Accounts accounts);

	List<Accounts> getAllAdvanceAccounts();

	Accounts searchAccountsByName(String gname);
	
	Accounts get15Accounts(Accounts accounts);
	Accounts getAccByOptiGid(Accounts accounts);
	
	Accounts getAccountsExist(int userid);
	List<Accounts> getAccountsByUserid(int userid);
}
