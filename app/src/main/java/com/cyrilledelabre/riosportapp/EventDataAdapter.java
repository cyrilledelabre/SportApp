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
package com.cyrilledelabre.riosportapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyrilledelabre.riosportapp.utils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.util.List;

/**
 *
 */
public class EventDataAdapter extends ArrayAdapter<DecoratedEvent> {

    private static final String TAG = "EventDataAdapter";

    private final Context mContext;

    public EventDataAdapter(Context context) {
        super(context, 0);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        DecoratedEvent decoratedEvent = getItem(position);

        if (convertView == null) {
            //create
            convertView = inflater.inflate(R.layout.mainevents_event_row, null);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.textEventTitle);
            holder.descriptionView = (TextView) convertView.findViewById(R.id.textEventDescription);
            holder.dateView = (TextView) convertView.findViewById(R.id.textView3);
            holder.registerView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(decoratedEvent.getEvent().getName());
        holder.descriptionView.setText(decoratedEvent.getEvent().getDescription());
        holder.dateView.setText(Utils.getEventDate(mContext,decoratedEvent.getEvent()));
        holder.registerView
                .setVisibility(decoratedEvent.isRegistered() ? View.VISIBLE : View.GONE);

        return convertView;
    }

    private class ViewHolder {

        TextView titleView;
        TextView descriptionView;
        TextView dateView;
        ImageView registerView;
    }

    public void setData(List<DecoratedEvent> data) {
        clear();
        if (data != null) {
            for (DecoratedEvent item : data) {
                add(item);
            }
        }

    }
}