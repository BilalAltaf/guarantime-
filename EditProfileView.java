package com.guarantime;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.tools.ant.taskdefs.email.EmailAddress;

public class EditProfileView extends FormLayout implements View {
    private Service service;
    private User user;
    private TextField FirstName;
    private TextField LastName;
    private TextField EmailAddress;
    private TextField TelNo;
    private TextField Username;

    public EditProfileView(Service service) {
        this.service = service;
        User user = Utils.getCurrentUser();

        setSpacing(true);
        setMargin(true);
        setHeight("630px");
        Label pageHeadingLabel = new Label("Update Profile");
        pageHeadingLabel.addStyleName("heading");
        addComponent(pageHeadingLabel);

        FirstName = new TextField("First Name");
        FirstName.setValue(user.getFirstname());
        addComponent(FirstName);
        LastName = new TextField("Last Name");
        LastName.setValue(user.getLastname());
        addComponent(LastName);
        TelNo = new TextField("Telephone No");
        TelNo.setValue(user.getTel());
        addComponent(TelNo);
        EmailAddress = new TextField("Email-Address");
        EmailAddress.setValue(user.getEmail());
        EmailAddress.setEnabled(false);
        addComponent(EmailAddress);
        Username = new TextField("Username");
        String username=user.getUsername();
        Username.setEnabled(false);
        if (username.equals("default"))
        {
            Username.setEnabled(true);
        }
        Username.setValue(user.getUsername());
        addComponent(Username);
        Button updateButton = new Button("Save");
        updateButton.addStyleName("newstyle");
        updateButton.addClickListener(clickEvent -> {

            if (Username.getValue().equals("default"))
            {
                Username.setEnabled(true);
            }
            else
            {
                Username.setEnabled(false);
            }

            updateprofile update = new updateprofile(
                    FirstName.getValue(),
                    LastName.getValue(),
                    TelNo.getValue(),
                    EmailAddress.getValue(),
                    Username.getValue()
            );
            User user1=new User(EmailAddress.getValue(),
                    user.getPasswordHash(),
                    FirstName.getValue(),
                    LastName.getValue(),
                    TelNo.getValue(),
                    Username.getValue(),
                    null);
            user1.setId((int) user.getId());
            user1.setAccessToken(user.getAccessToken());
            VaadinSession.getCurrent().setAttribute("user", user1);

            try {
                boolean check=service.updateprofile(update);
                if (check==true) {
                    Notification n = new Notification("profile updated!");
                    n.setDelayMsec(3000);
                    n.show(Page.getCurrent());
                    /**
                     * TODO: This is a hack to move to the start view and update the meeting list after a meeting has
                     * been created. A better solution might be something like the message bus in the Vaadin Dashboard
                     * demo app.
                     */
                }
            } catch (Exception e) { // TODO
                Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
            }
        });
        addComponent(updateButton);
    }

    private void updateFields(User user) {
        //nameField.setValue(user.getName());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        User user = Utils.getCurrentUser();
        if (user == null) {
            getUI().getNavigator().navigateTo("");
        }
    }
}

