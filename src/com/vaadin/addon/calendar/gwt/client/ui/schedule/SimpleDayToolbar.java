/**
 * 
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleDayToolbar extends HorizontalPanel {
    private int width = 0;
    private boolean isHeightUndefined;

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
            updateCellWidth();
        }
    }

    private boolean isWidthUndefined() {
        return isHeightUndefined;
    }

    private void setWidthUndefined(boolean isHeightUndefined) {
        this.isHeightUndefined = isHeightUndefined;

        if (isHeightUndefined) {
            addStyleDependentName("sized");

        } else {
            removeStyleDependentName("sized");
        }
    }

    private void updateCellWidth() {
        int cellw = width / getWidgetCount();
        if (cellw > 0) {
            for (int i = 0; i < getWidgetCount(); i++) {
                Widget widget = getWidget(i);
                setCellWidth(widget, cellw + "px");
            }
        }
    }
}