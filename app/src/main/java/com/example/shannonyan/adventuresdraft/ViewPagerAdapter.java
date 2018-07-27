package com.example.shannonyan.adventuresdraft;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return PrefFragment.newInstance();
            case 1: return PastTripFragment.newInstance();
            default: return PrefFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
