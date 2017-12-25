package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.Coupon;
import com.yfwl.yfgp.model.Identity;
import com.yfwl.yfgp.model.Invitation;
import com.yfwl.yfgp.model.Mem;
import com.yfwl.yfgp.model.OwnStock;
import com.yfwl.yfgp.model.Picture;

public interface FutureService {
	
	public boolean insertImage(Picture picture);
	
	public Picture selectImage(Picture picture);
	
	public Integer updateIdentity(Identity identity);
	
	public Integer setIdentity(Identity identity);
	
	public Identity getIdentity(Integer userId);
	
	public List<OwnStock> getOwnStock(Integer userId);
	
	public Integer getOwnStockCount(Integer userId);
	
	public Integer deleteAllStock(Integer userId);
	
	public Integer insertOwnStock(OwnStock stock);
	
	public Integer deleteOwnStock(OwnStock stock);
	
	public Integer initInvitation(Invitation invitation);
	
	public Invitation getInvitation(Invitation invitation);
	
	public Integer updateInvitation(Invitation invitation);
	
	public Integer initCoupon(Coupon coupon);
	
	public List<Coupon> getCoupon(Coupon coupon);
	Integer updateCoupon2(Coupon coupon);
	
	public Integer updateCoupon(Coupon coupon);
	
	public Coupon getCouponById(Integer id);
	
	public Mem getMem(Mem mem);
	
	public Integer initMem(Mem mem);
	
	public Integer updateMem(Mem mem);

}
