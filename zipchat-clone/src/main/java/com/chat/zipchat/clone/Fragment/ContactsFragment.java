package com.chat.zipchat.clone.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.zipchat.clone.Activity.MainActivity;
import com.chat.zipchat.clone.Adapter.ContactListAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ContactItemRequest;
import com.chat.zipchat.clone.Model.ContactRequest;
import com.chat.zipchat.clone.Model.ContactResponse;
import com.chat.zipchat.clone.Model.ResultItem;
import com.chat.zipchat.clone.Model.ResultItemDao;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.clone.Common.BaseClass.OTHER_FRAGMENT;
import static com.chat.zipchat.clone.Common.BaseClass.UserId;
import static com.chat.zipchat.clone.Common.BaseClass.apiInterface;
import static com.chat.zipchat.clone.Common.BaseClass.isOnline;
import static com.chat.zipchat.clone.Common.BaseClass.myLog;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSIONS;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.PERMISSION_ALL;
import static com.chat.zipchat.clone.Common.MarshmallowPermission.hasPermissions;


@SuppressLint("ValidFragment")
public class ContactsFragment extends Fragment implements Filterable {

    MainActivity mContext;
    ContactListAdapter contactAdapter;
    RecyclerView mRecyclerContact;
    RelativeLayout Rl_contact;
    ArrayList<ContactItemRequest> mListContact = new ArrayList<>();
    List<ResultItem> contactResponseList;

    EditText mSearchContact;
    TextView mTxtNoContact;

    public ContactsFragment(MainActivity mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        mTxtNoContact = view.findViewById(R.id.mTxtNoContact);

        Rl_contact = view.findViewById(R.id.Rl_contact);
        mRecyclerContact = view.findViewById(R.id.mRecyclerContact);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerContact.setLayoutManager(mLayoutManager);
        mRecyclerContact.setItemAnimator(new DefaultItemAnimator());
        mRecyclerContact.setHasFixedSize(true);

        contactResponseList = App.getmInstance().resultItemDao.queryBuilder().where(ResultItemDao.Properties.IsFromContact.eq("1")).list();

        contactAdapter = new ContactListAdapter(mContext, contactResponseList);
        mRecyclerContact.setAdapter(contactAdapter);

        if (contactResponseList.size() > 0) {

            mRecyclerContact.setVisibility(View.VISIBLE);
            mTxtNoContact.setVisibility(View.GONE);

        } else {

            mRecyclerContact.setVisibility(View.GONE);
            mTxtNoContact.setVisibility(View.VISIBLE);
        }


        if (!hasPermissions(mContext)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        } else {
            GetContactsIntoArrayList();
        }

        mSearchContact = view.findViewById(R.id.mSearchContact);

        mSearchContact.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (contactResponseList.size() > 0) {
                    contactAdapter.getFilter().filter(s.toString());
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(Manifest.permission.READ_CONTACTS)) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    GetContactsIntoArrayList();
                    return;
                }
            }
        }

    }

    public void GetContactsIntoArrayList() {

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        } catch (SecurityException e) {
            //SecurityException can be thrown if we don't have the right permissions
        }


        if (cursor != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                mListContact.clear();
                while (cursor.moveToNext()) {

                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);

                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {

                        String displayName = cursor.getString(indexOfDisplayName);
                        String displayNumber = cursor.getString(indexOfDisplayNumber);
                        String newDisplayNumber = displayNumber.replaceAll("[^0-9]", "");

                        ContactItemRequest contactItem = new ContactItemRequest();
                        contactItem.setName(displayName);
                        contactItem.setMobileNumber(newDisplayNumber);
                        mListContact.add(contactItem);

                        //haven't seen this number yet: do something with this contact!
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
                SyncContact();

            } finally {
                cursor.close();
            }
        }
    }

    private void SyncContact() {

        if (isOnline(mContext)) {

            ContactRequest contactRequest = new ContactRequest();
            contactRequest.setContact(mListContact);
            contactRequest.setDeviceType("ANDROID");

            Call<ContactResponse> contactResponseCall = apiInterface.contactDetails(contactRequest);
            contactResponseCall.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, final Response<ContactResponse> response) {

                    if (response.isSuccessful()) {

                        contactResponseList.clear();
                        App.getmInstance().contactResponseDao.deleteAll();
                        App.getmInstance().resultItemDao.deleteAll();

                        if (response.body() != null) {
                            ContactResponse contactResponse = response.body();
                            contactResponse.contact_id = 1L;
                            App.getmInstance().contactResponseDao.insertOrReplace(contactResponse);

                            if (response.body().getResult().size() > 0) {

                                for (ResultItem con : contactResponse.getResult()) {
                                    if (!con.getId().equalsIgnoreCase(UserId(mContext))) {
                                        con.setContact_id(contactResponse.contact_id);
                                        con.setIsFromContact("1");
                                        con.setIsSelected(false);
                                        App.getmInstance().resultItemDao.insertOrReplace(con);
                                    }

                                }

                            }

                            contactResponseList = App.getmInstance().resultItemDao.queryBuilder().list();

                            if (contactResponseList.size() > 0) {

                                mRecyclerContact.setVisibility(View.VISIBLE);
                                mTxtNoContact.setVisibility(View.GONE);

                                contactAdapter.updateContacttList(contactResponseList);

                            } else {

                                mRecyclerContact.setVisibility(View.GONE);
                                mTxtNoContact.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    myLog("onFailure: ", t.toString());
                }
            });

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
               /* FilterResults results = new FilterResults();
                ArrayList<String> lists = new ArrayList<>();
                if (!TextUtils.isEmpty(charSequence)) {

                    for (String temp : searchTipsArray) {

                        if (temp.toLowerCase().contains(charSequence)) {
                            lists.add(temp);
                        }
                    }
                    newList = lists;
                } else {
                    // newList = searchTipsArray;
                }
                results.values = newList;
                results.count = newList.size();*/

                return null;

            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null) {
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext.currentFragment = OTHER_FRAGMENT;
    }

}
