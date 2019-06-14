package com.chat.zipchat.clone.Model.ProfileUpdate;

import com.google.gson.annotations.SerializedName;

public class Result{

	@SerializedName("authorization")
	private String authorization;

	@SerializedName("mobile_verified")
	private int mobileVerified;

	@SerializedName("full_name")
	private String fullName;

	@SerializedName("__v")
	private int V;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("otp")
	private int otp;

	@SerializedName("profile_picture")
	private String profilePicture;

	@SerializedName("_id")
	private String id;

	@SerializedName("mobile_number")
	private String mobileNumber;

	@SerializedName("status")
	private String status;

	@SerializedName("stellarAddress")
	private String stellarAddress;

	@SerializedName("stellarSeed")
	private String stellarSeed;

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	@SerializedName("country_code")
	private String country_code;

	public void setAuthorization(String authorization){
		this.authorization = authorization;
	}

	public String getAuthorization(){
		return authorization;
	}

	public void setMobileVerified(int mobileVerified){
		this.mobileVerified = mobileVerified;
	}

	public int getMobileVerified(){
		return mobileVerified;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setV(int V){
		this.V = V;
	}

	public int getV(){
		return V;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setOtp(int otp){
		this.otp = otp;
	}

	public int getOtp(){
		return otp;
	}

	public void setProfilePicture(String profilePicture){
		this.profilePicture = profilePicture;
	}

	public String getProfilePicture(){
		return profilePicture;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public String getStellarAddress() {
		return stellarAddress;
	}

	public void setStellarAddress(String stellarAddress) {
		this.stellarAddress = stellarAddress;
	}

	public String getStellarSeed() {
		return stellarSeed;
	}

	public void setStellarSeed(String stellarSeed) {
		this.stellarSeed = stellarSeed;
	}
}