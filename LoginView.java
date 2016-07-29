package com.guarantime;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ChameleonTheme;
import com.vaadin.ui.themes.ValoTheme;

import java.awt.*;
import java.awt.GridLayout;
import java.io.File;

public class LoginView extends VerticalLayout implements View {
    private Service service;

    public LoginView(Service service) {
        this.service = service;
        setSpacing(true);
        setMargin(true);
        /*ExternalResource resource = new ExternalResource("GuarantimeUI/image/11.jpg");
        ThemeResource resource = new ThemeResource("image/11.jpg");*/


        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);
        /*ClassResource resource = new ClassResource(
                "/image/logo.jpg");*/
        ThemeResource resource = new ThemeResource("../garantimeui/image/logo.jpg");
        Image image = new Image("",
                resource);
        image.setWidth("90px");
        image.setHeight("90px");
        layout.addComponent(image);
        layout.setComponentAlignment(image, Alignment.TOP_CENTER);
        Label pageHeadingLabel = new Label("Log into your Guarantime account.");
        pageHeadingLabel.addStyleName(ValoTheme.LABEL_BOLD);
        //use success style for guarantime
        layout.addComponent(pageHeadingLabel);
        Label pageHeadingLabel1 = new Label(".");
        pageHeadingLabel1.setVisible(false);
        layout.addComponent(pageHeadingLabel1);
        layout.setComponentAlignment(pageHeadingLabel, Alignment.TOP_CENTER);
        layout.setComponentAlignment(pageHeadingLabel1, Alignment.TOP_CENTER);
        Panel panel = new Panel(layout);
        //panel.addStyleName(ValoTheme.PANEL_WELL);
        addComponent(panel);
        panel.setSizeUndefined();
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);




        TextField emailField = new TextField("Enter your email to get started.");
        emailField.setWidth("374px");
        emailField.setRequired(true);
        emailField.focus();
        emailField.addValidator(new EmailValidator("Please provide a valid e-mail address"));
        emailField.setInputPrompt("email address");
        emailField.setIcon(FontAwesome.ENVELOPE);
        layout.addComponent(emailField);
        Button continueButton = new Button("Continue");
        //continueButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        continueButton.addStyleName("new");
        continueButton.setClickShortcut(KeyCode.ENTER);
        layout.addComponent(continueButton);
        HorizontalLayout register_line=new HorizontalLayout();
        register_line.setSpacing(true);
        Label register=new Label("Don't have an account? ");
        Link registerLink = new Link("Sign up.", new ExternalResource("#!registration"));
        register_line.addComponent(register);
        register_line.addComponent(registerLink);
        layout.addComponent(register_line);
        PasswordField passwordField = new PasswordField("Enter your password.");
        passwordField.setInputPrompt("password");
        passwordField.setWidth("374px");
        passwordField.setRequired(true);
        passwordField.setIcon(FontAwesome.LOCK);
        layout.addComponent(passwordField);
        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setSpacing(true);
        Button loginButton = new Button("Login");
        loginButton.setClickShortcut(KeyCode.ENTER);
        loginButton.addStyleName("new");

        loginButton.addClickListener(clickEvent -> {

                // Do the fields have valid data?
                try {
                    passwordField.validate();
                } catch (Validator.InvalidValueException e) {
                    passwordField.setValidationVisible(true);
                    if(passwordField.getValue().equals(""))
                        {
                            passwordField.setRequired(true);
                            passwordField.setRequiredError("Password is required");
                            Notification.show("Field Required!", Notification.Type.WARNING_MESSAGE);
                        }
                    else {
                        Notification.show("Please Write Correct format of Email", Notification.Type.WARNING_MESSAGE);
                    }
                    return;
                }

                try {
                    User user =service.login(emailField.getValue(), passwordField.getValue());
                    if (user!=null) {
                        VaadinSession.getCurrent().setAttribute("user", user);
                        getUI().getNavigator().navigateTo("main");

                    }
                    else if (user==null) {
                        passwordField.clear();
                        passwordField.focus();
                        Notification.show("Invalid password", Notification.Type.WARNING_MESSAGE);
                        return;
                    }

                } catch (Exception e) {


                }
        });
        layout.addComponent(loginButton);
        /*VerticalLayout popupContent = new VerticalLayout();
        popupContent.setSpacing(true);
        Label head = new Label("<strong>Let's get you into your account</strong>");
        head.setContentMode(ContentMode.HTML);
        popupContent.addComponent(head);
        TextField email=new TextField("Enter email:");
        email.setRequired(true);
        email.addValidator(new EmailValidator("Please provide a valid e-mail address"));
        popupContent.addComponent(email);

        Button Continue = new Button("continue");
        Continue.addStyleName(ValoTheme.BUTTON_DANGER);
        popupContent.addComponent(Continue);
        Continue.addClickListener(clickEvent -> {


        });
        HorizontalLayout forget_=new HorizontalLayout();
        PopupView forgetpassword = new PopupView("Please send me a recovery email. ", popupContent);
        forgetpassword.setHideOnMouseOut(false);
        layout.addComponent(forgetpassword);*/
        Link link=new Link("(This is not me.)",new ExternalResource(""));
        link.setVisible(false);
        layout.addComponent(link);
        HorizontalLayout hor = new HorizontalLayout();
        hor.setVisible(false);
        Link link1 = new Link("Please send me a recovery email.",null);
        hor.addComponent(link1);
        hor.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                try {
                    if(service.forgetpassword(emailField.getValue()))
                    {
                        Notification successNotification = new Notification("Email sent!");
                        successNotification.setDescription("You will receive an e-mail allowing you to change your password");
                        successNotification.setDelayMsec(-1);
                        successNotification.show(Page.getCurrent());

                    }

                } catch (Exception e) {

                }
            }
        });
        layout.addComponent(hor);
        loginButton.setVisible(false);
        passwordField.setVisible(false);
//        forgetpassword.setVisible(false);
        continueButton.addClickListener(clickEvent -> {
            passwordField.focus();
            try {
                emailField.validate();
            }
            catch (Validator.InvalidValueException e)
            {
                if (emailField.getValue().equals("")) {
                    emailField.setRequired(true);
                    emailField.setRequiredError("Email Address is required");
                    Notification.show("Field Required!", Notification.Type.WARNING_MESSAGE);
                } else {
                    Notification.show("Please Write Correct format of Email", Notification.Type.WARNING_MESSAGE);
                }
                return;
            }
                try{
                    if(service.verifyemail(emailField.getValue()))
                    {
                        pageHeadingLabel1.addStyleName(ValoTheme.LABEL_SUCCESS);
                        pageHeadingLabel1.setValue("Welcome back, " + emailField.getValue() + "!");
                        hor.setVisible(true);
                        continueButton.setVisible(false);
                        pageHeadingLabel.setVisible(false);
                        pageHeadingLabel1.setVisible(true);
                        emailField.setVisible(false);
                        continueButton.setVisible(false);
                        register_line.setVisible(false);
                        loginButton.setVisible(true);
                        passwordField.setVisible(true);
                        link.setVisible(true);
                    }
                    else
                    {
                        pageHeadingLabel.addStyleName(ValoTheme.LABEL_FAILURE);
                        pageHeadingLabel.setValue("No account exists for " + emailField.getValue() + "!");
                    }
                }
                catch (ServiceException e) {

                }


        });
    }

    @Override
    public void enter(ViewChangeEvent event) {
        User user = Utils.getCurrentUser();
        if (user != null) {
            // Get the users e-mail address
            getUI().getNavigator().navigateTo("main");
            Page.getCurrent().reload();
        }
    }
}
