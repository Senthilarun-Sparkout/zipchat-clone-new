package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.zipchat.clone.Adapter.ChatListAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.AcceptRejectPojo;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojo;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojoDao;
import com.chat.zipchat.clone.Model.Contact.ContactResponse;
import com.chat.zipchat.clone.Model.Contact.ResultItem;
import com.chat.zipchat.clone.Model.Contact.ResultItemDao;
import com.chat.zipchat.clone.Model.Group.GroupItems;
import com.chat.zipchat.clone.Model.Group.GroupMember;
import com.chat.zipchat.clone.Model.Group.GroupMemberDao;
import com.chat.zipchat.clone.Model.Group.Groups;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;


@SuppressLint("ValidFragment")
public class ChatFragment extends Fragment {

    Context mContext;
    RecyclerView mRecyclerChat;
    private DatabaseReference referenceContact, referenceFriendList;
    List<ChatListPojo> chatPojoList;
    ChatListAdapter chatListAdapter;
    TextView mTxtNoChat;

    public ChatFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mTxtNoChat = view.findViewById(R.id.mTxtNoChat);


        mRecyclerChat = view.findViewById(R.id.mRcyclerChat);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerChat.setLayoutManager(mLayoutManager);
        mRecyclerChat.setItemAnimator(new DefaultItemAnimator());
        mRecyclerChat.setHasFixedSize(true);

        chatPojoList = new ArrayList<>();
        chatPojoList = App.getmInstance().chatListPojoDao.queryBuilder().orderAsc(ChatListPojoDao.Properties.Timestamp).list();

        Collections.sort(chatPojoList, new Comparator<ChatListPojo>() {
            @Override
            public int compare(ChatListPojo o1, ChatListPojo o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });

        chatListAdapter = new ChatListAdapter(mContext, chatPojoList);
        mRecyclerChat.setAdapter(chatListAdapter);

        return view;
    }

    ValueEventListener valueEventListenerContact = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                mRecyclerChat.setVisibility(View.VISIBLE);
                mTxtNoChat.setVisibility(View.GONE);

                for (final DataSnapshot ds : dataSnapshot.getChildren()) {

//                    Hide by Arun on 07-01-2019
//                    chatPojoList.clear();

                    if (ds.getValue().equals("1")) {

                        DatabaseReference referenceGroupDetails = FirebaseDatabase.getInstance().getReference("groups").child(ds.getKey()).child("group-details");
                        referenceGroupDetails.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    GroupItems groupItem = new GroupItems();
                                    groupItem.setId(ds.getKey());

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
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Groups groups = new Groups();
                        groups.setGroup_id(ds.getKey());
                        App.getmInstance().groupsDao.insertOrReplace(groups);

                        final DeleteQuery<GroupMember> tableDeleteQuery = App.getmInstance().groupMemberDao.queryBuilder()
                                .where(GroupMemberDao.Properties.Grp_id.eq(groups.grp_id))
                                .buildDelete();
                        tableDeleteQuery.executeDeleteWithoutDetachingEntities();

                        DatabaseReference referenceGroupMenbers = FirebaseDatabase.getInstance().getReference("groups").child(ds.getKey()).child("members");
                        referenceGroupMenbers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                                    DatabaseReference referenceGroupMenbers = FirebaseDatabase.getInstance().getReference("user-details").child(datas.getKey()).child("profile-details");
                                    referenceGroupMenbers.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            GroupMember groupMember = new GroupMember();
                                            groupMember.setId(datas.getKey());
                                            groupMember.setIsAdmin(datas.getValue().toString());
                                            groupMember.setGrp_id(groups.grp_id);

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
                                                    GroupMemberDao.Properties.Grp_id.eq(groups.grp_id))
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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        DatabaseReference referenceGroupMsg = FirebaseDatabase.getInstance().getReference("group-messages").child(ds.getKey());
                        referenceGroupMsg.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                                        DatabaseReference referenceGetMsg = FirebaseDatabase.getInstance().getReference("group-messages").child(ds.getKey()).child(child.getKey());
                                        referenceGetMsg.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                ChatListPojo chatListPojo = new ChatListPojo();
                                                String text = "";
                                                Integer msgType = 0;
                                                chatListPojo.setToId(ds.getKey());
                                                chatListPojo.setIsGroup(true);

                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                    if (ds.getKey().equals("text")) {
                                                        text = ds.getValue().toString();
                                                    } else if (ds.getKey().equals("msgType")) {
                                                        msgType = Integer.parseInt(ds.getValue().toString());
                                                    } else if (ds.getKey().equals("timestamp")) {
                                                        chatListPojo.setTimestamp(ds.getValue().toString());
                                                    }
                                                }

