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

import com.vaadin.addon.calendar.event.CalendarEvent.EventChange;
import com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier;

/**
 * <p>
 * Simple implementation of
 * {@link com.vaadin.addon.calendar.event.CalendarEventProvider
 * CalendarEventProvider}. Use {@link #addEvent(CalendarEvent)} and
 * {@link #removeEvent(CalendarEvent)} to add / remove events.
 * </p>
 * 
 * <p>
 * {@link com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier
 * EventSetChangeNotifier} and
 * {@link com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener
 * EventChangeListener} are also implemented, so the Calendar is notified when
 * an event is added, changed or removed.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @version
 * ${pom.version}
 */
@SuppressWarnings("serial")
public class BasicEventProvider implements CalendarEditableEventProvider,
EventSetChangeNotifier, CalendarEvent.EventChangeListener {

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

    /**
     * Does this event provider container this event
     * 
     * @param event
     *            The event to check for
     * @return If this provider has the event then true is returned, else false
     */
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

    /**
     * Fires a eventsetchange event. The event is fired when either an event is
     * added or removed to the event provider
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEditableEventProvider#addEvent
     * (com.vaadin.addon.calendar.event.CalendarEvent)
     */
    public void addEvent(CalendarEvent event) {
        eventList.add(event);
        if (event instanceof BasicEvent) {
            ((BasicEvent) event).addListener(this);
        }
        fireEventSetChange();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEditableEventProvider#removeEvent
     * (com.vaadin.addon.calendar.event.CalendarEvent)
     */
    public void removeEvent(CalendarEvent event) {
        eventList.remove(event);
        if (event instanceof BasicEvent) {
            ((BasicEvent) event).removeListener(this);
        }
        fireEventSetChange();
    }
}
