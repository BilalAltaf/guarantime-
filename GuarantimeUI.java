package com.guarantime;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;

@Theme("garantimeui")
@Title("Guarantime")

public class GuarantimeUI extends UI {
    private Service service;
    private Navigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        service = new HTTPService();
        //service = new TestService();

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        Label appNameLabel = new Label("Guarantime Meeting Planner");
        layout.addComponent(appNameLabel);

        Panel viewPanel = new Panel();
        layout.addComponent(viewPanel);

        navigator = new Navigator(this, this);

        navigator.addView("", new LoginView(service));
        navigator.addView("registration", new RegistrationView(service));
        navigator.addView("main", new MainView(service));
        //navigator.addView("createmeeting",new CreateMeetingView(service));
     //   navigator.addView("calendarintegration",new calendarintegration(service));
        //navigator.addView("changepassword",new ChangePasswordView(service));
       // navigator.addView("editprofile", new EditProfileView(service));
       // navigator.addView("meetingInvitation",new InvitationView(service));
    }
}