                                                if (msgType == 1 || msgType == 10 || msgType == 11 || msgType == 13) {
                                                    chatListPojo.setText(text);
                                                } else if (msgType == 2) {
                                                    chatListPojo.setText("Photo");
                                                } else if (msgType == 3) {
                                                    chatListPojo.setText("Audio");
                                                } else if (msgType == 4) {
                                                    chatListPojo.setText("Video");
                                                } else if (msgType == 5) {
                                                    chatListPojo.setText("Document");
                                                } else if (msgType == 6) {
                                                    chatListPojo.setText("Payment");
                                                } else if (msgType == 7) {
                                                    chatListPojo.setText("Gif");
                                                } else if (msgType == 8) {
                                                    chatListPojo.setText("Stickers");
                                                } else if (msgType == 9) {
                                                    chatListPojo.setText("Location");
                                                } else if (msgType == 12) {
                                                    chatListPojo.setText("Changed this group's icon");
                                                }

                                                App.getmInstance().chatListPojoDao.insertOrReplace(chatListPojo);

                                                chatPojoList = App.getmInstance().chatListPojoDao.queryBuilder().orderAsc(ChatListPojoDao.Properties.Timestamp).list();

                                                Collections.sort(chatPojoList, new Comparator<ChatListPojo>() {
                                                    @Override
                                                    public int compare(ChatListPojo o1, ChatListPojo o2) {
                                                        return o2.getTimestamp().compareTo(o1.getTimestamp());
                                                    }
                                                });
                                                chatListAdapter.updateFragChatList(chatPojoList);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //Handle possible errors.
                            }
                        });

                    } else {
                        List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(ResultItemDao.Properties.Id.eq(ds.getKey())).list();

                        if (resultItems.size() == 0) {
                            DatabaseReference referenceContactDetails = FirebaseDatabase.getInstance().getReference("user-details").child(ds.getKey()).child("profile-details");
                            referenceContactDetails.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        ResultItem resultItem = new ResultItem();
                                        resultItem.setId(ds.getKey());
                                        resultItem.setIsFromContact("0");

                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                            if (ds.getKey().equals("name")) {
                                                resultItem.setName(ds.getValue().toString());
                                            } else if (ds.getKey().equals("mobile-number")) {
                                                resultItem.setMobile_number(ds.getValue().toString());
                                            } else if (ds.getKey().equals("profile-url")) {
                                                resultItem.setProfile_picture(ds.getValue().toString());
                                            } else if (ds.getKey().equals("status")) {
                                                resultItem.setStatus(ds.getValue().toString());
                                            }
                                        }

                                        List<ContactResponse> contactResponseList = App.getmInstance().contactResponseDao.queryBuilder().list();

                                        if (contactResponseList.size() < 0) {
                                            resultItem.setContact_id(contactResponseList.get(0).contact_id);
                                        }
                                        App.getmInstance().resultItemDao.insertOrReplace(resultItem);
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }


                        DatabaseReference ref = referenceContact.child(ds.getKey());
                        ref.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {

                                    DatabaseReference referenceMessage = FirebaseDatabase.getInstance().getReference("messages").child(child.getKey());
                                    referenceMessage.addValueEventListener(valueEventListenerMessage);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //Handle possible errors.
                            }
                        });
                    }
                }
            } else {
                App.getmInstance().chatListPojoDao.deleteAll();
                App.getmInstance().chatPojoDao.deleteAll();
                App.getmInstance().groupItemsDao.deleteAll();
                mTxtNoChat.setVisibility(View.VISIBLE);
                mRecyclerChat.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }
    };

    ValueEventListener valueEventListenerMessage = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                String toId = "";

                ChatListPojo chatListPojo = new ChatListPojo();
                String text = "";
                Integer msgType = 0;
                chatListPojo.setIsGroup(false);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals("text")) {
                        text = ds.getValue().toString();
                    } else if (ds.getKey().equals("msgType")) {
                        msgType = Integer.parseInt(ds.getValue().toString());
                    } else if (ds.getKey().equals("toId") || ds.getKey().equals("fromId")) {

                        if (ds.getKey().equals("toId")) {
                            toId = ds.getValue().toString();
                        }

                        if (ds.getKey().equals("fromId") && !ds.getValue().equals(UserId(mContext))) {
                            chatListPojo.setToId(ds.getValue().toString());
                        } else if (ds.getKey().equals("toId") && !ds.getValue().equals(UserId(mContext))) {
                            chatListPojo.setToId(ds.getValue().toString());
                        }

                    } else if (ds.getKey().equals("timestamp")) {
                        chatListPojo.setTimestamp(ds.getValue().toString());
                    }
                }

                if (msgType == 1) {
                    chatListPojo.setText(text);
                } else if (msgType == 2) {
                    chatListPojo.setText("Photo");
                } else if (msgType == 3) {
                    chatListPojo.setText("Audio");
                } else if (msgType == 4) {
                    chatListPojo.setText("Video");
                } else if (msgType == 5) {
                    chatListPojo.setText("Document");
                } else if (msgType == 6) {
                    chatListPojo.setText("Payment");
                } else if (msgType == 7) {
                    chatListPojo.setText("Gif");
                } else if (msgType == 8) {
                    chatListPojo.setText("Stickers");
                } else if (msgType == 9) {
                    chatListPojo.setText("Location");
                } else if (msgType == 21) {
                    if (!toId.equals(UserId(mContext))) {
                        chatListPojo.setText(mContext.getString(R.string.you_sent_friend_request_to));
                    } else {
                        chatListPojo.setText(mContext.getString(R.string.you_received_friend_request_to));
                    }
                } else if (msgType == 22) {
                    if (!toId.equals(UserId(mContext))) {
                        chatListPojo.setText(mContext.getString(R.string.your_friend_request_rejected_by));
                    } else {
                        chatListPojo.setText(mContext.getString(R.string.you_have_rejected));
                    }
                } else if (msgType == 23) {
                    if (!toId.equals(UserId(mContext))) {
                        chatListPojo.setText(mContext.getString(R.string.your_friend_request_accepted_by));
                    } else {
                        chatListPojo.setText(mContext.getString(R.string.you_have_accepted));
                    }
                }

                App.getmInstance().chatListPojoDao.insertOrReplace(chatListPojo);

                chatPojoList = App.getmInstance().chatListPojoDao.queryBuilder().orderAsc(ChatListPojoDao.Properties.Timestamp).list();

                Collections.sort(chatPojoList, new Comparator<ChatListPojo>() {
                    @Override
                    public int compare(ChatListPojo o1, ChatListPojo o2) {
                        return o2.getTimestamp().compareTo(o1.getTimestamp());
                    }
                });

