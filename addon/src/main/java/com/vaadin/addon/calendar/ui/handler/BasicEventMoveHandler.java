/**
 * Copyright (C) 2010 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.addon.calendar.ui.handler;

import java.util.Date;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventEditor;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;

/**
 * Implements basic functionality needed to enable moving events.
 * 
 * @author Vaadin Ltd.
 * @version ${pom.version}
 * 
 */
@SuppressWarnings("serial")
public class BasicEventMoveHandler implements EventMoveHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler
     * #eventMove
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent)
     */
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
