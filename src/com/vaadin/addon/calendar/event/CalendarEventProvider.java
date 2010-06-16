package com.vaadin.addon.calendar.event;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Interface for querying events. Calendar component must have
 * CalendarEventProvider implementation. This interface may be dropped in future
 * versions. In future calendar may require DateContainer or some similar
 * container as a data source.
 */
public interface CalendarEventProvider extends Serializable {
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

    /**
     * Event to signal that the set of events has changed and the calendar
     * should refresh its view from the
     * {@link com.vaadin.addon.calendar.event.CalendarEventProvider
     * CalendarEventProvider} .
     * 
     */
    public class EventSetChange implements Serializable {

        private static final long serialVersionUID = -8866008738947581269L;

        private CalendarEventProvider source;

        public EventSetChange(CalendarEventProvider source) {
            this.source = source;
        }

        /**
         * @return the
         *         {@link com.vaadin.addon.calendar.event.CalendarEventProvider
         *         CalendarEventProvider} that has changed
         */
        public CalendarEventProvider getProvider() {
            return source;
        }
    }

    /**
     * Listener for EventSetChange events.
     */
    public interface EventSetChangeListener extends Serializable {

        /**
         * Called when the set of Events has changed.
         */
        public void eventSetChange(EventSetChange changeEvent);
    }

    /**
     * Notifier interface for EventSetChange events.
     */
    public interface EventSetChangeNotifier extends Serializable {

        void addListener(EventSetChangeListener listener);

        void removeListener(EventSetChangeListener listener);
    }
}
