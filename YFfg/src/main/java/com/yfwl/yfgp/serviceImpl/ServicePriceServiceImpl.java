package com.yfwl.yfgp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.ServicePriceMapper;
import com.yfwl.yfgp.dao.SystemServiceMapper;
import com.yfwl.yfgp.model.ServicePrice;
import com.yfwl.yfgp.model.SystemService;
import com.yfwl.yfgp.service.ServicePriceService;

@Service
public class ServicePriceServiceImpl implements ServicePriceService {

	@Autowired
	private SystemServiceMapper systemServiceMapper;
	@Autowired
	private ServicePriceMapper servicePriceMapper;

	@Override
	public SystemService getServicePriceByContentId(Integer contentid) {
		// TODO Auto-generated method stub
		return systemServiceMapper.getServicePriceByContentId(contentid);
	}

	@Override
	public ServicePrice getServicePriceByCode(ServicePrice servicePrice) {
		// TODO Auto-generated method stub
		return servicePriceMapper.getServicePriceByCode(servicePrice);
	}

	

	

	
	
	

}
