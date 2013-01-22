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
import java.util.GregorianCalendar;

import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickHandler;

/**
 * Implements basic functionality needed to change to week view when a week
 * number is clicked.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 */
@SuppressWarnings("serial")
public class BasicWeekClickHandler implements WeekClickHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickHandler
     * #weekClick
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick)
     */
    public void weekClick(WeekClick event) {
        int week = event.getWeek();
        int year = event.getYear();

        // set correct year and month
        Calendar javaCalendar = event.getComponent().getInternalCalendar();
        javaCalendar.set(GregorianCalendar.YEAR, year);
        javaCalendar.set(GregorianCalendar.WEEK_OF_YEAR, week);

        // starting at the beginning of the week
        javaCalendar.set(GregorianCalendar.DAY_OF_WEEK,
                javaCalendar.getFirstDayOfWeek());
        Date start = javaCalendar.getTime();

        // ending at the end of the week
        javaCalendar.add(GregorianCalendar.DATE, 6);
        Date end = javaCalendar.getTime();

        setDates(event, start, end);

        // times are automatically expanded, no need to worry about them
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
    protected void setDates(WeekClick event, Date start, Date end) {
        event.getComponent().setStartDate(start);
        event.getComponent().setEndDate(end);
    }

}
