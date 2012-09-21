package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.List;

import com.vaadin.shared.ComponentState;

/**
 * 
 * @author Johannes
 * 
 */
public class CalendarState extends ComponentState {

    private boolean format24H;
    private String[] dayNames;
    private String[] monthNames;
    private int firstDayOfWeek;
    private int lastDayOfWeek;
    private int firstHourOfDay;
    private int lastHourOfDay;
    private int fdow;
    private int scroll;
    private String now;
    private List<CalendarState.Day> days;
    private List<CalendarState.Event> events;
    private List<CalendarState.Action> actions;

    public static class Day implements java.io.Serializable {
        private String date;
        private String localizedDateFormat;
        private int dayOfWeek;
        private int week;

        public Day() {

        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLocalizedDateFormat() {
            return localizedDateFormat;
        }

        public void setLocalizedDateFormat(String localizedDateFormat) {
            this.localizedDateFormat = localizedDateFormat;
        }

        public int getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(int dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }
    }

    public static class Action implements java.io.Serializable {

        private String caption;
        private String iconKey;
        private String actionKey;
        private String startDate;
        private String endDate;

        public Action() {

        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getIconKey() {
            return iconKey;
        }

        public void setIconKey(String iconKey) {
            this.iconKey = iconKey;
        }

        public String getActionKey() {
            return actionKey;
        }

        public void setActionKey(String actionKey) {
            this.actionKey = actionKey;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

    }

    public static class Event implements java.io.Serializable {
        private int index;
        private String caption;
        private String dateFrom;
        private String dateTo;
        private String timeFrom;
        private String timeTo;
        private String styleName;
        private String description;
        private boolean allDay;

        public Event() {

        }

        public String getDateFrom() {
            return dateFrom;
        }

        public void setDateFrom(String dateFrom) {
            this.dateFrom = dateFrom;
        }

        public String getDateTo() {
            return dateTo;
        }

        public void setDateTo(String dateTo) {
            this.dateTo = dateTo;
        }

        public String getTimeFrom() {
            return timeFrom;
        }

        public void setTimeFrom(String timeFrom) {
            this.timeFrom = timeFrom;
        }

        public String getTimeTo() {
            return timeTo;
        }

        public void setTimeTo(String timeTo) {
            this.timeTo = timeTo;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getStyleName() {
            return styleName;
        }

        public void setStyleName(String styleName) {
            this.styleName = styleName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isAllDay() {
            return allDay;
        }

        public void setAllDay(boolean allDay) {
            this.allDay = allDay;
        }
    }

    public boolean isFormat24H() {
        return format24H;
    }

    public void setFormat24H(boolean format24h) {
        format24H = format24h;
    }

    public String[] getDayNames() {
        return dayNames;
    }

    public void setDayNames(String[] dayNames) {
        this.dayNames = dayNames;
    }

    public String[] getMonthNames() {
        return monthNames;
    }

    public void setMonthNames(String[] monthNames) {
        this.monthNames = monthNames;
    }

    /**
     * Returns the integer first visible day of week to be shown in calendar
     * 
     * @return
     */
    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /**
     * Sets the first visible day of week to be shown in calendar
     * 
     * @param firstDayOfWeek
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public int getLastDayOfWeek() {
        return lastDayOfWeek;
    }

    public void setLastDayOfWeek(int lastDayOfWeek) {
        this.lastDayOfWeek = lastDayOfWeek;
    }

    public int getFirstHourOfDay() {
        return firstHourOfDay;
    }

    public void setFirstHourOfDay(int firstHourOfDay) {
        this.firstHourOfDay = firstHourOfDay;
    }

    public int getLastHourOfDay() {
        return lastHourOfDay;
    }

    public void setLastHourOfDay(int lastHourOfDay) {
        this.lastHourOfDay = lastHourOfDay;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<CalendarState.Action> getActions() {
        return actions;
    }

    public void setActions(List<CalendarState.Action> actions) {
        this.actions = actions;
    }

    public int getScroll() {
        return scroll;
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
    }

    /**
     * Returns the first day of week specified by current locale
     * 
     * @return
     */
    public int getFDOW() {
        return fdow;
    }

    /**
     * Sets the first day of week specified by locale
     * 
     * @param fdow
     */
    public void setFDOW(int fdow) {
        this.fdow = fdow;
    }
}
