package com.chat.zipchat.clone.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.chat.zipchat.clone.Adapter.ForwardAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.Chat.ChatPojo;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojo;
import com.chat.zipchat.clone.Model.ChatList.ChatListPojoDao;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;

public class ForwardActivity extends AppCompatActivity {

    private RecyclerView rvMessageForward;
    private List<ChatListPojo> chatPojoList;
    ForwardAdapter forwardAdapter;
    ChatPojo chatPojo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);

        Toolbar toolbarMessageForward = findViewById(R.id.toolbar_message_forward);
        setSupportActionBar(toolbarMessageForward);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.forward_to));
        toolbarMessageForward.setTitleTextColor(getResources().getColor(R.color.white));
        toolbarMessageForward.setSubtitleTextColor(getResources().getColor(R.color.white));
        toolbarMessageForward.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        rvMessageForward = findViewById(R.id.rv_message_forward);
        rvMessageForward.setLayoutManager(new LinearLayoutManager(this));

        chatPojoList = new ArrayList<>();
        chatPojoList = App.getmInstance().chatListPojoDao.queryBuilder().orderAsc(ChatListPojoDao.Properties.Timestamp).list();

        chatPojo = (ChatPojo) getIntent().getExtras().getSerializable("messageForward");

        forwardAdapter = new ForwardAdapter(this, chatPojoList);
        rvMessageForward.setAdapter(forwardAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.tab_text));

        ImageView searchClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        searchView.setQueryHint(getResources().getString(R.string.forward_to));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (chatPojoList.size() > 0) {
                    forwardAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    public void selectedContact(ChatListPojo chatListPojo) {

        DatabaseReference referenceMessageInsert, referenceUser, referenceUserInsert;

        String toId = chatListPojo.getToId();
        ChatPojo newChatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText(newChatPojo.getText());
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(newChatPojo.getToId());
        chatPojo.setMsgType(newChatPojo.getMsgType());
        chatPojo.setIsRead(newChatPojo.getIsRead());
        chatPojo.setFileUrl(newChatPojo.getFileUrl());
        chatPojo.setSeenId("");
        chatPojo.setDeliverId("");
        chatPojo.setIsForward(true);

        if (chatListPojo.getIsGroup()) {
            referenceMessageInsert = FirebaseDatabase.getInstance().getReference("group-messages").child(toId);
            String mGroupId = referenceMessageInsert.push().getKey();
            referenceMessageInsert.child(mGroupId).setValue(chatPojo);

        } else {
            referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");
            String mGroupId = referenceMessageInsert.push().getKey();
            referenceMessageInsert.child(mGroupId).setValue(chatPojo);

            referenceUser = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(this));
            referenceUser.child(toId).child(mGroupId).setValue("1");
            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(this));
            referenceUserInsert.child(mGroupId).setValue("1");

        }
    }
}
