package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

public class ReservedCalendarEvent {
    private Date fromDate, toDate, fromDatetime, toDatetime;

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDatetime(Date fromDatetime) {
        this.fromDatetime = fromDatetime;
    }

    public Date getFromDatetime() {
        return fromDatetime;
    }

    public void setToDatetime(Date toDatetime) {
        this.toDatetime = toDatetime;
    }

    public Date getToDatetime() {
        return toDatetime;
    }

}
