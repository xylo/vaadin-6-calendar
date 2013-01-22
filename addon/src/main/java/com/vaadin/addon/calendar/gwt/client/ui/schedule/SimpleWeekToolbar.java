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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

public class SimpleWeekToolbar extends FlexTable implements ClickHandler {
    private int height;
    private VCalendar calendar;
    private boolean isHeightUndefined;

    public SimpleWeekToolbar(VCalendar parent) {
        calendar = parent;
        setCellSpacing(0);
        setCellPadding(0);
        setStyleName("v-calendar-week-numbers");
    }

    public void addWeek(int week, int year) {
        WeekLabel l = new WeekLabel(week + "", week, year);
        l.addClickHandler(this);
        int rowCount = getRowCount();
        insertRow(rowCount);
        setWidget(rowCount, 0, l);
        updateCellHeights();
    }

    public void updateCellHeights() {
        if (!isHeightUndefined()) {
            int rowCount = getRowCount();
            if (rowCount == 0) {
                return;
            }
            int cellheight = (height / rowCount) - 1;
            int remainder = height % rowCount;
            if (cellheight < 0) {
                cellheight = 0;
            }
            for (int i = 0; i < rowCount; i++) {
                if (remainder > 0) {
                    getWidget(i, 0).setHeight(cellheight + 1 + "px");
                } else {
                    getWidget(i, 0).setHeight(cellheight + "px");
                }
                getWidget(i, 0).getElement().getStyle().setProperty(
                        "lineHeight", cellheight + "px");
                remainder--;
            }
        } else {
            for (int i = 0; i < getRowCount(); i++) {
                getWidget(i, 0).setHeight("");
                getWidget(i, 0).getElement().getStyle().setProperty(
                        "lineHeight", "");
            }
        }
    }

    public void setHeightPX(int intHeight) {
        setHeightUndefined(intHeight == -1);
        height = intHeight;
        updateCellHeights();
    }

    public boolean isHeightUndefined() {
        return isHeightUndefined;
    }

    public void setHeightUndefined(boolean isHeightUndefined) {
        this.isHeightUndefined = isHeightUndefined;

        if (isHeightUndefined) {
            addStyleDependentName("Vsized");

        } else {
            removeStyleDependentName("Vsized");
        }
    }

    public void onClick(ClickEvent event) {
        WeekLabel wl = (WeekLabel) event.getSource();
        if (calendar.getWeekClickListener() != null) {
            calendar.getWeekClickListener().weekClick(
                    wl.getYear() + "w" + wl.getWeek());
        }
    }

    static class WeekLabel extends Label {
        private int week;
        private int year;

        public WeekLabel(String string, int week2, int year2) {
            super(string);
            setStylePrimaryName("v-calendar-week-number");
            week = week2;
            year = year2;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }
    }
}