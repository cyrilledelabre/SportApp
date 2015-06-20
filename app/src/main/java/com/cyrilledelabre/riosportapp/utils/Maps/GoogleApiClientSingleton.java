package com.cyrilledelabre.riosportapp.utils.maps;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.cyrilledelabre.riosportapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.places.Places;


public class GoogleApiClientSingleton implements com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener {
    private static volatile GoogleApiClientSingleton INSTANCE = null;
    protected com.google.android.gms.common.api.GoogleApiClient mGoogleApiClient;
    private final String LOG_TAG = GoogleApiClientSingleton.class.getSimpleName();


    public final static GoogleApiClientSingleton getInstance(FragmentActivity mContext) {
        if (GoogleApiClientSingleton.INSTANCE == null) {
            synchronized(GoogleApiClientSingleton.class) {
                if (GoogleApiClientSingleton.INSTANCE == null) {
                    GoogleApiClientSingleton.INSTANCE = new GoogleApiClientSingleton(mContext);
                }
            }
        }
        return GoogleApiClientSingleton.INSTANCE;
    }

    public final static GoogleApiClientSingleton tryInstance()
    {
        if (GoogleApiClientSingleton.INSTANCE == null) {
            return null;
        }
        return GoogleApiClientSingleton.INSTANCE;
    }


    private GoogleApiClientSingleton(FragmentActivity mContext)
    {
        // Construct a GoogleApiClientSingleton for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new com.google.android.gms.common.api.GoogleApiClient.Builder(mContext )
                .enableAutoManage(mContext, R.string.google_app_id, this)
                .addApi(Places.GEO_DATA_API)
                .build();

       Log.i(LOG_TAG, "mGoogleApiClient build");
    }

    public void connect()
    {

        mGoogleApiClient.connect();
    }

    public void disconnect()
    {
        mGoogleApiClient.disconnect();
    }

    public com.google.android.gms.common.api.GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(LOG_TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }

}
