package com.yfwl.yfgp.dao;

import java.util.List;

import com.yfwl.yfgp.model.DynContentMeta;

public interface DynContentMetaMapper {

	Integer insertMeta(DynContentMeta meta);

	Integer updateMeta(DynContentMeta meta);

	DynContentMeta getMeta(DynContentMeta meta);

}
