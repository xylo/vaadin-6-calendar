package com.vaadin.addon.calendar.ui;

import java.lang.reflect.Method;
import java.util.Date;

import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
import com.vaadin.event.ComponentEventListener;
import com.vaadin.tools.ReflectTools;

public interface CalendarEvents {

    /**
     * Listener interface for calendar event drag & drops.
     */
    public interface EventMoveNotifier {

        /**
         * Add a EventMoveListener.
         * 
         * @param listener
         *            EventMoveListener to be added
         */
        public void addListener(EventMoveListener listener);

        /**
         * Remove the EventMoveListener.
         * 
         * @param listener
         *            EventMoveListener to be removed
         * */
        public void removeListener(EventMoveListener listener);
    }

    /**
     * MoveEvent is sent when existing calendar event is dragged to a new
     * position.
     */
    @SuppressWarnings("serial")
    public class MoveEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.EVENTMOVE;

        /** Index for the moved Schedule.Event. */
        private Calendar.Event calendarEvent;

        /** New starting date for the moved Calendar.Event. */
        private Date newStart;

        /**
         * MoveEvent needs the target calendar event and new start date.
         * 
         * @param source
         *            Calendar component.
         * @param calendarEvent
         *            Target calendar event.
         * @param newStart
         *            Target calendar event's new start date.
         */
        public MoveEvent(Calendar source, Calendar.Event calendarEvent,
                Date newStart) {
            super(source);

            this.calendarEvent = calendarEvent;
            this.newStart = newStart;
        }

        /**
         * Get target calendar event.
         * 
         * @return Target calendar event.
         */
        public Calendar.Event getCalendarEvent() {
            return calendarEvent;
        }

        /**
         * Get new start date.
         * 
         * @return New start date.
         */
        public Date getNewStart() {
            return newStart;
        }
    }

    /** EventMoveListeners listens for MoveEvents. */
    public interface EventMoveListener extends ComponentEventListener {

        /** Trigger method for the MoveEvent. */
        public static final Method eventMoveMethod = ReflectTools.findMethod(
                EventMoveListener.class, "eventMove", MoveEvent.class);

        /**
         * This method will be called when calendar event has been moved to a
         * new position.
         * 
         * @param event
         *            MoveEvent containing specific information of the new
         *            position and target calendar event.
         */
        public void eventMove(MoveEvent event);
    }

    /**
     * Listener interface for day or time cell drag-marking with mouse.
     */
    public interface RangeSelectNotifier {

        /**
         * Add RangeSelectListener that listens for drag-marking.
         * 
         * @param listener
         *            RangeSelectListener to be added.
         */
        public void addListener(RangeSelectListener listener);

        /**
         * Remove target RangeSelectListener;
         * 
         * @param listener
         *            RangeSelectListener to be removed.
         */
        public void removeListener(RangeSelectListener listener);
    }

    /**
     * RangeSelectEvent is sent when day or time cells are drag-marked with
     * mouse.
     */
    @SuppressWarnings("serial")
    public class RangeSelectEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.RANGESELECT;

        /** Calendar event's start date. */
        private Date start;

        /** Calendar event's end date. */
        private Date end;

        /**
         * RangeSelectEvent needs a start and end date.
         * 
         * @param source
         *            Calendar component.
         * @param start
         *            Start date.
         * @param end
         *            End date.
         */
        public RangeSelectEvent(Calendar source, Date start, Date end) {
            super(source);
            this.start = start;
            this.end = end;
        }

        /**
         * Get start date.
         * 
         * @return Start date.
         */
        public Date getStart() {
            return start;
        }

        /**
         * Get end date.
         * 
         * @return End date.
         */
        public Date getEnd() {
            return end;
        }
    }

    /** EventMoveListeners listens for RangeSelectEvent. */
    public interface RangeSelectListener extends ComponentEventListener {

        /** Trigger method for the RangeSelectEvent. */
        public static final Method rangeSelectMethod = ReflectTools.findMethod(
                RangeSelectListener.class, "rangeSelect",
                RangeSelectEvent.class);

        public void rangeSelect(RangeSelectEvent event);
    }

    public interface NavigationNotifier {
        public void addListener(ForwardListener listener);

        public void removeListener(ForwardListener listener);

        public void addListener(BackwardListener listener);

        public void removeListener(BackwardListener listener);

        public void addListener(DateClickListener listener);

        public void removeListener(DateClickListener listener);

        public void addListener(EventClickListener listener);

        public void removeListener(EventClickListener listener);

        public void addListener(WeekClickListener listener);

        public void removeListener(WeekClickListener listener);
    }

    @SuppressWarnings("serial")
    public class ForwardEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.FORWARD;

        public ForwardEvent(Calendar source) {
            super(source);
        }
    }

    public interface ForwardListener extends ComponentEventListener {

        public static final Method forwardMethod = ReflectTools.findMethod(
                ForwardListener.class, "forward", ForwardEvent.class);

        public void forward(ForwardEvent event);
    }

    @SuppressWarnings("serial")
    public class BackwardEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.BACKWARD;

        public BackwardEvent(Calendar source) {
            super(source);
        }
    }

    public interface BackwardListener extends ComponentEventListener {

        public static final Method backwardMethod = ReflectTools.findMethod(
                BackwardListener.class, "backward", BackwardEvent.class);

        public void backward(BackwardEvent event);
    }

    @SuppressWarnings("serial")
    public class DateClickEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.DATECLICK;

        private Date date;

        public DateClickEvent(Calendar source, Date date) {
            super(source);
            this.date = date;
        }

        public Date getDate() {
            return date;
        }
    }

    public interface DateClickListener extends ComponentEventListener {

        public static final Method dateClickMethod = ReflectTools.findMethod(
                DateClickListener.class, "dateClick", DateClickEvent.class);

        public void dateClick(DateClickEvent event);
    }

    @SuppressWarnings("serial")
    public class EventClick extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.EVENTCLICK;

        private Calendar.Event calendarEvent;

        public EventClick(Calendar source, Calendar.Event calendarEvent) {
            super(source);
            this.calendarEvent = calendarEvent;
        }

        public Calendar.Event getCalendarEvent() {
            return calendarEvent;
        }
    }

    public interface EventClickListener extends ComponentEventListener {

        public static final Method eventClickMethod = ReflectTools.findMethod(
                EventClickListener.class, "eventClick", EventClick.class);

        public void eventClick(EventClick event);
    }

    @SuppressWarnings("serial")
    public class WeekClick extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.WEEKCLICK;

        private int week;

        private int year;

        public WeekClick(Calendar source, int week, int year) {
            super(source);
            this.week = week;
            this.year = year;
        }

        public int getWeek() {
            return week;
        }

        public int getYear() {
            return year;
        }
    }

    public interface WeekClickListener extends ComponentEventListener {

        public static final Method weekClickMethod = ReflectTools.findMethod(
                WeekClickListener.class, "weekClick", WeekClick.class);

        public void weekClick(WeekClick event);
    }
}
