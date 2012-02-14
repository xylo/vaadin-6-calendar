/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.ui.handler;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardHandler;

/**
 * Implements basic functionality needed to enable backwards navigation.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * 
 */
@SuppressWarnings("serial")
public class BasicBackwardHandler implements BackwardHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardHandler#
     * backward
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardEvent)
     */
    public void backward(BackwardEvent event) {
        Date start = event.getComponent().getStartDate();
        Date end = event.getComponent().getEndDate();

        // calculate amount to move back
        int durationInDays = (int) (((end.getTime()) - start.getTime()) / VCalendar.DAYINMILLIS);
        durationInDays++;
        durationInDays = -durationInDays;

        // set new start and end times
        Calendar javaCalendar = event.getComponent().getInternalCalendar();
        javaCalendar.setTime(start);
        javaCalendar.add(java.util.Calendar.DATE, durationInDays);
        Date newStart = javaCalendar.getTime();

        javaCalendar.setTime(end);
        javaCalendar.add(java.util.Calendar.DATE, durationInDays);
        Date newEnd = javaCalendar.getTime();

        setDates(event, newStart, newEnd);
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
    protected void setDates(BackwardEvent event, Date start, Date end) {
        event.getComponent().setStartDate(start);
        event.getComponent().setEndDate(end);
    }
}
