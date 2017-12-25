package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.DynContentPhotoMapper;
import com.yfwl.yfgp.model.DynContentPhoto;
import com.yfwl.yfgp.service.DynContentPhotoService;

@Service
public class DynContentPhotoServiceImpl implements DynContentPhotoService {
	
	@Autowired
	private DynContentPhotoMapper dynContentPhotoMapper;
	
	@Override
	public int onlyWordAdd2DynPhoto(DynContentPhoto dynContentPhoto) {
		// TODO Auto-generated method stub
		return dynContentPhotoMapper.onlyWordAdd2DynPhoto(dynContentPhoto);
	}

	@Override
	public int insertPhotoAddress(DynContentPhoto dynContentPhoto) {
		// TODO Auto-generated method stub
		return dynContentPhotoMapper.insertPhotoAddress(dynContentPhoto);
	}

	@Override
	public List<DynContentPhoto> selectDynContentPhoto(Integer conId) {
		// TODO Auto-generated method stub
		return dynContentPhotoMapper.selectDynContentPhoto(conId);
	}

}
