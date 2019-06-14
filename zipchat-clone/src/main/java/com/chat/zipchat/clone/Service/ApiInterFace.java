package com.chat.zipchat.clone.Service;

import com.chat.zipchat.clone.Model.ContactRequest;
import com.chat.zipchat.clone.Model.ContactResponse;
import com.chat.zipchat.clone.Model.Deposit.DepositTransRequest;
import com.chat.zipchat.clone.Model.Deposit.DepositTranssResponce;
import com.chat.zipchat.clone.Model.GetMobileNumbers.GetMobileNumberResponse;
import com.chat.zipchat.clone.Model.GifStickers.GifResponse;
import com.chat.zipchat.clone.Model.GifStickers.StickerResponse;
import com.chat.zipchat.clone.Model.Notification.NotificationRequest;
import com.chat.zipchat.clone.Model.Notification.NotificationResponse;
import com.chat.zipchat.clone.Model.NotificationPojo;
import com.chat.zipchat.clone.Model.OtpVerify.OtpVerifyRequest;
import com.chat.zipchat.clone.Model.OtpVerify.OtpVerifyResponse;
import com.chat.zipchat.clone.Model.Payments.AddMoneyTransListPojo;
import com.chat.zipchat.clone.Model.Payments.GetAmountPojo;
import com.chat.zipchat.clone.Model.Payments.GetUserSavedCards;
import com.chat.zipchat.clone.Model.Payments.GetUtoXPojo;
import com.chat.zipchat.clone.Model.Payments.GetWithdrawSavedCards;
import com.chat.zipchat.clone.Model.Payments.GetXtoUPojo;
import com.chat.zipchat.clone.Model.Payments.WithdrawnTransListPojo;
import com.chat.zipchat.clone.Model.ProfileImageUpdate.ProfileImageResponse;
import com.chat.zipchat.clone.Model.ProfileUpdate.ProfileUpdateRequest;
import com.chat.zipchat.clone.Model.ProfileUpdate.ProfileUpdateResponse;
import com.chat.zipchat.clone.Model.ReceiveTransaction.ReceiveTransListPojo;
import com.chat.zipchat.clone.Model.Register.RegisterRequest;
import com.chat.zipchat.clone.Model.Register.RegisterResponse;
import com.chat.zipchat.clone.Model.ResendOtp.ResendOtpRequest;
import com.chat.zipchat.clone.Model.ResendOtp.ResendOtpResponse;
import com.chat.zipchat.clone.Model.SendAmount.SendAmountRequest;
import com.chat.zipchat.clone.Model.SendAmount.SendAmountResponse;
import com.chat.zipchat.clone.Model.SentTransaction.SentTransListPojo;
import com.chat.zipchat.clone.Model.WithdrawTrans.UploadProofResponse;
import com.chat.zipchat.clone.Model.WithdrawTrans.WithdrawTransRequest;
import com.chat.zipchat.clone.Model.WithdrawTrans.WithdrawTransResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterFace {

    @POST("user")
    Call<RegisterResponse> createUser(@Body RegisterRequest user);

    @POST("otp/verify")
    Call<OtpVerifyResponse> otpVerify(@Body OtpVerifyRequest otpPojo);

    @POST("otp")
    Call<ResendOtpResponse> resendOtp(@Body ResendOtpRequest otpPojo);

    @Multipart
    @POST("image")
    Call<ProfileImageResponse> updateProfileImage(@Part MultipartBody.Part file);

    @PUT("user/{id}")
    Call<ProfileUpdateResponse> updateProfile(@Path("id") String id, @Body ProfileUpdateRequest profileUpdateRequest);

    @POST("contact")
    Call<ContactResponse> contactDetails(@Body ContactRequest contactRequest);

    @GET("account/{id}")
    Call<List<GetAmountPojo>> getAmount(@Path("id") String id);

    @GET("sentTransaction/{id}")
    Call<List<SentTransListPojo>> sendTransactionList(@Path("id") String id);

    @GET("receivedTransaction/{id}")
    Call<List<ReceiveTransListPojo>> receiveTransactionList(@Path("id") String id);

    @GET("depositTransaction/{id}")
    Call<List<AddMoneyTransListPojo>> depositTransactionList(@Path("id") String id);

    @GET("withdrawTransaction/{id}")
    Call<List<WithdrawnTransListPojo>> withdrawTransactionList(@Path("id") String id);

    @GET("card/{id}")
    Call<List<GetUserSavedCards>> getUserSavedCards(@Path("id") String id);

    @GET("withdraw/{id}")
    Call<List<GetWithdrawSavedCards>> getWithdrawSavedCards(@Path("id") String id);

    @POST("withdraw/{id}")
    Call<WithdrawTransResponse> withdrawAmount(@Body WithdrawTransRequest withdrawTransRequest, @Path("id") String id);

    @POST("payment")
    Call<SendAmountResponse> sendAmount(@Body SendAmountRequest sendAmountRequest);

    @GET("search")
    Call<List<GetMobileNumberResponse>> getMobileNumber(@Query("mobile_number") String mobile);

    @GET("user")
    Call<GetMobileNumberResponse> getUserDetails(@Query("mobile_number") String mobile);

    @POST(" upload")
    @FormUrlEncoded
    Call<UploadProofResponse> uploadProofImageform(@Field("image") String base64);

    @POST("deposit")
    Call<DepositTranssResponce> depositAmount(@Body DepositTransRequest depositTransRequest);

    @GET("price?fsym=USD&tsyms=XLM")
    Call<GetUtoXPojo> getUtoXConvertion();

    @GET("price?fsym=XLM&tsyms=USD")
    Call<GetXtoUPojo> getXtoUConvertion();

    @GET("index.php?server_key=AIzaSyBvaiVvZbJF5FTVQsWUvERWBH9CiBzBuVg")
    Call<NotificationPojo> notification(@Query("token") String id, @Query("message") String message);

    @POST("notification")
    Call<NotificationResponse> sendNotification(@Body NotificationRequest notificationRequest);

    @GET("trending")
    Call<GifResponse> getGif(@Query("api_key") String gif_api_key);

    @GET("stickers")
    Call<StickerResponse> getStickers();

}
