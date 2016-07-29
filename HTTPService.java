package com.guarantime;

import elemental.json.*;
import elemental.json.impl.JreJsonFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.*;

/**
 * Does the actual communication with the back-end. Data sent should be encrypted in some suitable way, also, we should
 * probably have some mechanism for ensuring that we're actually talking to the back-end and not someone else...
 */
public class HTTPService implements Service {

    //private final static String backendURL = "http://86.50.119.47:3000/api/";

    //private final static String backendURL = "http://192.168.0.110:3000/api/";
    private final static String backendURL = "http://localhost:3000/api/";
    private final JreJsonFactory factory = new JreJsonFactory();

    // TODO: Get rid of duplication
    private JsonObject jsonObjectRequest(String url, JsonObject data) throws ServiceException {
        Utils.printIfDebug("Request data: " + data.toString());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response;

        try {
            HttpPost request = new HttpPost(url);
            StringEntity json = new StringEntity(
                    data.toString(),
                    ContentType.APPLICATION_JSON
            );
            request.setEntity(json);
            response = client.execute(request);

            String responseString = EntityUtils.toString(response.getEntity());

            // Create JSON object from response
            JsonObject responseData = factory.parse(responseString);

            Utils.printIfDebug("Response data: " + responseData.toString());

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Check status code of JSON data as well
                int jsonStatusCode = (int) responseData.getNumber("statusCode");

                if (jsonStatusCode == 200) {
                    return responseData.getObject("data");
                }
                else if (jsonStatusCode == 201) {
                    return responseData.getObject("data");
                }
                else if(jsonStatusCode == 402)
                {
                    return responseData.getObject("data");
                }
                else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) { // TODO: What kind of exceptions are possible?
            throw new ServiceException("Error when contacting back-end");
        }
    }

