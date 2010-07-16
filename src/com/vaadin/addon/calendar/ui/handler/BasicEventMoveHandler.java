package com.vaadin.addon.calendar.ui.handler;

import java.util.Date;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventEditor;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;

/**
 * Implements basic functionality needed to enable moving events.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * 
 */
public class BasicEventMoveHandler implements EventMoveHandler {

    private static final long serialVersionUID = -2311929051549036879L;

    public void eventMove(MoveEvent event) {
        CalendarEvent calendarEvent = event.getCalendarEvent();

        if (calendarEvent instanceof CalendarEventEditor) {

            CalendarEventEditor editableEvent = (CalendarEventEditor) calendarEvent;

            Date newFromTime = event.getNewStart();

            // Update event dates
            long length = editableEvent.getEnd().getTime()
                    - editableEvent.getStart().getTime();
            setDates(editableEvent, newFromTime, new Date(newFromTime.getTime()
                    + length));
        }
    }

    protected void setDates(CalendarEventEditor event, Date start, Date end) {
        event.setStart(start);
        event.setEnd(end);
    }
}
