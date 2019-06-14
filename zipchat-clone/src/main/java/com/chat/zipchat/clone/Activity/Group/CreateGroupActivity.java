package com.chat.zipchat.clone.Activity.Group;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.Common.BaseClass;
import com.chat.zipchat.clone.Model.ChatPojo;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.ProfileImageUpdate.ProfileImageResponse;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.MY_REQUEST_CODE_IMAGE;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.getRealPathFromURI;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myCenterToast;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;
import static okhttp3.MediaType.parse;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbarCreateGroup;
    CircleImageView imgGroupIcon;
    RecyclerView rvCreateGroup;
    TextView tvParticipantCount;
    EditText etGroupSubject;
    FloatingActionButton fabCreateGroup;

    List<ResultItem> mListSelectedContact;
    SelectedContactListAdapter selectedContactListAdapter;

    String groupIcon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        toolbarCreateGroup = findViewById(R.id.toolbar_create_group);
        setSupportActionBar(toolbarCreateGroup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.new_group));
        getSupportActionBar().setSubtitle(getResources().getString(R.string.add_subject));
        toolbarCreateGroup.setTitleTextColor(getResources().getColor(R.color.white));
        toolbarCreateGroup.setSubtitleTextColor(getResources().getColor(R.color.white));
        toolbarCreateGroup.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        mListSelectedContact = (List<ResultItem>) getIntent().getExtras().getSerializable("selectedMembers");

        imgGroupIcon = findViewById(R.id.img_group_icon);
        etGroupSubject = findViewById(R.id.et_group_subject);
        tvParticipantCount = findViewById(R.id.tv_participant_count);
        fabCreateGroup = findViewById(R.id.fab_create_group);
        rvCreateGroup = findViewById(R.id.rv_create_group);
        rvCreateGroup.setLayoutManager(new GridLayoutManager(this, 4));

        if (mListSelectedContact.size() > 0) {
            tvParticipantCount.setText(getResources().getString(R.string.participants) + " : " + mListSelectedContact.size());
            selectedContactListAdapter = new SelectedContactListAdapter(this, mListSelectedContact, true);
            rvCreateGroup.setAdapter(selectedContactListAdapter);
        }

        fabCreateGroup.setOnClickListener(this);
        imgGroupIcon.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_group_icon) {
            imagePicker();
        } else if (i == R.id.fab_create_group) {
            if (TextUtils.isEmpty(etGroupSubject.getText().toString())) {
                myCenterToast(this, getResources().getString(R.string.group_subject_content));
            } else {
                createGroup();
            }
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
                Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(resultUri).into(imgGroupIcon);
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
                        groupIcon = response.body().getResult().getUrl();
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
            snackbar(this, fabCreateGroup, BaseClass.NO_INTERNET);
        }
    }

    private void createGroup() {

        showSimpleProgressDialog(this);

        DatabaseReference refUserMsg = FirebaseDatabase.getInstance().getReference("user-messages");
        DatabaseReference refGroupMember = FirebaseDatabase.getInstance().getReference("groups");
        String mGroupId = refGroupMember.push().getKey();

        refGroupMember.child(mGroupId).child("members").child(UserId(this)).setValue("1");
        refUserMsg.child(UserId(this)).child(mGroupId).setValue("1");

        for (int i = 0; i < mListSelectedContact.size(); i++) {
            refGroupMember.child(mGroupId).child("members").child(mListSelectedContact.get(i).getId()).setValue("0");
            refUserMsg.child(mListSelectedContact.get(i).getId()).child(mGroupId).setValue("1");
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("group-id", mGroupId);
        hashMap.put("group-name", etGroupSubject.getText().toString());
        hashMap.put("group-pic-url", groupIcon);
        hashMap.put("group-description", "");

        refGroupMember.child(mGroupId).child("group-details").setValue(hashMap);

        sendMsg(mGroupId);
    }

    private void sendMsg(String mGroupId) {

        DatabaseReference refGrpMsg = FirebaseDatabase.getInstance().getReference("group-messages").child(mGroupId);
        String mMsgId = refGrpMsg.push().getKey();

        ChatPojo chatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText("created group " + "\"" + etGroupSubject.getText().toString() + "\"");
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(mGroupId);
        chatPojo.setMsgType("10");
        chatPojo.setIsRead("0");
        chatPojo.setFileUrl("null");

        refGrpMsg.child(mMsgId).setValue(chatPojo);

        removeProgressDialog();

        startActivity(new Intent(this, MainActivity.class));
        myCenterToast(this, getResources().getString(R.string.group_created_successfully));
    }

}
