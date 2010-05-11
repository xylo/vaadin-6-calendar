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

import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
import com.vaadin.addon.calendar.ui.CalendarEvents.BackwardEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.BackwardListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.DateClickListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarEvents.EventClickListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.EventMoveListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.ForwardEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.ForwardListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.MoveEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.RangeSelectListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarEvents.WeekClickListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

/**
 * Vaadin Calendar is for visualizing events in calendar. Only
 * {@link java.util.GregorianCalendar GregorianCalendar} is supported. Events
 * can be visualized in variable length view depending on start and end dates.<br/>
 * <li>You must set view daterange with setStartDate and setEndDate otherwise
 * schedule will be empty <li>If view start to view end is 7 days or smaller,
 * weekly view is used.
 */
@ClientWidget(VCalendar.class)
public class Calendar extends AbstractComponent implements
        CalendarEvents.NavigationNotifier, CalendarEvents.EventMoveNotifier,
        CalendarEvents.RangeSelectNotifier {

    private static final long serialVersionUID = -1858262705387350736L;

    public enum CalendarFormat {

        Format12H(), Format24H();
    }

    private boolean hideWeekends = true;
    protected boolean disableOverlappingLongEvents = true;
    protected CalendarFormat currentFormat;

    protected java.util.Calendar currentCalendar = new GregorianCalendar();
    protected TimeZone timezone;
    protected Date startDate = null;
    protected Date endDate = null;
    protected EventProvider schduleEventProvider;

    public static final long HOURINMILLIS = 60 * 60 * 1000;
    public static final long DAYINMILLIS = 24 * HOURINMILLIS;

    private List<Calendar.Event> events;

    protected DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
    protected DateFormat df_time = new SimpleDateFormat("HH:mm:ss");
    protected DateFormat df_time_move = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    protected boolean rangeSelection = true;
    private int scrollTop = 305;

    /** Custom caption format for weekly and date views */
    private SimpleDateFormat weeklyCaptionFormat = (SimpleDateFormat) SimpleDateFormat
            .getDateInstance();

    public Calendar(EventProvider schduleEventProvider) {
        this.schduleEventProvider = schduleEventProvider;
        setSizeFull();

    }

    /**
     * Gets currently active time format. Value is either
     * CalendarFormat.Format12H or CalendarFormat.Format24H.
     * 
     * @return CalendarFormat
     */
    public CalendarFormat getCalendarFormat() {
        if (currentFormat == null) {
            SimpleDateFormat f = (SimpleDateFormat) SimpleDateFormat
                    .getTimeInstance(SimpleDateFormat.SHORT, getLocale());
            String p = f.toPattern();
            if (p.indexOf("HH") != -1 || p.indexOf("H") != -1)
                return CalendarFormat.Format24H;
            return CalendarFormat.Format12H;
        }
        return currentFormat;
    }

    /**
     * Example: setCalendarFormat(CalendarFormat.Format12H);</br> Set to null,
     * if you want the format being defined by the locale.
     * 
     * @param format
     *            Set 12h or 24h format. Default is defined by the locale.
     */
    public void setCalendarFormat(CalendarFormat format) {
        currentFormat = format;
        requestRepaint();
    }

    /**
     * Sets first showing date to given date
     * 
     * @param date
     *            First visible date to show
     */
    public void setStartDate(Date date) {
        if (!date.equals(startDate)) {
            startDate = date;
            requestRepaint();
        }
    }

    /**
     * Sets last showing date to given date. Only 6 weeks is shown starting from
     * StartDate if duration from startdate to enddate is longer than 6 weeks.
     * 
     * @param date
     *            Last visible date to show
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
            df_time_move.setTimeZone(zone);
            requestRepaint();
        }
    }

    /**
     * Returns a time zone that is currently used by this component.
     * 
     * @return Time zone
     */
    public TimeZone getTimeZone() {
        if (timezone == null)
            return currentCalendar.getTimeZone();
        return timezone;
    }

    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets custom date format for weekly and day views. This is the caption of
     * the date. Example use format like "mmm MM/dd" like google's calendar.
     * 
     * @param caption
     */
    public void setWeeklyCaptionFormat(String dateFormatPattern) {
        if (!weeklyCaptionFormat.toPattern().equals(dateFormatPattern)) {
            weeklyCaptionFormat.applyPattern(dateFormatPattern);
            requestRepaint();
        }
    }

    /**
     * @return pattern used in caption of dates in weekly and day views.
     */
    public String getWeeklyCaptionFormat() {
        return weeklyCaptionFormat.toPattern();
    }

    /** Sets the locale to be used in the Schedule component. */
    @Override
    public void setLocale(Locale l) {
        weeklyCaptionFormat = (SimpleDateFormat) SimpleDateFormat
                .getDateInstance(SimpleDateFormat.SHORT, l);
        if (timezone != null)
            currentCalendar = new GregorianCalendar(timezone, l);
        else
            currentCalendar = new GregorianCalendar(l);

        super.setLocale(l);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {

        if (startDate == null || endDate == null) {
            throw new PaintException(
                    "Schedule cannot be painted without proper date ranges.");
        }

        int durationInDays = (int) (((endDate.getTime()) - startDate.getTime()) / DAYINMILLIS);
        durationInDays++;
        if (durationInDays > 60) {
            throw new PaintException("Daterange is too big (max 60) = "
                    + durationInDays);
        }

        target.addAttribute("format24h",
                getCalendarFormat() == CalendarFormat.Format24H);
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
        // If more than week, use monthly view and get startweek and endweek.
        // Example if views daterange is from tuesday to next weeks
        // wednesday->expand to monday to nextweeks sunday. If firstdayofweek =
        // monday
        if (durationInDays > 7) {
            firstDateToShow = getFirstDateForWeek(startDate);
            lastDateToShow = getLastDateForWeek(endDate);
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

        events = schduleEventProvider
                .getEvents(firstDateToShow, lastDateToShow);
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
     * Paints single calendar event to uidl. Override this method to add custom
     * attributes for events.
     * 
     * @param i
     * @param target
     */
    protected void paintEvent(int i, PaintTarget target) throws PaintException {
        Calendar.Event e = events.get(i);
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
            String value = (String) variables.get(CalendarEventId.RANGESELECT);
            if (value != null && value.length() > 14 && value.contains("TO")) {
                String[] dates = value.split("TO");
                try {
                    Date d1 = df_date.parse(dates[0]);
                    Date d2 = df_date.parse(dates[1]);
                    fireRangeSelect(d1, d2);

                } catch (ParseException e) {
                    // Client cheating, do nothing
                }
                requestRepaint();
            } else if (value != null && value.length() > 12
                    && value.contains(":")) {
                String[] dates = value.split(":");
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
                        currentCalendar.add(java.util.Calendar.MILLISECOND, -1);
                        Date end = currentCalendar.getTime();
                        fireRangeSelect(start, end);
                    } catch (ParseException e) {
                        // Client cheating, do nothing
                    } catch (NumberFormatException e) {
                        // Client cheating, do nothing
                    }
                }
            }
        }
        if (variables.containsKey(CalendarEventId.EVENTCLICK)) {
            Integer i = (Integer) variables.get(CalendarEventId.EVENTCLICK);
            if (i >= 0 && i < events.size() && events.get(i) != null) {
                fireEventClick(i);
            }
        }
        if (variables.containsKey(CalendarEventId.DATECLICK)) {
            String message = (String) variables.get(CalendarEventId.DATECLICK);
            if (message != null && message.length() > 6) {
                try {
                    Date d = df_date.parse(message);
                    fireDateClick(d);
                } catch (ParseException e) {
                }
            }
        }
        if (variables.containsKey(CalendarEventId.WEEKCLICK)) {

            String s = (String) variables.get(CalendarEventId.WEEKCLICK);
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
        if (variables.containsKey("scroll")) {
            try {
                int i = Integer.parseInt(variables.get("scroll").toString());
                scrollTop = i;
            } catch (NumberFormatException e) {
                // Client cheating, do nothing
            }
        }
        if (variables.containsKey(CalendarEventId.EVENTMOVE) && !isReadOnly()) {
            String message = variables.get(CalendarEventId.EVENTMOVE)
                    .toString();
            if (message != null && message.length() > 10) {
                String[] splitted = message.split(":");
                if (splitted.length == 2) {
                    int index = Integer.parseInt(splitted[0]);

                    try {
                        Date d = df_time_move.parse(splitted[1]);
                        if (index >= 0 && index < events.size()
                                && events.get(index) != null) {
                            fireEventMove(index, d);
                        }
                    } catch (ParseException e) {
                        // Client cheating, do nothing
                    } catch (NumberFormatException e) {
                        // Client cheating, do nothing
                    }

                }
            }
        }
        if (variables.containsKey("navigation")) {
            int index = (Integer) variables.get("navigation");
            int durationInDays = (int) (((endDate.getTime()) - startDate
                    .getTime()) / DAYINMILLIS);
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

    protected void fireRangeSelect(Date from, Date to) {
        fireEvent(new RangeSelectEvent(this, from, to));
    }

    /**
     * @return localized display names for weeks days starting from sunday.
     *         Returned array is always .lenght() ==7
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
     * @return localized display names for months starting from January.
     *         Returned array is always .lenght() ==12
     */
    protected String[] getMonthNamesShort() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        return s.getShortMonths();
    }

    /**
     * @param dateInWeek
     * @return Date that is first date in same week that given date is
     */
    private Date getFirstDateForWeek(Date dateInWeek) {
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        currentCalendar.setTime(dateInWeek);
        while (firstDayOfWeek != currentCalendar
                .get(java.util.Calendar.DAY_OF_WEEK)) {
            currentCalendar.add(java.util.Calendar.DATE, -1);
        }
        return currentCalendar.getTime();
    }

    /**
     * @param dateInWeek
     * @return Date that is last date in same week that given date is
     */
    private Date getLastDateForWeek(Date dateInWeek) {
        currentCalendar.setTime(dateInWeek);
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
     * Disable or enable date range selection
     * 
     * @param b
     */
    public void setRangeSelection(boolean b) {
        if (rangeSelection != b) {
            rangeSelection = b;
            requestRepaint();
        }
    }

    public void setHideWeekends(boolean hideWeekends) {
        if (hideWeekends != this.hideWeekends) {
            this.hideWeekends = hideWeekends;
            requestRepaint();
        }
    }

    public boolean isHideWeekends() {
        return hideWeekends;
    }

    /**
     * Interface for querying datasource. Schedule component must have
     * EventProvider implementation. This interface will be dropped in future
     * versions. In future schedule will require DateContainer or similiar.
     */
    public interface EventProvider {
        public List<Calendar.Event> getEvents(Date fromStartDate, Date toEndDate);
    }

    /**
     * One event in schedule.<br/>
     * <li>start, end and caption fields are mandatory. <li>In "allDay" events,
     * starting and ending clocktimes are omitted in UI and only dates are
     * shown.
     */
    public interface Event {

        public Date getStart();

        public Date getEnd();

        public String getCaption();

        public String getDescription();

        public String getStyleName();
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

    public void addListener(CalendarEvents.WeekClickListener listener) {
        addListener(WeekClick.EVENT_ID, WeekClick.class, listener,
                WeekClickListener.weekClickMethod);
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

    public void addListener(EventMoveListener listener) {
        addListener(MoveEvent.EVENT_ID, MoveEvent.class, listener,
                EventMoveListener.eventMoveMethod);
    }

    public void removeListener(EventMoveListener listener) {
        removeListener(MoveEvent.EVENT_ID, MoveEvent.class, listener);
    }

    public void addListener(RangeSelectListener listener) {
        addListener(RangeSelectEvent.EVENT_ID, RangeSelectEvent.class,
                listener, RangeSelectListener.rangeSelectMethod);

    }

    public void removeListener(RangeSelectListener listener) {
        removeListener(RangeSelectEvent.EVENT_ID, RangeSelectEvent.class,
                listener);
    }

}
