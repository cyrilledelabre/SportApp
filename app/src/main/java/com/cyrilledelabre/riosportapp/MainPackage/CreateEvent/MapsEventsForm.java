package com.cyrilledelabre.riosportapp.MainPackage.CreateEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.appspot.riosportapp.event.model.Event;
import com.appspot.riosportapp.event.model.GeoPt;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.localisationProvider.NetworkProvider;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.cyrilledelabre.riosportapp.utils.eventUtils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.maps.PlaceAutocompleteAdapter;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 * On recupere les données avec un Intent
 * Et on les affiche dans la view
 */
public class MapsEventsForm extends Fragment {
    private static final String LOG_TAG = TextEventsForm.class.getSimpleName();

    private  int mPageNumber;
    public static final String ARG_PAGE = "page";

    private static int PLACE_PICKER_REQUEST = 1;



    private Context mContext;
    private TextView mName;
    private TextView mAddress;


    /**
     * GoogleApiClientSingleton wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;
    private static FormSlideActivity.ViewHolder mHolder;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;



    DecoratedEvent mDecoratedEvent;


    public MapsEventsForm() {
        setHasOptionsMenu(true);
    }

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static MapsEventsForm create(int pageNumber,  FormSlideActivity.ViewHolder holder) {
        mHolder = holder;
        MapsEventsForm fragment = new MapsEventsForm();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =getActivity().getApplicationContext();
        mPageNumber = getArguments().getInt(ARG_PAGE);

        dateFormatter = new SimpleDateFormat("dd-MMM-yy", Locale.US);
        timeFormatter = new SimpleDateFormat("HH-mm", Locale.US);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_events_form_map, container, false);


        setGoogleAutocomplete(rootView);
        setGooglePlacesPickUp(rootView);
        findViewById(rootView);
        setDateTimeField();
        setTimeField();


        if(mHolder.editMode) {
            setLastData(mHolder.mDecoratedEvent);
        }
        return rootView;
    }




    private void findViewById(View rootView){

        mHolder.mPlaceName = (TextView) rootView.findViewById(R.id.place_details);
        mHolder.mPlaceAddress = (TextView) rootView.findViewById(R.id.place_attribution);

        mHolder.mStartDateView = (EditText) rootView.findViewById(R.id.startDate);
        mHolder.mStartDateView.setInputType(InputType.TYPE_NULL);
        mHolder.mStartDateView.requestFocus();

        mHolder.mEndDateView = (EditText) rootView.findViewById(R.id.endDate);
        mHolder.mEndDateView.setInputType(InputType.TYPE_NULL);

        mHolder.mStartTimeView = (EditText) rootView.findViewById(R.id.startHour);
        mHolder.mStartTimeView.setInputType(InputType.TYPE_NULL);
        mHolder.mStartTimeView.requestFocus();

        mHolder.mEndTimeView = (EditText) rootView.findViewById(R.id.endHour);
        mHolder.mEndTimeView.setInputType(InputType.TYPE_NULL);

    }
    private void setDateTimeField() {
        //date
        mHolder.mStartDateView.setOnClickListener(
                new View.OnClickListener() {
                  Calendar newCalendar = Calendar.getInstance();

                  @Override
                  public void onClick(View v) {
                      new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                              mHolder.startDay = dayOfMonth ;  mHolder.startMonth = monthOfYear ;  mHolder.startYear = year;
                              Calendar newDate = Calendar.getInstance();
                              newDate.set(year, monthOfYear, dayOfMonth);
                              mHolder. mStartDateView.setText(dateFormatter.format(newDate.getTime()));
                          }
                      },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();

                  }
        });
        mHolder.mEndDateView.setOnClickListener(
                new View.OnClickListener() {
                    Calendar newCalendar = Calendar.getInstance();

                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mHolder.endDay = dayOfMonth;
                                mHolder.endMonth = monthOfYear;
                                mHolder.endYear = year;
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                mHolder.mEndDateView.setText(dateFormatter.format(newDate.getTime()));
                            }

                        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)).show();


                    }
                });
    }
    private void setTimeField() {
        //date
        mHolder.mStartTimeView.setOnClickListener(new View.OnClickListener() {
            Calendar newCalendar = Calendar.getInstance();

            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hour, int minutes) {
                        mHolder.startMinute = minutes;
                        mHolder.startHour = hour;
                        mHolder.mStartTimeView.setText(mHolder.startHour + "h" + mHolder.startMinute);

                    }

                }, newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), false).show();
            }
        });


        mHolder.mEndTimeView.setOnClickListener(new View.OnClickListener() {
            Calendar newCalendar = Calendar.getInstance();

            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hour, int minutes) {
                        mHolder.endHour = hour;
                        mHolder.endMinute = minutes;
                        mHolder.mEndTimeView.setText(mHolder.endHour + "h" + mHolder.endMinute);
                    }
                }, newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), false).show();
            }
        });
    }
    private void setLastData(DecoratedEvent mDecoratedEvent){
        Event event = mDecoratedEvent.getEvent();
        mHolder.mStartDateView.setText(Utils.getFormattedDay(mContext, event.getStartDate()));
        mHolder.mEndDateView.setText(Utils.getFormattedDay(mContext, event.getEndDate()));
        mHolder.mStartTimeView.setText(Utils.getFormattedTime(mContext, event.getStartDate()));
        mHolder.mEndTimeView.setText(Utils.getFormattedTime(mContext, event.getEndDate()));
        mHolder.mPlaceName.setText(event.getPlaceName());
    }


    private void setGooglePlacesPickUp(View view)
    {

        Button pickerButton = (Button) view.findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(NetworkProvider.getCurrentLocation());
                    Intent intent = intentBuilder.build(mContext.getApplicationContext());
                    getActivity().startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(LOG_TAG,"GooglePlacesPickup error "+ e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(LOG_TAG, "GooglePlayServicesNotAvailableException error " + e);
                }
            }
        });
    }


    private void setGoogleAutocomplete(View view)
    {

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                view.findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(mContext, R.layout.my_list_item,
                mHolder.mGoogleApiClient, NetworkProvider.getCurrentLocation(), null);

        mAutocompleteView.setAdapter(mAdapter);
    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mHolder.mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            Place place = places.get(0);
            mHolder.mLocalisation = new GeoPt();
            mHolder.mLocalisation.setLongitude((float) place.getLatLng().longitude);
            mHolder.mLocalisation.setLatitude((float) place.getLatLng().latitude);

            final CharSequence name = place.getName();
            if (name != null) {
                mHolder.mPlaceName.setVisibility(View.VISIBLE);
                mHolder.mPlaceName.setText(name);
            }
            // Display the third party attributions if set.
            final CharSequence address = place.getAddress();
            if (address != null) {
                mHolder.mPlaceAddress.setVisibility(View.VISIBLE);
                mHolder.mPlaceAddress.setText(address.toString());
            }
            places.release();
        }
    };


    /**
     * Add menu layout
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().onCreateOptionsMenu(menu);
    }

    /**
     * On Click Menu make some actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }
}
