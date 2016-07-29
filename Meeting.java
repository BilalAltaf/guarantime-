package com.guarantime;

import java.util.Date;
import java.util.List;

public class Meeting {
    private int id;
    private String accessToken;
    private String subject;
    private String description;
    private String hostEmail;
    private int meeting_hour;
    private List<String> Personal_vote;
    private List<String> invitedUsers;
    private List<String> removeUsers;
    // At least for now, as we only send dates away using JSON, we can keep them as strings
    private List<String> suggestedDates;
    private List<String> meeting_voting_dates;
    private List<String> meeting_confirm_vote;
    private List<Integer> date_id;

    public Meeting(String subject, String description, String hostEmail, List<String> invitedUsers, List<String> suggestedDates) {
        this.subject = subject;
        this.description = description;
        this.hostEmail = hostEmail;
        this.invitedUsers = invitedUsers;
        this.suggestedDates = suggestedDates;
    }

    public int getId() { return id; }
    public void setPersonal_vote(List<String> Personal_vote) { this.Personal_vote = Personal_vote; }
    public List<String> getPersonal_vote() { return Personal_vote; }
    public void setId(int id) { this.id = id; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setRemoveuser(List<String> removeUsers) { this.removeUsers = removeUsers; }
    public void setMeeting_voting_dates(List<String> meeting_voting_dates) { this.meeting_voting_dates = meeting_voting_dates; }
    public void setMeeting_confirm_vote(List<String> meeting_confirm_vote) { this.meeting_confirm_vote = meeting_confirm_vote; }
    public void setMeeting_hour(int meeting_hour) { this.meeting_hour = meeting_hour; }
    public void setDate_id(List<Integer> date_id) { this.date_id = date_id; }
    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getHost() {
        return hostEmail;
    }

    public List<String> getInvitedUsers() {
        return invitedUsers;
    }
    public List<String> getMeeting_voting_dates() {
        return meeting_voting_dates;
    }
    public List<String> getMeeting_confirm_vote() {
        return meeting_confirm_vote;
    }

    public List<String> getSuggestedDates() { return suggestedDates; }
    public List<String> getRemoveUsers() { return removeUsers; }
    public List<Integer> getDate_id() { return date_id; }
    public int getmeetinghour() { return meeting_hour; }

}
