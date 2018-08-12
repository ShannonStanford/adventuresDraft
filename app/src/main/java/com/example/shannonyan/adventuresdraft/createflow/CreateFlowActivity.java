package com.example.shannonyan.adventuresdraft.createflow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.createflow.fragments.CreateFragmentAdapter;
import com.example.shannonyan.adventuresdraft.createflow.fragments.FragmentChangeInterface;
import com.example.shannonyan.adventuresdraft.profileflow.ProfileActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateFlowActivity extends AppCompatActivity implements FragmentChangeInterface {

    public CreateFragmentAdapter vpAdapter;
    public ViewPager pager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flow);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.logo2);
        vpAdapter = new CreateFragmentAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(vpAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager, true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
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
            case R.id.btPrev:
                //handle currPos is zero
                pager.setCurrentItem(currPos - 1);
                break;
            case R.id.btNext:
                //handle currPos is reached last item
                pager.setCurrentItem(currPos + 1);
                break;
        }
    }
}
