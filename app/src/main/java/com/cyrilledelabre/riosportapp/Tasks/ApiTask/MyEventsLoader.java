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

/**
 * Created by cyrilledelabre on 21/05/15.
 */

public class MyEventsLoader extends AsyncTaskLoader<List<DecoratedEvent>> {

    private final String LOG_TAG = MyEventsLoader.class.getSimpleName();
    private Exception mException;

    public MyEventsLoader(Context context) {
        super(context);
    }

    @Override
    public List<DecoratedEvent> loadInBackground() {
        try {
            return EventUtils.getMyEvents();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get events", e);
            mException = e;
        } catch (EventException e) {
            Log.e(LOG_TAG, "Failed to get events", e);
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
