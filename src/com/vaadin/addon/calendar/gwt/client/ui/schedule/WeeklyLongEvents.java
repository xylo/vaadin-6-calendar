package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class WeeklyLongEvents extends HorizontalPanel {

    public static final int EVENT_HEIGTH = 15;

    public static final int EVENT_MARGIN = 1;

    private int width;

    private int rowCount = 0;

    public WeeklyLongEvents() {
        setStylePrimaryName("v-calendar-weekly-longevents");
    }

    public void addDate(Date d) {
        DateCellContainer dcc = new DateCellContainer();
        dcc.setDate(d);
        add(dcc);
    }

    public void setWidthPX(int width) {
        this.width = width;
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
        Date from = e.getStart();
        Date to = e.getEnd();
        boolean started = false;
        for (int i = 0; i < dateCount; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(to);
            DateCell event = dc.getDateCell(e.getSlotIndex());
            event.setStylePrimaryName("v-calendar-event");
            if (comp >= 0 && comp2 <= 0) {
                event.addStyleDependentName("all-day");
                if (comp == 0) {
                    event.addStyleDependentName("start");
                }
                if (comp2 == 0) {
                    event.addStyleDependentName("end");
                }
                if (!started && comp > 0 && comp2 <= 0) {
                    event.addStyleDependentName("continued-from");
                } else if (i == (dateCount - 1)) {
                    event.addStyleDependentName("continued-to");
                }
                final String extraStyle = e.getStyleName();
                if (extraStyle != null && extraStyle.length() > 0) {
                    event.addStyleDependentName(extraStyle + "-all-day");
                }
                if (!started) {
                    event.setText(e.getCaption());
                    started = true;
                }
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
        if (width > 0) {
            int cells = getWidgetCount();
            int cellWidth = width / cells;
            for (int i = 0; i < cells; i++) {
                DateCellContainer dc = (DateCellContainer) getWidget(i);
                dc.setWidth(cellWidth
                        - DateCellContainer.measureBorderWidth(dc) + "px");
            }
        }

    }

    public static class DateCellContainer extends FlowPanel {
        private Date date;

        private static int borderWidth = -1;

        public static int measureBorderWidth(DateCellContainer dc) {
            if (borderWidth == -1) {
                borderWidth = dc.getOffsetWidth()
                        - dc.getElement().getClientWidth();
            }
            return borderWidth;
        }

        public DateCellContainer() {
            setStylePrimaryName("v-calendar-datecell");
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public DateCell getDateCell(int slotIndex) {
            return (DateCell) getChildren().get(slotIndex);
        }

        public void addEmptyEventCells(int eventCount) {
            for (int i = 0; i < eventCount; i++) {
                addEmptyEventCell();
            }
        }

        public void addEmptyEventCell() {
            add(new DateCell());
        }
    }

    public static class DateCell extends HTML {
        private Date date;

        public DateCell() {
            // setStylePrimaryName("v-calendar-event");
            // addStyleDependentName("all-day");
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

    }
}
