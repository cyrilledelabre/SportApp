package com.cyrilledelabre.riosportapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add the file view to the Activity
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            //add the Fragment to the id view of the activity @activitylogin// id
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_container, new LoginFragment())
                    .commit();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}


/*
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class LoginActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private final String LOG_TAG = LoginFragment.class.getSimpleName();


    //google+ UTILS
    private static final int RC_SIGN_IN = 0;
    public static GoogleApiClient mGoogleApiClient;

    private static boolean mSignInClicked;
    private boolean mIntentInProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add the file view to the Activity
        setContentView(R.layout.activity_login);


        if (savedInstanceState == null) {

            FacebookSdk.sdkInitialize(getApplicationContext());

            //add the Fragment to the id view of the activity @activitylogin// id
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_container, new LoginFragment())
                    .commit();

            //AddGooglePlusImplementation();

        }


    }

    public static void setmSignInClicked(boolean bool)
    {
        mSignInClicked= bool;
    }

    private void AddGooglePlusImplementation() {
        //Instantiate the Google Login Manager
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        //add onclick listener to signinbutton
    }





    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
  //          mGoogleApiClient.disconnect();
    //    }

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            if (mSignInClicked && result.hasResolution()) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                try {
                    result.startResolutionForResult(this, RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        //TODO get credentials + Intent
    }


    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.reconnect();
            }
        }
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }



}

*/


