package com.vaadin.calendar.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.vaadin.calendar.gwt.client.ui.VSchedule;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

/**
 * Schedule is for visualizing events in calendar. Only
 * {@link java.util.GregorianCalendar GregorianCalendar} is supported. Events
 * can be visualized in variable length view depending on start and end dates.<br/>
 * <li>You must set view daterange with setStartDate and setEndDate otherwise
 * schedule will be empty <li>If view start to view end is 7 days or smaller,
 * weekly view is used.
 */
@ClientWidget(VSchedule.class)
public class Schedule extends AbstractComponent {

    // type safe enum pattern
    public static class CalendarFormat {
        private final String type;
        public static final CalendarFormat FORMAT_12H = new CalendarFormat(
                "FORMAT12");
        public static final CalendarFormat FORMAT_24H = new CalendarFormat(
                "FORMAT24");

        private CalendarFormat(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    private boolean hideWeekends = true;
    protected boolean disableOverlappingLongEvents = true;
    protected CalendarFormat currentFormat = CalendarFormat.FORMAT_24H;

    protected Calendar currentCalendar = new GregorianCalendar();
    protected Date startDate = null;
    protected Date endDate = null;
    protected EventReader datasourceReader;

    private final long HOURINMILLIS = 60 * 60 * 1000;
    private final long DAYINMILLIS = 24 * HOURINMILLIS;

    private ArrayList<ScheduleEvent> events;

    protected DateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");
    protected DateFormat df_time = new SimpleDateFormat("HH:mm:ss");
    protected DateFormat df_time_move = new SimpleDateFormat("yyyy-MM-dd-h-mm");
    private ArrayList<RangeSelectListener> rangeSelectListeners = new ArrayList<RangeSelectListener>();
    private ArrayList<EventMoveListener> eventMoveListeners = new ArrayList<EventMoveListener>();
    private ArrayList<WeekClickListener> weekClickListeners = new ArrayList<WeekClickListener>();
    private ArrayList<NavigationListener> navigationListeners = new ArrayList<NavigationListener>();
    protected boolean rangeSelection = true;
    private int scrollTop = 305;

    /** Custom caption format for weekly and date views */
    private SimpleDateFormat weeklyCaptionFormat = (SimpleDateFormat) SimpleDateFormat
            .getDateInstance();

    public Schedule(EventReader eventReader) {
        datasourceReader = eventReader;
        setSizeFull();

    }

    /**
     * Example: setCalendarFormat(CalendarFormat.FORMAT_12h);
     * 
     * @param format
     *            Set 12h or 24h format. Default is 24h format.
     */
    public void setCalendarFormat(CalendarFormat format) {
        currentFormat = format;
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

    public void setTimeZone(TimeZone zone) {
        if (!currentCalendar.getTimeZone().equals(zone)) {
            currentCalendar.setTimeZone(zone);
            requestRepaint();
        }
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

    @Override
    public void setLocale(Locale l) {
        weeklyCaptionFormat = (SimpleDateFormat) SimpleDateFormat
                .getDateInstance(SimpleDateFormat.SHORT, l);
        requestRepaint();
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
                currentFormat == CalendarFormat.FORMAT_24H);
        target.addAttribute("dayNames", getDayNamesShort());
        target.addAttribute("monthNames", getMonthNamesShort());
        target.addAttribute("fdow", currentCalendar.getFirstDayOfWeek());
        target.addAttribute("readonly", isReadOnly());
        // Use same timezone in all dates this component handles.
        // Show "now"-marker in browser within given timezone.
        Date now = new Date();
        currentCalendar.setTime(now);
        int offset = currentCalendar.getTimeZone().getOffset(now.getTime());
        currentCalendar.add(Calendar.MILLISECOND, offset);
        now = currentCalendar.getTime();
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
                    .get(Calendar.DAY_OF_WEEK));
            target
                    .addAttribute("w", currentCalendar
                            .get(Calendar.WEEK_OF_YEAR));
            target.endTag("day");
            currentCalendar.add(Calendar.DATE, 1);
        }

        target.endTag("days");

        events = datasourceReader.getEvents(firstDateToShow, lastDateToShow);
        target.startTag("events");
        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                target.startTag("event");
                paintEvent(i, target);
                target.endTag("event");
            }
        }
        target.endTag("events");
        target.addVariable(this, "rangeSelect", "");
        target.addVariable(this, "eventOpened", -1);
        target.addVariable(this, "dayOpened", "");
        target.addVariable(this, "weekOpened", "");
        target.addVariable(this, "scroll", scrollTop);
        target.addVariable(this, "eventMove", "");
        target.addVariable(this, "navigation", 0);
        target.addAttribute("asdf", this);
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
        ScheduleEvent e = events.get(i);
        target.addAttribute("i", i);
        target.addAttribute("caption", e.getCaption());
        target.addAttribute("dfrom", df_date.format(e.getWhenFrom()));
        target.addAttribute("dto", df_date.format(e.getWhenTo()));
        target.addAttribute("tfrom", df_time.format(e.getWhenFrom()));
        target.addAttribute("tto", df_time.format(e.getWhenTo()));
        target.addAttribute("description", e.getDescription() == null ? "" : e
                .getDescription());
        target.addAttribute("extracss", e.getStyleName() == null ? "" : e
                .getStyleName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey("rangeSelect") && !isReadOnly()) {
            System.out.println("rangeSelect");
            String value = (String) variables.get("rangeSelect");
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
                        currentCalendar.add(Calendar.MINUTE, startMinutes);
                        Date start = currentCalendar.getTime();
                        currentCalendar.add(Calendar.MINUTE, endMinutes
                                - startMinutes);
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
        if (variables.containsKey("eventOpened")) {
            Integer i = (Integer) variables.get("eventOpened");
            if (i >= 0 && i < events.size() && events.get(i) != null) {
                System.out.println("Schedule:eventOpened");
                fireEventClick(i);
            }
        }
        if (variables.containsKey("dayOpened")) {
            System.out.println(variables.get("dayOpened"));
            String message = (String) variables.get("dayOpened");
            if (message != null && message.length() > 6) {
                try {
                    Date d = df_date.parse(message);
                    fireDateClick(d);
                } catch (ParseException e) {
                }
            }
        }
        if (variables.containsKey("weekOpened")) {

            String s = (String) variables.get("weekOpened");
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
        if (variables.containsKey("eventMove") && !isReadOnly()) {
            String message = variables.get("eventMove").toString();
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
            currentCalendar.add(Calendar.DATE, durationInDays);
            startDate = currentCalendar.getTime();
            currentCalendar.setTime(endDate);
            currentCalendar.add(Calendar.DATE, durationInDays);
            endDate = currentCalendar.getTime();
            requestRepaint();
            fireNavigationEvent();
        }
    }

    private void fireNavigationEvent() {
        for (NavigationListener l : navigationListeners) {
            l.onScheduleForward();
        }
    }

    private void fireEventMove(int index, Date newFromDatetime) {
        ScheduleEvent e = events.get(index);
        for (EventMoveListener l : eventMoveListeners) {
            l.eventMoved(e, newFromDatetime);
        }
    }

    private void fireWeekClick(int week, int year) {
        for (WeekClickListener l : weekClickListeners) {
            l.weekClicked(week, year);
        }
    }

    private void fireEventClick(Integer i) {
        ScheduleEvent e = events.get(i);
        for (NavigationListener l : navigationListeners) {
            l.eventClicked(e);
        }
    }

    private void fireDateClick(Date d) {
        for (NavigationListener l : navigationListeners) {
            l.dateClicked(d);
        }
    }

    private void fireRangeSelect(Date from, Date to) {
        for (RangeSelectListener rsl : rangeSelectListeners) {
            rsl.rangeSelected(from, to);
        }
    }

    /**
     * @return localized display names for weeks days starting from sunday.
     *         Returned array is always .lenght() ==7
     */
    protected String[] getDayNamesShort() {
        Map<String, Integer> displayNames = currentCalendar.getDisplayNames(
                Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        String[] dayNames = new String[7];
        for (String s : displayNames.keySet()) {
            Integer value = displayNames.get(s);
            dayNames[value - 1] = s;
        }
        return dayNames;
    }

    /**
     * @return localized display names for months starting from January.
     *         Returned array is always .lenght() ==12
     */
    protected String[] getMonthNamesShort() {
        Map<String, Integer> displayNames = currentCalendar.getDisplayNames(
                Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        String[] monthNames = new String[12];
        for (String s : displayNames.keySet()) {
            Integer value = displayNames.get(s);
            monthNames[value] = s;
        }
        return monthNames;
    }

    /**
     * @param dateInWeek
     * @return Date that is first date in same week that given date is
     */
    private Date getFirstDateForWeek(Date dateInWeek) {
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        currentCalendar.setTime(dateInWeek);
        while (firstDayOfWeek != currentCalendar.get(Calendar.DAY_OF_WEEK)) {
            currentCalendar.add(Calendar.DATE, -1);
        }
        return currentCalendar.getTime();
    }

    /**
     * @param dateInWeek
     * @return Date that is last date in same week that given date is
     */
    private Date getLastDateForWeek(Date dateInWeek) {
        currentCalendar.setTime(dateInWeek);
        currentCalendar.add(Calendar.DATE, 1);
        int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
        // Roll to weeks last day using firstdayofweek. Roll until FDofW is
        // found and then roll back one day.
        while (firstDayOfWeek != currentCalendar.get(Calendar.DAY_OF_WEEK)) {
            currentCalendar.add(Calendar.DATE, 1);
        }
        currentCalendar.add(Calendar.DATE, -1);
        return currentCalendar.getTime();
    }

    /**
     * One event in schedule.<br/>
     * <li>whenFrom, whenTo and caption fields are mandatory. <li>In "allDay"
     * events, starting and ending clocktimes are omitted in UI and only dates
     * are shown.
     */
    public class ScheduleEvent {
        private Date whenFrom;
        private Date whenTo;
        private String caption;
        private String where;
        private String description;
        private Object data;
        private String styleName;

        public ScheduleEvent(String caption, Date whenFrom, Date whenTo) {
            this.caption = caption;
            this.whenFrom = whenFrom;
            this.whenTo = whenTo;
        }

        public Date getWhenFrom() {
            return whenFrom;
        }

        public void setWhenFrom(Date whenFrom) {
            this.whenFrom = whenFrom;
        }

        public Date getWhenTo() {
            return whenTo;
        }

        public void setWhenTo(Date whenTo) {
            this.whenTo = whenTo;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String what) {
            caption = what;
        }

        public String getWhere() {
            return where;
        }

        public void setWhere(String where) {
            this.where = where;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public void setStyleName(String styleName) {
            this.styleName = styleName;
        }

        public String getStyleName() {
            return styleName;
        }

    }

    public void addWeekClickListener(WeekClickListener l) {
        if (!weekClickListeners.contains(l)) {
            weekClickListeners.add(l);
        }
    }

    public void addEventMoveListener(EventMoveListener l) {
        if (!eventMoveListeners.contains(l)) {
            eventMoveListeners.add(l);
        }
    }

    public void addRangeSelectListener(RangeSelectListener l) {
        if (!rangeSelectListeners.contains(l)) {
            rangeSelectListeners.add(l);
        }
    }

    public void addNavigationListener(NavigationListener l) {
        navigationListeners.add(l);
    }

    public void removeNavigationLister(NavigationListener l) {
        if (!navigationListeners.contains(l)) {
            navigationListeners.remove(l);
        }
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
     * Listener for schedule event drag&drops
     */
    public interface EventMoveListener {
        public void eventMoved(ScheduleEvent e, Date newFromDatetime);
    }

    /**
     * Listener for day cell drag-marking with mouse
     */
    public interface RangeSelectListener {
        public void rangeSelected(Date startDate, Date endDate);
    }

    public interface NavigationListener {
        public void onScheduleForward();

        public void onScheduleBackward();

        public void dateClicked(Date d);

        public void eventClicked(ScheduleEvent e);

    }

    /**
     * Listener for week clicks
     */
    public interface WeekClickListener {
        public void weekClicked(int week, int year);
    }

    /**
     * Interface for querying datasource. Schedule component must have
     * EventReader implementation. This interface will be dropped in future
     * versions. In future schedule will require DateContainer or similiar.
     */
    public interface EventReader {
        public ArrayList<ScheduleEvent> getEvents(Date fromStartDate,
                Date toEndDate);
    }

}
