/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.addon.calendar.event.CalendarEvent.EventChangeNotifier;

/**
 * Simple implementation of
 * {@link com.vaadin.addon.calendar.event.CalendarEvent CalendarEvent}. Has
 * setters for all required fields and fires events when this event is changed.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 */
@SuppressWarnings("serial")
public class BasicEvent implements CalendarEventEditor, EventChangeNotifier {

    private String caption;
    private String description;
    private Date end;
    private Date start;
    private String styleName;
    private transient List<EventChangeListener> listeners = new ArrayList<EventChangeListener>();

    private boolean isAllDay;

    /**
     * Default constructor
     */
    public BasicEvent() {

    }

    /**
     * Constructor for creating an event with the same start and end date
     * 
     * @param caption
     *            The caption for the event
     * @param description
     *            The description for the event
     * @param date
     *            The date the event occurred
     */
    public BasicEvent(String caption, String description, Date date) {
        this.caption = caption;
        this.description = description;
        this.start = date;
        this.end = date;
    }

    /**
     * Constructor for creating an event with a start date and an end date.
     * Start date should be before the end date
     * 
     * @param caption
     *            The caption for the event
     * @param description
     *            The description for the event
     * @param startDate
     *            The start date of the event
     * @param endDate
     *            The end date of the event
     */
    public BasicEvent(String caption, String description, Date startDate,
            Date endDate) {
        this.caption = caption;
        this.description = description;
        this.start = startDate;
        this.end = endDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getCaption()
     */
    public String getCaption() {
        return caption;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getEnd()
     */
    public Date getEnd() {
        return end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStart()
     */
    public Date getStart() {
        return start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStyleName()
     */
    public String getStyleName() {
        return styleName;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    // setters for properties

    public void setCaption(String caption) {
        this.caption = caption;
        fireEventChange();
    }

    public void setDescription(String description) {
        this.description = description;
        fireEventChange();
    }

    public void setEnd(Date end) {
        this.end = end;
        fireEventChange();
    }

    public void setStart(Date start) {
        this.start = start;
        fireEventChange();
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
        fireEventChange();
    }

    public void setAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeListener
     * )
     */
    public void addListener(EventChangeListener listener) {
        listeners.add(listener);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeNotifier
     * #removeListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeListener
     * )
     */
    public void removeListener(EventChangeListener listener) {
        listeners.remove(listener);
    }

    protected void fireEventChange() {
        EventChange event = new EventChange(this);

        for (EventChangeListener listener : listeners) {
            listener.eventChange(event);
        }
    }
}
