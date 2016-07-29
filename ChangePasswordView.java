package com.guarantime;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.tools.ant.taskdefs.email.EmailAddress;

public class ChangePasswordView extends FormLayout implements View {
    private Service service;
    private User user;
    private PasswordField oldpassword,newpassword,confirmpassword;

    public ChangePasswordView(Service service) {
        this.service = service;
        User user = Utils.getCurrentUser();

        setSpacing(true);
        setMargin(true);
        setHeight("630px");

        Label pageHeadingLabel = new Label("Change Password");
        pageHeadingLabel.addStyleName("heading");
        addComponent(pageHeadingLabel);
        oldpassword = new PasswordField("Old Password");
        oldpassword.setInputPrompt("Add");
        addComponent(oldpassword);
        newpassword = new PasswordField("New password");
        newpassword.setInputPrompt("Add");
        addComponent(newpassword);
        confirmpassword  = new PasswordField("Confirm password");
        confirmpassword.setInputPrompt("Add");
        addComponent(confirmpassword);
        Button Changepassword = new Button("Save");
        Changepassword.addStyleName("newstyle");
        Changepassword.addClickListener(clickEvent -> {
            if(oldpassword.getValue()== ""|newpassword.getValue()== ""|confirmpassword.getValue()== "")
            {
                if(oldpassword.getValue()== "")
                {
                    oldpassword.setRequired(true);
                    oldpassword.setRequiredError("Old Password is required");
                }
                else if(newpassword.getValue()== "")
                {
                    newpassword.setRequired(true);
                    newpassword.setRequiredError("New Password is required");
                }

                else if(confirmpassword.getValue()== "")
                {
                    confirmpassword.setRequired(true);
                    confirmpassword.setRequiredError("Confirm Password is required");

                }
                Notification.show("Field Required!", Notification.Type.WARNING_MESSAGE);
            }


            else {
                 if (!newpassword.getValue().equals(confirmpassword.getValue()))
                {
                    Notification.show("New and Confirm password doesn't match!", Notification.Type.WARNING_MESSAGE);
                }
                else
                 {
                try {
                    String response=service.ChangePassword(oldpassword.getValue(), newpassword.getValue());
                    if (response.equals("Successfully done")) {
                        Notification n = new Notification("Password Sucessfully change updated!");
                        n.setDelayMsec(3000);
                        n.show(Page.getCurrent());
                        /**
                         * TODO: This is a hack to move to the start view and update the meeting list after a meeting has
                         * been created. A better solution might be something like the message bus in the Vaadin Dashboard
                         * demo app.
                         */
                    } else if (response.equals("Password Incorrect")){

                        Notification.show("Please Enter the correct Old password!", Notification.Type.WARNING_MESSAGE);
                    }

                } catch (Exception e) { // TODO
                    Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
                }
            }
            }
        });
        addComponent(Changepassword);
    }



    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User user = Utils.getCurrentUser();
        if (user == null) {
            getUI().getNavigator().navigateTo("");
        } else {
            // TODO: Send to login
        }
    }
}

