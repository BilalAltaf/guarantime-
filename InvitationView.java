package com.guarantime;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

import java.util.Date;
import java.util.List;

// At the moment this view is only used to show meetings that the user has been invited to, this may change
public class InvitationView extends FormLayout implements View {
    private Service service;
    private Meeting meeting;

    public InvitationView(Service service, Meeting meeting) {
        this.service = service;
        this.meeting = meeting;

        setSpacing(true);
        setMargin(true);

        setHeight("630px");
        Label pageHeadingLabel = new Label("Meeting Invitation");
        pageHeadingLabel.addStyleName("heading");
        addComponent(pageHeadingLabel);

        TextField subjectField = new TextField("Subject:");
        subjectField.setValue(meeting.getSubject());
        subjectField.setReadOnly(true);
        addComponent(subjectField);
        //ProgressBar progress = new ProgressBar(value);

        TextArea descriptionArea = new TextArea("Description:");
        descriptionArea.setValue(meeting.getDescription());
        descriptionArea.setReadOnly(true);
        addComponent(descriptionArea);

        /*TextArea area = new TextArea("Comment");
        area.setInputPrompt("Comment here");
        area.setValue(" ");
        addComponent(area);*/
       /* HorizontalLayout coming_maybe=new HorizontalLayout();
        Button coming = new Button("Vote");
        coming.addClickListener(clickEvent -> {
            List<Integer> date_ids = meeting.getDate_id();
            List<String> date1 = meeting.getSuggestedDates();
            for (int i = 0; i < date1.size(); i++) {

                if (date1.get(i).equals(datesBox.getValue().toString())) {
                    int date_id=date_ids.get(i);
                    try{
                        String check=service.votemeeting(date_id,meeting.getAccessToken(),1,area.getValue());
                        if (check.equals("Successfully done")) {
                            Notification n = new Notification("voted Successfully!");
                            n.setDelayMsec(3000);
                            n.show(Page.getCurrent());
                            *//**
                             * TODO: This is a hack to move to the start view and update the meeting list after a meeting has
                             * been created. A better solution might be something like the message bus in the Vaadin Dashboard
                             * demo app.
                             *//*
                        }
                        if (check.equals("User Already Casted Vote")){
                            Notification.show("You Already Casted Vote!", Notification.Type.WARNING_MESSAGE);
                        }
                    } catch (Exception e) { // TODO
                        Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
                    }

                }
            }
              // Save vote for date
        });
        coming_maybe.addComponent(coming);
        //addComponent(coming_maybe);*/
        Grid grid = new Grid();
        grid.setCaption("Meeting Vote");
        grid.addColumn("dates", String.class);
        //grid.addColumn("votes", String.class);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(3);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        Grid.SingleSelectionModel selection =(Grid.SingleSelectionModel) grid.getSelectionModel();
        List<String> votingdates_grid = meeting.getMeeting_voting_dates();
        List<String> confirmmeetingvotes_grid = meeting.getMeeting_confirm_vote();
        List<String> personal_vote = meeting.getPersonal_vote();
        List<Integer> date_ids = meeting.getDate_id();
        List<String> date1 = meeting.getSuggestedDates();

        for (int i = 0; i < date1.size(); i++) {
            grid.addRow(date1.get(i).toString());
            if((personal_vote.size()>0))
            {
               if(date1.get(i).toString().equals(personal_vote.get(0).toString()))
            {
                selection.select( grid.getContainerDataSource().getIdByIndex(i));
            }
            }

            }


        grid.addSelectionListener(selectionEvent -> { // Java 8
            Object selected = ((Grid.SingleSelectionModel)
                    grid.getSelectionModel()).getSelectedRow();



            if (selected != null)
            {for (int i = 0; i < date1.size(); i++) {
                if(date1.get(i).equals(grid.getContainerDataSource().getItem(selected).getItemProperty("dates").toString()))
                {
                try {
                    String check = service.votemeeting(date_ids.get(i), meeting.getAccessToken());
                    if (check.equals("Successfully done")) {
                        Notification.show("Successfully voted for " +
                                grid.getContainerDataSource().getItem(selected)
                                        .getItemProperty("dates"));
                    }
                    if (check.equals("User Already Casted Vote")) {
                        Notification.show("You Already Casted Vote!", Notification.Type.WARNING_MESSAGE);
                    }
                } catch (Exception e) { // TODO
                    Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
                }
                }

            }
        }
            else if (selected == null) {

                try {
                        for (int i = 0; i < date1.size(); i++) {
                        if (date1.get(i).equals(personal_vote.get(0))) {
                            if (service.cancelvote(meeting.getAccessToken(), date_ids.get(i))) {
                                Notification.show("No date selected for vote");
                            } else {
                                Notification.show("previous vote isn't removed");
                            }
                        }
                    }
                } catch (Exception e) { // TODO
                    Notification.show("There was an error when trying to contact the service", Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        addComponent(grid);
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
        // TODO: Check if user is logged in
    }
}

