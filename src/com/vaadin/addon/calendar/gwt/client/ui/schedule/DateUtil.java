/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtil {

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
        DateTimeFormat dateformat_date = DateTimeFormat.getFormat("yyyy-MM-dd");
        return dateformat_date.format(date);
    }

    /**
     * @param date
     *            the date to format
     * @return given Date as String, for communicating to server-side
     */
    public static Object formatClientSideTime(Date date) {
        DateTimeFormat dateformat_date = DateTimeFormat.getFormat("HH-mm");
        return dateformat_date.format(date);
    }
}
