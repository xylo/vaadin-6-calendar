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
