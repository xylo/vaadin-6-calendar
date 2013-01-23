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

import java.util.Calendar;
import java.util.Date;

import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickHandler;

/**
 * Implements basic functionality needed to switch to day view when a single day
 * is clicked.
 * 
 * @author Vaadin Ltd.
 * @version
 * ${pom.version}
 */
@SuppressWarnings("serial")
public class BasicDateClickHandler implements DateClickHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickHandler
     * #dateClick
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent)
     */
    public void dateClick(DateClickEvent event) {
        Date clickedDate = event.getDate();

        Calendar javaCalendar = event.getComponent().getInternalCalendar();
        javaCalendar.setTime(clickedDate);

        // as times are expanded, this is all that is needed to show one day
        Date start = javaCalendar.getTime();
        Date end = javaCalendar.getTime();

        setDates(event, start, end);
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
    protected void setDates(DateClickEvent event, Date start, Date end) {
        event.getComponent().setStartDate(start);
        event.getComponent().setEndDate(end);
    }
}
