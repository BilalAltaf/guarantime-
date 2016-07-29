package com.guarantime;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.themes.ValoTheme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by bilal on 2/18/2016.
 */
public class Contactview extends FormLayout implements View {


    public Contactview(Service service) {
        setSpacing(true);
        setMargin(true);
        setHeight("630px");
        Label pageHeadingLabel = new Label("Add Contact");
        pageHeadingLabel.addStyleName("heading");
        addComponent(pageHeadingLabel);
        Collection<Contacts> con = new ArrayList<>();
        List<String> contacts = new ArrayList<String>();
        try {
            contacts = service.showcontact();
            for (int i = 0; i < contacts.size(); i++) {
                con.add(new Contacts( contacts.get(i)));
            }


        } catch (Exception e) { // TODO
            Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);

        }
        BeanItemContainer<Contacts> container =
                new BeanItemContainer<Contacts>(Contacts.class, con);
        Grid grid = new Grid(container);
        grid.setCaption("Contacts Detail");
        grid.setColumnOrder( "email");
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(3);
        addComponent(grid);
        TextField enter_contact = new TextField("Enter contact:");
        enter_contact.setInputPrompt("email address");
        enter_contact.addValidator(new EmailValidator("Please provide a valid e-mail address"));
        Button addButton = new Button("Add");
        addButton.addStyleName("newstyle");
        addButton.addClickListener(clickEvent->

                {
                    User user = Utils.getCurrentUser();
                    if (enter_contact.getValue().equals(user.getEmail())) {
                        enter_contact.clear();
                        enter_contact.focus();
                        Notification.show("You cannot add yourself in contact list", Notification.Type.WARNING_MESSAGE);
                        return;
                    }
                    if (enter_contact.getValue().equals("")) {
                        enter_contact.setRequired(true);
                        enter_contact.setRequiredError("Email required");
                        return;
                    }
                    try {
                        service.addcontact(enter_contact.getValue());
                        MainView mainView = (MainView) getParent().getParent();
                        mainView.updateMeetingsItem();
                        mainView.showContacts();

                    } catch (Exception e) {
                    }
                    enter_contact.clear();
                }

        );
        Button delSelected = new Button("Remove", ee -> {
            // Delete all selected data items
           if(grid.getSelectedRows().size() > 0)
           {
               BeanItem<Contacts> item =
                       container.getItem(grid.getSelectedRow());

               try {
                   service.deletecontact(item.getBean().getEmail());
                   MainView mainView = (MainView) getParent().getParent();
                   mainView.updateMeetingsItem();
                   mainView.showContacts();
                   Notification.show("Deleted successfully " +item.getBean().getEmail());

               } catch (Exception e) {
               }
               for (Object itemId: grid.getSelectedRows())
                   grid.getContainerDataSource().removeItem(itemId);

               grid.getSelectionModel().reset();
           }
           else
           {
               Notification.show("Please select the contact!");
           }
        });
        delSelected.addStyleName("default");
        addComponent(delSelected);
        addComponent(enter_contact);
        addComponent(addButton);
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
