/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell.DayEvent;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VTooltip;

public class WeekGrid extends SimplePanel {

    private int width = 0;
    private int height = 0;
    private final HorizontalPanel content;
    private VCalendar calendar;
    private boolean disabled;
    private final Timebar timebar;
    private Panel wrapper;
    private boolean verticalScrollEnabled;
    private boolean horizontalScrollEnabled;
    private int[] cellHeights;
    private final int slotInMinutes = 30;
    private int dateCellBorder;
    private DateCell dateCellOfToday;
    private int[] cellWidths;
    private int firstHour;
    private int lastHour;

    public WeekGrid(VCalendar parent, boolean format24h) {
        setCalendar(parent);
        content = new HorizontalPanel();
        timebar = new Timebar(format24h);
        content.add(timebar);

        wrapper = new SimplePanel();
        wrapper.setStylePrimaryName("v-calendar-week-wrapper");
        wrapper.add(content);

        setWidget(wrapper);
    }

    private void setVerticalScroll(boolean isVerticalScrollEnabled) {
        if (isVerticalScrollEnabled && !(isVerticalScrollable())) {
            verticalScrollEnabled = true;
            horizontalScrollEnabled = false;
            wrapper.remove(content);

            final ScrollPanel scrollPanel = new ScrollPanel();
            scrollPanel.setStylePrimaryName("v-calendar-week-wrapper");
            scrollPanel.setWidget(content);

            scrollPanel.addScrollHandler(new ScrollHandler() {
                public void onScroll(ScrollEvent event) {
                    if (calendar.getScrollListener() != null) {
                        calendar.getScrollListener().scroll(
                                scrollPanel.getScrollPosition());
                    }
                }
            });

            setWidget(scrollPanel);
            wrapper = scrollPanel;

        } else if (!isVerticalScrollEnabled && (isVerticalScrollable())) {
            verticalScrollEnabled = false;
            horizontalScrollEnabled = false;
            wrapper.remove(content);

            SimplePanel simplePanel = new SimplePanel();
            simplePanel.setStylePrimaryName("v-calendar-week-wrapper");
            simplePanel.setWidget(content);

            setWidget(simplePanel);
            wrapper = simplePanel;
        }
    }

    public void setVerticalScrollPosition(int verticalScrollPosition) {
        if (isVerticalScrollable()) {
            ((ScrollPanel) wrapper).setScrollPosition(verticalScrollPosition);
        }
    }

    public int getInternalWidth() {
        return width;
    }

    public void addDate(Date d) {
        final DateCell dc = new DateCell(this, d);
        dc.setDisabled(isDisabled());
        dc.setHorizontalSized(isHorizontalScrollable() || width < 0);
        dc.setVerticalSized(isVerticalScrollable());
        content.add(dc);
    }

    /**
     * @param dateCell
     * @return get the index of the given date cell in this week, starting from
     *         0
     */
    public int getDateCellIndex(DateCell dateCell) {
        return content.getWidgetIndex(dateCell) - 1;
    }

    /**
     * @return get the slot border in pixels
     */
    public int getDateSlotBorder() {
        return ((DateCell) content.getWidget(1)).getSlotBorder();
    }

    private boolean isVerticalScrollable() {
        return verticalScrollEnabled;
    }

    private boolean isHorizontalScrollable() {
        return horizontalScrollEnabled;
    }

    public void setWidthPX(int width) {
        if (isHorizontalScrollable()) {
            updateCellWidths();

            // Otherwise the scroll wrapper is somehow too narrow = horizontal
            // scroll
            wrapper.setWidth(content.getOffsetWidth()
                    + Util.getNativeScrollbarSize() + "px");

            this.width = content.getOffsetWidth() - timebar.getOffsetWidth();

        } else {
            this.width = (width == -1) ? width : width
                    - timebar.getOffsetWidth();

            if (isVerticalScrollable() && width != -1) {
                this.width = this.width - Util.getNativeScrollbarSize();
            }
            updateCellWidths();
        }
    }

    public void setHeightPX(int intHeight) {
        height = intHeight;

        setVerticalScroll(height <= -1);

        // if not scrollable, use any height given
        if (!isVerticalScrollable() && height > 0) {

            if (BrowserInfo.get().isIE7() || BrowserInfo.get().isIE6()) {
                --height;
            }

            content.setHeight(height + "px");
            setHeight(height + "px");
            wrapper.setHeight(height + "px");
            wrapper.removeStyleDependentName("Vsized");
            updateCellHeights();
            timebar.setCellHeights(cellHeights);
            timebar.setHeightPX(height);

        } else if (isVerticalScrollable()) {
            updateCellHeights();
            wrapper.addStyleDependentName("Vsized");
            timebar.setHeightPX(height);
        }
    }

