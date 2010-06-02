package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

public class WeeklyLongEvents extends HorizontalPanel {

    public static final int EVENT_HEIGTH = 15;

    public static final int EVENT_MARGIN = 1;

    private int width;

    private int rowCount = 0;

    private VCalendar calendar;

    public WeeklyLongEvents(VCalendar calendar) {
        setStylePrimaryName("v-calendar-weekly-longevents");
        this.calendar = calendar;
    }

    public void addDate(Date d) {
        DateCellContainer dcc = new DateCellContainer();
        dcc.setDate(d);
        dcc.setCalendar(calendar);
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
        // addEmptyEventSlots(events.size());
        for (CalendarEvent e : events) {
            addEvent(e);
        }
    }

    public void addEvent(CalendarEvent calendarEvent) {
        updateEventSlot(calendarEvent);

        int dateCount = getWidgetCount();
        Date from = calendarEvent.getStart();
        Date to = calendarEvent.getEnd();
        boolean started = false;
        for (int i = 0; i < dateCount; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(to);
            DateCell eventLabel = dc.getDateCell(calendarEvent.getSlotIndex());
            eventLabel.setStylePrimaryName("v-calendar-event");
            if (comp >= 0 && comp2 <= 0) {
                eventLabel.setEvent(calendarEvent);

                eventLabel.addStyleDependentName("all-day");
                if (comp == 0) {
                    eventLabel.addStyleDependentName("start");
                }
                if (comp2 == 0) {
                    eventLabel.addStyleDependentName("end");
                }
                if (!started && comp > 0 && comp2 <= 0) {
                    eventLabel.addStyleDependentName("continued-from");
                } else if (i == (dateCount - 1)) {
                    eventLabel.addStyleDependentName("continued-to");
                }
                final String extraStyle = calendarEvent.getStyleName();
                if (extraStyle != null && extraStyle.length() > 0) {
                    eventLabel.addStyleDependentName(extraStyle + "-all-day");
                }
                if (!started) {
                    eventLabel.setText(calendarEvent.getCaption());
                    started = true;
                }
            }
        }
    }

    private void updateEventSlot(CalendarEvent e) {
        // TODO Now every event will be drawn to a new "line". Check if any
        // line has free space where this event could fit, and put it there.
        // Just updating the slotIndex should do the trick..
        // if (e.getSlotIndex() == -1) {
        // e.setSlotIndex(rowCount);
        // rowCount++;
        // }

        boolean foundFreeSlot = false;
        int slot = 0;
        while (!foundFreeSlot) {
            if (isSlotFree(slot, e.getStart(), e.getEnd())) {
                e.setSlotIndex(slot);
                foundFreeSlot = true;

            } else {
                slot++;
            }
        }
    }

    private boolean isSlotFree(int slot, Date start, Date end) {
        int dateCount = getWidgetCount();

        // Go over all dates this week
        for (int i = 0; i < dateCount; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(start);
            int comp2 = dcDate.compareTo(end);

            // check if the date is in the range we need
            if (comp >= 0 && comp2 <= 0) {

                // and that it has a free row
                if (dc.hasEvent(slot)) {
                    return false;
                }
            }
        }

        return true;
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

    public static class DateCellContainer extends FlowPanel implements
            MouseDownHandler, MouseUpHandler {

        private Date date;

        private Widget clickTargetWidget;

        private VCalendar calendar;

        private static int borderWidth = -1;

        public static int measureBorderWidth(DateCellContainer dc) {
            if (borderWidth == -1) {
                borderWidth = dc.getOffsetWidth()
                        - dc.getElement().getClientWidth();
            }
            return borderWidth;
        }

        public void setCalendar(VCalendar calendar) {
            this.calendar = calendar;
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

        public boolean hasEvent(int slotIndex) {

            return hasDateCell(slotIndex)
                    && ((DateCell) getChildren().get(slotIndex)).getEvent() != null;
        }

        public boolean hasDateCell(int slotIndex) {
            return (getChildren().size() - 1) >= slotIndex;
        }

        public DateCell getDateCell(int slotIndex) {
            if (!hasDateCell(slotIndex)) {
                addEmptyEventCells(slotIndex - (getChildren().size() - 1));
            }
            return (DateCell) getChildren().get(slotIndex);
        }

        public void addEmptyEventCells(int eventCount) {
            for (int i = 0; i < eventCount; i++) {
                addEmptyEventCell();
            }
        }

        public void addEmptyEventCell() {
            DateCell dateCell = new DateCell();
            dateCell.addMouseDownHandler(this);
            dateCell.addMouseUpHandler(this);
            add(dateCell);
        }

        @Override
        public void onMouseDown(MouseDownEvent event) {
            clickTargetWidget = (Widget) event.getSource();

            event.stopPropagation();
        }

        @Override
        public void onMouseUp(MouseUpEvent event) {
            if (event.getSource() == clickTargetWidget
                    && clickTargetWidget instanceof DateCell) {
                CalendarEvent calendarEvent = ((DateCell) clickTargetWidget)
                        .getEvent();
                calendar.getClient().updateVariable(calendar.getPID(),
                        CalendarEventId.EVENTCLICK, calendarEvent.getIndex(),
                        true);
            }
        }
    }

    public static class DateCell extends HTML {
        private Date date;
        private CalendarEvent event;

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

        public void setEvent(CalendarEvent event) {
            this.event = event;
        }

        public CalendarEvent getEvent() {
            return event;
        }

    }
}
