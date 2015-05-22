package com.cyrilledelabre.riosportapp.backend.spi;

import com.cyrilledelabre.riosportapp.backend.Constants;
import com.cyrilledelabre.riosportapp.backend.domain.AppEngineUser;
import com.cyrilledelabre.riosportapp.backend.domain.Event;
import com.cyrilledelabre.riosportapp.backend.domain.Profile;
import com.cyrilledelabre.riosportapp.backend.form.EventForm;
import com.cyrilledelabre.riosportapp.backend.form.EventQueryForm;
import com.cyrilledelabre.riosportapp.backend.form.ProfileForm;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.cyrilledelabre.riosportapp.backend.service.OfyService.factory;
import static com.cyrilledelabre.riosportapp.backend.service.OfyService.ofy;

/**
 * Defines event APIs.
 */

@Api(
        name = "event",
        version = "v1",
        scopes = { Constants.EMAIL_SCOPE },
        clientIds = { Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID,
                Constants.API_EXPLORER_CLIENT_ID},
        audiences = { Constants.ANDROID_AUDIENCE },
        description = "Event API for creating and querying events," +
                " and for creating and getting user Profiles"
)
public class SportApi{

    private static final Logger LOG = Logger.getLogger(SportApi.class.getName());



    private static String extractDefaultDisplayNameFromEmail(String email) {
        return email == null ? null : email.substring(0, email.indexOf("@"));
    }

    private static Profile getProfileFromUser(User user, String userId) {
        // First fetch it from the datastore.
        Profile profile = ofy().load().key(
                Key.create(Profile.class, userId)).now();
        if (profile == null) {
            // Create a new Profile if not exist.
            String email = user.getEmail();
            profile = new Profile(userId,
                    extractDefaultDisplayNameFromEmail(email), email);
        }
        return profile;
    }

    /**
     * This is an ugly workaround for null userId for Android clients.
     *
     * @param user A User object injected by the cloud endpoints.
     * @return the App Engine userId for the user.
     */
    private static String getUserId(User user) {
        String userId = user.getUserId();
        if (userId == null) {
            //LOG.info("userId is null, so trying to obtain it from the datastore.");
            AppEngineUser appEngineUser = new AppEngineUser(user);
            ofy().save().entity(appEngineUser).now();
            // Begin new session for not using session cache.
            Objectify objectify = ofy().factory().begin();
            AppEngineUser savedUser = objectify.load().key(appEngineUser.getKey()).now();
            userId = savedUser.getUser().getUserId();
            LOG.info("Obtained the userId: " + userId);
        }
        return userId;
    }

    /**
     * Just a wrapper for Boolean.
     */
    public static class WrappedBoolean {

        private final Boolean result;

        public WrappedBoolean(Boolean result) {
            this.result = result;
        }

        public Boolean getResult() {
            return result;
        }
    }

    /**
     * A wrapper class that can embrace a generic result or some kind of exception.
     *
     * Use this wrapper class for the return type of objectify transaction.
     * <pre>
     * {@code
     * // The transaction that returns Event object.
     * TxResult<Event> result = ofy().transact(new Work<TxResult<Event>>() {
     *     public TxResult<Event> run() {
     *         // Code here.
     *         // To throw 404
     *         return new TxResult<>(new NotFoundException("No such event"));
     *         // To return a event.
     *         Event event = somehow.getEvent();
     *         return new TxResult<>(event);
     *     }
     * }
     * // Actually the NotFoundException will be thrown here.
     * return result.getResult();
     * </pre>
     *
     * @param <ResultType> The type of the actual return object.
     */
    private static class TxResult<ResultType> {

        private ResultType result;

        private Throwable exception;

        private TxResult(ResultType result) {
            this.result = result;
        }

        private TxResult(Throwable exception) {
            if (exception instanceof NotFoundException ||
                    exception instanceof ForbiddenException ||
                    exception instanceof ConflictException) {
                this.exception = exception;
            } else {
                throw new IllegalArgumentException("Exception not supported.");
            }
        }

        private ResultType getResult() throws NotFoundException, ForbiddenException, ConflictException {
            if (exception instanceof NotFoundException) {
                throw (NotFoundException) exception;
            }
            if (exception instanceof ForbiddenException) {
                throw (ForbiddenException) exception;
            }
            if (exception instanceof ConflictException) {
                throw (ConflictException) exception;
            }
            return result;
        }
    }

    /**
     * Returns a Profile object associated with the given user object. The cloud endpoints system
     * automatically inject the User object.
     *
     * @param user A User object injected by the cloud endpoints.
     * @return Profile object.
     * @throws UnauthorizedException when the User object is null.
     */
    @ApiMethod(name = "getProfile", path = "profile", httpMethod = HttpMethod.GET)
    public Profile getProfile(final User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        return ofy().load().key(Key.create(Profile.class, getUserId(user))).now();
    }

