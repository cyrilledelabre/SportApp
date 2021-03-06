/* Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cyrilledelabre.riosportapp.utils.eventUtils;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appspot.riosportapp.event.model.GeoPt;
import com.cyrilledelabre.riosportapp.MainPackage.CreateEvent.FormSlideActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.Tasks.ApiTask.DeleteEventAsyncTask;
import com.cyrilledelabre.riosportapp.utils.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class EventDataAdapter extends ArrayAdapter<DecoratedEvent> {

    private static final String LOG_TAG = EventDataAdapter.class.getSimpleName();

    private final FragmentActivity mContext;
    private GoogleMap mGoogleMap;
    HashMap<String, Integer> mMarkers;


    public EventDataAdapter(FragmentActivity context) {
        super(context, 0);
        this.mContext = context;
        mMarkers = new HashMap<String, Integer>();
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DecoratedEvent decoratedEvent = getItem(position);
        boolean owner = decoratedEvent.isOwner();
        if(owner)
        {
            return getOwnerView(convertView, decoratedEvent);
        }else
        {
            return getUserView(convertView, decoratedEvent);
        }
    }

    private View getUserView(View convertView, DecoratedEvent decoratedEvent)
    {
        ViewHolder holder;
        //TODO see what does that do
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            //create
            convertView = inflater.inflate(R.layout.fragment_main_events, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.textEventTitle);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.textEventDescription);
            holder.dateView = (TextView) convertView.findViewById(R.id.textEventDate);
            holder.registerView = (ImageView) convertView.findViewById(R.id.imageIsRegisteredView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(decoratedEvent.getEvent().getName());
        holder.descriptionView.setText(decoratedEvent.getEvent().getDescription());
        holder.dateView.setText(Utils.getEventDate(mContext, decoratedEvent.getEvent()));
        holder.registerView.setVisibility(decoratedEvent.isRegistered() ? View.VISIBLE : View.GONE);

    return convertView;
    }


    private View getOwnerView(View convertView, final DecoratedEvent decoratedEvent)
    {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            //create
            convertView = inflater.inflate(R.layout.fragment_my_events, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.textEventTitle);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.textEventDescription);
            holder.dateView = (TextView) convertView.findViewById(R.id.textEventDate);
            holder.deleteView = (Button) convertView.findViewById(R.id.deleteEvent);
            holder.editView = (Button) convertView.findViewById(R.id.editEvent);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(decoratedEvent.getEvent().getName());
        holder.descriptionView.setText(decoratedEvent.getEvent().getDescription());
        holder.dateView.setText(Utils.getEventDate(mContext, decoratedEvent.getEvent()));
        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEvent(decoratedEvent);
            }
        });
        holder.editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEvent(decoratedEvent);
            }
        });

        return convertView;
    }

    public void deleteEvent(DecoratedEvent decoratedEvent)
    {
        DeleteEventAsyncTask deleteEventAsyncTask = new DeleteEventAsyncTask(decoratedEvent, mContext);
        deleteEventAsyncTask.execute();
        remove(decoratedEvent);
    }

    public void editEvent(DecoratedEvent decoratedEvent)
    {
        de.greenrobot.event.EventBus.getDefault().postSticky(decoratedEvent);

        Intent intent = new Intent(mContext,FormSlideActivity.class);
        de.greenrobot.event.EventBus.getDefault().postSticky(decoratedEvent);
        mContext.startActivity(intent);
        /*
        How to remplace current view // funny
        TextEventsForm fragment = new TextEventsForm();
        mContext.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();*/
    }

    public void addMarker(DecoratedEvent decoratedEvent) {
        if (mGoogleMap != null) {
            //TODO catch secure
            GeoPt geoPt = decoratedEvent.getEvent().getCoordinates();
            if (geoPt != null) {
                //convert geoPt to LatLng
                LatLng latLng = new LatLng(geoPt.getLatitude(), geoPt.getLongitude());
                String name = decoratedEvent.getEvent().getName();
                String desc = decoratedEvent.getEvent().getDescription();
                Log.e(LOG_TAG, "Marker added for : " + name);

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(name)
                        .snippet(desc));

                //save the position with the marker
                mMarkers.put(marker.getId(), getPosition(decoratedEvent));
            }
        }

    }


    public DecoratedEvent getDecoratedEventFrom(Marker marker) {
        return getItem(mMarkers.get(marker.getId()));
    }


    private class ViewHolder {

        TextView titleView;
        TextView descriptionView;
        TextView dateView;
        ImageView registerView;
        Button deleteView;
        Button editView;
    }

    public void setData(List<DecoratedEvent> data) {
        clear();
        if (mGoogleMap != null) mGoogleMap.clear();
        if (data != null) {
            for (DecoratedEvent item : data) {
                add(item);
                if (mGoogleMap != null) addMarker(item);
            }
        }

    }
}