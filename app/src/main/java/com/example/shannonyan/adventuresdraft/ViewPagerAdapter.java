package com.example.shannonyan.adventuresdraft;

import android.support.annotation.Nullable;
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
            case 0: return CreatePickUpFragment.newInstance();
            case 1: return CreateSecondFragment.newInstance();
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
            case 0: return "Preferences";
            case 1: return "Past Trips";
            default: return "Preferences";
        }
    }
}
