package com.cyrilledelabre.riosportapp.MainPackage.MainEvents;
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrilledelabre.riosportapp.MainPackage.CreateEvent.TextForm;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.common.SlidingTabLayout;


/**
 * A basic sample which shows how to use {@link  com.cyrilledelabre.riosportapp.utils.common.SlidingTabLayout}
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class EventsListFragment extends Fragment {
    public static final int NUM_PAGES = 2;
    static final String LOG_TAG = "SlidingTabsBasicFragment";

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO edit name
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link EventsSlideAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //TODO editname
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new EventsSlideAdapter(getActivity().getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    /**
     * A simple pager adapter that represents 5 {@link TextForm}
     * objects, in sequence.
     */
    class EventsSlideAdapter extends FragmentStatePagerAdapter {
        public EventsSlideAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position)
            {
                case 0:
                    return "All Events " ;
                default :
                    return "Events on the Map";
            }
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position)
            {
                case 0:
                    return EventListFragment.create(position);
                default :
                    return EventMapFragment.create(position);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        /**
        * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
        * {@link View}.
                */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }


}