package com.naturecode.cockatoo;

public class RowData {
	private int image_id;
	private int file_index;
	private String name;
	private String detail;
	private String path;
	
	public RowData(){};
	
	public int getFile_index() {
		return file_index;
	}

	public void setFile_index(int file_index) {
		this.file_index = file_index;
	}

	public RowData(int id, String title, String xdetail){
		image_id = id;
		name = title;
		detail = xdetail;
	}
	
	public RowData(int id, int file_id, String title, String xdetail){
		image_id = id;
		file_index = file_id;
		name = title;
		detail = xdetail;
	}
	
	@Override
	public String toString() {
		return getImage_id()+" "+getName()+" "+getDetail();
	}
	public int getImage_id() {
		return image_id;
	}
	public void setImage_id(int mId) {
		this.image_id = mId;
	}
	public String getName() {
		return name;
	}
	public void setName(String mTitle) {
		this.name = mTitle;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String mDetail) {
		this.detail = mDetail;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}