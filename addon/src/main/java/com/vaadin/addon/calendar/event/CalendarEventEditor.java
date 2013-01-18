/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.event;

import java.util.Date;

/**
 * <p>
 * Extension to the basic {@link com.vaadin.addon.calendar.event.CalendarEvent
 * CalendarEvent}. This interface provides setters (and thus editing
 * capabilities) for all {@link com.vaadin.addon.calendar.event.CalendarEvent
 * CalendarEvent} fields. For descriptions on the fields, refer to the extended
 * interface.
 * </p>
 * 
 * <p>
 * This interface is used by some of the basic Calendar event handlers in the
 * <code>com.vaadin.addon.calendar.ui.handler</code> package to determine
 * whether an event can be edited.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 */
public interface CalendarEventEditor extends CalendarEvent {

    /**
     * Set the visible text in the calendar for the event.
     * 
     * @param caption
     *            The text to show in the calendar
     */
    void setCaption(String caption);

    /**
     * Set the description of the event. This is shown in the calendar when
     * hoovering over the event.
     * 
     * @param description
     *            The text which describes the event
     */
    void setDescription(String description);

    /**
     * Set the end date of the event. Must be after the start date.
     * 
     * @param end
     *            The end date to set
     */
    void setEnd(Date end);

    /**
     * Set the start date for the event. Must be before the end date
     * 
     * @param start
     *            The start date of the event
     */
    void setStart(Date start);

    /**
     * Set the style name for the event used for styling the event cells
     * 
     * @param styleName
     *            The stylename to use
     * 
     */
    void setStyleName(String styleName);

    /**
     * Does the event span the whole day. If so then set this to true
     * 
     * @param isAllDay
     *            True if the event spans the whole day. In this case the start
     *            and end times are ignored.
     */
    void setAllDay(boolean isAllDay);

}
