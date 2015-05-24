/*
 * Copyright (C) 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyrilledelabre.riosportapp.Tasks.ApiTask;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cyrilledelabre.riosportapp.utils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.EventUtils;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.io.IOException;

public class DeleteEventAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private final DecoratedEvent mDecoratedEvent;
    private final Context mContext;
    private Exception mException;
    private final String LOG_TAG = DeleteEventAsyncTask.class.getSimpleName();

    public DeleteEventAsyncTask(DecoratedEvent event, Context context) {
        this.mContext = context;
        this.mDecoratedEvent = event;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            try {
                return EventUtils.deleteEvent(mDecoratedEvent.getEvent());
            } catch (IOException e) {
                mException = e;
            }
        } catch(Exception e) {
            mException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (null != result && result.booleanValue()) {
            Log.i(LOG_TAG, "success Delete Event : " + mDecoratedEvent.getEvent().getName());
            Utils.displayToastMessage(mDecoratedEvent.getEvent().getName() + " deleted", mContext);
        } else {
            Utils.displayToastMessage("Failed to perform deletion  "+mDecoratedEvent.getEvent().getName(), mContext);
            if (mException != null) {
                Log.e(LOG_TAG, "Deletion Exception : "+mDecoratedEvent.getEvent().getName() , mException);
            }
        }
    }
}