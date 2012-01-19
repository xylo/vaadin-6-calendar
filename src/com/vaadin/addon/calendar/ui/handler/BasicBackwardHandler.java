/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.ui.handler;

import java.util.Calendar;
import java.util.Date;

import com.vaadin.addon.calendar.gwt.client.ui.GWTCalendar;
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
public class BasicBackwardHandler implements BackwardHandler {

    private static final long serialVersionUID = 7706994719728595151L;

    public void backward(BackwardEvent event) {
        Date start = event.getComponent().getStartDate();
        Date end = event.getComponent().getEndDate();

        // calculate amount to move back
        int durationInDays = (int) (((end.getTime()) - start.getTime()) / GWTCalendar.DAYINMILLIS);
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

    protected void setDates(BackwardEvent event, Date start, Date end) {
        event.getComponent().setStartDate(start);
        event.getComponent().setEndDate(end);
    }
}
