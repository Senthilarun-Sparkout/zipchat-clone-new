package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.zipchat.clone.Adapter.MediaAdapter;
import com.chat.zipchat.clone.Common.App;
import com.chat.zipchat.clone.Model.ChatPojo;
import com.chat.zipchat.clone.Model.ChatPojoDao;
import com.chat.zipchat.clone.R;

import java.util.ArrayList;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.myLog;

@SuppressLint("ValidFragment")
public class DocumentFragment extends Fragment {

    RecyclerView mRecyclerDocuments;
    TextView mTxtNoDocuments;
    String toId;
    List<ChatPojo> chatPojoList;
    ArrayList<String> mediaPojos = new ArrayList<>();
    MediaAdapter mediaAdapter;

    public DocumentFragment(String toId) {
        this.toId = toId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_document, container, false);

        mTxtNoDocuments = view.findViewById(R.id.mTxtNoDocuments);
        mRecyclerDocuments = view.findViewById(R.id.mRecyclerDocuments);

        mRecyclerDocuments.setLayoutManager(new LinearLayoutManager(getActivity()));

        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder()
                .where(ChatPojoDao.Properties.FriendId.eq(toId))
                .where(ChatPojoDao.Properties.MsgType.eq("5")).list();

        myLog("chatPojoList", String.valueOf(chatPojoList.size()));

        load_document_files();

        return view;
    }

    private void load_document_files() {

        mediaPojos.clear();

        for (int i = 0; i < chatPojoList.size(); i++) {
            mediaPojos.add(chatPojoList.get(i).getFileUrl());
        }

        if (mediaPojos.size() > 0) {

            mRecyclerDocuments.setVisibility(View.VISIBLE);
            mTxtNoDocuments.setVisibility(View.GONE);

            mediaAdapter = new MediaAdapter(getActivity(), mediaPojos, 3);
            mRecyclerDocuments.setAdapter(mediaAdapter);
        } else {
            mRecyclerDocuments.setVisibility(View.GONE);
            mTxtNoDocuments.setVisibility(View.VISIBLE);
        }

    }

}
