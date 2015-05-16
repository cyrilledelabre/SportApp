package com.cyrilledelabre.riosportapp.DetailEvent;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.riosportapp.event.model.Event;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.EventUtils;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 * On recupere les données avec un Intent
 * Et on les affiche dans la view
 */
public class DetailFragment extends Fragment{
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private DecoratedEvent mDecoratedEvent;
    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mDateView;
    private TextView mSportsView;
    private TextView mParticipantsView;
    private Button mRegisterButton;

    public DetailFragment() {
        setHasOptionsMenu(true); //otherwise not call the oncreatemenuoptions
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detailevent_fragment_detail, container, false);

        if( savedInstanceState == null)
        {
            //recuperer l'objet
            mDecoratedEvent = (DecoratedEvent) EventBus.getDefault().removeStickyEvent(DecoratedEvent.class);
            // on set la vue
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

        }
        updateView();

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_event, menu);

    }

    /**
     * TODO later
     * @return

    private Intent createShareEventIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT ); //return to your applicatio
        shareIntent.setType("text/plain"); //what are you sharing
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                eventStr); //put the str
        return shareIntent;
    }
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    private void updateView()
    {
        Event instance = mDecoratedEvent.getEvent();

        mTitleView.setText(instance.getName());

        if(instance.getDescription() !=null)
            mDescriptionView.setText(instance.getDescription());

        if(instance.getSports() !=null)
            mSportsView.setText(Utils.getSports(instance));

        if(instance.getStartDate() !=null)
            mDateView.setText(Utils.getEventDate(getActivity(),instance));

        if((null != instance.getMaxParticipants()) && (instance.getEntriesAvailable() != null))
            mParticipantsView.setText(Utils.getFormattedParticipants(getActivity(),instance));


        mRegisterButton.setText(mDecoratedEvent.isRegistered() ? R.string.unregister
                : R.string.register);


    }

    private void registerToEvent(DecoratedEvent DecoratedEvent) {
        new RegistrationAsyncTask(DecoratedEvent).execute();
    }

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
                            //TODO local unregister better
                            mDecoratedEvent.getEvent().setEntriesAvailable(entriesAvailable+1);
                            mDecoratedEvent.setRegistered(false);
                        }
                        return success;
                    } else {
                        boolean success = EventUtils.registerForEvent(mDecoratedEvent.getEvent());
                        if (success) {
                            Log.i(LOG_TAG,"register success");
                            //TODO local unregister better
                            mDecoratedEvent.getEvent().setEntriesAvailable(entriesAvailable-1);
                            mDecoratedEvent.setRegistered(true);

                        }
                        return success;
                    }
                } catch (IOException e) {
                    mException = e;
                }
            } catch(Exception e) {
                //logged
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (null != result && result.booleanValue()) {
                // success
                Log.i(LOG_TAG,"onPostExecuteSuccess");

                updateView();
            } else {
                // failure
                Log.e(LOG_TAG, "Failed to perform registration update", mException);
                if (mException != null) {
                    Utils.displayNetworkErrorMessage(getActivity());
                }
            }
        }
    }






}