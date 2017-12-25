package com.yfwl.yfgp.dao;

import java.util.List;

import com.yfwl.yfgp.model.DynContentPhoto;

public interface DynContentPhotoMapper {
	
	/**
	 * 发表纯文字动态时插入conid到图片表中
	 * @param dynContentPhoto
	 * @return
	 */
	int onlyWordAdd2DynPhoto(DynContentPhoto dynContentPhoto);
	
	/**
	 * 插入图片地址
	 * @param dynContentPhoto
	 * @return
	 */
	int insertPhotoAddress(DynContentPhoto dynContentPhoto);
	
	List<DynContentPhoto> selectDynContentPhoto(Integer conId);
}
