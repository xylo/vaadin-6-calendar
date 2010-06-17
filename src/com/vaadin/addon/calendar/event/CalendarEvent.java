package com.vaadin.addon.calendar.event;

import java.io.Serializable;
import java.util.Date;

/**
 * Event in the calendar. Customize your own event by implementing this
 * interface.<br/>
 * <li>Start, end and caption fields are mandatory. <li>In "allDay" events
 * longer than one day, starting and ending clock times are omitted in UI and
 * only dates are shown.<li>An event with a same start and end date with zero
 * length time range will be considered as a single "allDay" event.
 */
public interface CalendarEvent extends Serializable {

    /**
     * Gets start date of event.
     * 
     * @return Start date.
     */
    public Date getStart();

    /**
     * Get end date of event.
     * 
     * @return End date;
     */
    public Date getEnd();

    /**
     * Gets caption of event.
     * 
     * @return Caption text
     */
    public String getCaption();

    /**
     * Gets description of event.
     * 
     * @return Description text.
     */
    public String getDescription();

    /**
     * Gets style name of event. In the client, style name will be set to the
     * event's element class name and can be styled by CSS.</br></br>Styling
     * example:</br> <code>Java code: </br>
     * event.setStyleName("color1");</br></br>CSS:</br>.v-calendar-event-color1 {</br>
     * &nbsp;&nbsp;&nbsp;background-color: #9effae;</br>}</code>
     * 
     * @return Style name.
     */
    public String getStyleName();

    /**
     * An all-day event typically does not occur at a specific time but targets
     * a whole day or days. The rendering of all-day events differs from normal
     * events.
     * 
     * @return true if this event is an all-day event, false otherwise
     */
    public boolean isAllDay();

    /**
     * Event to signal that an event has changed.
     */
    public class EventChange implements Serializable {

        private static final long serialVersionUID = 6847631666440007162L;

        private CalendarEvent source;

        public EventChange(CalendarEvent source) {
            this.source = source;
        }

        /**
         * @return the {@link com.vaadin.addon.calendar.event.CalendarEvent
         *         CalendarEvent} that has changed
         */
        public CalendarEvent getCalendarEvent() {
            return source;
        }
    }

    /**
     * Listener for EventSetChange events.
     */
    public interface EventChangeListener extends Serializable {

        /**
         * Called when an Event has changed.
         */
        public void eventChange(EventChange changeEvent);
    }

    /**
     * Notifier interface for EventChange events.
     */
    public interface EventChangeNotifier extends Serializable {

        void addListener(EventChangeListener listener);

        void removeListener(EventChangeListener listener);
    }

}
