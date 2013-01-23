/**
 * Copyright (C) 2010 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
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
 * ${pom.version}
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent#isAllDay()
     */
    public boolean isAllDay() {
        return isAllDay;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setCaption(java.lang
     * .String)
     */
    public void setCaption(String caption) {
        this.caption = caption;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setDescription(java
     * .lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setEnd(java.util.
     * Date)
     */
    public void setEnd(Date end) {
        this.end = end;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setStart(java.util
     * .Date)
     */
    public void setStart(Date start) {
        this.start = start;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setStyleName(java
     * .lang.String)
     */
    public void setStyleName(String styleName) {
        this.styleName = styleName;
        fireEventChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventEditor#setAllDay(boolean)
     */
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

    /**
     * Fires an event change event to the listeners. Should be triggered when
     * some property of the event changes.
     */
    protected void fireEventChange() {
        EventChange event = new EventChange(this);

        for (EventChangeListener listener : listeners) {
            listener.eventChange(event);
        }
    }
}
