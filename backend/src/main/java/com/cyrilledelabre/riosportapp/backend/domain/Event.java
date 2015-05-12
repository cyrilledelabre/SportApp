package com.cyrilledelabre.riosportapp.backend.domain;

import com.cyrilledelabre.riosportapp.backend.form.EventForm;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.cyrilledelabre.riosportapp.backend.service.OfyService.ofy;

/**
 * Event class stores event information.
 */
@Entity
@Cache
public class Event implements Serializable {


    /**
     * The id for the datastore key.
     */
    @Id
    private long id;
    /**
     * The name of the event.
     */
    @Index
    private String name;
    /**
     * The description of the event.
     */
    private String description;
    /**
     * The localization of the event.
     */
    @Index
    private GeoPt coordinates;
    /**
     * Holds Profile key as the parent.
     */
    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<Profile> profileKey;
    /**
     * The userId of the organizer @name.
     */
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private String organizerUserId;
    /**
     * Sports related to this events.
     */
    @Index
    private List<String> sports;
    /**
     * The starting date of this event.
     */
    private Date startDate;
    /**
     * The ending date of this event.
     */
    private Date endDate;
    /**
     * The maximum participants in this event.
     */
    @Index
    private int maxParticipants;
    /**
     * Number of entry currently available.
     */
    @Index
    private int entriesAvailable;

    /**
     * default constructor.
     */
    public Event() {}

    public Event(final long id, final String organizerUserId,
                 final EventForm eventForm) {

        Preconditions.checkNotNull(eventForm.getName(), "The name is required");
        Preconditions.checkNotNull(eventForm.getSports(), "a least one sport is required");
        //TODO add preconditions
        this.id = id;
        this.profileKey = Key.create(Profile.class, organizerUserId);
        this.organizerUserId = organizerUserId;
        updateWithEventForm(eventForm);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public GeoPt getCoordinates() {return coordinates;}

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Profile> getProfileKey() {
        return profileKey;
    }

    // Get a String version of the key
    public String getWebsafeKey() {
        return Key.create(profileKey, Event.class, id).getString();
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public String getOrganizerUserId() {
        return organizerUserId;
    }

    /**
     * Returns organizer's display name.
     *
     * @return organizer's display name. If there is no Profile, return his/her userId.
     */
    public String getOrganizerDisplayName() {
        Profile organizer = ofy().load().key(getProfileKey()).now();
        if (organizer == null) {
            return organizerUserId;
        } else {
            return organizer.getDisplayName();
        }
    }

    /**
     * Returns a defensive copy of sports if not null.
     * @return a defensive copy of sports if not null.
     */
    public List<String> getSports() {
        return sports == null ? null : ImmutableList.copyOf(sports);
    }

    /**
     * Returns a defensive copy of startDate if not null.
     * @return a defensive copy of startDate if not null.
     */
    public Date getStartDate() {
        return startDate == null ? null : new Date(startDate.getTime());
    }

    /**
     * Returns a defensive copy of endDate if not null.
     * @return a defensive copy of endDate if not null.
     */
    public Date getEndDate() {
        return endDate == null ? null : new Date(endDate.getTime());
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getEntriesAvailable() {
        return entriesAvailable;
    }

    /**
     * Updates the Event with EventForm.
     * This method is used upon object creation as well as updating existing Events
     * @param eventForm contains form data sent from the client.
     */
    public void updateWithEventForm(EventForm eventForm) {
        this.name = eventForm.getName();
        this.description = eventForm.getDescription();
        this.sports = eventForm.getSports();
        this.coordinates = eventForm.getCoordinates();

        Date startDate = eventForm.getStartDate();
        this.startDate = startDate == null ? null : new Date(startDate.getTime());
        Date endDate = eventForm.getEndDate();
        this.endDate = endDate == null ? null : new Date(endDate.getTime());

        int entryAllocated = maxParticipants - entriesAvailable;
        if (eventForm.getMaxParticipants() < entryAllocated) {
            throw new IllegalArgumentException(entryAllocated + " participants have already joined, "
                    + "you cannot change the max participants ");
        }
        //change values
        this.maxParticipants = eventForm.getMaxParticipants();
        this.entriesAvailable = this.maxParticipants - entryAllocated;
    }

    public void joinEvent(){
        if (entriesAvailable <= 0) {
            throw new IllegalArgumentException("There is no more place available.");
        }
        entriesAvailable--;
    }

    public void giveBackEntries() {
        entriesAvailable++;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Id: " + id + "\n")
                .append("Name: ").append(name).append("\n");

        if (sports != null && sports.size() > 0) {
            stringBuilder.append("Sports:\n");
            for (String topic : sports) {
                stringBuilder.append("\t").append(topic).append("\n");
            }
        }
        if (startDate != null) {
            stringBuilder.append("StartDate: ").append(startDate.toString()).append("\n");
        }
        if (endDate != null) {
            stringBuilder.append("EndDate: ").append(endDate.toString()).append("\n");
        }
        stringBuilder.append("Max Participants: ").append(maxParticipants).append("\n");
        return stringBuilder.toString();
    }
}
