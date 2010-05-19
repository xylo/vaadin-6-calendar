/**
 * 
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SimpleDayToolbar extends HorizontalPanel {
    private int width = 0;

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
        this.width = width;
        super.setWidth(this.width + "px");
        if (getWidgetCount() == 0) {
            return;
        }
        updateCellWidth();
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