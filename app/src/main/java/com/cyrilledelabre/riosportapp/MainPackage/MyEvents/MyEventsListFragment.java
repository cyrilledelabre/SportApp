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

package com.cyrilledelabre.riosportapp.MainPackage.MyEvents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;

import com.cyrilledelabre.riosportapp.MainPackage.DetailEvent.DetailActivity;
import com.cyrilledelabre.riosportapp.R;
import com.cyrilledelabre.riosportapp.Tasks.ApiTask.MyEventsLoader;
import com.cyrilledelabre.riosportapp.utils.DecoratedEvent;
import com.cyrilledelabre.riosportapp.utils.EventDataAdapter;
import com.cyrilledelabre.riosportapp.utils.Utils;

import java.util.List;

import de.greenrobot.event.EventBus;


public class MyEventsListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<List<DecoratedEvent>> {

    private final String LOG_TAG = MyEventsListFragment.class.getSimpleName();

    private EventDataAdapter mAdapter;

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().removeAllStickyEvents();
        //add layout
        getListView().setFastScrollEnabled(true);
        LayoutAnimationController controller = AnimationUtils
                .loadLayoutAnimation(getActivity(), R.anim.list_layout_controller);
        getListView().setLayoutAnimation(controller);

        //create adapter
        mAdapter = new EventDataAdapter(getActivity());

        setEmptyText(getString(R.string.no_events));

        setListAdapter(mAdapter);

        setListShown(false);
    }




    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(),DetailActivity.class);
        EventBus.getDefault().postSticky(mAdapter.getItem(position));
        this.startActivity(intent);
    }



    /*
     * (non-Javadoc)
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
     * android.os.Bundle)
     *
     * Start the EventsLoader Async Task
     */
    @Override
    public Loader<List<DecoratedEvent>> onCreateLoader(int arg0, Bundle arg1) {
        return new MyEventsLoader(getActivity());
    }

    /*
  * (non-Javadoc)
  * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
  * .support.v4.content.Loader)
  */
    @Override
    public void onLoaderReset(Loader<List<DecoratedEvent>> arg0) {
        mAdapter.setData(null);
    }


    /*
    * (non-Javadoc)
    * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
    * .support.v4.content.Loader, java.lang.Object)
    *
    * On load finished => Add the data to the adapter
    */
    @Override
    public void onLoadFinished(Loader<List<DecoratedEvent>> loader,
                               List<DecoratedEvent> data) {
        MyEventsLoader eventLoader = (MyEventsLoader) loader;
        if (eventLoader.getException() != null) {
            Utils.displayNetworkErrorMessage(getActivity());
            return;
        }

        //Add the data to the adapter
        mAdapter.setData(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    /*
    Constructor Instance //
     */
    public static MyEventsListFragment newInstance() {
        MyEventsListFragment f = new MyEventsListFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    public void loadEvents() {
        getLoaderManager().initLoader(0, null, this); //TODO see doc
    }


    public void reload() {
        setListShown(false);
        getLoaderManager().restartLoader(0, null, this).startLoading(); //TODO see doc
    }


}