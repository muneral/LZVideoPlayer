package com.example.LAndZVideoPlayer.MViewPager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.LAndZVideoPlayer.Adapter.FullVideoAdapter;
import com.example.LAndZVideoPlayer.R;
import com.example.LAndZVideoPlayer.PlayVideo.VideoPlayerManager;

//Liu
public class FullFragment extends Fragment {
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_full_video_list, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(new FullVideoAdapter(getContext()));
        return view;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        VideoPlayerManager.getInstance().stop();
    }
}
