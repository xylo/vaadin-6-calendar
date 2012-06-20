package com.vaadin.addon.calendar.demo;

import com.vaadin.addon.calendar.event.BasicEvent;

/**
 * Test CalendarEvent implementation.
 * 
 * @see com.vaadin.addon.calendar.test.ui.Calendar.Event
 */
public class CalendarTestEvent extends BasicEvent {

    private static final long serialVersionUID = 2820133201983036866L;
    private String where;
    private Object data;

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
        fireEventChange();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
        fireEventChange();
    }
}
