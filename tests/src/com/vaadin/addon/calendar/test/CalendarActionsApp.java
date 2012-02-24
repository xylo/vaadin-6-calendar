package com.vaadin.addon.calendar.test;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarDateRange;
import com.vaadin.event.Action;
import com.vaadin.ui.VerticalLayout;

public class CalendarActionsApp extends VerticalLayout {

    /**
     * Construct
     */
    public CalendarActionsApp() {
        setSizeFull();

        // Create the calendar
        Calendar calendar = new Calendar("My Contextual Calendar");
        calendar.setWidth("600px"); // Undefined by default
        calendar.setHeight("300px"); // Undefined by default

        // Use US English for date/time representation
        calendar.setLocale(new Locale("en", "US"));

        // Add an event from now to plus one hour
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        end.add(java.util.Calendar.HOUR, 1);
        calendar.addEvent(new BasicEvent("Calendar study",
                "Learning how to use Vaadin Calendar", start.getTime(), end
                .getTime()));

        Action.Handler actionHandler = new Action.Handler() {
            Action addEventAction = new Action("Add Event");
            Action deleteEventAction = new Action("Delete Event");

            public Action[] getActions(Object target, Object sender) {
                System.out.println("getActions()");
                if (!(target instanceof CalendarDateRange)) {
                    System.out.println("Target is a "
                            + target.getClass().getName());
                    return null;
                }
                CalendarDateRange dateRange = (CalendarDateRange) target;

                if (!(sender instanceof Calendar)) {
                    System.out.println("Sender is a "
                            + sender.getClass().getName());
                    return null;
                }
                Calendar calendar = (Calendar) sender;

                // List all the events on the requested day
                List<CalendarEvent> events = calendar.getEvents(
                        dateRange.getStart(), dateRange.getEnd());

                System.out.println("Returning two actions");
                return new Action[] { addEventAction, deleteEventAction };
            }

            public void handleAction(Action action, Object sender, Object target) {
                if (target instanceof CalendarEvent) {
                    CalendarEvent e = (CalendarEvent) target;
                    System.out.println(e.getCaption());
                }
                System.out.println("handeAction()");
            }
        };
        calendar.addActionHandler(actionHandler);

        addComponent(calendar);

    }

}
