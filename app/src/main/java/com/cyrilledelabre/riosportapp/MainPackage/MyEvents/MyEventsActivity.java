package com.cyrilledelabre.riosportapp.MainPackage.MyEvents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.Utils;

/**
 *
 */
public class MyEventsActivity extends Fragment {
    private final String LOG_TAG = MyEventsActivity.class.getSimpleName();

    private MyEventsListFragment mMyEventsListFragment;
    private String mEmailAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            mEmailAccount = Utils.getEmailAccount(getActivity());

        if (savedInstanceState == null) {
            mMyEventsListFragment = MyEventsListFragment.newInstance();

            getChildFragmentManager().beginTransaction()
                    .add(R.id.container, mMyEventsListFragment)
                    .commit();


        }

    }

    public void onResume() {
        super.onResume();

        if (null != mEmailAccount)
            getEventsForList();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.myevents_activity, container, false);

        return rootView;

    }

    private void getEventsForList() {
        if (TextUtils.isEmpty(mEmailAccount)) {
            return;
        }
        mMyEventsListFragment.loadEvents();
    }
}
