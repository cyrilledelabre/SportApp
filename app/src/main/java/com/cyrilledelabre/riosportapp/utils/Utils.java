/* Copyright 2014 Google Inc. All Rights Reserved.
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

package com.cyrilledelabre.riosportapp.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.appspot.riosportapp.event.model.Event;
import com.cyrilledelabre.riosportapp.R;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.util.DateTime;

import java.util.Calendar;

/**
 * A general utility class. All methods here are static and no state is maintained.
 */
public class Utils {

    private static final String LOG_TAG = "Utils";

    private final static String PREFS_KEY_EMAIL = "email_account";
    private final static String PREFS_KEY_TOKEN = "token_access";
    private final static String PREFS_KEY_NAME = "user_name";
    private final static String PREFS_KEY_RADIUS = "radius";



    /**
     * Persists the email address to preference storage space.
     *
     * @param context
     * @param email
     */
    public static void saveEmailAccount(Context context, String email) {
        saveStringToPreference(context, PREFS_KEY_EMAIL, email);
    }
    /**
     * Persists the email address to preference storage space.
     *
     * @param context
     * @param token
     */
    public static void saveTokenAccess(Context context, String token) {
        saveStringToPreference(context, PREFS_KEY_TOKEN, token);
    }
    /**
     * Persists the profile name to preference storage space.
     *
     * @param context
     * @param profile
     */
    public static void saveProfileName(Context context, String profile)
    {
        saveStringToPreference(context, PREFS_KEY_NAME, profile);
    }


    /**
     * Persists the radius to preference storage space.
     *
     * @param context
     * @param radius
     */
    public static void saveRadius(Context context, int radius)
    {
        saveIntToPreference(context, PREFS_KEY_RADIUS, radius);
    }


    /**
     * Returns the persisted name account, or <code>null</code> if none found.
     *
     * @param context
     * @return String
     */
    public static int getRadius(Context context)
    {
        return getIntFromPreference(context, PREFS_KEY_RADIUS);
    }

    /**
     * Returns the radius, or <code>null</code> if none found.
     *
     * @param context
     * @return String
     */
    public static String getProfileName(Context context)
    {
       return getStringFromPreference(context, PREFS_KEY_NAME);
    }

    /**
     * Returns the persisted token acccess, or <code>null</code> if none found.
     *
     * @param context
     * @return String
     */
    public static String getTokenAccess(Context context)
    {
        return getStringFromPreference(context, PREFS_KEY_TOKEN);
    }

    /**
     * Returns the persisted email account, or <code>null</code> if none found.
     *
     * @param context
     * @return
     */
    public static String getEmailAccount(Context context) {
        return getStringFromPreference(context, PREFS_KEY_EMAIL);
    }

