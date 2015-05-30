package com.cyrilledelabre.riosportapp.MainPackage.MainEvents;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyrilledelabre.riosportapp.MainPackage.DetailEvent.DetailActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.eventUtils.EventDataAdapter;
import com.cyrilledelabre.riosportapp.utils.NetworkProvider;
import com.cyrilledelabre.riosportapp.utils.maps.PopupAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import de.greenrobot.event.EventBus;

/**
 * Created by cyrilledelabre on 27/05/15.
 *
 * EventMapf
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private final String LOG_TAG = EventListFragment.class.getSimpleName();

    private SupportMapFragment mMapFragment;
    private NetworkProvider mNetworkProvider;
    private GoogleMap mGoogleMap;
    private Context mContext;
    private static EventDataAdapter mAdapter;
    public static final String ARG_PAGE = "page";


    public static EventMapFragment create(int pageNumber, EventDataAdapter adapter) {
        mAdapter = adapter;
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        mNetworkProvider = NetworkProvider.getInstance(mContext);
        mNetworkProvider.activateLocationProvider();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main_events_map, container, false);


        setUpMap();
        return rootView;

    }

    private void setUpMap()
    {
        //TODO Do a null check to confirm that we have not already instantiated the map.

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.maps, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e(LOG_TAG, "onMapReady");

        mGoogleMap = googleMap;
        //TODO see args
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(NetworkProvider.getCurrentLocation(), 0));
        mAdapter.setGoogleMap(mGoogleMap);

        mGoogleMap.setInfoWindowAdapter(new PopupAdapter(getActivity().getLayoutInflater()));
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                EventBus.getDefault().postSticky(mAdapter.getDecoratedEventFrom(marker));
                startActivity(intent);
            }
        });

    }


}