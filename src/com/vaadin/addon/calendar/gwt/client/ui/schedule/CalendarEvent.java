/**
 * 
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

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

    public String getStyleName() {
        return styleName;
    }

    public Date getStart() {
        return start;
    }

    public void setStyleName(String style) {
        this.styleName = style;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getIndex() {
        return index;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(int i) {
        slotIndex = i;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormat24h(boolean format24h) {
        this.format24h = format24h;
    }

    public String getTimeAsText() {
        if (format24h) {
            return dateformat_date24.format(startTime);
        } else {
            return dateformat_date.format(startTime);
        }
    }

    public long getRangeInMilliseconds() {
        return getEndTime().getTime() - getStartTime().getTime();
    }

    public long getRangeInMinutes() {
        return (getRangeInMilliseconds() / VCalendar.MINUTEINMILLIS);
    }

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
        } else
            return getRangeInMinutes();
    }

    @SuppressWarnings("deprecation")
    public boolean isTimeOnDifferentDays() {
        if (getEndTime().getTime() - getStart().getTime() > VCalendar.DAYINMILLIS)
            return true;

        if (getStart().compareTo(getEnd()) != 0) {
            if (getEndTime().getHours() == 0 && getEndTime().getMinutes() == 0) {
                return false;
            }
            return true;
        }
        return false;
    }
}