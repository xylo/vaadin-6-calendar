package com.vaadin.addon.calendar.ui;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardListener;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickListener;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChange;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventChangeNotifier;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickListener;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveListener;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResizeListener;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardListener;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectListener;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

/**
 * Vaadin Calendar is for visualizing events in calendar. Only
 * {@link java.util.GregorianCalendar GregorianCalendar} is supported. Calendar
 * events can be visualized in the variable length view depending on the start
 * and end dates.<br/>
 * <li>You must set view's date range with <code>setStartDate</code> and
 * <code>setEndDate</code> methods, otherwise schedule will be empty <li>
 * Calendar has two kind of views: monthly and weekly view<li>If date range is
 * seven days long or smaller, weekly view is used.
 */
@ClientWidget(VCalendar.class)
public class Calendar extends AbstractComponent implements
        CalendarComponentEvents.NavigationNotifier,
        CalendarComponentEvents.EventMoveNotifier,
        CalendarComponentEvents.RangeSelectNotifier,
        CalendarComponentEvents.EventResizeNotifier,
        CalendarComponentEvents.EventChangeListener {

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

    /** Internal calendar data source. Always a GregorianCalendar. */
    protected java.util.Calendar currentCalendar = new GregorianCalendar();

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
    private List<CalendarEvent> events;

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
     *            Event provider.
     */
    public Calendar(CalendarEventProvider calendarEventProvider) {
        this.setCalendarEventProvider(calendarEventProvider);
        setSizeFull();
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
     * Sets start date for the calendar.
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
            currentCalendar = new GregorianCalendar(timezone, l);

        } else {
            currentCalendar = new GregorianCalendar(l);
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
            if (p.indexOf("HH") != -1 || p.indexOf("H") != -1)
                return TimeFormat.Format24H;
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
        if (timezone == null)
            return currentCalendar.getTimeZone();
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
            if (zone == null)
                zone = TimeZone.getDefault();
            currentCalendar.setTimeZone(zone);
            df_date_time.setTimeZone(zone);
            requestRepaint();
        }
    }

    /**
     * Returns status of weekend days visibility. Default is false.
     * 
     * @return True when weekends are hidden. False when not.
     */
    public boolean isHideWeekends() {
        return hideWeekends;
    }

    /**
     * Sets the weekend visibility.
     * 
     * @param hideWeekends
     *            True when weekends should be hidden. False when not.
     */
    public void setHideWeekends(boolean hideWeekends) {
        if (hideWeekends != this.hideWeekends) {
            this.hideWeekends = hideWeekends;
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
            currentCalendar.set(GregorianCalendar.DAY_OF_WEEK, currentCalendar
                    .getFirstDayOfWeek());
            startDate = currentCalendar.getTime();

            currentCalendar.add(GregorianCalendar.DAY_OF_WEEK, 6);
            endDate = currentCalendar.getTime();
        }

        int durationInDays = (int) (((endDate.getTime()) - startDate.getTime()) / VCalendar.DAYINMILLIS);
        durationInDays++;
        if (durationInDays > 60) {
            throw new PaintException("Daterange is too big (max 60) = "
                    + durationInDays);
        }

        target.addAttribute("format24h",
                getTimeFormat() == TimeFormat.Format24H);
        target.addAttribute("dayNames", getDayNamesShort());
        target.addAttribute("monthNames", getMonthNamesShort());
        target.addAttribute("fdow", currentCalendar.getFirstDayOfWeek());
        target.addAttribute("readonly", isReadOnly());
        target.addAttribute("hideWeekends", isHideWeekends());
        // Use same timezone in all dates this component handles.
        // Show "now"-marker in browser within given timezone.
        Date now = new Date();
        currentCalendar.setTime(now);
        now = currentCalendar.getTime();

        // Reset time zones for custom date formats
        df_date.setTimeZone(currentCalendar.getTimeZone());
        df_time.setTimeZone(currentCalendar.getTimeZone());
        target.addAttribute("now", df_date.format(now) + " "
                + df_time.format(now));

        Date firstDateToShow = null;
        Date lastDateToShow = null;

        // If the duration is more than week, use monthly view and get startweek
        // and endweek. Example if views daterange is from tuesday to next weeks
        // wednesday->expand to monday to nextweeks sunday. If firstdayofweek =
        // monday

        // If the duration is one day, we expand the start and end to cover the
        // whole day.
        if (durationInDays > 7) {
            firstDateToShow = getFirstDateForWeek(startDate);
            lastDateToShow = getLastDateForWeek(endDate);

        } else if (durationInDays == 1) {
            firstDateToShow = getStartOfDay(startDate);
            lastDateToShow = getEndOfDay(endDate);

        } else {
            firstDateToShow = (Date) startDate.clone();
            lastDateToShow = (Date) endDate.clone();
        }
        currentCalendar.setTime(firstDateToShow);

        target.startTag("days");
        // Send all dates to client from server. This
        // approach was taken because gwt doesn't
        // support date localization properly.
        while (currentCalendar.getTime().compareTo(lastDateToShow) < 1) {
            target.startTag("day");
            target.addAttribute("date", df_date.format(currentCalendar
                    .getTime()));
            target.addAttribute("fdate", weeklyCaptionFormat
                    .format(currentCalendar.getTime()));
            target.addAttribute("dow", currentCalendar
                    .get(java.util.Calendar.DAY_OF_WEEK));
            target.addAttribute("w", currentCalendar
                    .get(java.util.Calendar.WEEK_OF_YEAR));
            target.endTag("day");
            currentCalendar.add(java.util.Calendar.DATE, 1);
        }

        target.endTag("days");

        events = getCalendarEventProvider().getEvents(firstDateToShow,
                lastDateToShow);
        target.startTag("events");
        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                target.startTag("event");
                paintEvent(i, target);
                target.endTag("event");
            }
        }
        target.endTag("events");
        target.addVariable(this, "scroll", scrollTop);
        target.addVariable(this, "navigation", 0);
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
        target.addAttribute("i", i);
        target.addAttribute("caption", e.getCaption());
        target.addAttribute("dfrom", df_date.format(e.getStart()));
        target.addAttribute("dto", df_date.format(e.getEnd()));
        target.addAttribute("tfrom", df_time.format(e.getStart()));
        target.addAttribute("tto", df_time.format(e.getEnd()));
        target.addAttribute("description", e.getDescription() == null ? "" : e
                .getDescription());
        target.addAttribute("extracss", e.getStyleName() == null ? "" : e
                .getStyleName());
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

        if (variables.containsKey(CalendarEventId.RANGESELECT) && !isReadOnly()) {
            handleRangeSelect((String) variables
                    .get(CalendarEventId.RANGESELECT));
        }

        if (variables.containsKey(CalendarEventId.EVENTCLICK)) {
            handleEventClick((Integer) variables
                    .get(CalendarEventId.EVENTCLICK));
        }

        if (variables.containsKey(CalendarEventId.DATECLICK)) {
            handleDateClick((String) variables.get(CalendarEventId.DATECLICK));
        }

        if (variables.containsKey(CalendarEventId.WEEKCLICK)) {
            handleWeekClick((String) variables.get(CalendarEventId.WEEKCLICK));
        }

        if (variables.containsKey("scroll")) {
            handleScroll(variables.get("scroll").toString());
        }

        if (variables.containsKey(CalendarEventId.EVENTMOVE) && !isReadOnly()) {
            handleEventMove(variables.get(CalendarEventId.EVENTMOVE).toString());
        }

        if (variables.containsKey("navigation")) {
            handleNavigation((Integer) variables.get("navigation"));
        }

        if (variables.containsKey(CalendarEventId.EVENTRESIZE) && !isReadOnly()) {
            handleEventResize((String) variables
                    .get(CalendarEventId.EVENTRESIZE));
        }
    }

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

    private void handleEventClick(Integer i) {
        if (i >= 0 && i < events.size() && events.get(i) != null) {
            fireEventClick(i);
        }
    }

    private void handleDateClick(String message) {
        if (message != null && message.length() > 6) {
            try {
                Date d = df_date.parse(message);
                fireDateClick(d);
            } catch (ParseException e) {
            }
        }
    }

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

    private void handleScroll(String varValue) {
        try {
            int i = Integer.parseInt(varValue);
            scrollTop = i;
        } catch (NumberFormatException e) {
            // NOP
        }
    }

    private void handleNavigation(Integer integer) {
        int index = integer;
        int durationInDays = (int) (((endDate.getTime()) - startDate.getTime()) / VCalendar.DAYINMILLIS);
        durationInDays++;
        if (index == -1) {
            durationInDays = -durationInDays;
        }
        currentCalendar.setTime(startDate);
        currentCalendar.add(java.util.Calendar.DATE, durationInDays);
        startDate = currentCalendar.getTime();
        currentCalendar.setTime(endDate);
        currentCalendar.add(java.util.Calendar.DATE, durationInDays);
        endDate = currentCalendar.getTime();
        requestRepaint();
        fireNavigationEvent(index != -1);
    }

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
        if (forward)
            fireEvent(new ForwardEvent(this));
        else
            fireEvent(new BackwardEvent(this));
    }

    protected void fireEventMove(int index, Date newFromDatetime) {
        fireEvent(new MoveEvent(this, events.get(index), newFromDatetime));
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
     * Get the end time of the day for the given date
     * 
     * @param date
     * @return
     */
    public Date getEndOfDay(Date date) {
        currentCalendar.setTime(date);
        currentCalendar.set(GregorianCalendar.MILLISECOND, currentCalendar
                .getActualMaximum(GregorianCalendar.MILLISECOND));
        currentCalendar.set(GregorianCalendar.SECOND, currentCalendar
                .getActualMaximum(GregorianCalendar.SECOND));
        currentCalendar.set(GregorianCalendar.MINUTE, currentCalendar
                .getActualMaximum(GregorianCalendar.MINUTE));
        currentCalendar.set(GregorianCalendar.HOUR, currentCalendar
                .getActualMaximum(GregorianCalendar.HOUR));
        currentCalendar.set(GregorianCalendar.HOUR_OF_DAY, currentCalendar
                .getActualMaximum(GregorianCalendar.HOUR_OF_DAY));

        return currentCalendar.getTime();
    }

    /**
     * Get the start time of the day for the given date
     * 
     * @param date
     * @return
     */
    public Date getStartOfDay(Date date) {
        currentCalendar.setTime(date);
        currentCalendar.set(GregorianCalendar.MILLISECOND, 0);
        currentCalendar.set(GregorianCalendar.SECOND, 0);
        currentCalendar.set(GregorianCalendar.MINUTE, 0);
        currentCalendar.set(GregorianCalendar.HOUR, 0);
        currentCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);

        return currentCalendar.getTime();
    }

    /**
     * @param calendarEventProvider
     *            the calendarEventProvider to set
     */
    public void setCalendarEventProvider(CalendarEventProvider calendarEventProvider) {
        // remove old listener
        if (getCalendarEventProvider() instanceof EventChangeNotifier) {
            ((EventChangeNotifier) getCalendarEventProvider())
                    .removeListener(this);
        }

        this.calendarEventProvider = calendarEventProvider;

        // add new listener
        if (calendarEventProvider instanceof EventChangeNotifier) {
            ((EventChangeNotifier) calendarEventProvider).addListener(this);
        }
    }

    /**
     * @return the calendarEventProvider
     */
    public CalendarEventProvider getCalendarEventProvider() {
        return calendarEventProvider;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarEvents.EventChangeListener#eventChange
     * (com.vaadin.addon.calendar.ui.CalendarEvents.EventChange)
     */
    public void eventChange(EventChange changeEvent) {
        // sanity check
        if (calendarEventProvider == changeEvent.getProvider()) {
            requestRepaint();
        }
    }

    public void addListener(ForwardListener listener) {
        addListener(ForwardEvent.EVENT_ID, ForwardEvent.class, listener,
                ForwardListener.forwardMethod);
    }

    public void addListener(BackwardListener listener) {
        addListener(BackwardEvent.EVENT_ID, BackwardEvent.class, listener,
                BackwardListener.backwardMethod);
    }

    public void addListener(DateClickListener listener) {
        addListener(DateClickEvent.EVENT_ID, DateClickEvent.class, listener,
                DateClickListener.dateClickMethod);
    }

    public void addListener(EventClickListener listener) {
        addListener(EventClick.EVENT_ID, EventClick.class, listener,
                EventClickListener.eventClickMethod);
    }

    public void addListener(WeekClickListener listener) {
        addListener(WeekClick.EVENT_ID, WeekClick.class, listener,
                WeekClickListener.weekClickMethod);
    }

    public void addListener(EventResizeListener listener) {
        addListener(EventResize.EVENT_ID, EventResize.class, listener,
                EventResizeListener.eventResizeMethod);
    }

    public void addListener(RangeSelectListener listener) {
        addListener(RangeSelectEvent.EVENT_ID, RangeSelectEvent.class,
                listener, RangeSelectListener.rangeSelectMethod);

    }

    public void addListener(EventMoveListener listener) {
        addListener(MoveEvent.EVENT_ID, MoveEvent.class, listener,
                EventMoveListener.eventMoveMethod);
    }

    public void removeListener(ForwardListener listener) {
        removeListener(ForwardEvent.EVENT_ID, ForwardEvent.class, listener);
    }

    public void removeListener(BackwardListener listener) {
        removeListener(BackwardEvent.EVENT_ID, BackwardEvent.class, listener);
    }

    public void removeListener(DateClickListener listener) {
        removeListener(DateClickEvent.EVENT_ID, DateClickEvent.class, listener);
    }

    public void removeListener(EventClickListener listener) {
        removeListener(EventClick.EVENT_ID, EventClick.class, listener);
    }

    public void removeListener(WeekClickListener listener) {
        removeListener(WeekClick.EVENT_ID, WeekClick.class, listener);
    }

    public void removeListener(EventMoveListener listener) {
        removeListener(MoveEvent.EVENT_ID, MoveEvent.class, listener);
    }

    public void removeListener(RangeSelectListener listener) {
        removeListener(RangeSelectEvent.EVENT_ID, RangeSelectEvent.class,
                listener);
    }

    public void removeListener(EventResizeListener listener) {
        removeListener(EventResize.EVENT_ID, EventResize.class, listener);
    }
}
