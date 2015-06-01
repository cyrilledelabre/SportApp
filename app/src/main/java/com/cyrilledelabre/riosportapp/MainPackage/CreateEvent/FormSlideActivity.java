/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyrilledelabre.riosportapp.MainPackage.CreateEvent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.appspot.riosportapp.event.model.GeoPt;
import com.cyrilledelabre.riosportapp.MainPackage.MainActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.NetworkProvider;
import com.cyrilledelabre.riosportapp.utils.eventUtils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.maps.GoogleApiClientSingleton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import de.greenrobot.event.EventBus;

/**
 *
 *  A "screen-slide" animation using a {@link ViewPager} for creating a new sport event.
 *  First screen-slide asks for the title, description, and number of participants.
 *  Second asks for the place with PlacePicker {@link PlacePicker} or with AutoComplete
 *  {@link android.widget.AutoCompleteTextView} from GooglePlaces
 *  and the date with {@link android.widget.DatePicker} and time with
 *  {@link android.widget.TimePicker} for the beginning and the end of the sport event.
 *
 */
public class FormSlideActivity extends ActionBarActivity {
    /**
     * The number of pages to show.
     */
    public static final int NUM_PAGES = 3;
    /**
     * Log tag
     */
    private final String LOG_TAG = FormSlideActivity.class.getSimpleName();
    /**
     * OnActivityResult for the place picker request
     * It's performed here instead of in the {@link MapsEventsForm} fragment.
     * (could have been the other place)
     */
    private static int PLACE_PICKER_REQUEST = 1;

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next wizard steps.
     */
    private ViewPager mPager;
    /**
     * The holder, which handles the data between the different fragments.
     */
    public ViewHolder mHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_create_events_slide);
        /**
         * creating Network Provider, Google API Client and getting (if has) the mDecoratedEvent
         * from the past Activity
         */
        mHolder = new ViewHolder();
        mHolder.mGoogleApiClient = GoogleApiClientSingleton.getInstance(this).getGoogleApiClient();
        mHolder.mNetworkProvider = NetworkProvider.getInstance(this);
        mHolder.mDecoratedEvent = EventBus.getDefault().getStickyEvent(DecoratedEvent.class);
        mHolder.editMode = (mHolder.mDecoratedEvent != null);

        t.run();
        mPager = (ViewPager) findViewById(R.id.pager);//TODO a modifier le nom du id
        PagerAdapter mPagerAdapter = new FormSlideAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });



    }


    /**
     * launching low tasks in another thread
     * TODO see how to pass params and launch more stuff
     *
     */
    Thread t = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    mHolder.mGoogleApiClient.connect();
                    mHolder.mNetworkProvider.activateLocationProvider();
                }
            }
    );

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html
                // for more.
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;

            case R.id.action_previous:
                // Go to the previous step in the wizard. If there is no previous
                // step,
                // setCurrentItem will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                return true;

            case R.id.action_next:
                // Advance to the next step in the wizard. If there is no next step,
                // setCurrentItem
                // will do nothing.
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


    /**
     * onCreateOptionMenu
     * creates on the fly menu option with previous or next/finish button to switch betweens
     * the different view from the FormSlideActivity
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //create on the fly menu options with previous / next|finish button
        MenuItem prev = menu.add(
                Menu.NONE,
                R.id.action_previous,
                Menu.NONE,
                R.string.action_previous);
        prev.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        prev.setEnabled(mPager.getCurrentItem()  >0);

        MenuItem item = menu
                .add(Menu.NONE,
                        R.id.action_next,
                        Menu.NONE,
                        (mPager.getCurrentItem() == FormSlideActivity.NUM_PAGES - 1) ? R.string.action_finish
                                : R.string.action_next);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);

    }


    /**
     * Simple ViewHolder containing all the data before creating {@link addEventAsyncTask} and
     * sending the event
     * It helps to switch between different Fragments and it's not maybe the best choice
     */
    protected class ViewHolder{
         TextView mTitleView;
         TextView mDescriptionView;
         EditText mStartDateView;
         EditText mEndDateView;
         EditText mStartTimeView;
         EditText mEndTimeView;
         Spinner mSportsView;
         TextView mParticipantsView;
         Button mSendButton;

        int startHour, startMinute, startDay, startMonth, startYear;
        int endHour, endMinute, endDay, endMonth, endYear;

        GeoPt mLocalisation;
        TextView mPlaceName;
        TextView mPlaceAddress;

        DecoratedEvent mDecoratedEvent;
        GoogleApiClient mGoogleApiClient;
        NetworkProvider mNetworkProvider;

        boolean editMode;


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                //TODO hey could not find the address would you like it to give a name ??
                if (place.getName() != null) {
                    mHolder.mPlaceName.setVisibility(View.VISIBLE);
                    mHolder.mPlaceName.setText(place.getName());
                }
                if (place.getAddress() != null) {
                    mHolder.mPlaceAddress.setVisibility(View.VISIBLE);
                    mHolder.mPlaceAddress.setText(place.getAddress());
                }
                mHolder.mLocalisation = new GeoPt();
                mHolder.mLocalisation.setLongitude((float) place.getLatLng().longitude);
                mHolder.mLocalisation.setLatitude((float) place.getLatLng().latitude);

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }

    }

    /**
     * A simple pager adapter that represents a {@link TextEventsForm} fragment , a {@link MapsEventsForm}
     * and the final {@link TextForm} for switching between the pages.
     */
    public class FormSlideAdapter extends FragmentStatePagerAdapter {
        public FormSlideAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position)
            {
                case 0:
                    return TextEventsForm.create(position, mHolder );
                case 1:
                    return MapsEventsForm.create(position, mHolder);
                default:
                    return TextForm.create(position, mHolder);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().removeStickyEvent(DecoratedEvent.class);
        mHolder.mGoogleApiClient.disconnect();
        mHolder.mNetworkProvider.deactivateLocationProvider();

        super.onDestroy();
    }


}
