/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
