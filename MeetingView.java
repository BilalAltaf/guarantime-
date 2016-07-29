package com.guarantime;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.shared.ui.datefield.Resolution;

import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by bilal on 1/26/2016.
 */

public class MeetingView extends FormLayout implements View {
    private Service service;
    private Meeting meeting;

    public MeetingView(Service service, Meeting meeting) {
        this.service = service;
        this.meeting = meeting;
        setSpacing(true);
        setMargin(true);
        setHeight("630px");

        Label pageHeadingLabel = new Label("Meeting Detail");
        pageHeadingLabel.addStyleName("heading");
        addComponent(pageHeadingLabel);
        TextField subjectField = new TextField("Subject");
        subjectField.setValue(meeting.getSubject());
        addComponent(subjectField);

        TextArea descriptionArea = new TextArea("Description");
        descriptionArea.setValue(meeting.getDescription());
        addComponent(descriptionArea);

        VerticalLayout popupContent1 = new VerticalLayout();
        popupContent1.setSpacing(true);
        popupContent1.setMargin(true);
        ListSelect AddUsers = new ListSelect("Add user");
        AddUsers.setNullSelectionAllowed(false);
        AddUsers.setWidth("100%");
        AddUsers.setHeight("45%");
        popupContent1.addComponent(AddUsers);
        Button removeButton = new Button("Remove");
        removeButton.addStyleName("new");
        removeButton.addClickListener(clickEvent -> {
            AddUsers.removeItem(AddUsers.getValue());
        });
        popupContent1.addComponent(removeButton);
        ComboBox Invitepeoples = new ComboBox("Enter contact");
        Invitepeoples.setIcon(FontAwesome.ENVELOPE);
        Invitepeoples.setNullSelectionAllowed(true);
        Invitepeoples.setInputPrompt("Choose from list");
        Invitepeoples.setTextInputAllowed(true);
        Invitepeoples.setNewItemsAllowed(true);
        Invitepeoples.setImmediate(true);
        Invitepeoples.setRequired(true);
        Invitepeoples.addValidator(new EmailValidator("Please provide a valid e-mail address"));
        Invitepeoples.setFilteringMode(FilteringMode.CONTAINS);
        try {
            /*Collection<Contacts> contacts = new ArrayList();
            contacts=service.showcontact();

                Invitepeoples.addItem(contacts.getName(),contacts.get(i));
            */

        } catch (Exception e) { // TODO
            Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);

        }
        Button adduserButton = new Button("Add guest");
        adduserButton.addStyleName("new");
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
            AddUsers.addItem(Invitepeoples.getValue());
            Invitepeoples.clear();
        });
        popupContent1.addComponent(Invitepeoples);
        popupContent1.addComponent(adduserButton);
        PopupView popup1 = new PopupView("Add Users", popupContent1);
        popup1.addStyleName("vis");
        popup1.setHideOnMouseOut(false);
