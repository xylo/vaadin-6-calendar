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
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 */
public class BasicWeekClickHandler implements WeekClickHandler {

    private static final long serialVersionUID = 3038767897246095156L;

    public void weekClick(WeekClick event) {
        int week = event.getWeek();
        int year = event.getYear();

        // set correct year and month
        Calendar javaCalendar = event.getComponent().getInternalCalendar();
        javaCalendar.set(GregorianCalendar.YEAR, year);
        javaCalendar.set(GregorianCalendar.WEEK_OF_YEAR, week);

        // starting at the beginning of the week
        javaCalendar.set(GregorianCalendar.DAY_OF_WEEK, javaCalendar
                .getFirstDayOfWeek());
        Date start = javaCalendar.getTime();

        // ending at the end of the week
        javaCalendar.add(GregorianCalendar.DATE, 6);
        Date end = javaCalendar.getTime();

        setDates(event, start, end);

        // times are automatically expanded, no need to worry about them
    }

    protected void setDates(WeekClick event, Date start, Date end) {
        event.getComponent().setStartDate(start);
        event.getComponent().setEndDate(end);
    }

}
