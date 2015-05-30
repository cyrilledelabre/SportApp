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
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.cyrilledelabre.riosportapp.utils.eventUtils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.eventUtils.EventException;
import com.cyrilledelabre.riosportapp.utils.eventUtils.EventUtils;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.io.IOException;
import java.util.List;

public class EventsLoader extends AsyncTaskLoader<List<DecoratedEvent>> {

    private final String LOG_TAG = EventsLoader.class.getSimpleName();
    private Exception mException;
    private Context mContext;

    public EventsLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public List<DecoratedEvent> loadInBackground() {
        try {
            //EventUtils.getProfile();
            return EventUtils.getEventsFromRadius(Utils.getFormattedRadius(mContext));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get events", e);
            mException = e;
        } catch (EventException e) {
            // logged
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
        if (mException != null) {
            Utils.displayNetworkErrorMessage(getContext());
        }
    }

    public Exception getException() {
        return mException;
    }

}