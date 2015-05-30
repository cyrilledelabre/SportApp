package com.cyrilledelabre.riosportapp.MainPackage.CreateEvent;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.appspot.riosportapp.event.model.EventForm;
import com.appspot.riosportapp.event.model.GeoPt;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.cyrilledelabre.riosportapp.utils.eventUtils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.eventUtils.EventException;
import com.cyrilledelabre.riosportapp.utils.eventUtils.EventUtils;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class addEventAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
    private final String LOG_TAG = TextEventsForm.class.getSimpleName();


    private boolean mEditMode;
    private boolean mFailure;
    private EventForm mEventForm;
    private DecoratedEvent mDecoratedEvent;
    private FormSlideActivity.ViewHolder mHolder;
    private Context mContext;

    public addEventAsyncTask(Boolean editMode, FormSlideActivity.ViewHolder holder, DecoratedEvent decoratedEvent, Context context) {
        mEditMode = editMode;
        mFailure = false;
        if(mEditMode)
        {
            mDecoratedEvent = decoratedEvent;
        }
        mHolder = holder ;
        mContext = context;
    }



    @Override
    protected void onPreExecute() {
        DateTime startDate,endDate;
        GeoPt mLocalisation;
        String mPlaceName;
        String mTitle, mDescription;
        try{
            startDate= createStartDate();
            endDate = createEndDate();
            mLocalisation = createLocalisation();
            mPlaceName = getPlaceName();
            mTitle = verifyingText(mHolder.mTitleView.getText().toString(), "Title");
            mDescription = verifyingText(mHolder.mDescriptionView.getText().toString(), "Description");


        }catch(EventException e)
        {
            Utils.displayToastMessage(e.getMessage(), mContext);
            mFailure = true;
            return;
        }

        String mSports = mHolder.mSportsView.getSelectedItem().toString();
        String mParticipants = mHolder.mParticipantsView.getText().toString();
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

        createEventForm(mTitle, mDescription, startDate, endDate, mParticipantsToInt, sports, mLocalisation, mPlaceName);


    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if(mFailure)
            return false;

        try {
            if(mEditMode)
            {
                EventUtils.updateEvent(mDecoratedEvent.getEvent(), mEventForm);
            }
            else
            {
                EventUtils.createEvent(mEventForm);
            }
        } catch (IOException e) {
            String message = "Error with "+mHolder.mTitleView.getText();
            Utils.displayToastMessage(message, mContext);
            return false;
        } catch (EventException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        String message;
        if (null != result && result.booleanValue())
        {
            message = mHolder.mTitleView.getText() + (!mEditMode ? " created !" : " modified !");
            Utils.displayToastMessage(message, mContext);
        }

    }

    private String verifyingText(String text, String error) throws EventException
    {
        if (text.equals(""))
        {
            throw new EventException("You forgot the " + error);
        }
        return text;
    }

    private DateTime createStartDate () throws EventException {
        Calendar newDate = Calendar.getInstance();
        Log.i(LOG_TAG, "createStartDate : "+mHolder.startDay);
        if(mHolder.startDay ==0 &&  mHolder.startMonth ==0 && mHolder.startHour ==0)
        {
            throw new EventException("You Forgot a Start Date");
        }

        newDate.set(mHolder.startYear, mHolder.startMonth, mHolder.startDay, mHolder.startHour,mHolder.startMinute);
        return new DateTime(newDate.getTime() ,  TimeZone.getTimeZone("UTC"));
    }

    private DateTime createEndDate()
    {
        Calendar newDate = Calendar.getInstance();
        newDate.set(mHolder.endYear, mHolder.endMonth, mHolder.endDay, mHolder.endHour, mHolder.endMinute);
        return new DateTime(newDate.getTime() ,  TimeZone.getTimeZone("UTC"));
    }

    private GeoPt createLocalisation() throws EventException {
        GeoPt geoPt = mHolder.mLocalisation;

        if (geoPt == null)
            throw new EventException("You Forgot to set a Place ! ");

        return geoPt;
    }

    private String getPlaceName()
    {
        if (mHolder.mPlaceName.getText() != null)
        {
            return mHolder.mPlaceName.getText().toString();
        }
        return null;
    }


    private void createEventForm(
            String mTitle, String mDescription, DateTime startDate,
            DateTime endDate,int mParticipantsToInt,ArrayList<String>  mSports,
            GeoPt mLocalisation, String placeName)
    {
        mEventForm = new EventForm();
        mEventForm.setName(mTitle);
        mEventForm.setDescription(mDescription);
        mEventForm.setEndDate(endDate);
        mEventForm.setStartDate(startDate);
        mEventForm.setEntriesAvailable(mParticipantsToInt);
        mEventForm.setMaxParticipants(mParticipantsToInt);
        mEventForm.setSports(mSports);
        mEventForm.setCoordinates(mLocalisation);
        mEventForm.setPlaceName(placeName);
    }
}
