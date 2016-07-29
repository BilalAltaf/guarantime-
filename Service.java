package com.guarantime;

import java.util.Collection;
import java.util.List;

public interface Service {
    /**
     * Try to log in using provided e-mail and password.
     * @return A User instance if login successful, null if incorrect e-mail/password
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public User login(String email, String password) throws ServiceException;

    /**
     * Register a new user with the provided data
     * @return true if user was successfully registered, false if there already is a user with the provided e-mail address
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public boolean register(String email, String password) throws ServiceException;

    /**
     * Fetch data for a specific user (identified by e-mail adress) from the back-end
     * @return an instance of the User class, or null if the e-mail address isn't registered
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public User getUser(String email) throws ServiceException;

    /**
     * Fetch all currently registered users in the system and return them as a collection
     * @return a Collection of User instances
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public Collection<User> getUsers() throws ServiceException;

    /**
     * Save a new meeting in the back-end
     * @return true if the meeting was successfully created, false otherwise
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public boolean createMeeting(Meeting meeting) throws ServiceException;
    public boolean updatemeeting(Meeting meeting) throws ServiceException;
    public String votemeeting(int date_id,String accesstoken ) throws ServiceException;
    public String sycGoogleCalendar() throws ServiceException;
    public boolean confirmmeeting(String confirmedDate ,int mid) throws ServiceException;
    public boolean verifyemail(String email) throws ServiceException;
    public boolean cancelvote(String m_access,int date_id) throws ServiceException;
    public Collection<Meeting> viewcalender(String email,int uid) throws ServiceException;

    /**
     * Get a meeting from the back-end by its id
     * @return a Meeting instance
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public Meeting getMeeting(int id) throws ServiceException;

    /**
     * Get meetings created by a specific user
     * @return a Collection of Meeting instances
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public Collection<Meeting> getMeetingsCreatedByUser(int uid) throws ServiceException;
    public Meeting showsinglemeeting(Meeting meeting)throws ServiceException;
    public boolean cancelmeeting(int mid)throws ServiceException;
    public boolean forgetpassword(String email) throws ServiceException;
    public boolean calendereventcreation(Calendar_Event calendar_event) throws ServiceException;
    public String ChangePassword(String oldpassword,String newpassword) throws ServiceException;
    public Collection<Free_slot> suggesteddates(List<String> email) throws ServiceException;
    public boolean addcontact(String email) throws ServiceException;
    public boolean deletecontact(String email) throws ServiceException;
    public boolean updateprofile(updateprofile update) throws ServiceException;
    public List<String> showcontact() throws ServiceException;


    /**
     * Get invitations for a specific user
     * @return a Collection of Meeting instances
     * @throws ServiceException if back-end cannot be reached, request times out etc.
     */
    public Collection<Meeting> getInvitations(String email) throws ServiceException;
}
