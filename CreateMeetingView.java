package com.guarantime;

import com.vaadin.data.Container;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.themes.ValoTheme;

import java.util.*;

public class CreateMeetingView extends FormLayout implements View {
    private Service service;

    public CreateMeetingView(Service service) {
        this.service = service;

        setSpacing(true);
        setMargin(true);
        Label pageHeadingLabel = new Label("Create Meeting");
        pageHeadingLabel.addStyleName("heading");
        addComponent(pageHeadingLabel);
        TextField subjectField = new TextField("Subject");
        subjectField.setInputPrompt("Add a subject");
        subjectField.setRequired(true);
        subjectField.addValidator(new NullValidator("Must be given", false));
        addComponent(subjectField);

        TextArea descriptionArea = new TextArea("Description");
        descriptionArea.setInputPrompt("Add a description");
        descriptionArea.setRequired(true);
        descriptionArea.addValidator(new NullValidator("Must be given", false));
        addComponent(descriptionArea);

        ComboBox m_hours = new ComboBox("How long", getmeetinghours());
        m_hours.setInputPrompt("Set Hours");
        m_hours.setWidth("15%");
        m_hours.setNewItemsAllowed(true);
        m_hours.setRequired(true);
        m_hours.addValidator(new NullValidator("Must be given", false));
        addComponent(m_hours);

        List<DateField> dateFields = new ArrayList();
        DateField d1=new DateField("Suggested date");
        d1.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                Date dateValue = (Date) value;
                Date now = new Date();
                if (dateValue.before(now))
                {
                    d1.clear();
                    Notification.show("select date from coming dates", Notification.Type.WARNING_MESSAGE);
                }
            }
        });
        d1.setRequired(true);
        d1.addValidator(new NullValidator("Must be given", false));
        dateFields.add(d1);
        DateField d2=new DateField("Suggested date");
        d2.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                Date dateValue = (Date) value;
                Date now = new Date();
                if (dateValue.before(now))
                {
                    d2.clear();
                    Notification.show("select date from coming dates", Notification.Type.WARNING_MESSAGE);
                }
            }
        });
        d2.setRequired(true);
        d2.addValidator(new NullValidator("Must be given", false));
        dateFields.add(d2);
        DateField d3=new DateField("Suggested date");
        d3.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                Date dateValue = (Date) value;
                Date now = new Date();
                if (dateValue.before(now))
                {
                    d3.clear();
                    Notification.show("select date from coming dates", Notification.Type.WARNING_MESSAGE);
                }
            }
        });
        d3.setRequired(true);
        d3.addValidator(new NullValidator("Must be given", false));
        dateFields.add(d3);
        for (DateField df : dateFields) {
            df.setResolution(Resolution.SECOND);
            addComponent(df);
        }
        Button addDateButton = new Button("Add date");
        addDateButton.addStyleName("default");
        addDateButton.setIcon(new ThemeResource("../runo/icons/16/plus_button.png"));
        addComponent(addDateButton);
        addDateButton.addClickListener(clickEvent -> {
            if(dateFields.size()<5) {
                DateField df = new DateField("Suggested date");
                df.setResolution(Resolution.SECOND);

                dateFields.add(df);

                addComponent(df, getComponentIndex(dateFields.get(dateFields.size() - 2)) + 1);

            }

            else{
                Notification.show("You cann't add more dates", Notification.Type.WARNING_MESSAGE);
            }
        });

        VerticalLayout popupContent1 = new VerticalLayout();
        popupContent1.setSpacing(true);
        popupContent1.setMargin(true);
        ListSelect invitedUsers = new ListSelect("Invited users");
        invitedUsers.setRequired(true);
        invitedUsers.addValidator(new NullValidator("Must be given", false));
        invitedUsers.setNullSelectionAllowed(false);
        invitedUsers.setWidth("100%");
        invitedUsers.setHeight("45%");
        popupContent1.addComponent(invitedUsers);
        Button removeButton = new Button("Remove");
        removeButton.addStyleName("new");
        removeButton.addClickListener(clickEvent -> {
            invitedUsers.removeItem(invitedUsers.getValue());
        });
        popupContent1.addComponent(removeButton);
        ComboBox Invitepeoples = new ComboBox("Enter contact ");
        Invitepeoples.setNullSelectionAllowed(true);
        Invitepeoples.setIcon(FontAwesome.ENVELOPE);
        Invitepeoples.setInputPrompt("Choose from list");
        Invitepeoples.setTextInputAllowed(true);
        Invitepeoples.setNewItemsAllowed(true);
        Invitepeoples.setImmediate(true);
        Invitepeoples.setRequired(true);
        Invitepeoples.addValidator(new EmailValidator("Please provide a valid e-mail address"));

        Invitepeoples.setFilteringMode(FilteringMode.CONTAINS);
        try {
            List<String> contacts = new ArrayList<String>();
            contacts=service.showcontact();
            for (int i = 0; i < contacts.size(); i++) {
                Invitepeoples.addItem(contacts.get(i));
            }
        } catch (Exception e) { // TODO
            Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);

        }

        Button adduserButton = new Button("Add guest");
        adduserButton.addStyleName("new");
        adduserButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        Invitepeoples.addValueChangeListener(event ->{
            Invitepeoples.setImmediate(true);
        });
        adduserButton.addClickListener(clickEvent -> {
            try
            {
                Invitepeoples.validate();
            }
            catch(Exception e)
            {
                Invitepeoples.setRequiredError("Required !");
                Notification.show("Please Write Correct format of Email", Notification.Type.WARNING_MESSAGE);
                return;
            }
            User user = Utils.getCurrentUser();
            if(Invitepeoples.getValue().equals(user.getEmail()))
            {
                Invitepeoples.clear();
                Invitepeoples.focus();
                Notification.show("You cannot  invite yourself in meeting", Notification.Type.WARNING_MESSAGE);
                return;
            }
            invitedUsers.addItem(Invitepeoples.getValue());
            Invitepeoples.clear();
            return;
        });
        popupContent1.addComponent(Invitepeoples);
        popupContent1.addComponent(adduserButton);
        PopupView popup1 = new PopupView("Invite guests", popupContent1);
        popup1.addStyleName("vis");
        popup1.setHideOnMouseOut(false);
        HorizontalLayout invite_suggestion=new HorizontalLayout();
        invite_suggestion.setSpacing(true);
        invite_suggestion.addComponent(popup1);
        VerticalLayout popupContent = new VerticalLayout();
        PopupView popup = new PopupView("Check Busy Time",popupContent);
        popup.addStyleName("vis");
        popup.setHideOnMouseOut(false);
        popup.addPopupVisibilityListener(Event ->{
            List<String> invitedUsersList = new ArrayList<String>((Collection<String>) invitedUsers.getItemIds());
            Collection<Free_slot> free_slots;
            Calendar calendar = new Calendar("Busy time");
            calendar.setWidth("600px");  // Undefined by default
            calendar.setHeight("600px"); // Undefined by default
            // Use US English for date/time representation
            calendar.setLocale(new Locale("en", "US"));
            if(invitedUsersList.size()>0) {
                try {
                    free_slots = service.suggesteddates(invitedUsersList);
                } catch (ServiceException e) {
                    Notification.show("Failed to get free_slot created by user from the database", Notification.Type.ERROR_MESSAGE);
                    return;
                }

                if (free_slots != null) {
                    for (Free_slot f : free_slots) {
                        String startdates = f.getStart_date();
                        String enddates = f.getEnd_date();
                        String[] start_datetimeParts = startdates.split(" ");
                        String[] end_datetimeParts = enddates.split(" ");
                        String start_date = start_datetimeParts[0];
                        String end_date = end_datetimeParts[0];
                        String[] Start_datesplit = start_date.toString().split("-");
                        String[] End_datesplit = end_date.toString().split("-");
                        String start_year_string = Start_datesplit[0];
                        String start_month_string = Start_datesplit[1];
                        String start_day_string = Start_datesplit[2];
                        int start_day = Integer.parseInt(start_day_string);
                        int start_year = Integer.parseInt(start_year_string);
                        int start_month = Integer.parseInt(start_month_string);
                        start_month = start_month - 1;
                        String end_year_string = End_datesplit[0];
                        String end_month_string = End_datesplit[1];
                        String end_day_string = End_datesplit[2];
                        int end_day = Integer.parseInt(end_day_string);
                        int end_year = Integer.parseInt(end_year_string);
                        int end_month = Integer.parseInt(end_month_string);
                        end_month = end_month - 1;
                        String starttime = start_datetimeParts[1];
                        String endtime = end_datetimeParts[1];
                        String[] start_timesplit = starttime.split(":");
                        String[] end_timesplit = endtime.split(":");
                        String start_hour_string = start_timesplit[0];
                        String start_minute_string = start_timesplit[1];
                        String start_second_string = start_timesplit[2];
                        int start_hour = Integer.parseInt(start_hour_string);
                        int start_minute = Integer.parseInt(start_minute_string);
                        int start_second = Integer.parseInt(start_second_string);
                        String end_hour_string = end_timesplit[0];
                        String end_minute_string = end_timesplit[1];
                        String end_second_string = end_timesplit[2];
                        int end_hour = Integer.parseInt(end_hour_string);
                        int end_minute = Integer.parseInt(end_minute_string);
                        int end_second = Integer.parseInt(end_second_string);
                        GregorianCalendar eventStart =
                                new GregorianCalendar(start_year, start_month, start_day, start_hour, start_minute, start_second);
                        GregorianCalendar eventEnd =
                                new GregorianCalendar(end_year, end_month, end_day, end_hour, end_minute, end_second);
                        BasicEvent bevent = new BasicEvent("Busy", "Busy time to schedule meeting", eventStart.getTime(), eventEnd.getTime());
                        bevent.setStyleName("color4");
                        calendar.addEvent(bevent);

                    }
                    popupContent.addComponent(calendar);
                }
            }else {
                    Notification n = new Notification("First add users!");
                    n.show(Page.getCurrent());
                }
        });
        invite_suggestion.addComponent(popup);
        addComponent(invite_suggestion);
        Button addButton = new Button("Save");
        addButton.addStyleName("newstyle");
        addButton.addClickListener(clickEvent -> {
            try {
                subjectField.validate();
                descriptionArea.validate();
                m_hours.validate();
                d1.validate();
                d2.validate();
                d3.validate();
            } catch (Validator.InvalidValueException e) {
                subjectField.setRequiredError("Subject Required !");
                descriptionArea.setRequiredError("Description Required !");
                m_hours.setRequiredError("Set hours Required !");
                d1.setRequiredError("Set first date !");
                d2.setRequiredError("Set second date!");
                d3.setRequiredError("Set third date !");
                return;
            }

            List<String> invitedUsersList = new ArrayList<String>((Collection<String>) invitedUsers.getItemIds());
            if(invitedUsersList.size()==0)
            {
                Notification.show("Please invite users !", Notification.Type.WARNING_MESSAGE);
                return;
            }
            List<String> suggestedDatesList = new ArrayList<String>();
            for (DateField df : dateFields) {
                if (df.getValue() != null) {
                    suggestedDatesList.add(df.getValue().toString());
                }
            }
            User user = Utils.getCurrentUser();
            Meeting meeting = new Meeting(
                    subjectField.getValue(),
                    descriptionArea.getValue(),
                    user.getEmail(),
                    invitedUsersList,
                    suggestedDatesList
            );
            int minutes=(int)m_hours.getValue()*60;
            meeting.setMeeting_hour(minutes);

            try {
                if (service.createMeeting(meeting)){
                    Notification n = new Notification("Meeting successfully created!");
                    n.setDelayMsec(3000);
                    n.show(Page.getCurrent());
                    /**
                     * TODO: This is a hack to move to the start view and update the meeting list after a meeting has
                     * been created. A better solution might be something like the message bus in the Vaadin Dashboard
                     * demo app.
                     */
                    MainView mainView = (MainView) getParent().getParent();
                    mainView.updateMeetingsItem();
                    mainView.showStartView();
                }
            } catch (Exception e) { // TODO
                Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);

            }

        });
        addComponent(addButton);
    }

    private Container getmeetinghours() {
        IndexedContainer container = new IndexedContainer();

        for (int i=1;i<=24;i++) {
            container.addItem(i);
        }
        return container;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User user = Utils.getCurrentUser();

        // Is the user logged in?
        if (user == null) {
            // Get the users e-mail addres

            // User not logged in, throw them back to login page
            getUI().getNavigator().navigateTo("");
            Notification.show("Please log in to continue", Notification.Type.WARNING_MESSAGE);
        }
    }
}