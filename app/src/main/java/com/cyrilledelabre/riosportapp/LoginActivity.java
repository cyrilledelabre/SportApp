package com.cyrilledelabre.riosportapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the layout view
        setContentView(R.layout.activity_login);
        LoginFragment fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.login_container, fragment)
                .commit();

    }




}


