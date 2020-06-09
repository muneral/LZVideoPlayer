package com.example.LAndZVideoPlayer.MViewPager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.LAndZVideoPlayer.Adapter.VideoAdapter;
import com.example.LAndZVideoPlayer.R;
import com.example.LAndZVideoPlayer.PlayVideo.VideoPlayerManager;

//Zhu
public class VideoFragment extends Fragment {
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recyclerview_layout, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new VideoAdapter(getContext()));
        return view;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        VideoPlayerManager.getInstance().stop();
    }
}