    /**
     * Creates or updates a Profile object associated with the given user object.
     *
     * @param user A User object injected by the cloud endpoints.
     * @param profileForm A ProfileForm object sent from the client form.
     * @return Profile object just created.
     * @throws UnauthorizedException when the User object is null.
     */
    @ApiMethod(name = "saveProfile", path = "profile", httpMethod = HttpMethod.POST)
    public Profile saveProfile(final User user, final ProfileForm profileForm)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        LOG.setLevel(Level.INFO);

        LOG.info("saveProfile: email : " + user.getEmail());

        String displayName = profileForm.getDisplayName();

        Profile profile = ofy().load().key(Key.create(Profile.class, getUserId(user))).now();
        if (profile == null) {
            if (displayName == null) {
                displayName = extractDefaultDisplayNameFromEmail(user.getEmail());
            }
            
            profile = new Profile(getUserId(user), displayName, user.getEmail());
        } else {
            profile.update(displayName);
        }
        ofy().save().entity(profile).now();
        return profile;
    }

    /**
     * Creates a new Event object and stores it to the datastore.
     *
     * @param user A user who invokes this method, null when the user is not signed in.
     * @param eventForm A EventForm object representing user's inputs.
     * @return A newly created Event Object.
     * @throws UnauthorizedException when the user is not signed in.
     */
    @ApiMethod(name = "createEvent", path = "event", httpMethod = HttpMethod.POST)
    public Event createEvent(final User user, final EventForm eventForm)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        // Allocate Id first, in order to make the transaction idempotent.
        Key<Profile> profileKey = Key.create(Profile.class, getUserId(user));
        final Key<Event> eventKey = factory().allocateId(profileKey, Event.class);
        final long eventId = eventKey.getId();
        final Queue queue = QueueFactory.getDefaultQueue();
        final String userId = getUserId(user);
        // Start a transaction.
        Event event = ofy().transact(new Work<Event>() {
            @Override
            public Event run() {
                // Fetch user's Profile.
                Profile profile = getProfileFromUser(user, userId);
                Event event = new Event(eventId, userId, eventForm);
                // Save Event and Profile.
                ofy().save().entities(event, profile).now();

                return event;
            }
        });
        return event;
    }

    /**
     * Updates the existing Event with the given eventId.
     *
     * @param user A user who invokes this method, null when the user is not signed in.
     * @param eventForm A EventForm object representing user's inputs.
     * @param websafeEventKey The String representation of the Event key.
     * @return Updated Event object.
     * @throws UnauthorizedException when the user is not signed in.
     * @throws NotFoundException when there is no Event with the given eventId.
     * @throws ForbiddenException when the user is not the owner of the Event.
     */
    @ApiMethod(
            name = "updateEvent",
            path = "event/{websafeEventKey}",
            httpMethod = HttpMethod.PUT
    )
    public Event updateEvent(final User user, final EventForm eventForm,
                                       @Named("websafeEventKey")
                                       final String websafeEventKey)
            throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        final String userId = getUserId(user);
        // Update the event with the eventForm sent from the client.
        // Need a transaction because we need to safely preserve the number of allocated participants.
        TxResult<Event> result = ofy().transact(new Work<TxResult<Event>>() {
            @Override
            public TxResult<Event> run() {
                // If there is no Event with the id, throw a 404 error.
                Key<Event> eventKey = Key.create(websafeEventKey);
                Event event = ofy().load().key(eventKey).now();
                if (event == null) {
                    return new TxResult<>(
                            new NotFoundException("No Event found with the key: "
                                    + websafeEventKey));
                }
                // If the user is not the owner, throw a 403 error.
                Profile profile = ofy().load().key(Key.create(Profile.class, userId)).now();
                if (profile == null ||
                        !event.getOrganizerUserId().equals(userId)) {
                    return new TxResult<>(
                            new ForbiddenException("Only the owner can update the event."));
                }
                event.updateWithEventForm(eventForm);
                ofy().save().entity(event).now();
                return new TxResult<>(event);
            }
        });
        // NotFoundException or ForbiddenException is actually thrown here.
        return result.getResult();
    }



    /**
     * Returns a Event object with the given eventId.
     *
     * @param websafeEventKey The String representation of the Event Key.
     * @return a Event object with the given eventId.
     * @throws NotFoundException when there is no Event with the given eventId.
     */
    @ApiMethod(
            name = "getEvent",
            path = "event/{websafeEventKey}",
            httpMethod = HttpMethod.GET
    )
    public Event getEvent(
            @Named("websafeEventKey") final String websafeEventKey)
            throws NotFoundException {
        Key<Event> eventKey = Key.create(websafeEventKey);
        Event event = ofy().load().key(eventKey).now();
        if (event == null) {
            throw new NotFoundException("No Event found with key: " + websafeEventKey);
        }
        return event;
    }

    /**
     * Returns a collection of Event Object that the user is going to attend.
     *
     * @param user An user who invokes this method, null when the user is not signed in.
     * @return a Collection of Events that the user is going to attend.
     * @throws UnauthorizedException when the User object is null.
     */
    @ApiMethod(
            name = "getEventsToAttend",
            path = "getEventsToAttend",
            httpMethod = HttpMethod.GET
    )
    public Collection<Event> getEventsToAttend(final User user)
            throws UnauthorizedException, NotFoundException {
        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        Profile profile = ofy().load().key(Key.create(Profile.class, getUserId(user))).now();
        if (profile == null) {
            throw new NotFoundException("Profile doesn't exist.");
        }
        List<String> keyStringsToAttend = profile.getEventsKeysToJoin();
        List<Key<Event>> keysToAttend = new ArrayList<>();
        for (String keyString : keyStringsToAttend) {
            keysToAttend.add(Key.<Event>create(keyString));
        }
        return ofy().load().keys(keysToAttend).values();
    }

    /**
     * Queries against the datastore with the given filters and returns the result.
     *
     * Normally this kind of method is supposed to get invoked by a GET HTTP method,
     * but we do it with POST, in order to receive eventQueryForm Object via the POST body.
     *
     * @param eventQueryForm A form object representing the query.
     * @return A List of Events that match the query.
     */
    @ApiMethod(
            name = "queryEvents",
            path = "queryEvents",
            httpMethod = HttpMethod.POST
    )
    public List<Event> queryEvents(EventQueryForm eventQueryForm) {
        Iterable<Event> eventIterable = eventQueryForm.getQuery();
        List<Event> result = new ArrayList<>(0);
        List<Key<Profile>> organizersKeyList = new ArrayList<>(0);
        for (Event event : eventIterable) {
            organizersKeyList.add(Key.create(Profile.class, event.getOrganizerUserId()));
            result.add(event);
        }
        // To avoid separate datastore gets for each Event, pre-fetch the Profiles.
        ofy().load().keys(organizersKeyList);
        return result;
    }

    /**
     * Returns a list of Events that the user created.
     * In order to receive the websafeEventKey via the JSON params, we uses a POST method.
     *
     * @param user An user who invokes this method, null when the user is not signed in.
     * @return a list of Events that the user created.
     * @throws UnauthorizedException when the user is not signed in.
     */
    @ApiMethod(
            name = "getEventsCreated",
            path = "getEventsCreated",
            httpMethod = HttpMethod.POST
    )
    public List<Event> getEventsCreated(final User user) throws UnauthorizedException {
        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        String userId = getUserId(user);
        return ofy().load().type(Event.class)
                .ancestor(Key.create(Profile.class, userId))
                .order("name").list();
    }

    /**
     * Register to the specified Event.
     *
     * @param user An user who invokes this method, null when the user is not signed in.
     * @param websafeEventKey The String representation of the Event Key.
     * @return Boolean true when success, otherwise false
     * @throws UnauthorizedException when the user is not signed in.
     * @throws NotFoundException when there is no Event with the given eventId.
     */
    @ApiMethod(
            name = "registerForEvent",
            path = "event/{websafeEventKey}/registration",
            httpMethod = HttpMethod.POST
    )
    public WrappedBoolean registerForEvent(final User user,
                                                @Named("websafeEventKey")
                                                final String websafeEventKey)
            throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        final String userId = getUserId(user);
        TxResult<Boolean> result = ofy().transact(new Work<TxResult<Boolean>>() {
            @Override
            public TxResult<Boolean> run() {
                Key<Event> eventKey = Key.create(websafeEventKey);
                Event event = ofy().load().key(eventKey).now();
                // 404 when there is no Event with the given eventId.
                if (event == null) {
                    return new TxResult<>(new NotFoundException(
                            "No Event found with key: " + websafeEventKey));
                }
                // Registration happens here.
                Profile profile = getProfileFromUser(user, userId);
                if (profile.getEventsKeysToJoin().contains(websafeEventKey)) {
                    return new TxResult<>(new ConflictException("You have already registered for this event"));
                } else if (event.getEntriesAvailable() <= 0) {
                    return new TxResult<>(new ConflictException("There are no places available."));
                } else {
                    profile.addToEventsKeysToJoin(websafeEventKey);
                    event.joinEvent();
                    ofy().save().entities(profile, event).now();
                    return new TxResult<>(true);
                }
            }
        });
        // NotFoundException is actually thrown here.
        return new WrappedBoolean(result.getResult());
    }

    /**
     * Unregister from the specified Event.
     *
     * @param user An user who invokes this method, null when the user is not signed in.
     * @param websafeEventKey The String representation of the Event Key to unregister
     *                             from.
     * @return Boolean true when success, otherwise false.
     * @throws UnauthorizedException when the user is not signed in.
     * @throws NotFoundException when there is no Event with the given eventId.
     */
    @ApiMethod(
            name = "unregisterFromEvent",
            path = "event/{websafeEventKey}/registration",
            httpMethod = HttpMethod.DELETE
    )
    public WrappedBoolean unregisterFromEvent(final User user,
                                                   @Named("websafeEventKey")
                                                   final String websafeEventKey)
            throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        final String userId = getUserId(user);
        TxResult<Boolean> result = ofy().transact(new Work<TxResult<Boolean>>() {
            @Override
            public TxResult<Boolean> run() {
                Key<Event> eventKey = Key.create(websafeEventKey);
                Event event = ofy().load().key(eventKey).now();
                // 404 when there is no Event with the given eventId.
                if (event == null) {
                    return new TxResult<>(new NotFoundException(
                            "No Event found with key: " + websafeEventKey));
                }
                // Un-registering from the Event.
                Profile profile = getProfileFromUser(user, userId);
                if (profile.getEventsKeysToJoin().contains(websafeEventKey)) {
                    profile.unregisterFromEvent(websafeEventKey);
                    event.giveBackEntries();
                    ofy().save().entities(profile, event).now();
                    return new TxResult<>(true);
                } else {
                    return new TxResult<>(false);
                }
            }
        });
        // NotFoundException is actually thrown here.
        return new WrappedBoolean(result.getResult());
    }







    /**
     * Delete the specified Event.
     *
     * @param user An user who invokes this method, null when the user is not signed in.
     * @param websafeEventKey The String representation of the Event Key to unregister
     *                             from.
     * @return Boolean true when success, otherwise false.
     * @throws UnauthorizedException when the user is not signed in.
     * @throws NotFoundException when there is no Event with the given eventId.
     * @throws ForbiddenException when the user is not the owner of the Event.
     */
    @ApiMethod(
            name = "deleteEvent",
            path = "event/{websafeEventKey}/delete",
            httpMethod = HttpMethod.DELETE
    )
    public WrappedBoolean deleteEvent(final User user, @Named("websafeEventKey") final String websafeEventKey)
            throws UnauthorizedException, NotFoundException, ForbiddenException, ConflictException {
        // If not signed in, throw a 401 error.
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        // Update the event with the eventForm sent from the client.
        // Need a transaction because we need to safely preserve the number of allocated participants.
    /*    TxResult<Boolean> result = ofy().transact(new Work<TxResult<Boolean>>() {
            @Override
            public TxResult<Boolean> run() {
                // If there is no Event with the id, throw a 404 error.

                if (event == null) {
                    return new TxResult<>(new NotFoundException("No Event found with the key: " + websafeEventKey));
                }
                // If the user is not the owner, throw a 403 error.
                Profile profile = ofy().load().key(Key.create(Profile.class, userId)).now();
                if (profile == null ||
                        !event.getOrganizerUserId().equals(userId)) {
                    return new TxResult<>(new ForbiddenException("Only the owner can update the event."));
                }
                 ofy().delete().entity(event).now();
                //we suppose everything okey
                return new TxResult<>(true);


                }
        });
        */

        final String userId = getUserId(user);
        Key<Event> eventKey = Key.create(websafeEventKey);
        final Event  event = ofy().load().key(eventKey).now();
        // If there is no Event with the id, throw a 404 error.
        if (event == null) {
            throw new NotFoundException("No Event found with the key: " + websafeEventKey);
        }
        // If the user is not the owner, throw a 403 error.
        Profile profile = ofy().load().key(Key.create(Profile.class, userId)).now();
        if (profile == null || !event.getOrganizerUserId().equals(userId)) {
           throw new ForbiddenException("Only the owner can update the event.");
        }

        //TODO prepare query and post after//
        // Un-registering everyone from the deleted Event.
        Query<Profile> query = ofy().load().type(Profile.class);
        for (Profile TempProfile : query)
        {
            if (TempProfile.getEventsKeysToJoin().contains(websafeEventKey)) {
                TempProfile.unregisterFromEvent(websafeEventKey);
                event.giveBackEntries();
                ofy().save().entities(TempProfile, event);
            }
        }

        ofy().delete().entity(event).now();

        // NotFoundException is actually thrown here.
        return new WrappedBoolean(true);
    }



}
