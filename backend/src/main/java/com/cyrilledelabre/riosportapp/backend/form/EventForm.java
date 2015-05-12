package com.cyrilledelabre.riosportapp.backend.form;

import com.google.appengine.api.datastore.GeoPt;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A simple Java object (POJO) representing a Event form sent from the client.
 */
public class EventForm implements Serializable {
    /**
     * The name of the event.
     */
    private String name;

    /**
     * The description of the event.
     */
    private String description;

    /**
     * Topics that are discussed in this event.
     */
    private List<String> sports;

    /**
     * The start date of the event.
     */
    private Date startDate;

    /**
     * The end date of the event.
     */
    private Date endDate;

    /**
     * The capacity of the event.
     */
    private int maxParticipants;

    private int entriesAvailable;

    private GeoPt coordinates;

    private EventForm() {}

    /**
     * Public constructor is solely for Unit Test.
     * @param name
     * @param description
     * @param sports
     * @param startDate
     * @param endDate
     * @param maxParticipants
     */
    public EventForm(String name, String description, List<String> sports,
                     Date startDate, Date endDate, int maxParticipants) {
        this.name = name;
        this.description = description;
        this.sports = sports == null ? null : ImmutableList.copyOf(sports);
        this.startDate = startDate == null ? null : new Date(startDate.getTime());
        this.endDate = endDate == null ? null : new Date(endDate.getTime());
        this.maxParticipants = maxParticipants;
        this.entriesAvailable =maxParticipants; //init
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSports() {
        return sports;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getEntriesAvailable() {
        return entriesAvailable;
    }


    public GeoPt getCoordinates() { return coordinates; }
}
