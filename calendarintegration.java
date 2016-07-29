package com.guarantime;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

public class calendarintegration extends VerticalLayout implements View {
    private Service service;

    public calendarintegration(Service service) {
        this.service = service;

        FormLayout layout = new FormLayout();
        Panel panel = new Panel(layout);
        addComponent(panel);
        panel.setSizeUndefined();
        setComponentAlignment(panel, Alignment.MIDDLE_CENTER);

        layout.setSpacing(true);
        layout.setMargin(true);
    }
    @Override
    public void enter(ViewChangeEvent event) {
        User user = Utils.getCurrentUser();
        if (user != null) {
            // Get the users e-mail address
            String email = user.getEmail();

            // Find the correct component to update
            for (Component c : this) {
                if ("label-email".equals(c.getId())) {
                    Label label = (Label) c;
                    label.setValue("You are logged in with the e-mail address " + email);
                }
            }
        }
    }
}
