package com.cyrilledelabre.riosportapp.MainPackage.MainEvents;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.LocalisationProvider.NetworkProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by cyrilledelabre on 27/05/15.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private final String LOG_TAG = EventListFragment.class.getSimpleName();

    private SupportMapFragment mMapFragment;
    private NetworkProvider mNetworkProvider;
    private GoogleMap mGoogleMap;
    private Context mContext;
    public static final String ARG_PAGE = "page";


    public static EventMapFragment create(int pageNumber) {
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_events_map, container, false);
        mContext = getActivity();
        mNetworkProvider = NetworkProvider.getInstance(mContext);
        mNetworkProvider.activateLocationProvider();
        setUpMap();
        return rootView;

    }

    private void setUpMap()
    {
        // Do a null check to confirm that we have not already instantiated the map.

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.maps, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(LOG_TAG,"onMapReady");

        mGoogleMap = googleMap;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mNetworkProvider.getCurrentLocation(), 0));

        googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(0, 0))
                    .title("Marker"));
    }

}
