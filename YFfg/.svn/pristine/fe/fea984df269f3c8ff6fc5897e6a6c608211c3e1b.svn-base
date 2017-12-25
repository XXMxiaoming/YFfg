package com.yfwl.yfgp.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.DynContentCommentMapper;
import com.yfwl.yfgp.model.DynContentComment;
import com.yfwl.yfgp.service.DynContentCommentService;

@Service
public class DynContentCommentServiceImpl implements DynContentCommentService {
	
	@Autowired
	private DynContentCommentMapper dynContentCommentMapper;
	
	@Override
	public List<DynContentComment> selectCommentByConId(Integer contentId,Integer pageCount) {
		// TODO Auto-generated method stub
		return dynContentCommentMapper.selectCommentByConId(contentId,pageCount);
	}

	@Override
	public boolean insertComment(DynContentComment dynContentComment) {
		// TODO Auto-generated method stub
		boolean isInsertOk = false;
		int insertVal = dynContentCommentMapper.insert(dynContentComment);
		if(insertVal > 0){
			isInsertOk = true;
		}
		return isInsertOk;
	}

	@Override
	public boolean deleteComment(Integer com_id) {
		// TODO Auto-generated method stub
		boolean isDeleteOk = false;
		int deleteVal = dynContentCommentMapper.deleteByPrimaryKey(com_id);
		if(deleteVal > 0){
			isDeleteOk = true;
		}
		return isDeleteOk;
	}

	@Override
	public Integer selectCountOfComment(Integer conId) {
		// TODO Auto-generated method stub
		return dynContentCommentMapper.selectCountOfComment(conId);
	}

}
