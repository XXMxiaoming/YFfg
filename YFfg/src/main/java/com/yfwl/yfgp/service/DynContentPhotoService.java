package com.yfwl.yfgp.service;

import java.util.List;

import com.yfwl.yfgp.model.DynContentPhoto;

public interface DynContentPhotoService {
	
	/**
	 * 发表纯文字动态时插入conid到图片表中
	 * @param dynContentPhoto
	 * @return
	 */
	public int onlyWordAdd2DynPhoto(DynContentPhoto dynContentPhoto);
	
	/**
	 * 插入图片地址
	 * @param dynContentPhoto
	 * @return
	 */
	public int insertPhotoAddress(DynContentPhoto dynContentPhoto);
	
	
	List<DynContentPhoto> selectDynContentPhoto(Integer conId);
}
