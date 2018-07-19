package com.example.shannonyan.adventuresdraft;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter{
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
//        DemoFragment demoFragment = new DemoFragment();
//        position = position+1;
//        Bundle bundle = new Bundle();
//        bundle.putString("message", "Fragment: " + position);
//        demoFragment.setArguments(bundle);
//        return demoFragment;
        switch(position) {
            case 0: return DemoFragment.newInstance("FirstFragment, Instance 1");
            default: return DemoFragment.newInstance("FirstFragment, Instance 1");
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
