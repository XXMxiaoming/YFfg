package com.yfwl.yfgp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.ValidateCodeMapper;
import com.yfwl.yfgp.model.ValidateCode;
import com.yfwl.yfgp.service.ValidateCodeService;

@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {
	
	@Autowired
	private ValidateCodeMapper validateCodeMapper;
	
	@Override
	public Integer initCode(ValidateCode validateCode) {
		// TODO Auto-generated method stub
		return validateCodeMapper.initCode(validateCode);
	}

	@Override
	public ValidateCode getCode(ValidateCode validateCode) {
		// TODO Auto-generated method stub
		return validateCodeMapper.getCode(validateCode);
	}

	@Override
	public Integer updateCode(ValidateCode validateCode) {
		// TODO Auto-generated method stub
		return validateCodeMapper.updateCode(validateCode);
	}

}
