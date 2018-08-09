package com.example.shannonyan.adventuresdraft.createflow.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CreateFragmentAdapter extends FragmentPagerAdapter {
    private static final int PAGE_0 = 0;
    private static final int PAGE_1 = 1;
    private static final int PAGE_2 = 2;
    private static final int COUNT = 3;

    public CreateFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case PAGE_0: return CityPriceDetailsFragment.newInstance();
            case PAGE_1: return PickUpLocFragment.newInstance();
            case PAGE_2: return TripOverviewFragment.newInstance();
            default: return PickUpLocFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

}
