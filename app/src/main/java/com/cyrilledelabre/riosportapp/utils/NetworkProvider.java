package com.cyrilledelabre.riosportapp.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.cyrilledelabre.riosportapp.utils.maps.SphericalUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

/**
 * Created by cyrilledelabre on 25/05/15.
 */
public class NetworkProvider {
    private static volatile NetworkProvider INSTANCE = null;
    private final String LOG_TAG = NetworkProvider.class.getSimpleName();
    // default 5km
    // default RJ view
    private static LatLngBounds CURRENT_POSITION_VIEW = new LatLngBounds(new LatLng(-23.00767101677464, -43.224337814194875), new LatLng(-22.917738983225355, -43.126666185805135));
    //default value
    private static LatLng CURRENT_POSITION = new LatLng(-23.00767101677464, -43.224337814194875);
    // Acquire a reference to the system Location Manager
    LocationManager mLocationManager;

    // Define a listener that responds to location updates
    LocationListener mLocationListener;
    static Context mContext;


    public final static NetworkProvider getInstance(Context context) {
        if (NetworkProvider.INSTANCE == null) {
            synchronized (NetworkProvider.class) {
                if (NetworkProvider.INSTANCE == null) {
                    NetworkProvider.INSTANCE = new NetworkProvider(context);
                }
            }
        }
        return NetworkProvider.INSTANCE;
    }


    private NetworkProvider(Context context) {
        mContext = context;
        onCreateLocationProvider();
    }


    private void onCreateLocationProvider() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //try to get location from locations providers;
        List<String> locationsProviders = mLocationManager.getAllProviders();
        for (String locationProvider : locationsProviders) {
            Location location = mLocationManager.getLastKnownLocation(locationProvider);
            if (location != null) changeLatLng(location);
        }
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                changeLatLng(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                //Log.i(LOG_TAG, "Provider enabled for " + provider);
            }

            public void onProviderDisabled(String provider) {
                //Log.i(LOG_TAG, "Provider Disabled for " + provider);
            }
        };
    }


    private int getRadiusFromPreferences() {
        return Utils.getFormattedRadius(mContext);
    }

    private void changeLatLng(Location location) {
        //TODO better
        int radius = getRadiusFromPreferences();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CURRENT_POSITION = latLng;
        if (!CURRENT_POSITION_VIEW.contains(latLng)) {
            LatLngBounds tmpLatLng = new LatLngBounds.Builder().
                    include(SphericalUtil.computeOffset(latLng, radius, 0)).
                    include(SphericalUtil.computeOffset(latLng, radius, 90)).
                    include(SphericalUtil.computeOffset(latLng, radius, 180)).
                    include(SphericalUtil.computeOffset(latLng, radius, 270)).build();
            CURRENT_POSITION_VIEW = tmpLatLng;
        }
    }

    public void deactivateLocationProvider() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    public void activateLocationProvider() {


        //provider | minTime | minDistance | Listener
        List<String> locationsProviders = mLocationManager.getAllProviders();
        for (String locationProvider : locationsProviders) {
            mLocationManager.requestLocationUpdates(locationProvider, 0, 0, mLocationListener);
        }
    }

    public static LatLngBounds getCurrentLocation() {
        return CURRENT_POSITION_VIEW;
    }

    public static LatLng getCurrentPosition() {
        return CURRENT_POSITION;
    }


}
