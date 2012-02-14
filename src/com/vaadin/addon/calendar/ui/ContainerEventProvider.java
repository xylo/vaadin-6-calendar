/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.ui;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.CalendarEditableEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener;
import com.vaadin.addon.calendar.event.CalendarEvent.EventChangeNotifier;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeNotifier;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeNotifier;

/**
 * A event provider which uses a {@link Container} as a datasource. Container
 * used as data source.
 * 
 * NOTE: The data source must be sorted by date!
 * 
 * @since 1.3.0
 */
@SuppressWarnings("serial")
public class ContainerEventProvider implements CalendarEditableEventProvider,
EventSetChangeNotifier, EventChangeNotifier, EventMoveHandler,
EventResizeHandler,
Container.ItemSetChangeListener, Property.ValueChangeListener {

    // Default property ids
    public static final String CAPTION_PROPERTY = "caption";
    public static final String DESCRIPTION_PROPERTY = "description";
    public static final String STARTDATE_PROPERTY = "start";
    public static final String ENDDATE_PROPERTY = "end";
    public static final String STYLENAME_PROPERTY = "styleName";

    /**
     * Internal class to keep the container index which item this event
     * represents
     * 
     */
    private class ContainerCalendarEvent extends BasicEvent {
        private final int index;

        public ContainerCalendarEvent(int containerIndex) {
            super();
            index = containerIndex;
        }

        public int getContainerIndex() {
            return index;
        }
    }

    /**
     * Listeners attached to the container
     */
    private final List<EventSetChangeListener> eventSetChangeListeners = new LinkedList<CalendarEventProvider.EventSetChangeListener>();
    private final List<EventChangeListener> eventChangeListeners = new LinkedList<CalendarEvent.EventChangeListener>();

    /**
     * The event cache contains the events previously created by
     * {@link #getEvents(Date, Date)}
     */
    private final List<CalendarEvent> eventCache = new LinkedList<CalendarEvent>();

    /**
     * The container used as datasource
     */
    private Indexed container;

    /**
     * Container properties. Defaults based on using the {@link BasicEvent}
     * helper class.
     */
    private Object captionProperty = CAPTION_PROPERTY;
    private Object descriptionProperty = DESCRIPTION_PROPERTY;
    private Object startDateProperty = STARTDATE_PROPERTY;
    private Object endDateProperty = ENDDATE_PROPERTY;
    private Object styleNameProperty = STYLENAME_PROPERTY;

    /**
     * Constructor
     * 
     * @param container
     *            Container to use as a data source.
     */
    public ContainerEventProvider(Container.Indexed container) {
        this.container = container;
        listenToContainerEvents();
    }

    /**
     * Set the container data source
     * 
     * @param container
     *            The container to use as datasource
     * 
     */
    public void setContainerDataSource(Container.Indexed container) {
        // Detach the previous container
        detachContainerDataSource();

        this.container = container;
        listenToContainerEvents();
    }

    /**
     * Returns the container used as data source
     * 
     */
    public Container.Indexed getContainerDataSource() {
        return this.container;
    }

    /**
     * Attaches listeners to the container so container events can be processed
     */
    private void listenToContainerEvents() {
        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container).addListener(this);
        }
        if (container instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) container).addListener(this);
        }
    }

    /**
     * Removes listeners from the container so no events are processed
     */
    private void ignoreContainerEvents() {
        if (container instanceof ItemSetChangeNotifier) {
            ((ItemSetChangeNotifier) container).removeListener(this);
        }
        if (container instanceof ValueChangeNotifier) {
            ((ValueChangeNotifier) container).removeListener(this);
        }
    }

    /**
     * Converts an event in the container to an {@link CalendarEvent}
     * 
     * @param index
     *            The index of the item in the container to get the event for
     * @return
     */
    private CalendarEvent getEvent(int index){

        // Check the event cache first
        for (CalendarEvent e : eventCache) {
            if (e instanceof ContainerCalendarEvent
                    && ((ContainerCalendarEvent) e).getContainerIndex() == index) {
                return e;
            } else if (container.getIdByIndex(index) == e) {
                return e;
            }
        }

        final Object id = container.getIdByIndex(index);
        Item item = container.getItem(id);
        CalendarEvent event;
        if (id instanceof CalendarEvent) {
            /*
             * If we are using the BeanItemContainer or another container
             * which stores the objects as ids then just return the
             * instances
             */
            event = (CalendarEvent) id;

        } else {
            /*
             * Else we use the properties to create the event
             */
            BasicEvent basicEvent = new ContainerCalendarEvent(index);

            // Set values from property values
            if (captionProperty != null
                    && item.getItemPropertyIds().contains(captionProperty)) {
                basicEvent.setCaption(String.valueOf(item.getItemProperty(
                        captionProperty).getValue()));
            }
            if (descriptionProperty != null
                    && item.getItemPropertyIds().contains(
                            descriptionProperty)) {
                basicEvent.setDescription(String.valueOf(item
                        .getItemProperty(descriptionProperty).getValue()));
            }
            if (startDateProperty != null
                    && item.getItemPropertyIds()
                    .contains(startDateProperty)) {
                basicEvent.setStart((Date) item.getItemProperty(
                        startDateProperty).getValue());
            }
            if (endDateProperty != null
                    && item.getItemPropertyIds().contains(endDateProperty)) {
                basicEvent.setEnd((Date) item.getItemProperty(
                        endDateProperty).getValue());
            }
            if (styleNameProperty != null
                    && item.getItemPropertyIds()
                    .contains(styleNameProperty)) {
                basicEvent.setDescription(String.valueOf(item
                        .getItemProperty(descriptionProperty).getValue()));
            }
            event = basicEvent;
        }
        return event;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider#getEvents(java.
     * util.Date, java.util.Date)
     */
    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        eventCache.clear();

        int[] rangeIndexes = getFirstAndLastEventIndex(startDate, endDate);
        for (int i = rangeIndexes[0]; i <= rangeIndexes[1]
                && i < container.size(); i++) {
            eventCache.add(getEvent(i));
        }
        return Collections.unmodifiableList(eventCache);
    }

    /**
     * Get the first event for a date
     * 
     * @param date
     *            The date to search for, NUll returns first event in container
     * @return Returns an array where the first item is the start index and the
     *         second item is the end item
     */
    private int[] getFirstAndLastEventIndex(Date start, Date end) {
        int startIndex = 0;
        int size = container.size();
        int endIndex = size - 1;

        if (start != null) {
            /*
             * Iterating from the start of the container, if range is in the end
             * of the container then this will be slow TODO This could be
             * improved by using some sort of divide and conquer algorithm
             */
            while (startIndex < size) {
                Object id = container.getIdByIndex(startIndex);
                Item item = container.getItem(id);
                Date d = (Date) item.getItemProperty(startDateProperty)
                        .getValue();
                if (d.compareTo(start) >= 0) {
                    break;
                }
                startIndex++;
            }
        }

        if (end != null) {
            /*
             * Iterate from the start index until range ends
             */
            endIndex = startIndex;
            while (endIndex < size - 1) {
                Object id = container.getIdByIndex(endIndex);
                Item item = container.getItem(id);
                Date d = (Date) item.getItemProperty(endDateProperty)
                        .getValue();
                if (d == null) {
                    // No end date present, use start date
                    d = (Date) item.getItemProperty(startDateProperty)
                            .getValue();
                }
                if (d.compareTo(end) >= 0) {
                    endIndex--;
                    break;
                }
                endIndex++;
            }
        }

        return new int[] { startIndex, endIndex };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier
     * #addListener(com.vaadin.addon.calendar.event.CalendarEventProvider.
     * EventSetChangeListener)
     */
    public void addListener(EventSetChangeListener listener) {
        if (!eventSetChangeListeners.contains(listener)) {
            eventSetChangeListeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier
     * #removeListener(com.vaadin.addon.calendar.event.CalendarEventProvider.
     * EventSetChangeListener)
     */
    public void removeListener(EventSetChangeListener listener) {
        eventSetChangeListeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEvent.EventChangeNotifier#addListener
     * (com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener)
     */
    public void addListener(EventChangeListener listener) {
        if (eventChangeListeners.contains(listener)) {
            eventChangeListeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.calendar.event.CalendarEvent.EventChangeNotifier#
     * removeListener
     * (com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener)
     */
    public void removeListener(EventChangeListener listener) {
        eventChangeListeners.remove(listener);
    }

    /**
     * Get the property which provides the caption of the event
     */
    public Object getCaptionProperty() {
        return captionProperty;
    }

    /**
     * Set the property which provides the caption of the event
     */
    public void setCaptionProperty(Object captionProperty) {
        this.captionProperty = captionProperty;
    }

    /**
     * Get the property which provides the description of the event
     */
    public Object getDescriptionProperty() {
        return descriptionProperty;
    }

    /**
     * Set the property which provides the description of the event
     */
    public void setDescriptionProperty(Object descriptionProperty) {
        this.descriptionProperty = descriptionProperty;
    }

    /**
     * Get the property which provides the starting date and time of the event
     */
    public Object getStartDateProperty() {
        return startDateProperty;
    }

    /**
     * Set the property which provides the starting date and time of the event
     */
    public void setStartDateProperty(Object startDateProperty) {
        this.startDateProperty = startDateProperty;
    }

    /**
     * Get the property which provides the ending date and time of the event
     */
    public Object getEndDateProperty() {
        return endDateProperty;
    }

    /**
     * Set the property which provides the ending date and time of the event
     */
    public void setEndDateProperty(Object endDateProperty) {
        this.endDateProperty = endDateProperty;
    }

    /**
     * Get the property which provides the style name for the event
     */
    public Object getStyleNameProperty() {
        return styleNameProperty;
    }

    /**
     * Set the property which provides the style name for the event
     */
    public void setStyleNameProperty(Object styleNameProperty) {
        this.styleNameProperty = styleNameProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Container.ItemSetChangeListener#containerItemSetChange
     * (com.vaadin.data.Container.ItemSetChangeEvent)
     */
    public void containerItemSetChange(ItemSetChangeEvent event) {
        if (event.getContainer() == container) {
            // Trigger an eventset change event when the itemset changes
            for (EventSetChangeListener listener : eventSetChangeListeners) {
                listener.eventSetChange(new EventSetChange(this));
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data
     * .Property.ValueChangeEvent)
     */
    public void valueChange(ValueChangeEvent event) {
        /*
         * TODO Need to figure out how to get the item which triggered the the
         * valuechange event and then trigger a EventChange event to the
         * listeners
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler
     * #eventMove
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent)
     */
    public void eventMove(MoveEvent event) {
        CalendarEvent ce = event.getCalendarEvent();
        if (eventCache.contains(ce)) {
            int index;
            if (ce instanceof ContainerCalendarEvent) {
                index = ((ContainerCalendarEvent) ce).getContainerIndex();
            } else {
                index = container.indexOfId(ce);
            }

            long eventLength = ce.getEnd().getTime() - ce.getStart().getTime();
            Date newEnd = new Date(event.getNewStart().getTime() + eventLength);

            ignoreContainerEvents();
            Item item = container.getItem(container.getIdByIndex(index));
            item.getItemProperty(startDateProperty).setValue(
                    event.getNewStart());
            item.getItemProperty(endDateProperty).setValue(newEnd);
            listenToContainerEvents();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler
     * #eventResize
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize)
     */
    public void eventResize(EventResize event) {
        CalendarEvent ce = event.getCalendarEvent();
        if (eventCache.contains(ce)) {
            int index;
            if (ce instanceof ContainerCalendarEvent) {
                index = ((ContainerCalendarEvent) ce).getContainerIndex();
            } else {
                index = container.indexOfId(ce);
            }
            ignoreContainerEvents();
            Item item = container.getItem(container.getIdByIndex(index));
            item.getItemProperty(startDateProperty).setValue(
                    event.getNewStartTime());
            item.getItemProperty(endDateProperty).setValue(
                    event.getNewEndTime());
            listenToContainerEvents();
        }
    }

    /**
     * If you are reusing the container which previously have been attached to
     * this ContainerEventProvider call this method to remove this event
     * providers container listeners before attaching it to an other
     * ContainerEventProvider
     */
    public void detachContainerDataSource() {
        ignoreContainerEvents();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEditableEventProvider#addEvent
     * (com.vaadin.addon.calendar.event.CalendarEvent)
     */
    public void addEvent(CalendarEvent event) {
        Item item = container.addItem(event);
        if (item != null) {
            item.getItemProperty(getCaptionProperty()).setValue(
                    event.getCaption());
            item.getItemProperty(getStartDateProperty()).setValue(
                    event.getStart());
            item.getItemProperty(getEndDateProperty()).setValue(event.getEnd());
            item.getItemProperty(getStyleNameProperty()).setValue(
                    event.getStyleName());
            item.getItemProperty(getDescriptionProperty()).setValue(
                    event.getDescription());

            // Ensure container is sorted
            if (container instanceof Container.Sortable) {
                ((Container.Sortable) container).sort(new Object[] {
                        getStartDateProperty(), getEndDateProperty() },
                        new boolean[] { true, true });
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEditableEventProvider#removeEvent
     * (com.vaadin.addon.calendar.event.CalendarEvent)
     */
    public void removeEvent(CalendarEvent event) {
        container.removeItem(event);
    }
}
