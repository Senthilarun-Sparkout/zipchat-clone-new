package com.chat.zipchat.clone.Activity.Group;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.clone.Adapter.GroupMembersAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Common.BaseClass;
import com.chat.zipchat.clone.Model.Chat.ChatPojo;
import com.chat.zipchat.clone.Model.Group.GroupItems;
import com.chat.zipchat.clone.Model.Group.GroupItemsDao;
import com.chat.zipchat.clone.Model.Group.GroupMember;
import com.chat.zipchat.clone.Model.Group.GroupMemberDao;
import com.chat.zipchat.clone.Model.Group.Groups;
import com.chat.zipchat.clone.Model.Group.GroupsDao;
import com.chat.zipchat.clone.Model.ProfileImageUpdate.ProfileImageResponse;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.greendao.query.DeleteQuery;

import java.io.File;
import java.util.HashMap;
import java.util.List;

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
import static com.chat.zipchat.clone.Common.BaseClass.myToast;
import static com.chat.zipchat.clone.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.requestOptionsD;
import static com.chat.zipchat.clone.Common.BaseClass.showSimpleProgressDialog;
import static com.chat.zipchat.clone.Common.BaseClass.snackbar;
import static okhttp3.MediaType.parse;

public class GroupInfoActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    boolean isShow = true;
    int scrollRange = -1;

    private ImageView headerGroupIcon;
    private LinearLayout llAddParticipants;
    private CollapsingToolbarLayout collapsingGroupInfo;
    private MenuItem itemEditGroup, itemAddParticipants;

    private String toId;
    private List<GroupItems> groupItems;
    private List<Groups> groupsList;
    private List<GroupMember> myListData;

    private Dialog changeGrpSubDialog;

    private List<GroupMember> mGroupMember;
    private TextView tvParticipantCount;
    private RecyclerView rvGroupMembers;
    private GroupMembersAdapter groupMembersAdapter;

    TextView tvAddDescGroupInfo, tvDescGroupInfo;
    LinearLayout llDescGroupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        Toolbar toolbarGroupName = findViewById(R.id.toolbar_group_name);
        setSupportActionBar(toolbarGroupName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarGroupName.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        collapsingGroupInfo = findViewById(R.id.collapsing_group_info);
        AppBarLayout appbarGroupInfo = findViewById(R.id.appbar_group_info);
        appbarGroupInfo.addOnOffsetChangedListener(this);

        ImageView imgGroupNameEdit = findViewById(R.id.img_group_name_edit);
        headerGroupIcon = findViewById(R.id.header_group_icon);
        tvParticipantCount = findViewById(R.id.tv_participant_count);
        rvGroupMembers = findViewById(R.id.rv_group_members);
        llAddParticipants = findViewById(R.id.ll_add_participants);
        llAddParticipants.setOnClickListener(this);
        imgGroupNameEdit.setOnClickListener(this);
        headerGroupIcon.setOnClickListener(this);

        tvAddDescGroupInfo = findViewById(R.id.tv_add_desc_group_info);
        tvDescGroupInfo = findViewById(R.id.tv_desc_group_info);
        llDescGroupInfo = findViewById(R.id.ll_desc_group_info);
        tvAddDescGroupInfo.setOnClickListener(this);
        tvDescGroupInfo.setOnClickListener(this);

        toId = getIntent().getStringExtra("toId");

        groupItems = App.getmInstance().groupItemsDao.queryBuilder().where(GroupItemsDao.Properties.Id.eq(toId)).list();
        Glide.with(this).setDefaultRequestOptions(requestOptionsD()).load(groupItems.get(0).getGroup_picture()).into(headerGroupIcon);
        collapsingGroupInfo.setTitle(groupItems.get(0).getName());

        if (TextUtils.isEmpty(groupItems.get(0).getDescription())) {
            tvAddDescGroupInfo.setVisibility(View.VISIBLE);
            llDescGroupInfo.setVisibility(View.GONE);
        } else {
            tvAddDescGroupInfo.setVisibility(View.GONE);
            llDescGroupInfo.setVisibility(View.VISIBLE);
            tvDescGroupInfo.setText(groupItems.get(0).getDescription());
        }

        groupsList = App.getmInstance().groupsDao.queryBuilder().where(
                GroupsDao.Properties.Group_id.eq(toId)).list();

        if (groupsList.size() > 0) {
            mGroupMember = groupsList.get(0).getGroupMember();
            tvParticipantCount.setText(mGroupMember.size() + " " + getResources().getString(R.string.participants));
            groupMembersAdapter = new GroupMembersAdapter(this, mGroupMember);
            rvGroupMembers.setAdapter(groupMembersAdapter);
        }

        myListData = App.getmInstance().groupMemberDao.queryBuilder().where(
                GroupMemberDao.Properties.Grp_id.eq(groupsList.get(0).getGrp_id()))
                .where(GroupMemberDao.Properties.Id.eq(UserId(this))).list();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        syncMembersDetails();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        if (scrollRange + verticalOffset == 0) {
            isShow = true;
            if (itemEditGroup != null) {
                itemEditGroup.setVisible(true);
            }
        } else if (isShow) {
            isShow = false;
            if (itemEditGroup != null) {
                itemEditGroup.setVisible(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_group_info, menu);
        itemEditGroup = menu.findItem(R.id.item_edit_group);
        itemAddParticipants = menu.findItem(R.id.item_add_participants);

        if (myListData.size() > 0) {

            if (myListData.get(0).getIsAdmin().equals("1")) {
                if (null != itemAddParticipants) {
                    itemAddParticipants.setVisible(true);
                }
                llAddParticipants.setVisibility(View.VISIBLE);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.item_edit_group) {
            changeGrpSubDialog(1);
        } else if (i == R.id.item_add_participants) {
            GroupMemberActivityCall();
        }

        return true;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.ll_add_participants) {
            GroupMemberActivityCall();
        } else if (i == R.id.img_group_name_edit) {
            changeGrpSubDialog(1);
        } else if (i == R.id.header_group_icon) {
            imagePicker();
        } else if (i == R.id.tv_add_desc_group_info) {
            changeGrpSubDialog(2);
        } else if (i == R.id.tv_desc_group_info) {
            changeGrpSubDialog(2);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (null != changeGrpSubDialog && changeGrpSubDialog.isShowing()) {
            changeGrpSubDialog.dismiss();
        } else {
            finish();
        }
    }

    /**
     * fun - Intent to the SelectGroupMemberActivity for add participant
     */
    private void GroupMemberActivityCall() {
        Intent intent = new Intent(this, SelectGroupMemberActivity.class);
        intent.putExtra("isFromMain", false);
        intent.putExtra("groupId", toId);
        startActivity(intent);
    }

    /**
     * fun - to change the group_subject or group_description
     *
     * @param value - 1 (group_name) or 2 (group_description)
     */
    private void changeGrpSubDialog(int value) {
        changeGrpSubDialog = new Dialog(this, R.style.DialogThemeforview_pop);
        changeGrpSubDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changeGrpSubDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        changeGrpSubDialog.setContentView(R.layout.dialog_change_group_subject);
        changeGrpSubDialog.show();

        EditText etDialogGroupSubject = changeGrpSubDialog.findViewById(R.id.et_dialog_group_subject);
        TextView tvDialogGroupDesc = changeGrpSubDialog.findViewById(R.id.tv_dialog_group_desc);
        TextView tvGroupDialogTitle = changeGrpSubDialog.findViewById(R.id.tv_group_dialog_title);
        AppCompatButton btnGroupDialogCancel = changeGrpSubDialog.findViewById(R.id.btn_group_dialog_cancel);
        AppCompatButton btnGroupDialogok = changeGrpSubDialog.findViewById(R.id.btn_group_dialog_ok);

        btnGroupDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGrpSubDialog.dismiss();
            }
        });

        if (value == 1) {
            etDialogGroupSubject.setHint(getResources().getString(R.string.add_subject));
            etDialogGroupSubject.setText(groupItems.get(0).getName());
            tvGroupDialogTitle.setText(getResources().getString(R.string.enter_new_subject));
            tvDialogGroupDesc.setVisibility(View.GONE);
        } else {
            etDialogGroupSubject.setHint(getResources().getString(R.string.add_group_description));
            etDialogGroupSubject.setText(groupItems.get(0).getDescription());
            tvGroupDialogTitle.setText(getResources().getString(R.string.group_description));
            tvDialogGroupDesc.setVisibility(View.VISIBLE);
        }


        btnGroupDialogok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value == 1) {
                    if (TextUtils.isEmpty(etDialogGroupSubject.getText().toString())) {
                        changeGrpSubDialog.dismiss();
                        myCenterToast(GroupInfoActivity.this, GroupInfoActivity.this.getResources().getString(R.string.subject_cant_be_empty));
                    } else {
                        changeGrpSubDialog.dismiss();
                        GroupInfoActivity.this.updateGroupData(etDialogGroupSubject.getText().toString(), null, null);
                    }
                } else {
                    changeGrpSubDialog.dismiss();
                    if (!groupItems.get(0).getDescription().equals(etDialogGroupSubject.getText().toString())) {
                        if (TextUtils.isEmpty(etDialogGroupSubject.getText().toString())) {
                            GroupInfoActivity.this.updateGroupData(null, null, "");
                        } else {
                            GroupInfoActivity.this.updateGroupData(null, null, etDialogGroupSubject.getText().toString());
                        }
                    }
                }
            }
        });

    }

    /**
     * fun - to refresh the group_details after changing group icon, name or description
     */
    private void syncGroupDetails() {

        DatabaseReference referenceGroupDetails = FirebaseDatabase.getInstance().getReference("groups").child(toId).child("group-details");
        referenceGroupDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    GroupItems groupItem = new GroupItems();
                    groupItem.setId(toId);

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.getKey().equals("group-name")) {
                            groupItem.setName(ds.getValue().toString());
                        } else if (ds.getKey().equals("group-pic-url")) {
                            groupItem.setGroup_picture(ds.getValue().toString());
                        } else if (ds.getKey().equals("group-description")) {
                            groupItem.setDescription(ds.getValue().toString());
                        }
                    }

                    App.getmInstance().groupItemsDao.insertOrReplace(groupItem);

                    groupItems = App.getmInstance().groupItemsDao.queryBuilder().where(GroupItemsDao.Properties.Id.eq(toId)).list();
                    Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptionsD()).load(groupItems.get(0).getGroup_picture()).into(headerGroupIcon);
                    collapsingGroupInfo.setTitle(groupItems.get(0).getName());

                    if (TextUtils.isEmpty(groupItems.get(0).getDescription())) {
                        tvAddDescGroupInfo.setVisibility(View.VISIBLE);
                        llDescGroupInfo.setVisibility(View.GONE);
                    } else {
                        tvAddDescGroupInfo.setVisibility(View.GONE);
                        llDescGroupInfo.setVisibility(View.VISIBLE);
                        tvDescGroupInfo.setText(groupItems.get(0).getDescription());
                    }

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * fun - to refresh the group_members after add, remove, make_admin or dismiss_admin the participants
     */
    private void syncMembersDetails() {

        DatabaseReference referenceGroupMenbers = FirebaseDatabase.getInstance().getReference("groups").child(toId).child("members");
        referenceGroupMenbers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final DeleteQuery<GroupMember> tableDeleteQuery = App.getmInstance().groupMemberDao.queryBuilder()
                        .where(GroupMemberDao.Properties.Grp_id.eq(groupsList.get(0).grp_id))
                        .buildDelete();
                tableDeleteQuery.executeDeleteWithoutDetachingEntities();

                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                    DatabaseReference referenceGroupMenbers = FirebaseDatabase.getInstance().getReference("user-details").child(datas.getKey()).child("profile-details");
                    referenceGroupMenbers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            GroupMember groupMember = new GroupMember();
                            groupMember.setId(datas.getKey());
                            groupMember.setIsAdmin(datas.getValue().toString());
                            groupMember.setGrp_id(groupsList.get(0).grp_id);

                            for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                                if (dataSnap.getKey().equals("name")) {
                                    groupMember.setName(dataSnap.getValue().toString());
                                } else if (dataSnap.getKey().equals("mobile-number")) {
                                    groupMember.setMobile_number(dataSnap.getValue().toString());
                                } else if (dataSnap.getKey().equals("profile-url")) {
                                    groupMember.setProfile_picture(dataSnap.getValue().toString());
                                } else if (dataSnap.getKey().equals("status")) {
                                    groupMember.setStatus(dataSnap.getValue().toString());
                                }
                            }

                            List<GroupMember> groupMemberList = App.getmInstance().groupMemberDao.queryBuilder().where(
                                    GroupMemberDao.Properties.Grp_id.eq(groupsList.get(0).grp_id))
                                    .where(GroupMemberDao.Properties.Id.eq(datas.getKey())).list();

                            if (groupMemberList.size() > 0) {
                                App.getmInstance().groupMemberDao.insertOrReplace(groupMember);
                            } else {
                                App.getmInstance().groupMemberDao.insert(groupMember);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                groupsList = App.getmInstance().groupsDao.queryBuilder().where(
                        GroupsDao.Properties.Group_id.eq(toId)).list();

                if (groupsList.size() > 0) {
                    mGroupMember = groupsList.get(0).getGroupMember();
                    tvParticipantCount.setText(mGroupMember.size() + " " + getResources().getString(R.string.participants));
                    groupMembersAdapter = new GroupMembersAdapter(GroupInfoActivity.this, mGroupMember);
                    rvGroupMembers.setAdapter(groupMembersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * fun - to update group_name , group_icon or group_description
     *
     * @param grp_name    - Group Name
     * @param groupIcon   - Group Icon
     * @param description - Group Description
     */
    private void updateGroupData(String grp_name, String groupIcon, String description) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("group-id", toId);

        if (!TextUtils.isEmpty(grp_name)) {
            hashMap.put("group-name", grp_name);
            sendMsg("11", "changed the subject from " + "\"" + groupItems.get(0).getName() + "\"" + " to " + "\"" + grp_name + "\"");
        }

        if (!TextUtils.isEmpty(groupIcon)) {
            hashMap.put("group-pic-url", groupIcon);
            sendMsg("12", groupItems.get(0).getGroup_picture() + " to " + groupIcon);
        }

        if (null != description) {
            hashMap.put("group-description", description);

            if (TextUtils.isEmpty(description)) {
                sendMsg("13", "deleted the group description");
            } else {
                sendMsg("13", "changed the group description");
            }


        }

        DatabaseReference refGroupMember = FirebaseDatabase.getInstance().getReference("groups");
        refGroupMember.child(toId).child("group-details").updateChildren(hashMap);

        syncGroupDetails();
    }

    /**
     * fun - call from GroupMembersAdapter while select popup item
     * To view, message, make_admin, dismiss_admin or remove the selected member.
     *
     * @param item        - selected item
     * @param groupMember - selected member details
     */
    public void GroupMemberPopupAction(MenuItem item, GroupMember groupMember) {

        DatabaseReference refGroupMember = FirebaseDatabase.getInstance().getReference("groups");

        int i = item.getItemId();
        if (i == R.id.nav_message) {
            myToast(this, item.getTitle().toString());
        } else if (i == R.id.nav_view) {
            myToast(this, item.getTitle().toString());
        } else if (i == R.id.nav_make_admin) {
            refGroupMember.child(toId).child("members").child(groupMember.getId()).setValue("1");
            syncMembersDetails();
        } else if (i == R.id.nav_dismiss_admin) {
            refGroupMember.child(toId).child("members").child(groupMember.getId()).setValue("0");
            syncMembersDetails();
        } else if (i == R.id.nav_remove) {
            refGroupMember.child(toId).child("members").child(groupMember.getId()).removeValue();
            DatabaseReference refUserMsg = FirebaseDatabase.getInstance().getReference("user-messages");
            refUserMsg.child(groupMember.getId()).child(toId).removeValue();
            syncMembersDetails();
        }
    }

    private void sendMsg(String msgType, String msgContent) {

        DatabaseReference refGrpMsg = FirebaseDatabase.getInstance().getReference("group-messages").child(toId);
        String mMsgId = refGrpMsg.push().getKey();

        ChatPojo chatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText(msgContent);
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(toId);
        chatPojo.setMsgType(msgType);
        chatPojo.setIsRead("0");
        chatPojo.setFileUrl("null");

        refGrpMsg.child(mMsgId).setValue(chatPojo);
    }

    /**
     * fun - Image picker to change the group icon
     */
    private void imagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_IMAGE);
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String filePath = getRealPathFromURI(this, resultUri);
                UpdateProfileImageService(filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    /**
     * fun - to upload selected group_icon to the local server
     *
     * @param filePath - group icon file path
     */
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
                        updateGroupData(null, response.body().getResult().getUrl(), null);
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
            snackbar(this, llAddParticipants, BaseClass.NO_INTERNET);
        }
    }
}
