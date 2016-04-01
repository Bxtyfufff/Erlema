package com.erlema.bean;


import cn.bmob.v3.BmobObject;

public class Comments extends BmobObject {
	private String goodsID;// 物品
	private String commets;// 评论
	private String username;// 作者
	private String userId;//作者id
	private String userAvator;//作者头像
	private Comments answer;// 回复对象评论
	private String answerto;//回复谁

	public String getGoodsID() {
		return goodsID;
	}

	public void setGoodsID(String goodsID) {
		this.goodsID = goodsID;
	}

	public String getCommets() {
		return commets;
	}

	public void setCommets(String commets) {
		this.commets = commets;
	}


	public Comments getAnswer() {
		return answer;
	}

	public void setAnswer(Comments answer) {
		this.answer = answer;
	}


	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return "Comments{" +
				"goodsID='" + goodsID + '\'' +
				", commets='" + commets + '\'' +
				", username='" + username + '\'' +
				", userId='" + userId + '\'' +
				", userAvator='" + userAvator + '\'' +
				", answer=" + answer +
				", answerto='" + answerto + '\'' +
				'}';
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserAvator() {
		return userAvator;
	}

	public void setUserAvator(String userAvator) {
		this.userAvator = userAvator;
	}

	public String getAnswerto() {
		return answerto;
	}

	public void setAnswerto(String answerto) {
		this.answerto = answerto;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
