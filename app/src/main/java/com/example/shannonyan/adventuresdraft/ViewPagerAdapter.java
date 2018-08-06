package com.example.shannonyan.adventuresdraft;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_0 = 0;
    private static final int PAGE_1 = 1;
    private static final int PAGE_2 = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: return CreateSecondFragment.newInstance();
            case 1: return CreatePickUpFragment.newInstance();
            case 2: return CreateThirdFragment.newInstance();
            default: return CreatePickUpFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 3;
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
