package com.guarantime;

import java.net.URL;
import java.util.*;

/**
 * A user registered in the system
 * At the moment, I'm thinking mostly about "edit profile" functionality here... this class is something to return when
 * we try to fetch a user from the service. It is very likely to change as we start using it.
 */
public class User {
    private int id;
    private String accessToken;
    // The main e-mail address, others can be associated as well
    private String email;
    // Do we even need to access this here?
    private String passwordHash;
    // Adding a name is completely optional
    private String firstname;
    private String lastname;
    private String tel;
    private String Username;
    private List<URL> calendars;

    public User(String email, String passwordHash, String firstname,String lastname,String tel,String Username, List<URL> calendars) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstname = firstname;
        this.lastname = lastname;
        this.tel=tel;
        this.Username=Username;
        this.calendars = calendars;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return Username;
    }

    public String getTel(){return tel;}

    public String getPasswordHash() { return passwordHash; }

    public String getFirstname() { return firstname; };
    public String getLastname() { return lastname; };

    public List<URL> getCalendars() { return calendars; }
}
