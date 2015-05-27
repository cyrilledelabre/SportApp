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

import com.cyrilledelabre.riosportapp.MainPackage.MainActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.LocalisationProvider.NetworkProvider;
import com.cyrilledelabre.riosportapp.utils.Maps.GoogleApiClientSingleton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import de.greenrobot.event.EventBus;

/**
 * Demonstrates a "screen-slide" animation using a {@link ViewPager}. Because
 * {@link ViewPager} automatically plays such an animation when calling
 * {@link ViewPager#setCurrentItem(int)}, there isn't any animation-specific
 * code in this sample.
 *
 * <p>
 * This sample shows a "next" button that advances the user to the next step in
 * a wizard, animating the current screen out (to the left) and the next screen
 * in (from the right). The reverse animation is played when the user presses
 * the "previous" button.
 * </p>
 *
 * @see TextForm
 */
public class FormSlideActivity extends ActionBarActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    public static final int NUM_PAGES = 3;
    private final String LOG_TAG = TextEventsForm.class.getSimpleName();
    private static int PLACE_PICKER_REQUEST = 1;

    /**
     * The pager widget, which handles animation and allows swiping horizontally
     * to access previous and next wizard steps.
     */
    private ViewPager mPager;
    public ViewHolder mHolder;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_create_events_slide);

        mHolder = new ViewHolder();
        mHolder.mGoogleApiClient = GoogleApiClientSingleton.getInstance(this).getGoogleApiClient();
        mHolder.mNetworkProvider = NetworkProvider.getInstance(this);
        mHolder.mDecoratedEvent = EventBus.getDefault().getStickyEvent(DecoratedEvent.class);
        mHolder.editMode = (mHolder.mDecoratedEvent != null);

        t.run();
        mPager = (ViewPager) findViewById(R.id.pager);//TODO a modifier le nom du id
        mPagerAdapter = new FormSlideAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });



    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            mHolder.mGoogleApiClient.connect();
            mHolder.mNetworkProvider.activateLocationProvider();
        }
    });

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        // Instantiate a ViewPager and a PagerAdapter.

        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //create on the fly menu options previous / next|finish button
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

        Place mPlace;
        TextView mPlaceName;
        TextView mPlaceAddress;

        DecoratedEvent mDecoratedEvent;
        GoogleApiClient mGoogleApiClient;
        //getNetwork Localisation
        NetworkProvider mNetworkProvider;

        boolean editMode;


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
                mHolder.mPlace = PlacePicker.getPlace(data, this);

                //TODO hey could not find the address would you like it to give a name ??
                mHolder.mPlaceAddress.setVisibility(View.VISIBLE);
                mHolder.mPlaceName.setVisibility(View.VISIBLE);
                mHolder.mPlaceName.setText(mHolder.mPlace.getName());
                mHolder.mPlaceAddress.setText(mHolder.mPlace.getAddress());
            } else {

                super.onActivityResult(requestCode, resultCode, data);
            }

    }

    /**
     * A simple pager adapter that represents  {@link TextForm}
     * objects, in sequence.
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
