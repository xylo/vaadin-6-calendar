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

import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickHandler;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Implements basic functionality needed to change to week view when a week
 * number is clicked.
 * 
 * @author Vaadin Ltd.
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
