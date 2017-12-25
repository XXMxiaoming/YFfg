package com.yfwl.yfgp.service;

import com.yfwl.yfgp.model.ValidateCode;

public interface ValidateCodeService {
	
	public Integer initCode(ValidateCode validateCode);
	public ValidateCode getCode(ValidateCode validateCode);
	public Integer updateCode(ValidateCode validateCode);
	
}
