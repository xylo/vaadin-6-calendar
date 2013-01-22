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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleDayToolbar extends HorizontalPanel {
    private int width = 0;
    private boolean isWidthUndefined = false;

    public SimpleDayToolbar() {
        setStylePrimaryName("v-calendar-header-month");
    }

    public void setDayNames(String[] dayNames) {
        clear();
        for (int i = 0; i < dayNames.length; i++) {
            Label l = new Label(dayNames[i]);
            l.setStylePrimaryName("v-calendar-header-day");
            add(l);
        }
        updateCellWidth();
    }

    public void setWidthPX(int width) {
        GWT.log("SimpleDayToolbar width " + width);
        this.width = width;

        setWidthUndefined(width == -1);

        if (!isWidthUndefined()) {
            super.setWidth(this.width + "px");
            if (getWidgetCount() == 0) {
                return;
            }
        }
        updateCellWidth();
    }

    private boolean isWidthUndefined() {
        return isWidthUndefined;
    }

    private void setWidthUndefined(boolean isWidthUndefined) {
        this.isWidthUndefined = isWidthUndefined;

        if (isWidthUndefined) {
            addStyleDependentName("Hsized");

        } else {
            removeStyleDependentName("Hsized");
        }
    }

    private void updateCellWidth() {
        int cellw = -1;
        int widgetCount = getWidgetCount();
        if (widgetCount <= 0) {
            return;
        }
        if (isWidthUndefined()) {
            Widget widget = getWidget(0);
            String w = widget.getElement().getStyle().getWidth();
            if (w.length() > 2) {
                cellw = Integer.parseInt(w.substring(0, w.length() - 2));
            }
        } else {
            cellw = width / getWidgetCount();
        }
        if (cellw > 0) {
            for (int i = 0; i < getWidgetCount(); i++) {
                Widget widget = getWidget(i);
                setCellWidth(widget, cellw + "px");
            }
        }
    }
}