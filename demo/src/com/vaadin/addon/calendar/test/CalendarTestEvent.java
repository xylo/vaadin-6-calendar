package com.vaadin.addon.calendar.test;

import java.util.Date;

import com.vaadin.addon.calendar.ui.Calendar;

/**
 * Test Calendar.Event implementation.
 * 
 * @see com.vaadin.addon.calendar.ui.Calendar.Event
 */
public class CalendarTestEvent implements Calendar.Event {

    private Date start;
    private Date end;
    private String caption;
    private String where;
    private String description;
    private Object data;
    private String styleName;

    public CalendarTestEvent(String caption, Date start, Date end) {
        this.caption = caption;
        this.start = start;
        this.end = end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.ui.Calendar.Event#getStart()
     */
    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.ui.Calendar.Event#getEnd()
     */
    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.ui.Calendar.Event#getCaption()
     */
    public String getCaption() {
        return caption;
    }

    public void setCaption(String what) {
        caption = what;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.ui.Calendar.Event#getDescription()
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.ui.Calendar.Event#getStyleName()
     */
    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }
}
