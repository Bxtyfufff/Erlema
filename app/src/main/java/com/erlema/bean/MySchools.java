package com.erlema.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class MySchools extends BmobObject{
	private String schoolname;
	private BmobGeoPoint geopoint;
	public String getSchoolname() {
		return schoolname;
	}
	public void setSchoolname(String schoolname) {
		this.schoolname = schoolname;
	}
	public BmobGeoPoint getGeopoint() {
		return geopoint;
	}
	public void setGeopoint(BmobGeoPoint geopoint) {
		this.geopoint = geopoint;
	}

}
