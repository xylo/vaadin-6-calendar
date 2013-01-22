/**
 * Copyright (C) 2010 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
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

    public CalendarDay(String date, String localizedDateFormat, int dayOfWeek,
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
