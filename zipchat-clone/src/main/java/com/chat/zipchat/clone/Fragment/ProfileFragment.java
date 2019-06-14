package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.Activity.ZoomActivity;
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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.OTHER_FRAGMENT;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.getRealPathFromURI;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.sessionManager;
import static com.chat.zipchat.clone.Common.BaseClass.sharedPreferences;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSIONS;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSION_ALL;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.hasPermissions;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_PROFILE_PIC;
import static com.chat.zipchat.clone.Common.SessionManager.KEY_USERNAME;
import static com.chat.zipchat.clone.Common.SessionManager.PHONE;
import static com.chat.zipchat.clone.Common.SessionManager.STATUS;
import static okhttp3.MediaType.parse;

@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment implements View.OnClickListener {


    MainActivity mActivity;
    ImageView mImgUserProfile;
    EditText mUserName, mMobileNumber, mStatus;
    int value = 0;

    String profile_picture;

    @SuppressLint("ValidFragment")
    public ProfileFragment(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mImgUserProfile = view.findViewById(R.id.mImgUser);
        mUserName = view.findViewById(R.id.mUserName);
        mMobileNumber = view.findViewById(R.id.mMobileNumber);
        mStatus = view.findViewById(R.id.mStatus);

        mImgUserProfile.setOnClickListener(this);


        Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(sharedPreferences(getActivity()).getString(KEY_PROFILE_PIC, null)).into(mImgUserProfile);
        mUserName.setText(sharedPreferences(getActivity()).getString(KEY_USERNAME, null));
        mMobileNumber.setText(sharedPreferences(getActivity()).getString(PHONE, null));
        mStatus.setText(sharedPreferences(getActivity()).getString(STATUS, null));

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.mToolEdit) {
            if (item.getTitle().equals("Edit")) {
                item.setTitle("Save");
                mUserName.setEnabled(true);
                mStatus.setEnabled(true);
                value = 1;
            } else if (item.getTitle().equals("Save")) {
                item.setTitle("Edit");
                mUserName.setEnabled(false);
                mStatus.setEnabled(false);
                value = 0;
                if (Validation()) {
                    UpdateProfileService();
                }
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mImgUser) {
            if (value == 0) {
                if ("null" != sharedPreferences(getActivity()).getString(KEY_PROFILE_PIC, null) && !TextUtils.isEmpty(sharedPreferences(getActivity()).getString(KEY_PROFILE_PIC, null))) {
                    Intent mImageIntent = new Intent(getActivity(), ZoomActivity.class);
                    mImageIntent.putExtra("Value", 2);
                    mImageIntent.putExtra("URL", sharedPreferences(getActivity()).getString(KEY_PROFILE_PIC, null));
                    startActivity(mImageIntent);
                }
            } else if (value == 1) {

                imagePicker();
            }
        }
    }

    private Boolean Validation() {

        if (TextUtils.isEmpty(mUserName.getText().toString())) {
            mUserName.setError(getResources().getString(R.string.enter_your_name));
            return false;
        } else if (TextUtils.isEmpty(mMobileNumber.getText().toString())) {
            mMobileNumber.setError(getResources().getString(R.string.enter_mobile_number));
            return false;
        }

        return true;
    }

    private void imagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(getActivity())) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
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
                .start(mActivity, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        myLog("", "onActivity");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(resultUri).into(mImgUserProfile);
                String filePath = getRealPathFromURI(getActivity(), resultUri);
                UpdateProfileImageService(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        myLog("", "onRequestPermissionsResult");
    }

    private void UpdateProfileImageService(String filePath) {
        if (isOnline(getActivity())) {

            showSimpleProgressDialog(getActivity());

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
            snackbar(getActivity(), mImgUserProfile, BaseClass.NO_INTERNET);
        }
    }

    private void UpdateProfileService() {
        if (isOnline(getActivity())) {

            showSimpleProgressDialog(getActivity());

            ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
            profileUpdateRequest.setFull_name(mUserName.getText().toString());
            profileUpdateRequest.setProfile_picture(profile_picture);


            if (TextUtils.isEmpty(mStatus.getText().toString())) {
                profileUpdateRequest.setStatus("Available");
            } else {
                profileUpdateRequest.setStatus(mStatus.getText().toString());
            }

            final Call<ProfileUpdateResponse> registerResponseCall = apiInterface.updateProfile(UserId(getActivity()), profileUpdateRequest);
            registerResponseCall.enqueue(new Callback<ProfileUpdateResponse>() {
                @Override
                public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                    removeProgressDialog();
                    if (response.isSuccessful()) {
                        sessionManager(getActivity()).createLoginSession(
                                response.body().getResult().getId(),
                                response.body().getResult().getFullName(),
                                response.body().getResult().getProfilePicture(),
                                response.body().getResult().getAuthorization(),
                                response.body().getResult().getMobileNumber(),
                                response.body().getResult().getCountry_code(),
                                response.body().getResult().getStatus(),
                                response.body().getResult().getStellarAddress(),
                                response.body().getResult().getStellarSeed());


                        Glide.with(getActivity()).setDefaultRequestOptions(requestOptionsD()).load(response.body().getResult().getProfilePicture()).into(mImgUserProfile);
                        Glide.with(mActivity).setDefaultRequestOptions(requestOptionsD()).load(sharedPreferences(getActivity()).getString(KEY_PROFILE_PIC, null)).into(mActivity.mImgUser);

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isOnline", "1");
                        map.put("offline-time", ConvertedDateTime());
                        map.put("name", response.body().getResult().getFullName());
                        map.put("mobile-number", response.body().getResult().getMobileNumber());
                        map.put("profile-url", response.body().getResult().getProfilePicture());

                        if (TextUtils.isEmpty(mStatus.getText().toString())) {
                            map.put("status", "Available");
                        } else {
                            map.put("status", mStatus.getText().toString());
                        }

                        DatabaseReference referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(getActivity())).child("profile-details");
                        referenceUserInsert.updateChildren(map);

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
            snackbar(getActivity(), mImgUserProfile, BaseClass.NO_INTERNET);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.currentFragment = OTHER_FRAGMENT;
    }

}
