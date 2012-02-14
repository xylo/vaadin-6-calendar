/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEvent;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.DayToolbar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.MonthGrid;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleDayCell;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleDayToolbar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleWeekToolbar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeeklyLongEvents;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.terminal.gwt.client.BrowserInfo;

/**
 * Clients side implementation for {@link Calendar}.
 * 
 * @since 1.3.0
 * @version
 * @VERSION@
 */
public class VCalendar extends Composite {

    public static final long MINUTEINMILLIS = 60 * 1000;
    public static final long HOURINMILLIS = 60 * MINUTEINMILLIS;
    public static final long DAYINMILLIS = 24 * HOURINMILLIS;
    public static final long WEEKINMILLIS = 7 * DAYINMILLIS;
    public static final String ATTR_FIRSTDAYOFWEEK = "firstDay";
    public static final String ATTR_LASTDAYOFWEEK = "lastDay";
    public static final String ATTR_FIRSTHOUROFDAY = "firstHour";
    public static final String ATTR_LASTHOUROFDAY = "lastHour";

    // private boolean hideWeekends;
    private String[] monthNames;
    private String[] dayNames;
    private boolean format;
    private final DockPanel outer = new DockPanel();
    private int rows;

    private boolean rangeSelectAllowed = true;
    private boolean rangeMoveAllowed = true;
    private boolean eventResizeAllowed = true;
    private boolean eventMoveAllowed = true;

    private String height = null;
    private String width = null;
    private final SimpleDayToolbar nameToolbar = new SimpleDayToolbar();

    private final DayToolbar dayToolbar = new DayToolbar(this);
    private final SimpleWeekToolbar weekToolbar;
    private WeeklyLongEvents weeklyLongEvents;
    private MonthGrid monthGrid;
    private WeekGrid weekGrid;
    private int intWidth = 0;
    private int intHeight = 0;

    protected final DateTimeFormat dateformat_datetime = DateTimeFormat
            .getFormat("yyyy-MM-dd HH:mm:ss");
    protected final DateTimeFormat dateformat_date = DateTimeFormat
            .getFormat("yyyy-MM-dd");
    protected final DateTimeFormat time12format_date = DateTimeFormat
            .getFormat("h:mm a");
    protected final DateTimeFormat time24format_date = DateTimeFormat
            .getFormat("HH:mm");

    private boolean readOnly = false;
    private boolean disabled = false;

    private boolean isHeightUndefined = false;

    private boolean isWidthUndefined = false;
    private int firstDay;
    private int lastDay;
    private int firstHour;
    private int lastHour;

    public interface DateClickListener {
        void dateClick(String date);
    }

    public interface WeekClickListener {
        void weekClick(String event);
    }

    public interface ForwardListener {
        void forward();
    }

    public interface BackwardListener {
        void backward();
    }

    public interface RangeSelectListener {
        void rangeSelected(String value);
    }

    public interface EventClickListener {
        void eventClick(CalendarEvent event);
    }

    public interface EventMovedListener {
        void eventMoved(CalendarEvent event);
    }

    public interface EventResizeListener {
        void eventResized(CalendarEvent event);
    }

    public interface ScrollListener {
        void scroll(int scrollPosition);
    }

    public interface MouseEventListener {
        void contextMenu(ContextMenuEvent event, Widget widget);
    }

    public VCalendar() {
        weekToolbar = new SimpleWeekToolbar(this);
        initWidget(outer);
        setStylePrimaryName("v-calendar");
        blockSelect(getElement());
    }

    private native void blockSelect(Element e)
    /*-{
    	e.onselectstart = function() {
    		return false;
    	}

    	e.ondragstart = function() {
    		return false;
    	}
    }-*/;

