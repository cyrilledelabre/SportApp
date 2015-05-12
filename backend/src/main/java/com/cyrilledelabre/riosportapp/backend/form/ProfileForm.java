package com.cyrilledelabre.riosportapp.backend.form;

import java.io.Serializable;

/**
 * Pojo representing a profile form on the client side.
 */
public class ProfileForm implements Serializable {
    /**
     * Any string user wants us to display him/her on this system.
     */
    private String displayName;
    private ProfileForm () {}

    /**
     * Constructor for ProfileForm, solely for unit test.
     * @param displayName A String for displaying the user on this system.
     */
    public ProfileForm(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
