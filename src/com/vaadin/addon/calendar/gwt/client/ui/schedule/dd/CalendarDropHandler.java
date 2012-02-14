/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule.dd;

import com.vaadin.addon.calendar.gwt.client.ui.VCalendarPaintable;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler;

/**
 * Abstract base class for calendar drop handlers.
 * 
 */
public abstract class CalendarDropHandler extends VAbstractDropHandler {

    protected VCalendarPaintable calendarPaintable;

    /**
     * Set the calendar instance
     * 
     * @param calendarPaintable
     */
    public void setCalendarPaintable(VCalendarPaintable calendarPaintable) {
        this.calendarPaintable = calendarPaintable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#getPaintable()
     */
    @Override
    public Paintable getPaintable() {
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
