package com.cyrilledelabre.riosportapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appspot.riosportapp.event.model.EventForm;
import com.cyrilledelabre.riosportapp.utils.EventException;
import com.cyrilledelabre.riosportapp.utils.EventUtils;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 * On recupere les données avec un Intent
 * Et on les affiche dans la view
 */
public class CreateEventsForm extends Fragment{
    private final String LOG_TAG = CreateEventsForm.class.getSimpleName();

    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mDateView;
    private TextView mSportsView;
    private TextView mParticipantsView;
    private Button mSendButton;

    public CreateEventsForm() {
        setHasOptionsMenu(true); //otherwise not call the oncreatemenuoptions
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_events_form, container, false);
        //recuperer l'objet

        // on set la vue
        mTitleView = (TextView) rootView.findViewById(R.id.titleEvent);
        mDescriptionView = (TextView) rootView.findViewById(R.id.descEvent);
        mSendButton = (Button) rootView.findViewById(R.id.sendButton);


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addEventAsyncTask().execute();

            }

        });


        /*// capture our View elements
        //mPickTime = (Button) rootView.findViewById(R.id.pickTime);

        // add a click listener to the button
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().showDialog(TIME_DIALOG_ID);
            }
        });*/


        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //inflater.inflate(R.menu.detail_event, menu);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    /** Called when the activity is first created. */
    private TextView mTimeDisplay;
    private Button mPickTime;

    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 0;


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(getActivity().getBaseContext(),
                        mTimeSetListener, mHour, mMinute, false);
        }
        return null;
    }


    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;

                }
            };



    public boolean addEvent() {

        String mTitle = mTitleView.getText().toString();
        String mDescription = mDescriptionView.getText().toString();
        // Jan = 0, dec = 11
        DateTime startDate=new DateTime(
                new GregorianCalendar(2013,0,31).getTime(),
                TimeZone.getTimeZone("UTC")
        );

        DateTime endDate = new DateTime(
                new GregorianCalendar(2015,2,31).getTime(),
                TimeZone.getTimeZone("UTC")
        );

        int maxParticipants = 100;
        ArrayList<String> sports  = new ArrayList();
        EventForm eventForm = new EventForm();
        eventForm.setName(mTitle);
        eventForm.setDescription(mDescription);
        eventForm.setEndDate(endDate);
        eventForm.setStartDate(startDate);
        eventForm.setEntriesAvailable(maxParticipants);
        eventForm.setMaxParticipants(maxParticipants);
        eventForm.setSports(sports);
        Log.e(LOG_TAG, "startDate 31 janvier 2013 : " + startDate.toString() );


        try {
            EventUtils.createEvent(eventForm);
        } catch (EventException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    class addEventAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private Exception mException;

        public addEventAsyncTask() {
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                        return addEvent();
            } catch(Exception e) {
                //logged
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != result && result.booleanValue())
                Toast.makeText(getActivity(), mTitleView.getText() + " created", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
        }
    }



}






