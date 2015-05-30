package com.cyrilledelabre.riosportapp.MainPackage.MainEvents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrilledelabre.riosportapp.R;
import com.facebook.FacebookSdk;

/**
 *
 */
public class MainEventsActivity extends Fragment {
    private final String LOG_TAG = MainEventsActivity.class.getSimpleName();
    private EventListFragment mEventListFragment;


    /**
     * onCreateFragment :
     * Init mEventListFragment and add the Fragment into the MainEventsActivity layout container
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            EventsListFragment fragment = new EventsListFragment();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
        }

    }

    /**
     * Inflates the layout view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_main_events, container, false);
        return rootView;

    }

}