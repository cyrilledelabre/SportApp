package com.cyrilledelabre.riosportapp.MainPackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.cyrilledelabre.riosportapp.MainPackage.CreateEvent.CreateEventsForm;
import com.cyrilledelabre.riosportapp.MainPackage.MainEvents.MainEventsActivity;
import com.cyrilledelabre.riosportapp.MainPackage.MyEvents.MyEventsActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.SettingsActivity;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import it.neokree.googlenavigationdrawer.GAccount;
import it.neokree.googlenavigationdrawer.GAccountListener;
import it.neokree.googlenavigationdrawer.GSection;
import it.neokree.googlenavigationdrawer.GoogleNavigationDrawer;

/**
 * Created by neokree on 17/12/14.
 */
public class MainActivity extends GoogleNavigationDrawer implements GAccountListener{

    GAccount account;
    GSection section1, section2, recorder,night,last,settingsSection;
    GSection createEvents;
    private String mEmailAccount;
    private String mUserName;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String email="";
        String name="";

        if((mEmailAccount = Utils.getEmailAccount(this)) != null)
            email = mEmailAccount;
        if((mUserName = Utils.getProfileName(this)) != null)
            name= mUserName;

        account = new GAccount(name,email,new ColorDrawable(Color.parseColor("#9e9e9e")),this.getResources().getDrawable(R.drawable.mat2));
        this.addAccount(account);

        this.setAccountListener(this);

        // create sections
        section1 = this.newSection("Home",new MainEventsActivity());
        section2 = this.newSection("My Events",new MyEventsActivity());

        createEvents = this.newSection("Create an Event", new CreateEventsForm());

        // recorder section with icon and 10 notifications
        //recorder = this.newSection("Recorder",this.getResources().getDrawable(R.drawable.ic_mic_white_24dp),new FragmentIndex()).setNotifications(10);
        // night section with icon, section color and notifications
        //night = this.newSection("Night Section", this.getResources().getDrawable(R.drawable.ic_hotel_grey600_24dp), new FragmentIndex())
        //        .setSectionColor(Color.parseColor("#2196f3")).setNotifications(150);
        // night section with section color
        //last = this.newSection("Last Section", new FragmentIndex()).setSectionColor((Color.parseColor("#ff9800")));

        Intent i = new Intent(this,SettingsActivity.class);
        settingsSection = this.newSection("Settings",this.getResources().getDrawable(R.drawable.ic_settings_black_24dp),i);

        // add your sections to the drawer
        this.addSection(section1);
        this.addSection(section2);
        this.addDivisor();
        this.addSection(createEvents);
      //  this.addSection(night);
       // this.addDivisor();
       // this.addSection(last);
       // this.addBottomSection(settingsSection);

        // start thread
        if(Utils.getTokenAccess(getApplicationContext())!=null)
            t.start();

    }


    @Override
    public void onAccountOpening(GAccount account) {
        // open account activity or do what you want
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                URL MyProfilePicURL = new URL("https://graph.facebook.com/me/picture?type=normal&method=GET&access_token="+ Utils.getTokenAccess(getApplicationContext()));
                account.setPhoto(getResources().getDrawable(R.drawable.photo));
                Bitmap picture = null;
                try {
                    picture = BitmapFactory.decodeStream(MyProfilePicURL.openConnection().getInputStream());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                account.setPhoto(picture);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyAccountDataChanged();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //else get standard photo picture
                account.setPhoto(getResources().getDrawable(R.drawable.mat1));
                notifyAccountDataChanged();

            }
        }
    });
}
