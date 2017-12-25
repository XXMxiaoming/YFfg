package com.yfwl.yfgp.model;

public class Picture {
	private Integer picId;
	private String attachId;
	private Integer pictureType;
	private String originPic;
	private String compressPic;
	private Integer picState;
	
	public void setPicId(Integer picId) {
		this.picId = picId;
	}
	
	public Integer getPicID( ){
		return picId;
	}
	
	public void setAttachId(String attachId) {
		this.attachId = attachId;
	}
	
	public String getAttachId() {
		return attachId;
	}
	
	public void setPictureType(Integer pictureType) {
		this.pictureType = pictureType;
	}
	
	public Integer getPictureType(){
		return pictureType;
	}
	
	public void setOriginPic(String originPic) {
		this.originPic = originPic;
	}
	
	public String getOriginPic() {
		return originPic;
	}
	
	public void setCompressPic(String compressPic) {
		this.compressPic = compressPic;
	}
	
	public String getCompressPic() {
		return compressPic;
	}
	
	public void setPicState(Integer picState) {
		this.picState = picState;
	}
	
	public Integer getPicState(){
		return picState;
	}

}