    private void updateEventsToWeekGrid(CalendarEvent[] events) {
        List<CalendarEvent> allDayLong = new ArrayList<CalendarEvent>();
        List<CalendarEvent> belowDayLong = new ArrayList<CalendarEvent>();

        for (CalendarEvent e : events) {
            if (e.isAllDay()) {
                // Event is set on one "allDay" event or more than one.
                allDayLong.add(e);

            } else {
                // Event is set only on one day.
                belowDayLong.add(e);
            }
        }

        weeklyLongEvents.addEvents(allDayLong);

        for (CalendarEvent e : belowDayLong) {
            weekGrid.addEvent(e);
        }
    }

    protected void updateEventsToMonthGrid(Collection<CalendarEvent> events,
            boolean drawImmediately) {
        for (CalendarEvent e : sortEventsByDuration(events)) {
            addEventToMonthGrid(e, false);
        }
    }

    private void addEventToMonthGrid(CalendarEvent e, boolean renderImmediately) {
        Date when = e.getStart();
        Date to = e.getEnd();
        boolean eventAdded = false;
        boolean inProgress = false; // Event adding has started
        boolean eventMoving = false;
        List<SimpleDayCell> dayCells = new ArrayList<SimpleDayCell>();
        List<SimpleDayCell> timeCells = new ArrayList<SimpleDayCell>();
        for (int row = 0; row < monthGrid.getRowCount(); row++) {
            if (eventAdded) {
                break;
            }
            for (int cell = 0; cell < monthGrid.getCellCount(row); cell++) {
                SimpleDayCell sdc = (SimpleDayCell) monthGrid.getWidget(row,
                        cell);
                if (isEventInDay(when, to, sdc.getDate())
                        && isEventInDayWithTime(when, to, sdc.getDate(),
                                e.getEndTime(), e.isAllDay())) {
                    if (!eventMoving) {
                        eventMoving = sdc.getMoveEvent() != null;
                    }
                    long d = e.getRangeInMilliseconds();
                    if ((d > 0 && d <= VCalendar.DAYINMILLIS) && !e.isAllDay()) {
                        timeCells.add(sdc);
                    } else {
                        dayCells.add(sdc);
                    }
                    inProgress = true;
                    continue;
                } else if (inProgress) {
                    eventAdded = true;
                    inProgress = false;
                    break;
                }
            }
        }

        updateEventSlotIndex(e, dayCells);
        updateEventSlotIndex(e, timeCells);

        for (SimpleDayCell sdc : dayCells) {
            sdc.addCalendarEvent(e);
        }
        for (SimpleDayCell sdc : timeCells) {
            sdc.addCalendarEvent(e);
        }

        if (renderImmediately) {
            reDrawAllMonthEvents(!eventMoving);
        }
    }

    /*
     * We must also handle the special case when the event lasts exactly for 24
     * hours, thus spanning two days e.g. from 1.1.2001 00:00 to 2.1.2001 00:00.
     * That special case still should span one day when rendered.
     */
    @SuppressWarnings("deprecation")
    // Date methods are not deprecated in GWT
    private boolean isEventInDayWithTime(Date from, Date to, Date date,
            Date endTime, boolean isAllDay) {
        return (isAllDay || !(to.getDay() == date.getDay()
                && from.getDay() != to.getDay() && isMidnight(endTime)));
    }

    private void updateEventSlotIndex(CalendarEvent e, List<SimpleDayCell> cells) {
        if (cells.isEmpty()) {
            return;
        }

        if (e.getSlotIndex() == -1) {
            // Update slot index
            int newSlot = -1;
            for (SimpleDayCell sdc : cells) {
                int slot = sdc.getEventCount();
                if (slot > newSlot) {
                    newSlot = slot;
                }
            }
            newSlot++;

            for (int i = 0; i < newSlot; i++) {
                // check for empty slot
                if (isSlotEmpty(e, i, cells)) {
                    newSlot = i;
                    break;
                }
            }
            e.setSlotIndex(newSlot);
        }
    }

    private void reDrawAllMonthEvents(boolean clearCells) {
        for (int row = 0; row < monthGrid.getRowCount(); row++) {
            for (int cell = 0; cell < monthGrid.getCellCount(row); cell++) {
                SimpleDayCell sdc = (SimpleDayCell) monthGrid.getWidget(row,
                        cell);
                sdc.reDraw(clearCells);
            }
        }
    }

