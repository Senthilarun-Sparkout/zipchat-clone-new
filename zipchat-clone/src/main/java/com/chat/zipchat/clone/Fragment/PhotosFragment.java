package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.chat.zipchat.clone.Common.BaseClass.PhotoDirectoryPath;

@SuppressLint("ValidFragment")
public class PhotosFragment extends Fragment {

    RecyclerView mRecyclerPhotos;
    TextView mTxtNoPhotos;
    String toId;
    MediaAdapter mediaAdapter;
    ArrayList<String> mediaPojos = new ArrayList<>();
    List<ChatPojo> chatPojoList;

    public PhotosFragment(String toId) {
        this.toId = toId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        mTxtNoPhotos = view.findViewById(R.id.mTxtNoPhotos);
        mRecyclerPhotos = view.findViewById(R.id.mRecyclerPhotos);
        mRecyclerPhotos.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder()
                .where(ChatPojoDao.Properties.FriendId.eq(toId))
                .where(ChatPojoDao.Properties.MsgType.eq("2")).list();
        File file = new File(PhotoDirectoryPath);
        load_image_files(file);

        return view;
    }

    private void load_image_files(File dir) {

        File[] listFile = dir.listFiles();
        mediaPojos.clear();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    load_image_files(listFile[i]);
                } else {
                    for (int j = 0; j < chatPojoList.size(); j++) {
                        if (listFile[i].getName().contains(chatPojoList.get(j).getMessageId())) {
                            mediaPojos.add(listFile[i].getAbsolutePath());
                        }
                    }
                }
            }

            if (mediaPojos.size() > 0) {

                mRecyclerPhotos.setVisibility(View.VISIBLE);
                mTxtNoPhotos.setVisibility(View.GONE);

                mediaAdapter = new MediaAdapter(getActivity(), mediaPojos, 1);
                mRecyclerPhotos.setAdapter(mediaAdapter);
            } else {
                mRecyclerPhotos.setVisibility(View.GONE);
                mTxtNoPhotos.setVisibility(View.VISIBLE);
            }
        }
    }

}
