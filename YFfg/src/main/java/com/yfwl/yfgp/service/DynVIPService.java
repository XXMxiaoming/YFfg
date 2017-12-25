package com.yfwl.yfgp.service;

import java.util.List;
import java.util.Map;

public interface DynVIPService {
	
	Integer getMyVIPNnm(Integer userId);
	
	Integer getMySubscribeVIPNum(Integer userId);
	
	List<Map<String,Object>> getVIPOrder(Integer userId,Integer pageCount,String type);

}
