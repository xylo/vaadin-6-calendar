package com.vaadin.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class WeeklyLongEvents extends HorizontalPanel {
    private static final int MARGINLEFT = 50;
    private static final int BORDERWIDTH = 1;
    private static final int HEIGHT = 30;
    private int width;

    public WeeklyLongEvents() {
        setStylePrimaryName("v-schedule-weekly-longevents");
        getElement().getStyle().setProperty("marginLeft", MARGINLEFT + "px");
        setHeight(HEIGHT - BORDERWIDTH + "px");
    }

    public void addDate(Date d) {
        DateCell dc = new DateCell();
        dc.setDate(d);
        add(dc);
    }

    public void setWidthPX(int width) {
        this.width = width - (MARGINLEFT + BORDERWIDTH + 16);
        if (getWidgetCount() == 0) {
            return;
        }
        updateCellWidths();
    }

    public void addEvent(ScheduleEvent e) {
        int dateCount = getWidgetCount();
        Date from = e.getFromDate();
        Date to = e.getToDate();
        boolean started = false;
        for (int i = 0; i < dateCount; i++) {
            DateCell dc = (DateCell) getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(to);
            Element eventElement = dc.getEventElement();
            if (comp >= 0 && comp2 <= 0) {
                if (comp == 0) {
                    setStyleName(eventElement, "cell-start", true);
                } else if (comp2 == 0) {
                    setStyleName(eventElement, "cell-end", true);
                } else if (!started && comp > 0 && comp2 <= 0) {
                    setStyleName(eventElement, "cell-continue-left", true);
                } else if (comp > 0 && comp2 <= 0) {
                    setStyleName(eventElement, "cell", true);
                } else if (i == (dateCount - 1)) {
                    setStyleName(eventElement, "cell-continue-right", true);
                }
                String extraStyle = e.getStyleName();
                if (extraStyle != null && extraStyle.length() > 0) {
                    setStyleName(eventElement, extraStyle, true);
                }
                if (!started) {
                    eventElement.setInnerText(e.getCaption());
                    started = true;
                }
            } else if (started) {
                break;
            }
        }
    }

    public void updateCellWidths() {
        if (this.width > 0) {
            int cells = getWidgetCount();
            int cellWidth = width / cells;
            cellWidth -= BORDERWIDTH;
            for (int i = 0; i < cells; i++) {
                DateCell dc = (DateCell) getWidget(i);
                dc.setWidth(cellWidth + "px");
                dc.setHeight((HEIGHT - BORDERWIDTH) + "px");
            }
        }

    }

    public static class DateCell extends HTML {
        private Date date;
        private Element event;

        public DateCell() {
            Element e = getElement();
            event = DOM.createDiv();
            setStyleName(event, "cell");
            e.appendChild(event);
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Element getEventElement() {
            return event;
        }

        public Date getDate() {
            return date;
        }

    }
}
