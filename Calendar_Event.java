package com.guarantime;

/**
 * Created by bilal on 2/18/2016.
 */
public class Calendar_Event {
    private String subject;
    private String description;
    private int meeting_hour;
    private String Startdate;

    public Calendar_Event(String subject, String description, int meeting_hour, String startdate){
        this.subject=subject;
        this.description=description;
        this.meeting_hour=meeting_hour;
        this.Startdate=startdate;
    }
    public String getSubject(){return subject;}
    public String getDescription(){return description;}
    public int getMeeting_hour(){return meeting_hour;}
    public String getStartdate(){return Startdate;}
}