//                    Hide by Arun on 07-01-2019
              /*  chatListAdapter = new ChatListAdapter(mContext, chatPojoList);
                mRcyclerChat.setAdapter(chatListAdapter);*/
//                chatListAdapter.notifyDataSetChanged();

                chatListAdapter.updateFragChatList(chatPojoList);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }

    };

    ValueEventListener valueEventListenerFriendList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    DatabaseReference referenceFriendList = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(mContext)).child("friend-list").child(ds.getKey());
                    referenceFriendList.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            AcceptRejectPojo acceptRejectPojo = new AcceptRejectPojo();
                            acceptRejectPojo.setFriendId(dataSnapshot.getKey());

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.getKey().equals("status")) {
                                    acceptRejectPojo.setStatus(String.valueOf(ds.getValue()));
                                } else if (ds.getKey().equals("send_request_count")) {
                                    acceptRejectPojo.setSend_request_count(String.valueOf(ds.getValue()));
                                } else if (ds.getKey().equals("receive_request_count")) {
                                    acceptRejectPojo.setReceive_request_count(String.valueOf(ds.getValue()));
                                }
                            }
                            App.getmInstance().acceptRejectPojoDao.insertOrReplace(acceptRejectPojo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                App.getmInstance().acceptRejectPojoDao.deleteAll();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        referenceContact = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(mContext));
        referenceContact.addValueEventListener(valueEventListenerContact);

        referenceFriendList = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(mContext)).child("friend-list");
        referenceFriendList.addValueEventListener(valueEventListenerFriendList);
    }

}
