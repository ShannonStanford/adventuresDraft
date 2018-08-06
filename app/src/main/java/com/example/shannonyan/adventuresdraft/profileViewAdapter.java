package com.example.shannonyan.adventuresdraft;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class profileViewAdapter extends FragmentPagerAdapter {
    private static final int PAGE_0 = 0;
    private static final int PAGE_1 = 1;
    private static final int COUNT = 2;

    public profileViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case PAGE_0: return PrefFragment.newInstance();
            case PAGE_1: return PastTripFragment.newInstance();
            default: return PrefFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case PAGE_0: return Constants.PREF;
            case PAGE_1: return Constants.PAST_TRIPS;
            default: return Constants.PREF;
        }
    }
}