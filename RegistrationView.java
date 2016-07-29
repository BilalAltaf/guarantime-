package com.guarantime;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

public class RegistrationView extends VerticalLayout implements View {
    private Service service;

    public RegistrationView(Service service) {
        this.service = service;
        setSpacing(true);
        setMargin(true);
        Label pageHeadingLabel = new Label("Sign up for Guarantime");
        pageHeadingLabel.addStyleName(ValoTheme.LABEL_BOLD);
        pageHeadingLabel.addStyleName(ValoTheme.DATEFIELD_ALIGN_CENTER);
        //setComponentAlignment(pageHeadingLabel, Alignment.MIDDLE_CENTER);
        //use success style for guarantime
        addComponent(pageHeadingLabel);
        VerticalLayout layout = new VerticalLayout();
        Panel panel = new Panel(layout);
        addComponent(panel);
        panel.setSizeUndefined();
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        layout.setSpacing(true);
        layout.setMargin(true);



        TextField emailField = new TextField("E-mail address");
        emailField.setRequired(true);
        emailField.setWidth("280px");
        emailField.addValidator(new EmailValidator("Please provide a valid e-mail address"));
        emailField.setIcon(FontAwesome.ENVELOPE);
        layout.addComponent(emailField);

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setRequired(true);
        passwordField.setWidth("280px");
        passwordField.setIcon(FontAwesome.LOCK);
        layout.addComponent(passwordField);

        PasswordField confirmPasswordField = new PasswordField("Confirm password");
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setWidth("280px");
        confirmPasswordField.setIcon(FontAwesome.LOCK);
        // Check that both password fields match
        confirmPasswordField.addValidator(value -> {
            if (!(value instanceof String ) || !((String) value).equals(passwordField.getValue())) {
                throw new Validator.InvalidValueException("Please make sure to type the same password in both fields");
            }
        });
        layout.addComponent(confirmPasswordField);

        CheckBox tocCheckbox = new CheckBox("I agree to the terms and conditions");
        tocCheckbox.setRequired(true);
        layout.addComponent(tocCheckbox);

        Button registerButton = new Button("Register");
        registerButton.addStyleName("new");
        registerButton.setClickShortcut(KeyCode.ENTER);
        registerButton.addClickListener(clickEvent -> {
            // Do the fields have valid data?
            try {
                emailField.validate();
                passwordField.validate();
                confirmPasswordField.validate();
                tocCheckbox.validate();
            } catch (Validator.InvalidValueException e) {
                emailField.setValidationVisible(true);
                passwordField.setValidationVisible(true);
                confirmPasswordField.setValidationVisible(true);
                tocCheckbox.setValidationVisible(true);
                Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
                return;
            }

            try {
                if (service.register(emailField.getValue(), passwordField.getValue())) {
                    Notification successNotification = new Notification("The registration was successful!");
                    successNotification.setDescription("You will recieve an e-mail allowing you to confirm your e-mail address, after which you can log in to the site.");
                    // This message is important, make it stay up until the user clicks it
                    successNotification.setDelayMsec(-1);
                    successNotification.show(Page.getCurrent());
                } else {
                    Notification.show("There already exists a user with that e-mail address in the database", Notification.Type.WARNING_MESSAGE);
                }
            } catch (ServiceException e) {
                Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
            }
        });
        layout.addComponent(registerButton);

        Link loginLink = new Link("Back to login", new ExternalResource("#!"));
        layout.addComponent(loginLink);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
