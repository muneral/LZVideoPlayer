package com.example.LAndZVideoPlayer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.LAndZVideoPlayer.MViewPager.FullFragment;
import com.example.LAndZVideoPlayer.MViewPager.VideoFragment;

//分类数据适配器
//Liu
public class TabAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT =2;

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 1)
            return new FullFragment();
        else
            return new VideoFragment();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0)       return "         RECYCLERVIEW         ";
        else if (position == 1)  return "           VIEWPAGER           ";
        return null;
    }
}
