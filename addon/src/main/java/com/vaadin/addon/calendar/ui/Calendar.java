/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.addon.calendar.ui;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEditableEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEvent.EventChange;
import com.vaadin.addon.calendar.event.CalendarEvent.EventChangeListener;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.gwt.client.ui.CalendarServerRpc;
import com.vaadin.addon.calendar.gwt.client.ui.CalendarState;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendarAction;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.DateUtil;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicBackwardHandler;
import com.vaadin.addon.calendar.ui.handler.BasicDateClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicEventMoveHandler;
import com.vaadin.addon.calendar.ui.handler.BasicEventResizeHandler;
import com.vaadin.addon.calendar.ui.handler.BasicForwardHandler;
import com.vaadin.addon.calendar.ui.handler.BasicWeekClickHandler;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.server.KeyMapper;
import com.vaadin.ui.AbstractComponent;

/**
 * <p>
 * Vaadin Calendar is for visualizing events in calendar. Calendar events can be
 * visualized in the variable length view depending on the start and end dates.
 * </p>
 * 
 * <li>You can set the viewable date range with {@link #setStartDate(Date)} and
 * {@link #setEndDate(Date)} methods. Calendar has a default date range of one
 * week</li>
 * 
 * <li>Calendar has two kind of views: monthly and weekly view</li>
 * 
 * <li>If date range is seven days long or smaller, weekly view is used.</li>
 * 
 * <li>Calendar queries its events by using a
 * {@link com.vaadin.addon.calendar.event.CalendarEventProvider
 * CalendarEventProvider}. By default, a
 * {@link com.vaadin.addon.calendar.event.BasicEventProvider BasicEventProvider}
 * is used.</li>
 * 
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class Calendar extends AbstractComponent implements
        CalendarComponentEvents.NavigationNotifier,
        CalendarComponentEvents.EventMoveNotifier,
        CalendarComponentEvents.RangeSelectNotifier,
        CalendarComponentEvents.EventResizeNotifier,
        CalendarEventProvider.EventSetChangeListener, DropTarget,
        CalendarEditableEventProvider, Action.Container {

    /**
     * Calendar can use either 12 hours clock or 24 hours clock.
     */
    public enum TimeFormat {

        Format12H(), Format24H();
    }

    /** Defines currently active format for time. 12H/24H. */
    protected TimeFormat currentTimeFormat;

    /** Internal calendar data source. */
    protected java.util.Calendar currentCalendar = java.util.Calendar
            .getInstance();

    /** Defines the component's active time zone. */
    protected TimeZone timezone;

    /** Defines the calendar's date range starting point. */
    protected Date startDate = null;

    /** Defines the calendar's date range ending point. */
    protected Date endDate = null;

    /** Event provider. */
    private CalendarEventProvider calendarEventProvider;

    /**
     * Internal buffer for the events that are retrieved from the event
     * provider.
     */
    protected List<CalendarEvent> events;

    /** Date format that will be used in the UIDL for dates. */
    protected DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");

    /** Time format that will be used in the UIDL for time. */
    protected DateFormat df_time = new SimpleDateFormat("HH:mm:ss");

    /** Date format that will be used in the UIDL for both date and time. */
    protected DateFormat df_date_time = new SimpleDateFormat(
            DateUtil.CLIENT_DATE_FORMAT + "-" + DateUtil.CLIENT_TIME_FORMAT);

    /**
     * Week view's scroll position. Client sends updates to this value so that
     * scroll position wont reset all the time.
     */
    private int scrollTop = 0;

    /** Caption format for the weekly view */
    private String weeklyCaptionFormat = null;

    /** Map from event ids to event handlers */
    private final Map<String, EventListener> handlers;

    /**
     * Drop Handler for Vaadin DD. By default null.
     */
    private DropHandler dropHandler;

    /**
     * First day to show for a week
     */
    private int firstDay = 1;

    /**
     * Last day to show for a week
     */
    private int lastDay = 7;

    /**
     * First hour to show for a day
     */
    private int firstHour = 0;

    /**
     * Last hour to show for a day
     */
    private int lastHour = 23;

    /**
     * List of action handlers.
     */
    private LinkedList<Action.Handler> actionHandlers = null;

    /**
     * Action mapper.
     */
    private KeyMapper<Action> actionMapper = null;

    /**
     * 
     */
    private CalendarServerRpcImpl rpc = new CalendarServerRpcImpl();

    /**
     * Returns the logger for the calendar
     */
    protected Logger getLogger() {
        return Logger.getLogger(Calendar.class.getName());
    }

    /**
     * Construct a Vaadin Calendar with a BasicEventProvider and no caption.
     * Default date range is one week.
     */
    public Calendar() {
        this(null, new BasicEventProvider());
    }

    /**
     * Construct a Vaadin Calendar with a BasicEventProvider and the provided
     * caption. Default date range is one week.
     * 
     * @param caption
     */
    public Calendar(String caption) {
        this(caption, new BasicEventProvider());
    }

    /**
     * <p>
     * Construct a Vaadin Calendar with event provider. Event provider is
     * obligatory, because calendar component will query active events through
     * it.
     * </p>
     * 
     * <p>
     * By default, Vaadin Calendar will show dates from the start of the current
     * week to the end of the current week. Use {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)} to change this.
     * </p>
     * 
     * @param eventProvider
     *            Event provider, cannot be null.
     */
    public Calendar(CalendarEventProvider eventProvider) {
        this(null, eventProvider);
    }

    /**
     * <p>
     * Construct a Vaadin Calendar with event provider and a caption. Event
     * provider is obligatory, because calendar component will query active
     * events through it.
     * </p>
     * 
     * <p>
     * By default, Vaadin Calendar will show dates from the start of the current
     * week to the end of the current week. Use {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)} to change this.
     * </p>
     * 
     * @param eventProvider
     *            Event provider, cannot be null.
     */
    // this is the constructor every other constructor calls
    public Calendar(String caption, CalendarEventProvider eventProvider) {
        registerRpc(rpc);
        setCaption(caption);
        handlers = new HashMap<String, EventListener>();
        setDefaultHandlers();
        currentCalendar.setTime(new Date());
        setEventProvider(eventProvider);
        getState().setFirstVisibleDayOfWeek(firstDay);
        getState().setLastVisibleDayOfWeek(lastDay);
        getState().setFirstHourOfDay(firstHour);
        getState().setLastHourOfDay(lastHour);
        setTimeFormat(null);

    }

    @Override
    public CalendarState getState() {
        return (CalendarState) super.getState();
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        initCalendarWithLocale();

        getState().setFormat24H(TimeFormat.Format24H == getTimeFormat());
        setupDaysAndActions();
        setupCalendarEvents();
        rpc.scroll(scrollTop);
    }

    /**
     * Set all the wanted default handlers here. This is always called after
     * constructing this object. All other events have default handlers except
     * range and event click.
     */
    protected void setDefaultHandlers() {
        setHandler(new BasicBackwardHandler());
        setHandler(new BasicForwardHandler());
        setHandler(new BasicWeekClickHandler());
        setHandler(new BasicDateClickHandler());
        setHandler(new BasicEventMoveHandler());
        setHandler(new BasicEventResizeHandler());
    }

    /**
     * Gets the calendar's start date.
     * 
     * @return First visible date.
     */
    public Date getStartDate() {
        if (startDate == null) {
            currentCalendar.set(java.util.Calendar.DAY_OF_WEEK,
                    currentCalendar.getFirstDayOfWeek());
            return currentCalendar.getTime();
        }
        return startDate;
    }

    /**
     * Sets start date for the calendar. This and {@link #setEndDate(Date)}
     * control the range of dates visible on the component. The default range is
     * one week.
     * 
     * @param date
     *            First visible date to show.
     */
    public void setStartDate(Date date) {
        if (!date.equals(startDate)) {
            startDate = date;
            markAsDirty();
        }
    }

    /**
     * Gets the calendar's end date.
     * 
     * @return Last visible date.
     */
    public Date getEndDate() {
        if (endDate == null) {
            currentCalendar.set(java.util.Calendar.DAY_OF_WEEK,
                    currentCalendar.getFirstDayOfWeek() + 6);
            return currentCalendar.getTime();
        }
        return endDate;
    }

    /**
     * Sets end date for the calendar. Starting from startDate, only six weeks
     * will be shown if duration to endDate is longer than six weeks.
     * 
     * This and {@link #setStartDate(Date)} control the range of dates visible
     * on the component. The default range is one week.
     * 
     * @param date
     *            Last visible date to show.
     */
    public void setEndDate(Date date) {
        if (startDate != null && startDate.after(date)) {
            startDate = (Date) date.clone();
            markAsDirty();
        } else if (!date.equals(endDate)) {
            endDate = date;
            markAsDirty();
        }
    }

    /**
     * Sets the locale to be used in the Calendar component.
     * 
     * @see com.vaadin.ui.AbstractComponent#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale newLocale) {
        super.setLocale(newLocale);
        initCalendarWithLocale();
    }

    /**
     * Initialize the java calendar instance with the current locale and
     * timezone.
     */
    private void initCalendarWithLocale() {
        if (timezone != null) {
            currentCalendar = java.util.Calendar.getInstance(timezone,
                    getLocale());

        } else {
            currentCalendar = java.util.Calendar.getInstance(getLocale());
        }
    }

    private void setupCalendarEvents() {
        int durationInDays = (int) (((endDate.getTime()) - startDate.getTime()) / VCalendar.DAYINMILLIS);
        durationInDays++;
        if (durationInDays > 60) {
            throw new RuntimeException("Daterange is too big (max 60) = "
                    + durationInDays);
        }

        Date firstDateToShow = expandStartDate(startDate, durationInDays > 7);
        Date lastDateToShow = expandEndDate(endDate, durationInDays > 7);

        currentCalendar.setTime(firstDateToShow);
        events = getEventProvider().getEvents(firstDateToShow, lastDateToShow);

        List<CalendarState.Event> calendarStateEvents = new ArrayList<CalendarState.Event>();
        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                CalendarEvent e = events.get(i);
                CalendarState.Event event = new CalendarState.Event();
                event.setIndex(i);
                event.setCaption(e.getCaption() == null ? "" : e.getCaption());
                event.setDateFrom(df_date.format(e.getStart()));
                event.setDateTo(df_date.format(e.getEnd()));
                event.setTimeFrom(df_time.format(e.getStart()));
                event.setTimeTo(df_time.format(e.getEnd()));
                event.setDescription(e.getDescription() == null ? "" : e
                        .getDescription());
                event.setStyleName(e.getStyleName() == null ? "" : e
                        .getStyleName());
                event.setAllDay(e.isAllDay());
                calendarStateEvents.add(event);
            }
        }
        getState().setEvents(calendarStateEvents);
    }

    private void setupDaysAndActions() {
        // Make sure we have a up-to-date locale
        initCalendarWithLocale();

        CalendarState state = getState();

        state.setFirstDayOfWeek(currentCalendar.getFirstDayOfWeek());

        // If only one is null, throw exception
        // If both are null, set defaults
        if (startDate == null ^ endDate == null) {
            String message = "Schedule cannot be painted without a proper date range.\n";
            if (startDate == null) {
                throw new RuntimeException(message
                        + "You must set a start date using setStartDate(Date).");

            } else {
                throw new RuntimeException(message
                        + "You must set an end date using setEndDate(Date).");
            }

        } else if (startDate == null && endDate == null) {
            // set defaults
            startDate = getStartDate();
            endDate = getEndDate();
        }

        int durationInDays = (int) (((endDate.getTime()) - startDate.getTime()) / VCalendar.DAYINMILLIS);
        durationInDays++;
        if (durationInDays > 60) {
            throw new RuntimeException("Daterange is too big (max 60) = "
                    + durationInDays);
        }

        state.setDayNames(getDayNamesShort());
        state.setMonthNames(getMonthNamesShort());

        // Use same timezone in all dates this component handles.
        // Show "now"-marker in browser within given timezone.
        Date now = new Date();
        currentCalendar.setTime(now);
        now = currentCalendar.getTime();

        // Reset time zones for custom date formats
        df_date.setTimeZone(currentCalendar.getTimeZone());
        df_time.setTimeZone(currentCalendar.getTimeZone());

        state.setNow(df_date.format(now) + " " + df_time.format(now));

        Date firstDateToShow = expandStartDate(startDate, durationInDays > 7);
        Date lastDateToShow = expandEndDate(endDate, durationInDays > 7);

        currentCalendar.setTime(firstDateToShow);

        DateFormat weeklyCaptionFormatter = getWeeklyCaptionFormatter();
        weeklyCaptionFormatter.setTimeZone(currentCalendar.getTimeZone());

        Map<CalendarDateRange, Set<Action>> actionMap = new HashMap<CalendarDateRange, Set<Action>>();

        List<CalendarState.Day> days = new ArrayList<CalendarState.Day>();

        // Send all dates to client from server. This
        // approach was taken because gwt doesn't
        // support date localization properly.
        while (currentCalendar.getTime().compareTo(lastDateToShow) < 1) {
            final Date date = currentCalendar.getTime();
            final CalendarState.Day day = new CalendarState.Day();
            day.setDate(df_date.format(date));
            day.setLocalizedDateFormat(weeklyCaptionFormatter.format(date));
            day.setDayOfWeek(getDowByLocale(currentCalendar));
            day.setWeek(currentCalendar.get(java.util.Calendar.WEEK_OF_YEAR));

            days.add(day);

            // Get actions for a specific date
            if (actionHandlers != null) {
                for (Action.Handler actionHandler : actionHandlers) {

                    // Create calendar which omits time
                    GregorianCalendar cal = new GregorianCalendar(
                            getTimeZone(), getLocale());
                    cal.clear();
                    cal.set(currentCalendar.get(java.util.Calendar.YEAR),
                            currentCalendar.get(java.util.Calendar.MONTH),
                            currentCalendar.get(java.util.Calendar.DATE));

                    // Get day start and end times
                    Date start = cal.getTime();
                    cal.add(java.util.Calendar.DATE, 1);
                    Date end = cal.getTime();

                    boolean monthView = (durationInDays > 7);

                    /**
                     * If in day or week view add actions for each half-an-hour.
                     * If in month view add actions for each day
                     */
                    if (monthView) {
                        setActionsForDay(actionMap, start, end, actionHandler);
                    } else {
                        setActionsForEachHalfHour(actionMap, start, end,
                                actionHandler);
                    }

                }
            }

            currentCalendar.add(java.util.Calendar.DATE, 1);
        }
        state.setDays(days);
        state.setActions(createActionsList(actionMap));
    }

    private void setActionsForEachHalfHour(
            Map<CalendarDateRange, Set<Action>> actionMap, Date start,
            Date end, Action.Handler actionHandler) {
        GregorianCalendar cal = new GregorianCalendar(getTimeZone(),
                getLocale());
        cal.setTime(start);
        while (cal.getTime().before(end)) {
            Date s = cal.getTime();
            cal.add(java.util.Calendar.MINUTE, 30);
            Date e = cal.getTime();
            CalendarDateRange range = new CalendarDateRange(s, e, getTimeZone());
            Action[] actions = actionHandler.getActions(range, this);
            if (actions != null) {
                Set<Action> actionSet = new HashSet<Action>(
                        Arrays.asList(actions));
                actionMap.put(range, actionSet);
            }
        }
    }

    private void setActionsForDay(
            Map<CalendarDateRange, Set<Action>> actionMap, Date start,
            Date end, Action.Handler actionHandler) {
        CalendarDateRange range = new CalendarDateRange(start, end,
                getTimeZone());
        Action[] actions = actionHandler.getActions(range, this);
        if (actions != null) {
            Set<Action> actionSet = new HashSet<Action>(Arrays.asList(actions));
            actionMap.put(range, actionSet);
        }
    }

    private List<CalendarState.Action> createActionsList(
            Map<CalendarDateRange, Set<Action>> actionMap) {
        if (actionMap.isEmpty()) {
            return null;
        }

        List<CalendarState.Action> calendarActions = new ArrayList<CalendarState.Action>();

        SimpleDateFormat formatter = new SimpleDateFormat(
                VCalendarAction.ACTION_DATE_FORMAT_PATTERN);
        formatter.setTimeZone(getTimeZone());

        for (Entry<CalendarDateRange, Set<Action>> entry : actionMap.entrySet()) {
            CalendarDateRange range = entry.getKey();
            Set<Action> actions = entry.getValue();
            for (Action action : actions) {
                String key = actionMapper.key(action);
                CalendarState.Action calendarAction = new CalendarState.Action();
                calendarAction.setActionKey(key);
                calendarAction.setCaption(action.getCaption());
                setResource(key, action.getIcon());
                calendarAction.setIconKey(key);
                calendarAction.setStartDate(formatter.format(range.getStart()));
                calendarAction.setEndDate(formatter.format(range.getEnd()));
                calendarActions.add(calendarAction);
            }
        }

        return calendarActions;
    }

    /**
     * Gets currently active time format. Value is either TimeFormat.Format12H
     * or TimeFormat.Format24H.
     * 
     * @return TimeFormat Format for the time.
     */
    public TimeFormat getTimeFormat() {
        if (currentTimeFormat == null) {
            SimpleDateFormat f = (SimpleDateFormat) SimpleDateFormat
                    .getTimeInstance(SimpleDateFormat.SHORT, getLocale());
            String p = f.toPattern();
            if (p.indexOf("HH") != -1 || p.indexOf("H") != -1) {
                return TimeFormat.Format24H;
            }
            return TimeFormat.Format12H;
        }
        return currentTimeFormat;
    }

    /**
     * Example: <code>setTimeFormat(TimeFormat.Format12H);</code></br> Set to
     * null, if you want the format being defined by the locale.
     * 
     * @param format
     *            Set 12h or 24h format. Default is defined by the locale.
     */
    public void setTimeFormat(TimeFormat format) {
        currentTimeFormat = format;
        markAsDirty();
    }

    /**
     * Returns a time zone that is currently used by this component.
     * 
     * @return Component's Time zone
     */
    public TimeZone getTimeZone() {
        if (timezone == null) {
            return currentCalendar.getTimeZone();
        }
        return timezone;
    }

    /**
     * Set time zone that this component will use. Null value sets the default
     * time zone.
     * 
     * @param zone
     *            Time zone to use
     */
    public void setTimeZone(TimeZone zone) {
        timezone = zone;
        if (!currentCalendar.getTimeZone().equals(zone)) {
            if (zone == null) {
                zone = TimeZone.getDefault();
            }
            currentCalendar.setTimeZone(zone);
            df_date_time.setTimeZone(zone);
            markAsDirty();
        }
    }

    /**
     * Get the internally used Calendar instance. This is the currently used
     * instance of {@link java.util.Calendar} but is bound to change during the
     * lifetime of the component.
     * 
     * @return the currently used java calendar
     */
    public java.util.Calendar getInternalCalendar() {
        return currentCalendar;
    }

    /**
     * <p>
     * This method restricts the weekdays that are shown. This affects both the
     * monthly and the weekly view. The general contract is that <b>firstDay <
     * lastDay</b>.
     * </p>
     * 
     * <p>
     * Note that this only affects the rendering process. Events are still
     * requested by the dates set by {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)}.
     * </p>
     * 
     * @deprecated Use {@link #setFirstVisibleDayOfWeek(int)} and
     *             {@link #setLastVisibleDayOfWeek(int)} instead.
     * 
     * @param firstDay
     *            the first day of the week to show, between 1 and 7
     * @param lastDay
     *            the first day of the week to show, between 1 and 7
     */
    @Deprecated
    public void setVisibleDaysOfWeek(int firstDay, int lastDay) {
        if (firstDay >= lastDay || firstDay < 1 || lastDay > 7) {
            throw new IllegalArgumentException(
                    "Illegal values for visible days of the week: first day "
                            + firstDay + ", last day " + lastDay);
        }

        setFirstVisibleDayOfWeek(firstDay);
        setLastVisibleDayOfWeek(lastDay);
    }

    /**
     * Get the first and last visible day of the week. Returns the weekdays as
     * integers represented by {@link java.util.Calendar#DAY_OF_WEEK}
     * 
     * @deprecated Use {@link Calendar#getFirstVisibleDayOfWeek()} and
     *             {@link Calendar#getLastVisibleDayOfWeek()} instead.
     * 
     */
    @Deprecated
    public int[] getVisibleDaysOfWeek() {
        return new int[] { getFirstVisibleDayOfWeek(),
                getLastVisibleDayOfWeek() };
    }

    /**
     * <p>
     * This method restricts the weekdays that are shown. This affects both the
     * monthly and the weekly view. The general contract is that <b>firstDay <
     * lastDay</b>.
     * </p>
     * 
     * <p>
     * Note that this only affects the rendering process. Events are still
     * requested by the dates set by {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)}.
     * </p>
     * 
     * @param firstDay
     *            the first day of the week to show, between 1 and 7
     */
    public void setFirstVisibleDayOfWeek(int firstDay) {
        if (this.firstDay != firstDay && firstDay >= 1 && firstDay <= 7
                && getLastVisibleDayOfWeek() >= firstDay) {
            this.firstDay = firstDay;
            getState().setFirstVisibleDayOfWeek(firstDay);
        }
    }

    /**
     * Get the first visible day of the week. Returns the weekdays as integers
     * represented by {@link java.util.Calendar#DAY_OF_WEEK}
     * 
     * @return An integer representing the week day according to
     *         {@link java.util.Calendar#DAY_OF_WEEK}
     */
    public int getFirstVisibleDayOfWeek() {
        return firstDay;
    }

    /**
     * <p>
     * This method restricts the weekdays that are shown. This affects both the
     * monthly and the weekly view. The general contract is that <b>firstDay <
     * lastDay</b>.
     * </p>
     * 
     * <p>
     * Note that this only affects the rendering process. Events are still
     * requested by the dates set by {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)}.
     * </p>
     * 
     * @param lastDay
     *            the first day of the week to show, between 1 and 7
     */
    public void setLastVisibleDayOfWeek(int lastDay) {
        if (this.lastDay != lastDay && lastDay >= 1 && lastDay <= 7
                && getFirstVisibleDayOfWeek() <= lastDay) {
            this.lastDay = lastDay;
            getState().setLastVisibleDayOfWeek(lastDay);
        }
    }

    /**
     * Get the last visible day of the week. Returns the weekdays as integers
     * represented by {@link java.util.Calendar#DAY_OF_WEEK}
     * 
     * @return An integer representing the week day according to
     *         {@link java.util.Calendar#DAY_OF_WEEK}
     */
    public int getLastVisibleDayOfWeek() {
        return lastDay;
    }

    /**
     * <p>
     * This method restricts the hours that are shown per day. This affects the
     * weekly view. The general contract is that <b>firstHour < lastHour</b>.
     * </p>
     * 
     * <p>
     * Note that this only affects the rendering process. Events are still
     * requested by the dates set by {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)}.
     * </p>
     * 
     * @deprecated Use {@link #setFirstVisibleHourOfDay(int)} and
     *             {@link #setLastVisibleHourOfDay(int)} instead
     * 
     * @param firstHour
     *            the first hour of the day to show, between 0 and 23
     * @param lastHour
     *            the first hour of the day to show, between 0 and 23
     */
    @Deprecated
    public void setVisibleHoursOfDay(int firstHour, int lastHour) {
        if (firstHour >= lastHour || firstHour < 0 || lastHour > 23) {
            throw new IllegalArgumentException(
                    "Illegal values for visible hours of the day: first hour "
                            + firstHour + ", last hour " + lastHour);
        }

        setFirstVisibleHourOfDay(firstHour);
        setLastVisibleHourOfDay(lastHour);
    }

    /**
     * <p>
     * This method restricts the hours that are shown per day. This affects the
     * weekly view. The general contract is that <b>firstHour < lastHour</b>.
     * </p>
     * 
     * <p>
     * Note that this only affects the rendering process. Events are still
     * requested by the dates set by {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)}.
     * </p>
     * 
     * @param firstHour
     *            the first hour of the day to show, between 0 and 23
     */
    public void setFirstVisibleHourOfDay(int firstHour) {
        if (this.firstHour != firstHour && firstHour >= 0 && firstHour <= 23
                && firstHour <= getLastVisibleHourOfDay()) {
            this.firstHour = firstHour;
            getState().setFirstHourOfDay(firstHour);
        }
    }

    /**
     * Returns the first visible hour in the week view. Returns the hour using a
     * 24h time format
     * 
     */
    public int getFirstVisibleHourOfDay() {
        return firstHour;
    }

    /**
     * <p>
     * This method restricts the hours that are shown per day. This affects the
     * weekly view. The general contract is that <b>firstHour < lastHour</b>.
     * </p>
     * 
     * <p>
     * Note that this only affects the rendering process. Events are still
     * requested by the dates set by {@link #setStartDate(Date)} and
     * {@link #setEndDate(Date)}.
     * </p>
     * 
     * @param lastHour
     *            the first hour of the day to show, between 0 and 23
     */
    public void setLastVisibleHourOfDay(int lastHour) {
        if (this.lastHour != lastHour && lastHour >= 0 && lastHour <= 23
                && lastHour >= getFirstVisibleHourOfDay()) {
            this.lastHour = lastHour;
            getState().setLastHourOfDay(lastHour);
        }
    }

    /**
     * Returns the last visible hour in the week view. Returns the hour using a
     * 24h time format
     * 
     */
    public int getLastVisibleHourOfDay() {
        return lastHour;
    }

    /**
     * Gets the date caption format for the weekly view.
     * 
     * @return The pattern used in caption of dates in weekly view.
     */
    public String getWeeklyCaptionFormat() {
        return weeklyCaptionFormat;
    }

    /**
     * Sets custom date format for the weekly view. This is the caption of the
     * date. Format could be like "mmm MM/dd".
     * 
     * @param dateFormatPattern
     *            The date caption pattern.
     */
    public void setWeeklyCaptionFormat(String dateFormatPattern) {
        if ((weeklyCaptionFormat == null && dateFormatPattern != null)
                || (weeklyCaptionFormat != null && !weeklyCaptionFormat
                        .equals(dateFormatPattern))) {
            weeklyCaptionFormat = dateFormatPattern;
            markAsDirty();
        }
    }

    private DateFormat getWeeklyCaptionFormatter() {
        if (weeklyCaptionFormat != null) {
            return new SimpleDateFormat(weeklyCaptionFormat, getLocale());
        } else {
            return SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT,
                    getLocale());
        }
    }

    /**
     * Get the day of week by the given calendar and its locale
     * 
     * @param calendar
     *            The calendar to use
     * @return
     */
    private static int getDowByLocale(java.util.Calendar calendar) {
        int fow = calendar.get(java.util.Calendar.DAY_OF_WEEK);

        // monday first
        if (calendar.getFirstDayOfWeek() == java.util.Calendar.MONDAY) {
            fow = (fow == java.util.Calendar.SUNDAY) ? 7 : fow - 1;
        }

        return fow;
    }

    /**
     * Is the user allowed to trigger events which alters the events
     * 
     * @return true if the client is allowed to send changes to server
     * @see #isEventClickAllowed()
     */
    protected boolean isClientChangeAllowed() {
        return !isReadOnly() && isEnabled();
    }

    /**
     * Is the user allowed to trigger click events
     * 
     * @return true if the client is allowed to click events
     * @see #isClientChangeAllowed()
     */
    protected boolean isEventClickAllowed() {
        return isEnabled();
    }

    /**
     * Fires an event when the user selecing moving forward/backward in the
     * calendar.
     * 
     * @param forward
     *            True if the calendar moved forward else backward is assumed.
     */
    protected void fireNavigationEvent(boolean forward) {
        if (forward) {
            fireEvent(new ForwardEvent(this));
        } else {
            fireEvent(new BackwardEvent(this));
        }
    }

    /**
     * Fires an event move event to all server side move listerners
     * 
     * @param index
     *            The index of the event in the events list
     * @param newFromDatetime
     *            The changed from date time
     */
    protected void fireEventMove(int index, Date newFromDatetime) {
        MoveEvent event = new MoveEvent(this, events.get(index),
                newFromDatetime);

        if (calendarEventProvider instanceof EventMoveHandler) {
            // Notify event provider if it is an event move handler
            ((EventMoveHandler) calendarEventProvider).eventMove(event);
        }

        // Notify event move handler attached by using the
        // setHandler(EventMoveHandler) method
        fireEvent(event);
    }

    /**
     * Fires event when a week was clicked in the calendar.
     * 
     * @param week
     *            The week that was clicked
     * @param year
     *            The year of the week
     */
    protected void fireWeekClick(int week, int year) {
        fireEvent(new WeekClick(this, week, year));
    }

    /**
     * Fires event when a date was clicked in the calendar. Uses an existing
     * event from the event cache.
     * 
     * @param index
     *            The index of the event in the event cache.
     */
    protected void fireEventClick(Integer index) {
        fireEvent(new EventClick(this, events.get(index)));
    }

    /**
     * Fires event when a date was clicked in the calendar. Creates a new event
     * for the date and passes it to the listener.
     * 
     * @param date
     *            The date and time that was clicked
     */
    protected void fireDateClick(Date date) {
        fireEvent(new DateClickEvent(this, date));
    }

    /**
     * Fires an event range selected event. The event is fired when a user
     * highlights an area in the calendar. The highlighted areas start and end
     * dates are returned as arguments.
     * 
     * @param from
     *            The start date and time of the highlighted area
     * @param to
     *            The end date and time of the highlighted area
     * @param monthlyMode
     *            Is the calendar in monthly mode
     */
    protected void fireRangeSelect(Date from, Date to, boolean monthlyMode) {
        fireEvent(new RangeSelectEvent(this, from, to, monthlyMode));
    }

    /**
     * Fires an event resize event. The event is fired when a user resizes the
     * event in the calendar causing the time range of the event to increase or
     * decrease. The new start and end times are returned as arguments to this
     * method.
     * 
     * @param index
     *            The index of the event in the event cache
     * @param startTime
     *            The new start date and time of the event
     * @param endTime
     *            The new end date and time of the event
     */
    protected void fireEventResize(int index, Date startTime, Date endTime) {
        EventResize event = new EventResize(this, events.get(index), startTime,
                endTime);

        if (calendarEventProvider instanceof EventResizeHandler) {
            // Notify event provider if it is an event resize handler
            ((EventResizeHandler) calendarEventProvider).eventResize(event);
        }

        // Notify event resize handler attached by using the
        // setHandler(EventMoveHandler) method
        fireEvent(event);
    }

    /**
     * Localized display names for week days starting from sunday. Returned
     * array's length is always 7.
     * 
     * @return Array of localized weekday names.
     */
    protected String[] getDayNamesShort() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        String[] names = s.getShortWeekdays();
        List<String> weekNames = new ArrayList<String>(Arrays.asList(names));
        weekNames.remove(0);
        return weekNames.toArray(new String[7]);
    }

    /**
     * Localized display names for months starting from January. Returned
     * array's length is always 12.
     * 
     * @return Array of localized month names.
     */
    protected String[] getMonthNamesShort() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        return s.getShortMonths();
    }

    /**
     * Gets a date that is first day in the week that target given date belongs
     * to.
     * 
     * @param date
     *            Target date
     * @return Date that is first date in same week that given date is.
     */
    public Date getFirstDateForWeek(Date date) {
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        currentCalendar.setTime(date);
        while (firstDayOfWeek != currentCalendar
                .get(java.util.Calendar.DAY_OF_WEEK)) {
            currentCalendar.add(java.util.Calendar.DATE, -1);
        }
        return currentCalendar.getTime();
    }

    /**
     * Gets a date that is last day in the week that target given date belongs
     * to.
     * 
     * @param date
     *            Target date
     * @return Date that is last date in same week that given date is.
     */
    public Date getLastDateForWeek(Date date) {
        currentCalendar.setTime(date);
        currentCalendar.add(java.util.Calendar.DATE, 1);
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        // Roll to weeks last day using firstdayofweek. Roll until FDofW is
        // found and then roll back one day.
        while (firstDayOfWeek != currentCalendar
                .get(java.util.Calendar.DAY_OF_WEEK)) {
            currentCalendar.add(java.util.Calendar.DATE, 1);
        }
        currentCalendar.add(java.util.Calendar.DATE, -1);
        return currentCalendar.getTime();
    }

    /**
     * Calculates the end time of the day using the given calendar and date
     * 
     * @param date
     * @param calendar
     *            the calendar instance to be used in the calculation. The given
     *            instance is unchanged in this operation.
     * @return the given date, with time set to the end of the day
     */
    public static Date getEndOfDay(java.util.Calendar calendar, Date date) {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar
                .clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND,
                calendarClone.getActualMaximum(java.util.Calendar.MILLISECOND));
        calendarClone.set(java.util.Calendar.SECOND,
                calendarClone.getActualMaximum(java.util.Calendar.SECOND));
        calendarClone.set(java.util.Calendar.MINUTE,
                calendarClone.getActualMaximum(java.util.Calendar.MINUTE));
        calendarClone.set(java.util.Calendar.HOUR,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR));
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY,
                calendarClone.getActualMaximum(java.util.Calendar.HOUR_OF_DAY));

        return calendarClone.getTime();
    }

    /**
     * Calculates the end time of the day using the given calendar and date
     * 
     * @param date
     * @param calendar
     *            the calendar instance to be used in the calculation. The given
     *            instance is unchanged in this operation.
     * @return the given date, with time set to the end of the day
     */
    public static Date getStartOfDay(java.util.Calendar calendar, Date date) {
        java.util.Calendar calendarClone = (java.util.Calendar) calendar
                .clone();

        calendarClone.setTime(date);
        calendarClone.set(java.util.Calendar.MILLISECOND, 0);
        calendarClone.set(java.util.Calendar.SECOND, 0);
        calendarClone.set(java.util.Calendar.MINUTE, 0);
        calendarClone.set(java.util.Calendar.HOUR, 0);
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY, 0);

        return calendarClone.getTime();
    }

    /**
     * Finds the first day of the week and returns a day representing the start
     * of that day
     * 
     * @param start
     *            The actual date
     * @param expandToFullWeek
     *            Should the returned date be moved to the start of the week
     * @return If expandToFullWeek is set then it returns the first day of the
     *         week, else it returns a clone of the actual date with the time
     *         set to the start of the day
     */
    protected Date expandStartDate(Date start, boolean expandToFullWeek) {
        // If the duration is more than week, use monthly view and get startweek
        // and endweek. Example if views daterange is from tuesday to next weeks
        // wednesday->expand to monday to nextweeks sunday. If firstdayofweek =
        // monday
        if (expandToFullWeek) {
            start = getFirstDateForWeek(start);

        } else {
            start = (Date) start.clone();
        }

        // Always expand to the start of the first day to the end of the last
        // day
        start = getStartOfDay(currentCalendar, start);

        return start;
    }

    /**
     * Finds the last day of the week and returns a day representing the end of
     * that day
     * 
     * @param end
     *            The actual date
     * @param expandToFullWeek
     *            Should the returned date be moved to the end of the week
     * @return If expandToFullWeek is set then it returns the last day of the
     *         week, else it returns a clone of the actual date with the time
     *         set to the end of the day
     */
    protected Date expandEndDate(Date end, boolean expandToFullWeek) {
        // If the duration is more than week, use monthly view and get startweek
        // and endweek. Example if views daterange is from tuesday to next weeks
        // wednesday->expand to monday to nextweeks sunday. If firstdayofweek =
        // monday
        if (expandToFullWeek) {
            end = getLastDateForWeek(end);

        } else {
            end = (Date) end.clone();
        }

        // Always expand to the start of the first day to the end of the last
        // day
        end = getEndOfDay(currentCalendar, end);

        return end;
    }

    /**
     * Set the {@link com.vaadin.addon.calendar.event.CalendarEventProvider
     * CalendarEventProvider} to be used with this calendar. The EventProvider
     * is used to query for events to show, and must be non-null. By default a
     * {@link com.vaadin.addon.calendar.event.BasicEventProvider
     * BasicEventProvider} is used.
     * 
     * @param calendarEventProvider
     *            the calendarEventProvider to set. Cannot be null.
     */
    public void setEventProvider(CalendarEventProvider calendarEventProvider) {
        if (calendarEventProvider == null) {
            throw new IllegalArgumentException(
                    "Calendar event provider cannot be null");
        }

        // remove old listener
        if (getEventProvider() instanceof EventSetChangeNotifier) {
            ((EventSetChangeNotifier) getEventProvider()).removeListener(this);
        }

        this.calendarEventProvider = calendarEventProvider;

        // add new listener
        if (calendarEventProvider instanceof EventSetChangeNotifier) {
            ((EventSetChangeNotifier) calendarEventProvider).addListener(this);
        }
    }

    /**
     * @return the {@link com.vaadin.addon.calendar.event.CalendarEventProvider
     *         CalendarEventProvider} currently used
     */
    public CalendarEventProvider getEventProvider() {
        return calendarEventProvider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarEvents.EventChangeListener#eventChange
     * (com.vaadin.addon.calendar.ui.CalendarEvents.EventChange)
     */
    public void eventSetChange(EventSetChange changeEvent) {
        // sanity check
        if (calendarEventProvider == changeEvent.getProvider()) {
            markAsDirty();
        }
    }

    /**
     * Set the handler for the given type information. Mirrors
     * {@link #addListener(String, Class, Object, Method) addListener} from
     * AbstractComponent
     * 
     * @param eventId
     *            A unique id for the event. Usually one of
     *            {@link CalendarEventId}
     * @param eventType
     *            The class of the event, most likely a subclass of
     *            {@link CalendarComponentEvent}
     * @param listener
     *            A listener that listens to the given event
     * @param listenerMethod
     *            The method on the lister to call when the event is triggered
     */
    protected void setHandler(String eventId, Class<?> eventType,
            EventListener listener, Method listenerMethod) {
        if (handlers.get(eventId) != null) {
            removeListener(eventId, eventType, handlers.get(eventId));
            handlers.remove(eventId);
        }

        if (listener != null) {
            addListener(eventId, eventType, listener, listenerMethod);
            handlers.put(eventId, listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.NavigationNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardHandler)
     */
    public void setHandler(ForwardHandler listener) {
        setHandler(ForwardEvent.EVENT_ID, ForwardEvent.class, listener,
                ForwardHandler.forwardMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.NavigationNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardHandler)
     */
    public void setHandler(BackwardHandler listener) {
        setHandler(BackwardEvent.EVENT_ID, BackwardEvent.class, listener,
                BackwardHandler.backwardMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.NavigationNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickHandler)
     */
    public void setHandler(DateClickHandler listener) {
        setHandler(DateClickEvent.EVENT_ID, DateClickEvent.class, listener,
                DateClickHandler.dateClickMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.NavigationNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler)
     */
    public void setHandler(EventClickHandler listener) {
        setHandler(EventClick.EVENT_ID, EventClick.class, listener,
                EventClickHandler.eventClickMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.NavigationNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickHandler)
     */
    public void setHandler(WeekClickHandler listener) {
        setHandler(WeekClick.EVENT_ID, WeekClick.class, listener,
                WeekClickHandler.weekClickMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeHandler
     * )
     */
    public void setHandler(EventResizeHandler listener) {
        setHandler(EventResize.EVENT_ID, EventResize.class, listener,
                EventResizeHandler.eventResizeMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectHandler
     * )
     */
    public void setHandler(RangeSelectHandler listener) {
        setHandler(RangeSelectEvent.EVENT_ID, RangeSelectEvent.class, listener,
                RangeSelectHandler.rangeSelectMethod);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveNotifier
     * #addListener
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler)
     */
    public void setHandler(EventMoveHandler listener) {
        setHandler(MoveEvent.EVENT_ID, MoveEvent.class, listener,
                EventMoveHandler.eventMoveMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.CalendarEventNotifier
     * #getHandler(java.lang.String)
     */
    public EventListener getHandler(String eventId) {
        return handlers.get(eventId);
    }

    /**
     * Get the currently active drop handler
     */
    public DropHandler getDropHandler() {
        return dropHandler;
    }

    /**
     * Set the drop handler for the calendar See {@link DropHandler} for
     * implementation details.
     * 
     * @param dropHandler
     *            The drop handler to set
     */
    public void setDropHandler(DropHandler dropHandler) {
        this.dropHandler = dropHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.event.dd.DropTarget#translateDropTargetDetails(java.util.Map)
     */
    public TargetDetails translateDropTargetDetails(
            Map<String, Object> clientVariables) {
        Map<String, Object> serverVariables = new HashMap<String, Object>(1);

        if (clientVariables.containsKey("dropSlotIndex")) {
            int slotIndex = (Integer) clientVariables.get("dropSlotIndex");
            int dayIndex = (Integer) clientVariables.get("dropDayIndex");

            currentCalendar.setTime(getStartOfDay(currentCalendar, startDate));
            currentCalendar.add(java.util.Calendar.DATE, dayIndex);

            // change this if slot length is modified
            currentCalendar.add(java.util.Calendar.MINUTE, slotIndex * 30);

            serverVariables.put("dropTime", currentCalendar.getTime());

        } else {
            int dayIndex = (Integer) clientVariables.get("dropDayIndex");
            currentCalendar.setTime(expandStartDate(startDate, true));
            currentCalendar.add(java.util.Calendar.DATE, dayIndex);
            serverVariables.put("dropDay", currentCalendar.getTime());
        }

        CalendarTargetDetails td = new CalendarTargetDetails(serverVariables,
                this);
        td.setHasDropTime(clientVariables.containsKey("dropSlotIndex"));

        return td;
    }

    /**
     * Sets a container as a data source for the events in the calendar.
     * Equivalent for doing
     * <code>Calendar.setEventProvider(new ContainerEventProvider(container))</code>
     * 
     * Use this method if you are adding a container which uses the default
     * property ids like {@link BeanItemContainer} for instance. If you are
     * using custom properties instead use
     * {@link Calendar#setContainerDataSource(com.vaadin.data.Container.Indexed, Object, Object, Object, Object, Object)}
     * 
     * Please note that the container must be sorted by date!
     * 
     * @param container
     *            The container to use as a datasource
     */
    public void setContainerDataSource(Container.Indexed container) {
        ContainerEventProvider provider = new ContainerEventProvider(container);
        provider.addListener(new CalendarEventProvider.EventSetChangeListener() {
            public void eventSetChange(EventSetChange changeEvent) {
                // Repaint if events change
                markAsDirty();
            }
        });
        provider.addListener(new EventChangeListener() {
            public void eventChange(EventChange changeEvent) {
                // Repaint if event changes
                markAsDirty();
            }
        });
        setEventProvider(provider);
    }

    /**
     * Sets a container as a data source for the events in the calendar.
     * Equivalent for doing
     * <code>Calendar.setEventProvider(new ContainerEventProvider(container))</code>
     * 
     * Please note that the container must be sorted by date!
     * 
     * @param container
     *            The container to use as a data source
     * @param captionProperty
     *            The property that has the caption, null if no caption property
     *            is present
     * @param descriptionProperty
     *            The property that has the description, null if no description
     *            property is present
     * @param startDateProperty
     *            The property that has the starting date
     * @param endDateProperty
     *            The property that has the ending date
     * @param styleNameProperty
     *            The property that has the stylename, null if no stylname
     *            property is present
     */
    public void setContainerDataSource(Container.Indexed container,
            Object captionProperty, Object descriptionProperty,
            Object startDateProperty, Object endDateProperty,
            Object styleNameProperty) {
        ContainerEventProvider provider = new ContainerEventProvider(container);
        provider.setCaptionProperty(captionProperty);
        provider.setDescriptionProperty(descriptionProperty);
        provider.setStartDateProperty(startDateProperty);
        provider.setEndDateProperty(endDateProperty);
        provider.setStyleNameProperty(styleNameProperty);
        provider.addListener(new CalendarEventProvider.EventSetChangeListener() {
            public void eventSetChange(EventSetChange changeEvent) {
                // Repaint if events change
                markAsDirty();
            }
        });
        provider.addListener(new EventChangeListener() {
            public void eventChange(EventChange changeEvent) {
                // Repaint if event changes
                markAsDirty();
            }
        });
        setEventProvider(provider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEventProvider#getEvents(java.
     * util.Date, java.util.Date)
     */
    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
        return getEventProvider().getEvents(startDate, endDate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.event.CalendarEditableEventProvider#addEvent
     * (com.vaadin.addon.calendar.event.CalendarEvent)
     */
    public void addEvent(CalendarEvent event) {
        if (getEventProvider() instanceof CalendarEditableEventProvider) {
            CalendarEditableEventProvider provider = (CalendarEditableEventProvider) getEventProvider();
            provider.addEvent(event);
            markAsDirty();
        } else {
            throw new UnsupportedOperationException(
                    "Event provider does not support adding events");
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
        if (getEventProvider() instanceof CalendarEditableEventProvider) {
            CalendarEditableEventProvider provider = (CalendarEditableEventProvider) getEventProvider();
            provider.removeEvent(event);
            markAsDirty();
        } else {
            throw new UnsupportedOperationException(
                    "Event provider does not support removing events");
        }
    }

    /**
     * Adds an action handler to the calender that handles event produced by the
     * context menu.
     * 
     * <p>
     * The {@link Handler#getActions(Object, Object)} parameters depend on what
     * view the Calendar is in:
     * <ul>
     * <li>If the Calendar is in <i>Day or Week View</i> then the target
     * parameter will be a {@link CalendarDateRange} with a range of
     * half-an-hour. The {@link Handler#getActions(Object, Object)} method will
     * be called once per half-hour slot.</li>
     * <li>If the Calendar is in <i>Month View</i> then the target parameter
     * will be a {@link CalendarDateRange} with a range of one day. The
     * {@link Handler#getActions(Object, Object)} will be called once for each
     * day.
     * </ul>
     * The Dates passed into the {@link CalendarDateRange} are in the same
     * timezone as the calendar is.
     * </p>
     * 
     * <p>
     * The {@link Handler#handleAction(Action, Object, Object)} parameters
     * depend on what the context menu is called upon:
     * <ul>
     * <li>If the context menu is called upon an event then the target parameter
     * is the event, i.e. instanceof {@link CalendarEvent}</li>
     * <li>If the context menu is called upon an empty slot then the target is a
     * {@link Date} representing that slot
     * </ul>
     * </p>
     */
    public void addActionHandler(Handler actionHandler) {
        if (actionHandler != null) {
            if (actionHandlers == null) {
                actionHandlers = new LinkedList<Action.Handler>();
                actionMapper = new KeyMapper<Action>();
            }
            if (!actionHandlers.contains(actionHandler)) {
                actionHandlers.add(actionHandler);
                markAsDirty();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.event.Action.Container#removeActionHandler(com.vaadin.event
     * .Action.Handler)
     */
    public void removeActionHandler(Handler actionHandler) {
        if (actionHandlers != null && actionHandlers.contains(actionHandler)) {
            actionHandlers.remove(actionHandler);
            if (actionHandlers.isEmpty()) {
                actionHandlers = null;
                actionMapper = null;
            }
            markAsDirty();
        }
    }

    private class CalendarServerRpcImpl implements CalendarServerRpc {

        @Override
        public void eventMove(int eventIndex, String newDate) {
            if (!isClientChangeAllowed()) {
                return;
            }
            if (newDate != null) {
                try {
                    Date d = df_date_time.parse(newDate);
                    if (eventIndex >= 0 && eventIndex < events.size()
                            && events.get(eventIndex) != null) {
                        fireEventMove(eventIndex, d);
                    }
                } catch (ParseException e) {
                    getLogger().log(Level.WARNING, e.getMessage());
                }
            }
        }

        @Override
        public void rangeSelect(String range) {
            if (!isClientChangeAllowed()) {
                return;
            }

            if (range != null && range.length() > 14 && range.contains("TO")) {
                String[] dates = range.split("TO");
                try {
                    Date d1 = df_date.parse(dates[0]);
                    Date d2 = df_date.parse(dates[1]);

                    fireRangeSelect(d1, d2, true);

                } catch (ParseException e) {
                    // NOP
                }
            } else if (range != null && range.length() > 12
                    && range.contains(":")) {
                String[] dates = range.split(":");
                if (dates.length == 3) {
                    try {
                        Date d = df_date.parse(dates[0]);
                        currentCalendar.setTime(d);
                        int startMinutes = Integer.parseInt(dates[1]);
                        int endMinutes = Integer.parseInt(dates[2]);
                        currentCalendar.add(java.util.Calendar.MINUTE,
                                startMinutes);
                        Date start = currentCalendar.getTime();
                        currentCalendar.add(java.util.Calendar.MINUTE,
                                endMinutes - startMinutes);
                        Date end = currentCalendar.getTime();
                        fireRangeSelect(start, end, false);
                    } catch (ParseException e) {
                        // NOP
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            }
        }

        @Override
        public void forward() {
            fireEvent(new ForwardEvent(Calendar.this));
        }

        @Override
        public void backward() {
            fireEvent(new BackwardEvent(Calendar.this));
        }

        @Override
        public void dateClick(String date) {
            if (!isClientChangeAllowed()) {
                return;
            }
            if (date != null && date.length() > 6) {
                try {
                    Date d = df_date.parse(date);
                    fireDateClick(d);
                } catch (ParseException e) {
                }
            }
        }

        @Override
        public void weekClick(String event) {
            if (!isClientChangeAllowed()) {
                return;
            }
            if (event.length() > 0 && event.contains("w")) {
                String[] splitted = event.split("w");
                if (splitted.length == 2) {
                    try {
                        int yr = 1900 + Integer.parseInt(splitted[0]);
                        int week = Integer.parseInt(splitted[1]);
                        fireWeekClick(week, yr);
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            }
        }

        @Override
        public void eventClick(int eventIndex) {
            if (!isEventClickAllowed()) {
                return;
            }
            if (eventIndex >= 0 && eventIndex < events.size()
                    && events.get(eventIndex) != null) {
                fireEventClick(eventIndex);
            }
        }

        @Override
        public void eventResize(int eventIndex, String newStartDate,
                String newEndDate) {
            if (!isClientChangeAllowed()) {
                return;
            }
            if (newStartDate != null && !"".equals(newStartDate)
                    && newEndDate != null && !"".equals(newEndDate)) {
                try {
                    Date newStartTime = df_date_time.parse(newStartDate);
                    Date newEndTime = df_date_time.parse(newEndDate);

                    fireEventResize(eventIndex, newStartTime, newEndTime);
                } catch (ParseException e) {
                    // NOOP
                }
            }
        }

        @Override
        public void scroll(int scrollPosition) {
            scrollTop = scrollPosition;
        }

        @Override
        public void actionOnEmptyCell(String actionKey, String startDate,
                String endDate) {
            Action action = actionMapper.get(actionKey);
            SimpleDateFormat formatter = new SimpleDateFormat(
                    VCalendarAction.ACTION_DATE_FORMAT_PATTERN);
            formatter.setTimeZone(getTimeZone());
            try {
                Date start = formatter.parse(startDate);
                for (Action.Handler ah : actionHandlers) {
                    ah.handleAction(action, this, start);
                }

            } catch (ParseException e) {
                getLogger().log(Level.WARNING,
                        "Could not parse action date string");
            }

        }

        @Override
        public void actionOnEvent(String actionKey, String startDate,
                String endDate, int eventIndex) {
            Action action = actionMapper.get(actionKey);
            SimpleDateFormat formatter = new SimpleDateFormat(
                    VCalendarAction.ACTION_DATE_FORMAT_PATTERN);
            formatter.setTimeZone(getTimeZone());
            for (Action.Handler ah : actionHandlers) {
                ah.handleAction(action, this, events.get(eventIndex));
            }
        }
    }
}