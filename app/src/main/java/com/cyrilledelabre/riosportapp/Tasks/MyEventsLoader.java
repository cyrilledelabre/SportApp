package com.cyrilledelabre.riosportapp.Tasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.cyrilledelabre.riosportapp.utils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.EventException;
import com.cyrilledelabre.riosportapp.utils.EventUtils;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.io.IOException;
import java.util.List;

/**
 * Created by cyrilledelabre on 21/05/15.
 */

public class MyEventsLoader extends AsyncTaskLoader<List<DecoratedEvent>> {

    private static final String TAG = "MyEventsLoader";
    private Exception mException;

    public MyEventsLoader(Context context) {
        super(context);
    }

    @Override
    public List<DecoratedEvent> loadInBackground() {
        try {
            EventUtils.getProfile();
            return EventUtils.getMyEvents();
        } catch (IOException e) {
            Log.e(TAG, "Failed to get events", e);
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
