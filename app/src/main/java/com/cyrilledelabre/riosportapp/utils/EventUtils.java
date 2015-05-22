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

package com.cyrilledelabre.riosportapp.utils;

import android.content.Context;
import android.util.Log;

import com.appspot.riosportapp.event.model.Event;
import com.appspot.riosportapp.event.model.EventCollection;
import com.appspot.riosportapp.event.model.EventForm;
import com.appspot.riosportapp.event.model.Profile;
import com.appspot.riosportapp.event.model.WrappedBoolean;
import com.cyrilledelabre.riosportapp.AppConstants;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for communication with the Cloud Endpoint.
 */
public class EventUtils {

    private static final String LOG_TAG = EventUtils.class.getSimpleName();
    private static com.appspot.riosportapp.event.Event sApiServiceHandler;

    public static void build(Context context, String email) {
         sApiServiceHandler = buildServiceHandler(context, email);
    }

    /**
     * Returns a list of {@link com.cyrilledelabre.riosportapp.utils.DecoratedEvent}s.
     * This list includes information about what {@link com.appspot.riosportapp.event.Event}
     * user has registered for.
     *
     * @return
     * @throws EventException
     * @see <code>getProfile</code>
     */
    public static List<DecoratedEvent> getEvents()
            throws EventException, IOException {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "getEvents(): no service handler was built");
            throw new EventException();

        }
        com.appspot.riosportapp.event.Event.QueryEvents queryEvents = sApiServiceHandler.queryEvents(null);
        EventCollection eventCollection = queryEvents.execute();

        if (eventCollection != null && eventCollection.getItems() != null) {
            List<com.appspot.riosportapp.event.model.Event> events = eventCollection.getItems();

            List<DecoratedEvent> decoratedList = null;

            if (null == events || events.isEmpty()) {
                //return null
                return decoratedList;
            }
            //getRegisteredAttribute
            Profile profile = getProfile();
            List<String> registeredConfKeys = null;
            if (null != profile) {
                registeredConfKeys = profile.getEventsKeysToJoin();
            }
            if (null == registeredConfKeys) {
                registeredConfKeys = new ArrayList<String>();
            }

            decoratedList = new ArrayList<>();
            for (Event event : events) {
                DecoratedEvent decorated = new DecoratedEvent(event,registeredConfKeys.contains(event.getWebsafeKey()),false);
                decoratedList.add(decorated);
            }
            return decoratedList;
        }
        return null;
    }


    /**
     * Returns a list of {@link com.cyrilledelabre.riosportapp.utils.DecoratedEvent}s.
     * This list includes information about what {@link com.appspot.riosportapp.event.Event}
     * user has registered for.
     *
     * @return
     * @throws EventException
     * @see <code>getProfile</code>
     */
    public static List<DecoratedEvent> getMyEvents()
            throws EventException, IOException {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "getEvents(): no service handler was built");
            throw new EventException();

        }
        com.appspot.riosportapp.event.Event.GetEventsCreated queryEvents = sApiServiceHandler.getEventsCreated();
        EventCollection eventCollection = queryEvents.execute();

        if (eventCollection != null && eventCollection.getItems() != null) {
            List<com.appspot.riosportapp.event.model.Event> events = eventCollection.getItems();
            List<DecoratedEvent> decoratedList = null;
            if (null == events || events.isEmpty()) {
                return decoratedList;
            }
            decoratedList = new ArrayList<DecoratedEvent>();
            Profile profile = getProfile();
            List<String> registeredConfKeys = null;
            if (null != profile) {
                registeredConfKeys = profile.getEventsKeysToJoin();
            }
            if (null == registeredConfKeys) {
                registeredConfKeys = new ArrayList<String>();
            }
            for (Event event : events) {
                DecoratedEvent decorated = new DecoratedEvent(event,registeredConfKeys.contains(event.getWebsafeKey()),true);
                decoratedList.add(decorated);
            }
            return decoratedList;
        }
        return null;
    }

    /**
     * Registers user for a {@link com.appspot.riosportapp.event.Event}
     * @param event
     * @return result boolean Query
     * @throws EventException, IOException
     */
    public static boolean registerForEvent(Event event)
            throws EventException, IOException {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "registerForEvent(): no service handler was built");
            throw new EventException();
        }

        com.appspot.riosportapp.event.Event.RegisterForEvent
                registerForEvent = sApiServiceHandler.registerForEvent(
                event.getWebsafeKey());
        WrappedBoolean result = registerForEvent.execute();
        return result.getResult();
    }

    /**
     * Unregisters user from a {@link com.appspot.riosportapp.event.Event}.
     * @param event
     * @return result boolean Query
     * @throws EventException
     */
    public static boolean unregisterFromEvent(Event event)
            throws EventException, IOException {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "unregisterFromEvent(): no service handler was built");
            throw new EventException();
        }

        com.appspot.riosportapp.event.Event.UnregisterFromEvent
                unregisterFromEvent = sApiServiceHandler.unregisterFromEvent(
                event.getWebsafeKey());
        WrappedBoolean result = unregisterFromEvent.execute();
        return result.getResult();
    }

    /**
     * create a Event of {@link com.appspot.riosportapp.event.Event}.
     *
     * @param  event EventForm
     * @return event Event
     * @throws EventException
     */
    public static Event createEvent(EventForm event) throws EventException, IOException
    {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "createEvent(...): no service handler was built");
            throw new EventException();
        }
        com.appspot.riosportapp.event.Event.CreateEvent
                createEvent = sApiServiceHandler.createEvent(event);
        return createEvent.execute();

    }


    /**
     * updateEvent a Event of {@link com.appspot.riosportapp.event.Event}.
     *
     * @param  event EventForm
     * @return event Event
     * @throws EventException, IOException
     */
    public static Event updateEvent(Event event, EventForm updateEvent) throws EventException, IOException
    {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "updateEvent(...): no service handler was built");
            throw new EventException();
        }


        com.appspot.riosportapp.event.Event.UpdateEvent
                mEvent = sApiServiceHandler.updateEvent(event.getWebsafeKey(), updateEvent);
        return mEvent.execute();

    }

    /**
     * delete  a Event of {@link com.appspot.riosportapp.event.Event}.
     *
     * @param  event EventForm
     * @return event Event
     * @throws EventException
     */
    public static boolean deleteEvent(Event event) throws EventException, IOException
    {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "deleteEvent(...): no service handler was built");
            throw new EventException();
        }
        com.appspot.riosportapp.event.Event.DeleteEvent
                unregisterFromEvent = sApiServiceHandler.deleteEvent(
                event.getWebsafeKey());
        WrappedBoolean result = unregisterFromEvent.execute();
        return result.getResult();

    }

    /**
     * Returns the user {@link com.appspot.riosportapp.event.model.Profile}. Can
     * be used to find out what events user is registered for.
     *
     * @return
     * @throws EventException
     */
    public static Profile getProfile() throws EventException, IOException {
        if (null == sApiServiceHandler) {
            Log.e(LOG_TAG, "getProfile(): no service handler was built");
            throw new EventException();
        }

        com.appspot.riosportapp.event.Event.GetProfile getProfile =
                sApiServiceHandler.getProfile();

        Profile profile  = getProfile.execute();

        return profile;
    }

    /**
     * Build and returns an instance of {@link com.appspot.riosportapp.event.Event}
     *
     * @param context
     * @param email
     * @return
     */
    public static com.appspot.riosportapp.event.Event buildServiceHandler(
            Context context, String email) {
        // get the audience
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                context, AppConstants.AUDIENCE);
        credential.setSelectedAccountName(email);

        com.appspot.riosportapp.event.Event.Builder builder
                = new com.appspot.riosportapp.event.Event.Builder(
                AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY, credential);
        builder.setApplicationName("AndroidSportApp");


        return builder.build();
    }



}
