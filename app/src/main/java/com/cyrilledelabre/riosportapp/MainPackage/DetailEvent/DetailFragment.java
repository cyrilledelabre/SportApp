package com.cyrilledelabre.riosportapp.MainPackage.DetailEvent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.riosportapp.event.model.Event;
import com.appspot.riosportapp.event.model.GeoPt;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.cyrilledelabre.riosportapp.utils.eventUtils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.eventUtils.EventUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 * On recupere les données avec un Intent
 * Et on les affiche dans la view
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private DecoratedEvent mDecoratedEvent;
    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mDateView;
    private TextView mSportsView;
    private TextView mParticipantsView;
    private TextView mPlaceView;
    private Button mRegisterButton;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_detail_event, container, false);
        if (savedInstanceState != null)
            return rootView;

        mDecoratedEvent = EventBus.getDefault().getStickyEvent(DecoratedEvent.class);

        mPlaceView = (TextView) rootView.findViewById(R.id.detail_place_textview);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mTitleView = (TextView) rootView.findViewById(R.id.detail_title_textview);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_description_textview);
        mSportsView = (TextView) rootView.findViewById(R.id.detail_sports_textview);
        mParticipantsView = (TextView) rootView.findViewById(R.id.detail_participants_textview);
        mRegisterButton = (Button) rootView.findViewById(R.id.detail_register_button);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerToEvent(mDecoratedEvent);
            }
        });


        updateView();

        return rootView;
    }


    private void updateView()
    {
        Event instance = mDecoratedEvent.getEvent();

        if (instance.getName() != null)
            mTitleView.setText(instance.getName());

        if(instance.getDescription() !=null)
            mDescriptionView.setText(instance.getDescription());

        if(instance.getSports() !=null)
            mSportsView.setText(Utils.getSports(instance));

        if(instance.getStartDate() !=null)
            mDateView.setText(Utils.getEventDate(getActivity(), instance));

        if((null != instance.getMaxParticipants()) && (instance.getEntriesAvailable() != null))
            mParticipantsView.setText(Utils.getFormattedParticipants(getActivity(), instance));

        if (instance.getPlaceName() != null)
            mPlaceView.setText(instance.getPlaceName());

        if (instance.getCoordinates() != null)
            setUpMap();

        mRegisterButton.setText(mDecoratedEvent.isRegistered() ? R.string.unregister
                : R.string.register);


    }


    private void setUpMap() {
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction =
                    getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.maps, mMapFragment);
            fragmentTransaction.commit();
            mMapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        GeoPt geoPt = mDecoratedEvent.getEvent().getCoordinates();
        LatLng latLng = new LatLng(geoPt.getLatitude(), geoPt.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
        );
    }

    private void registerToEvent(DecoratedEvent DecoratedEvent) {
        new RegistrationAsyncTask(DecoratedEvent).execute();
    }


    //TODO better unregister/register AsyncTask
    class RegistrationAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private final DecoratedEvent mDecoratedEvent;
        private Exception mException;

        public RegistrationAsyncTask(DecoratedEvent event) {
            this.mDecoratedEvent = event;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                try {
                    int entriesAvailable = mDecoratedEvent.getEvent().getEntriesAvailable();
                    if (mDecoratedEvent.isRegistered()) {
                        //unregister the user @api call
                        boolean success = EventUtils.unregisterFromEvent(mDecoratedEvent.getEvent());
                        if (success) {
                            Log.i(LOG_TAG,"unregister success");
                            mDecoratedEvent.getEvent().setEntriesAvailable(entriesAvailable+1);
                            mDecoratedEvent.setRegistered(false);
                        }
                        return success;
                    } else {
                        boolean success = EventUtils.registerForEvent(mDecoratedEvent.getEvent());
                        if (success) {
                            Log.i(LOG_TAG,"register success");
                            mDecoratedEvent.getEvent().setEntriesAvailable(entriesAvailable-1);
                            mDecoratedEvent.setRegistered(true);
                        }
                        return success;
                    }
                } catch (IOException e) {
                    mException = e;
                }
            } catch(Exception e) {
                Log.e(LOG_TAG, "Exception :  " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != result && result.booleanValue()) {
                updateView();
                String message = ((mDecoratedEvent.isRegistered()) ? "Registered " : "Unregistered") + " to " + mDecoratedEvent.getEvent().getName();
                Utils.displayToastMessage(message, getActivity());
            } else {
                if (mException != null) {
                    Utils.displayToastMessage(mException.getMessage(), getActivity());
                }
            }
        }
    }
}