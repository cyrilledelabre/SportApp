package com.cyrilledelabre.riosportapp.backend;

import com.google.api.server.spi.Constant;

/**
 * Contains the client IDs and scopes for allowed clients consuming the conference API.
 */
public class Constants {
    public static final String WEB_CLIENT_ID = "308342923791-ttptt6pn7s8gihhu20fhmd92jcr7fq7p.apps.googleusercontent.com";
    public static final String ANDROID_CLIENT_ID = "308342923791-qmedp47oobtrvu6544ahu4f3cgqb3a01.apps.googleusercontent.com";
   // public static final String IOS_CLIENT_ID = "replace this with your iOS client ID";
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
    public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
    public static final String API_EXPLORER_CLIENT_ID = Constant.API_EXPLORER_CLIENT_ID;

    public static final String MEMCACHE_ANNOUNCEMENTS_KEY = "RECENT_ANNOUNCEMENTS";
}