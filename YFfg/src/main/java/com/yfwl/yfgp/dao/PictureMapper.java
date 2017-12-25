package com.yfwl.yfgp.dao;

import com.yfwl.yfgp.model.Picture;

public interface PictureMapper {
	Integer updateImage(Picture picture);
	Integer insertImage(Picture picture);
	Picture selectImage(Picture picture);
}
