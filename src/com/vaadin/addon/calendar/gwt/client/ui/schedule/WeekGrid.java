package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell.DayEvent;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class WeekGrid extends SimplePanel implements NativePreviewHandler {

    private int width = 0;
    private int height = 0;
    private HorizontalPanel content;
    private VCalendar calendar;
    private boolean readOnly;
    private boolean format24h;
    private Timebar timebar;
    private Panel wrapper;
    private int[] cellHeights;
    private int slotInMinutes = 30;
    private int dateCellBorder;

    public WeekGrid(VCalendar parent, boolean format24h) {
        setCalendar(parent);
        this.format24h = format24h;
        content = new HorizontalPanel();
        timebar = new Timebar(format24h);
        content.add(timebar);

        wrapper = new SimplePanel();
        wrapper.setStylePrimaryName("v-calendar-week-wrapper");
        wrapper.add(content);

        setWidget(wrapper);

        Event.addNativePreviewHandler(this);
    }

    private void setScroll(boolean isScrollEnabled) {
        if (isScrollEnabled && !(isScrollable())) {
            wrapper.remove(content);

            final ScrollPanel scrollPanel = new ScrollPanel();
            scrollPanel.setStylePrimaryName("v-calendar-week-wrapper");
            scrollPanel.setWidget(content);

            scrollPanel.addScrollHandler(new ScrollHandler() {
                public void onScroll(ScrollEvent event) {
                    getCalendar().getClient().updateVariable(
                            getCalendar().getPID(), VCalendar.ATTR_SCROLL,
                            scrollPanel.getScrollPosition(), false);
                }
            });

            setWidget(scrollPanel);
            wrapper = scrollPanel;

        } else if (!isScrollEnabled && (isScrollable())) {
            wrapper.remove(content);

            SimplePanel simplePanel = new SimplePanel();
            simplePanel.setStylePrimaryName("v-calendar-week-wrapper");
            simplePanel.setWidget(content);

            setWidget(simplePanel);
            wrapper = simplePanel;
        }
    }

    public void setScrollPosition(int scrollPosition) {
        if (isScrollable()) {
            ((ScrollPanel) wrapper).setScrollPosition(scrollPosition);
        }
    }

    public int getInternalWidth() {
        return width;
    }

    public void addDate(Date d) {
        DateCell dc = new DateCell(this);
        dc.setDate(d);
        dc.setReadOnly(readOnly);
        dc.setSized(isScrollable());
        content.add(dc);
    }

    private boolean isScrollable() {
        return (wrapper instanceof ScrollPanel);
    }

    public void setWidthPX(int width) {
        if (isScrollable()) {
            updateCellWidths();

            // Otherwise the scroll wrapper is somehow too narrow = horizontal
            // scroll
            wrapper.setWidth(content.getOffsetWidth()
                    + Util.getNativeScrollbarSize() + "px");

            this.width = content.getOffsetWidth() - timebar.getOffsetWidth();

        } else {
            this.width = width - timebar.getOffsetWidth();
            updateCellWidths();
        }
    }

    public void setHeightPX(int intHeight) {
        height = intHeight;

        setScroll(height <= -1);

        // if not scrollable, use any height given
        if (!isScrollable() && height > 0) {

            if (BrowserInfo.get().isIE7() || BrowserInfo.get().isIE6()) {
                --height;
            }

            content.setHeight(height + "px");
            setHeight(height + "px");
            wrapper.setHeight(height + "px");
            wrapper.removeStyleDependentName("sized");
            updateCellHeights();
            timebar.setCellHeights(cellHeights);
            timebar.setHeightPX(height);

        } else if (isScrollable()) {
            updateCellHeights();
            wrapper.addStyleDependentName("sized");
            timebar.setHeightPX(height);
        }
    }

    public void clearDates() {
        while (content.getWidgetCount() > 1) {
            content.remove(1);
        }
    }

    public void updateCellWidths() {
        if (!isScrollable()) {
            int count = content.getWidgetCount();
            int datesWidth = width;
            if (datesWidth > 0 && count > 1) {
                int cellWidth = datesWidth / (count - 1);
                int cellWidthMinusBorder = cellWidth - 1;
                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setWidthPX(cellWidthMinusBorder);
                    if (dc.isToday()) {
                        dc.setTimeBarWidth(getOffsetWidth());
                    }
                }
            }

        } else {
            int count = content.getWidgetCount();
            if (count > 1) {
                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setSized(isScrollable());
                }
            }
        }
    }

    public void updateCellHeights() {
        if (!isScrollable()) {
            int count = content.getWidgetCount();
            if (count > 1) {
                DateCell first = (DateCell) content.getWidget(1);
                dateCellBorder = first.getSlotBorder();
                cellHeights = VCalendar.distributeSize(height, 48,
                        -dateCellBorder);
                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setHeightPX(height, cellHeights);
                }
            }

        } else {
            int count = content.getWidgetCount();
            if (count > 1) {
                DateCell first = (DateCell) content.getWidget(1);
                dateCellBorder = first.getSlotBorder();
                int dateHeight = (first.getOffsetHeight() / 48)
                        - dateCellBorder;
                cellHeights = new int[48];
                Arrays.fill(cellHeights, dateHeight);

                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setSized(isScrollable());

                }
            }
        }
    }

    public void addEvent(CalendarEvent e) {
        int dateCount = content.getWidgetCount();
        Date from = e.getStart();
        Date toTime = e.getEndTime();
        for (int i = 1; i < dateCount; i++) {
            DateCell dc = (DateCell) content.getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(toTime);
            if (comp >= 0 && comp2 < 0) {
                // Same event may be over two DateCells if event's date
                // range floats over one day. It can't float over two days,
                // because event which range is over 24 hours, will be handled
                // as a "fullDay" event.
                dc.addEvent(dcDate, e);
            }
        }
    }

    public int getPixelLengthFor(int startFromMinutes, int durationInMinutes) {
        int pixelLength = 0;
        int currentSlot = 0;
        // calculate full slots to event
        int slotsTillEvent = startFromMinutes / slotInMinutes;
        int startOverFlowTime = slotInMinutes
                - (startFromMinutes % slotInMinutes);
        if (startOverFlowTime == slotInMinutes) {
            startOverFlowTime = 0;
            currentSlot = slotsTillEvent;
        } else {
            currentSlot = slotsTillEvent + 1;
        }

        int durationInSlots = 0;
        int endOverFlowTime = 0;

        if (startOverFlowTime > 0) {
            durationInSlots = (durationInMinutes - startOverFlowTime)
                    / slotInMinutes;
            endOverFlowTime = (durationInMinutes - startOverFlowTime)
                    % slotInMinutes;

        } else {
            durationInSlots = durationInMinutes / slotInMinutes;
            endOverFlowTime = durationInMinutes % slotInMinutes;
        }

        // calculate slot overflow at start
        if (startOverFlowTime > 0 && currentSlot < cellHeights.length) {
            int lastSlotHeight = cellHeights[currentSlot] + dateCellBorder;
            pixelLength += (int) (((double) lastSlotHeight / (double) slotInMinutes) * startOverFlowTime);
        }

        // calculate length in full slots
        int lastFullSlot = currentSlot + durationInSlots;
        for (; currentSlot < lastFullSlot && currentSlot < cellHeights.length; currentSlot++) {
            pixelLength += cellHeights[currentSlot] + dateCellBorder;
        }

        // calculate overflow at end
        if (endOverFlowTime > 0 && currentSlot < cellHeights.length) {
            int lastSlotHeight = cellHeights[currentSlot] + dateCellBorder;
            pixelLength += (int) (((double) lastSlotHeight / (double) slotInMinutes) * endOverFlowTime);
        }

        return pixelLength;
    }

    public int getPixelTopFor(int startFromMinutes) {
        int pixelsToTop = 0;
        int slotIndex = 0;

        // calculate full slots to event
        int slotsTillEvent = startFromMinutes / slotInMinutes;
        int overFlowTime = startFromMinutes % slotInMinutes;
        if (slotsTillEvent > 0) {
            for (slotIndex = 0; slotIndex < slotsTillEvent; slotIndex++) {
                pixelsToTop += cellHeights[slotIndex] + dateCellBorder;
            }
        }

        // calculate lengths less than one slot
        if (overFlowTime > 0) {
            int lastSlotHeight = cellHeights[slotIndex] + dateCellBorder;
            pixelsToTop += ((double) lastSlotHeight / (double) slotInMinutes)
                    * overFlowTime;
        }

        return pixelsToTop;
    }

    public void eventMoved(DayEvent dayEvent) {
        Style s = dayEvent.getElement().getStyle();
        int left = Integer.parseInt(s.getLeft().substring(0,
                s.getLeft().length() - 2));
        int datesWidth = width;
        int count = content.getWidgetCount();
        int cellWidth = datesWidth / (count - 1);
        DateCell previousParent = (DateCell) dayEvent.getParent();
        DateCell newParent = (DateCell) content
                .getWidget((left / cellWidth) + 1);
        CalendarEvent se = dayEvent.getCalendarEvent();
        previousParent.removeEvent(dayEvent);
        newParent.addEvent(dayEvent);
        if (!previousParent.equals(newParent)) {
            previousParent.recalculateEventWidths();
        }
        newParent.recalculateEventWidths();
        DateTimeFormat dateformat_date = DateTimeFormat.getFormat("yyyy-MM-dd");
        DateTimeFormat dateformat_time = DateTimeFormat.getFormat("HH-mm");
        String eventMove = se.getIndex() + ":"
                + dateformat_date.format(se.getStart()) + "-"
                + dateformat_time.format(se.getStartTime());

        if (getCalendar().getClient().hasEventListeners(getCalendar(),
                CalendarEventId.EVENTMOVE)) {
            getCalendar().getClient().updateVariable(getCalendar().getPID(),
                    CalendarEventId.EVENTMOVE, eventMove, true);
        }
    }

    public void setToday(Date todayDate, Date todayTimestamp) {
        int count = content.getWidgetCount();
        if (count > 1) {
            for (int i = 1; i < count; i++) {
                DateCell dc = (DateCell) content.getWidget(i);
                if (dc.getDate().getTime() == todayDate.getTime()) {
                    if (isScrollable()) {
                        dc.setToday(todayTimestamp, -1);
                    } else {
                        dc.setToday(todayTimestamp, getOffsetWidth());
                    }
                }
            }
        }
    }

    public void onPreviewNativeEvent(NativePreviewEvent event) {
        if (event.getTypeInt() == Event.ONMOUSEDOWN
                && DOM.isOrHasChild(getElement(),
                        (com.google.gwt.user.client.Element) Element.as(event
                                .getNativeEvent().getEventTarget()))) {
            event.getNativeEvent().preventDefault();
        }
    }

    public void addReservedEvent(ReservedCalendarEvent e) {
        int dateCount = content.getWidgetCount();
        Date from = e.getFromDate();
        Date to = e.getToDate();
        for (int i = 1; i < dateCount; i++) {
            DateCell dc = (DateCell) content.getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(to);
            if (comp >= 0 && comp2 <= 0) {
                dc.addReservedEvent(e);
            }
        }
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isFormat24h() {
        return format24h;
    }

    public void setFormat24h(boolean format24h) {
        this.format24h = format24h;
    }

    public Timebar getTimeBar() {
        return timebar;
    }

    public void setDateColor(Date when, Date to, String styleName) {
        int dateCount = content.getWidgetCount();
        for (int i = 1; i < dateCount; i++) {
            DateCell dc = (DateCell) content.getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(when);
            int comp2 = dcDate.compareTo(to);
            if (comp >= 0 && comp2 <= 0) {
                dc.setDateColor(styleName);
            }
        }
    }

    /**
     * @param calendar
     *            the calendar to set
     */
    public void setCalendar(VCalendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the calendar
     */
    public VCalendar getCalendar() {
        return calendar;
    }

    public static class Timebar extends HTML {

        private int height;

        private int verticalPadding = 7; // FIXME measure this from DOM

        private int[] slotCellHeights;

        public Timebar(boolean format24h) {
            createTimeBar(format24h);
        }

        public void setCellHeights(int[] cellHeights) {
            slotCellHeights = cellHeights;
        }

        private void createTimeBar(boolean format24h) {
            setStylePrimaryName("v-calendar-times");

            // Fist "time" is empty
            Element e = DOM.createDiv();
            setStyleName(e, "v-calendar-time");
            e.setInnerText("");
            getElement().appendChild(e);

            DateTimeService dts = new DateTimeService();

            if (format24h) {
                for (int i = 1; i < 24; i++) {
                    e = DOM.createDiv();
                    setStyleName(e, "v-calendar-time");
                    String delimiter = dts.getClockDelimeter();
                    e.setInnerHTML("<span>" + i + "</span>" + delimiter + "00");
                    getElement().appendChild(e);
                }
            } else {
                // FIXME Use dts.getAmPmStrings(); and make sure that
                // DateTimeService has a some Locale set.
                String[] ampm = new String[] { "AM", "PM" };
                for (int i = 1; i < 13; i++) {
                    e = DOM.createDiv();
                    setStyleName(e, "v-calendar-time");
                    e.setInnerHTML("<span>" + i + "</span>" + " " + ampm[0]);
                    getElement().appendChild(e);
                }
                for (int i = 1; i < 12; i++) {
                    e = DOM.createDiv();
                    setStyleName(e, "v-calendar-time");
                    e.setInnerHTML("<span>" + i + "</span>" + " " + ampm[1]);
                    getElement().appendChild(e);
                }
            }
        }

        public void updateTimeBar(boolean format24h) {
            clear();
            createTimeBar(format24h);
        }

        private void clear() {
            while (getElement().getChildCount() > 0) {
                getElement().removeChild(getElement().getChild(0));
            }
        }

        public void setHeightPX(int pixelHeight) {
            height = pixelHeight;

            if (pixelHeight > -1) {
                // as the negative margins on children pulls the whole element
                // upwards, we must compensate. otherwise the element would be
                // too short
                super.setHeight((height + verticalPadding) + "px");
                removeStyleDependentName("sized");
                updateChildHeights();

            } else {
                addStyleDependentName("sized");
                updateChildHeights();
            }
        }

        private void updateChildHeights() {
            int childCount = getElement().getChildCount();

            if (height != -1) {

                // 23 hours + first is empty
                // we try to adjust the height of time labels to the distributed
                // heights of the time slots
                int slotsPerHour = slotCellHeights.length / 24;
                int[] cellHeights = new int[slotCellHeights.length
                        / slotsPerHour];

                int slotHeightPosition = 0;
                for (int i = 0; i < cellHeights.length; i++) {
                    for (int j = slotHeightPosition; j < slotHeightPosition
                            + slotsPerHour; j++) {
                        cellHeights[i] += slotCellHeights[j] + 1;
                        // 1px more for borders
                        // FIXME measure from DOM
                    }
                    slotHeightPosition += slotsPerHour;
                }

                for (int i = 0; i < childCount; i++) {
                    Element e = (Element) getElement().getChild(i);
                    e.getStyle().setHeight(cellHeights[i], Unit.PX);
                }

            } else {
                for (int i = 0; i < childCount; i++) {
                    Element e = (Element) getElement().getChild(i);
                    e.getStyle().setProperty("height", "");
                }
            }
        }
    }

    public static class DateCell extends ComplexPanel implements
            MouseDownHandler, MouseMoveHandler, MouseUpHandler {
        private Date date;
        private int width;
        private int eventRangeStart = -1;
        private int eventRangeStop = -1;
        private WeekGrid weekgrid;
        private boolean isReadOnly = false;
        private int height;
        private Element[] slotElements;
        private int[] slotElementHeights;
        private int startingSlotHeight;
        private Date today;
        private Element todaybar;

        // private double slotHeight;

        public DateCell(WeekGrid parent) {
            weekgrid = parent;
            Element mainElement = DOM.createDiv();
            setElement(mainElement);
            addStyleName("v-calendar-day-times");
            addHandler(this, MouseDownEvent.getType());
            addHandler(this, MouseUpEvent.getType());
            addHandler(this, MouseMoveEvent.getType());

            slotElements = new Element[48];
            slotElementHeights = new int[48];

            for (int i = 0; i < 48; i++) {
                Element e = DOM.createDiv();
                if (i % 2 == 0) {
                    setStyleName(e, "v-slot-even");
                } else {
                    setStyleName(e, "v-slot");
                }
                e.setInnerHTML("&nbsp;");
                Event.sinkEvents(e, Event.MOUSEEVENTS);
                mainElement.appendChild(e);
                slotElements[i] = e;
            }
            Event.sinkEvents(mainElement, Event.MOUSEEVENTS);
        }

        public void setTimeBarWidth(int timebarWidth) {
            todaybar.getStyle().setWidth(timebarWidth, Unit.PX);
        }

        /**
         * @param isSized
         *            if true, this DateCell is sized with CSS and not via
         *            {@link #setWidthPX(int)}
         */
        public void setSized(boolean isSized) {
            if (isSized) {
                addStyleDependentName("sized");

                width = getOffsetWidth()
                        - Util.measureHorizontalBorder(getElement());
                recalculateEventWidths();

                // recalc heights&size for events. all other height sizes come
                // from css
                startingSlotHeight = slotElements[0].getOffsetHeight();
                recalculateEventPositions();

                if (isToday()) {
                    recalculateTimeBarPosition();
                }

            } else {
                removeStyleDependentName("sized");
            }
        }

        @Override
        protected void onUnload() {
            super.onUnload();
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setWidthPX(int cellWidth) {
            width = cellWidth;
            setWidth(cellWidth + "px");
            recalculateEventWidths();
        }

        public void setHeightPX(int height, int[] cellHeights) {
            this.height = height;
            slotElementHeights = cellHeights;
            setHeight(height + "px");
            recalculateCellHeights();
            recalculateEventPositions();
            if (today != null) {
                recalculateTimeBarPosition();
            }
        }

        // date methods are not depricated in GWT
        @SuppressWarnings("deprecation")
        private void recalculateTimeBarPosition() {
            int h = today.getHours();
            int m = today.getMinutes();
            int pixelTop = weekgrid.getPixelTopFor(m + 60 * h);
            todaybar.getStyle().setTop(pixelTop, Unit.PX);
        }

        private void recalculateEventPositions() {
            for (int i = 0; i < getWidgetCount(); i++) {
                DayEvent dayEvent = (DayEvent) getWidget(i);
                updatePositionFor(dayEvent, getDate(), dayEvent
                        .getCalendarEvent());
            }
        }

        public void recalculateEventWidths() {
            List<Group> groups = new ArrayList<Group>();

            int count = getWidgetCount();

            List<Integer> handled = new ArrayList<Integer>();

            // Iterate through all events and group them. Events that overlaps
            // with each other, are added to the same group.
            for (int i = 0; i < count; i++) {
                if (handled.contains(i)) {
                    continue;
                }

                Group curGroup = getOverlappingEvents(i);
                handled.addAll(curGroup.getItems());

                int top = curGroup.top;
                int bottom = curGroup.bottom;

                boolean newGroup = true;
                // No need to check other groups, if size equals the count
                if (curGroup.getItems().size() != count) {
                    // Check other groups. When the whole group overlaps with
                    // other group, the group is merged to the other.
                    for (Group g : groups) {
                        int nextTop = g.top;
                        int nextBottom = g.bottom;

                        if (doOverlap(top, bottom, nextTop, nextBottom)) {
                            newGroup = false;
                            updateGroup(g, curGroup, top, bottom);
                        }
                    }
                } else {
                    if (newGroup) {
                        groups.add(curGroup);
                    }
                    break;
                }

                if (newGroup) {
                    groups.add(curGroup);
                }
            }

            drawDayEvents(groups);
        }

        private void recalculateCellHeights() {
            startingSlotHeight = height / 48;
            //
            // // account for borders
            // int border = getSlotBorder();
            //
            // // startingSlotHeight = startingSlotHeight - border;
            //
            // slotElementHeights = VCalendar.distributeSize(height, 48,
            // -border);

            boolean isIE6 = BrowserInfo.get().isIE6();

            for (int i = 0; i < slotElements.length; i++) {
                slotElements[i].getStyle().setHeight(slotElementHeights[i],
                        Unit.PX);

                if (isIE6) {
                    slotElements[i].getStyle().setProperty("lineHeight",
                            slotElementHeights[i] + "px");
                }
            }
        }

        public int getSlotHeight() {
            return startingSlotHeight;
        }

        public int getSlotBorder() {
            return Util
                    .measureVerticalBorder((com.google.gwt.user.client.Element) slotElements[0]);

        }

        private void drawDayEvents(List<Group> groups) {
            for (Group g : groups) {
                int col = 0;
                int colCount = 0;
                List<Integer> order = new ArrayList<Integer>();
                Map<Integer, Integer> columns = new HashMap<Integer, Integer>();
                for (Integer eventIndex : g.getItems()) {
                    DayEvent d = (DayEvent) getWidget(eventIndex);
                    d.setMoveWidth(width);

                    int freeSpaceCol = findFreeColumnSpaceOnLeft(d.getTop(), d
                            .getTop()
                            + d.getOffsetHeight() - 1, order, columns);
                    if (freeSpaceCol >= 0) {
                        col = freeSpaceCol;
                        columns.put(eventIndex, col);
                        int newOrderindex = 0;
                        for (Integer i : order) {
                            if (columns.get(i) >= col) {
                                newOrderindex = order.indexOf(i);
                                break;
                            }
                        }
                        order.add(newOrderindex, eventIndex);
                    } else {
                        // New column
                        col = colCount++;
                        columns.put(eventIndex, col);
                        order.add(eventIndex);
                    }
                }

                // Update widths and left position
                int eventWidth = (width / colCount);
                for (Integer index : g.getItems()) {
                    DayEvent d = (DayEvent) getWidget(index);
                    d.getElement().getStyle().setMarginLeft(
                            (eventWidth * columns.get(index)), Unit.PX);
                    d.setWidth(eventWidth + "px");
                    d.setSlotHeightInPX(getSlotHeight());
                }
            }
        }

        private int findFreeColumnSpaceOnLeft(int top, int bottom,
                List<Integer> order, Map<Integer, Integer> columns) {
            int freeSpot = -1;
            int skipIndex = -1;
            for (Integer eventIndex : order) {
                int col = columns.get(eventIndex);
                if (col == skipIndex) {
                    continue;
                }

                if (freeSpot != -1 && freeSpot != col) {
                    // Free spot found
                    return freeSpot;
                }

                DayEvent d = (DayEvent) getWidget(eventIndex);

                if (doOverlap(top, bottom, d.getTop(), d.getTop()
                        + d.getOffsetHeight() - 1)) {
                    skipIndex = col;
                    freeSpot = -1;
                } else {
                    freeSpot = col;
                }
            }

            return freeSpot;
        }

        private boolean doOverlap(int top, int bottom, int nextTop,
                int nextBottom) {
            boolean isInsideFromBottomSide = top >= nextTop
                    && top <= nextBottom;
            boolean isFullyInside = top >= nextTop && bottom <= nextBottom;
            boolean isInsideFromTopSide = bottom <= nextBottom
                    && bottom >= nextTop;
            boolean isFullyOverlapping = top <= nextTop && bottom >= nextBottom;
            return isInsideFromBottomSide || isFullyInside
                    || isInsideFromTopSide || isFullyOverlapping;
        }

        /* Update top and bottom values. Add new index to the group. */
        private void updateGroup(Group targetGroup, Group byGroup, int top,
                int bottom) {
            if (top < targetGroup.top) {
                targetGroup.top = top;
            }
            if (bottom > targetGroup.bottom) {
                targetGroup.bottom = bottom;
            }

            for (Integer index : byGroup.getItems()) {
                if (!targetGroup.getItems().contains(index)) {
                    targetGroup.add(index);
                }
            }
        }

        /**
         * Returns all overlapping DayEvent indexes in the Group. Including the
         * target.
         * 
         * @param targetIndex
         *            Index of DayEvent in the current DateCell widget.
         * @return Group that contains all Overlapping DayEvent indexes
         */
        public Group getOverlappingEvents(int targetIndex) {
            Group g = new Group(targetIndex);

            int count = getWidgetCount();
            DayEvent target = (DayEvent) getWidget(targetIndex);
            int top = target.getTop();
            int bottom = top + target.getOffsetHeight() - 1;

            for (int i = 0; i < count; i++) {
                if (targetIndex == i) {
                    continue;
                }

                DayEvent d = (DayEvent) getWidget(i);
                int nextTop = d.getTop();
                int nextBottom = nextTop + d.getOffsetHeight() - 1;
                if (doOverlap(top, bottom, nextTop, nextBottom)) {
                    g.add(i);

                    // Update top & bottom values to the greatest
                    if (nextTop < top) {
                        top = nextTop;
                    }
                    if (nextBottom > bottom) {
                        bottom = nextBottom;
                    }
                }
            }

            g.top = top;
            g.bottom = bottom;
            return g;
        }

        public Date getDate() {
            return date;
        }

        public void addEvent(Date targetDay, CalendarEvent calendarEvent) {
            Element main = getElement();
            DayEvent dayEvent = new DayEvent(weekgrid, calendarEvent);
            dayEvent.setSlotHeightInPX(getSlotHeight());
            dayEvent.setReadOnly(isReadOnly);

            if (startingSlotHeight > 0) {
                updatePositionFor(dayEvent, targetDay, calendarEvent);
            }

            add(dayEvent, (com.google.gwt.user.client.Element) main);
        }

        @SuppressWarnings("deprecation")
        private void updatePositionFor(DayEvent dayEvent, Date targetDay,
                CalendarEvent calendarEvent) {
            Date fromDt = calendarEvent.getStartTime();
            int h = fromDt.getHours();
            int m = fromDt.getMinutes();
            long range = calendarEvent.getRangeInMinutesForDay(targetDay);

            boolean onDifferentDays = calendarEvent.isTimeOnDifferentDays();
            if (onDifferentDays) {
                if (calendarEvent.getEnd().compareTo(targetDay) == 0) {
                    // Current day slot is for the end date. Lets fix also the
                    // start & end times.
                    h = 0;
                    m = 0;
                }
            }

            int startFromMinutes = (h * 60) + m;
            dayEvent.updatePosition(startFromMinutes, range);
        }

        public void addEvent(DayEvent dayEvent) {
            Element main = getElement();
            int index = 0;
            List<CalendarEvent> events = new ArrayList<CalendarEvent>();

            // events are the only widgets in this panel
            // slots are just elements
            for (; index < getWidgetCount(); index++) {
                DayEvent dc = (DayEvent) getWidget(index);
                dc.setReadOnly(isReadOnly);
                events.add(dc.getCalendarEvent());
            }
            events.add(dayEvent.getCalendarEvent());

            index = 0;
            for (CalendarEvent e : weekgrid.getCalendar().sortEventsByDuration(
                    events)) {
                if (e.equals(dayEvent.getCalendarEvent())) {
                    break;
                }
                index++;
            }
            this.insert(dayEvent, (com.google.gwt.user.client.Element) main,
                    index, true);
        }

        public void removeEvent(DayEvent dayEvent) {
            remove(dayEvent);
        }

        public void onMouseDown(MouseDownEvent event) {
            Element e = Element.as(event.getNativeEvent().getEventTarget());
            if (e.getClassName().contains("reserved") || isReadOnly) {
                eventRangeStart = -1;
            } else {
                eventRangeStart = event.getY();
                eventRangeStop = eventRangeStart;
                Event.setCapture(getElement());
            }

        }

        @SuppressWarnings("deprecation")
        public void onMouseUp(MouseUpEvent event) {
            Event.releaseCapture(getElement());
            if (eventRangeStart > -1) {
                Element main = getElement();
                if (eventRangeStart > eventRangeStop) {
                    if (eventRangeStop <= -1) {
                        eventRangeStop = 0;
                    }
                    int temp = eventRangeStart;
                    eventRangeStart = eventRangeStop;
                    eventRangeStop = temp;
                }
                boolean reservedFound = false;
                NodeList<Node> nodes = main.getChildNodes();
                // FIXME measure all hardcoded pixels from the DOM or at least
                // use a shared constant (I believe the 19px is the same as
                // HALFHOUR_IN_PX)
                int slot = (int) (((eventRangeStart - ((double) eventRangeStart % getSlotHeight())) / getSlotHeight()));
                int slotEnd = (int) ((eventRangeStop - ((double) eventRangeStop % getSlotHeight())) / getSlotHeight());
                if (slotEnd > 47) {
                    slotEnd = 47;
                }

                GWT.log("Slot start " + slot + " slot end " + slotEnd);

                int slotEndBeforeReserved = slotEnd;
                for (int i = slot; i <= slotEnd; i++) {
                    Element c = (Element) nodes.getItem(i);
                    if (c == null) {
                        continue;
                    }

                    c.removeClassName("v-daterange");
                    if (!reservedFound
                            && c.getClassName().contains("v-reserved")) {
                        reservedFound = true;
                        slotEndBeforeReserved = i - 1;
                    }
                }

                int startMinutes = slot * 30;
                int endMinutes = (slotEndBeforeReserved + 1) * 30;
                VCalendar schedule = weekgrid.getCalendar();
                Date currentDate = getDate();
                String yr = (currentDate.getYear() + 1900) + "-"
                        + (currentDate.getMonth() + 1) + "-"
                        + currentDate.getDate();

                if (schedule.getClient().hasEventListeners(schedule,
                        CalendarEventId.RANGESELECT)) {
                    schedule.getClient().updateVariable(schedule.getPID(),
                            CalendarEventId.RANGESELECT,
                            yr + ":" + startMinutes + ":" + endMinutes, true);
                }
                eventRangeStart = -1;
            }
        }

        public void onMouseMove(MouseMoveEvent event) {
            if (eventRangeStart >= 0) {
                int newY = event.getY();
                int fromY = 0;
                int toY = 0;
                if (newY < eventRangeStart) {
                    fromY = newY;
                    toY = eventRangeStart;
                } else {
                    fromY = eventRangeStart;
                    toY = newY;
                }
                Element main = getElement();
                eventRangeStop = newY;
                NodeList<Node> nodes = main.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element c = (Element) nodes.getItem(i);
                    int elemStart = c.getOffsetTop();
                    int elemStop = elemStart + getSlotHeight();
                    if (elemStart >= fromY && elemStart <= toY) {
                        c.addClassName("v-daterange");
                    } else if (elemStop >= fromY && elemStop <= toY) {
                        c.addClassName("v-daterange");
                    } else if (elemStop >= fromY && elemStart <= toY) {
                        c.addClassName("v-daterange");
                    } else {
                        c.removeClassName("v-daterange");
                    }
                }
            }
        }

        public void setToday(Date today, int width) {
            this.today = today;
            addStyleDependentName("today");
            Element lastChild = (Element) getElement().getLastChild();
            if (lastChild.getClassName().equals("v-calendar-current-time")) {
                todaybar = lastChild;
            } else {
                todaybar = DOM.createDiv();
                todaybar.setClassName("v-calendar-current-time");
                getElement().appendChild(todaybar);
            }

            if (width != -1) {
                todaybar.getStyle().setWidth(width, Unit.PX);
            }

            // position is calculated later, when we know the cell heights
        }

        @SuppressWarnings("deprecation")
        public void addReservedEvent(ReservedCalendarEvent e) {
            Element main = getElement();
            Date fromDt = e.getFromDatetime();
            Date toDt = e.getToDatetime();
            int h = fromDt.getHours();
            int m = fromDt.getMinutes();
            int slot = h * 2;
            if (m >= 30) {
                slot += 1;
            }
            h = toDt.getHours();
            m = toDt.getMinutes();

            int slotEnd = h * 2;
            if (m > 0 && m <= 30) {
                slotEnd += 1;
            } else if (m > 30) {
                slotEnd += 2;
            }
            for (int i = slot; i < slotEnd; i++) {
                Element slotelement = Element.as(main.getChild(i));
                slotelement.addClassName("reserved");
            }
        }

        public void setReadOnly(boolean readOnly) {
            isReadOnly = readOnly;
        }

        public boolean isReadOnly() {
            return isReadOnly;
        }

        public void setDateColor(String styleName) {
            this.setStyleName("v-calendar-datecell " + styleName);
        }

        public boolean isToday() {
            return today != null;
        }

        private static class Group {
            public int top;
            public int bottom;
            private List<Integer> items;

            public Group(Integer index) {
                items = new ArrayList<Integer>();
                items.add(index);
            }

            public List<Integer> getItems() {
                return items;
            }

            public void add(Integer index) {
                items.add(index);
            }
        }

        public static class DayEvent extends HTML implements MouseDownHandler,
                MouseUpHandler, MouseMoveHandler {
            private Element caption = null;
            private Element eventContent;
            private CalendarEvent calendarEvent = null;
            private HandlerRegistration moveRegistration;
            private int startY = -1;
            private int startX = -1;
            private String moveWidth;
            public static final int halfHourInMilliSeconds = 1800 * 1000;
            private Date startDatetimeFrom;
            private Date startDatetimeTo;
            private boolean mouseMoveStarted;
            private int top;
            private int startYrelative;
            private int startXrelative;
            private boolean readOnly;
            private WeekGrid weekGrid;
            private com.google.gwt.user.client.Element topResizeBar;
            private com.google.gwt.user.client.Element bottomResizeBar;
            private Element clickTarget;
            private Integer eventIndex;
            private boolean eventMoveAllowed;
            private int slotHeight;

            // private int slotHeight;
            // private int doubleSlotHeight;

            public DayEvent(WeekGrid parent, CalendarEvent event) {
                super();
                setStylePrimaryName("v-calendar-event");
                setCalendarEvent(event);

                weekGrid = parent;

                Style s = getElement().getStyle();
                if (event.getStyleName().length() > 0) {
                    addStyleDependentName(event.getStyleName());
                }
                s.setPosition(Position.ABSOLUTE);

                caption = DOM.createDiv();
                caption.addClassName("v-calendar-event-caption");
                getElement().appendChild(caption);

                eventContent = DOM.createDiv();
                eventContent.addClassName("v-calendar-event-content");
                getElement().appendChild(eventContent);

                VCalendar calendar = weekGrid.getCalendar();
                if (calendar.getClient().hasEventListeners(calendar,
                        CalendarEventId.EVENTRESIZE)) {
                    topResizeBar = DOM.createDiv();
                    bottomResizeBar = DOM.createDiv();

                    topResizeBar.addClassName("v-calendar-event-resizetop");
                    bottomResizeBar
                            .addClassName("v-calendar-event-resizebottom");

                    getElement().appendChild(topResizeBar);
                    getElement().appendChild(bottomResizeBar);
                }

                addMouseDownHandler(this);
                addMouseUpHandler(this);

                sinkEvents(VTooltip.TOOLTIP_EVENTS);
                eventIndex = event.getIndex();

                eventMoveAllowed = calendar.getClient().hasEventListeners(
                        calendar, CalendarEventId.EVENTMOVE);
            }

            // public void setSlotHeightInPX(int slotHeight) {
            // this.slotHeight = slotHeight;
            // doubleSlotHeight = 2 * slotHeight;
            // }

            public void setSlotHeightInPX(int slotHeight) {
                this.slotHeight = slotHeight;
            }

            @Override
            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);
                VCalendar calendar = weekGrid.getCalendar();
                if (calendar.getClient() != null) {
                    calendar.getClient().handleTooltipEvent(event, calendar,
                            eventIndex);
                }
            }

            public void updatePosition(long startFromMinutes,
                    long durationInMinutes) {
                if (startFromMinutes < 0) {
                    startFromMinutes = 0;
                }
                top = weekGrid.getPixelTopFor((int) startFromMinutes);

                getElement().getStyle().setTop(top, Unit.PX);
                if (durationInMinutes > 0) {
                    int heightMinutes = weekGrid.getPixelLengthFor(
                            (int) startFromMinutes, (int) durationInMinutes);
                    setHeight(heightMinutes);
                } else {
                    setHeight(-1);
                }

                boolean multiRowCaption = (durationInMinutes > 30);
                updateCaptions(multiRowCaption);
            }

            public int getTop() {
                return top;
            }

            public void setMoveWidth(int width) {
                moveWidth = width + "px";
            }

            public void setHeight(int h) {
                if (h == -1) {
                    getElement().getStyle().setProperty("height", "");
                    eventContent.getStyle().setProperty("height", "");
                } else {
                    getElement().getStyle().setHeight(h, Unit.PX);
                    // FIXME measure the border height (2px) from the DOM
                    eventContent.getStyle().setHeight(h - 2, Unit.PX);
                }
            }

            /**
             * @param bigMode
             *            If false, event is so small that caption must be in
             *            time-row
             */
            private void updateCaptions(boolean bigMode) {
                String separator = bigMode ? "<br />" : ": ";
                caption.setInnerHTML("<span>" + calendarEvent.getTimeAsText()
                        + "</span>" + separator
                        + Util.escapeHTML(calendarEvent.getCaption()));
                eventContent.setInnerHTML("");
            }

            public void onMouseDown(MouseDownEvent event) {
                clickTarget = Element.as(event.getNativeEvent()
                        .getEventTarget());
                if (eventMoveAllowed || clickTargetsResize()) {
                    moveRegistration = addMouseMoveHandler(this);
                    startX = event.getClientX();
                    startY = event.getClientY();
                    try {
                        startYrelative = (int) ((double) event
                                .getRelativeY(caption) % slotHeight);
                        startXrelative = (event.getRelativeX(weekGrid
                                .getElement()) - weekGrid.timebar
                                .getOffsetWidth())
                                % getDateCellWidth();
                    } catch (Exception e) {
                        GWT.log("foo", e);
                    }
                    mouseMoveStarted = false;
                    Style s = getElement().getStyle();
                    startDatetimeFrom = (Date) calendarEvent.getStartTime()
                            .clone();
                    startDatetimeTo = (Date) calendarEvent.getEndTime().clone();
                    s.setZIndex(1000);
                    Event.setCapture(getElement());
                    event.getNativeEvent().stopPropagation();
                }

                // make sure the right cursor is always displayed
                if (clickTargetsResize()) {
                    addGlobalResizeStyle();
                }
            }

            public void onMouseUp(MouseUpEvent event) {
                Event.releaseCapture(getElement());
                if (moveRegistration != null) {
                    moveRegistration.removeHandler();
                }
                int endX = event.getClientX();
                int endY = event.getClientY();
                int xDiff = startX - endX;
                int yDiff = startY - endY;
                startX = -1;
                startY = -1;
                mouseMoveStarted = false;
                Style s = getElement().getStyle();
                s.setZIndex(1);
                if (!clickTargetsResize()) {
                    if (xDiff < -3 || xDiff > 3 || yDiff < -3 || yDiff > 3) {
                        weekGrid.eventMoved(this);
                    } else {
                        EventTarget et = event.getNativeEvent()
                                .getEventTarget();
                        Element e = Element.as(et);
                        if (e == caption || e == eventContent) {

                            VCalendar calendar = weekGrid.getCalendar();

                            if (calendar.getClient().hasEventListeners(
                                    calendar, CalendarEventId.EVENTCLICK)) {
                                calendar.getClient().updateVariable(
                                        calendar.getPID(),
                                        CalendarEventId.EVENTCLICK,
                                        calendarEvent.getIndex(), true);
                            }
                        } else if (e == getElement()) {
                        }
                    }

                } else { // click targeted resize bar
                    removeGlobalResizeStyle();

                    VCalendar calendar = weekGrid.getCalendar();

                    if (calendar.getClient().hasEventListeners(calendar,
                            CalendarEventId.EVENTRESIZE)) {
                        calendar.getClient().updateVariable(calendar.getPID(),
                                CalendarEventId.EVENTRESIZE,
                                buildResizeString(calendarEvent), true);
                    }
                }
            }

            @SuppressWarnings("deprecation")
            public void onMouseMove(MouseMoveEvent event) {
                if (startY < 0 && startX < 0) {
                    return;
                }
                if (isReadOnly()) {
                    Event.releaseCapture(getElement());
                    mouseMoveStarted = false;
                    startY = -1;
                    startX = -1;
                    removeGlobalResizeStyle();
                    return;
                }
                int currentY = event.getClientY();
                int currentX = event.getClientX();
                int moveY = (currentY - startY);
                int moveX = (currentX - startX);
                if ((moveY < 5 && moveY > -6) && (moveX < 5 && moveX > -6)) {
                    return;
                }
                if (!mouseMoveStarted) {
                    setWidth(moveWidth);
                    getElement().getStyle().setMarginLeft(0, Unit.PX);
                    mouseMoveStarted = true;
                }

                HorizontalPanel parent = (HorizontalPanel) getParent()
                        .getParent();
                int relativeX = event.getRelativeX(parent.getElement())
                        - weekGrid.timebar.getOffsetWidth();
                int halfHourDiff = 0;
                if (moveY > 0) {
                    halfHourDiff = (startYrelative + moveY) / slotHeight;
                } else {
                    halfHourDiff = (moveY - startYrelative) / slotHeight;
                }

                int dateCellWidth = getDateCellWidth();
                long dayDiff = 0;
                if (moveX >= 0) {
                    dayDiff = (startXrelative + moveX) / dateCellWidth;
                } else {
                    dayDiff = (moveX - (dateCellWidth - startXrelative))
                            / dateCellWidth;
                }

                int dayOffset = relativeX / dateCellWidth;
                dayOffset = dayOffset * dateCellWidth
                        + weekGrid.timebar.getOffsetWidth();
                if (relativeX < 0 || relativeX >= getDatesWidth()) {
                    return;
                }

                Style s = getElement().getStyle();

                Date from = calendarEvent.getStartTime();
                Date to = calendarEvent.getEndTime();
                long duration = to.getTime() - from.getTime();

                if (!clickTargetsResize() && eventMoveAllowed) {
                    long daysMs = dayDiff * VCalendar.DAYINMILLIS;
                    from.setTime(startDatetimeFrom.getTime() + daysMs);
                    from.setTime(from.getTime()
                            + ((long) halfHourInMilliSeconds * halfHourDiff));
                    to.setTime((from.getTime() + duration));
                    calendarEvent.setStartTime(from);
                    calendarEvent.setEndTime(to);
                    calendarEvent.setStart(new Date(from.getTime()));
                    calendarEvent.setEnd(new Date(to.getTime()));

                    // Set new position for the event
                    long startFromMinutes = (from.getHours() * 60)
                            + from.getMinutes();
                    long range = calendarEvent.getRangeInMinutes();
                    boolean eventStartAtDifferentDay = from.getDate() != to
                            .getDate();
                    if (eventStartAtDifferentDay) {
                        long minutesOnPrevDay = (getTargetDateByCurrentPosition(
                                dayOffset).getTime() - from.getTime())
                                / VCalendar.MINUTEINMILLIS;
                        startFromMinutes = -1 * minutesOnPrevDay;
                    }
                    updatePosition(startFromMinutes, range);

                    s.setLeft(dayOffset, Unit.PX);

                } else if (clickTarget == topResizeBar) {
                    long oldStartTime = startDatetimeFrom.getTime();
                    long newStartTime = oldStartTime
                            + ((long) halfHourInMilliSeconds * halfHourDiff);

                    if (!isTimeRangeTooSmall(newStartTime, startDatetimeTo
                            .getTime())) {
                        newStartTime = startDatetimeTo.getTime()
                                - getMinTimeRange();
                    }

                    from.setTime(newStartTime);

                    calendarEvent.setStartTime(from);

                    // Set new position for the event
                    long startFromMinutes = (from.getHours() * 60)
                            + from.getMinutes();
                    long range = calendarEvent.getRangeInMinutes();

                    updatePosition(startFromMinutes, range);

                } else if (clickTarget == bottomResizeBar) {
                    long oldEndTime = startDatetimeTo.getTime();
                    long newEndTime = oldEndTime
                            + ((long) halfHourInMilliSeconds * halfHourDiff);

                    if (!isTimeRangeTooSmall(startDatetimeFrom.getTime(),
                            newEndTime)) {
                        newEndTime = startDatetimeFrom.getTime()
                                + getMinTimeRange();
                    }

                    to.setTime(newEndTime);

                    calendarEvent.setEndTime(to);

                    // Set new position for the event
                    long startFromMinutes = (startDatetimeFrom.getHours() * 60)
                            + startDatetimeFrom.getMinutes();
                    long range = calendarEvent.getRangeInMinutes();

                    updatePosition(startFromMinutes, range);
                }

            }

            /**
             * Check if the given time range is too small for events
             * 
             * @param start
             * @param end
             * @return
             */
            private boolean isTimeRangeTooSmall(long start, long end) {
                return (end - start) >= getMinTimeRange();
            }

            /**
             * @return the minimum amount of ms that an event must last when
             *         resized
             */
            private long getMinTimeRange() {
                return VCalendar.MINUTEINMILLIS * 30;
            }

            /**
             * Build the string for sending resize events to server
             * 
             * @param event
             * @return
             */
            private String buildResizeString(CalendarEvent event) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(event.getIndex());
                buffer.append(",");
                buffer.append(DateUtil.formatClientSideDate(event.getStart()));
                buffer.append("-");
                buffer.append(DateUtil.formatClientSideTime(event
                        .getStartTime()));
                buffer.append(",");
                buffer.append(DateUtil.formatClientSideDate(event.getEnd()));
                buffer.append("-");
                buffer
                        .append(DateUtil.formatClientSideTime(event
                                .getEndTime()));

                return buffer.toString();
            }

            private Date getTargetDateByCurrentPosition(int left) {
                DateCell newParent = (DateCell) weekGrid.content
                        .getWidget((left / getDateCellWidth()) + 1);
                Date targetDate = newParent.getDate();
                return targetDate;
            }

            private int getDateCellWidth() {
                int count = weekGrid.content.getWidgetCount() - 1;
                int cellWidth = weekGrid.getInternalWidth() / count;
                return cellWidth;
            }

            /* Returns total width of all date cells. */
            private int getDatesWidth() {
                return weekGrid.getInternalWidth();
            }

            /**
             * @return true if the current mouse movement is resizing
             */
            private boolean clickTargetsResize() {
                return clickTarget == topResizeBar
                        || clickTarget == bottomResizeBar;
            }

            private void addGlobalResizeStyle() {
                if (clickTarget == topResizeBar) {
                    weekGrid.getCalendar().addStyleDependentName("nresize");
                } else if (clickTarget == bottomResizeBar) {
                    weekGrid.getCalendar().addStyleDependentName("sresize");
                }
            }

            private void removeGlobalResizeStyle() {
                weekGrid.getCalendar().removeStyleDependentName("nresize");
                weekGrid.getCalendar().removeStyleDependentName("sresize");
            }

            public void setCalendarEvent(CalendarEvent calendarEvent) {
                this.calendarEvent = calendarEvent;
            }

            public CalendarEvent getCalendarEvent() {
                return calendarEvent;
            }

            public void setReadOnly(boolean readOnly) {
                this.readOnly = readOnly;
            }

            public boolean isReadOnly() {
                return readOnly;
            }

        }
    }

}