    /**
     * Saves a string value under the provided key in the preference manager. If <code>value</code>
     * is <code>null</code>, then the provided key will be removed from the preferences.
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveStringToPreference(Context context, String key, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (null == value) {
            // we want to remove
            pref.edit().remove(key).apply();
        } else {
            pref.edit().putString(key, value).apply();
        }
    }


    /**
     * Saves a int value under the provided key in the preference manager. If <code>value</code>
     * is <code>0</code>, then the provided key will be removed from the preferences.
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveIntToPreference(Context context, String key, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (value == 0) {
            // we want to remove
            pref.edit().remove(key).apply();
        } else {
            pref.edit().putInt(key, value).apply();
        }
    }


    /**
     * Retrieves a String value from preference manager. If no such key exists, it will return
     * <code>null</code>.
     *
     * @param context
     * @param key
     * @return
     */
    public static String getStringFromPreference(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, null);
    }


    /**
     * Retrieves a String value from preference manager. If no such key exists, it will return
     * <code>null</code>.
     *
     * @param context
     * @param key
     * @return
     */
    public static int getIntFromPreference(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(key, 0);
    }

    /**
     * Returns a detailed description of a event.
     *
     * @param context
     * @param event
     * @return
     */
    public static String getEventCard(Context context, Event event) {
        StringBuffer sb = new StringBuffer();
        if (!TextUtils.isEmpty(event.getDescription())) {
            sb.append(event.getDescription() + "\n");
        }

        if (null != event.getStartDate()) {
            sb.append("\n" + getEventDate(context, event));
        }

        if(null != event.getSports()){
            for(String e : event.getSports())
            {
                sb.append(e + " / ");
            }
            sb.append("\n");
        }

        if ((null != event.getMaxParticipants()) && (event.getEntriesAvailable() != null)) {

            String res = event.getEntriesAvailable().intValue()
                    +"/"+ event.getMaxParticipants().intValue();
            sb.append("\n" +context.getString(R.string.seats_available,res ));
        }
        return sb.toString();
    }

    public static String getFormattedParticipants(Context context, Event event)
    {
        StringBuffer sb = new StringBuffer();
        int participants =event.getMaxParticipants().intValue() - event.getEntriesAvailable().intValue();
        String res = participants+"/"+ event.getMaxParticipants().intValue();
        sb.append(context.getString(R.string.seats_available,res ));

        return sb.toString();
    }

    /**
     * Returns the different sports of a event.
     * TODO Get better formatted Sport
     * @param event
     * @return String
     */
    public static String getSports(Event event)
    {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for(String e : event.getSports())
        {
            if(i++ == 0)
                sb.append(e);
            else
                sb.append(" / " + e);
        }


    return sb.toString();
    }

    /**
     * Returns the date of a event.
     *
     * @param context
     * @param event
     * @return
     */
    public static String getEventDate(Context context, Event event) {
        StringBuffer sb = new StringBuffer();


        if (null != event.getStartDate() && null != event.getEndDate()) {
            //end and start date
            sb.append(getFormattedDateRange(context, event.getStartDate(),
                    event.getEndDate()));
        } else if (null != event.getStartDate()) {

            //only start date
            sb.append(getFormattedDate(context, event.getStartDate()));
        }
        return sb.toString();
    }

    /**
     * Returns a user-friendly localized date.
     *  TODO get more friendly date
     * @param context
     * @param dateTime
     * @return
     */
    public static String getFormattedDate(Context context, DateTime dateTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateTime.getValue());
        return DateUtils
                .getRelativeTimeSpanString(cal.getTimeInMillis(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME |
                        DateUtils.FORMAT_ABBREV_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH).toString();
    }

    /**
     * Returns a user-friendly localized data range.
     * TODO getMorefriendly date
     * @param context
     * @param dateTimeStart
     * @param dateTimeEnd
     * @return
     */
    public static String getFormattedDateRange(Context context, DateTime dateTimeStart,
            DateTime dateTimeEnd) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(dateTimeStart.getValue());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(dateTimeEnd.getValue());
        return DateUtils
                .formatDateRange(context, cal1.getTimeInMillis(), cal2.getTimeInMillis(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_TIME);
    }

    public static String getFormattedTime(Context context, DateTime dateTime)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(dateTime.getValue());
        return DateUtils.formatDateTime(context, cal1.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_TIME);
    }

    public static String getFormattedDay(Context context, DateTime dateTime)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(dateTime.getValue());
        return DateUtils.formatDateTime(context, cal1.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_TIME);
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     */
    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    /**
     * Called if the device does not have Google Play Services installed.
     */
    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
            final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    /**
     * Returns the {@code Array} of Google Accounts, if any. Return value can be an empty array
     * (if no such account exists) but never <code>null</code>.
     *
     * @param context
     * @return
     */
    public static Account[] getGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        return am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
    }

    /**
     * Displays error dialog when a network error occurs. Exits application when user confirms
     * dialog.
     *
     * @param context
     */
    public static void displayNetworkErrorMessage(Context context) {
        new AlertDialog.Builder(
                context).setTitle(R.string.api_error_title)
                .setMessage(R.string.api_error_message)
                .setCancelable(true)

                .create().show();
    }



    public static void displayToastMessage(String message, Context context)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}


