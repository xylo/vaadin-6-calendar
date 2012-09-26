/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule.dd;

import com.vaadin.addon.calendar.gwt.client.ui.CalendarConnector;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.dd.VAbstractDropHandler;

/**
 * Abstract base class for calendar drop handlers.
 * 
 */
public abstract class CalendarDropHandler extends VAbstractDropHandler {

    protected CalendarConnector calendarPaintable;

    /**
     * Set the calendar instance
     * 
     * @param calendarPaintable
     */
    public void setCalendarPaintable(CalendarConnector calendarPaintable) {
        this.calendarPaintable = calendarPaintable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#getConnector()
     */
    @Override
    public ComponentConnector getConnector() {
        return calendarPaintable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VDropHandler#getApplicationConnection
     * ()
     */
    public ApplicationConnection getApplicationConnection() {
        return calendarPaintable.getClient();
    }

}
