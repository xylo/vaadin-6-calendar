package com.vaadin.addon.calendar.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class NotificationTestUI extends UI {

    private DummyEventProvider provider;

    private static class DummyEventProvider implements CalendarEventProvider {

        private int index;
        private List<CalendarEvent> events = new ArrayList<CalendarEvent>();

        public void addEvent(Date date) {
            BasicEvent e = new BasicEvent();
            e.setAllDay(true);
            e.setStart(date);
            e.setEnd(date);
            e.setCaption("Some event " + ++index);
            events.add(e);
        }

        public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
            return events;
        }

    }

    @Override
    protected void init(com.vaadin.server.VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        setContent(content);
        final Button btn = new Button("Show working notification",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Notification
                                .show("This will disappear when you move your mouse!");
                    }
                });
        addComponent(btn);

        provider = new DummyEventProvider();
        final Calendar cal = new Calendar(provider);
        cal.setSizeFull();
        cal.setHandler(new DateClickHandler() {
            public void dateClick(DateClickEvent event) {
                provider.addEvent(event.getDate());
                Notification
                        .show("This should disappear, but if wont unless clicked.");

                // this requestRepaint call interferes with the notification
                cal.markAsDirty();
            }
        });
        addComponent(cal);

        java.util.Calendar javaCal = java.util.Calendar.getInstance();
        javaCal.set(java.util.Calendar.MONTH, 0);
        javaCal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        Date start = javaCal.getTime();
        javaCal.set(java.util.Calendar.DAY_OF_MONTH, 31);
        Date end = javaCal.getTime();

        cal.setStartDate(start);
        cal.setEndDate(end);
    }

}
