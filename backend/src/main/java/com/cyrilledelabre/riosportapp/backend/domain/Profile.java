package com.cyrilledelabre.riosportapp.backend.domain;

import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Profile Class
 * User preferences for the application on the server
 * displayName
 * TODO SportsPreferences
 * TODO Km Preferences for the geolocalisation
 *
 */
@Cache
@Entity
public class Profile implements Serializable {
    @Id
    String userId;
    String displayName;
    String mainEmail;


    /**
     * Keys of the events that the user registers to participate.
     */
    private List<String> eventsKeysToJoin = new ArrayList<>(0);



    /**
     * Public constructor for Profile.
     * @param userId The user id, obtained from the email (google account // TODO facebook)
     * @param displayName Name the user wants to display him/her on this system.
     * @param mainEmail User's main e-mail address.
     *
     */
    public Profile (String userId, String displayName, String mainEmail) {
        this.userId = userId;
        this.displayName = displayName;
        this.mainEmail = mainEmail;
    }


    public String getDisplayName() {
        return displayName;
    }
    public String getMainEmail() {
        return mainEmail;
    }
    public String getUserId() {
        return userId;
    }




    /**
     * Getter for eventsIdsToJoin.
     * @return an immutable copy of eventsIdsToJoin.
     */
    public List<String> getEventsKeysToJoin() {
        return ImmutableList.copyOf(eventsKeysToJoin);
    }

    /**
     * default constructor private.
     */
    public Profile() {}



    /**
     * Adds a eventsId to eventsIdsToJoin.
     * TODO add multithreading (?)
     * @param eventsKeys a websafe String representation of the Event Key.
     */
    public void addToEventsKeysToJoin(String eventsKeys) {
        eventsKeysToJoin.add(eventsKeys);
    }

    /**
     * Remove the eventsId from eventsIdsToJoin.
     * @param eventsKeys String representation of the Events Key.
     */
    public void unregisterFromEvent(String eventsKeys) {
        if (eventsKeysToJoin.contains(eventsKeys)) {
            eventsKeysToJoin.remove(eventsKeys);
        } else {
            throw new IllegalArgumentException("Invalid eventsKeys: " + eventsKeys);
        }
    }
    /**
     * Update the Profile with the given displayName.
     * @param displayName
     */
    public void update(String displayName) {
        if (displayName != null) {
            this.displayName = displayName;
        }
    }

}
