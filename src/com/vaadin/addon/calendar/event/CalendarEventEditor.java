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
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 */
public interface CalendarEventEditor extends CalendarEvent {

    void setCaption(String caption);

    void setDescription(String description);

    void setEnd(Date end);

    void setStart(Date start);

    void setStyleName(String styleName);

    void setAllDay(boolean isAllDay);

}
