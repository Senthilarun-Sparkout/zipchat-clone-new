package com.chat.zipchat.clone.Model.ProfileUpdate;

import com.google.gson.annotations.SerializedName;


public class ProfileUpdateResponse{

	@SerializedName("result")
	private Result result;

	@SerializedName("status")
	private boolean status;

	public void setResult(Result result){
		this.result = result;
	}

	public Result getResult(){
		return result;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}
}