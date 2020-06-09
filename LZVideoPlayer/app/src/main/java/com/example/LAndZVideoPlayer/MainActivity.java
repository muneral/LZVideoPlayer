package com.example.LAndZVideoPlayer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.LAndZVideoPlayer.Adapter.TabAdapter;
import com.example.LAndZVideoPlayer.PlayVideo.VideoPlayerManager;
import com.example.LAndZVideoPlayer.PlayVideo.AbsVideoPlayerView;

//Liu
public class MainActivity extends AppCompatActivity {

    AbsVideoPlayerView mPlayerView;
    ViewPager mViewPager;
    TabLayout mTabPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.viewpager_video_tab);
        mTabPageIndicator = findViewById(R.id.indicator_tab_container);

        mTabPageIndicator.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                VideoPlayerManager.getInstance().stop();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        mTabPageIndicator.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoPlayerManager.getInstance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.getInstance().release();
    }

}
