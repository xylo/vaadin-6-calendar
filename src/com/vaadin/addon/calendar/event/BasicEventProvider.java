/**
 * 
 */
package com.vaadin.addon.calendar.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.addon.calendar.event.CalendarEvent.EventChange;
import com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier;

/**
 * <p>
 * Simple implementation of
 * {@link com.vaadin.addon.calendar.event.CalendarEventProvider
 * CalendarEventProvider}. Use {@link #addEvent(BasicEvent)} and
 * {@link #removeEvent(BasicEvent)} to add / remove events.
 * </p>
 * 
 * <p>
 * {@link com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeNotifier
 * EventSetChangeNotifier} and
 * {@link com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeNotifier
 * EventChangeListener} are also implemented, so the Calendar is notified when
 * an event is added, changed or removed.
 * </p>
 * 
 */
public class BasicEventProvider implements CalendarEventProvider,
        EventSetChangeNotifier, CalendarEvent.EventChangeListener {

    private static final long serialVersionUID = 630145351104741918L;

    protected List<CalendarEvent> eventList = new ArrayList<CalendarEvent>();

    private List<EventSetChangeListener> listeners = new ArrayList<EventSetChangeListener>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider#getEvents(java.
     * util.Date, java.util.Date)
     */
    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        ArrayList<CalendarEvent> activeEvents = new ArrayList<CalendarEvent>();

        for (CalendarEvent ev : eventList) {
            long from = startDate.getTime();
            long to = endDate.getTime();

            long f = ev.getStart().getTime();
            long t = ev.getEnd().getTime();
            // Select only events that overlaps with startDate and
            // endDate.
            if ((f <= to && f >= from) || (t >= from && t <= to)
                    || (f <= from && t >= to)) {
                activeEvents.add(ev);
            }
        }

        return activeEvents;
    }

    public void addEvent(BasicEvent event) {
        eventList.add(event);

        event.addListener(this);

        fireEventSetChange();
    }

    public void removeEvent(BasicEvent event) {
        eventList.remove(event);

        event.removeListener(this);

        fireEventSetChange();
    }

    public boolean containsEvent(BasicEvent event) {
        return eventList.contains(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeListener
     * )
     */
    public void addListener(EventSetChangeListener listener) {
        listeners.add(listener);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeNotifier
     * #removeListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChangeListener
     * )
     */
    public void removeListener(EventSetChangeListener listener) {
        listeners.remove(listener);
    }

    protected void fireEventSetChange() {
        EventSetChange event = new EventSetChange(this);

        for (EventSetChangeListener listener : listeners) {
            listener.eventSetChange(event);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeListener
     * #eventChange
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventSetChange)
     */
    public void eventChange(EventChange changeEvent) {
        // naive implementation
        fireEventSetChange();
    }
}
