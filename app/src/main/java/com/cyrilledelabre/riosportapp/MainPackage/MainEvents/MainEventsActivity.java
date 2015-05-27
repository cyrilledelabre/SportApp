package com.cyrilledelabre.riosportapp.MainPackage.MainEvents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.SettingsActivity;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.facebook.FacebookSdk;

/**
 *
 */
public class MainEventsActivity extends Fragment {
    private final String LOG_TAG = MainEventsActivity.class.getSimpleName();
    private EventListFragment mEventListFragment;



    /**
     * Add menu layout
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_event, menu);

    }

    /**
     * On Click Menu make some actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_account:
                new AlertDialog.Builder(getActivity().getApplicationContext()).setTitle(null)
                        .setMessage(getString(R.string.clear_account_message))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Utils.saveEmailAccount(getActivity().getApplicationContext(), null);
                                dialog.cancel();
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();

                break;
            case R.id.action_reload:
                mEventListFragment.reload();
                break;
            case R.id.action_settings:
                startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * onCreateFragment :
     * Init mEventListFragment and add the Fragment into the MainEventsActivity layout container
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
/*

        if (savedInstanceState == null) {
            mEventListFragment = EventListFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.container, mEventListFragment)
                    .commit();
        }
*/

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_main_events, container, false);
        return rootView;

    }


}
