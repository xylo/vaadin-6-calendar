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

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Utility class for {@link Date} operations
 * 
 */
public class DateUtil {

    public static final String CLIENT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String CLIENT_TIME_FORMAT = "HH-mm";

    /**
     * Checks if dates are same day without checking datetimes.
     * 
     * @param date1
     * @param date2
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean compareDate(Date date1, Date date2) {
        if (date1.getDate() == date2.getDate()
                && date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()) {
            return true;
        }
        return false;
    }

    /**
     * @param date
     *            the date to format
     * 
     * @return given Date as String, for communicating to server-side
     */
    public static String formatClientSideDate(Date date) {
        DateTimeFormat dateformat_date = DateTimeFormat
                .getFormat(CLIENT_DATE_FORMAT);
        return dateformat_date.format(date);
    }

    /**
     * @param date
     *            the date to format
     * @return given Date as String, for communicating to server-side
     */
    public static String formatClientSideTime(Date date) {
        DateTimeFormat dateformat_date = DateTimeFormat
                .getFormat(CLIENT_TIME_FORMAT);
        return dateformat_date.format(date);
    }
}
