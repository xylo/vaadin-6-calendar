package com.vaadin.addon.calendar.event;

import java.util.Date;
import java.util.List;

/**
 * Interface for querying events. Calendar component must have CalendarEventProvider
 * implementation. This interface may be dropped in future versions. In future
 * calendar may require DateContainer or some similar container as a data
 * source.
 */
public interface CalendarEventProvider {
    /**
     * Gets all available events in the target date range between startDate and
     * endDate.
     * 
     * @param startDate
     *            Start date
     * @param endDate
     *            End date
     * @return List of events
     */
    public List<CalendarEvent> getEvents(Date startDate, Date endDate);
}
