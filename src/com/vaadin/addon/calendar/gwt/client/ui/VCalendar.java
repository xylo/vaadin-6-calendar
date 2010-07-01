package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEvent;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.DayToolbar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.MonthGrid;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleDayCell;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleDayToolbar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleWeekToolbar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeeklyLongEvents;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.TooltipInfo;
import com.vaadin.terminal.gwt.client.UIDL;

public class VCalendar extends Composite implements Paintable {

    public static final String ATTR_WEEK = "w";
    public static final String ATTR_DOW = "dow";
    public static final String ATTR_FDATE = "fdate";
    public static final String ATTR_DATE = "date";
    public static final String ATTR_STYLE = "extracss";
    public static final String ATTR_DESCRIPTION = "desc";
    public static final String ATTR_TIMETO = "tto";
    public static final String ATTR_TIMEFROM = "tfrom";
    public static final String ATTR_DATETO = "dto";
    public static final String ATTR_DATEFROM = "dfrom";
    public static final String ATTR_CAPTION = "caption";
    public static final String ATTR_INDEX = "i";
    public static final String ATTR_SCROLL = "scroll";
    public static final String ATTR_FDOW = "fdow";
    public static final String ATTR_NOW = "now";
    public static final String ATTR_READONLY = "readonly";
    public static final String ATTR_HIDE_WEEKENDS = "hideWeekends";
    public static final String ATTR_MONTH_NAMES = "mNames";
    public static final String ATTR_DAY_NAMES = "dNames";
    public static final String ATTR_FORMAT24H = "format24h";
    public static final String ATTR_ALLDAY = "allday";
    public static final String ATTR_NAVIGATION = "navigation";

    public static final long MINUTEINMILLIS = 60 * 1000;
    public static final long HOURINMILLIS = 60 * MINUTEINMILLIS;
    public static final long DAYINMILLIS = 24 * HOURINMILLIS;
    public static final long WEEKINMILLIS = 7 * DAYINMILLIS;

    private String PID;
    private boolean hideWeekends;
    private String[] monthNames;
    private String[] dayNames;
    private boolean format;
    private DockPanel outer = new DockPanel();
    private int rows;
    private ApplicationConnection client;
    private String height = null;
    private String width = null;
    private SimpleDayToolbar nameToolbar = new SimpleDayToolbar();
    private DayToolbar dayToolbar = new DayToolbar();
    private SimpleWeekToolbar weekToolbar;
    private WeeklyLongEvents weeklyLongEvents;
    private MonthGrid monthGrid;
    private WeekGrid weekGrid;
    private int intWidth = 0;
    private int intHeight = 0;

    private DateTimeFormat dateformat_datetime = DateTimeFormat
            .getFormat("yyyy-MM-dd HH:mm:ss");
    private DateTimeFormat dateformat_date = DateTimeFormat
            .getFormat("yyyy-MM-dd");
    private DateTimeFormat time12format_date = DateTimeFormat
            .getFormat("h:mm a");
    private DateTimeFormat time24format_date = DateTimeFormat
            .getFormat("HH:mm");

    private boolean readOnly = false;

    private boolean isHeightUndefined = false;

    private boolean isWidthUndefined = false;

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

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and let the containing layout manage caption, etc.
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save reference to server connection object to be able to send
        // user interaction later
        this.client = client;
        PID = uidl.getId();
        format = uidl.getBooleanAttribute(ATTR_FORMAT24H);
        dayNames = uidl.getStringArrayAttribute(ATTR_DAY_NAMES);
        monthNames = uidl.getStringArrayAttribute(ATTR_MONTH_NAMES);
        hideWeekends = uidl.getBooleanAttribute(ATTR_HIDE_WEEKENDS);
        if (uidl.hasAttribute(ATTR_READONLY)) {
            readOnly = uidl.getBooleanAttribute(ATTR_READONLY);
        }

        UIDL daysUidl = uidl.getChildUIDL(0);
        int daysCount = daysUidl.getChildCount();
        while (outer.getWidgetCount() > 0) {
            outer.remove(0);
        }

