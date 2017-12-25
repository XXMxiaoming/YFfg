package com.yfwl.yfgp.dao;

import com.yfwl.yfgp.model.SystemService;

public interface SystemServiceMapper {
	
	SystemService getServicePriceByContentId(Integer contentid);
}
