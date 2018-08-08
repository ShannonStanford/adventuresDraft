package com.example.shannonyan.adventuresdraft;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/medium.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
