package com.example.shannonyan.adventuresdraft.Create_Flow.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_0 = 0;
    private static final int PAGE_1 = 1;
    private static final int PAGE_2 = 2;
    private static final int COUNT = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case PAGE_0: return CreateSecondFragment.newInstance();
            case PAGE_1: return CreatePickUpFragment.newInstance();
            case PAGE_2: return CreateThirdFragment.newInstance();
            default: return CreatePickUpFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

}