    private boolean isSlotEmpty(CalendarEvent addedEvent, int slotIndex,
            List<SimpleDayCell> cells) {
        for (SimpleDayCell sdc : cells) {
            CalendarEvent e = sdc.getCalendarEvent(slotIndex);
            if (e != null && !e.equals(addedEvent)) {
                return false;
            }
        }
        return true;
    }

    public void removeMonthEvent(CalendarEvent target,
            boolean repaintImmediately) {
        if (target != null && target.getSlotIndex() >= 0) {
            // Remove event
            for (int row = 0; row < monthGrid.getRowCount(); row++) {
                for (int cell = 0; cell < monthGrid.getCellCount(row); cell++) {
                    SimpleDayCell sdc = (SimpleDayCell) monthGrid.getWidget(
                            row, cell);
                    if (sdc == null) {
                        return;
                    }
                    sdc.removeEvent(target, repaintImmediately);
                }
            }
        }
    }

    public void updateEventToMonthGrid(CalendarEvent changedEvent) {
        removeMonthEvent(changedEvent, true);
        changedEvent.setSlotIndex(-1);
        addEventToMonthGrid(changedEvent, true);
    }

    public CalendarEvent[] sortEventsByDuration(Collection<CalendarEvent> events) {
        CalendarEvent[] sorted = events
                .toArray(new CalendarEvent[events.size()]);

        /*
         * this is required because of
         * https://bugs.webkit.org/show_bug.cgi?id=40367
         * 
         * remove this workaround when the fix has been released with a stable
         * build
         */
        if (BrowserInfo.get().isSafari()
                && BrowserInfo.get().getWebkitVersion() > 5) {
            customQuicksort(sorted, getEventComparator());

        } else {
            Arrays.sort(sorted, getEventComparator());
        }

        return sorted;
    }

