package com.example.shannonyan.adventuresdraft.createflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.shannonyan.adventuresdraft.createflow.fragments.CityPriceDetailsFragment;
import com.example.shannonyan.adventuresdraft.createflow.fragments.CreateFragmentAdapter;
import com.example.shannonyan.adventuresdraft.createflow.fragments.PickUpLocFragment;
import com.example.shannonyan.adventuresdraft.createflow.fragments.TripOverviewFragment;
import com.example.shannonyan.adventuresdraft.profileflow.ProfileActivity;
import com.example.shannonyan.adventuresdraft.R;

public class CreateFlowActivity extends AppCompatActivity implements PickUpLocFragment.OnButtonClickListener,TripOverviewFragment.OnButtonClickListener,CityPriceDetailsFragment.OnButtonClickListener {

    public CreateFragmentAdapter vpAdapter;
    public ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flow);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.logo);

        vpAdapter = new CreateFragmentAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(vpAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager, true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miProfile:
                Intent i = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onButtonClicked(View view){
        int currPos = pager.getCurrentItem();

        switch(view.getId()){

            case R.id.arrow_l:
                //handle currPos is zero
                pager.setCurrentItem(currPos-1);
                break;

            case R.id.arrow_r:
                //handle currPos is reached last item
                pager.setCurrentItem(currPos+1);
                break;
        }
    }
}
