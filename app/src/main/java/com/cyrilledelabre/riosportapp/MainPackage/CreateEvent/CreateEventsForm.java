package com.cyrilledelabre.riosportapp.MainPackage.CreateEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.appspot.riosportapp.event.model.EventForm;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.EventException;
import com.cyrilledelabre.riosportapp.utils.EventUtils;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 * On recupere les données avec un Intent
 * Et on les affiche dans la view
 */
public class CreateEventsForm extends Fragment implements View.OnClickListener{
    private final String LOG_TAG = CreateEventsForm.class.getSimpleName();

    private TextView mTitleView;
    private TextView mDescriptionView;
    private EditText mStartDateView;
    private EditText mEndDateView;
    private EditText mStartTimeView;
    private EditText mEndTimeView;
    private Spinner mSportsView;
    private TextView mParticipantsView;
    private Button mSendButton;

    private int startHour, startMinute, startDay, startMonth, startYear;
    private int endHour, endMinute, endDay, endMonth, endYear;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;
    private SimpleDateFormat dateFormatter;


    public CreateEventsForm() {
        setHasOptionsMenu(true); //otherwise not call the oncreatemenuoptions
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_create_events_form, container, false);
        //recuperer l'objet
        if(savedInstanceState ==null)
        {

            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            // on set la vue
            findViewsById(rootView);
            setDateTimeField();
            setTimeField();
        }



        return rootView;
    }

    private void findViewsById(View rootView) {
        mTitleView = (TextView) rootView.findViewById(R.id.titleEvent);
        mDescriptionView = (TextView) rootView.findViewById(R.id.descEvent);

        mStartDateView = (EditText) rootView.findViewById(R.id.startDate);
        mStartDateView.setInputType(InputType.TYPE_NULL);
        mStartDateView.requestFocus();

        mEndDateView = (EditText) rootView.findViewById(R.id.endDate);
        mEndDateView.setInputType(InputType.TYPE_NULL);

        mStartTimeView = (EditText) rootView.findViewById(R.id.startHour);
        mStartTimeView.setInputType(InputType.TYPE_NULL);
        mStartTimeView.requestFocus();

        mEndTimeView = (EditText) rootView.findViewById(R.id.endHour);
        mEndTimeView.setInputType(InputType.TYPE_NULL);


        mSportsView = (Spinner) rootView.findViewById(R.id.eventType);
        mParticipantsView = (TextView) rootView.findViewById(R.id.participants);

        mSendButton = (Button) rootView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addEventAsyncTask().execute();

            }

        });
    }


    private void setDateTimeField() {
        //date
        mStartDateView.setOnClickListener(this);
        mEndDateView.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
               startDay = dayOfMonth ; startMonth = monthOfYear ; startYear = year;
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mStartDateView.setText(dateFormatter.format(newDate.getTime()));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endDay = dayOfMonth; endMonth = monthOfYear ; endYear = year;
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEndDateView.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


    }


    private void setTimeField() {
        //date
        mStartTimeView.setOnClickListener(this);
        mEndTimeView.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        fromTimePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minutes) {
                startMinute= minutes;startHour=hour;
                mStartTimeView.setText(startHour+"h"+startMinute);

            }

        },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE),true);

        toTimePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minutes) {
                endHour = hour ; endMinute = minutes;
                mEndTimeView.setText(endHour+"h"+endMinute);
            }

        },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE),true);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_event, menu);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    public boolean addEvent() {
        DateTime startDate, endDate;
        Calendar newDate = Calendar.getInstance();
        newDate.set(startYear, startMonth, startDay, startHour,startMinute);
        startDate = new DateTime(newDate.getTime() ,  TimeZone.getTimeZone("UTC"));
        newDate.set(endYear, endMonth, endDay, endHour,endMinute);
        endDate = new DateTime(newDate.getTime() ,  TimeZone.getTimeZone("UTC"));


        String mTitle = mTitleView.getText().toString();
        String mDescription = mDescriptionView.getText().toString();

        String mSports = mSportsView.getSelectedItem().toString();
        String mParticipants = mParticipantsView.getText().toString();
        int mParticipantsToInt;
        if(mParticipants.matches("\\d+")) //check if only digits. Could also be text.matches("[0-9]+")
        {
              mParticipantsToInt = Integer.parseInt(mParticipants);
        }else{
            mParticipantsToInt = 100;
        }
        ArrayList<String> sports  = new ArrayList();
        sports.add(mSports);

        //eventForm
        EventForm eventForm = new EventForm();
        eventForm.setName(mTitle);
        eventForm.setDescription(mDescription);
        eventForm.setEndDate(endDate);
        eventForm.setStartDate(startDate);
        eventForm.setEntriesAvailable(mParticipantsToInt);
        eventForm.setMaxParticipants(mParticipantsToInt);
        eventForm.setSports(sports);


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

    @Override
    public void onClick(View view) {
        if(view == mStartDateView) {
            fromDatePickerDialog.show();
        } else if(view == mEndDateView) {
            toDatePickerDialog.show();
        } else if( view == mStartTimeView){
            fromTimePickerDialog.show();
        } else if (view == mEndTimeView){
            toTimePickerDialog.show();
        }

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






