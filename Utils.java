package com.guarantime;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;

import java.util.*;

public class Utils {
    public static void printIfDebug(String message) {
        if (!VaadinService.getCurrent().getDeploymentConfiguration().isProductionMode()) {
            System.out.println(message);
        }
    }

    public static void notImplemented() {
        Notification.show("Functionality not yet implemented", Notification.Type.WARNING_MESSAGE);
    }

    public static User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute("user");
    }

    /**
     * Generate a fake meeting for testing purposes
     */
    public static Meeting generateFakeMeeting() {
        Random rand = new Random();

        List<String> invitedUsers = new ArrayList();
        invitedUsers.add("one@example.com");
        invitedUsers.add("two@example.com");
        invitedUsers.add("three@example.com");

        // Add a random number of suggested semi-random dates
        List<String> suggestedDates = new ArrayList();
        Calendar calendar = new GregorianCalendar();
        // Add at least one suggested date
        int numberOfDates = rand.nextInt(4) + 1;
        for (int i = 0; i < numberOfDates; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, rand.nextInt(7));
            suggestedDates.add(calendar.getTime().toString());
        }

        Meeting meeting = new Meeting(
                "Meeting #" + Math.abs(rand.nextInt()),
                "Description of our very important meeting",
                "test@test.com",
                invitedUsers,
                suggestedDates
        );

        return meeting;
    }
}
