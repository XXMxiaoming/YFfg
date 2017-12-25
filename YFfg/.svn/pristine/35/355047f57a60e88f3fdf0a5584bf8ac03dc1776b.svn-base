
package com.yfwl.yfgp.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yfwl.yfgp.dao.DynContentMapper;
import com.yfwl.yfgp.model.DynContent;
import com.yfwl.yfgp.service.DynContentService;

@Service
public class DynContentServiceImpl implements DynContentService{
	
	@Autowired
	private DynContentMapper dynContentMapper;


	@Override
	public boolean updateGoodCount(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isAddCountOk = false;
		int iCount = dynContentMapper.updateGoodCount(contentId);
		if(iCount > 0){
			isAddCountOk = true;
		}
		return isAddCountOk;
	}

	@Override
	public List<DynContent> selectOwnContent(Integer userId,Integer pageCount) {
		// TODO Auto-generated method stub
		return dynContentMapper.selectOwnContent(userId, pageCount);
	}

	@Override
	public boolean deleteOwnContent(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isDeleteOk = false;
		int iDelete = dynContentMapper.deleteOwnContent(contentId);
		if(iDelete > 0){
			isDeleteOk = true;
		}
		return isDeleteOk;
	}
	
	@Override
	public boolean deleteContentPhoto(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isDeleteOk = false;
		int iDelete = dynContentMapper.deleteContentPhoto(contentId);
		if(iDelete > 0){
			isDeleteOk = true;
		}
		return isDeleteOk;
	}

	@Override
	public boolean updateForwordCount(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isUpdateOk = false;
		int iUpdate = dynContentMapper.updateForwordCount(contentId);
		if(iUpdate > 0){
			isUpdateOk = true;
		}
		return isUpdateOk;
	}

	@Override
	public boolean updateComCount(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isUpdateOk = false;
		int iUpdate = dynContentMapper.updateComCount(contentId);
		if(iUpdate > 0){
			isUpdateOk = true;
		}
		return isUpdateOk;
	}

	@Override
	public boolean cutGoodCount(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isCutOk = false;
		int iCut = dynContentMapper.cutGoodCount(contentId);
		if(iCut > 0){
			isCutOk = true;
		}
		return isCutOk;
	}

	@Override
	public Integer selectGoodCount(Integer contentId) {
		// TODO Auto-generated method stub
		Integer goodCount = dynContentMapper.selectGoodCount(contentId);
		return goodCount;
	}

	@Override
	public int addDynContentOnlyWord(DynContent record) {
		// TODO Auto-generated method stub
		return dynContentMapper.addDynContentOnlyWord(record);
	}

	@Override
	public int selectComCount(Integer contentId) {
		// TODO Auto-generated method stub
		return dynContentMapper.selectComCount(contentId);
	}

	@Override
	public boolean cutComCount(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isCutOk = false;
		Integer iCut = dynContentMapper.cutComCount(contentId);
		if(iCut > 0){
			isCutOk = true;
		}
		return isCutOk;
	}

	@Override
	public List<DynContent> selectDynContentByUserName(String userName,
			Integer pageCount) {
		// TODO Auto-generated method stub		
		return dynContentMapper.selectDynContentByUserName(userName, pageCount);
	}

	@Override
	public DynContent selectDynContentByConId(Integer conId) {
		// TODO Auto-generated method stub
		return dynContentMapper.selectDynContentByConId(conId);
	}

	@Override
	public Integer insertDynContent(DynContent dynContent) {
		// TODO Auto-generated method stub
		return dynContentMapper.insertDynContent(dynContent);
	}

	@Override
	public Integer getDynCount(Integer userId) {
		// TODO Auto-generated method stub
		return dynContentMapper.getDynCount(userId);
	}

	@Override
	public boolean deleteContentComment(Integer contentId) {
		// TODO Auto-generated method stub
		boolean isDeleteOk = false;
		int iDelete = dynContentMapper.deleteContentComment(contentId);
		if(iDelete > 0){
			isDeleteOk = true;
		}
		return isDeleteOk;
	}

	@Override
	public List<DynContent> selectNewOwnContent(Integer userId, Integer pageCount) {
		// TODO Auto-generated method stub
		return dynContentMapper.selectNewOwnContent(userId, pageCount);
	}

	@Override
	public List<DynContent> selectNewDynContentByUserName(String userName, Integer pageCount) {
		// TODO Auto-generated method stub
		return dynContentMapper.selectNewDynContentByUserName(userName, pageCount);
	}

	/*@Override
	public List<DynContent> selectNewFriendDynContent(Integer userId, Integer pageCount) {
		// TODO Auto-generated method stub
		return dynContentMapper.selectNewFriendDynContent(userId, pageCount);
	}*/

	
	@Override
	public List<DynContent> selectFriendDynContent(Integer userId,Integer pageCount) {
		// TODO Auto-generated method stub
		List<Integer> list = dynContentMapper.getFriendId(userId);
		list.add(userId);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		map.put("pageCount", pageCount);
		List<DynContent> listOfFriendContent = dynContentMapper.selectFriendDynContent(map);
		return listOfFriendContent;
	}
	
	@Override
	public List<DynContent> selectNewFriendDynContent(Integer userId,Integer pageCount,Integer pageSize) {
		// TODO Auto-generated method stub
		List<Integer> list = dynContentMapper.getFriendId(userId);
		list.add(32772);
		list.add(32774);
		list.add(12421);
		list.add(32788);
		list.add(35762);
		list.add(userId);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		map.put("pageCount", pageCount);
		map.put("pageSize", pageSize);
		
		return dynContentMapper.selectNewFriendDynContent(map);
	}

	
	
	@Override
	public List<DynContent> selectFriendDynContentByList(List<Integer> list,Integer pageCount) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("list", list);
		map.put("pageCount", pageCount);
		List<DynContent> listOfFriendContent = dynContentMapper.selectFriendDynContent(map);
		return listOfFriendContent;
	}


}
