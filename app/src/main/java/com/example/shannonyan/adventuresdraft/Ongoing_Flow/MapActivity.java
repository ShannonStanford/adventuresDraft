package com.example.shannonyan.adventuresdraft.Ongoing_Flow;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.shannonyan.adventuresdraft.Constants;
import com.example.shannonyan.adventuresdraft.R;

public class MapActivity extends AppCompatActivity {

    public String mapURL;
    public WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        mapURL = Constants.MAP_LINK;
        loadMapView();
    }

    public void loadMapView() {
        webview  = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        webview.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });

        webview.loadUrl(mapURL);
        setContentView(webview);
    }

    //This method would be called when we test the map in a real Uber ride (non simulation).
    public void mapLinkProductionVersion(){
        Intent intent = getIntent();
        mapURL = intent.getExtras().getString(Constants.MAP_URL);
    }
}
