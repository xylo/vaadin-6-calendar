package com.vaadin.addon.calendar.test;

import java.util.Date;
import java.util.GregorianCalendar;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.ui.VerticalLayout;

/**
 * A testing application to demonstrate the use of the JPAContainer with the
 * Calendar
 */
public class JPAContainerTestApp extends VerticalLayout {

    public static final String PERSISTANCE_UNIT = "jpa-container-test-persistance-unit";

    private JPAContainer<PersistentEvent> eventContainer;

    public JPAContainerTestApp() {
        setSizeFull();

        // Create the JPA Container
        eventContainer = JPAContainerFactory.make(PersistentEvent.class,
                PERSISTANCE_UNIT);

        // Ensure we have no events in database from previous sessions
        eventContainer.removeAllItems();

        // Create the calendar instance
        Calendar calendar = new Calendar();
        calendar.setSizeFull();

        // Attach the container to the calendar
        calendar.setContainerDataSource(eventContainer);

        // Add an event to the calendar
        calendar.addEvent(createEvent(new Date()));

        addComponent(calendar);
    }

    /**
     * Creates a new event at the desired date. The event is by default three
     * hours.
     * 
     * @param date
     * @return
     */
    private PersistentEvent createEvent(Date date) {
        java.util.Calendar cal = new GregorianCalendar();
        PersistentEvent event = new PersistentEvent();
        event.setStart(cal.getTime());
        cal.add(java.util.Calendar.HOUR, 3);
        event.setEnd(cal.getTime());
        event.setCaption("My event");
        event.setDescription("My event description");
        return event;
    }
}
