package com.example.shannonyan.adventuresdraft.uberhelper;

import android.content.Context;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.AccessTokenStorage;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.AccessTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.services.RidesService;

import java.util.Arrays;
import java.util.List;

public final class UberClient {
    //IOC/injection
    private static UberClient uberClientInstance;
    //UBER API varsD4bTw-t73c
    public String CLIENT_ID = "KVIZ1mELnrgyX7cIhv_xPT3LeD6HBqP7";
    public String TOKEN = "eNrJY4R7S4AX9ziTAIDJxopr4hmM0XTA3JD2OCyg"; //serverToken
    public String testAccessToken = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6Iis2UEJpQVJvUlpHS0dna0NPWHpPU2c9PSIsImV4cGlyZXNfYXQiOjE1MzYxODkyNzUsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.YIxJFe-E0-wNv0JiBSHi5G7HHMSIqsORYlZYV3gXdcM";
    public RidesService service;
    public SessionConfiguration config = new SessionConfiguration.Builder()
            // mandatory
            .setClientId(CLIENT_ID)
            // required for enhanced button features
            .setServerToken(TOKEN)
            .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST, Scope.HISTORY_LITE, Scope.PLACES, Scope.REQUEST_RECEIPT))
            // required for implicit grant authentication
            //.setRedirectUri("<REDIRECT_URI>")
            // optional: set sandbox as operating environment
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();

    // UberClient prevents any other class from instantiating
    private UberClient(Context context) {
        //UBER initializations
        UberSdk.initialize(config);
        AccessTokenManager accessTokenManager = new AccessTokenManager(context, testAccessToken);
        Long expirationTime = Long.valueOf(2592000);
        List<Scope> scopes = Arrays.asList(Scope.PROFILE, Scope.REQUEST, Scope.HISTORY_LITE, Scope.PLACES, Scope.REQUEST_RECEIPT);
        String refreshToken = "obtainedRefreshToken";
        String tokenType = "access_token";
        AccessToken accessToken = new AccessToken(expirationTime, scopes, testAccessToken, refreshToken, tokenType);
        AccessTokenStorage accessTokenStorage = new AccessTokenManager(context);
        accessTokenStorage.setAccessToken(accessToken);
        AccessTokenSession session = new AccessTokenSession(config, accessTokenStorage);
        service = UberRidesApi.with(session).build().createService();
    }

    // Providing Global point of access
    public static UberClient getUberClientInstance(Context context) {
        if (null == uberClientInstance) {
            uberClientInstance = new UberClient(context);
        }
        return uberClientInstance;
    }

    public void printSingleton(){
        System.out.println("Inside print uberClient");
    }
}