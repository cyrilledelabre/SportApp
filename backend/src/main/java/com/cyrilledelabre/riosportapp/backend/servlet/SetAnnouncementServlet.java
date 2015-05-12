package com.cyrilledelabre.riosportapp.backend.servlet;

import com.cyrilledelabre.riosportapp.backend.Constants;
import com.cyrilledelabre.riosportapp.backend.domain.Event;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.cyrilledelabre.riosportapp.backend.service.OfyService.*;


/**
 * A servlet for putting announcements in memcache.
 * The announcement announces envents that are nearly full
 * (defined as having 1 - 5 places left)
 */
@SuppressWarnings("serial")
public class SetAnnouncementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Query for conferences with less than 5 seats left
        Iterable<Event> iterable = ofy().load().type(Event.class)
                .filter("getEntriesAvailable <", 5)
                .filter("getEntriesAvailable >", 0);

        // Get the names of the nearly sold out conferences
        List<String> eventsNames = new ArrayList<>(0);
        for (Event event : iterable) {
            eventsNames.add(event.getName());
        }
        if (eventsNames.size() > 0) {

            // Build a String that announces the nearly sold-out conferences
            StringBuilder announcementStringBuilder = new StringBuilder(
                    "Last chance to participate! The following events are nearly full: ");
            Joiner joiner = Joiner.on(", ").skipNulls();
            announcementStringBuilder.append(joiner.join(eventsNames));

            // Get the Memcache Service
            MemcacheService memcacheService = MemcacheServiceFactory.getMemcacheService();

            // Put the announcement String in memcache,
            // keyed by Constants.MEMCACHE_ANNOUNCEMENTS_KEY
            String announcementKey = Constants.MEMCACHE_ANNOUNCEMENTS_KEY;
            String announcementText = announcementStringBuilder.toString();

            memcacheService.put(announcementKey, announcementText);
            /**
            memcacheService.put(Constants.MEMCACHE_ANNOUNCEMENTS_KEY,
                    announcementStringBuilder.toString());
            **/
        }

        // Set the response status to 204 which means
        // the request was successful but there's no data to send back
        // Browser stays on the same page if the get came from the browser
        response.setStatus(204);
    }
}
