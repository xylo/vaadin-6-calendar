/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.event;

/**
 * An event provider which allows adding and removing events
 * 
 * @since 1.3.0
 * @version
 * @VERSION@
 */
public interface CalendarEditableEventProvider extends CalendarEventProvider {

    /**
     * Adds an event to the event provider
     * 
     * @param event
     *            The event to add
     */
    void addEvent(CalendarEvent event);

    /**
     * Removes an event from the event provider
     * 
     * @param event
     *            The event
     */
    void removeEvent(CalendarEvent event);
}
