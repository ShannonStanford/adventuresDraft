package com.example.shannonyan.adventuresdraft;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

public class UberClient {

    public String CLIENT_ID = "0toSWTHkZXJIa-llj9rh900hXrelnQeY";
    public String TOKEN = "c2hx0dzYfc5ptMEPWA0w3ODBWdUsaITDQ_UTWF4M";


    SessionConfiguration config = new SessionConfiguration.Builder()
            // mandatory
            .setClientId(CLIENT_ID)
            // required for enhanced button features
            .setServerToken(TOKEN)
            // required for implicit grant authentication
            .setRedirectUri("<REDIRECT_URI>")
            // optional: set sandbox as operating environment
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();
}

