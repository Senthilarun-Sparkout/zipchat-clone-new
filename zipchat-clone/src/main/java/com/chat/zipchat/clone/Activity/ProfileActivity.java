package com.chat.zipchat.clone.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Common.BaseClass;
import com.chat.zipchat.clone.Model.ProfileImageUpdate.ProfileImageResponse;
import com.chat.zipchat.clone.Model.ProfileUpdate.ProfileUpdateRequest;
import com.chat.zipchat.clone.Model.ProfileUpdate.ProfileUpdateResponse;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.MY_REQUEST_CODE_IMAGE;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.getRealPathFromURI;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;
import static okhttp3.MediaType.parse;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView mImgProfile, mBtnCamera;
    EditText mEtName;
    Button mBtnContinue;
    String id, profile_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImgProfile = findViewById(R.id.mImgProfile);
        mBtnCamera = findViewById(R.id.mBtnCamera);
        mEtName = findViewById(R.id.mEtName);
        mBtnContinue = findViewById(R.id.mBtnContinue);

        mBtnCamera.setOnClickListener(this);
        mBtnContinue.setOnClickListener(this);

        id = getIntent().getStringExtra("id");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnContinue:
                if (TextUtils.isEmpty(mEtName.getText().toString())) {
                    mEtName.setError("Enter your Name");
                    mEtName.requestFocus();
                } else {
                    UpdateProfileService();
                }
                break;

            case R.id.mBtnCamera:
                imagePicker();
                break;
        }
    }

    private void imagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_IMAGE);
            } else {
                getImageFromGallery();
            }
        } else {
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(resultUri).into(mImgProfile);
                String filePath = getRealPathFromURI(this, resultUri);
                UpdateProfileImageService(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void UpdateProfileImageService(String filePath) {
        if (isOnline(this)) {

            showSimpleProgressDialog(this);

            //pass it like this
            File file = new File(filePath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("media", file.getName(), requestBody);

            final Call<ProfileImageResponse> registerResponseCall = apiInterface.updateProfileImage(fileToUpload);
            registerResponseCall.enqueue(new Callback<ProfileImageResponse>() {
                @Override
                public void onResponse(Call<ProfileImageResponse> call, Response<ProfileImageResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        profile_picture = response.body().getResult().getUrl();
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<ProfileImageResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mBtnContinue, BaseClass.NO_INTERNET);
        }
    }

    private void UpdateProfileService() {
        if (isOnline(this)) {

            showSimpleProgressDialog(this);

            ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
            profileUpdateRequest.setFull_name(mEtName.getText().toString());
            profileUpdateRequest.setProfile_picture(profile_picture);
            profileUpdateRequest.setStatus("Available");

            final Call<ProfileUpdateResponse> registerResponseCall = apiInterface.updateProfile(id, profileUpdateRequest);
            registerResponseCall.enqueue(new Callback<ProfileUpdateResponse>() {
                @Override
                public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        sessionManager(ProfileActivity.this).createLoginSession(
                                response.body().getResult().getId(),
                                response.body().getResult().getFullName(),
                                response.body().getResult().getProfilePicture(),
                                response.body().getResult().getAuthorization(),
                                response.body().getResult().getMobileNumber(),
                                response.body().getResult().getCountry_code(),
                                response.body().getResult().getStatus(),
                                response.body().getResult().getStellarAddress(),
                                response.body().getResult().getStellarSeed());

                        HashMap<String, String> map = new HashMap<>();
                        map.put("name", response.body().getResult().getFullName());
                        map.put("mobile-number", response.body().getResult().getMobileNumber());
                        map.put("profile-url", response.body().getResult().getProfilePicture());
                        map.put("isOnline", "1");
                        map.put("offline-time", ConvertedDateTime());
                        map.put("status", "Available");

                        DatabaseReference referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-details").child(response.body().getResult().getId()).child("profile-details");
                        referenceUserInsert.setValue(map);

                        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                    removeProgressDialog();
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mBtnContinue, BaseClass.NO_INTERNET);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case MY_REQUEST_CODE_IMAGE:
                getImageFromGallery();
                break;
        }
    }
}
