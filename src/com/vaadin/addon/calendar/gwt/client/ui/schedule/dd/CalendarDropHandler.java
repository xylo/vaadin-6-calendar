/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule.dd;

import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler;

/**
 * Abstract base class for calendar drop handlers.
 * 
 */
public abstract class CalendarDropHandler extends VAbstractDropHandler {

    protected VCalendar calendarPaintable;

    public void setCalendarPaintable(VCalendar calendarPaintable) {
        this.calendarPaintable = calendarPaintable;
    }

    @Override
    public Paintable getPaintable() {
        return calendarPaintable;
    }

    public ApplicationConnection getApplicationConnection() {
        return calendarPaintable.getClient();
    }

}