    private JsonArray jsonArrayRequest(String url, JsonObject data) throws ServiceException {
        Utils.printIfDebug("Request data: " + data.toString());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpResponse response;

        try {
            HttpPost request = new HttpPost(url);
            StringEntity json = new StringEntity(
                    data.toString(),
                    ContentType.APPLICATION_JSON
            );
            request.setEntity(json);
            response = client.execute(request);

            String responseString = EntityUtils.toString(response.getEntity());

            // Create JSON object from response
            JsonObject responseData = factory.parse(responseString);

            Utils.printIfDebug("Response data: " + responseData.toString());

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Check status code of JSON data as well
                int jsonStatusCode = (int) responseData.getNumber("statusCode");

                if (jsonStatusCode == 200) {
                    return responseData.getArray("data");
                } else {
                    return null;
                }
            }
            else {
                return null;
            }
        } catch (Exception e) { // TODO: What kind of exceptions are possible?
            throw new ServiceException("Error when contacting back-end");
        }
    }

    public User login(String email, String password) throws ServiceException {
        JsonObject data = factory.createObject();

        data.put("email", email);
        data.put("password", password);

        JsonObject response1 = jsonObjectRequest(backendURL + "registration/login", data);

        if (response1 == null) {
            return null;
        } else {


            User user = new User(
                    response1.getString("primary_email"),
                    response1.getString("password"),
                    response1.getString("first_name"),
                    response1.getString("last_name"),
                    response1.getString("tel"),
                    response1.getString("username"),
                    null
            );
            user.setId((int) response1.getNumber("uid"));
            user.setAccessToken(response1.getString("access_token"));
            return user;
        }
    }

    public boolean register(String email, String password) throws ServiceException {
        JsonObject data = factory.createObject();

        data.put("firstName", " ");
        data.put("lastName", " ");
        data.put("tel", " ");
        data.put("email", email);
        data.put("isActive", 0);
        data.put("username", "default");
        data.put("password", password);

        JsonObject response = jsonObjectRequest(backendURL + "registration/register", data);
        return response != null;
    }

    public User getUser(String email) throws ServiceException {
        // TODO: Implement

        return null;
    }

    // TODO: This function duplicates functionality from TestService for the purpose of testing the backend
    public Collection<User> getUsers() throws ServiceException {
        ArrayList list = new ArrayList();

        // Common list of calendar URLs for all test users
        ArrayList calendars = new ArrayList();
        calendars.add("http://www.example.com/calendar1");
        calendars.add("http://www.example.com/calendar2");
        calendars.add("http://www.example.com/calendar3");

        list.add(new User("one@example.com", "1234", "Named User","last","","", null));
        list.add(new User("two@example.com", "2345", "","last","","", null));
        list.add(new User("three@example.com", "3456", null,"last","","", null));

        return list;
    }

    public boolean createMeeting(Meeting meeting) throws ServiceException {
        User user = Utils.getCurrentUser();

        // Meeting creation is split into two requests: one with meeting data, one with invited users

        // First request
        JsonObject data1 = factory.createObject();

        JsonArray dates = factory.createArray();
        List<String> dateList = meeting.getSuggestedDates();
        for (int i = 0; i < dateList.size(); i++) {
            // If date suggestion was empty, date is going to be null
            if ((dateList.get(i)) != null) {
                dates.set(i, dateList.get(i).toString());
            }
        }

        data1.put("uid", user.getId());
        data1.put("title", meeting.getSubject());
        data1.put("meetingDescription", meeting.getDescription());
        data1.put("host", meeting.getHost());
        data1.put("suggestedDates", dates);
        data1.put("duration",meeting.getmeetinghour());

        JsonObject response1 = jsonObjectRequest(backendURL + "meeting/createMeeting", data1);

        if(response1==null) {
            return false;
        }
        // Second request
        JsonObject data2 = factory.createObject();

        JsonArray users = factory.createArray();
        List<String> usersList = meeting.getInvitedUsers();
        for (int i = 0; i < usersList.size(); i++) {
            users.set(i, usersList.get(i));
        }

        data2.put("uid", user.getId());
        data2.put("mid", response1.getNumber("mid"));
        data2.put("meeting_access_token", response1.getString("meeting_access_token"));
        data2.put("invitations", users);
        data2.put("name", user.getFirstname()+" "+user.getLastname());

        JsonObject response2 = jsonObjectRequest(backendURL + "meeting/createMeetingInvitation", data2);
        if(response2.getString("message").equals("Successfully done")) {
            return true;
        }

        return false;
    }
    public String ChangePassword(String oldpassword,String newpassword) throws ServiceException
    {
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();

        data.put("uid", user.getId());
        data.put("oldPassword", oldpassword);
        data.put("newPassword", newpassword);
//have to change the password api
        JsonObject response = jsonObjectRequest(backendURL + "registration/changePassword", data);
        //String chk= response.getNumber("statusCode").toString();7
        System.out.println(response.getString("message"));
        if(response!=null) {
            return response.getString("message");
        }

        return null;
    }
    public boolean updateprofile(updateprofile update) throws ServiceException{
        User user = Utils.getCurrentUser();

        JsonObject data = factory.createObject();
        data.put("uid", user.getId());
        data.put("firstName", update.getFirstName());
        data.put("lastName", update.getLastName());
        data.put("tel", update.getTelephoneNo());
        data.put("email", update.getEmailAddress());
        data.put("username", update.getUsername());

        JsonObject response = jsonObjectRequest(backendURL + "registration/editProfile", data);
        if(response.getString("message").equals("Successfully Updated Profile")) {
                return true;
            }

        return false;
    }
    public Collection<Meeting>  viewcalender(String email,int uid) throws ServiceException{
        Collection<Meeting> meetings = new ArrayList();
        JsonObject data = factory.createObject();
        data.put("uid", uid);
        data.put("Primary_Email", email);
        JsonArray response = jsonArrayRequest(backendURL + "meeting/calendarMeetings", data);
        if(response==null){
            return null;
        }else{
            for (int i = 0; i < response.length(); i++) {
                JsonObject jsonMeeting = response.getObject(i);
                List<String> suggestedDatesList = new ArrayList<>();
                suggestedDatesList.add(jsonMeeting.getString("date"));
                Meeting meeting1 = new Meeting(jsonMeeting.getString("title"), jsonMeeting.getString("description"), "", null, suggestedDatesList);
                meeting1.setMeeting_hour((int)jsonMeeting.getNumber("duration"));
                meetings.add(meeting1);
        }}
        return meetings;
    }

    public Meeting getMeeting(int id) {
        // TODO: Implement

        return null;
    }
   public Meeting showsinglemeeting(Meeting meeting)throws ServiceException
    {
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        data.put("uid", user.getId());
        data.put("mid",meeting.getId());
        data.put("meetingAccessToken",meeting.getAccessToken());
        JsonObject response = jsonObjectRequest(backendURL + "meeting/showSingleMeeting", data);

        List<String> meeting_vote_date=new ArrayList<>();
        List<String> meeting_confirm_vote=new ArrayList<>();

            JsonArray voting_meeting=response.getArray("meetingVotesList");
        for (int i = 0; i < voting_meeting.length(); i++) {
            JsonObject jsonMeeting = voting_meeting.getObject(i);
            jsonMeeting.getString("dates");
            meeting_vote_date.add(jsonMeeting.getString("dates"));
            meeting_confirm_vote.add(jsonMeeting.getString("confirmed"));

        }

        JsonArray dates = response.getArray("datesList");
        List<String> suggestedDatesList=new ArrayList<>();
         for (int i = 0; i < dates.length(); i++) {
           JsonObject jsonMeeting = dates.getObject(i);

             suggestedDatesList.add(jsonMeeting.getString("date"));

        }
        List<Integer> date_id=new ArrayList<>();
        for (int i = 0; i < dates.length(); i++) {
            JsonObject jsonMeeting = dates.getObject(i);

            date_id.add((int) jsonMeeting.getNumber("id"));

        }
        JsonArray guestlist = response.getArray("guestsList");
        List<String> guestsLists=new ArrayList<>();
        for (int i = 0; i < guestlist.length(); i++) {
            JsonObject jsonguest = guestlist.getObject(i);
            guestsLists.add(jsonguest.getString("email"));
        }
        List<String> personal_vote=new ArrayList<>();
        JsonArray personal_date = response.getArray("personVoteDetails");
        for (int i = 0; i < personal_date.length(); i++) {
            JsonObject jsonvote = personal_date.getObject(i);
            personal_vote.add(jsonvote.getString("dates"));
        }


        JsonObject meetingdetails = response.getObject("meetingDetails");
            Meeting meetings=new Meeting(meetingdetails.getString("title"), meetingdetails.getString("description"),meetingdetails.getString("title") , guestsLists,suggestedDatesList);
            meetings.setId((int) meetingdetails.getNumber("mid"));
            meetings.setDate_id(date_id);
            meetings.setAccessToken(meetingdetails.getString("meeting_access_token"));
            meetings.setMeeting_voting_dates(meeting_vote_date);
            meetings.setMeeting_confirm_vote(meeting_confirm_vote);
            meetings.setPersonal_vote(personal_vote);


        return meetings;
    }

    public Collection<Meeting> getMeetingsCreatedByUser(int uid) throws ServiceException {
        User user = Utils.getCurrentUser();


        JsonObject data = factory.createObject();
        data.put("uid", user.getId());


        Collection<Meeting> meetings = new ArrayList();

        JsonArray response = jsonArrayRequest(backendURL + "meeting/showAllMeetings", data);


        for (int i = 0; i < response.length(); i++) {
            JsonObject jsonMeeting = response.getObject(i);
            Meeting meeting1=new Meeting(jsonMeeting.getString("title"), jsonMeeting.getString("description"), "", null, jsonMeeting.get("SuggestedDates"));
            meeting1.setId((int) jsonMeeting.getNumber("mid"));
            meeting1.setAccessToken(jsonMeeting.getString("meeting_access_token"));
            meetings.add(meeting1);

        }

        return meetings;
    }

    // TODO: This function duplicates functionality from TestService for the purpose of testing the backend
    public Collection<Meeting> getInvitations(String email) throws ServiceException {

        JsonObject data = factory.createObject();
        data.put("Primary_Email", email);


        Collection<Meeting> meetings = new ArrayList();

        JsonArray response = jsonArrayRequest(backendURL + "meeting/showAllInvitations", data);
        if (response == null) {
            return meetings;
        }

        for (int i = 0; i < response.length(); i++) {

            JsonObject jsonMeeting = response.getObject(i);
            Meeting meeting1=new Meeting(jsonMeeting.getString("title"), jsonMeeting.getString("description"), "", null, jsonMeeting.get("SuggestedDates"));
            meeting1.setId((int) jsonMeeting.getNumber("mid"));
            meeting1.setAccessToken(jsonMeeting.getString("meeting_access_token"));
            meetings.add(meeting1);

        }


        return meetings;
    }
    public boolean cancelmeeting(int mid)throws ServiceException {
        JsonObject data = factory.createObject();
        data.put("mid", mid);
        JsonObject response = jsonObjectRequest(backendURL + "meeting/cancelMeeting", data);
        if(response.getString("message").equals("Successfully done")) {
            return true;
        }

        return false;
    }
    public boolean updatemeeting(Meeting meeting) throws ServiceException
    {
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        JsonArray addGuest = factory.createArray();
        List<String> addGuests = meeting.getInvitedUsers();
        for (int i = 0; i < addGuests.size(); i++) {
            // If date suggestion was empty, date is going to be null
            if ((addGuests.get(i)) != null) {
                addGuest.set(i, addGuests.get(i).toString());
            }
        }
        JsonArray removeGuest = factory.createArray();
        List<String> removeGuests = meeting.getRemoveUsers();
        for (int i = 0; i < removeGuests.size(); i++) {
            // If date suggestion was empty, date is going to be null
            if ((removeGuests.get(i)) != null) {
                removeGuest.set(i, removeGuests.get(i).toString());
            }
        }
        System.out.println(meeting.getSubject()+meeting.getDescription());
        data.put("title", meeting.getSubject());
       data.put("description",meeting.getDescription());
       data.put("uKey",1);
        data.put("mid",meeting.getId());
        data.put("name",user.getFirstname()+" "+user.getLastname());
       data.put("meetingAccessToken",meeting.getAccessToken());
       data.put("addGuests",addGuest);
       data.put("removeGuests",removeGuest);
        JsonObject response = jsonObjectRequest(backendURL + "meeting/updateMeeting", data);
        if(response.getString("message").equals("Successfully done")) {
            return true;
        }
        return false;

    }
    public String votemeeting(int date_id,String accesstoken ) throws ServiceException
    {
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        data.put("suggestionDateId",date_id );
        data.put("meetingAccessToken",accesstoken);
        data.put("guestId", user.getId());
        JsonObject response = jsonObjectRequest(backendURL + "meeting/changeInvitationVote", data);
        if(response.getString("message").equals("Successfully done")) {
            return response.getString("message");
        }
        else if(response.getString("message").equals("User Already Casted Vote")) {
            return response.getString("message");
        }
        return null;
    }
    public boolean confirmmeeting(String confirmedDate ,int mid) throws ServiceException{

        JsonObject data = factory.createObject();
        data.put("mid", mid);
        data.put("confirmedDate", confirmedDate);
        JsonObject response = jsonObjectRequest(backendURL + "meeting/meetingConfirmation", data);
        if(response.getString("message").equals("Successfully done")) {
            return true;
        }
        return false;
    }
    public boolean forgetpassword(String email) throws ServiceException{

        JsonObject data = factory.createObject();
        data.put("Primary_Email", email);
        JsonObject response = jsonObjectRequest(backendURL + "registration/forgetPassword", data);
        if(response.getString("message").equals("Successfully done")) {
            return true;
        }
        return false;
    }
    //TODO: still need to implement
    public boolean calendereventcreation(Calendar_Event calendar_event) throws ServiceException
    {
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        data.put("uid", user.getId());
        data.put("Primary_Email", user.getEmail());
        data.put("Subject", calendar_event.getSubject());
        data.put("Description", calendar_event.getDescription());
        data.put("Meeting_hour", calendar_event.getMeeting_hour());
        data.put("Startdate", calendar_event.getStartdate());
        JsonObject response = jsonObjectRequest(backendURL + "event/createEvent", data);
        if(response.getString("message").equals("Successfully done")) {
            return true;
        }
        return false;
    }
    public List<String> showcontact() throws ServiceException{
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        data.put("uid", user.getId());
        List<String> contacts = new ArrayList<String>();
        JsonArray response = jsonArrayRequest(backendURL + "contacts/showAllContacts", data);
        if(response!=null) {
            for (int i = 0; i < response.length(); i++) {

                JsonObject jsonContacts = response.getObject(i);
                contacts.add(jsonContacts.getString("guest_email"));

            }
            return contacts;
        }
        return contacts;
    }
    public boolean addcontact(String email) throws ServiceException{
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        data.put("uid", user.getId());
        data.put("guestName", "");
        data.put("guestEmail", email);
        JsonObject response = jsonObjectRequest(backendURL + "contacts/addContacts", data);
        if(response.getString("message").equals("Successfully done")) {
            return true;
        }
        return false;
    }
    public boolean deletecontact(String email) throws ServiceException{
        User user = Utils.getCurrentUser();
        JsonArray removecotacts = factory.createArray();
        removecotacts.set(0, email);
        JsonObject data = factory.createObject();
        data.put("uid", user.getId());
        data.put("guestEmails", removecotacts);
        JsonObject response = jsonObjectRequest(backendURL + "contacts/removeContacts", data);
        if(response.getString("message").equals("Successfully done")) {
            return true;
        }
        return false;
    }
    public Collection<Free_slot> suggesteddates(List<String> email) throws ServiceException
    {
        User user = Utils.getCurrentUser();
        List<String> emails = email;
        JsonObject data = factory.createObject();
        JsonArray users_email = factory.createArray();
        for (int i = 0; i < emails.size(); i++) {
            users_email.set(i, emails.get(i));
        }
        data.put("uid", user.getId());
        data.put("guestEmails", users_email);
        Collection<Free_slot> free_slots = new ArrayList();
        JsonArray response = jsonArrayRequest(backendURL + "dateSuggestion/dateSuggestion", data);
        if(response!=null) {
            for (int i = 0; i < response.length(); i++) {

                JsonObject jsonfreeslot = response.getObject(i);
                Free_slot free_slot=new Free_slot(jsonfreeslot.getString("start_time"),jsonfreeslot.getString("end_time"));
                free_slots.add(free_slot);
            }
        }
        return free_slots;
    }
    public boolean verifyemail(String email) throws ServiceException{
        JsonObject data = factory.createObject();
        data.put("email", email);
        JsonObject response = jsonObjectRequest(backendURL + "registration/loginVerifyEmail", data);
        if(response==null) {
            return false;
        }
        return true;
    }
    public boolean cancelvote(String m_access,int date_id) throws ServiceException{
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        data.put("guestId",user.getId() );
        data.put("meetingAccessToken", m_access);
        data.put("suggestionDateId", date_id);
        JsonObject response = jsonObjectRequest(backendURL + "meeting/cancelInvitationVote", data);
        if(response==null) {
            return false;
        }
        return true;
    }
    public String sycGoogleCalendar() throws ServiceException{
        User user = Utils.getCurrentUser();
        JsonObject data = factory.createObject();
        data.put("uid",user.getId() );
        JsonObject response = jsonObjectRequest(backendURL + "calendar/googleCalendar", data);
        if(response==null) {
            return null;
        }
        else
        {
            return response.getString("url");
        }
    }

}
