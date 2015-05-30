package com.cyrilledelabre.riosportapp.MainPackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;

import com.cyrilledelabre.riosportapp.MainPackage.CreateEvent.FormSlideActivity;
import com.cyrilledelabre.riosportapp.MainPackage.MainEvents.MainEventsActivity;
import com.cyrilledelabre.riosportapp.MainPackage.MyEvents.MyEventsActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.NetworkProvider;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import it.neokree.googlenavigationdrawer.GAccount;
import it.neokree.googlenavigationdrawer.GAccountListener;
import it.neokree.googlenavigationdrawer.GSection;
import it.neokree.googlenavigationdrawer.GoogleNavigationDrawer;


public class MainActivity extends GoogleNavigationDrawer implements GAccountListener{
    
    private  String mAccessToken;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    GAccount mAccount;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
    
    @Override
    public void init(Bundle savedInstanceState) {

        NetworkProvider mNetworkProvider = NetworkProvider.getInstance(getApplicationContext());

        GSection Home, myEvents,settingsSection, createEvents;
        String mEmailAccount,mUserName;

        mEmailAccount=Utils.getEmailAccount(this);
        mUserName=Utils.getProfileName(this);
        mAccessToken = Utils.getTokenAccess(this);

        if(mUserName==null && mEmailAccount != null)
        {
                mUserName =  mEmailAccount.substring(0, mEmailAccount.indexOf("@"));
                Utils.saveProfileName(this, mUserName);
        }
        if(mAccessToken !=null)
        {
            t.start();
        }

        mAccount = new GAccount(mUserName,mEmailAccount,new ColorDrawable(Color.parseColor("#9e9e9e")),this.getResources().getDrawable(R.drawable.mat2));
        this.addAccount(mAccount);

        this.setAccountListener(this);

        // create sections
        Home = newSection("Home", new MainEventsActivity());
        myEvents = newSection("My Events", new MyEventsActivity());

        //Intent createEventsIntent = new Intent(this, CreateEventsActivity.class);
        //createEvents = newSection("Create an Event", createEventsIntent);

        Intent i = new Intent(this,SettingsActivity.class);
        settingsSection = this.newSection("Settings", this.getResources().getDrawable(R.drawable.ic_settings_black_24dp), i);


        Intent screen = new Intent(this, FormSlideActivity.class);
        GSection screenSlide = newSection("Create an Event", screen);

        // add your sections to the drawer
        this.addSection(Home);
        this.addSection(myEvents);
        this.addDivisor();
        //this.addSection(createEvents);
        this.addSection(screenSlide);



        this.addBottomSection(settingsSection);
    }


    @Override
    public void onAccountOpening(GAccount mAccount) {
        // open mAccount activity or do what you want
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                URL MyProfilePicURL = new URL("https://graph.facebook.com/me/picture?type=normal&method=GET&access_token="+ mAccessToken);
                //mAccount.setPhoto(getResources().getDrawable(R.drawable.photo));
                Bitmap picture = null;
                try {
                    picture = BitmapFactory.decodeStream(MyProfilePicURL.openConnection().getInputStream());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mAccount.setPhoto(picture);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyAccountDataChanged();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //else get standard photo picture
            }
        }
    });
}
