package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.CollectionMapper;
import com.yfwl.yfgp.model.Collect;
import com.yfwl.yfgp.service.CollectionService;


@Service
public class CollectionServiceImpl implements CollectionService{
	@Autowired
	CollectionMapper collectionMapper;
	
	@Override
	public Integer insertCollection(Collect collection) {
		// TODO Auto-generated method stub
		return collectionMapper.insertCollection(collection);
	}

	@Override
	public Integer deleteCollection(Integer id) {
		// TODO Auto-generated method stub
		return collectionMapper.deleteCollection(id);
	}

	@Override
	public List<Collect> getCollection(Integer userId) {
		// TODO Auto-generated method stub
		return collectionMapper.getCollection(userId);
	}

	@Override
	public Collect getCollect(Collect collect) {
		// TODO Auto-generated method stub
		return collectionMapper.getCollect(collect);
	}

}
