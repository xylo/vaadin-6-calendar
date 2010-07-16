package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

public class DayToolbar extends HorizontalPanel implements ClickHandler {
    private int width = 0;
    protected static final int MARGINLEFT = 50;
    protected static final int MARGINRIGHT = 20;
    protected Button backLabel;
    protected Button nextLabel;
    private boolean verticalSized;
    private boolean horizontalSized;
    private VCalendar calendar;

    public DayToolbar(VCalendar vcalendar) {
        this.calendar = vcalendar;
        setStylePrimaryName("v-calendar-header-week");
        backLabel = new Button();
        backLabel.setStylePrimaryName("v-calendar-back");
        nextLabel = new Button();
        nextLabel.addClickHandler(this);
        nextLabel.setStylePrimaryName("v-calendar-next");
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

    public void add(String dayName, final String date,
            String localized_date_format, String extraClass) {
        DayLabel l = new DayLabel(dayName + " " + localized_date_format);
        l.setDate(date);

        if (extraClass != null) {
            l.addStyleDependentName(extraClass);
        }

        if (verticalSized) {
            l.addStyleDependentName("Vsized");
        }
        if (horizontalSized) {
            l.addStyleDependentName("Hsized");
        }

        l.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (!calendar.isDisabled()
                        && calendar.getClient().hasEventListeners(calendar,
                                CalendarEventId.DATECLICK)) {
                    calendar.getClient().updateVariable(calendar.getPID(),
                            CalendarEventId.DATECLICK, date, true);
                }
            }

        });

        add(l);
    }

    public void addBackButton() {
        if (!calendar.getClient().hasEventListeners(calendar,
                CalendarEventId.FORWARD)) {
            nextLabel.getElement().getStyle().setOpacity(0);
        }
        add(backLabel);
    }

    public void addNextButton() {
        if (!calendar.getClient().hasEventListeners(calendar,
                CalendarEventId.BACKWARD)) {
            backLabel.getElement().getStyle().setOpacity(0);
        }
        add(nextLabel);
    }

    public static class DayLabel extends Label {
        private String date;

        public DayLabel(String string) {
            super(string);
            setStylePrimaryName("v-calendar-header-day");
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }

    }

    public void onClick(ClickEvent event) {
        VCalendar w = (VCalendar) getParent().getParent();

        if (!calendar.isDisabled()) {
            if (event.getSource() == nextLabel) {
                if (w.getClient().hasEventListeners(w, CalendarEventId.FORWARD)) {
                    w.getClient().updateVariable(w.getPID(),
                            VCalendar.ATTR_NAVIGATION, true, true);
                }
            } else if (event.getSource() == backLabel) {
                if (w.getClient()
                        .hasEventListeners(w, CalendarEventId.BACKWARD)) {
                    w.getClient().updateVariable(w.getPID(),
                            VCalendar.ATTR_NAVIGATION, false, true);
                }
            }
        }
    }

    public void setVerticalSized(boolean sized) {
        verticalSized = sized;
    }

    public void setHorizontalSized(boolean sized) {
        horizontalSized = sized;
    }
}
