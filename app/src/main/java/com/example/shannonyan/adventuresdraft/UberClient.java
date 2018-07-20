package com.example.shannonyan.adventuresdraft;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

public class UberClient {

    public String CLIENT_ID = "0toSWTHkZXJIa-llj9rh900hXrelnQeY";
    public String TOKEN = "c2hx0dzYfc5ptMEPWA0w3ODBWdUsaITDQ_UTWF4M";
    public String AccessTokenTest = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6InZ1YkREQit1U2UrdUxrS3l6UzNmTkE9PSIsImV4cGlyZXNfYXQiOjE1MzQ2NDQ3NTQsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.qXqHB-ZHIJ8XBmXopiwcFMo3_Yw0qzFGdg6fVBWhqxU";

    SessionConfiguration config = new SessionConfiguration.Builder()
            // mandatory
            .setClientId(CLIENT_ID)
            // required for enhanced button features
            .setServerToken(TOKEN)
            // required for implicit grant authentication
            //.setRedirectUri("<REDIRECT_URI>")
            // optional: set sandbox as operating environment
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();

}

//requests to sandbox env. made to : https://sandbox-api.uber.com/<version>
//client ID: 0toSWTHkZXJIa-llj9rh900hXrelnQeY
//Server Token: c2hx0dzYfc5ptMEPWA0w3ODBWdUsaITDQ_UTWF4M
//Client Secret: dNAgDE2Se5bttpmahF696rOYmcUCThiLuDhtHRQw
//Personal Access Token for Testing: KA.eyJ2ZXJzaW9uIjoyLCJpZCI6InZ1YkREQit1U2UrdUxrS3l6UzNmTkE9PSIsImV4cGlyZXNfYXQiOjE1MzQ2NDQ3NTQsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.qXqHB-ZHIJ8XBmXopiwcFMo3_Yw0qzFGdg6fVBWhqxU