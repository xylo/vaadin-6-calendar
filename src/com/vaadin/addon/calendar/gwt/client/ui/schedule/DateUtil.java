package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

public class DateUtil {

    /**
     * Return week for given date without using Calendar.
     * 
     * @param d
     * @return
     */
    public static int getWeek(Date d) {
        Date onejan = new Date(1970, 0, 1);
        return (int) Math.ceil((((d.getTime() - onejan.getTime()) / 86400000)
                + onejan.getDay() + 1) / 7);
    }

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

}
