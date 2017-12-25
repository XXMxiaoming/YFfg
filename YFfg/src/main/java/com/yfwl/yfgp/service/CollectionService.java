package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Collect;

public interface CollectionService {
	
	Integer insertCollection(Collect collection);
	
	Integer deleteCollection(Integer id);
	
	List<Collect> getCollection(Integer userId);
	
	Collect getCollect(Collect collect);

}
