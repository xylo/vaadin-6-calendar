package com.vaadin.addon.calendar.demo;

import java.util.Date;
import java.util.Random;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarTargetDetails;
import com.vaadin.data.Item;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.TableTransferable;
import com.vaadin.ui.UI;

public class CalendarDragAndDropDemo extends UI {

    private static final long serialVersionUID = 5885467281800911082L;

    private String[] eventTypes = { "Birthday", "Holiday", "Party", "Meeting",
            "Training", "Appointment", "Lunch", "Breakfast", "Dinner" };

    private String[] participants = { "Prometheus", "Atlas", "Dione", "Gaea",
            "Uranus", "Cronus", "Rhea", "Oceanus", "Mnemosyne", "Tethys",
            "Themis", "Iapetus", "Coeus", "Phoebe", "Crius", "Hyperion",
            "Thea", "Epimetheus", "Metis" };

    @Override
    public void init(WrappedRequest request) {
        Table table = createDDTable();
        final Calendar calendar = createDDCalendar();

        populateTable(table);

        table.setSizeFull();
        calendar.setSizeFull();

        HorizontalLayout main = new HorizontalLayout();
        main.setWidth("100%");
        main.setHeight("500px");
        main.setSpacing(true);

        main.addComponent(calendar);
        main.addComponent(table);

        main.setExpandRatio(calendar, 0.5f);
        main.setExpandRatio(table, 0.5f);

        Button monthButton = new Button("View month");
        monthButton.addClickListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                java.util.Calendar internalCalendar = calendar
                        .getInternalCalendar();
                internalCalendar.setTime(calendar.getStartDate());
                internalCalendar.set(java.util.Calendar.DATE, 1);
                Date start = internalCalendar.getTime();
                internalCalendar.set(java.util.Calendar.DATE,
                        internalCalendar.getMaximum(java.util.Calendar.DATE));
                Date end = internalCalendar.getTime();

                calendar.setStartDate(start);
                calendar.setEndDate(end);
            }
        });

        getContent().addComponent(monthButton);
        getContent().addComponent(main);
    }

    private Table createDDTable() {
        Table table = new Table();
        table.setDragMode(Table.TableDragMode.ROW);
        return table;
    }

    private Calendar createDDCalendar() {
        Calendar calendar = new Calendar();
        calendar.setDropHandler(new DropHandler() {

            private static final long serialVersionUID = -8939822725278862037L;

            public void drop(DragAndDropEvent event) {
                CalendarTargetDetails details = (CalendarTargetDetails) event
                        .getTargetDetails();
                TableTransferable transferable = (TableTransferable) event
                        .getTransferable();

                createEvent(details, transferable);
                removeTableRow(transferable);
            }

            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

        });

        return calendar;
    }

    private void populateTable(Table table) {
        table.addContainerProperty("type", String.class, null);
        table.addContainerProperty("participants", String[].class, null);

        table.addGeneratedColumn("participants", new ColumnGenerator() {

            public Component generateCell(Table source, Object itemId,
                    Object columnId) {
                String[] participantList = (String[]) source.getItem(itemId)
                        .getItemProperty(columnId).getValue();

                Label participantsLabel = new Label(
                        getParticipantString(participantList));

                return participantsLabel;
            }
        });

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            String eventType = eventTypes[random.nextInt(eventTypes.length)];
            String[] participantList = new String[5];
            for (int j = 0; j < participantList.length; j++) {
                participantList[j] = participants[random
                        .nextInt(participants.length)];
            }

            Item item = table.addItem(random.nextInt());
            item.getItemProperty("type").setValue(eventType);
            item.getItemProperty("participants").setValue(participantList);

        }

        table.setColumnHeaders(new String[] { "Event type", "Participants" });
    }

    private String getParticipantString(String[] participantList) {
        StringBuilder sb = new StringBuilder();
        for (String participant : participantList) {
            sb.append(participant);
            sb.append(", ");
        }

        return sb.substring(0, sb.length() - 2);
    }

    protected void createEvent(CalendarTargetDetails details,
            TableTransferable transferable) {
        Date dropTime = details.getDropTime();
        java.util.Calendar timeCalendar = details.getTargetCalendar()
                .getInternalCalendar();
        timeCalendar.setTime(dropTime);
        timeCalendar.add(java.util.Calendar.MINUTE, 120);
        Date endTime = timeCalendar.getTime();

        Item draggedItem = transferable.getSourceComponent().getItem(
                transferable.getItemId());

        String eventType = (String) draggedItem.getItemProperty("type")
                .getValue();

        String eventDescription = "Attending: "
                + getParticipantString((String[]) draggedItem.getItemProperty(
                        "participants").getValue());

        BasicEvent newEvent = new BasicEvent();
        newEvent.setAllDay(!details.hasDropTime());
        newEvent.setCaption(eventType);
        newEvent.setDescription(eventDescription);
        newEvent.setStart(dropTime);
        newEvent.setEnd(endTime);

        BasicEventProvider ep = (BasicEventProvider) details
                .getTargetCalendar().getEventProvider();
        ep.addEvent(newEvent);
    }

    protected void removeTableRow(TableTransferable transferable) {
        transferable.getSourceComponent().removeItem(transferable.getItemId());
    }
}
