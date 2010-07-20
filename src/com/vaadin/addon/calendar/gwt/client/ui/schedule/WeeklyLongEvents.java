package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class WeeklyLongEvents extends HorizontalPanel {

    public static final int EVENT_HEIGTH = 15;

    public static final int EVENT_MARGIN = 1;

    private int width;

    private int rowCount = 0;

    private VCalendar calendar;

    private boolean undefinedWidth;

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
        undefinedWidth = (width < 0);

        updateCellWidths();
    }

    public void addEvents(List<CalendarEvent> events) {
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
                eventLabel.setCalendar(calendar);

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

                // check if the slot is taken
                if (dc.hasEvent(slot)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void updateCellWidths() {
        int cells = getWidgetCount();
        if (cells <= 0) {
            return;
        }

        int cellWidth = -1;

        // if width is undefined, use the width of the first cell
        // otherwise use distributed sizes
        if (undefinedWidth) {
            cellWidth = calendar.getWeekGrid().getDateCellWidth()
                    - calendar.getWeekGrid().getDateSlotBorder();
        }

        for (int i = 0; i < cells; i++) {
            DateCellContainer dc = (DateCellContainer) getWidget(i);

            if (undefinedWidth) {
                dc.setWidth(cellWidth + "px");

            } else {
                dc.setWidth(calendar.getWeekGrid().getDateCellWidths()[i]
                        + "px");
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
                borderWidth = Util.measureHorizontalBorder(dc.getElement());
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

        public void onMouseDown(MouseDownEvent event) {
            clickTargetWidget = (Widget) event.getSource();

            event.stopPropagation();
        }

        public void onMouseUp(MouseUpEvent event) {
            if (event.getSource() == clickTargetWidget
                    && clickTargetWidget instanceof DateCell
                    && !calendar.isDisabled()) {
                CalendarEvent calendarEvent = ((DateCell) clickTargetWidget)
                        .getEvent();
                if (calendar.getClient().hasEventListeners(calendar,
                        CalendarEventId.EVENTCLICK)) {
                    calendar.getClient().updateVariable(calendar.getPID(),
                            CalendarEventId.EVENTCLICK,
                            calendarEvent.getIndex(), true);
                }
            }
        }
    }

    public static class DateCell extends HTML {
        private Date date;
        private CalendarEvent calendarEvent;
        private VCalendar calendar;

        public DateCell() {
            sinkEvents(VTooltip.TOOLTIP_EVENTS);
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);

            if (calendar != null && calendar.getClient() != null) {
                calendar.getClient().handleTooltipEvent(event, calendar,
                        calendarEvent.getIndex());
            }
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public void setEvent(CalendarEvent event) {
            calendarEvent = event;
        }

        public CalendarEvent getEvent() {
            return calendarEvent;
        }

        public void setCalendar(VCalendar calendar) {
            this.calendar = calendar;
        }

        public VCalendar getCalendar() {
            return calendar;
        }
    }
}
