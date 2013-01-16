/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Iterator;

import com.google.gwt.dom.client.Style.Unit;
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
        calendar = vcalendar;

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
            setCellHorizontalAlignment(nextLabel, ALIGN_RIGHT);
            int cellw = width / (count - 2);
            int remain = width % (count - 2);
            int cellw2 = cellw + 1;
            if (cellw > 0) {
                int[] cellWidths = VCalendar
                        .distributeSize(width, count - 2, 0);
                for (int i = 1; i < count - 1; i++) {
                    Widget widget = getWidget(i);
                    // if (remain > 0) {
                    // setCellWidth(widget, cellw2 + "px");
                    // remain--;
                    // } else {
                    // setCellWidth(widget, cellw + "px");
                    // }
                    setCellWidth(widget, cellWidths[i - 1] + "px");
                    widget.setWidth(cellWidths[i - 1] + "px");
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
                if (calendar.getDateClickListener() != null) {
                    calendar.getDateClickListener().dateClick(date);
                }
            }
        });

        add(l);
    }

    public void addBackButton() {
        if (!calendar.isBackwardNavigationEnabled()) {
            nextLabel.getElement().getStyle().setHeight(0, Unit.PX);
        }
        add(backLabel);
    }

    public void addNextButton() {
        if (!calendar.isForwardNavigationEnabled()) {
            backLabel.getElement().getStyle().setHeight(0, Unit.PX);
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
        if (!calendar.isDisabledOrReadOnly()) {
            if (event.getSource() == nextLabel) {
                if (calendar.getForwardListener() != null) {
                    calendar.getForwardListener().forward();
                }
            } else if (event.getSource() == backLabel) {
                if (calendar.getBackwardListener() != null) {
                    calendar.getBackwardListener().backward();
                }
            }
        }
    }

    public void setVerticalSized(boolean sized) {
        verticalSized = sized;
        updateDayLabelSizedStyleNames();
    }

    public void setHorizontalSized(boolean sized) {
        horizontalSized = sized;
        updateDayLabelSizedStyleNames();
    }

    private void updateDayLabelSizedStyleNames() {
        Iterator<Widget> it = iterator();
        while (it.hasNext()) {
            updateWidgetSizedStyleName(it.next());
        }
    }

    private void updateWidgetSizedStyleName(Widget w) {
        if (verticalSized) {
            w.addStyleDependentName("Vsized");
        } else {
            w.removeStyleDependentName("VSized");
        }
        if (horizontalSized) {
            w.addStyleDependentName("Hsized");
        } else {
            w.removeStyleDependentName("HSized");
        }
    }
}
