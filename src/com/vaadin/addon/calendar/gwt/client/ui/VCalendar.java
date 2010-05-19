package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.ArrayList;
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
import com.vaadin.terminal.gwt.client.UIDL;

public class VCalendar extends Composite implements Paintable {

    public static final long MINUTEINMILLIS = 60 * 1000;
    public static final long HOURINMILLIS = 60 * 60 * 1000;
    public static final long DAYINMILLIS = 24 * HOURINMILLIS;

    private String PID;
    private boolean hideWeekends;
    private String[] monthNames;
    private String[] dayNames;
    private boolean format;
    private DockPanel outer = new DockPanel();
    private int rows;
    private ApplicationConnection client;
    private String height = "";
    private String width = "";
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
        format = uidl.getBooleanAttribute("format24h");
        dayNames = uidl.getStringArrayAttribute("dayNames");
        monthNames = uidl.getStringArrayAttribute("monthNames");
        hideWeekends = uidl.getBooleanAttribute("hideWeekends");
        if (uidl.hasAttribute("readonly")) {
            readOnly = uidl.getBooleanAttribute("readonly");
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
        int firstDayOfWeek = uidl.getIntAttribute("fdow");
        Date today = dateformat_datetime.parse(uidl.getStringAttribute("now"));

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
        updateEventsToMonthGrid(events);
        recalculateHeights();
    }

    private void updateWeekView(UIDL uidl, UIDL daysUidl) {
        int scroll = uidl.getIntVariable("scroll");
        Date today = dateformat_datetime.parse(uidl.getStringAttribute("now"));

        monthGrid = null;
        ArrayList<CalendarEvent> events = getEvents(uidl.getChildUIDL(1));

        weeklyLongEvents = new WeeklyLongEvents();
        if (weekGrid == null) {
            weekGrid = new WeekGrid(this, format);
        }
        updateWeekGrid(daysUidl.getChildCount(), daysUidl, today);
        updateEventsToWeekGrid(events);
        outer.add(dayToolbar, DockPanel.NORTH);
        outer.add(weeklyLongEvents, DockPanel.NORTH);
        outer.add(weekGrid, DockPanel.SOUTH);
        weekGrid.setHeightPX(intHeight - weeklyLongEvents.getOffsetHeight()
                - dayToolbar.getOffsetHeight());
        weekGrid.setWidthPX(intWidth);
        dayToolbar.updateCellWidths();
        weeklyLongEvents.setWidthPX(weekGrid.getInternalWidth());
        weekGrid.setScrollPosition(scroll);
    }

    private void updateEventsToWeekGrid(ArrayList<CalendarEvent> events) {
        List<CalendarEvent> allDayLong = new ArrayList<CalendarEvent>();
        List<CalendarEvent> belowDayLong = new ArrayList<CalendarEvent>();
        long rangeInMillis = 0;
        for (CalendarEvent e : events) {
            rangeInMillis = e.getRangeInMilliseconds();
            if (rangeInMillis >= DAYINMILLIS || rangeInMillis == 0) {
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

    private void updateEventsToMonthGrid(ArrayList<CalendarEvent> events) {
        for (CalendarEvent e : events) {
            Date when = e.getStart();
            Date to = e.getEnd();
            boolean eventAdded = false;
            boolean inProgress = false; // Event adding has started
            for (int row = 0; row < monthGrid.getRowCount(); row++) {
                if (eventAdded) {
                    break;
                }
                for (int cell = 0; cell < monthGrid.getCellCount(row); cell++) {
                    SimpleDayCell sdc = (SimpleDayCell) monthGrid.getWidget(
                            row, cell);
                    if (isEventInDay(when, to, sdc.getDate())) {
                        sdc.addScheduleEvent(e);
                        inProgress = true;
                        continue;
                    } else if (inProgress) {
                        eventAdded = true;
                        inProgress = false;
                        break;
                    }
                }
            }

        }
    }

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
            int index = eventUIDL.getIntAttribute("i");
            String caption = eventUIDL.getStringAttribute("caption");
            String datefrom = eventUIDL.getStringAttribute("dfrom");
            String dateto = eventUIDL.getStringAttribute("dto");
            String timefrom = eventUIDL.getStringAttribute("tfrom");
            String timeto = eventUIDL.getStringAttribute("tto");
            String desc = eventUIDL.getStringAttribute("desc");
            String style = eventUIDL.getStringAttribute("extracss");
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
            events.add(e);
        }
        return events;
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
        weekGrid.clearDates();
        weekGrid.setReadOnly(readOnly);
        for (int i = 0; i < daysCount; i++) {
            UIDL dayUidl = daysUidl.getChildUIDL(i);
            String date = dayUidl.getStringAttribute("date");
            String localized_date_format = dayUidl.getStringAttribute("fdate");
            Date d = dateformat_date.parse(date);
            int dayOfWeek = dayUidl.getIntAttribute("dow");
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
        weekToolbar.removeAllRows();
        int pos = 0;
        for (int i = 0; i < daysCount; i++) {
            UIDL dayUidl = daysUidl.getChildUIDL(i);
            String date = dayUidl.getStringAttribute("date");
            Date d = dateformat_date.parse(date);
            int dayOfWeek = dayUidl.getIntAttribute("dow");
            int week = dayUidl.getIntAttribute("w");
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
        if (!height.equals(newHeight)) {
            height = newHeight;
            intHeight = Integer.parseInt(newHeight.substring(0, newHeight
                    .length() - 2));
            super.setHeight(intHeight + "px");
            recalculateHeights();
        }
    }

    private void recalculateHeights() {
        if (monthGrid != null) {
            monthGrid.updateCellSizes(intWidth - weekToolbar.getOffsetWidth(),
                    intHeight - nameToolbar.getOffsetHeight());
            weekToolbar.setHeightPX(intHeight - nameToolbar.getOffsetHeight());
        } else if (weekGrid != null) {
            weekGrid.setHeightPX(intHeight - weeklyLongEvents.getOffsetHeight()
                    - dayToolbar.getOffsetHeight());
        }
    }

    private void recalculateWidths() {
        outer.setWidth(intWidth + "px");
        super.setWidth(intWidth + "px");
        nameToolbar.setWidthPX(intWidth);
        dayToolbar.setWidthPX(intWidth);
        if (monthGrid != null) {
            monthGrid.updateCellSizes(intWidth - weekToolbar.getOffsetWidth(),
                    intHeight - nameToolbar.getOffsetHeight());
        } else if (weekGrid != null) {
            weekGrid.setWidthPX(intWidth);
            weeklyLongEvents.setWidthPX(weekGrid.getInternalWidth());
        }
    }

    @Override
    public void setWidth(String newWidth) {
        if (!width.equals(newWidth)) {
            width = newWidth;
            intWidth = Integer.parseInt(newWidth.substring(0,
                    newWidth.length() - 2));
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

}