        if (daysCount > 7) {
            updateMonthView(uidl, daysUidl);

        } else {
            updateWeekView(uidl, daysUidl);
        }

    }

    private void updateMonthView(UIDL uidl, UIDL daysUidl) {
        int firstDayOfWeek = uidl.getIntAttribute(ATTR_FDOW);
        Date today = dateformat_datetime.parse(uidl
                .getStringAttribute(ATTR_NOW));

        if (hideWeekends) {
            nameToolbar.setDayNames(new String[] { dayNames[1], dayNames[2],
                    dayNames[3], dayNames[4], dayNames[5] });
        } else if (firstDayOfWeek == 2) {
            nameToolbar.setDayNames(new String[] { dayNames[1], dayNames[2],
                    dayNames[3], dayNames[4], dayNames[5], dayNames[6],
                    dayNames[0] });
        } else {
            nameToolbar.setDayNames(new String[] { dayNames[0], dayNames[1],
                    dayNames[2], dayNames[3], dayNames[4], dayNames[5],
                    dayNames[6] });
        }

        weeklyLongEvents = null;
        weekGrid = null;
        updateMonthGrid(daysUidl.getChildCount(), daysUidl, today);
        outer.add(nameToolbar, DockPanel.NORTH);
        outer.add(weekToolbar, DockPanel.WEST);
        weekToolbar.updateCellHeights();
        outer.add(monthGrid, DockPanel.CENTER);
        ArrayList<CalendarEvent> events = getEvents(uidl.getChildUIDL(1));
        updateEventsToMonthGrid(events, false);
        recalculateHeights();
        recalculateWidths();
    }

    private void updateWeekView(UIDL uidl, UIDL daysUidl) {
        int scroll = uidl.getIntVariable(ATTR_SCROLL);
        Date today = dateformat_datetime.parse(uidl
                .getStringAttribute(ATTR_NOW));

        monthGrid = null;
        Collection<CalendarEvent> events = getEvents(uidl.getChildUIDL(1));

        weeklyLongEvents = new WeeklyLongEvents(this);
        if (weekGrid == null) {
            weekGrid = new WeekGrid(this, format);
        }
        updateWeekGrid(daysUidl.getChildCount(), daysUidl, today);
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

    private void updateEventsToMonthGrid(Collection<CalendarEvent> events,
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
                        && isEventInDayWithTime(when, to, sdc.getDate(), e
                                .getEndTime(), e.isAllDay())) {
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
     * hours, thus spanning two days. That special case still should span one
     * day when rendered.
     */
    @SuppressWarnings("deprecation")
    // Date methods are not deprecated in GWT
    private boolean isEventInDayWithTime(Date from, Date to, Date date,
            Date endTime, boolean isAllDay) {
        return (isAllDay || !(to.getDay() == date.getDay()
                && from.getDay() != to.getDay() && endTime.getMinutes() == 0));
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
        Arrays.sort(sorted, new Comparator<CalendarEvent>() {

            public int compare(CalendarEvent o1, CalendarEvent o2) {
                if (o1.isAllDay() != o2.isAllDay()) {
                    if (o2.isAllDay())
                        return 1;
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
        });
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

    /** Transforms uidl to list of ScheduleEvents */
    protected ArrayList<CalendarEvent> getEvents(UIDL childUIDL) {
        int eventCount = childUIDL.getChildCount();
        ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
        for (int i = 0; i < eventCount; i++) {
            UIDL eventUIDL = childUIDL.getChildUIDL(i);

            int index = eventUIDL.getIntAttribute(ATTR_INDEX);
            String caption = eventUIDL.getStringAttribute(ATTR_CAPTION);
            String datefrom = eventUIDL.getStringAttribute(ATTR_DATEFROM);
            String dateto = eventUIDL.getStringAttribute(ATTR_DATETO);
            String timefrom = eventUIDL.getStringAttribute(ATTR_TIMEFROM);
            String timeto = eventUIDL.getStringAttribute(ATTR_TIMETO);
            String desc = eventUIDL.getStringAttribute(ATTR_DESCRIPTION);
            String style = eventUIDL.getStringAttribute(ATTR_STYLE);
            boolean allDay = eventUIDL.getBooleanAttribute(ATTR_ALLDAY);

            CalendarEvent e = new CalendarEvent();

            e.setCaption(caption);
            e.setDescription(desc);
            e.setIndex(index);
            e.setEnd(dateformat_date.parse(dateto));
            e.setStart(dateformat_date.parse(datefrom));
            e
                    .setStartTime(dateformat_datetime.parse(datefrom + " "
                            + timefrom));
            e.setEndTime(dateformat_datetime.parse(dateto + " " + timeto));
            e.setStyleName(style);
            e.setFormat24h(format);
            e.setAllDay(allDay);

            events.add(e);

            registerEventToolTip(e);
        }
        return events;
    }

    /**
     * Register the description of the event as a tooltip for this paintable.
     * This way, any event displaying widget can use the event index as a key to
     * display the tooltip.
     */
    private void registerEventToolTip(CalendarEvent e) {
        if (e.getDescription() != null && !"".equals(e.getDescription())) {
            TooltipInfo info = new TooltipInfo(e.getDescription());
            client.registerTooltip(this, e.getIndex(), info);

        } else {
            client.registerTooltip(this, e.getIndex(), null);
        }
    }

    @SuppressWarnings("deprecation")
    public void updateWeekGrid(int daysCount, UIDL daysUidl, Date today) {
        if (format != weekGrid.isFormat24h()) {
            // Time format has changed, update time bar.
            weekGrid.setFormat24h(format);
            weekGrid.getTimeBar().updateTimeBar(format);
        }
        dayToolbar.clear();
        dayToolbar.addBackButton();
        dayToolbar.setVerticalSized(isHeightUndefined);
        dayToolbar.setHorizontalSized(isWidthUndefined);
        weekGrid.clearDates();
        weekGrid.setReadOnly(readOnly);
        for (int i = 0; i < daysCount; i++) {
            UIDL dayUidl = daysUidl.getChildUIDL(i);
            String date = dayUidl.getStringAttribute(ATTR_DATE);
            String localized_date_format = dayUidl
                    .getStringAttribute(ATTR_FDATE);
            Date d = dateformat_date.parse(date);
            int dayOfWeek = dayUidl.getIntAttribute(ATTR_DOW);
            if (hideWeekends && (dayOfWeek == 1 || dayOfWeek == 7)) {
                continue;
            }
            boolean isToday = false;
            int dayOfMonth = d.getDate();
            if (today.getDate() == dayOfMonth && today.getYear() == d.getYear()
                    && today.getMonth() == d.getMonth()) {
                isToday = true;
            }
            dayToolbar.add(dayNames[dayOfWeek - 1], date,
                    localized_date_format, isToday ? "today" : null);
            weeklyLongEvents.addDate(d);
            weekGrid.addDate(d);
            if (isToday) {
                weekGrid.setToday(d, today);
            }
        }
        dayToolbar.addNextButton();
    }

    @SuppressWarnings("deprecation")
    private void updateMonthGrid(int daysCount, UIDL daysUidl, Date today) {
        rows = (int) Math.ceil(daysCount / (double) 7);
        int columns = (hideWeekends == true ? 5 : 7);
        monthGrid = new MonthGrid(this, rows, columns);
        monthGrid.setReadOnly(readOnly);
        monthGrid.setHeightPX(intHeight);
        monthGrid.setWidthPX(intWidth);
        weekToolbar.removeAllRows();
        int pos = 0;
        for (int i = 0; i < daysCount; i++) {
            UIDL dayUidl = daysUidl.getChildUIDL(i);
            String date = dayUidl.getStringAttribute(ATTR_DATE);
            Date d = dateformat_date.parse(date);
            int dayOfWeek = dayUidl.getIntAttribute(ATTR_DOW);
            int week = dayUidl.getIntAttribute(ATTR_WEEK);
            if (hideWeekends && (dayOfWeek == 1 || dayOfWeek == 7)) {
                continue;
            }
            int y = (pos / columns);
            int x = pos - (y * columns);
            if (x == 0 && daysCount > 7) {
                // Add week to weekToolbar for navigation
                weekToolbar.addWeek(week, d.getYear());
            }
            SimpleDayCell cell = new SimpleDayCell(this, y, x);
            cell.setDate(d);
            int dayOfMonth = d.getDate();
            if (dayOfMonth == 1
                    || (hideWeekends && x == 0 && (dayOfMonth == 2 || dayOfMonth == 3))) {
                cell.setMonthNameVisible(true);
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
                intHeight = Integer.parseInt(newHeight.substring(0, newHeight
                        .length() - 2));
            } else {
                intHeight = -1;
            }

            super.setHeight(height);
            recalculateHeights();
        }
    }

    private void recalculateHeights() {
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

    private void recalculateWidths() {
        if (!isWidthUndefined) {
            outer.setWidth(intWidth + "px");
            super.setWidth(intWidth + "px");
            nameToolbar.setWidthPX(intWidth);
            dayToolbar.setWidthPX(intWidth);

            if (monthGrid != null) {
                monthGrid.updateCellSizes(intWidth
                        - weekToolbar.getOffsetWidth(), intHeight
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
                intWidth = Integer.parseInt(newWidth.substring(0, newWidth
                        .length() - 2));
            } else {
                intWidth = -1;
            }
            recalculateWidths();
        }
    }

    public ApplicationConnection getClient() {
        return client;
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

    public String[] getMonthNames() {
        return monthNames;
    }

    public String getPID() {
        return PID;
    }

    public boolean isReadOnly() {
        return readOnly;
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
}
