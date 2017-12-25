package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.CouponMapper;
import com.yfwl.yfgp.dao.IdentityMapper;
import com.yfwl.yfgp.dao.InvitationMapper;
import com.yfwl.yfgp.dao.MemMapper;
import com.yfwl.yfgp.dao.OwnStockMapper;
import com.yfwl.yfgp.dao.PictureMapper;
import com.yfwl.yfgp.model.Coupon;
import com.yfwl.yfgp.model.Identity;
import com.yfwl.yfgp.model.Invitation;
import com.yfwl.yfgp.model.Mem;
import com.yfwl.yfgp.model.OwnStock;
import com.yfwl.yfgp.model.Picture;
import com.yfwl.yfgp.service.FutureService;


@Service
public class FutureServiceImpl implements FutureService{
	@Autowired
	PictureMapper pictureMapper;
	@Autowired
	IdentityMapper identityMapper;
	@Autowired
	OwnStockMapper ownStockMapper;
	@Autowired
	InvitationMapper invitationMapper;
	@Autowired
	CouponMapper couponMapper;
	@Autowired
	MemMapper memMapper;

	@Override
	public boolean insertImage(Picture picture) {
		// TODO Auto-generated method stub
		boolean isUpdateOk = false;
		Integer updateVal = pictureMapper.insertImage(picture);
		if(updateVal > 0){
			isUpdateOk = true;
		}
		return isUpdateOk;
	}

	@Override
	public Picture selectImage(Picture picture) {
		// TODO Auto-generated method stub
		
		return pictureMapper.selectImage(picture);
	}

	@Override
	public Integer setIdentity(Identity identity) {
		// TODO Auto-generated method stub
		return identityMapper.setIdentity(identity);
	}

	@Override
	public Identity getIdentity(Integer userId) {
		// TODO Auto-generated method stub
		return identityMapper.getIdentity(userId);
	}

	@Override
	public Integer updateIdentity(Identity identity) {
		// TODO Auto-generated method stub
		return identityMapper.updateIdentity(identity);
	}

	@Override
	public List<OwnStock> getOwnStock(Integer userId) {
		// TODO Auto-generated method stub
		return ownStockMapper.getOwnStock(userId);
	}

	@Override
	public Integer insertOwnStock(OwnStock stock) {
		// TODO Auto-generated method stub
		return ownStockMapper.insertOwnStock(stock);
	}

	@Override
	public Integer deleteOwnStock(OwnStock stock) {
		// TODO Auto-generated method stub
		return ownStockMapper.deleteOwnStock(stock);
	}

	@Override
	public Integer getOwnStockCount(Integer userId) {
		// TODO Auto-generated method stub
		return ownStockMapper.getOwnStockCount(userId);
	}

	@Override
	public Invitation getInvitation(Invitation invitation) {
		// TODO Auto-generated method stub
		return invitationMapper.getInvitation(invitation);
	}

	@Override
	public Integer updateInvitation(Invitation invitation) {
		// TODO Auto-generated method stub
		return invitationMapper.updateInvitation(invitation);
	}

	@Override
	public Integer initInvitation(Invitation invitation) {
		// TODO Auto-generated method stub
		return invitationMapper.initInvitation(invitation);
	}

	@Override
	public Integer initCoupon(Coupon coupon) {
		// TODO Auto-generated method stub
		return couponMapper.initCoupon(coupon);
	}

	@Override
	public List<Coupon> getCoupon(Coupon coupon) {
		// TODO Auto-generated method stub
		return couponMapper.getCoupon(coupon);
	}

	@Override
	public Integer updateCoupon(Coupon coupon) {
		// TODO Auto-generated method stub
		return couponMapper.updateCoupon(coupon);
	}

	@Override
	public Mem getMem(Mem mem) {
		Mem result = memMapper.getMem(mem);
		if(result != null) {
			return result;
		}
		else {
			Mem newMem = mem;
			memMapper.newMem(mem);
			return newMem;
		}
	}

	@Override
	public Integer initMem(Mem mem) {
		// TODO Auto-generated method stub
		return memMapper.initMem(mem);
	}

	@Override
	public Integer updateMem(Mem mem) {
		// TODO Auto-generated method stub
		return memMapper.updateMem(mem);
	}

	@Override
	public Coupon getCouponById(Integer id) {
		// TODO Auto-generated method stub
		return couponMapper.getCouponById(id);
	}

	@Override
	public Integer deleteAllStock(Integer userId) {
		// TODO Auto-generated method stub
		return ownStockMapper.deleteAllStock(userId);
	}

	@Override
	public Integer updateCoupon2(Coupon coupon) {
		// TODO Auto-generated method stub
		return couponMapper.updateCoupon2(coupon);
	}

	
}
