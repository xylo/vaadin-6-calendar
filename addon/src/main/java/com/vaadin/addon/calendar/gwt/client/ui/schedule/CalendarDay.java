package com.vaadin.addon.calendar.gwt.client.ui.schedule;

/**
 * Utility class used to represent a day when updating views. Only used
 * internally.
 */
public class CalendarDay {
    private String date;
    private String localizedDateFormat;
    private int dayOfWeek;
    private int week;

    public CalendarDay(String date, String localizedDateFormat, int dayOfWeek, int week) {
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
