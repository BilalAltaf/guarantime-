package com.guarantime;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainView extends VerticalLayout implements View {
    private Service service;
    // This panel shows the main functionality in this view
    private Panel panel;
    private MenuBar menu;

    private TextField nameField;
    // This needs updating after constructor
    private MenuBar.MenuItem meetingItem;

    public MainView(Service service) {
        this.service = service;

        panel = new Panel();

        setSpacing(true);
        //setMargin(true);

        menu = new MenuBar();
        menu.addItem("Home", (selectedItem) -> {
            showStartView();
        });

        menu.addItem("Create Meeting", (selectedItem) -> {
            showCreateMeetingView();
        });

        menu.addItem("Update Profile", (selectedItem) -> {

            showEditProfileView();

        });
        menu.addItem("Contacts", (selectedItem) -> {

            showContacts();

        });
        menu.addItem("Change Password", (selectedItem) -> {

            showChangePasswordView();

        });

        menu.addItem("Log out", (selectedItem) -> {
            VaadinSession.getCurrent().close();
            // Need to reload the page after closing the session (this will actually redirect to login in this case)
            Page.getCurrent().reload();
        });
        addComponent(menu);



        addComponent(panel);
        VerticalLayout footer = new VerticalLayout();
        footer.setSpacing(true);
        footer.addStyleName("foo");
        Label commitment=new Label("Our Commitment");
        commitment.addStyleName("foo1");
        //footer.addComponent(commitment);
        Label tagline=new Label("Connecting People, Independently  without their Calendars.");
        tagline.addStyleName("foo2");
       // footer.addComponent(tagline);
        addComponent(footer);
        HorizontalLayout social_button=new HorizontalLayout();
        social_button.setSpacing(true);
        Button facebook=new Button();
        facebook.addClickListener(clickEvent -> {
            Page.getCurrent().open("https://www.facebook.com", "_self");
        });
        facebook.setIcon(new ThemeResource("../runo/icons/16/facebook.png"));
        facebook.addStyleName("Social_website");
        social_button.addComponent(facebook);
        Button twitter=new Button();
        twitter.addClickListener(clickEvent -> {
            Page.getCurrent().open("https://www.twitter.com", "_self");
        });
        twitter.setIcon(new ThemeResource("../runo/icons/16/twitter.png"));
        twitter.addStyleName("Social_website");
        social_button.addComponent(twitter);
        Button google=new Button();
        google.addClickListener(clickEvent -> {
            Page.getCurrent().open("https://plus.google.com/", "_self");
        });
        google.setIcon(new ThemeResource("../runo/icons/16/google.png"));
        google.addStyleName("Social_website");
        social_button.addComponent(google);
        footer.addComponent(social_button);
        Label copyright=new Label("©  2016 Guarantime. All rights reserved. Registered in Finland");
        copyright.addStyleName("-footer-copyright");
        footer.addComponent(copyright);
    }

    public void showStartView() {

        panel.setContent(new startView(service));
    }
    public  void showContacts(){
        panel.setContent(new Contactview(service));
    }

    private void addMeetingsItem() {
        Collection<Meeting> meetings;

        try {
            meetings = service.getMeetingsCreatedByUser(Utils.getCurrentUser().getId());
        } catch (ServiceException e) {
            Notification.show("Failed to get meetings created by user from the database", Notification.Type.ERROR_MESSAGE);
            return;
        }

        // Add this as the second menu item... so before the current second item
        meetingItem = menu.addItemBefore("My Meetings" , null, null, menu.getItems().get(2));


        for (Meeting m : meetings) {
            meetingItem.addItem(m.getSubject(), selectedItem -> {
                showMeetingView(m);

                // Show meeting for editing and viewing date suggestion votes
            });
        }
    }

    // TODO: This is a hack to let meeting creation view update meeting list in main view... move to event bus or somehting
    public void updateMeetingsItem() {
        menu.removeItem(meetingItem);
        addMeetingsItem();
    }

    private void showCreateMeetingView() {
        /* TODO: If we write something in this view, then navigate away and back again, we should probably retain what
           was written here */
        // TODO: Can you have another navigator here, instead of just setting content?
        panel.setContent(new CreateMeetingView(service));
    }
    private void showMeetingView(Meeting meetings) {

        try {

            Meeting meeting=service.showsinglemeeting(meetings);
            panel.setContent(new MeetingView(service, meeting));
        } catch (ServiceException e) {
            Notification.show("Failed to get invitations from the database", Notification.Type.ERROR_MESSAGE);
            return;
        }
        }




    private void showEditProfileView() {
        panel.setContent(new EditProfileView(service));
    }

    private void addSyncCalendar(){
        MenuBar.MenuItem calendarItem = menu.addItemBefore("Sync Calendars" , null, null, menu.getItems().get(6));
        calendarItem.addItem("Google",new ThemeResource("../garantimeui/icons/google.png"), selectedItem -> {
            try {
                String response = service.sycGoogleCalendar();
                Page.getCurrent().open(response, "_self");

                Notification n = new Notification("Google Calendar Synchronized Successfully!");
                n.setDelayMsec(3000);
                n.show(Page.getCurrent());
                MainView mainView = (MainView) getParent().getParent();
                mainView.updateMeetingsItem();
                mainView.showStartView();

            }
            catch (ServiceException e) {
                Notification.show("Failed to get invitations from the database", Notification.Type.ERROR_MESSAGE);
                return;
            }
         });
        calendarItem.addItem("icalendar",new ThemeResource("../garantimeui/icons/ical.png"), selectedItem -> {

            // Show meeting for editing and viewing date suggestion votes
        });
        calendarItem.addItem("Outlook",new ThemeResource("../garantimeui/icons/outlook.png"), selectedItem -> {

            // Show meeting for editing and viewing date suggestion votes
        });
    }
    private void addInvitationsItem() {
        Collection<Meeting> invitations;
        User user = Utils.getCurrentUser();
        try {
            invitations = service.getInvitations(user.getEmail());
        } catch (ServiceException e) {
            Notification.show("Failed to get invitations from the database", Notification.Type.ERROR_MESSAGE);
            return;
        }

        // Add this as the fourth menu item
        MenuBar.MenuItem item = menu.addItemBefore(invitations.size() + " invitation(s)", null, null, menu.getItems().get(3));

        for (Meeting m : invitations) {
            item.addItem(m.getSubject(), selectedItem -> {
                showInvitationView(m);
            });
        }
    }
    private void showChangePasswordView() {
        panel.setContent(new ChangePasswordView(service));
    }

    private void showInvitationView(Meeting meetings)
    {
        try {

        Meeting meeting=service.showsinglemeeting(meetings);
        panel.setContent(new InvitationView(service, meeting));
    } catch (ServiceException e) {
        Notification.show("Failed to get invitations from the database", Notification.Type.ERROR_MESSAGE);
        return;
    }

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User user = Utils.getCurrentUser();

        // Is the user logged in?
        if (user == null) {
            getUI().getNavigator().navigateTo("");

        } else {
            addMeetingsItem();
            addInvitationsItem();
            addSyncCalendar();
            showStartView();
            // User not logged in, throw them back to login page

            //Notification.show("Please log in to continue", Notification.Type.WARNING_MESSAGE);
        }
    }
}
