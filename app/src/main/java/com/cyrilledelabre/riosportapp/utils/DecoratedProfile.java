package com.cyrilledelabre.riosportapp.utils;

import com.appspot.riosportapp.event.model.Profile;

/**
 * Created by cyrilledelabre on 10/05/15.
 */
public class DecoratedProfile {
    private Profile profile;
    public  DecoratedProfile(Profile profile)
    {
        this.profile = profile;
    }
    public Profile getProfile(){return profile;}
}
