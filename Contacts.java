package com.guarantime;

import java.io.Serializable;

/**
 * Created by bilal on 2/27/2016.
 */

public class Contacts implements Serializable {
    private String  email;

    public Contacts( String email) {

        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
