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
public class BasicEventResizeHandler implements EventResizeHandler {

    private static final long serialVersionUID = -6498174671188624888L;

    public void eventResize(EventResize event) {
        CalendarEvent calendarEvent = event.getCalendarEvent();

        if (calendarEvent instanceof CalendarEventEditor) {
            Date newStartTime = event.getNewStartTime();
            Date newEndTime = event.getNewEndTime();

            CalendarEventEditor editableEvent = (CalendarEventEditor) calendarEvent;

            setDates(editableEvent, newStartTime, newEndTime);
        }
    }

    protected void setDates(CalendarEventEditor event, Date start, Date end) {
        event.setStart(start);
        event.setEnd(end);
    }
}
