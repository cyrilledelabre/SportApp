package com.cyrilledelabre.riosportapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class LoginActivity extends ActionBarActivity
      {

    private final String LOG_TAG = LoginFragment.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add the file view to the Activity
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            //add the Fragment to the id view of the activity @activitylogin// id
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_container, fragment)
                    .commit();
        }

     //   AddGooglePlusImplementation();

    }


}


