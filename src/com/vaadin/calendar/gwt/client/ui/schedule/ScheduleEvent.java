/**
 * 
 */
package com.vaadin.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class ScheduleEvent {
    private int index;
    private String caption;
    private Date fromDate, toDate;
    private String styleName;
    private Date fromDatetime, toDatetime;
    private String description;
    private int slotIndex = -1;
    private boolean format24h;

    DateTimeFormat dateformat_date = DateTimeFormat.getFormat("h:mm a");
    DateTimeFormat dateformat_date24 = DateTimeFormat.getFormat("H:mm");

    public String getStyleName() {
        return styleName;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setStyleName(String style) {
        this.styleName = style;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromDatetime() {
        return fromDatetime;
    }

    public void setFromDatetime(Date fromDatetime) {
        this.fromDatetime = fromDatetime;
    }

    public Date getToDatetime() {
        return toDatetime;
    }

    public void setToDatetime(Date toDatetime) {
        this.toDatetime = toDatetime;
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
            return dateformat_date24.format(fromDatetime);
        } else {
            return dateformat_date.format(fromDatetime);
        }
    }

}