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
package com.vaadin.addon.calendar.ui;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * Class for representing a date range.
 * 
 * @since 1.2.2
 * @version
 * @VERSION@
 */
@SuppressWarnings("serial")
public class CalendarDateRange implements Serializable {

    private Date start;

    private Date end;

    private final transient TimeZone tz;

    private static final String DELIMITER = ",";

    /**
     * Constructor
     * 
     * @param start
     *            The start date and time of the date range
     * @param end
     *            The end date and time of the date range
     */
    public CalendarDateRange(Date start, Date end, TimeZone tz) {
        super();
        this.start = start;
        this.end = end;
        this.tz = tz;
    }

    /**
     * Get the start date of the date range
     * 
     * @return the start Date of the range
     */
    public Date getStart() {
        return start;
    }

    /**
     * Get the end date of the date range
     * 
     * @return the end Date of the range
     */
    public Date getEnd() {
        return end;
    }

    /**
     * Is a date in the date range
     * 
     * @param date
     *            The date to check
     * @return true if the date range contains a date start and end of range
     *         inclusive; false otherwise
     */
    public boolean inRange(Date date) {
        if (date == null) {
            return false;
        }

        return date.compareTo(start) >= 0 && date.compareTo(end) <= 0;
    }

    /**
     * Serialize the calendar range event for sending to the client side
     * implementation
     * 
     * @return the date range serialized as String
     */
    public String serialize() {
        return start.getTime() + DELIMITER + end.getTime();
    }

    /**
     * Un-serialize a serialized range and set the values to the serialized
     * values
     * 
     * @param serialized
     *            The serialized version of the string
     */
    public void unserialize(String serialized) {
        if (serialized != null) {
            String[] parts = serialized.split(DELIMITER);
            GregorianCalendar cal = new GregorianCalendar(tz);
            if (parts.length >= 2) {
                cal.clear();
                cal.setTimeInMillis(Long.valueOf(parts[0]));
                start = cal.getTime();
                cal.setTimeInMillis(Long.valueOf(parts[1]));
                end = cal.getTime();
            } else {
                Logger.getLogger(CalendarDateRange.class.getName()).warning(
                        "Could not desialize string '" + serialized + "'");
            }
        } else {
            Logger.getLogger(CalendarDateRange.class.getName()).warning(
                    "Could not desialize null string");
        }
    }
}
