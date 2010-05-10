package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VSchedule;

public class DayToolbar extends HorizontalPanel implements ClickHandler {
    private int width = 0;
    protected static final int MARGINLEFT = 50;
    protected static final int MARGINRIGHT = 20;
    protected Label backLabel;
    protected Label nextLabel;

    public DayToolbar() {
        setStylePrimaryName("v-schedule-header-daily");
        setHeight(VSchedule.MONTHLY_DAYTOOLBARHEIGHT + "px");
        backLabel = new Label("");
        backLabel.addStyleName("back");
        nextLabel = new Label("");
        nextLabel.addClickHandler(this);
        nextLabel.addStyleName("next");
        backLabel.addClickHandler(this);
        setBorderWidth(0);
        setSpacing(0);
    }

    public void setWidthPX(int width) {
        this.width = (width - MARGINLEFT) - MARGINRIGHT;
        // super.setWidth(this.width + "px");
        if (getWidgetCount() == 0) {
            return;
        }
        updateCellWidths();
    }

    public void updateCellWidths() {
        int count = getWidgetCount();
        if (count > 0) {
            setCellWidth(backLabel, MARGINLEFT + "px");
            setCellWidth(nextLabel, MARGINRIGHT + "px");
            int cellw = width / (count - 2);
            int remain = width % (count - 2);
            int cellw2 = cellw + 1;
            if (cellw > 0) {
                for (int i = 1; i < count - 1; i++) {
                    Widget widget = getWidget(i);
                    if (remain > 0) {
                        setCellWidth(widget, cellw2 + "px");
                        remain--;
                    } else {
                        setCellWidth(widget, cellw + "px");
                    }
                }
            }
        }
    }

    public void add(String dayName, String date, String localized_date_format,
            String extraClass) {
        DayLabel l = new DayLabel(dayName + " " + localized_date_format);
        l.setDate(date);
        l.setHorizontalAlignment(ALIGN_CENTER);
        if (extraClass != null) {
            l.addStyleName(extraClass);
        }
        add(l);
    }

    public void addBackButton() {
        add(backLabel);
    }

    public void addNextButton() {
        add(nextLabel);
    }

    public static class DayLabel extends Label implements ClickHandler {
        private String date;

        public DayLabel(String string) {
            super(string);
            addClickHandler(this);
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public void onClick(ClickEvent event) {
            VSchedule w = (VSchedule) getParent().getParent().getParent();
            if (w.getClient().hasEventListeners(w, ScheduleEventId.DATECLICK)) {
                w.getClient().updateVariable(w.getPID(),
                        ScheduleEventId.DATECLICK, date, true);
            }
        }

    }

    public void onClick(ClickEvent event) {
        VSchedule w = (VSchedule) getParent().getParent();
        if (event.getSource() == nextLabel) {
            if (w.getClient().hasEventListeners(w, ScheduleEventId.FORWARD)) {
                w.getClient().updateVariable(w.getPID(), "navigation", 1, true);
            }
        } else if (event.getSource() == backLabel) {
            if (w.getClient().hasEventListeners(w, ScheduleEventId.BACKWARD)) {
                w.getClient()
                        .updateVariable(w.getPID(), "navigation", -1, true);
            }
        }
    }

}
