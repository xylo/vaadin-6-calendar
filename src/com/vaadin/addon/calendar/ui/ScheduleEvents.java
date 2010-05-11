package com.vaadin.addon.calendar.ui;

import java.lang.reflect.Method;
import java.util.Date;

import com.vaadin.addon.calendar.gwt.client.ui.schedule.ScheduleEventId;
import com.vaadin.event.ComponentEventListener;
import com.vaadin.tools.ReflectTools;
import com.vaadin.ui.Component;

public interface ScheduleEvents {

    /**
     * Listener for schedule event drag&drops
     */
    public interface EventMoveNotifier {

        public void addListener(EventMoveListener listener);

        public void removeListener(EventMoveListener listener);
    }

    @SuppressWarnings("serial")
    public class EventMoveEvent extends Component.Event {

        public static final String EVENT_ID = ScheduleEventId.EVENTMOVE;

        /** Index for the moved Schedule.Event. */
        private Calendar.Event scheduleEvent;

        /** New starting date for the moved Schedule.Event. */
        private Date newFromDateTime;

        public EventMoveEvent(Component source, Calendar.Event scheduleEvent,
                Date newFromDateTime) {
            super(source);
            this.scheduleEvent = scheduleEvent;
            this.newFromDateTime = newFromDateTime;
        }

        public Calendar.Event getScheduleEvent() {
            return scheduleEvent;
        }

        public Date getNewFromDateTime() {
            return newFromDateTime;
        }
    }

    public interface EventMoveListener extends ComponentEventListener {

        public static final Method eventMoveMethod = ReflectTools.findMethod(
                EventMoveListener.class, "eventMove", EventMoveEvent.class);

        public void eventMove(EventMoveEvent event);
    }

    /**
     * Listener for day cell drag-marking with mouse
     */
    public interface RangeSelectNotifier {

        public void addListener(RangeSelectListener listener);

        public void removeListener(RangeSelectListener listener);
    }

    @SuppressWarnings("serial")
    public class RangeSelectEvent extends Component.Event {

        public static final String EVENT_ID = ScheduleEventId.RANGESELECT;

        private Date from;

        private Date to;

        public RangeSelectEvent(Component source, Date from, Date to) {
            super(source);
            this.from = from;
            this.to = to;
        }

        public Date getFrom() {
            return from;
        }

        public Date getTo() {
            return to;
        }
    }

    public interface RangeSelectListener extends ComponentEventListener {

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
    public class ForwardEvent extends Component.Event {

        public static final String EVENT_ID = ScheduleEventId.FORWARD;

        public ForwardEvent(Component source) {
            super(source);
        }
    }

    public interface ForwardListener extends ComponentEventListener {

        public static final Method forwardMethod = ReflectTools.findMethod(
                ForwardListener.class, "forward", ForwardEvent.class);

        public void forward(ForwardEvent event);
    }

    @SuppressWarnings("serial")
    public class BackwardEvent extends Component.Event {

        public static final String EVENT_ID = ScheduleEventId.BACKWARD;

        public BackwardEvent(Component source) {
            super(source);
        }
    }

    public interface BackwardListener extends ComponentEventListener {

        public static final Method backwardMethod = ReflectTools.findMethod(
                BackwardListener.class, "backward", BackwardEvent.class);

        public void backward(BackwardEvent event);
    }

    @SuppressWarnings("serial")
    public class DateClickEvent extends Component.Event {

        public static final String EVENT_ID = ScheduleEventId.DATECLICK;

        private Date date;

        public DateClickEvent(Component source, Date date) {
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
    public class EventClickEvent extends Component.Event {

        public static final String EVENT_ID = ScheduleEventId.EVENTCLICK;

        private Calendar.Event scheduleEvent;

        public EventClickEvent(Component source, Calendar.Event scheduleEvent) {
            super(source);
            this.scheduleEvent = scheduleEvent;
        }

        public Calendar.Event getScheduleEvent() {
            return scheduleEvent;
        }
    }

    public interface EventClickListener extends ComponentEventListener {

        public static final Method eventClickMethod = ReflectTools.findMethod(
                EventClickListener.class, "eventClick", EventClickEvent.class);

        public void eventClick(EventClickEvent event);
    }

    @SuppressWarnings("serial")
    public class WeekClickEvent extends Component.Event {

        public static final String EVENT_ID = ScheduleEventId.WEEKCLICK;

        private int week;

        private int year;

        public WeekClickEvent(Component source, int week, int year) {
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
                WeekClickListener.class, "weekClick", WeekClickEvent.class);

        public void weekClick(WeekClickEvent event);
    }
}
