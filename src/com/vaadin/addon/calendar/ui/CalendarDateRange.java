/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.ui;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Class for representing a date range.
 * 
 * @since 1.3.0
 * @version
 * @VERSION@
 */
@SuppressWarnings("serial")
public class CalendarDateRange implements Serializable {

    private Date start;

    private Date end;

    private static final String DELIMITER = ",";

    /**
     * Constructor
     * 
     * @param start
     *            The start date and time of the date range
     * @param end
     *            The end date and time of the date range
     */
    public CalendarDateRange(Date start, Date end) {
        super();
        this.start = start;
        this.end = end;
    }

    /**
     * Get the start date of the date range
     * 
     * @return
     */
    public Date getStart() {
        return start;
    }

    /**
     * Get the end date of the date range
     * 
     * @return
     */
    public Date getEnd() {
        return end;
    }

    public boolean inRange(Date date) {
        if (date == null) {
            return false;
        }
        return start.compareTo(date) <= 0 && end.compareTo(date) >= 0;
    }

    /**
     * Serialize the calendar range event for sending to the client side implementation
     * 
     * @return
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
            if (parts.length >= 2) {
                start = new Date(Long.valueOf(parts[0]));
                end = new Date(Long.valueOf(parts[1]));
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