    /*
     * Check if the given event occurs at the given date.
     */
    private boolean isEventInDay(Date eventWhen, Date eventTo, Date gridDate) {
        if (eventWhen.compareTo(gridDate) <= 0
                && eventTo.compareTo(gridDate) >= 0) {

            return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    public void updateWeekGrid(int daysCount, List<Day> days, Date today,
            String[] realDayNames) {
        weekGrid.setFirstHour(firstHour);
        weekGrid.setLastHour(lastHour);
        weekGrid.getTimeBar().updateTimeBar(format);

        dayToolbar.clear();
        dayToolbar.addBackButton();
        dayToolbar.setVerticalSized(isHeightUndefined);
        dayToolbar.setHorizontalSized(isWidthUndefined);
        weekGrid.clearDates();
        weekGrid.setDisabled(isDisabledOrReadOnly());

        for (Day day : days) {
            String date = day.getDate();
            String localized_date_format = day.getLocalizedDateFormat();
            Date d = dateformat_date.parse(date);
            int dayOfWeek = day.getDayOfWeek();
            if (dayOfWeek < firstDay || dayOfWeek > lastDay) {
                continue;
            }
            boolean isToday = false;
            int dayOfMonth = d.getDate();
            if (today.getDate() == dayOfMonth && today.getYear() == d.getYear()
                    && today.getMonth() == d.getMonth()) {
                isToday = true;
            }
            dayToolbar.add(realDayNames[dayOfWeek - 1], date,
                    localized_date_format, isToday ? "today" : null);
            weeklyLongEvents.addDate(d);
            weekGrid.addDate(d);
            if (isToday) {
                weekGrid.setToday(d, today);
            }
        }
        dayToolbar.addNextButton();
    }

    /**
     * Updates the events in the Month viewq
     * 
     * @param daysCount
     *            How many days there are
     * @param daysUidl
     * 
     * @param today
     *            Todays date
     */
    @SuppressWarnings("deprecation")
    protected void updateMonthGrid(int daysCount, List<Day> days, Date today) {
        int columns = lastDay - firstDay + 1;
        rows = (int) Math.ceil(daysCount / (double) 7);

        monthGrid = new MonthGrid(this, rows, columns);
        monthGrid.setDisabled(isDisabledOrReadOnly());
        monthGrid.setHeightPX(intHeight);
        monthGrid.setWidthPX(intWidth);
        weekToolbar.removeAllRows();
        int pos = 0;
        boolean monthNameDrawn = true;
        boolean firstDayFound = false;
        boolean lastDayFound = false;

        for (Day day : days) {
            String date = day.getDate();
            Date d = dateformat_date.parse(date);
            int dayOfWeek = day.getDayOfWeek();
            int week = day.getWeek();

            int dayOfMonth = d.getDate();

            // reset at start of each month
            if (dayOfMonth == 1) {
                monthNameDrawn = false;
                if (firstDayFound) {
                    lastDayFound = true;
                }
                firstDayFound = true;
            }

            if (dayOfWeek < firstDay || dayOfWeek > lastDay) {
                continue;
            }
            int y = (pos / columns);
            int x = pos - (y * columns);
            if (x == 0 && daysCount > 7) {
                // Add week to weekToolbar for navigation
                weekToolbar.addWeek(week, d.getYear());
            }
            final SimpleDayCell cell = new SimpleDayCell(this, y, x);
            cell.setMonthGrid(monthGrid);
            cell.setDate(d);
            cell.addDomHandler(new ContextMenuHandler() {
                public void onContextMenu(ContextMenuEvent event) {
                    if (mouseEventListener != null) {
                        event.preventDefault();
                        event.stopPropagation();
                        mouseEventListener.contextMenu(event, cell);
                    }
                }
            }, ContextMenuEvent.getType());

            if (!firstDayFound) {
                cell.addStyleDependentName("prev-month");
            } else if (lastDayFound) {
                cell.addStyleDependentName("next-month");
            }

            if (dayOfMonth >= 1 && !monthNameDrawn) {
                cell.setMonthNameVisible(true);
                monthNameDrawn = true;
            }

            if (today.getDate() == dayOfMonth && today.getYear() == d.getYear()
                    && today.getMonth() == d.getMonth()) {
                cell.setToday(true);

            }
            monthGrid.setWidget(y, x, cell);
            pos++;
        }
    }

    @Override
    public void setHeight(String newHeight) {
        if (!newHeight.equals(height)) {
            height = newHeight;
            isHeightUndefined = "".equals(height);

            if (!isHeightUndefined) {
                intHeight = Integer.parseInt(newHeight.substring(0,
                        newHeight.length() - 2));
            } else {
                intHeight = -1;
            }

            super.setHeight(height);
            recalculateHeights();
        }
    }

    protected void recalculateHeights() {
        if (monthGrid != null) {
            monthGrid.setHeightPX(intHeight);
            monthGrid.updateCellSizes(intWidth - weekToolbar.getOffsetWidth(),
                    intHeight - nameToolbar.getOffsetHeight());
            weekToolbar.setHeightPX((intHeight == -1) ? intHeight : intHeight
                    - nameToolbar.getOffsetHeight());

        } else if (weekGrid != null) {
            weekGrid.setHeightPX((intHeight == -1) ? intHeight : intHeight
                    - weeklyLongEvents.getOffsetHeight()
                    - dayToolbar.getOffsetHeight());
        }
    }

    protected void recalculateWidths() {
        if (!isWidthUndefined) {
            outer.setWidth(intWidth + "px");
            super.setWidth(intWidth + "px");
            nameToolbar.setWidthPX(intWidth);
            dayToolbar.setWidthPX(intWidth);

            if (monthGrid != null) {
                monthGrid.updateCellSizes(
                        intWidth - weekToolbar.getOffsetWidth(), intHeight
                        - nameToolbar.getOffsetHeight());
            } else if (weekGrid != null) {
                weekGrid.setWidthPX(intWidth);
                weeklyLongEvents.setWidthPX(weekGrid.getInternalWidth());
            }

        } else {
            dayToolbar.setWidthPX(intWidth);
            nameToolbar.setWidthPX(intWidth);

            if (monthGrid != null) {
                monthGrid.setWidthPX(intWidth);
            }
        }
    }

    @Override
    public void setWidth(String newWidth) {
        if (!newWidth.equals(width)) {
            width = newWidth;
            isWidthUndefined = "".equals(width);

            if (!isWidthUndefined) {
                intWidth = Integer.parseInt(newWidth.substring(0,
                        newWidth.length() - 2));
            } else {
                intWidth = -1;
            }
            recalculateWidths();
        }
    }

    public DateTimeFormat getDateFormat() {
        return dateformat_date;
    }

    public DateTimeFormat getTimeFormat() {
        if (format) {
            return time24format_date;
        }
        return time12format_date;
    }

    public DateTimeFormat getDateTimeFormat() {
        return dateformat_datetime;
    }

    public boolean isDisabledOrReadOnly() {
        return disabled || readOnly;
    }

    /**
     * Is the component disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Is the component disabled
     * 
     * @param disabled
     *          True if disabled
     */
    public void setDisabled(boolean disabled){
        this.disabled = disabled;
    }

    /**
     * Is the component read-only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Is the component read-only
     * 
     * @param readOnly
     *          True if component is readonly
     */
    public void setReadOnly(boolean readOnly){
        this.readOnly = readOnly;
    }

    public MonthGrid getMonthGrid() {
        return monthGrid;
    }

    public WeekGrid getWeekGrid() {
        return weekGrid;
    }

    /**
     * Calculates correct size for all cells (size / amount of cells ) and
     * distributes any overflow over all the cells.
     * 
     * @param totalSize
     *            the total amount of size reserved for all cells
     * @param numberOfCells
     *            the number of cells
     * @param sizeModifier
     *            a modifier which is applied to all cells before distributing
     *            the overflow
     * @return an integer array that contains the correct size for each cell
     */
    public static int[] distributeSize(int totalSize, int numberOfCells,
            int sizeModifier) {
        int[] cellSizes = new int[numberOfCells];
        int startingSize = totalSize / numberOfCells;
        int cellSizeOverFlow = totalSize % numberOfCells;

        for (int i = 0; i < numberOfCells; i++) {
            cellSizes[i] = startingSize + sizeModifier;
        }

        // distribute size overflow amongst all slots
        int j = 0;
        while (cellSizeOverFlow > 0) {
            cellSizes[j]++;
            cellSizeOverFlow--;
            j++;
            if (j >= numberOfCells) {
                j = 0;
            }
        }

        // cellSizes[numberOfCells - 1] += cellSizeOverFlow;

        return cellSizes;
    }

    public static Comparator<CalendarEvent> getEventComparator() {
        return new Comparator<CalendarEvent>() {

            public int compare(CalendarEvent o1, CalendarEvent o2) {
                if (o1.isAllDay() != o2.isAllDay()) {
                    if (o2.isAllDay()) {
                        return 1;
                    }
                    return -1;
                }

                Long d1 = o1.getRangeInMilliseconds();
                Long d2 = o2.getRangeInMilliseconds();
                int r = 0;
                if (!d1.equals(0L) && !d2.equals(0L)) {
                    r = d2.compareTo(d1);
                    return (r == 0) ? ((Integer) o2.getIndex()).compareTo(o1
                            .getIndex()) : r;
                }

                if (d2.equals(0L) && d1.equals(0L)) {
                    return ((Integer) o2.getIndex()).compareTo(o1.getIndex());
                } else if (d2.equals(0L) && d1 >= VCalendar.DAYINMILLIS) {
                    return -1;
                } else if (d2.equals(0L) && d1 < VCalendar.DAYINMILLIS) {
                    return 1;
                } else if (d1.equals(0L) && d2 >= VCalendar.DAYINMILLIS) {
                    return 1;
                } else if (d1.equals(0L) && d2 < VCalendar.DAYINMILLIS) {
                    return -1;
                }
                r = d2.compareTo(d1);
                return (r == 0) ? ((Integer) o2.getIndex()).compareTo(o1
                        .getIndex()) : r;
            }
        };
    }

    static <T> void customQuicksort(T[] array, Comparator<? super T> comparator) {

        if (array.length > 1) {
            quicksort(array, 0, array.length - 1, comparator);
        }
    }

    static <T> void quicksort(T[] array, int left0, int right0,
            Comparator<? super T> comparator) {

        int left = left0;
        int right = right0 + 1;
        T pivot, temp;

        pivot = array[left0];

        do {

            do {
                left++;
            } while (left <= right0
                    && comparator.compare(array[left], pivot) < 0);

            do {
                right--;
            } while (comparator.compare(array[right], pivot) > 0);

            if (left < right) {
                temp = array[left];
                array[left] = array[right];
                array[right] = temp;
            }

        } while (left <= right);

        temp = array[left0];
        array[left0] = array[right];
        array[right] = temp;

        if (left0 < right) {
            quicksort(array, left0, right, comparator);
        }
        if (left < right0) {
            quicksort(array, left, right0, comparator);
        }

    }

    @SuppressWarnings("deprecation")
    // Date methods are not deprecated in GWT
    public static boolean isMidnight(Date endTime) {
        return (endTime.getHours() == 0 && endTime.getMinutes() == 0 && endTime
                .getSeconds() == 0);
    }

    @SuppressWarnings("deprecation")
    // Date methods are not deprecated in GWT
    public static boolean areDatesEqualToSecond(Date date1, Date date2) {
        return date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDay() == date2.getDay()
                && date1.getHours() == date2.getHours()
                && date1.getSeconds() == date2.getSeconds();
    }

    public static boolean isZeroLengthMidnightEvent(CalendarEvent e) {
        return areDatesEqualToSecond(e.getStartTime(), e.getEndTime())
                && isMidnight(e.getEndTime());
    }

    /**
     * Should the 24h time format be used
     * 
     * @param format
     *            True if the 24h format should be used else the 12h format is
     *            used
     */
    public void set24HFormat(boolean format) {
        this.format = format;
    }

    /**
     * Is the 24h time format used
     */
    public boolean is24HFormat() {
        return this.format;
    }

    /**
     * Set the names of the week days
     * 
     * @param names
     *            The names of the days (Monday, Thursday,...)
     */
    public void setDayNames(String[] names) {
        dayNames = names;
    }

    /**
     * Get the names of the week days
     */
    public String[] getDayNames() {
        return dayNames;
    }

    /**
     * Set the names of the months
     * 
     * @param names
     *            The names of the months (January, February,...)
     */
    public void setMonthNames(String[] names) {
        monthNames = names;
    }

    /**
     * Get the month names
     */
    public String[] getMonthNames() {
        return monthNames;
    }

    /**
     * Set the number when a week starts
     * 
     * @param dayNumber
     *            The number of the day
     */
    protected void setFirstDayNumber(int dayNumber) {
        firstDay = dayNumber;
    }

    /**
     * Get the number when a week starts
     */
    protected int getFirstDayNumber() {
        return firstDay;
    }

    /**
     * Set the number when a week ends
     * 
     * @param dayNumber
     *            The number of the day
     */
    protected void setLastDayNumber(int dayNumber) {
        lastDay = dayNumber;
    }

    /**
     * Get the number when a week ends
     */
    protected int getLastDayNumber() {
        return lastDay;
    }

    /**
     * Set the number when a week starts
     * 
     * @param dayNumber
     *            The number of the day
     */
    protected void setFirstHourOfTheDay(int hour) {
        firstHour = hour;
    }

    /**
     * Get the number when a week starts
     */
    protected int getFirstHourOfTheDay() {
        return firstHour;
    }

    /**
     * Set the number when a week ends
     * 
     * @param dayNumber
     *            The number of the day
     */
    protected void setLastHourOfTheDay(int hour) {
        lastHour = hour;
    }

    /**
     * Get the number when a week ends
     */
    protected int getLastHourOfTheDay() {
        return lastHour;
    }

    /**
     * Utility class used to represent a day when updating views. Only used
     * internally.
     */
    protected class Day {
        private String date;
        private String localizedDateFormat;
        private int dayOfWeek;
        private int week;

        public Day(String date, String localizedDateFormat, int dayOfWeek,
                int week) {
            super();
            this.date = date;
            this.localizedDateFormat = localizedDateFormat;
            this.dayOfWeek = dayOfWeek;
            this.week = week;
        }

        public String getDate() {
            return date;
        }

        public String getLocalizedDateFormat() {
            return localizedDateFormat;
        }

        public int getDayOfWeek() {
            return dayOfWeek;
        }

        public int getWeek() {
            return week;
        }
    }

    /**
     * 
     * @param scroll
     * @param today
     * @param daysInMonth
     * @param firstDayOfWeek
     * @param events
     */
    protected void updateWeekView(int scroll, Date today, int daysInMonth,
            int firstDayOfWeek, Collection<CalendarEvent> events, List<Day> days) {

        while (outer.getWidgetCount() > 0) {
            outer.remove(0);
        }

        monthGrid = null;
        String[] realDayNames = new String[getDayNames().length];
        int j = 0;

        if (firstDayOfWeek == 2) {
            for (int i = 1; i < getDayNames().length; i++) {
                realDayNames[j++] = getDayNames()[i];
            }
            realDayNames[j] = getDayNames()[0];
        } else {
            for (int i = 0; i < getDayNames().length; i++) {
                realDayNames[j++] = getDayNames()[i];
            }

        }

        weeklyLongEvents = new WeeklyLongEvents(this);
        if (weekGrid == null) {
            weekGrid = new WeekGrid(this, is24HFormat());
        }
        updateWeekGrid(daysInMonth, days, today, realDayNames);
        updateEventsToWeekGrid(sortEventsByDuration(events));
        outer.add(dayToolbar, DockPanel.NORTH);
        outer.add(weeklyLongEvents, DockPanel.NORTH);
        outer.add(weekGrid, DockPanel.SOUTH);
        if (!isHeightUndefined) {
            weekGrid.setHeightPX(intHeight - weeklyLongEvents.getOffsetHeight()
                    - dayToolbar.getOffsetHeight());
        } else {
            weekGrid.setHeightPX(intHeight);
        }
        weekGrid.setWidthPX(intWidth);
        dayToolbar.updateCellWidths();
        weeklyLongEvents.setWidthPX(weekGrid.getInternalWidth());
        weekGrid.setVerticalScrollPosition(scroll);
        recalculateHeights();
        recalculateWidths();
    }

    protected void updateMonthView(int firstDayOfWeek, Date today,
            int daysInMonth, Collection<CalendarEvent> events, List<Day> days) {

        while (outer.getWidgetCount() > 0) {
            outer.remove(0);
        }

        int daysPerWeek = getLastDayNumber() - getFirstDayNumber() + 1;
        String[] realDayNames = new String[daysPerWeek];
        int j = 0;

        if (firstDayOfWeek == 2) {
            for (int i = getFirstDayNumber(); i < getLastDayNumber() + 1; i++) {
                if (i == 7) {
                    realDayNames[j++] = getDayNames()[0];
                } else {
                    realDayNames[j++] = getDayNames()[i];
                }
            }
        } else {
            for (int i = getFirstDayNumber() - 1; i < getLastDayNumber(); i++) {
                realDayNames[j++] = getDayNames()[i];
            }

        }

        nameToolbar.setDayNames(realDayNames);

        weeklyLongEvents = null;
        weekGrid = null;

        updateMonthGrid(daysInMonth, days, today);

        outer.add(nameToolbar, DockPanel.NORTH);
        outer.add(weekToolbar, DockPanel.WEST);
        weekToolbar.updateCellHeights();
        outer.add(monthGrid, DockPanel.CENTER);

        updateEventsToMonthGrid(events, false);
        recalculateHeights();
        recalculateWidths();
    }

    private DateClickListener dateClickListener;

    public void setListener(DateClickListener listener) {
        dateClickListener = listener;
    }

    public DateClickListener getDateClickListener() {
        return dateClickListener;
    }

    private ForwardListener forwardListener;

    public void setListener(ForwardListener listener) {
        forwardListener = listener;
    }

    public ForwardListener getForwardListener() {
        return forwardListener;
    }

    private BackwardListener backwardListener;

    public void setListener(BackwardListener listener) {
        backwardListener = listener;
    }

    public BackwardListener getBackwardListener() {
        return backwardListener;
    }

    private WeekClickListener weekClickListener;

    public void setListener(WeekClickListener listener) {
        weekClickListener = listener;
    }

    public WeekClickListener getWeekClickListener() {
        return weekClickListener;
    }

    private RangeSelectListener rangeSelectListener;

    public void setListener(RangeSelectListener listener) {
        rangeSelectListener = listener;
    }

    public RangeSelectListener getRangeSelectListener() {
        return rangeSelectListener;
    }

    private EventClickListener eventClickListener;

    public EventClickListener getEventClickListener() {
        return eventClickListener;
    }

    public void setListener(EventClickListener listener) {
        this.eventClickListener = listener;
    }

    private EventMovedListener eventMovedListener;

    public EventMovedListener getEventMovedListener() {
        return eventMovedListener;
    }

    public void setListener(EventMovedListener eventMovedListener) {
        this.eventMovedListener = eventMovedListener;
    }

    private ScrollListener scrollListener;

    public ScrollListener getScrollListener() {
        return scrollListener;
    }

    public void setListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private EventResizeListener eventResizeListener;

    public EventResizeListener getEventResizeListener() {
        return eventResizeListener;
    }

    public void setListener(EventResizeListener eventResizeListener) {
        this.eventResizeListener = eventResizeListener;
    }

    private MouseEventListener mouseEventListener;

    public MouseEventListener getMouseEventListener() {
        return mouseEventListener;
    }

    public void setListener(MouseEventListener mouseEventListener) {
        this.mouseEventListener = mouseEventListener;
    }

    /**
     * Handles to tooltip event
     * 
     * @param event
     *          The browser event
     */
    public void handleTooltipEvent(Event event, Object key) {
        // Nothing to do, for extension purposes
    }

    /**
     * Is selecting a range allowed?
     */
    public boolean isRangeSelectAllowed() {
        return rangeSelectAllowed;
    }

    /**
     * Set selecting a range allowed
     * 
     * @param rangeSelectAllowed
     *            Should selecting a range be allowed
     */
    public void setRangeSelectAllowed(boolean rangeSelectAllowed) {
        this.rangeSelectAllowed = rangeSelectAllowed;
    }

    /**
     * Is moving a range allowed
     * 
     * @return
     */
    public boolean isRangeMoveAllowed() {
        return rangeMoveAllowed;
    }

    /**
     * Is moving a range allowed
     * 
     * @param rangeMoveAllowed
     *            Is it allowed
     */
    public void setRangeMoveAllowed(boolean rangeMoveAllowed) {
        this.rangeMoveAllowed = rangeMoveAllowed;
    }

    /**
     * Is resizing an event allowed
     */
    public boolean isEventResizeAllowed() {
        return eventResizeAllowed;
    }

    /**
     * Is resizing an event allowed
     * 
     * @param eventResizeAllowed
     *            True if allowed false if not
     */
    public void setEventResizeAllowed(boolean eventResizeAllowed) {
        this.eventResizeAllowed = eventResizeAllowed;
    }

    /**
     * Is moving an event allowed
     */
    public boolean isEventMoveAllowed() {
        return eventMoveAllowed;
    }

    /**
     * Is moving an event allowed
     * 
     * @param eventMoveAllowed
     *            True if moving is allowed, false if not
     */
    public void setEventMoveAllowed(boolean eventMoveAllowed) {
        this.eventMoveAllowed = eventMoveAllowed;
    }
}
