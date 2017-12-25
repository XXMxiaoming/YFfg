package com.yfwl.yfgp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.ContactMapper;
import com.yfwl.yfgp.model.Contact;
import com.yfwl.yfgp.service.ContactService;


@Service
public class ContactServiceImpl implements ContactService{
	@Autowired
	ContactMapper contactMapper;

	@Override
	public boolean putContact(Contact contact) {
		// TODO Auto-generated method stub
		boolean isInsertOk = false;
		int insertVal = contactMapper.insert(contact);
		if(insertVal > 0){
			isInsertOk = true;
		}
		return isInsertOk;
	}

}
