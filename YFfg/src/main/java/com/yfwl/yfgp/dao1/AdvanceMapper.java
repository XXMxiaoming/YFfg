package com.yfwl.yfgp.dao1;

import java.util.List;

import com.yfwl.yfgp.model.Advance;

public interface AdvanceMapper {

	Integer insertAdvance(Advance advance);

	List<Advance> getAccountsAdvance(Integer gid);

	Integer deleteAllSystemAdvance(int gid);

}
