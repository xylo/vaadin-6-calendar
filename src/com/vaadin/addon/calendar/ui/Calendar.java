package com.vaadin.addon.calendar.ui;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChange;
import com.vaadin.addon.calendar.event.CalendarEventProvider.EventSetChangeNotifier;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
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
import com.vaadin.event.ComponentEventListener;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetails;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

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
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 */
@ClientWidget(VCalendar.class)
public class Calendar extends AbstractComponent implements
        CalendarComponentEvents.NavigationNotifier,
        CalendarComponentEvents.EventMoveNotifier,
        CalendarComponentEvents.RangeSelectNotifier,
        CalendarComponentEvents.EventResizeNotifier,
        CalendarEventProvider.EventSetChangeListener, DropTarget {

    private static final long serialVersionUID = -1858262705387350736L;

    /**
     * Calendar can use either 12 hours clock or 24 hours clock.
     */
    public enum TimeFormat {

        Format12H(), Format24H();
    }

    /** Defines weekend days visibility. */
    private boolean hideWeekends = false;

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
    protected DateFormat df_date_time = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    /**
     * Week view's scroll position. Client sends updates to this value so that
     * scroll position wont reset all the time.
     */
    private int scrollTop = 0;

    /** Custom caption format for weekly and date views */
    private SimpleDateFormat weeklyCaptionFormat = (SimpleDateFormat) SimpleDateFormat
            .getDateInstance();

    /** Map from event ids to event handlers */
    private Map<String, ComponentEventListener> handlers;

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
     * @param calendarEventProvider
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
     * @param calendarEventProvider
     *            Event provider, cannot be null.
     */
    // this is the constructor every other constuctor calls
    public Calendar(String caption, CalendarEventProvider eventProvider) {
        setEventProvider(eventProvider);
        setCaption(caption);

        handlers = new HashMap<String, ComponentEventListener>();

        setDefaultHandlers();
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
            requestRepaint();
        }
    }

    /**
     * Gets the calendar's end date.
     * 
     * @return Last visible date.
     */
    public Date getEndDate() {
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
            requestRepaint();
        } else if (!date.equals(endDate)) {
            endDate = date;
            requestRepaint();
        }
    }

    /**
     * Sets the locale to be used in the Calendar component.
     * 
     * @see com.vaadin.ui.AbstractComponent#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale l) {
        weeklyCaptionFormat = (SimpleDateFormat) SimpleDateFormat
                .getDateInstance(SimpleDateFormat.SHORT, l);
        if (timezone != null) {
            currentCalendar = java.util.Calendar.getInstance(timezone, l);

        } else {
            currentCalendar = java.util.Calendar.getInstance(l);
        }

        super.setLocale(l);
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
        requestRepaint();
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
            requestRepaint();
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

    public void setVisibleDaysOfWeek(int firstDay, int lastDay) {
        if (this.firstDay != firstDay || this.lastDay != lastDay) {
            this.firstDay = firstDay;
            this.lastDay = lastDay;
            requestRepaint();
        }
    }

    public int[] getVisibleDaysOfWeek() {
        return new int[] { firstDay, lastDay };
    }

    public void setVisibleHoursOfDay(int firstHour, int lastHour) {
        if (this.firstHour != firstHour || this.lastHour != lastHour) {
            this.firstHour = firstHour;
            this.lastHour = lastHour;
            requestRepaint();
        }
    }

    /**
     * Gets the date caption format for the weekly view.
     * 
     * @return The pattern used in caption of dates in weekly view.
     */
    public String getWeeklyCaptionFormat() {
        return weeklyCaptionFormat.toPattern();
    }

    /**
     * Sets custom date format for the weekly view. This is the caption of the
     * date. Format could be like "mmm MM/dd".
     * 
     * @param dateFormatPattern
     *            The date caption pattern.
     */
    public void setWeeklyCaptionFormat(String dateFormatPattern) {
        if (!weeklyCaptionFormat.toPattern().equals(dateFormatPattern)) {
            weeklyCaptionFormat.applyPattern(dateFormatPattern);
            requestRepaint();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.ui.AbstractComponent#paintContent(com.vaadin.terminal.PaintTarget
     * )
     */
    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        // Make sure we have a up-to-date locale
        setLocale(getLocale());

        // If only one is null, throw exception
        // If both are null, set defaults
        if (startDate == null ^ endDate == null) {
            String message = "Schedule cannot be painted without a proper date range.\n";
            if (startDate == null) {
                throw new PaintException(message
                        + "You must set a start date using setStartDate(Date).");

            } else {
                throw new PaintException(message
                        + "You must set an end date using setEndDate(Date).");
            }

        } else if (startDate == null && endDate == null) {
            // set defaults
            currentCalendar.setTime(new Date());
            currentCalendar.set(java.util.Calendar.DAY_OF_WEEK, currentCalendar
                    .getFirstDayOfWeek());
            startDate = currentCalendar.getTime();

            currentCalendar.add(java.util.Calendar.DAY_OF_WEEK, 6);
            endDate = currentCalendar.getTime();
        }

        int durationInDays = (int) (((endDate.getTime()) - startDate.getTime()) / VCalendar.DAYINMILLIS);
        durationInDays++;
        if (durationInDays > 60) {
            throw new PaintException("Daterange is too big (max 60) = "
                    + durationInDays);
        }

        target.addAttribute(VCalendar.ATTR_FORMAT24H,
                getTimeFormat() == TimeFormat.Format24H);
        target.addAttribute(VCalendar.ATTR_DAY_NAMES, getDayNamesShort());
        target.addAttribute(VCalendar.ATTR_MONTH_NAMES, getMonthNamesShort());
        target.addAttribute(VCalendar.ATTR_FDOW, currentCalendar
                .getFirstDayOfWeek());
        target.addAttribute(VCalendar.ATTR_READONLY, isReadOnly());
        // target.addAttribute(VCalendar.ATTR_HIDE_WEEKENDS, isHideWeekends());

        target.addAttribute(VCalendar.ATTR_FIRSTDAYOFWEEK, firstDay);
        target.addAttribute(VCalendar.ATTR_LASTDAYOFWEEK, lastDay);

        target.addAttribute(VCalendar.ATTR_FIRSTHOUROFDAY, firstHour);
        target.addAttribute(VCalendar.ATTR_LASTHOUROFDAY, lastHour);

        // Use same timezone in all dates this component handles.
        // Show "now"-marker in browser within given timezone.
        Date now = new Date();
        currentCalendar.setTime(now);
        now = currentCalendar.getTime();

        // Reset time zones for custom date formats
        df_date.setTimeZone(currentCalendar.getTimeZone());
        df_time.setTimeZone(currentCalendar.getTimeZone());
        target.addAttribute(VCalendar.ATTR_NOW, df_date.format(now) + " "
                + df_time.format(now));

        Date firstDateToShow = expandStartDate(startDate, durationInDays > 7);
        Date lastDateToShow = expandEndDate(endDate, durationInDays > 7);

        currentCalendar.setTime(firstDateToShow);

        target.startTag("days");
        // Send all dates to client from server. This
        // approach was taken because gwt doesn't
        // support date localization properly.
        while (currentCalendar.getTime().compareTo(lastDateToShow) < 1) {
            // int dow = currentCalendar.get(java.util.Calendar.DAY_OF_WEEK) -
            // 1;
            //
            // if (dow >= firstDay && dow <= lastDay) {
            target.startTag("day");
            target.addAttribute(VCalendar.ATTR_DATE, df_date
                    .format(currentCalendar.getTime()));
            target.addAttribute(VCalendar.ATTR_FDATE, weeklyCaptionFormat
                    .format(currentCalendar.getTime()));
            target.addAttribute(VCalendar.ATTR_DOW, currentCalendar
                    .get(java.util.Calendar.DAY_OF_WEEK));
            target.addAttribute(VCalendar.ATTR_WEEK, currentCalendar
                    .get(java.util.Calendar.WEEK_OF_YEAR));
            target.endTag("day");
            currentCalendar.add(java.util.Calendar.DATE, 1);
            // }
        }

        target.endTag("days");

        events = getEventProvider().getEvents(firstDateToShow, lastDateToShow);
        target.startTag("events");
        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                target.startTag("event");
                paintEvent(i, target);
                target.endTag("event");
            }
        }
        target.endTag("events");
        target.addVariable(this, VCalendar.ATTR_SCROLL, scrollTop);
        target.addVariable(this, "navigation", 0);

        if (dropHandler != null) {
            dropHandler.getAcceptCriterion().paint(target);
        }
        super.paintContent(target);
    }

    /**
     * Paints single calendar event to UIDL. Override this method to add custom
     * attributes for events.
     * 
     * @param i
     *            Index of target Calendar.Event
     * @param target
     *            PaintTarget
     */
    protected void paintEvent(int i, PaintTarget target) throws PaintException {
        CalendarEvent e = events.get(i);
        target.addAttribute(VCalendar.ATTR_INDEX, i);
        target.addAttribute(VCalendar.ATTR_CAPTION,
                (e.getCaption() == null ? "" : e.getCaption()));
        target.addAttribute(VCalendar.ATTR_DATEFROM, df_date.format(e
                .getStart()));
        target.addAttribute(VCalendar.ATTR_DATETO, df_date.format(e.getEnd()));
        target.addAttribute(VCalendar.ATTR_TIMEFROM, df_time.format(e
                .getStart()));
        target.addAttribute(VCalendar.ATTR_TIMETO, df_time.format(e.getEnd()));
        target.addAttribute(VCalendar.ATTR_DESCRIPTION,
                e.getDescription() == null ? "" : e.getDescription());
        target.addAttribute(VCalendar.ATTR_STYLE, e.getStyleName() == null ? ""
                : e.getStyleName());
        target.addAttribute(VCalendar.ATTR_ALLDAY, e.isAllDay());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractComponent#changeVariables(java.lang.Object,
     * java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);

        if (variables.containsKey(CalendarEventId.RANGESELECT)
                && isClientChangeAllowed()) {
            handleRangeSelect((String) variables
                    .get(CalendarEventId.RANGESELECT));
        }

        if (variables.containsKey(CalendarEventId.EVENTCLICK)
                && isClientChangeAllowed()) {
            handleEventClick((Integer) variables
                    .get(CalendarEventId.EVENTCLICK));
        }

        if (variables.containsKey(CalendarEventId.DATECLICK)
                && isClientChangeAllowed()) {
            handleDateClick((String) variables.get(CalendarEventId.DATECLICK));
        }

        if (variables.containsKey(CalendarEventId.WEEKCLICK)
                && isClientChangeAllowed()) {
            handleWeekClick((String) variables.get(CalendarEventId.WEEKCLICK));
        }

        if (variables.containsKey(VCalendar.ATTR_SCROLL)) {
            handleScroll(variables.get(VCalendar.ATTR_SCROLL).toString());
        }

        if (variables.containsKey(CalendarEventId.EVENTMOVE)
                && isClientChangeAllowed()) {
            handleEventMove(variables.get(CalendarEventId.EVENTMOVE).toString());
        }

        if (variables.containsKey(VCalendar.ATTR_NAVIGATION)) {
            handleNavigation((Boolean) variables.get("navigation"));
        }

        if (variables.containsKey(CalendarEventId.EVENTRESIZE)
                && isClientChangeAllowed()) {
            handleEventResize((String) variables
                    .get(CalendarEventId.EVENTRESIZE));
        }
    }

    /**
     * @return true if the client is allowed to send changes to server
     */
    protected boolean isClientChangeAllowed() {
        return !isReadOnly() && isEnabled();
    }

    /*
     * Handle an event move message from client.
     */
    private void handleEventMove(String message) {
        if (message != null && message.length() > 10) {
            String[] splitted = message.split(":");
            if (splitted.length == 2) {
                int index = Integer.parseInt(splitted[0]);

                try {
                    Date d = df_date_time.parse(splitted[1]);
                    if (index >= 0 && index < events.size()
                            && events.get(index) != null) {
                        fireEventMove(index, d);
                    }
                } catch (ParseException e) {
                    // NOP
                } catch (NumberFormatException e) {
                    // NOP
                }
            }
        }
    }

    /*
     * Handle a range select message from client.
     */
    private void handleRangeSelect(String value) {
        if (value != null && value.length() > 14 && value.contains("TO")) {
            String[] dates = value.split("TO");
            try {
                Date d1 = df_date.parse(dates[0]);
                Date d2 = df_date.parse(dates[1]);

                fireRangeSelect(d1, d2, true);

            } catch (ParseException e) {
                // NOP
            }
            requestRepaint();
        } else if (value != null && value.length() > 12 && value.contains(":")) {
            String[] dates = value.split(":");
            if (dates.length == 3) {
                try {
                    Date d = df_date.parse(dates[0]);
                    currentCalendar.setTime(d);
                    int startMinutes = Integer.parseInt(dates[1]);
                    int endMinutes = Integer.parseInt(dates[2]);
                    currentCalendar
                            .add(java.util.Calendar.MINUTE, startMinutes);
                    Date start = currentCalendar.getTime();
                    currentCalendar.add(java.util.Calendar.MINUTE, endMinutes
                            - startMinutes);
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

    /*
     * Handle an event click message from client.
     */
    private void handleEventClick(Integer i) {
        if (i >= 0 && i < events.size() && events.get(i) != null) {
            fireEventClick(i);
        }
    }

    /*
     * Handle a date click message from client.
     */
    private void handleDateClick(String message) {
        if (message != null && message.length() > 6) {
            try {
                Date d = df_date.parse(message);
                fireDateClick(d);
            } catch (ParseException e) {
            }
        }
    }

    /*
     * Handle a week message from client.
     */
    private void handleWeekClick(String s) {
        if (s.length() > 0 && s.contains("w")) {
            String[] splitted = s.split("w");
            if (splitted.length == 2) {
                try {
                    int yr = 1900 + Integer.parseInt(splitted[0]);
                    int week = Integer.parseInt(splitted[1]);
                    fireWeekClick(week, yr);
                } catch (NumberFormatException e) {

                }
            }
        }
    }

    /*
     * Handle a scroll message from client.
     */
    private void handleScroll(String varValue) {
        try {
            int i = Integer.parseInt(varValue);
            scrollTop = i;
        } catch (NumberFormatException e) {
            // NOP
        }
    }

    /*
     * Handle a navigation message from client.
     */
    private void handleNavigation(Boolean forward) {
        fireNavigationEvent(forward);
    }

    /*
     * Handle an event resize message from client.
     */
    private void handleEventResize(String value) {
        if (value != null && !"".equals(value)) {
            try {
                String[] values = value.split(",");
                if (values.length == 3) {
                    int eventIndex = Integer.parseInt(values[0]);
                    Date newStartTime = df_date_time.parse(values[1]);
                    Date newEndTime = df_date_time.parse(values[2]);

                    fireEventResize(eventIndex, newStartTime, newEndTime);
                }
            } catch (NumberFormatException e) {
                // NOOP
            } catch (ParseException e) {
                // NOOP
            }
        }
    }

    protected void fireNavigationEvent(boolean forward) {
        if (forward) {
            fireEvent(new ForwardEvent(this));
        } else {
            fireEvent(new BackwardEvent(this));
        }
    }

    protected void fireEventMove(int index, Date newFromDatetime) {
        fireEvent(new MoveEvent(this, events.get(index), newFromDatetime));

        // make sure the result of the move event is painted back
        requestRepaint();
    }

    protected void fireWeekClick(int week, int year) {
        fireEvent(new WeekClick(this, week, year));
    }

    protected void fireEventClick(Integer i) {
        fireEvent(new EventClick(this, events.get(i)));
    }

    protected void fireDateClick(Date d) {
        fireEvent(new DateClickEvent(this, d));
    }

    protected void fireRangeSelect(Date from, Date to, boolean monthlyMode) {
        fireEvent(new RangeSelectEvent(this, from, to, monthlyMode));
    }

    protected void fireEventResize(int index, Date startTime, Date endTime) {
        fireEvent(new EventResize(this, events.get(index), startTime, endTime));

        // make sure the result of the resize event is painted back
        requestRepaint();
    }

    /**
     * Localized display names for week days starting from sunday. Returned
     * array's length is always 7.
     * 
     * @return Array of localized weekday names.
     */
    @SuppressWarnings("unchecked")
    protected String[] getDayNamesShort() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        String[] names = s.getShortWeekdays();
        List<String> weekNames = new ArrayList(Arrays.asList(names));
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
     * @param dateInWeek
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
        calendarClone.set(java.util.Calendar.MILLISECOND, calendarClone
                .getActualMaximum(java.util.Calendar.MILLISECOND));
        calendarClone.set(java.util.Calendar.SECOND, calendarClone
                .getActualMaximum(java.util.Calendar.SECOND));
        calendarClone.set(java.util.Calendar.MINUTE, calendarClone
                .getActualMaximum(java.util.Calendar.MINUTE));
        calendarClone.set(java.util.Calendar.HOUR, calendarClone
                .getActualMaximum(java.util.Calendar.HOUR));
        calendarClone.set(java.util.Calendar.HOUR_OF_DAY, calendarClone
                .getActualMaximum(java.util.Calendar.HOUR_OF_DAY));

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
            requestRepaint();
        }
    }

    /**
     * Set the handler for the given type information. Mirrors
     * {@link #addListener(String, Class, Object, Method) addListener} from
     * AbstractComponent
     * 
     * @param eventId
     * @param eventType
     * @param listener
     * @param listenerMethod
     */
    protected void setHandler(String eventId, Class<?> eventType,
            ComponentEventListener listener, Method listenerMethod) {
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
    public ComponentEventListener getHandler(String eventId) {
        return handlers.get(eventId);
    }

    /* Drag and Drop related */

    public DropHandler getDropHandler() {
        return dropHandler;
    }

    public void setDropHandler(DropHandler dropHandler) {
        this.dropHandler = dropHandler;
    }

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
}
