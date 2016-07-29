package com.guarantime;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Calendar;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.tools.ant.taskdefs.email.EmailAddress;

import java.util.*;

import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;

import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.event.BasicEvent;


public class startView extends GridLayout implements View {
    private Service service;
    private User user;

    public startView(Service service) {
        this.service = service;
        Calendar calendar = new Calendar("Calendar");
        calendar.setWidth("1300px");  // Undefined by default
        calendar.setHeight("630px"); // Undefined by default
        // Use US English for date/time representation
        calendar.setLocale(new Locale("en", "US"));
        GregorianCalendar calStart = new GregorianCalendar();
        calendar.setStartDate(calStart.getTime());

// Set end date to last day of this month
        GregorianCalendar calEnd = new GregorianCalendar();
        calEnd.set(java.util.Calendar.DATE, 1);
        calEnd.roll(java.util.Calendar.DATE, -1);
        calendar.setEndDate(calEnd.getTime());

        User user= Utils.getCurrentUser();
        Collection<Meeting> meetings;
        try {
            meetings = service.viewcalender(user.getEmail(), user.getId());
        } catch (ServiceException e) {
            Notification.show("Failed to get meetings created by user from the database", Notification.Type.ERROR_MESSAGE);
            return;
        }

        if(meetings!=null) {
            int j =1;
            for (Meeting m : meetings) {
                List<String> dates = m.getSuggestedDates();
                for (int i = 0; i < dates.size(); i++) {
                    // If date suggestion was empty, date is going to be null
                    int p = i + 1;

                    if ((dates.get(i)) != null) {
                        String[] datetimeParts = dates.get(i).toString().split(" ");
                        String date = datetimeParts[0];
                        String[] datesplit = date.toString().split("-");
                        String year_string = datesplit[0];
                        String month_string = datesplit[1];
                        String day_string = datesplit[2];
                        int day = Integer.parseInt(day_string);
                        int year = Integer.parseInt(year_string);
                        int month = Integer.parseInt(month_string);
                        month = month - 1;
                        String time = datetimeParts[1];
                        String[] timesplit = time.toString().split(":");
                        String hour_string = timesplit[0];
                        String minute_string = timesplit[1];
                        String second_string = timesplit[2];
                        int hour = Integer.parseInt(hour_string);
                        int minute = Integer.parseInt(minute_string);
                        int second = Integer.parseInt(second_string);
                        int duration = m.getmeetinghour();
                        int end_minutes;
                        int meetingduration;
                        if(duration<60)
                        {
                            end_minutes=duration;
                            meetingduration=hour;
                        }
                        else {
                              end_minutes = duration%60;
                              meetingduration = hour+duration/ 60;
                        }
                        GregorianCalendar eventStart =
                                new GregorianCalendar(year, month, day, hour, minute, second);
                        GregorianCalendar eventEnd =
                                new GregorianCalendar(year, month, day, meetingduration, end_minutes, second);
                        BasicEvent bevent = new BasicEvent(m.getSubject(),
                                m.getDescription(),
                                eventStart.getTime(), eventEnd.getTime());
                        if(j==6)
                        {
                            j=1;
                        }
                        bevent.setStyleName("color"+j);
                        j=j+1;
                        calendar.addEvent(bevent);
                        //calendar.addClickListener(clickEvent -> {});
                        // Show meeting for editing and viewing date suggestion votes
                    }
                }
            }
        }



        //TODO: this the part to generate the popup window to generate event through range selection from calendar

        calendar.setHandler(new RangeSelectHandler() {
            @Override
            public void rangeSelect(RangeSelectEvent event) {
                VerticalLayout popupContent = new VerticalLayout();
                popupContent.setSpacing(true);
                popupContent.setMargin(true);
                String startdate=event.getStart().toString();
                TextField Subject=new TextField("Subject");
                Subject.setWidth("100%");
                Subject.addValidator(new NullValidator("Must be given", false));
                Subject.setInputPrompt("Add a subject");
                Subject.setRequired(true);
                popupContent.addComponent(Subject);
                TextArea Description=new TextArea("Description");
                Description.setInputPrompt("Add a description");
                Description.addValidator(new NullValidator("Must be given", false));
                Description.setRequired(true);
                Description.setWidth("100%");
                popupContent.addComponent(Description);
                HorizontalLayout start_end=new HorizontalLayout();
                start_end.setSpacing(true);
                TextField Start_date=new TextField("Start");
                start_end.addComponent(Start_date);
                TextField End_date=new TextField("End");
                start_end.addComponent(End_date);
                popupContent.addComponent(start_end);
                HorizontalLayout long_date=new HorizontalLayout();
                long_date.setSpacing(true);
                TextField total_hours=new TextField("How long");
                long_date.addComponent(total_hours);
                TextField day_date=new TextField("Day & Date");
                long_date.addComponent(day_date);
                popupContent.addComponent(long_date);
                HorizontalLayout pop_button=new HorizontalLayout();
                pop_button.setSpacing(true);
                Button confirm_event=new Button("Confirm");
                confirm_event.addStyleName("new");;
                //confirm_event.setIcon(new ThemeResource("../runo/icons/16/ok.png"));
                confirm_event.addClickListener(clickEvent -> {
                    try {
                        Subject.validate();
                        Description.validate();
                    } catch (Validator.InvalidValueException e) {
                        Subject.setRequiredError("Subject Required !");
                        Description.setRequiredError("Description Required !");
                        return;
                    }
                    String[] start1 = total_hours.getValue().toString().split(" ");
                    Calendar_Event calendar_event;
                    int minutes=(Integer.parseInt(start1[0])*60)+Integer.parseInt(start1[2]);
                    if (minutes==0)
                    {
                                calendar_event = new Calendar_Event(
                                Subject.getValue(),
                                Description.getValue(),
                                1440,
                                startdate
                        );
                    }
                    else {
                                calendar_event = new Calendar_Event(
                                Subject.getValue(),
                                Description.getValue(),
                                minutes,
                                startdate
                        );
                    }
                    try {
                        if (service.calendereventcreation(calendar_event)){
                            Notification n = new Notification("successfully added to calendar");
                            calendar.addEvent(new BasicEvent(calendar_event.getSubject(),
                            calendar_event.getDescription(),
                            event.getStart(),
                            event.getEnd()));
                            n.setDelayMsec(3000);
                            n.show(Page.getCurrent());
                            MainView mainView = (MainView) getParent().getParent();
                            mainView.showStartView();
                        }
                    } catch (Exception e) {
                        Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);

                    }
                });
                pop_button.addComponent(confirm_event);
                Button cancel_event=new Button("Cancel");
                cancel_event.addStyleName("new");
                cancel_event.setIcon(new ThemeResource("../runo/icons/16/cancel.png"));
                pop_button.addComponent(cancel_event);
                popupContent.addComponent(pop_button);
                Subject.clear();
                Description.clear();
                final PopupView popup = new PopupView(null,popupContent);
                Start_date.setEnabled(false);
                End_date.setEnabled(false);
                day_date.setEnabled(false);
                String[] start = event.getStart().toString().split(" ");
                String[] start_parts = start[3].split(":");
                int start_hour=Integer.parseInt(start_parts[0]);
                int start_min=Integer.parseInt(start_parts[1]);
                int start_sec=Integer.parseInt(start_parts[2]);
                String[] end = event.getEnd().toString().split(" ");
                String[] end_parts = end[3].split(":");
                int end_hour=Integer.parseInt(end_parts[0]);
                int end_min=Integer.parseInt(end_parts[1]);
                int end_sec=Integer.parseInt(end_parts[2]);
                int total_hour=end_hour-start_hour;
                int total_minute=end_min-start_min;
                if (total_minute<0)
                {
                    total_minute=30;
                    total_hour=total_hour-1;
                }
                int check=end_hour-start_hour;
                if(check==0)
                {
                    Start_date.setValue("00:00:00");
                    End_date.setValue("24:00:00");
                    total_hours.setValue("24 hours 00 minutes");
                }
                else {
                    Start_date.setValue(start[3].toString());
                    End_date.setValue(end[3].toString());
                    total_hours.setValue(total_hour + " hours " + total_minute + " minutes");
                }
                day_date.setValue(start[0]+" "+start[1]+" "+start[2]);
                total_hours.setEnabled(false);
                popup.setHideOnMouseOut(false);
                cancel_event.addClickListener(clickEvent -> {
                    popup.setPopupVisible(false);
                });
                popup.setPopupVisible(true);
                addComponent(popup);
                setComponentAlignment(popup, Alignment.MIDDLE_CENTER);
            }

        });

        addComponent(calendar);
    }
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User user = Utils.getCurrentUser();
        if (user == null) {
            getUI().getNavigator().navigateTo("");
        } else {
            getUI().getNavigator().navigateTo("main");
        }
    }
}


