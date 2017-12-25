package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.DynContentMetaMapper;
import com.yfwl.yfgp.model.DynContentMeta;
import com.yfwl.yfgp.service.DynContentMetaService;


@Service
public class DynContentMetaServiceImpl implements DynContentMetaService{
	@Autowired
	DynContentMetaMapper dynContentMetaMapper;
	
	@Override
	public Integer insertMeta(DynContentMeta meta) {
		// TODO Auto-generated method stub
		return dynContentMetaMapper.insertMeta(meta);
	}

	@Override
	public Integer updateMeta(DynContentMeta meta) {
		// TODO Auto-generated method stub
		return dynContentMetaMapper.updateMeta(meta);
	}

	@Override
	public DynContentMeta getMeta(DynContentMeta meta) {
		// TODO Auto-generated method stub
		return dynContentMetaMapper.getMeta(meta);
	}

}
