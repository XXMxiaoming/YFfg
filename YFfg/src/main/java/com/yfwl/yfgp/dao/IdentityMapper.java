package com.yfwl.yfgp.dao;

import com.yfwl.yfgp.model.Identity;

public interface IdentityMapper {

	Integer setIdentity(Identity identity);

	Identity getIdentity(Integer userId);

	Integer updateIdentity(Identity identity);
	
}
