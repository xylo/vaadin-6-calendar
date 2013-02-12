/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.addon.calendar.ui.handler;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventEditor;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler;

import java.util.Date;

/**
 * Implements basic functionality needed to enable event resizing.
 * 
 * @author Vaadin Ltd.
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