//-------------------------------------
        VerticalLayout popupContent = new VerticalLayout();
        popupContent.setSpacing(true);
        popupContent.setMargin(true);
        ListSelect RemoveUsers = new ListSelect("Remove user");
        RemoveUsers.setWidth("100%");
        RemoveUsers.setHeight("45%");
        popupContent.addComponent(RemoveUsers);
        List<String> guest = meeting.getInvitedUsers();
        ComboBox Invitepeoples1 = new ComboBox("select invited user from list");
        Invitepeoples1.setIcon(FontAwesome.ENVELOPE);
        try {
            for (int i = 0; i < guest.size(); i++) {
                Invitepeoples1.addItem(guest.get(i));
            }

        } catch (Exception e) { // TODO
            Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);

        }
        Invitepeoples1.setFilteringMode(FilteringMode.CONTAINS);
        Button adduserButton1 = new Button("Remove guest");
        adduserButton1.addStyleName("new");
        adduserButton1.addClickListener(clickEvent -> {
            RemoveUsers.addItem(Invitepeoples1.getValue());
            Invitepeoples1.clear();
        });
        popupContent.addComponent(Invitepeoples1);
        popupContent.addComponent(adduserButton1);
        PopupView popup = new PopupView("remove guest", popupContent);
        popup.addStyleName("vis");
        popup.setHideOnMouseOut(false);
        HorizontalLayout add_remove = new HorizontalLayout();
        add_remove.setSpacing(true);
        add_remove.addComponent(popup1);
        add_remove.addComponent(popup);
        addComponent(add_remove);



        CheckBox confirm_meeting = new CheckBox("Confirm meeting");


        HorizontalLayout horizontal = new HorizontalLayout();
        horizontal.setSpacing(true);
        Grid grid = new Grid();
        grid.setCaption("Meeting Vote");
        grid.addColumn("dates", String.class);
        grid.addColumn("Total votes", String.class);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(3);
        Grid.SingleSelectionModel selection =(Grid.SingleSelectionModel) grid.getSelectionModel();
        List<String> votingdates_grid = meeting.getMeeting_voting_dates();
        List<String> confirmmeetingvotes_grid = meeting.getMeeting_confirm_vote();
        for (int i = 0; i < votingdates_grid.size(); i++) {
            grid.addRow(votingdates_grid.get(i),confirmmeetingvotes_grid.get(i));
        }

        addComponent(grid);
        Button Update = new Button("Save");
        Update.addStyleName("newstyle");
        Update.addClickListener(clickEvent -> {

            Object selected = ((Grid.SingleSelectionModel)
                    grid.getSelectionModel()).getSelectedRow();
            List<String> addUsersList = new ArrayList<String>((Collection<String>) AddUsers.getItemIds());
            List<String> removeUsersList = new ArrayList<String>((Collection<String>) RemoveUsers.getItemIds());
            User user = Utils.getCurrentUser();
            Meeting meeting1 = new Meeting(
                    subjectField.getValue(),
                    descriptionArea.getValue(),
                    user.getEmail(),
                    addUsersList,
                    null
            );
            meeting1.setAccessToken(meeting.getAccessToken());
            meeting1.setId(meeting.getId());
            meeting1.setRemoveuser(removeUsersList);
            if(confirm_meeting.getValue()) {
                if (selected != null) {

                    try {
                        boolean check = service.confirmmeeting(grid.getContainerDataSource().getItem(selected).getItemProperty("dates").toString(), meeting.getId());
                        if (check == true) {
                            try {
                                boolean check1 = service.updatemeeting(meeting1);
                                if (check1 == true) {
                                    Notification n = new Notification("Meeting successfully Confirmed!");
                                    MainView mainView = (MainView) getParent().getParent();
                                    mainView.updateMeetingsItem();
                                    mainView.showStartView();
                                    n.show(Page.getCurrent());
                                }
                            } catch (Exception e) { // TODO
                                Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
                            }
                        }
                    } catch (Exception e) { // TODO
                        Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
                    }
                }
            else if(selected == null) {
                Notification.show("Please select the date!", Notification.Type.WARNING_MESSAGE);
                return;
            }
            }
            else
            {
                try {
                    boolean check=service.updatemeeting(meeting1);
                    if (check==true) {
                        Notification n = new Notification("Meeting Updated Successfully!");
                        n.setDelayMsec(3000);
                        n.show(Page.getCurrent());
                        MainView mainView = (MainView) getParent().getParent();
                        mainView.updateMeetingsItem();
                        mainView.showStartView();
                        /**
                         * TODO: This is a hack to move to the start view and update the meeting list after a meeting has
                         * been created. A better solution might be something like the message bus in the Vaadin Dashboard
                         * demo app.
                         */
                    }
                } catch (Exception e) { // TODO
                    Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
                }
            }

        });
        horizontal.addComponent(Update);

        /*Button Confirmmeeting = new Button("Confirm Meeting");
        Confirmmeeting.addClickListener(clickEvent -> {

        });
        horizontal.addComponent(Confirmmeeting);*/

        Button Cancel = new Button("Cancel");
        Cancel.addStyleName("default");
        Cancel.setIcon(new ThemeResource("../runo/icons/16/cancel.png"));
                Cancel.addClickListener(clickEvent -> {
                    try {
                        boolean check = service.cancelmeeting(meeting.getId());
                        if (check == true) {
                            Notification n = new Notification("meeting Cancelled!");
                            n.setDelayMsec(3000);
                            MainView mainView = (MainView) getParent().getParent();
                            mainView.updateMeetingsItem();
                            mainView.showStartView();
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
        horizontal.addComponent(Cancel);
        addComponent(confirm_meeting);
        addComponent(horizontal);
    }

    private Container getUserContainer() {
        IndexedContainer container = new IndexedContainer();

        container.addContainerProperty("email", String.class, null);

        try {
            for (User user : service.getUsers()) {
                container.addItem(user.getEmail());
            }
        } catch (Exception e) {
            // TODO: Handle this properly
        }

        return container;
    }
    private Container getSuggestedDatesContainer() {
        IndexedContainer container = new IndexedContainer();
        List<String> dates = meeting.getSuggestedDates();
        for (int i = 0; i < dates.size(); i++) {

            if ((dates.get(i)) != null) {

                /*final ProgressBar progress = new ProgressBar(new Float(0.0));
                progress.setEnabled(false);
                addComponent(progress);*/

                container.addItem(dates.get(i).toString());
            }
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
