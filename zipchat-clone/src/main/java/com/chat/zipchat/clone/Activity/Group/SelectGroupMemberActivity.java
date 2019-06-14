package com.chat.zipchat.clone.Activity.Group;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.GroupsDao;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.GroupMember;
import com.chat.zipchat.clone.Model.Groups;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.chat.zipchat.clone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.myCenterToast;

public class SelectGroupMemberActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbarMemberSelect;

    RecyclerView rvSelectedContact, rvContacts;
    GroupContactListAdapter contactListAdapter;
    SelectedContactListAdapter selectedContactListAdapter;
    FloatingActionButton fabSelectMember;

    List<ResultItem> mListContact, mListSelectedContact;

    boolean isFromMain = true;
    String toId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_member);

        toolbarMemberSelect = findViewById(R.id.toolbar_member_select);
        setSupportActionBar(toolbarMemberSelect);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarMemberSelect.setTitleTextColor(getResources().getColor(R.color.white));
        toolbarMemberSelect.setSubtitleTextColor(getResources().getColor(R.color.white));
        toolbarMemberSelect.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        isFromMain = getIntent().getBooleanExtra("isFromMain", true);

        if (isFromMain) {
            getSupportActionBar().setTitle(getResources().getString(R.string.new_group));
            getSupportActionBar().setSubtitle(getResources().getString(R.string.add_participant));
        } else {
            toId = getIntent().getStringExtra("groupId");
            getSupportActionBar().setTitle(getResources().getString(R.string.add_participant));
        }

        rvSelectedContact = findViewById(R.id.rv_selected_contact);
        rvContacts = findViewById(R.id.rv_contacts);
        fabSelectMember = findViewById(R.id.fab_select_member);
        TextView tvNoContacts = findViewById(R.id.tv_no_contacts);
        fabSelectMember.setOnClickListener(this);

        mListContact = new ArrayList<>();
        mListSelectedContact = new ArrayList<>();

        mListContact = App.getmInstance().resultItemDao.queryBuilder().where(ResultItemDao.Properties.IsFromContact.eq("1")).list();

        if (!isFromMain) {
            List<Groups> groupsList = App.getmInstance().groupsDao.queryBuilder().where(
                    GroupsDao.Properties.Group_id.eq(toId)).list();

            if (groupsList.size() > 0) {
                List<GroupMember> mGroupMember = groupsList.get(0).getGroupMember();
                for (int i = 0; i < mGroupMember.size(); i++) {
                    for (int j = 0; j < mListContact.size(); j++) {
                        if (mGroupMember.get(i).getId().equals(mListContact.get(j).getId())) {
                            mListContact.remove(j);
                        }
                    }
                }
            }
        }

        if (mListContact.size() > 0) {
            contactListAdapter = new GroupContactListAdapter(this, mListContact);
            rvContacts.setAdapter(contactListAdapter);
        } else {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

    }

    public void getSelectedList(ResultItem contactPojo) {

        mListSelectedContact.add(contactPojo);

        if (mListSelectedContact.size() > 0) {
            selectedContactListAdapter = new SelectedContactListAdapter(this, mListSelectedContact, false);
            rvSelectedContact.setAdapter(selectedContactListAdapter);

            getSupportActionBar().setSubtitle(mListSelectedContact.size() + " of " + mListContact.size() + " selected");

        } else {
            getSupportActionBar().setSubtitle(getResources().getString(R.string.add_participant));
        }

        contactPojo.setIsSelected(true);
        contactListAdapter.notifyDataSetChanged();

    }

    public void removeSelectedList(ResultItem contactPojo) {

        mListSelectedContact.remove(contactPojo);
        selectedContactListAdapter.notifyDataSetChanged();

        if (mListSelectedContact.size() == 0) {
            getSupportActionBar().setSubtitle(getResources().getString(R.string.add_participant));
        }

        contactPojo.setIsSelected(false);
        contactListAdapter.notifyDataSetChanged();
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

        searchView.setQueryHint(getResources().getString(R.string.search_member));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (mListContact.size() > 0) {
                    contactListAdapter.getFilter().filter(newText);
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

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fab_select_member) {
            if (mListSelectedContact.size() == 0) {
                myCenterToast(this, getResources().getString(R.string.minimum_member));
            } else if (isFromMain) {
                Intent intent = new Intent(this, CreateGroupActivity.class);
                intent.putExtra("selectedMembers", (Serializable) mListSelectedContact);
                startActivity(intent);
            } else {
                DatabaseReference refUserMsg = FirebaseDatabase.getInstance().getReference("user-messages");
                DatabaseReference refGroupMember = FirebaseDatabase.getInstance().getReference("groups");
                refGroupMember.child(toId).child("members").child(UserId(this)).setValue("1");
                refUserMsg.child(UserId(this)).child(toId).setValue("1");

                for (int i = 0; i < mListSelectedContact.size(); i++) {
                    refGroupMember.child(toId).child("members").child(mListSelectedContact.get(i).getId()).setValue("0");
                    refUserMsg.child(mListSelectedContact.get(i).getId()).child(toId).setValue("1");
                }
                finish();
            }
        }
    }

}
