package com.cyrilledelabre.riosportapp.utils.LocalisationProvider;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.cyrilledelabre.riosportapp.MainPackage.CreateEvent.TextEventsForm;
import com.cyrilledelabre.riosportapp.utils.Maps.SphericalUtil;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by cyrilledelabre on 25/05/15.
 */
public class NetworkProvider{
    private static volatile NetworkProvider INSTANCE = null;

    private final String LOG_TAG = TextEventsForm.class.getSimpleName();

    private static LatLngBounds CURRENT_POSITION_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    private String locationProvider = LocationManager.NETWORK_PROVIDER;
    private String locationGPSProvider = LocationManager.GPS_PROVIDER;
    private String locationPassiveProvider = LocationManager.PASSIVE_PROVIDER;

    // Acquire a reference to the system Location Manager
    LocationManager mLocationManager;

    // Define a listener that responds to location updates
    LocationListener mLocationListener;
    static Context mContext;



    public final static NetworkProvider getInstance(Context context) {
        if (NetworkProvider.INSTANCE == null || context != mContext) {
            synchronized(NetworkProvider.class) {
                if (NetworkProvider.INSTANCE == null) {
                    NetworkProvider.INSTANCE = new NetworkProvider(context);
                }
            }
        }
        return NetworkProvider.INSTANCE;
    }


    private NetworkProvider(Context context)
    {
        mContext = context;
        onCreateLocationProvider();
    }



    private void onCreateLocationProvider()
    {
        Log.e(LOG_TAG, "Created Location Provider ! ");

        //initialise the location provider;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //try to find a position from lower to higher
        Location loc = mLocationManager.getLastKnownLocation(locationPassiveProvider);
        if(loc != null ) changeLatLng(loc);
        loc = mLocationManager.getLastKnownLocation(locationProvider);
        if(loc != null ) changeLatLng(loc);
        loc = mLocationManager.getLastKnownLocation(locationGPSProvider);
        if(loc != null ) changeLatLng(loc);

        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                changeLatLng(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider)
            {
            }

            public void onProviderDisabled(String provider)
            {
            }
        };

    }


    private void changeLatLng(Location location)
    {
        //TODO do Better and refactor all
        //test purpose :  5km around you
        Utils.saveRadius(mContext, 500);
        int radius = Utils.getRadius(mContext);
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        if(!CURRENT_POSITION_VIEW.contains(latLng))
        {
            LatLngBounds tmpLatLng = new LatLngBounds.Builder().
                    include(SphericalUtil.computeOffset(latLng, radius, 0)).
                    include(SphericalUtil.computeOffset(latLng, radius, 90)).
                    include(SphericalUtil.computeOffset(latLng, radius, 180)).
                    include(SphericalUtil.computeOffset(latLng, radius, 270)).build();
            CURRENT_POSITION_VIEW = tmpLatLng;
            Log.e(LOG_TAG, "Location Changed ! ");
        }
    }

    public void  deactivateLocationProvider()
    {
        Log.e(LOG_TAG, "Provider Disabled ! ");
        mLocationManager.removeUpdates(mLocationListener);
    }

    public void activateLocationProvider()
    {
        //provider | minTime | minDistance | Listener
        Log.e(LOG_TAG, "Provider enabled ! ");
        mLocationManager.requestLocationUpdates(locationProvider, 0, 0, mLocationListener);
    }

    public LatLngBounds getCurrentLocation()
    {
        return CURRENT_POSITION_VIEW;
    }


}