    public void clearDates() {
        while (content.getWidgetCount() > 1) {
            content.remove(1);
        }

        dateCellOfToday = null;
    }

    /**
     * @return true if this weekgrid contains a date that is today
     */
    public boolean hasToday() {
        return dateCellOfToday != null;
    }

    public void updateCellWidths() {
        if (!isHorizontalScrollable() && width != -1) {
            int count = content.getWidgetCount();
            int datesWidth = width;
            if (datesWidth > 0 && count > 1) {
                cellWidths = VCalendar
                        .distributeSize(datesWidth, count - 1, -1);

                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setWidthPX(cellWidths[i - 1]);
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
                    dc.setHorizontalSized(isHorizontalScrollable() || width < 0);
                }
            }
        }
    }

    /**
     * @return an int-array containing the widths of the cells (days)
     */
    public int[] getDateCellWidths() {
        return cellWidths;
    }

    public void updateCellHeights() {
        if (!isVerticalScrollable()) {
            int count = content.getWidgetCount();
            if (count > 1) {
                DateCell first = (DateCell) content.getWidget(1);
                dateCellBorder = first.getSlotBorder();
                cellHeights = VCalendar.distributeSize(height,
                        first.getNumberOfSlots(), -dateCellBorder);
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
                int dateHeight = (first.getOffsetHeight() / first
                        .getNumberOfSlots()) - dateCellBorder;
                cellHeights = new int[48];
                Arrays.fill(cellHeights, dateHeight);

                for (int i = 1; i < count; i++) {
                    DateCell dc = (DateCell) content.getWidget(i);
                    dc.setVerticalSized(isVerticalScrollable());
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
            if (comp >= 0
                    && comp2 < 0
                    || (comp == 0 && comp2 == 0 && VCalendar
                    .isZeroLengthMidnightEvent(e))) {
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

        int firstHourInMinutes = firstHour * 60;

        if (firstHourInMinutes > startFromMinutes) {
            startFromMinutes = 0;
        } else {
            startFromMinutes -= firstHourInMinutes;
        }

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

        // reduce possible underflow at end
        if (endOverFlowTime < 0) {
            int lastSlotHeight = cellHeights[currentSlot] + dateCellBorder;
            pixelLength += (int) (((double) lastSlotHeight / (double) slotInMinutes) * endOverFlowTime);
        }

        return pixelLength;
    }

    public int getPixelTopFor(int startFromMinutes) {
        int pixelsToTop = 0;
        int slotIndex = 0;

        int firstHourInMinutes = firstHour * 60;

        if (firstHourInMinutes > startFromMinutes) {
            startFromMinutes = 0;
        } else {
            startFromMinutes -= firstHourInMinutes;
        }

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
        DateCell previousParent = (DateCell) dayEvent.getParent();
        DateCell newParent = (DateCell) content
                .getWidget((left / getDateCellWidth()) + 1);
        CalendarEvent se = dayEvent.getCalendarEvent();
        previousParent.removeEvent(dayEvent);
        newParent.addEvent(dayEvent);
        if (!previousParent.equals(newParent)) {
            previousParent.recalculateEventWidths();
        }
        newParent.recalculateEventWidths();
        if (calendar.getEventMovedListener() != null) {
            calendar.getEventMovedListener().eventMoved(se);
        }
    }

    public void setToday(Date todayDate, Date todayTimestamp) {
        int count = content.getWidgetCount();
        if (count > 1) {
            for (int i = 1; i < count; i++) {
                DateCell dc = (DateCell) content.getWidget(i);
                if (dc.getDate().getTime() == todayDate.getTime()) {
                    if (isVerticalScrollable()) {
                        dc.setToday(todayTimestamp, -1);
                    } else {
                        dc.setToday(todayTimestamp, getOffsetWidth());
                    }
                }
                dateCellOfToday = dc;
            }
        }
    }

    public DateCell getDateCellOfToday() {
        return dateCellOfToday;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
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

    /**
     * Get width of the single date cell
     * 
     * @return Date cell width
     */
    public int getDateCellWidth() {
        int count = content.getWidgetCount() - 1;
        int cellWidth = -1;
        if (count <= 0) {
            return cellWidth;
        }

        if (width == -1) {
            Widget firstWidget = content.getWidget(1);
            cellWidth = firstWidget.getElement().getOffsetWidth();
        } else {
            cellWidth = getInternalWidth() / count;
        }
        return cellWidth;
    }

    /**
     * @return the number of day cells in this week
     */
    public int getDateCellCount() {
        return content.getWidgetCount() - 1;
    }

    public void setFirstHour(int firstHour) {
        this.firstHour = firstHour;
        timebar.setFirstHour(firstHour);
    }

    public void setLastHour(int lastHour) {
        this.lastHour = lastHour;
        timebar.setLastHour(lastHour);
    }

    public int getFirstHour() {
        return firstHour;
    }

    public int getLastHour() {
        return lastHour;
    }

    public static class Timebar extends HTML {

        private static final int[] timesFor12h = { 12, 1, 2, 3, 4, 5, 6, 7, 8,
            9, 10, 11 };

        private int height;

        private final int verticalPadding = 7; // FIXME measure this from DOM

        private int[] slotCellHeights;

        private int firstHour;

        private int lastHour;

        public Timebar(boolean format24h) {
            createTimeBar(format24h);
        }

        public void setLastHour(int lastHour) {
            this.lastHour = lastHour;
        }

        public void setFirstHour(int firstHour) {
            this.firstHour = firstHour;

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
                for (int i = firstHour + 1; i <= lastHour; i++) {
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

                int amStop = (lastHour < 11) ? lastHour : 11;
                int pmStart = (firstHour > 11) ? firstHour % 11 : 0;

                if (firstHour < 12) {
                    for (int i = firstHour + 1; i <= amStop; i++) {
                        e = DOM.createDiv();
                        setStyleName(e, "v-calendar-time");
                        e.setInnerHTML("<span>" + timesFor12h[i] + "</span>"
                                + " " + ampm[0]);
                        getElement().appendChild(e);
                    }
                }

                if (lastHour > 11) {
                    for (int i = pmStart; i < lastHour - 11; i++) {
                        e = DOM.createDiv();
                        setStyleName(e, "v-calendar-time");
                        e.setInnerHTML("<span>" + timesFor12h[i] + "</span>"
                                + " " + ampm[1]);
                        getElement().appendChild(e);
                    }
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
                removeStyleDependentName("Vsized");
                updateChildHeights();

            } else {
                addStyleDependentName("Vsized");
                updateChildHeights();
            }
        }

        private void updateChildHeights() {
            int childCount = getElement().getChildCount();

            if (height != -1) {

                // 23 hours + first is empty
                // we try to adjust the height of time labels to the distributed
                // heights of the time slots
                int hoursPerDay = lastHour - firstHour + 1;

                int slotsPerHour = slotCellHeights.length / hoursPerDay;
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

    public static class DateCell extends FocusableComplexPanel implements
    MouseDownHandler, MouseMoveHandler, MouseUpHandler, KeyDownHandler,
    ContextMenuHandler {
        private static final String DRAGEMPHASISSTYLE = " dragemphasis";
        private Date date;
        private int width;
        private int eventRangeStart = -1;
        private int eventRangeStop = -1;
        private final WeekGrid weekgrid;
        private boolean disabled = false;
        private int height;
        private final Element[] slotElements;
        private final List<DateCellSlot> slots = new ArrayList<WeekGrid.DateCell.DateCellSlot>();
        private int[] slotElementHeights;
        private int startingSlotHeight;
        private Date today;
        private Element todaybar;
        private final List<HandlerRegistration> handlers;
        private final int numberOfSlots;
        private final int firstHour;
        private final int lastHour;

        public class DateCellSlot extends Widget {

            private final DateCell cell;

            private final Date from;

            private final Date to;

            public DateCellSlot(DateCell cell, Date from, Date to) {
                setElement(DOM.createDiv());
                getElement().setInnerHTML("&nbsp;");
                this.cell = cell;
                this.from = from;
                this.to = to;
            }

            public Date getFrom() {
                return from;
            }

            public Date getTo() {
                return to;
            }

            public DateCell getParentCell() {
                return cell;
            }
        }

        public DateCell(WeekGrid parent, Date date) {
            weekgrid = parent;
            Element mainElement = DOM.createDiv();
            setElement(mainElement);
            makeFocusable();
            setDate(date);

            addStyleName("v-calendar-day-times");

            handlers = new LinkedList<HandlerRegistration>();

            // 2 slots / hour
            firstHour = weekgrid.getFirstHour();
            lastHour = weekgrid.getLastHour();
            numberOfSlots = (lastHour - firstHour + 1) * 2;
            long slotTime = Math.round(((lastHour - firstHour + 1) * 3600000.0)
                    / numberOfSlots);

            slotElements = new Element[numberOfSlots];
            slotElementHeights = new int[numberOfSlots];

            slots.clear();
            long start = getDate().getTime() + firstHour * 3600000;
            long end = start + slotTime;
            for (int i = 0; i < numberOfSlots; i++) {
                DateCellSlot slot = new DateCellSlot(DateCell.this, new Date(
                        start), new Date(end));
                if (i % 2 == 0) {
                    slot.setStyleName("v-datecellslot-even");
                } else {
                    slot.setStyleName("v-datecellslot");
                }
                Event.sinkEvents(slot.getElement(), Event.MOUSEEVENTS);
                mainElement.appendChild(slot.getElement());
                slotElements[i] = slot.getElement();
                slots.add(slot);
                start = end;
                end = start + slotTime;
            }

            // Sink events for tooltip handling
            Event.sinkEvents(mainElement, Event.MOUSEEVENTS);
        }

        public int getFirstHour() {
            return firstHour;
        }

        public int getLastHour() {
            return lastHour;
        }

        @Override
        protected void onAttach() {
            super.onAttach();

            handlers.add(addHandler(this, MouseDownEvent.getType()));
            handlers.add(addHandler(this, MouseUpEvent.getType()));
            handlers.add(addHandler(this, MouseMoveEvent.getType()));
            handlers.add(addDomHandler(this, ContextMenuEvent.getType()));
            handlers.add(addKeyDownHandler(this));
        }

        @Override
        protected void onDetach() {
            for (HandlerRegistration handler : handlers) {
                handler.removeHandler();
            }
            handlers.clear();

            super.onDetach();
        }

        public int getSlotIndex(Element slotElement) {
            for (int i = 0; i < slotElements.length; i++) {
                if (slotElement == slotElements[i]) {
                    return i;
                }
            }

            throw new IllegalArgumentException(
                    "Element not found in this DateCell");
        }

        public DateCellSlot getSlot(int index) {
            return slots.get(index);
        }

        public int getNumberOfSlots() {
            return numberOfSlots;
        }

        public void setTimeBarWidth(int timebarWidth) {
            todaybar.getStyle().setWidth(timebarWidth, Unit.PX);
        }

        /**
         * @param isHorizontalSized
         *            if true, this DateCell is sized with CSS and not via
         *            {@link #setWidthPX(int)}
         */
        public void setHorizontalSized(boolean isHorizontalSized) {
            if (isHorizontalSized) {
                addStyleDependentName("Hsized");

                width = getOffsetWidth()
                        - Util.measureHorizontalBorder(getElement());
                recalculateEventWidths();
            } else {
                removeStyleDependentName("Hsized");
            }
        }

        /**
         * @param isVerticalSized
         *            if true, this DateCell is sized with CSS and not via
         *            {@link #setHeightPX(int)}
         */
        public void setVerticalSized(boolean isVerticalSized) {
            if (isVerticalSized) {
                addStyleDependentName("Vsized");

                // recalc heights&size for events. all other height sizes come
                // from css
                startingSlotHeight = slotElements[0].getOffsetHeight();
                recalculateEventPositions();

                if (isToday()) {
                    recalculateTimeBarPosition();
                }

            } else {
                removeStyleDependentName("Vsized");
            }
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

        // date methods are not deprecated in GWT
        @SuppressWarnings("deprecation")
        private void recalculateTimeBarPosition() {
            int h = today.getHours();
            int m = today.getMinutes();
            if (h >= firstHour && h <= lastHour) {
                int pixelTop = weekgrid.getPixelTopFor(m + 60 * h);
                todaybar.getStyle().clearDisplay();
                todaybar.getStyle().setTop(pixelTop, Unit.PX);
            } else {
                todaybar.getStyle().setDisplay(Display.NONE);
            }
        }

        private void recalculateEventPositions() {
            for (int i = 0; i < getWidgetCount(); i++) {
                DayEvent dayEvent = (DayEvent) getWidget(i);
                updatePositionFor(dayEvent, getDate(),
                        dayEvent.getCalendarEvent());
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
            startingSlotHeight = height / numberOfSlots;

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

                    int freeSpaceCol = findFreeColumnSpaceOnLeft(d.getTop(),
                            d.getTop() + d.getOffsetHeight() - 1, order,
                            columns);
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
                    d.getElement()
                    .getStyle()
                    .setMarginLeft((eventWidth * columns.get(index)),
                            Unit.PX);
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

                if (doOverlap(top, bottom, d.getTop(),
                        d.getTop() + d.getOffsetHeight() - 1)) {
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
            dayEvent.setDisabled(isDisabled());

            if (startingSlotHeight > 0) {
                updatePositionFor(dayEvent, targetDay, calendarEvent);
            }

            add(dayEvent, (com.google.gwt.user.client.Element) main);
        }

        // date methods are not deprecated in GWT
        @SuppressWarnings("deprecation")
        private void updatePositionFor(DayEvent dayEvent, Date targetDay,
                CalendarEvent calendarEvent) {
            if (canDisplay(calendarEvent)) {

                dayEvent.getElement().getStyle().clearDisplay();

                Date fromDt = calendarEvent.getStartTime();
                int h = fromDt.getHours();
                int m = fromDt.getMinutes();
                long range = calendarEvent.getRangeInMinutesForDay(targetDay);

                boolean onDifferentDays = calendarEvent.isTimeOnDifferentDays();
                if (onDifferentDays) {
                    if (calendarEvent.getStart().compareTo(targetDay) != 0) {
                        // Current day slot is for the end date. Lets fix also
                        // the
                        // start & end times.
                        h = 0;
                        m = 0;
                    }
                }

                int startFromMinutes = (h * 60) + m;
                dayEvent.updatePosition(startFromMinutes, range);

            } else {
                dayEvent.getElement().getStyle().setDisplay(Display.NONE);
            }
        }

        public void addEvent(DayEvent dayEvent) {
            Element main = getElement();
            int index = 0;
            List<CalendarEvent> events = new ArrayList<CalendarEvent>();

            // events are the only widgets in this panel
            // slots are just elements
            for (; index < getWidgetCount(); index++) {
                DayEvent dc = (DayEvent) getWidget(index);
                dc.setDisabled(isDisabled());
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

        /**
         * 
         * @param event
         * @return
         */
        // Date methods not deprecated in GWT
        @SuppressWarnings("deprecation")
        private boolean canDisplay(CalendarEvent event) {
            Date eventStart = event.getStartTime();
            Date eventEnd = event.getEndTime();

            int eventStartHours = eventStart.getHours();
            int eventEndHours = eventEnd.getHours();

            return (eventStartHours <= lastHour)
                    && (eventEndHours >= firstHour);
        }

        public void onKeyDown(KeyDownEvent event) {
            int keycode = event.getNativeEvent().getKeyCode();
            if (keycode == KeyCodes.KEY_ESCAPE && eventRangeStart > -1) {
                cancelRangeSelect();
            }
        }

        public void onMouseDown(MouseDownEvent event) {
            if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
                Element e = Element.as(event.getNativeEvent().getEventTarget());
                if (e.getClassName().contains("reserved") || isDisabled()
                        || !weekgrid.getParentCalendar().isRangeSelectAllowed()) {
                    eventRangeStart = -1;
                } else {
                    eventRangeStart = event.getY();
                    eventRangeStop = eventRangeStart;
                    Event.setCapture(getElement());
                    setFocus(true);
                }
            }
        }

        @SuppressWarnings("deprecation")
        public void onMouseUp(MouseUpEvent event) {
            if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                return;
            }
            Event.releaseCapture(getElement());
            setFocus(false);
            int dragDistance = Math.abs(eventRangeStart - event.getY());
            if (dragDistance > 0 && eventRangeStart >= 0) {
                Element main = getElement();
                if (eventRangeStart > eventRangeStop) {
                    if (eventRangeStop <= -1) {
                        eventRangeStop = 0;
                    }
                    int temp = eventRangeStart;
                    eventRangeStart = eventRangeStop;
                    eventRangeStop = temp;
                }

                NodeList<Node> nodes = main.getChildNodes();

                int slotStart = -1;
                int slotEnd = -1;

                // iterate over all child nodes, until we find first the start,
                // and then the end
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.getItem(i);
                    boolean isRangeElement = element.getClassName().contains(
                            "v-daterange");

                    if (isRangeElement && slotStart == -1) {
                        slotStart = i;
                        slotEnd = i; // to catch one-slot selections

                    } else if (isRangeElement) {
                        slotEnd = i;

                    } else if (slotStart != -1 && slotEnd != -1) {
                        break;
                    }
                }

                GWT.log("Slot start " + slotStart + " slot end " + slotEnd);

                clearSelectionRange();

                int startMinutes = firstHour * 60 + slotStart * 30;
                int endMinutes = (firstHour * 60) + (slotEnd + 1) * 30;
                Date currentDate = getDate();
                String yr = (currentDate.getYear() + 1900) + "-"
                        + (currentDate.getMonth() + 1) + "-"
                        + currentDate.getDate();
                if (weekgrid.getCalendar().getRangeSelectListener() != null) {
                    weekgrid.getCalendar()
                    .getRangeSelectListener()
                    .rangeSelected(
                            yr + ":" + startMinutes + ":" + endMinutes);
                }
                eventRangeStart = -1;
            } else {
                // Click event
                eventRangeStart = -1;
                cancelRangeSelect();

            }
        }

        public void onMouseMove(MouseMoveEvent event) {
            if (event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                return;
            }

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

                    if (todaybar != c) {

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

            event.preventDefault();
        }

        public void cancelRangeSelect() {
            Event.releaseCapture(getElement());
            setFocus(false);

            clearSelectionRange();
        }

        private void clearSelectionRange() {
            if (eventRangeStart > -1) {
                // clear all "selected" class names
                Element main = getElement();
                NodeList<Node> nodes = main.getChildNodes();

                for (int i = 0; i <= 47; i++) {
                    Element c = (Element) nodes.getItem(i);
                    if (c == null) {
                        continue;
                    }
                    c.removeClassName("v-daterange");
                }

                eventRangeStart = -1;
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

        public Element getTodaybarElement() {
            return todaybar;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public boolean isDisabled() {
            return disabled;
        }

        public void setDateColor(String styleName) {
            this.setStyleName("v-calendar-datecell " + styleName);
        }

        public boolean isToday() {
            return today != null;
        }

        public void addEmphasisStyle(
                com.google.gwt.user.client.Element elementOver) {
            String originalStylename = getStyleName(elementOver);
            setStyleName(elementOver, originalStylename + DRAGEMPHASISSTYLE);
        }

        public void removeEmphasisStyle(
                com.google.gwt.user.client.Element elementOver) {
            String originalStylename = getStyleName(elementOver);
            setStyleName(
                    elementOver,
                    originalStylename.substring(0, originalStylename.length()
                            - DRAGEMPHASISSTYLE.length()));
        }

        private static class Group {
            public int top;
            public int bottom;
            private final List<Integer> items;

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

        public class DayEvent extends FocusableHTML implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler,
        KeyDownHandler, ContextMenuHandler {

            private Element caption = null;
            private final Element eventContent;
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
            private boolean disabled;
            private final WeekGrid weekGrid;
            private com.google.gwt.user.client.Element topResizeBar;
            private com.google.gwt.user.client.Element bottomResizeBar;
            private Element clickTarget;
            private final Integer eventIndex;
            private int slotHeight;
            private final List<HandlerRegistration> handlers;
            private boolean mouseMoveCanceled;

            public DayEvent(WeekGrid parent, CalendarEvent event) {
                super();

                handlers = new LinkedList<HandlerRegistration>();

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
                if (weekGrid.getCalendar().isEventResizeAllowed()) {
                    topResizeBar = DOM.createDiv();
                    bottomResizeBar = DOM.createDiv();

                    topResizeBar.addClassName("v-calendar-event-resizetop");
                    bottomResizeBar
                    .addClassName("v-calendar-event-resizebottom");

                    getElement().appendChild(topResizeBar);
                    getElement().appendChild(bottomResizeBar);
                }

                sinkEvents(VTooltip.TOOLTIP_EVENTS);
                eventIndex = event.getIndex();
            }

            @Override
            protected void onAttach() {
                super.onAttach();
                handlers.add(addMouseDownHandler(this));
                handlers.add(addMouseUpHandler(this));
                handlers.add(addKeyDownHandler(this));
                handlers.add(addDomHandler(this, ContextMenuEvent.getType()));
            }

            @Override
            protected void onDetach() {
                for (HandlerRegistration handler : handlers) {
                    handler.removeHandler();
                }
                handlers.clear();
                super.onDetach();
            }

            public void setSlotHeightInPX(int slotHeight) {
                this.slotHeight = slotHeight;
            }

            @Override
            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);
                VCalendar calendar = weekGrid.getCalendar();
                calendar.handleTooltipEvent(event, eventIndex);
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

            public void onKeyDown(KeyDownEvent event) {
                int keycode = event.getNativeEvent().getKeyCode();
                if (keycode == KeyCodes.KEY_ESCAPE && mouseMoveStarted) {
                    cancelMouseMove();
                }
            }

            public void onMouseDown(MouseDownEvent event) {
                if (isDisabled()
                        || event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
                    return;
                }

                clickTarget = Element.as(event.getNativeEvent()
                        .getEventTarget());
                mouseMoveCanceled = false;

                if (weekGrid.getCalendar().isEventMoveAllowed()
                        || clickTargetsResize()) {
                    moveRegistration = addMouseMoveHandler(this);
                    setFocus(true);
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
                        GWT.log("Exception calculating relative start position",
                                e);
                    }
                    mouseMoveStarted = false;
                    Style s = getElement().getStyle();
                    s.setZIndex(1000);
                    startDatetimeFrom = (Date) calendarEvent.getStartTime()
                            .clone();
                    startDatetimeTo = (Date) calendarEvent.getEndTime().clone();
                    Event.setCapture(getElement());
                }

                // make sure the right cursor is always displayed
                if (clickTargetsResize()) {
                    addGlobalResizeStyle();
                }

                /*
                 * We need to stop the event propagation or else the WeekGrid
                 * range select will kick in
                 */
                event.stopPropagation();
                event.preventDefault();
            }

            public void onMouseUp(MouseUpEvent event) {
                if (mouseMoveCanceled) {
                    return;
                }

                Event.releaseCapture(getElement());
                setFocus(false);
                if (moveRegistration != null) {
                    moveRegistration.removeHandler();
                    moveRegistration = null;
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
                    // check if mouse has moved over threshold of 3 pixels
                    boolean mouseMoved = (xDiff < -3 || xDiff > 3 || yDiff < -3 || yDiff > 3);

                    if (!weekGrid.getCalendar().isDisabledOrReadOnly()
                            && mouseMoved) {
                        // Event Move:
                        // - calendar must be enabled
                        // - calendar must not be in read-only mode
                        weekGrid.eventMoved(this);
                    } else if (!weekGrid.getCalendar().isDisabled()) {
                        // Event Click:
                        // - calendar must be enabled (read-only is allowed)
                        EventTarget et = event.getNativeEvent()
                                .getEventTarget();
                        Element e = Element.as(et);
                        if (e == caption || e == eventContent
                                || e.getParentElement() == caption) {
                            if (weekGrid.getCalendar().getEventClickListener() != null) {
                                weekGrid.getCalendar().getEventClickListener()
                                .eventClick(
                                        calendarEvent);
                            }
                        }
                    }

                } else { // click targeted resize bar
                    removeGlobalResizeStyle();
                    if (weekGrid.getCalendar().getEventResizeListener() != null) {
                        weekGrid.getCalendar().getEventResizeListener()
                        .eventResized(
                                calendarEvent);
                    }
                }
            }

            @SuppressWarnings("deprecation")
            public void onMouseMove(MouseMoveEvent event) {
                if (startY < 0 && startX < 0) {
                    return;
                }
                if (isDisabled()) {
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

                // sanity check for right side overflow
                int dateCellCount = weekGrid.getDateCellCount();
                if (dayOffset >= dateCellCount) {
                    dayOffset--;
                    dayDiff--;
                }

                int dayOffsetPx = calculateDateCellOffsetPx(dayOffset)
                        + weekGrid.timebar.getOffsetWidth();

                GWT.log("DateCellWidth: " + dateCellWidth + " dayDiff: "
                        + dayDiff + " dayOffset: " + dayOffset
                        + " dayOffsetPx: " + dayOffsetPx + " startXrelative: "
                        + startXrelative + " moveX: " + moveX);

                if (relativeX < 0 || relativeX >= getDatesWidth()) {
                    return;
                }

                Style s = getElement().getStyle();

                Date from = calendarEvent.getStartTime();
                Date to = calendarEvent.getEndTime();
                long duration = to.getTime() - from.getTime();

                if (!clickTargetsResize()
                        && weekGrid.getCalendar().isEventMoveAllowed()) {
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
                    startFromMinutes = calculateStartFromMinute(
                            startFromMinutes, from, to, dayOffsetPx);
                    if (startFromMinutes < 0) {
                        range += startFromMinutes;
                    }
                    updatePosition(startFromMinutes, range);

                    s.setLeft(dayOffsetPx, Unit.PX);

                    if (weekGrid.getDateCellWidths() != null) {
                        s.setWidth(weekGrid.getDateCellWidths()[dayOffset],
                                Unit.PX);
                    } else {
                        setWidth(moveWidth);
                    }

                } else if (clickTarget == topResizeBar) {
                    long oldStartTime = startDatetimeFrom.getTime();
                    long newStartTime = oldStartTime
                            + ((long) halfHourInMilliSeconds * halfHourDiff);

                    if (!isTimeRangeTooSmall(newStartTime,
                            startDatetimeTo.getTime())) {
                        newStartTime = startDatetimeTo.getTime()
                                - getMinTimeRange();
                    }

                    from.setTime(newStartTime);

                    calendarEvent.setStartTime(from);
                    calendarEvent.setStart(new Date(from.getTime()));

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
                    calendarEvent.setEnd(new Date(to.getTime()));

                    // Set new position for the event
                    long startFromMinutes = (startDatetimeFrom.getHours() * 60)
                            + startDatetimeFrom.getMinutes();
                    long range = calendarEvent.getRangeInMinutes();
                    startFromMinutes = calculateStartFromMinute(
                            startFromMinutes, from, to, dayOffsetPx);
                    if (startFromMinutes < 0) {
                        range += startFromMinutes;
                    }
                    updatePosition(startFromMinutes, range);
                }
            }

            private void cancelMouseMove() {
                mouseMoveCanceled = true;

                // reset and remove everything related to the event handling
                Event.releaseCapture(getElement());
                setFocus(false);

                if (moveRegistration != null) {
                    moveRegistration.removeHandler();
                    moveRegistration = null;
                }

                mouseMoveStarted = false;
                removeGlobalResizeStyle();

                Style s = getElement().getStyle();
                s.setZIndex(1);

                // reset the position of the event
                int dateCellWidth = getDateCellWidth();
                int dayOffset = startXrelative / dateCellWidth;
                s.clearLeft();

                calendarEvent.setStartTime(startDatetimeFrom);
                calendarEvent.setEndTime(startDatetimeTo);

                long startFromMinutes = (startDatetimeFrom.getHours() * 60)
                        + startDatetimeFrom.getMinutes();
                long range = calendarEvent.getRangeInMinutes();

                startFromMinutes = calculateStartFromMinute(startFromMinutes,
                        startDatetimeFrom, startDatetimeTo, dayOffset);
                if (startFromMinutes < 0) {
                    range += startFromMinutes;
                }

                updatePosition(startFromMinutes, range);

                startY = -1;
                startX = -1;

                // to reset the event width
                ((DateCell) getParent()).recalculateEventWidths();
            }

            // date methods are not deprecated in GWT
            @SuppressWarnings("deprecation")
            private long calculateStartFromMinute(long startFromMinutes,
                    Date from, Date to, int dayOffset) {
                boolean eventStartAtDifferentDay = from.getDate() != to
                        .getDate();
                if (eventStartAtDifferentDay) {
                    long minutesOnPrevDay = (getTargetDateByCurrentPosition(
                            dayOffset).getTime() - from.getTime())
                            / VCalendar.MINUTEINMILLIS;
                    startFromMinutes = -1 * minutesOnPrevDay;
                }

                return startFromMinutes;
            }

            /**
             * @param dateOffset
             * @return the amount of pixels the given date is from the left side
             */
            private int calculateDateCellOffsetPx(int dateOffset) {
                int dateCellOffset = 0;
                int[] dateWidths = weekGrid.getDateCellWidths();

                if (dateWidths != null) {
                    for (int i = 0; i < dateOffset; i++) {
                        dateCellOffset += dateWidths[i] + 1;
                    }
                } else {
                    dateCellOffset = dateOffset * weekGrid.getDateCellWidth();
                }

                return dateCellOffset;
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
                buffer.append(DateUtil.formatClientSideTime(event.getEndTime()));

                return buffer.toString();
            }

            private Date getTargetDateByCurrentPosition(int left) {
                DateCell newParent = (DateCell) weekGrid.content
                        .getWidget((left / getDateCellWidth()) + 1);
                Date targetDate = newParent.getDate();
                return targetDate;
            }

            private int getDateCellWidth() {
                return weekGrid.getDateCellWidth();
            }

            /* Returns total width of all date cells. */
            private int getDatesWidth() {
                if (weekGrid.width == -1) {
                    // Undefined width. Needs to be calculated by the known cell
                    // widths.
                    int count = weekGrid.content.getWidgetCount() - 1;
                    return count * getDateCellWidth();
                }

                return weekGrid.getInternalWidth();
            }

            /**
             * @return true if the current mouse movement is resizing
             */
            private boolean clickTargetsResize() {
                return weekGrid.getCalendar().isEventResizeAllowed()
                        && (clickTarget == topResizeBar || clickTarget == bottomResizeBar);
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

            public void setDisabled(boolean disabled) {
                this.disabled = disabled;
            }

            public boolean isDisabled() {
                return disabled;
            }

            public void onContextMenu(ContextMenuEvent event) {
                if (weekgrid.getCalendar().getMouseEventListener() != null) {
                    event.preventDefault();
                    event.stopPropagation();
                    weekgrid.getCalendar().getMouseEventListener()
                    .contextMenu(event, DayEvent.this);
                }
            }
        }

        public void onContextMenu(ContextMenuEvent event) {
            if (weekgrid.getCalendar().getMouseEventListener() != null) {
                event.preventDefault();
                event.stopPropagation();
                weekgrid.getCalendar().getMouseEventListener()
                .contextMenu(event, DateCell.this);
            }
        }
    }

    public VCalendar getParentCalendar() {
        return calendar;
    }
}
