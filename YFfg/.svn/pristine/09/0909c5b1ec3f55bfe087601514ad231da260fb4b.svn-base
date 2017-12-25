package com.yfwl.yfgp.serviceImpl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.BannerMapper;
import com.yfwl.yfgp.model.Banner;
import com.yfwl.yfgp.service.BannerService;

@Service
public class BannerServiceImpl implements BannerService{
	
	@Resource
	private BannerMapper bannerMapper;
	
	@Override
	public Banner selectBanner(Integer sequence) {
		// TODO Auto-generated method stub
		return bannerMapper.selectBanner(sequence);
	}

}
