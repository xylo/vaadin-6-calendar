/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.ui.handler;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardHandler;

/**
 * Implements basic functionality needed to enable forward navigation.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 */
@SuppressWarnings("serial")
public class BasicForwardHandler implements ForwardHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardHandler#forward
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardEvent)
     */
    public void forward(ForwardEvent event) {
        Date start = event.getComponent().getStartDate();
        Date end = event.getComponent().getEndDate();

        // calculate amount to move forward
        int durationInDays = (int) (((end.getTime()) - start.getTime()) / VCalendar.DAYINMILLIS);
        durationInDays++;

        // set new start and end times
        Calendar javaCalendar = Calendar.getInstance();
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
    protected void setDates(ForwardEvent event, Date start, Date end) {
        event.getComponent().setStartDate(start);
        event.getComponent().setEndDate(end);
    }
}
