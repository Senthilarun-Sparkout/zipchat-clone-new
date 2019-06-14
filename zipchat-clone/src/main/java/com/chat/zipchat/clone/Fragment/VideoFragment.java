package com.chat.zipchat.clone.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import static com.chat.zipchat.clone.Common.BaseClass.VideoDirectoryPath;

@SuppressLint("ValidFragment")
public class VideoFragment extends Fragment {

    RecyclerView mRecyclerVideos;
    TextView mTxtNoVideos;
    String toId;
    MediaAdapter mediaAdapter;
    ArrayList<String> mediaPojos = new ArrayList<>();
    List<ChatPojo> chatPojoList;

    public VideoFragment(String toId) {
        this.toId = toId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        mTxtNoVideos = view.findViewById(R.id.mTxtNoVideos);
        mRecyclerVideos = view.findViewById(R.id.mRecyclerVideos);
        mRecyclerVideos.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder()
                .where(ChatPojoDao.Properties.FriendId.eq(toId))
                .where(ChatPojoDao.Properties.MsgType.eq("4")).list();

        File file = new File(VideoDirectoryPath);
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
                        if (chatPojoList.get(j).getMessageId().contains(listFile[i].getName())) {
                            mediaPojos.add(listFile[i].getAbsolutePath());
                        }
                    }
                }
            }

            if (mediaPojos.size() > 0) {

                mRecyclerVideos.setVisibility(View.VISIBLE);
                mTxtNoVideos.setVisibility(View.GONE);

                mediaAdapter = new MediaAdapter(getActivity(), mediaPojos, 2);
                mRecyclerVideos.setAdapter(mediaAdapter);
            } else {
                mRecyclerVideos.setVisibility(View.GONE);
                mTxtNoVideos.setVisibility(View.VISIBLE);
            }
        }
    }

}
