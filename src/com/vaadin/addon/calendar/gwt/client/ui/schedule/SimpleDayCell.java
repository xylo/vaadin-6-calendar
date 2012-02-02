/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.FocusableFlowPanel;

/**
 * A class representing a single cell within the calendar in month-view
 */
public class SimpleDayCell extends FocusableFlowPanel implements
MouseUpHandler, MouseDownHandler, MouseOverHandler, MouseMoveHandler {

    private static int BOTTOMSPACERHEIGHT = -1;
    private static int EVENTHEIGHT = -1;
    private static final int BORDERPADDINGSIZE = 1;

    private final VCalendar calendar;
    private Date date;
    private int intHeight;
    private final HTML bottomspacer;
    private final Label caption;
    private final CalendarEvent[] events = new CalendarEvent[10];
    private final int cell;
    private final int row;
    private boolean monthNameVisible;
    private HandlerRegistration mouseUpRegistration;
    private HandlerRegistration mouseDownRegistration;
    private HandlerRegistration mouseOverRegistration; 
    private boolean monthEventMouseDown;
    private boolean labelMouseDown;
    private int eventCount = 0;

    private int startX = -1;
    private int startY = -1;
    private int startYrelative;
    private int startXrelative;
    private Date startDateFrom;
    private Date startDateTo;
    private int prevDayDiff = 0;
    private int prevWeekDiff = 0;
    private HandlerRegistration moveRegistration;
    private CalendarEvent moveEvent;
    private Widget clickedWidget;
    private HandlerRegistration bottomSpacerMouseDownHandler;
    private boolean scrollable = false;
    private boolean eventCanceled;
    private MonthGrid monthGrid;
    private HandlerRegistration keyDownHandler;

    public SimpleDayCell(VCalendar calendar, int row, int cell) {
        this.calendar = calendar;
        this.row = row;
        this.cell = cell;
        setStylePrimaryName("v-calendar-month-day");
        caption = new Label();
        bottomspacer = new HTML();
        bottomspacer.setStyleName("v-calendar-bottom-spacer-empty");
        bottomspacer.setWidth(3 + "em");
        caption.setStyleName("v-calendar-day-number");
        add(caption);
        add(bottomspacer);
        caption.addMouseDownHandler(this);
        caption.addMouseUpHandler(this);
    }

    @Override
    public void onLoad() {
        BOTTOMSPACERHEIGHT = bottomspacer.getOffsetHeight();
        EVENTHEIGHT = BOTTOMSPACERHEIGHT;
    }

    public void setMonthGrid(MonthGrid monthGrid) {
        this.monthGrid = monthGrid;
    }

    public MonthGrid getMonthGrid() {
        return monthGrid;
    }

    @SuppressWarnings("deprecation")
    public void setDate(Date date) {
        int dateOfMonth = date.getDate();
        if (monthNameVisible) {
            caption.setText(dateOfMonth + " "
                    + calendar.getMonthNames()[date.getMonth()]);
        } else {
            caption.setText("" + dateOfMonth);
        }
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void reDraw(boolean clear) {
        setHeightPX(intHeight + BORDERPADDINGSIZE, clear);
    }

    /*
     * Events and whole cell content are drawn by this method. By the
     * clear-argument, you can choose to clear all old content. Notice that
     * clearing will also remove all element's event handlers.
     */
    public void setHeightPX(int px, boolean clear) {
        // measure from DOM if needed
        if (px < 0) {
            intHeight = getOffsetHeight() - BORDERPADDINGSIZE;
        } else {
            intHeight = px - BORDERPADDINGSIZE;
        }

        if (clear) {
            while (getWidgetCount() > 1) {
                remove(1);
            }
        }

        // How many events can be shown in UI
        int slots = 0;
        if (scrollable) {
            for (int i = 0; i < events.length; i++) {
                if (events[i] != null) {
                    slots = i + 1;
                }
            }
            setHeight(intHeight + "px"); // Fixed height
        } else {
            // Dynamic height by the content
            DOM.removeElementAttribute(getElement(), "height");
            slots = (intHeight - caption.getOffsetHeight() - BOTTOMSPACERHEIGHT)
                    / EVENTHEIGHT;
            if (slots > 10) {
                slots = 10;
            }
        }

        updateEvents(slots, clear);

    }

    public void updateEvents(int slots, boolean clear) {
        int eventsAdded = 0;

        // some IE magic
        // events tend to stretch the day cell too wide, so we measure the day
        // cell before the events, and then force the events to be the same
        // width after adding them
        boolean isIE = BrowserInfo.get().isIE6() || BrowserInfo.get().isIE7();
        int widthBeforeEvents = getWidth();

        for (int i = 0; i < slots; i++) {
            CalendarEvent e = events[i];
            if (e == null) {
                // Empty slot
                HTML slot = new HTML();
                slot.setStyleName("v-calendar-spacer");
                if (!clear) {
                    remove(i + 1);
                    insert(slot, i + 1);
                } else {
                    add(slot);
                }
            } else {
                // Event slot
                eventsAdded++;
                if (!clear) {
                    Widget w = getWidget(i + 1);
                    if (!(w instanceof MonthEventLabel)) {
                        remove(i + 1);
                        insert(createMonthEventLabel(e), i + 1);
                    }
                } else {
                    add(createMonthEventLabel(e));
                }
            }
        }

        if (isIE) {
            for (Widget widget : getChildren()) {
                if (widget instanceof MonthEventLabel) {
                    MonthEventLabel eventDiv = (MonthEventLabel) widget;
                    int paddingBorder = Util.measureHorizontalPaddingAndBorder(
                            eventDiv.getElement(), 4);
                    int newWidth = widthBeforeEvents - paddingBorder;
                    eventDiv.setWidth(newWidth + "px");
                }
            }
        }
        int remainingSpace = intHeight
                - ((slots * EVENTHEIGHT) + BOTTOMSPACERHEIGHT + caption
                        .getOffsetHeight());
        int newHeight = remainingSpace + BOTTOMSPACERHEIGHT;
        if (newHeight < 0) {
            newHeight = EVENTHEIGHT;
        }
        bottomspacer.setHeight(newHeight + "px");
        if (clear) {
            add(bottomspacer);
        }

        int more = eventCount - eventsAdded;
        if (more > 0) {
            if (bottomSpacerMouseDownHandler == null) {
                bottomSpacerMouseDownHandler = bottomspacer
                        .addMouseDownHandler(this);
            }
            bottomspacer.setStyleName("v-calendar-bottom-spacer");
            bottomspacer.setText("+ " + more);
        } else {
            if (!scrollable && bottomSpacerMouseDownHandler != null) {
                bottomSpacerMouseDownHandler.removeHandler();
                bottomSpacerMouseDownHandler = null;
            }

            if (scrollable) {
                bottomspacer.setText("[ - ]");
            } else {
                bottomspacer.setStyleName("v-calendar-bottom-spacer-empty");
                bottomspacer.setText("");
            }
        }
    }

    private MonthEventLabel createMonthEventLabel(CalendarEvent e) {
        long rangeInMillis = e.getRangeInMilliseconds();
        boolean timeEvent = rangeInMillis <= VCalendar.DAYINMILLIS
                && !e.isAllDay();
        Date fromDatetime = e.getStartTime();

        // Create a new MonthEventLabel
        MonthEventLabel eventDiv = new MonthEventLabel();
        eventDiv.addStyleDependentName("month");
        eventDiv.addMouseDownHandler(this);
        eventDiv.addMouseUpHandler(this);
        eventDiv.setCalendar(calendar);
        eventDiv.setEventIndex(e.getIndex());

        if (timeEvent) {
            eventDiv.setTimeSpecificEvent(true);
            if (e.getStyleName() != null) {
                eventDiv.addStyleDependentName(e.getStyleName());
            }
            eventDiv.setHTML(calendar.getTimeFormat().format(fromDatetime)
                    + " " + e.getCaption());
        } else {
            eventDiv.setTimeSpecificEvent(false);
            Date from = e.getStart();
            Date to = e.getEnd();
            if (e.getStyleName().length() > 0) {
                eventDiv.addStyleName("month-event " + e.getStyleName());
            } else {
                eventDiv.addStyleName("month-event");
            }
            int fromCompareToDate = from.compareTo(date);
            int toCompareToDate = to.compareTo(date);
            eventDiv.addStyleDependentName("all-day");
            if (fromCompareToDate == 0) {
                eventDiv.addStyleDependentName("start");
                eventDiv.setText(e.getCaption());
            } else if (fromCompareToDate < 0 && cell == 0) {
                eventDiv.addStyleDependentName("continued-from");
                eventDiv.setText(e.getCaption());
            }
            if (toCompareToDate == 0) {
                eventDiv.addStyleDependentName("end");
            } else if (toCompareToDate > 0
                    && (cell + 1) == getMonthGrid().getCellCount(row)) {
                eventDiv.addStyleDependentName("continued-to");
            }
            if (e.getStyleName() != null) {
                eventDiv.addStyleDependentName(e.getStyleName() + "-all-day");
            }
        }

        return eventDiv;
    }

    private void setUnlimitedCellHeight() {
        scrollable = true;
        addStyleDependentName("scrollable");
    }

    private void setLimitedCellHeight() {
        scrollable = false;
        removeStyleDependentName("scrollable");
    }

    public void addCalendarEvent(CalendarEvent e) {
        eventCount++;
        int slot = e.getSlotIndex();
        if (slot == -1) {
            for (int i = 0; i < events.length; i++) {
                if (events[i] == null) {
                    events[i] = e;
                    e.setSlotIndex(i);
                    break;
                }
            }
        } else {
            events[slot] = e;
        }
    }

    @SuppressWarnings("deprecation")
    public void setMonthNameVisible(boolean b) {
        monthNameVisible = b;
        int dateOfMonth = date.getDate();
        caption.setText(dateOfMonth + " "
                + calendar.getMonthNames()[date.getMonth()]);
    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        mouseUpRegistration = addDomHandler(this, MouseUpEvent.getType());
        mouseDownRegistration = addDomHandler(this, MouseDownEvent.getType());
        mouseOverRegistration = addDomHandler(this, MouseOverEvent.getType());
    }

    @Override
    protected void onDetach() {
        mouseUpRegistration.removeHandler();
        mouseDownRegistration.removeHandler();
        mouseOverRegistration.removeHandler();     
        super.onDetach();
    }

    public void onMouseUp(MouseUpEvent event) {
        if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
            return;
        }

        Widget w = (Widget) event.getSource();
        if (moveRegistration != null) {
            Event.releaseCapture(getElement());
            moveRegistration.removeHandler();
            moveRegistration = null;
            keyDownHandler.removeHandler();
            keyDownHandler = null;
        }

        if (w == bottomspacer && monthEventMouseDown) {
            GWT.log("Mouse up over bottomspacer");

        } else if (clickedWidget instanceof MonthEventLabel
                && monthEventMouseDown) {
            MonthEventLabel mel = (MonthEventLabel) clickedWidget;

            int endX = event.getClientX();
            int endY = event.getClientY();
            int xDiff = startX - endX;
            int yDiff = startY - endY;
            startX = -1;
            startY = -1;
            prevDayDiff = 0;
            prevWeekDiff = 0;

            if (!mel.isTimeSpecificEvent()
                    && (xDiff < -3 || xDiff > 3 || yDiff < -3 || yDiff > 3)) {
                eventMoved(moveEvent);

            } else if (calendar.getEventClickListener() != null) {
                CalendarEvent e = getEventByWidget(mel);
                calendar.getEventClickListener().eventClick(e);
            }

            moveEvent = null;
        } else if (w == this) {
            getMonthGrid().setSelectionReady();

        } else if (w instanceof Label && labelMouseDown) {
            String clickedDate = calendar.getDateFormat().format(date);
            if (calendar.getDateClickListener() != null) {
                calendar.getDateClickListener().dateClick(clickedDate);
            }
        }
        monthEventMouseDown = false;
        labelMouseDown = false;
        clickedWidget = null;
    }

    public void onMouseDown(MouseDownEvent event) {
        if (calendar.isDisabled()
                || event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
            return;
        }

        Widget w = (Widget) event.getSource();
        clickedWidget = w;

        if (w instanceof MonthEventLabel) {
            // event clicks should be allowed even when read-only
            monthEventMouseDown = true;

            if (w instanceof MonthEventLabel) {
                startCalendarEventDrag(event, (MonthEventLabel) w);
            }
        } else if (!calendar.isReadOnly()) {
            // these are not allowed when in read-only
            if (w == bottomspacer) {
                if (scrollable) {
                    setLimitedCellHeight();
                } else {
                    setUnlimitedCellHeight();
                }
                reDraw(true);

            } else if (w == this && !scrollable) {
                MonthGrid grid = getMonthGrid();
                if (!grid.isDisabled() && calendar.isRangeSelectAllowed()) {
                    grid.setSelectionStart(this);
                    grid.setSelectionEnd(this);
                }
            } else if (w instanceof Label) {
                labelMouseDown = true;
            }
        }

        event.stopPropagation();
    }

    public void onMouseOver(MouseOverEvent event) {
        event.preventDefault();
        getMonthGrid().setSelectionEnd(this);
    }

    public void onMouseMove(MouseMoveEvent event) {
        if (clickedWidget instanceof MonthEventLabel && !monthEventMouseDown
                || (startY < 0 && startX < 0)) {
            return;
        }

        MonthEventLabel w = (MonthEventLabel) clickedWidget;

        if (calendar.isDisabledOrReadOnly()) {
            Event.releaseCapture(getElement());
            monthEventMouseDown = false;
            startY = -1;
            startX = -1;
            return;
        }

        int currentY = event.getClientY();
        int currentX = event.getClientX();
        int moveY = (currentY - startY);
        int moveX = (currentX - startX);
        if ((moveY < 5 && moveY > -6) && (moveX < 5 && moveX > -6)) {
            return;
        }

        int dateCellWidth = getWidth();
        int dateCellHeigth = getHeigth();

        Element parent = getMonthGrid().getElement();
        int relativeX = event.getRelativeX(parent);
        int relativeY = event.getRelativeY(parent);
        int weekDiff = 0;
        if (moveY > 0) {
            weekDiff = (startYrelative + moveY) / dateCellHeigth;
        } else {
            weekDiff = (moveY - (dateCellHeigth - startYrelative))
                    / dateCellHeigth;
        }

        int dayDiff = 0;
        if (moveX >= 0) {
            dayDiff = (startXrelative + moveX) / dateCellWidth;
        } else {
            dayDiff = (moveX - (dateCellWidth - startXrelative))
                    / dateCellWidth;
        }
        // Check boundaries
        if (relativeY < 0
                || relativeY >= (calendar.getMonthGrid().getRowCount() * dateCellHeigth)
                || relativeX < 0
                || relativeX >= (calendar.getMonthGrid().getColumnCount() * dateCellWidth)) {
            return;
        }

        GWT.log("Event moving delta: " + weekDiff + " weeks " + dayDiff
                + " days" + " (" + getCell() + "," + getRow() + ")");

        CalendarEvent e = moveEvent;
        if (e == null) {
            e = getEventByWidget(w);
        }

        Date from = e.getStart();
        Date to = e.getEnd();
        long duration = to.getTime() - from.getTime();

        long daysMs = dayDiff * VCalendar.DAYINMILLIS;
        long weeksMs = weekDiff * VCalendar.WEEKINMILLIS;
        from.setTime(startDateFrom.getTime() + weeksMs + daysMs);
        to.setTime((from.getTime() + duration));
        e.setStart(from);
        e.setEnd(to);
        e.setStartTime(new Date(from.getTime()));
        e.setEndTime(new Date(to.getTime()));

        updateDragPosition(w, dayDiff, weekDiff);
    }

    private void eventMoved(CalendarEvent e) {
        calendar.updateEventToMonthGrid(e);
        if (calendar.getEventMovedListener() != null) {
            calendar.getEventMovedListener().eventMoved(e);
        }
    }

    public void startCalendarEventDrag(MouseDownEvent event,
            final MonthEventLabel w) {
        if (w.isTimeSpecificEvent()) {
            return;
        }

        moveRegistration = addMouseMoveHandler(this);
        startX = event.getClientX();
        startY = event.getClientY();
        startYrelative = event.getRelativeY(w.getParent().getElement())
                % getHeigth();
        startXrelative = event.getRelativeX(w.getParent().getElement())
                % getWidth();

        CalendarEvent e = getEventByWidget(w);
        startDateFrom = (Date) e.getStart().clone();
        startDateTo = (Date) e.getEnd().clone();

        Event.setCapture(getElement());
        keyDownHandler = addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                    cancelEventDrag(w);
                }
            }

        });

        focus();

        GWT.log("Start drag");
    }

    protected void cancelEventDrag(MonthEventLabel w) {
        if (moveRegistration != null) {
            // reset position
            if (moveEvent == null) {
                moveEvent = getEventByWidget(w);
            }

            moveEvent.setStart(startDateFrom);
            moveEvent.setEnd(startDateTo);
            calendar.updateEventToMonthGrid(moveEvent);

            // reset drag-related properties
            Event.releaseCapture(getElement());
            moveRegistration.removeHandler();
            moveRegistration = null;
            keyDownHandler.removeHandler();
            keyDownHandler = null;
            setFocus(false);
            monthEventMouseDown = false;
            startY = -1;
            startX = -1;
            moveEvent = null;
            labelMouseDown = false;
            clickedWidget = null;
        }
    }

    public void updateDragPosition(MonthEventLabel w, int dayDiff, int weekDiff) {
        // Draw event to its new position only when position has changed
        if (dayDiff == prevDayDiff && weekDiff == prevWeekDiff) {
            return;
        }

        prevDayDiff = dayDiff;
        prevWeekDiff = weekDiff;

        if (moveEvent == null) {
            moveEvent = getEventByWidget(w);
        }

        calendar.updateEventToMonthGrid(moveEvent);
    }

    public int getRow() {
        return row;
    }

    public int getCell() {
        return cell;
    }

    public int getHeigth() {
        return intHeight + BORDERPADDINGSIZE;
    }

    public int getWidth() {
        return getOffsetWidth() - BORDERPADDINGSIZE;
    }

    public void setToday(boolean today) {
        if (today) {
            addStyleDependentName("today");
        } else {
            removeStyleDependentName("today");
        }
    }

    public boolean removeEvent(CalendarEvent targetEvent,
            boolean reDrawImmediately) {
        int slot = targetEvent.getSlotIndex();
        if (slot < 0) {
            return false;
        }

        CalendarEvent e = getCalendarEvent(slot);
        if (targetEvent.equals(e)) {
            events[slot] = null;
            eventCount--;
            if (reDrawImmediately) {
                reDraw(moveEvent == null);
            }
            return true;
        }
        return false;
    }

    private CalendarEvent getEventByWidget(MonthEventLabel eventWidget) {
        int index = getWidgetIndex(eventWidget);
        return getCalendarEvent(index - 1);
    }

    public CalendarEvent getCalendarEvent(int i) {
        return events[i];
    }

    public CalendarEvent[] getEvents() {
        return events;
    }

    public int getEventCount() {
        return eventCount;
    }

    public CalendarEvent getMoveEvent() {
        return moveEvent;
    }

    public void addEmphasisStyle() {
        addStyleDependentName("dragemphasis");
    }

    public void removeEmphasisStyle() {
        removeStyleDependentName("dragemphasis");
    }

    public static class MonthEventLabel extends HTML {

        private boolean timeSpecificEvent = false;
        private Integer eventIndex;
        private VCalendar calendar;

        public MonthEventLabel() {
            setStylePrimaryName("v-calendar-event");
            sinkEvents(VTooltip.TOOLTIP_EVENTS);
        }

        public void setEventIndex(int index) {
            eventIndex = index;
        }

        public void setCalendar(VCalendar calendar) {
            this.calendar = calendar;
        }

        public boolean isTimeSpecificEvent() {
            return timeSpecificEvent;
        }

        public void setTimeSpecificEvent(boolean timeSpecificEvent) {
            this.timeSpecificEvent = timeSpecificEvent;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
         * .user.client.Event)
         */
        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            calendar.handleTooltipEvent(event, eventIndex);
        }
    }
}