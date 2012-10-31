/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HumanInputEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.FocusableFlowPanel;

/**
 * A class representing a single cell within the calendar in month-view
 */
public class SimpleDayCell extends FocusableFlowPanel implements
        MouseDownHandler, TouchStartHandler, NativePreviewHandler {

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
    private HandlerRegistration mouseDownRegistration;
    private HandlerRegistration bottomSpacerMouseDownHandler;
    private HandlerRegistration bottomSpacerTouchStartHandler;
    private HandlerRegistration touchStartRegistration;
    private HandlerRegistration nativePreviewRegistration;
    private boolean monthEventMouseDown;
    private boolean labelMouseDown;
    private int eventCount = 0;

    private int startX = -1;
    private int startY = -1;
    private int lastMoveX = -1;
    private int lastMoveY = -1;
    private CalendarEvent moveEvent;
    private Widget clickedWidget;
    private Widget dragEventWidget;
    private int dragEventWidgetOffsetX = 0;
    private int dragEventWidgetOffsetY = 0;
    private SimpleDayCell lastDragEventCell;
    private boolean scrollable = false;
    private boolean eventCanceled;
    private MonthGrid monthGrid;
    boolean rangeSelect = false;
    boolean dragging = false;

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
        caption.addTouchStartHandler(this);
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

        // Couldn't measure height or it ended up negative. Don't bother
        // continuing
        if (intHeight == -1) {
            return;
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
            if (bottomSpacerTouchStartHandler == null) {
                bottomSpacerTouchStartHandler = bottomspacer
                        .addTouchStartHandler(this);
            }
            bottomspacer.setStyleName("v-calendar-bottom-spacer");
            bottomspacer.setText("+ " + more);
        } else {
            if (!scrollable) {
                if (bottomSpacerMouseDownHandler != null) {
                    bottomSpacerMouseDownHandler.removeHandler();
                    bottomSpacerMouseDownHandler = null;
                }
                if (bottomSpacerTouchStartHandler != null) {
                    bottomSpacerTouchStartHandler.removeHandler();
                    bottomSpacerTouchStartHandler = null;
                }
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
        eventDiv.addTouchStartHandler(this);
        eventDiv.setCalendar(calendar);
        eventDiv.setEventIndex(e.getIndex());

        if (timeEvent) {
            eventDiv.setTimeSpecificEvent(true);
            if (e.getStyleName() != null) {
                eventDiv.addStyleDependentName(e.getStyleName());
            }
            eventDiv.setCaption(e.getCaption());
            eventDiv.setTime(fromDatetime);

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
                eventDiv.setCaption(e.getCaption());

            } else if (fromCompareToDate < 0 && cell == 0) {
                eventDiv.addStyleDependentName("continued-from");
                eventDiv.setCaption(e.getCaption());
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

    @Override
    protected void onAttach() {
        super.onAttach();
        mouseDownRegistration = addDomHandler(this, MouseDownEvent.getType());
        touchStartRegistration = addDomHandler(this, TouchStartEvent.getType());
    }

    @Override
    protected void onDetach() {
        mouseDownRegistration.removeHandler();
        touchStartRegistration.removeHandler();
        super.onDetach();
    }

    private boolean hasMoved(int x, int y, int xthreshold, int ythreshold) {
        int xDiff = startX - x;
        int yDiff = startY - y;
        return Math.abs(xDiff) > xthreshold || Math.abs(yDiff) > ythreshold;
    }

    private void handleMouseUpAndTouchEnd(NativeEvent event) {
        final int x = Util.getTouchOrMouseClientX(event);
        final int y = Util.getTouchOrMouseClientY(event);
        Element el = getElementUnderMouseOrTouch(event, x, y);

        if (!Element.is(el)) {
            if (isDragging()) {
                cancelEventDrag((MonthEventLabel) clickedWidget);
            } else if (isRangeSelect()) {
                getMonthGrid().cancelRangeSelection();
            }
            return;
        }

        Widget w = null;
        com.google.gwt.user.client.Element eventTargetElement = (com.google.gwt.user.client.Element) Element
                .as(el);

        final boolean hasMoved = hasMoved(x, y, 3, 3);
        if (isDragging() && hasMoved) {
            w = Util.findWidget(eventTargetElement, SimpleDayCell.class);
        } else {
            if (labelMouseDown) {
                w = Util.findWidget(eventTargetElement, Label.class);
                if (w != null) {
                    String clickedDate = calendar.getDateFormat().format(date);
                    if (calendar.getDateClickListener() != null) {
                        calendar.getDateClickListener().dateClick(clickedDate);
                    }
                }
            } else if (monthEventMouseDown
                    && calendar.getEventClickListener() != null) {
                w = Util.findWidget(eventTargetElement, MonthEventLabel.class);
                if (w != null) {
                    CalendarEvent e = getEventByWidget((MonthEventLabel) w);
                    calendar.getEventClickListener().eventClick(e);
                }
            }
        }

        if (isDragging()) {
            MonthEventLabel mel = (MonthEventLabel) clickedWidget;

            startX = -1;
            startY = -1;

            if (!mel.isTimeSpecificEvent() && moveEvent != null && w != null
                    && hasMoved) {
                Date newEventDay = ((SimpleDayCell) w).getDate();
                if (isEventMovedToDifferentDay(moveEvent, newEventDay)) {
                    getMonthGrid().removeHighlights();
                    eventMoved(moveEvent, newEventDay);
                    moveEvent = null;
                } else {
                    cancelEventDrag((MonthEventLabel) clickedWidget);
                }
            } else {
                cancelEventDrag((MonthEventLabel) clickedWidget);
            }

        } else if (isRangeSelect()) {
            getMonthGrid().setSelectionReady();
        }
        monthEventMouseDown = false;
        labelMouseDown = false;
        clickedWidget = null;
        if (dragEventWidget != null) {
            dragEventWidget.removeFromParent();
        }
        dragEventWidget = null;
        lastDragEventCell = null;
        dragging = false;
        rangeSelect = false;
    }

    @SuppressWarnings("deprecation")
    private boolean isEventMovedToDifferentDay(CalendarEvent event,
            Date newEventDay) {
        Date oldEventDay = event.getStart();
        return ((oldEventDay.getDate() != newEventDay.getDate())
                || (oldEventDay.getMonth() != newEventDay.getMonth()) || (oldEventDay
                    .getYear() != newEventDay.getYear()));
    }

    public void onMouseDown(MouseDownEvent event) {
        if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
            return;
        }

        handleMouseDownAndTouchStart(event);
    }

    @Override
    public void onTouchStart(TouchStartEvent event) {
        handleMouseDownAndTouchStart(event);
    }

    @Override
    public void onPreviewNativeEvent(NativePreviewEvent event) {
        int typeInt = event.getTypeInt();
        VConsole.error("onPreviewNativeEvent "
                + event.getNativeEvent().getType());
        switch (typeInt) {
        case Event.ONMOUSEUP:
        case Event.ONTOUCHEND:
            event.cancel();
            handleMouseUpAndTouchEnd(event.getNativeEvent());
            nativePreviewRegistration.removeHandler();
            break;
        case Event.ONTOUCHCANCEL:
            event.cancel();
            nativePreviewRegistration.removeHandler();
            break;
        case Event.ONMOUSEMOVE:
        case Event.ONTOUCHMOVE:
            event.cancel();
            handleMouseMoveAndTouchMove(event.getNativeEvent());
            break;
        case Event.ONKEYDOWN:
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE
                    && isDragging()) {
                event.cancel();
                cancelEventDrag((MonthEventLabel) clickedWidget);
            }
            break;
        default:
            break;
        }
    }

    private boolean isRangeSelect() {
        return rangeSelect;
    }

    private boolean isDragging() {
        return dragging;
    }

    private <H extends EventHandler> void handleMouseDownAndTouchStart(
            HumanInputEvent<H> event) {
        if (calendar.isDisabled()) {
            return;
        }

        nativePreviewRegistration = Event.addNativePreviewHandler(this);

        Widget w = (Widget) event.getSource();

        clickedWidget = w;

        if (w instanceof MonthEventLabel) {
            monthEventMouseDown = true;
            if (!((MonthEventLabel) w).isTimeSpecificEvent()) {
                startCalendarEventDrag(event, (MonthEventLabel) clickedWidget);
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
                    rangeSelect = true;
                }
            } else if (w instanceof Label) {
                labelMouseDown = true;
            }
        }

        event.stopPropagation();
        event.preventDefault();
    }

    private void handleMouseMoveAndTouchMove(NativeEvent event) {
        if (isDragging()) {
            handleDragging(event);
        } else if (isRangeSelect()) {
            handleRangeSelect(event);
        }
    }

    private void handleDragging(NativeEvent event) {
        MonthEventLabel w = (MonthEventLabel) clickedWidget;

        if (calendar.isDisabledOrReadOnly()) {
            dragging = false;
            monthEventMouseDown = false;
            startY = -1;
            startX = -1;
            return;
        }

        if (dragEventWidget == null) {
            dragEventWidget = createDragEventWidget(w);
            clickedWidget.getElement().getStyle().setOpacity(0.5);
            Point relativeXY = getRelativeXY(event, clickedWidget.getElement());
            dragEventWidgetOffsetX = relativeXY.x;
            dragEventWidgetOffsetY = relativeXY.y;
        }

        int x = Util.getTouchOrMouseClientX(event);
        int y = Util.getTouchOrMouseClientY(event);

        if (!hasMoved(x, y, 5, 5)) {
            return;
        }

        Element dropElement = getElementUnderMouseOrTouch(event, x, y);

        Widget potentialDropTarget = null;
        if (Element.is(dropElement)) {
            potentialDropTarget = Util.findWidget(
                    (com.google.gwt.user.client.Element) Element
                            .as(dropElement), SimpleDayCell.class);
        }
        if (potentialDropTarget != null) {
            if (potentialDropTarget != lastDragEventCell) {
                getMonthGrid().highlightDayCells(
                        ((SimpleDayCell) potentialDropTarget), moveEvent);
                lastDragEventCell = (SimpleDayCell) potentialDropTarget;
            }
        } else {
            getMonthGrid().removeHighlights();
        }

        Element parent = getMonthGrid().getElement();
        Point relativeXY = getRelativeXY(event, parent);
        Point adjustedXY = checkBoundaries(parent, x, y, relativeXY.x,
                relativeXY.y);

        int newLeft = adjustedXY.x - dragEventWidgetOffsetX;
        int newTop = adjustedXY.y - dragEventWidgetOffsetY;
        RootPanel.get().setWidgetPosition(dragEventWidget, newLeft, newTop);
        lastMoveX = adjustedXY.x;
        lastMoveY = adjustedXY.y;
    }

    private Widget createDragEventWidget(MonthEventLabel from) {
        MonthEventLabel clonedLabel = from.doClone();

        clonedLabel.addStyleName("v-calendar-event-all-day-dd");
        clonedLabel.setWidth(from.getOffsetWidth() + "px");
        if (clonedLabel.getCaption() == null) {
            clonedLabel.setCaption(moveEvent.getCaption());
        }
        RootPanel.get().add(clonedLabel, from.getAbsoluteLeft(),
                from.getAbsoluteTop());

        return clonedLabel;
    }

    private Point checkBoundaries(Element parent, int x, int y, int relX,
            int relY) {
        final int minY = dragEventWidgetOffsetY;
        final int minX = dragEventWidgetOffsetX;
        final int maxY = parent.getClientHeight()
                - dragEventWidget.getOffsetHeight() + dragEventWidgetOffsetY;
        final int maxX = parent.getClientWidth()
                - dragEventWidget.getOffsetWidth() + dragEventWidgetOffsetX;
        // Check boundaries
        if (relY < minY) {
            y += minY - relY;
        } else if (relY >= maxY) {
            y -= relY - maxY;
        }
        if (relX < minX) {
            x += minX - relX;
        } else if (relX >= maxX) {
            x -= relX - maxX;
        }

        return new Point(x, y);
    }

    private void handleRangeSelect(NativeEvent event) {

        final int x = Util.getTouchOrMouseClientX(event);
        final int y = Util.getTouchOrMouseClientY(event);

        if (!hasMoved(x, y, 5, 5)) {
            return;
        }

        Element overElement = getElementUnderMouseOrTouch(event, x, y);
        Widget dayCell = null;
        if (Element.is(overElement)) {
            dayCell = Util.findWidget(
                    (com.google.gwt.user.client.Element) Element
                            .as(overElement), SimpleDayCell.class);
        }

        if (dayCell != null) {
            getMonthGrid().setSelectionEnd((SimpleDayCell) dayCell);
        } else if (!getMonthGrid().getElement().isOrHasChild(overElement)) {
            // Element under x,y is not a descendant of VCalendar.
            getMonthGrid().cancelRangeSelection();
        }
    }

    @SuppressWarnings("deprecation")
    private void eventMoved(CalendarEvent e, Date newDay) {

        Date oldStartTime = e.getStartTime();
        Date newStartTime = new Date(newDay.getTime() + oldStartTime.getHours()
                * VCalendar.HOURINMILLIS + oldStartTime.getMinutes()
                * VCalendar.MINUTEINMILLIS + oldStartTime.getSeconds() * 1000);
        Date newEndTime = new Date(newStartTime.getTime()
                + e.getRangeInMilliseconds());
        Date newStart = new Date(newDay.getTime());
        Date newEnd = new Date(newEndTime.getYear(), newEndTime.getMonth(),
                newEndTime.getDate(), 0, 0, 0);
        e.setStart(newStart);
        e.setEnd(newEnd);
        e.setStartTime(newStartTime);
        e.setEndTime(newEndTime);

        calendar.updateEventToMonthGrid(e);
        if (calendar.getEventMovedListener() != null) {
            calendar.getEventMovedListener().eventMoved(e);
        }
    }

    private <H extends EventHandler> void startCalendarEventDrag(
            HumanInputEvent<H> event, final MonthEventLabel w) {
        startX = Util.getTouchOrMouseClientX(event.getNativeEvent());
        startY = Util.getTouchOrMouseClientY(event.getNativeEvent());
        lastMoveX = startX;
        lastMoveY = startY;

        moveEvent = getEventByWidget(w);

        focus();

        dragging = true;
        GWT.log("Start drag");
    }

    private Point getRelativeXY(NativeEvent event, Element relativeTo) {
        if (Util.isTouchEvent(event)) {
            Touch touch = event.getChangedTouches().get(0);
            return new Point(touch.getRelativeX(relativeTo),
                    touch.getRelativeY(relativeTo));
        } else {
            int relativeX = event.getClientX() - relativeTo.getAbsoluteLeft()
                    + relativeTo.getScrollLeft()
                    + relativeTo.getOwnerDocument().getScrollLeft();
            int relativeY = event.getClientY() - relativeTo.getAbsoluteTop()
                    + relativeTo.getScrollTop()
                    + relativeTo.getOwnerDocument().getScrollTop();
            return new Point(relativeX, relativeY);
        }
    }

    private Element getElementUnderMouseOrTouch(NativeEvent event, int x, int y) {
        Element elem = null;
        if (isDragging() && dragEventWidget != null) {
            dragEventWidget.setVisible(false);
        }
        if (Util.isTouchEvent(event)) {
            elem = Util.getElementFromPoint(x, y);
        } else {
            elem = Util.getElementUnderMouse(event);
        }
        if (isDragging() && dragEventWidget != null) {
            dragEventWidget.setVisible(true);
        }

        return elem;
    }

    protected void cancelEventDrag(MonthEventLabel w) {
        if (isDragging()) {
            // reset drag-related properties
            setFocus(false);
            monthEventMouseDown = false;
            startY = -1;
            startX = -1;
            moveEvent = null;
            labelMouseDown = false;
            if (clickedWidget != null) {
                clickedWidget.getElement().getStyle().setOpacity(1.0);
            }
            clickedWidget = null;
            if (dragEventWidget != null) {
                dragEventWidget.removeFromParent();
            }
            dragEventWidget = null;
            lastDragEventCell = null;
            dragging = false;
        }
        getMonthGrid().removeHighlights();
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

    /**
     * The label in a month cell
     */
    public static class MonthEventLabel extends HTML implements HasTooltipKey {

        private static final String STYLENAME = "v-calendar-event";

        private boolean timeSpecificEvent = false;
        private Integer eventIndex;
        private VCalendar calendar;
        private String caption;
        private Date time;

        /**
         * Default constructor
         */
        public MonthEventLabel() {
            setStylePrimaryName(STYLENAME);
        }

        private MonthEventLabel(Element elem) {
            super(elem);
        }

        public MonthEventLabel doClone() {
            return new MonthEventLabel(Util.cloneNode(getElement(), true));
        }

        /**
         * Set the time of the event label
         * 
         * @param date
         *            The date object that specifies the time
         */
        public void setTime(Date date) {
            time = date;
            renderCaption();
        }

        public String getCaption() {
            return caption;
        }

        /**
         * Set the caption of the event label
         * 
         * @param caption
         *            The caption string, can be HTML
         */
        public void setCaption(String caption) {
            this.caption = caption;
            renderCaption();
        }

        /**
         * Renders the caption in the DIV element
         */
        private void renderCaption() {
            StringBuilder html = new StringBuilder();
            if (caption != null && time != null) {
                html.append("<span class=\"" + STYLENAME + "-time\">");
                html.append(calendar.getTimeFormat().format(time));
                html.append("</span> ");
                html.append(caption);
            } else if (caption != null) {
                html.append(caption);
            } else if (time != null) {
                html.append("<span class=\"" + STYLENAME + "-time\">");
                html.append(calendar.getTimeFormat().format(time));
                html.append("</span>");
            }
            super.setHTML(html.toString());
        }

        /**
         * Set the (server side) index of the event
         * 
         * @param index
         *            The integer index
         */
        public void setEventIndex(int index) {
            eventIndex = index;
        }

        /**
         * Set the Calendar instance this label belongs to
         * 
         * @param calendar
         *            The calendar instance
         */
        public void setCalendar(VCalendar calendar) {
            this.calendar = calendar;
        }

        /**
         * Is the event bound to a specific time
         * 
         * @return
         */
        public boolean isTimeSpecificEvent() {
            return timeSpecificEvent;
        }

        /**
         * Is the event bound to a specific time
         * 
         * @param timeSpecificEvent
         *            True if the event is bound to a time, false if it is only
         *            bound to the day
         */
        public void setTimeSpecificEvent(boolean timeSpecificEvent) {
            this.timeSpecificEvent = timeSpecificEvent;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.google.gwt.user.client.ui.HTML#setHTML(java.lang.String)
         */
        @Override
        public void setHTML(String html) {
            throw new UnsupportedOperationException(
                    "Use setCaption() and setTime() instead");
        }

        @Override
        public Object getTooltipKey() {
            return eventIndex;
        }
    }

    private static class Point {
        public final int x;
        public final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}