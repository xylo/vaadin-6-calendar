package com.vaadin.addon.calendar.ui;

import java.lang.reflect.Method;
import java.util.Date;

import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
import com.vaadin.event.ComponentEventListener;
import com.vaadin.tools.ReflectTools;

/**
 * interface for all calendar component events.
 */
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
         * Defines the event's calendar view mode.
         */
        private boolean monthlyMode;

        /**
         * RangeSelectEvent needs a start and end date.
         * 
         * @param source
         *            Calendar component.
         * @param start
         *            Start date.
         * @param end
         *            End date.
         * @param monthlyMode
         *            Calendar view mode.
         */
        public RangeSelectEvent(Calendar source, Date start, Date end,
                boolean monthlyMode) {
            super(source);
            this.start = start;
            this.end = end;
            this.monthlyMode = monthlyMode;
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

        /**
         * Gets the event's calendar view mode. Calendar can be be either in
         * monthly or weekly mode, depending on the active date range.
         * 
         * @return Returns true when monthly view is active.
         */
        public boolean isMonthlyMode() {
            return this.monthlyMode;
        }
    }

    /** EventMoveListeners listens for RangeSelectEvent. */
    public interface RangeSelectListener extends ComponentEventListener {

        /** Trigger method for the RangeSelectEvent. */
        public static final Method rangeSelectMethod = ReflectTools.findMethod(
                RangeSelectListener.class, "rangeSelect",
                RangeSelectEvent.class);

        /**
         * This method will be called when day or time cells are drag-marked
         * with mouse.
         * 
         * @param event
         *            RangeSelectEvent that contains range start and end date.
         */
        public void rangeSelect(RangeSelectEvent event);
    }

    /** Listener interface for calendar navigation listening. */
    public interface NavigationNotifier {
        /**
         * Add a forward navigation listener.
         * 
         * @param listener
         *            ForwardListener to be added.
         */
        public void addListener(ForwardListener listener);

        /**
         * Remove the target ForwardListener.
         * 
         * @param listener
         *            ForwardListener to be removed.
         */
        public void removeListener(ForwardListener listener);

        /**
         * Add a backward navigation listener.
         * 
         * @param listener
         *            BackwardListener to be added.
         */
        public void addListener(BackwardListener listener);

        /**
         * Remove the target BackwardListener.
         * 
         * @param listener
         *            BackwardListener to be removed.
         */
        public void removeListener(BackwardListener listener);

        /**
         * Add a date click listener.
         * 
         * @param listener
         *            DateClickListener to be added.
         */
        public void addListener(DateClickListener listener);

        /**
         * Remove the target DateClickListener.
         * 
         * @param listener
         *            DateClickListener to be removed.
         */
        public void removeListener(DateClickListener listener);

        /**
         * Add a event click listener.
         * 
         * @param listener
         *            EventClickListener to be added.
         */
        public void addListener(EventClickListener listener);

        /**
         * Remove the target EventClickListener.
         * 
         * @param listener
         *            EventClickListener to be removed.
         */
        public void removeListener(EventClickListener listener);

        /**
         * Add a week click listener.
         * 
         * @param listener
         *            WeekClickListener to be added.
         */
        public void addListener(WeekClickListener listener);

        /**
         * Remove the target WeekClickListener.
         * 
         * @param listener
         *            WeekClickListener to be removed.
         */
        public void removeListener(WeekClickListener listener);
    }

    /**
     * ForwardEvent is sent when calendar's forward navigation button is
     * clicked.
     */
    @SuppressWarnings("serial")
    public class ForwardEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.FORWARD;

        /**
         * ForwardEvent needs only the source calendar component.
         * 
         * @param source
         *            Calendar component.
         */
        public ForwardEvent(Calendar source) {
            super(source);
        }
    }

    /** ForwardListener listens for ForwardEvent. */
    public interface ForwardListener extends ComponentEventListener {

        /** Trigger method for the ForwardEvent. */
        public static final Method forwardMethod = ReflectTools.findMethod(
                ForwardListener.class, "forward", ForwardEvent.class);

        /**
         * This method will be called when calendar date range is moved forward.
         * 
         * @param event
         *            ForwardEvent
         */
        public void forward(ForwardEvent event);
    }

    /**
     * BackwardEvent is sent when calendar's backward navigation button is
     * clicked.
     */
    @SuppressWarnings("serial")
    public class BackwardEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.BACKWARD;

        /**
         * BackwardEvent needs only the source calendar component.
         * 
         * @param source
         *            Calendar component.
         */
        public BackwardEvent(Calendar source) {
            super(source);
        }
    }

    /** BackwardListener listens for BackwardEvent. */
    public interface BackwardListener extends ComponentEventListener {

        /** Trigger method for the BackwardEvent. */
        public static final Method backwardMethod = ReflectTools.findMethod(
                BackwardListener.class, "backward", BackwardEvent.class);

        /**
         * This method will be called when calendar date range is moved
         * backwards.
         * 
         * @param event
         *            BackwardEvent
         */
        public void backward(BackwardEvent event);
    }

    /**
     * DateClickEvent is sent when calendar's date is clicked.
     */
    @SuppressWarnings("serial")
    public class DateClickEvent extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.DATECLICK;

        /** Date that was clicked. */
        private Date date;

        /** DateClickEvent needs the target date that was clicked. */
        public DateClickEvent(Calendar source, Date date) {
            super(source);
            this.date = date;
        }

        /**
         * Get clicked date.
         * 
         * @return Clicked date.
         */
        public Date getDate() {
            return date;
        }
    }

    /** DateClickListener listens for DateClickEvent. */
    public interface DateClickListener extends ComponentEventListener {

        /** Trigger method for the DateClickEvent. */
        public static final Method dateClickMethod = ReflectTools.findMethod(
                DateClickListener.class, "dateClick", DateClickEvent.class);

        /**
         * This method will be called when calendar date is clicked.
         * 
         * @param event
         *            DateClickEvent containing the target date.
         */
        public void dateClick(DateClickEvent event);
    }

    /**
     * EventClick is sent when calendar's event is clicked.
     */
    @SuppressWarnings("serial")
    public class EventClick extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.EVENTCLICK;

        /** Clicked calendar event. */
        private Calendar.Event calendarEvent;

        /** Target calendar event is needed for the EventClick. */
        public EventClick(Calendar source, Calendar.Event calendarEvent) {
            super(source);
            this.calendarEvent = calendarEvent;
        }

        /**
         * Get clicked calendar event.
         * 
         * @return Clicked calendar event.
         */
        public Calendar.Event getCalendarEvent() {
            return calendarEvent;
        }
    }

    /** EventClickListener listens for EventClick. */
    public interface EventClickListener extends ComponentEventListener {

        /** Trigger method for the EventClick. */
        public static final Method eventClickMethod = ReflectTools.findMethod(
                EventClickListener.class, "eventClick", EventClick.class);

        /**
         * This method will be called when calendar event is clicked.
         * 
         * @param event
         *            EventClick containing the target event.
         */
        public void eventClick(EventClick event);
    }

    /**
     * WeekClick is sent when calendar's week is clicked.
     */
    @SuppressWarnings("serial")
    public class WeekClick extends CalendarEvent {

        public static final String EVENT_ID = CalendarEventId.WEEKCLICK;

        /** Target week. */
        private int week;

        /** Target year. */
        private int year;

        /**
         * WeekClick needs a target year and week.
         * 
         * @param source
         *            Target calendar.
         * @param week
         *            Target week.
         * @param year
         *            Target year.
         */
        public WeekClick(Calendar source, int week, int year) {
            super(source);
            this.week = week;
            this.year = year;
        }

        /**
         * Get week as a integer. See {@link java.util.Calendar} for the allowed
         * values.
         * 
         * @return Week as a integer.
         */
        public int getWeek() {
            return week;
        }

        /**
         * Get year as a integer. See {@link java.util.Calendar} for the allowed
         * values.
         * 
         * @return Year as a integer
         */
        public int getYear() {
            return year;
        }
    }

    /** WeekClickListener listens for WeekClick. */
    public interface WeekClickListener extends ComponentEventListener {

        /** Trigger method for the WeekClick. */
        public static final Method weekClickMethod = ReflectTools.findMethod(
                WeekClickListener.class, "weekClick", WeekClick.class);

        /**
         * This method will be called when calendar's week is clicked.
         * 
         * @param event
         *            WeekClick containing the target week and year.
         */
        public void weekClick(WeekClick event);
    }
}
