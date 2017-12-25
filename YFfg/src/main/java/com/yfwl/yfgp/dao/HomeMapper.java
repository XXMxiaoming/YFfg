package com.yfwl.yfgp.dao;

import java.util.List;
import java.util.Map;

import com.yfwl.yfgp.model.Article;
import com.yfwl.yfgp.model.Home;

public interface HomeMapper {
	 List<Home> getBannerInfo();
	
	 List<Home> getHotInfo();
	 
	 List<Home> getInformation();

	List<Home> getRaiseInfo();

	List<Home> getRecommendList();

	List<Home> getSuccessInfo();
}
