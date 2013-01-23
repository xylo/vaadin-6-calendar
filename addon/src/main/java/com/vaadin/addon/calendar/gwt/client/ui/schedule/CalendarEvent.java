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
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

/**
 * A client side implementation of a calendar event
 * 
 * @version
 * ${pom.version}
 */
public class CalendarEvent {
    private int index;
    private String caption;
    private Date start, end;
    private String styleName;
    private Date startTime, endTime;
    private String description;
    private int slotIndex = -1;
    private boolean format24h;

    DateTimeFormat dateformat_date = DateTimeFormat.getFormat("h:mm a");
    DateTimeFormat dateformat_date24 = DateTimeFormat.getFormat("H:mm");
    private boolean allDay;

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStyleName()
     */
    public String getStyleName() {
        return styleName;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStart()
     */
    public Date getStart() {
        return start;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStyleName()
     * @param style
     */
    public void setStyleName(String style) {
        styleName = style;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getStart()
     * @param start
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getEnd()
     * @return
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @see com.vaadin.addon.calendar.event.CalendarEvent#getEnd()
     * @param end
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * Returns the start time of the event
     * 
     * @return Time embedded in the {@link Date} object
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Set the start time of the event
     * 
     * @param startTime
     *            The time of the event. Use the time fields in the {@link Date}
     *            object
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the end time of the event
     * 
     * @return Time embedded in the {@link Date} object
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * Set the end time of the event
     * 
     * @param endTime
     *            Time embedded in the {@link Date} object
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Get the (server side) index of the event
     * 
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the index of the slot where the event in rendered
     * 
     * @return
     */
    public int getSlotIndex() {
        return slotIndex;
    }

    /**
     * Set the index of the slot where the event in rendered
     * 
     * @param index
     *          The index of the slot
     */
    public void setSlotIndex(int index) {
        slotIndex = index;
    }

    /**
     * Set the (server side) index of the event
     * 
     * @param index
     *            The index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get the caption of the event. The caption is the text displayed in the
     * calendar on the event.
     * 
     * @return
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Set the caption of the event. The caption is the text displayed in the
     * calendar on the event.
     * 
     * @param caption
     *            The visible caption of the event
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Get the description of the event. The description is the text displayed
     * when hoovering over the event with the mouse
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the event. The description is the text displayed
     * when hoovering over the event with the mouse
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Does the event use the 24h time format
     * 
     * @param format24h
     *            True if it uses the 24h format, false if it uses the 12h time
     *            format
     */
    public void setFormat24h(boolean format24h) {
        this.format24h = format24h;
    }

    /**
     * Is the event an all day event.
     * 
     * @param allDay
     *            True if the event should be rendered all day
     */
    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    /**
     * Is the event an all day event.
     * 
     * @return
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     * Get the time as a formatted string
     * 
     * @return
     */
    public String getTimeAsText() {
        if (format24h) {
            return dateformat_date24.format(startTime);
        } else {
            return dateformat_date.format(startTime);
        }
    }

    /**
     * Get the amount of milliseconds between the start and end of the event
     * 
     * @return
     */
    public long getRangeInMilliseconds() {
        return getEndTime().getTime() - getStartTime().getTime();
    }

    /**
     * Get the amount of minutes between the start and end of the event
     * 
     * @return
     */
    public long getRangeInMinutes() {
        return (getRangeInMilliseconds() / VCalendar.MINUTEINMILLIS);
    }

    /**
     * Get the amount of minutes for the event on a specific day. This is useful
     * if the event spans several days.
     * 
     * @param targetDay
     *            The date to check
     * @return
     */
    public long getRangeInMinutesForDay(Date targetDay) {
        if (isTimeOnDifferentDays()) {
            // Time range is on different days. Calculate the second day's
            // range.
            long range = (getEndTime().getTime() - getEnd().getTime())
                    / VCalendar.MINUTEINMILLIS;

            if (getEnd().compareTo(targetDay) != 0) {
                // Calculate first day's range.
                return getRangeInMinutes() - range;
            }

            return range;
        } else {
            return getRangeInMinutes();
        }
    }

    /**
     * Does the event span several days
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public boolean isTimeOnDifferentDays() {
        if (getEndTime().getTime() - getStart().getTime() > VCalendar.DAYINMILLIS) {
            return true;
        }

        if (getStart().compareTo(getEnd()) != 0) {
            if (getEndTime().getHours() == 0 && getEndTime().getMinutes() == 0) {
                return false;
            }
            return true;
        }
        return false;
    }
}