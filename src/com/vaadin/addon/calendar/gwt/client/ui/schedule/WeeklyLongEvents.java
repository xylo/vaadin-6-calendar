package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WeeklyLongEvents extends HorizontalPanel {

    private static final int MARGINLEFT = 50;

    public static final int EVENT_HEIGTH = 15;

    public static final int EVENT_MARGIN = 1;

    private int width;

    private int rowCount = 0;

    public WeeklyLongEvents() {
        setStylePrimaryName("v-calendar-weekly-longevents");
        getElement().getStyle().setProperty("marginLeft", MARGINLEFT + "px");
    }

    public void addDate(Date d) {
        DateCellContainer dcc = new DateCellContainer();
        dcc.setDate(d);
        add(dcc);
    }

    public void setWidthPX(int width) {
        this.width = width - (MARGINLEFT + 16);
        if (getWidgetCount() == 0) {
            return;
        }
        updateCellWidths();
    }

    public void addEvents(List<CalendarEvent> events) {
        addEmptyEventSlots(events.size());
        for (CalendarEvent e : events) {
            addEvent(e);
        }
    }

    public void addEvent(CalendarEvent e) {
        updateEventSlot(e);

        int dateCount = getWidgetCount();
        Date from = e.getFromDate();
        Date to = e.getToDate();
        boolean started = false;
        for (int i = 0; i < dateCount; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(to);
            Element eventElement = dc.getElement(e.getSlotIndex());
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

                // heigth =
                // Integer.parseInt(dc.getElement().getStyle().getHeight().substring(0,
                // ));
            } else if (started) {
                break;
            }
        }
    }

    private void updateEventSlot(CalendarEvent e) {
        // TODO Now every event will be drawn to a new "line". Check if any
        // line has free space where this event could fit, and put it there.
        // Just updating the slotIndex should do the trick..
        if (e.getSlotIndex() == -1) {
            e.setSlotIndex(rowCount);
            rowCount++;
        }
    }

    private void addEmptyEventSlots(int eventCount) {
        int dateCount = getWidgetCount();
        for (int i = 0; i < dateCount; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);
            dc.addEmptyEventCells(eventCount);
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public void updateCellWidths() {
        if (this.width > 0) {
            int cells = getWidgetCount();
            int cellWidth = width / cells;
            for (int i = 0; i < cells; i++) {
                DateCellContainer dc = (DateCellContainer) getWidget(i);
                dc.setWidth(cellWidth + "px");
            }
        }

    }

    public int calculateHeigth() {
        return getRowCount() * (EVENT_HEIGTH + EVENT_MARGIN);
    }

    public static class DateCellContainer extends VerticalPanel {
        private Date date;

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public Element getElement(int slotIndex) {
            return ((DateCell) getChildren().get(slotIndex)).getEventElement();
        }

        public void addEmptyEventCells(int eventCount) {
            for (int i = 0; i < eventCount; i++) {
                add(new DateCell());
            }
        }

        public void addEmptyEventCell() {
            add(new DateCell());
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
