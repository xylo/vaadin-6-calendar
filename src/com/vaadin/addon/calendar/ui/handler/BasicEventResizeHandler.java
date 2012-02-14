/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.ui.handler;

import java.util.Date;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventEditor;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler;

/**
 * Implements basic functionality needed to enable event resizing.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 */
@SuppressWarnings("serial")
public class BasicEventResizeHandler implements EventResizeHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler
     * #eventResize
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize)
     */
    public void eventResize(EventResize event) {
        CalendarEvent calendarEvent = event.getCalendarEvent();

        if (calendarEvent instanceof CalendarEventEditor) {
            Date newStartTime = event.getNewStartTime();
            Date newEndTime = event.getNewEndTime();

            CalendarEventEditor editableEvent = (CalendarEventEditor) calendarEvent;

            setDates(editableEvent, newStartTime, newEndTime);
        }
    }

    /**
     * Set the start and end dates for the event
     * 
     * @param event
     *            The event that the start and end dates should be set
     * @param start
     *            The start date
     * @param end
     *            The end date
     */
    protected void setDates(CalendarEventEditor event, Date start, Date end) {
        event.setStart(start);
        event.setEnd(end);
    }
}
