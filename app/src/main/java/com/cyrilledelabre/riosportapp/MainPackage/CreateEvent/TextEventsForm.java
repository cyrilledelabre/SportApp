package com.cyrilledelabre.riosportapp.MainPackage.CreateEvent;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.appspot.riosportapp.event.model.Event;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.utils.eventUtils.DecoratedEvent;

import de.greenrobot.event.EventBus;

/**
 * A placeholder fragment containing a simple view.
 * On recupere les données avec un Intent
 * Et on les affiche dans la view
 */
public class TextEventsForm extends Fragment{
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;


    private final String LOG_TAG = TextEventsForm.class.getSimpleName();

    private static FormSlideActivity.ViewHolder mHolder;





    private boolean editMode;
    DecoratedEvent mDecoratedEvent;


    public TextEventsForm() {
        setHasOptionsMenu(true); //otherwise not call the oncreatemenuoptions
    }

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static TextEventsForm create(int pageNumber, FormSlideActivity.ViewHolder holder) {
        mHolder =holder;
        TextEventsForm fragment = new TextEventsForm();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    /**
     * Add menu layout
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.activity_screen_slide, menu);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_create_events_form, container, false);

        if(savedInstanceState ==null)
        {
            findViewsById(rootView);

        }
        mDecoratedEvent = EventBus.getDefault().getStickyEvent(DecoratedEvent.class);
        if(mDecoratedEvent !=null)
        {
            setLastData(mDecoratedEvent);
            editMode = true;
        }else
        {
            editMode = false;
        }
        return rootView;
    }


    private void setLastData(DecoratedEvent mDecoratedEvent)
    {
        Context mContext = getActivity();
        Event event = mDecoratedEvent.getEvent();
        mHolder.mTitleView.setText(event.getName());
        mHolder.mDescriptionView.setText(event.getDescription());
        //TODO do something for Sports Selection;
        // String sport =  event.getSports().get(0);
        mHolder.mSportsView.setSelection(0, true);
        mHolder.mParticipantsView.setText(event.getMaxParticipants().toString());

    }

    private void findViewsById(View rootView) {
        mHolder.mTitleView = (TextView) rootView.findViewById(R.id.titleEvent);
        mHolder.mDescriptionView = (TextView) rootView.findViewById(R.id.descEvent);
        mHolder.mSportsView = (Spinner) rootView.findViewById(R.id.eventType);
        mHolder.mParticipantsView = (TextView) rootView.findViewById(R.id.participants);

    }





    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}






