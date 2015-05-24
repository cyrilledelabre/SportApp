package com.cyrilledelabre.riosportapp.Tasks.ApiTask;


import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.EventUtils;
import com.cyrilledelabre.riosportapp.utils.Utils;

/**
 * Verifies OAuth2 token access for the application and Google account combination with
 * the {@code AccountManager} and the Play Services installed application. If the appropriate
 * OAuth2 access hasn't been granted (to this application) then the task may fire an
 * {@code Intent} to request that the user approve such access. If the appropriate access does
 * exist then the button that will let the user proceed to the next activity is enabled.
 */
public class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {
    private final String LOG_TAG = AuthorizationCheckTask.class.getSimpleName();

    private final static boolean SUCCESS = true;
    private final static boolean FAILURE = false;
    private Exception mException;

    public String mEmailAccount;
    private Activity mContext;

    public AuthorizationCheckTask(Activity context)
    {
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... emailAccounts) {
        Log.e(LOG_TAG, "Background task started.");

        if (!Utils.checkGooglePlayServicesAvailable(mContext)) {
            publishProgress(R.string.gms_not_available);
            return FAILURE;
        }
        mEmailAccount = emailAccounts[0];
        // Ensure an email was selected.
        if (TextUtils.isEmpty(mEmailAccount)) {
            publishProgress(R.string.toast_no_google_account_selected);
            return FAILURE;
        }

        //save the email account
        Utils.saveEmailAccount(mContext.getApplicationContext(), mEmailAccount);
        //Build the connection
        try{
            EventUtils.build(mContext.getApplicationContext(), mEmailAccount);
        }
        catch(Exception e)
        {
            Log.e(LOG_TAG,"EventUtils.build(..) ERROR : "+e);
        }

        return SUCCESS;
    }


    @Override
    protected void onProgressUpdate(Integer... stringIds) {
        Integer stringId = stringIds[0];
        Toast.makeText(mContext.getApplicationContext(), mContext.getString(stringId), Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            Log.e(LOG_TAG,"Finished AuthorizationChecktask ");
        } else {
            mEmailAccount = null;
            if (mException != null) {
                Utils.displayNetworkErrorMessage(mContext.getApplicationContext());
            }
        }
    }
}
