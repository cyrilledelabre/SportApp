package com.cyrilledelabre.riosportapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends ActionBarActivity
      {

    private final String LOG_TAG = LoginFragment.class.getSimpleName();


    //google+ UTILS
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;

    private boolean mSignInClicked;
    private boolean mIntentInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add the file view to the Activity
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            //add the Fragment to the id view of the activity @activitylogin// id
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_login, fragment)
                    .commit();
        }

     //   AddGooglePlusImplementation();

    }


}


