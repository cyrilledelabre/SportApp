package com.cyrilledelabre.riosportapp.MainPackage.MainEvents;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.EventUtils;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

/**
 *
 */
public class MainEventsActivity extends Fragment {
    private final String LOG_TAG = MainEventsActivity.class.getSimpleName();

    /**
     * Activity result indicating a return from the Google account selection intent.
     */
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;

    private AuthorizationCheckTask mAuthTask;
    private String mEmailAccount;
    private String mUserName;

    private EventListFragment mEventListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //get the email account @
        mEmailAccount = Utils.getEmailAccount(getActivity());
        mUserName= Utils.getProfileName(getActivity());

        if (savedInstanceState == null) {
            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
            mEventListFragment = EventListFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.container, mEventListFragment)
                    .commit();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.mainevents_activity_main, container, false);
        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
            mAuthTask = null;
        }
    }

    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getActivity());

        if (null != mEmailAccount) {
            performAuthCheck(mEmailAccount);
        } else {
            selectAccount();
        }
    }

 /*
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }




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
        return true;
    }

     * Selects an account for talking to Google Play services. If there is more than one account on
     * the device, it allows user to choose one. */

    private void selectAccount() {
        //Get the email account from
        Account[] accounts = Utils.getGoogleAccounts(getActivity().getApplicationContext());
        Log.e(LOG_TAG, accounts[0].name);

        int numOfAccount = accounts.length;
        switch (numOfAccount) {
            case 0:
                // No accounts registered, nothing to do.
                Toast.makeText(getActivity().getApplicationContext(), R.string.toast_no_google_accounts_registered,
                        Toast.LENGTH_LONG).show();
                break;
            case 1:
                mEmailAccount = accounts[0].name;
                performAuthCheck(mEmailAccount);
                break;
            default:
                // More than one Google Account is present, a chooser is necessary.
                // Invoke an {@code Intent} to allow the user to select a Google account.
                Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                        new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                        getString(R.string.select_account_for_access), null, null, null);
                startActivityForResult(accountSelector, ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == Activity.RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.
            mEmailAccount = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        } else {
            getActivity().finish();
        }
    }

    /*
     * Schedule the authorization check.
     */
    private void performAuthCheck(String email) {
        // Cancel previously running tasks.
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
        }

        // Start task to check authorization.
        mAuthTask = new AuthorizationCheckTask();
        mAuthTask.execute(email);
    }

    /**
     * Verifies OAuth2 token access for the application and Google account combination with
     * the {@code AccountManager} and the Play Services installed application. If the appropriate
     * OAuth2 access hasn't been granted (to this application) then the task may fire an
     * {@code Intent} to request that the user approve such access. If the appropriate access does
     * exist then the button that will let the user proceed to the next activity is enabled.
     */
    private class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {

        private final static boolean SUCCESS = true;
        private final static boolean FAILURE = false;
        private Exception mException;

        @Override
        protected Boolean doInBackground(String... emailAccounts) {
            Log.i(LOG_TAG, "Background task started.");

            if (!Utils.checkGooglePlayServicesAvailable(getActivity())) {
                publishProgress(R.string.gms_not_available);
                return FAILURE;
            }

            String emailAccount = emailAccounts[0];
            // Ensure only one task is running at a time.
            mAuthTask = this;

            // Ensure an email was selected.
            if (TextUtils.isEmpty(emailAccount)) {
                publishProgress(R.string.toast_no_google_account_selected);
                return FAILURE;
            }

            mEmailAccount = emailAccount;
            Utils.saveEmailAccount(getActivity().getApplicationContext(), emailAccount);

            return SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Integer... stringIds) {
            // Toast only the most recent.
            Integer stringId = stringIds[0];
            Toast.makeText(getActivity().getApplicationContext(), getString(stringId), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            mAuthTask = this;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Authorization check successful, get events.
                EventUtils.build(getActivity().getApplicationContext(), mEmailAccount);
                getEventsForList();
            } else {
                // Authorization check unsuccessful.
                mEmailAccount = null;
                if (mException != null) {
                    Utils.displayNetworkErrorMessage(getActivity().getApplicationContext());
                }
            }
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }



    private void getEventsForList() {
        if (TextUtils.isEmpty(mEmailAccount)) {
            return;
        }
        mEventListFragment.loadEvents();
    }
}
