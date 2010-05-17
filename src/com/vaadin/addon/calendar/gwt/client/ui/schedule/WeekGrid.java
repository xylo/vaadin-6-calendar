package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell.DayEvent;

public class WeekGrid extends ScrollPanel implements NativePreviewHandler {

    private int width = 0;
    private int height = 0;
    private HorizontalPanel content;
    private VCalendar schedule;
    private boolean readOnly;
    private boolean format24h;
    private Timebar timebar;

    public WeekGrid(VCalendar parent, boolean format24h) {
        this.schedule = parent;
        this.format24h = format24h;
        content = new HorizontalPanel();
        addStyleName("v-calendar-wgwrapper");
        setWidget(content);
        timebar = new Timebar(format24h);
        content.add(timebar);
        addScrollHandler(new ScrollHandler() {

            public void onScroll(ScrollEvent event) {
                schedule.getClient().updateVariable(schedule.getPID(),
                        "scroll", getScrollPosition(), false);
            }
        });
        Event.addNativePreviewHandler(this);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
    }

    public void addDate(Date d) {
        DateCell dc = new DateCell(this);
        dc.setDate(d);
        dc.setReadOnly(readOnly);
        content.add(dc);
    }

    public void setWidthPX(int width) {
        this.width = width;
        updateCellWidths();
    }

    public void setHeightPX(int intHeight) {
        this.height = intHeight;
        if (height > 0) {
            setHeight(height - 43 + "px");
        }
    }

    public void clearDates() {
        while (content.getWidgetCount() > 1) {
            content.remove(1);
        }
    }

    public void updateCellWidths() {
        int count = content.getWidgetCount();
        int datesWidth = width - 67;
        if (datesWidth > 0 && count > 1) {
            int cellWidth = datesWidth / (count - 1);
            int cellWidthMinusBorder = cellWidth - 1;
            for (int i = 1; i < count; i++) {
                DateCell dc = (DateCell) content.getWidget(i);
                dc.setPositionLeft(((i - 1) * cellWidth) + 50);
                dc.setWidthPX(cellWidthMinusBorder);
            }
        }
    }

    public static class Timebar extends HTML {

        public Timebar(boolean format24h) {
            createTimeBar(format24h);
        }

        private void createTimeBar(boolean format24h) {
            setStylePrimaryName("v-calendar-timebar");
            setWidth("49px");
            if (format24h) {
                for (int i = 0; i < 24; i++) {
                    Element e = DOM.createDiv();
                    setStyleName(e, "minutes");
                    e.setInnerText("00");
                    getElement().appendChild(e);
                    e = DOM.createDiv();
                    setStyleName(e, "hour");
                    e.setInnerText(i + "");
                    getElement().appendChild(e);
                }
            } else {
                Element e = DOM.createDiv();
                setStyleName(e, "hour");
                e.setInnerText("12 am");
                getElement().appendChild(e);
                for (int i = 1; i < 13; i++) {
                    e = DOM.createDiv();
                    setStyleName(e, "hour");
                    e.setInnerText(i + " am");
                    getElement().appendChild(e);
                }
                for (int i = 1; i < 12; i++) {
                    e = DOM.createDiv();
                    setStyleName(e, "hour");
                    e.setInnerText(i + " pm");
                    getElement().appendChild(e);
                }
            }
        }

        public void updateTimeBar(boolean format24h) {
            clear();
            createTimeBar(format24h);
        }

        private void clear() {
            while (getElement().getChildCount() > 0)
                getElement().removeChild(getElement().getChild(0));
        }
    }

    public static class DateCell extends ComplexPanel implements
            MouseDownHandler, MouseMoveHandler, MouseUpHandler {
        private Date date;
        private int width;
        private int eventRangeStart = -1;
        private int eventRangeStop = -1;
        private WeekGrid weekgrid;
        private int positionLeft;
        private boolean readOnly = false;

        public DateCell(WeekGrid parent) {
            this.weekgrid = parent;
            Element mainElement = DOM.createDiv();
            setElement(mainElement);
            addStyleName("v-calendar-datecell");
            addHandler(this, MouseDownEvent.getType());
            addHandler(this, MouseUpEvent.getType());
            addHandler(this, MouseMoveEvent.getType());

            for (int i = 0; i < 48; i++) {
                Element e = DOM.createDiv();
                if (i % 2 == 0) {
                    setStyleName(e, "halfhour-even");
                } else {
                    setStyleName(e, "halfhour");
                }
                e.setInnerText("  ");
                Event.sinkEvents(e, Event.MOUSEEVENTS);
                mainElement.appendChild(e);
            }
            Event.sinkEvents(mainElement, Event.MOUSEEVENTS);
        }

        public void setPositionLeft(int i) {
            this.positionLeft = i;
        }

        public int getPositionLeft() {
            return this.positionLeft;
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);

        }

        @Override
        protected void onUnload() {
            super.onUnload();
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public void setWidthPX(int cellWidth) {
            this.width = cellWidth;
            setWidth(cellWidth + "px");
            recalculateEventWidths();
        }

        public void recalculateEventWidths() {
            List<Group> groups = new ArrayList<Group>();

            int count = getWidgetCount();

            List<Integer> handled = new ArrayList<Integer>();

            // Iterate through all events and group them. Events that overlaps
            // with each other, are added to the same group.
            for (int i = 0; i < count; i++) {
                if (handled.contains(i))
                    continue;

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
                    if (newGroup)
                        groups.add(curGroup);
                    break;
                }

                if (newGroup)
                    groups.add(curGroup);
            }

            drawDayEvents(groups);
        }

        private void drawDayEvents(List<Group> groups) {
            for (Group g : groups) {
                int eventWidth = ((width) / g.getItems().size());
                int i = 0;
                for (Integer index : g.getItems()) {
                    DayEvent d = (DayEvent) getWidget(index);
                    d.setWidth(eventWidth - 4 + "px");
                    d.setMoveWidth(width);
                    d.getElement().getStyle().setLeft(
                            (eventWidth * i) + positionLeft, Unit.PX);
                    i++;
                }
            }
        }

        private boolean doOverlap(int top, int bottom, int nextTop,
                int nextBottom) {
            boolean isInsideFromBottomSide = top >= nextTop
                    && top <= nextBottom;
            boolean isFullyInside = top >= nextTop && bottom <= nextBottom;
            boolean isInsideFromTopSide = bottom <= nextBottom
                    && bottom >= nextTop;
            boolean isFullyOverlapping = top < nextTop && bottom > nextBottom;
            return isInsideFromBottomSide || isFullyInside
                    || isInsideFromTopSide || isFullyOverlapping;
        }

        /* Update top and bottom values. Add new index to the group. */
        private void updateGroup(Group targetGroup, Group byGroup, int top,
                int bottom) {
            if (top < targetGroup.top)
                targetGroup.top = top;
            if (bottom > targetGroup.bottom)
                targetGroup.bottom = bottom;

            for (Integer index : byGroup.getItems()) {
                if (!targetGroup.getItems().contains(index))
                    targetGroup.add(index);
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
            int bottom = top + target.getOffsetHeight();

            for (int i = 0; i < count; i++) {
                if (targetIndex == i)
                    continue;

                DayEvent d = (DayEvent) getWidget(i);
                int nextTop = d.getTop();
                int nextBottom = nextTop + d.getOffsetHeight();
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

        @SuppressWarnings("deprecation")
        public void addEvent(Date targetDay, CalendarEvent e) {
            Element main = getElement();
            DayEvent de = new DayEvent(e);
            de.setReadOnly(readOnly);
            Date fromDt = e.getStartTime();
            int h = fromDt.getHours();
            int m = fromDt.getMinutes();
            long range = e.getRangeInMinutesForDay(targetDay);

            boolean onDifferentDays = e.isTimeOnDifferentDays();
            if (onDifferentDays) {
                if (e.getEnd().compareTo(targetDay) == 0) {
                    // Current day slot is for the end date. Lets fix also the
                    // start & end times.
                    h = 0;
                    m = 0;
                }
            }

            int startFromMinutes = (h * 60) + m;
            de.updatePosition(startFromMinutes, range);
            add(de, (com.google.gwt.user.client.Element) main);
        }

        public void addEvent(DayEvent dayEvent) {
            Element main = getElement();
            int index = 0;
            for (; index < getWidgetCount(); index++) {
                DayEvent dc = (DayEvent) getWidget(index);
                dc.setReadOnly(readOnly);
                if (dc.calendarEvent.getStartTime().getTime() > dayEvent.calendarEvent
                        .getStartTime().getTime()) {
                    break;
                }
            }
            this.insert(dayEvent, (com.google.gwt.user.client.Element) main,
                    index, true);
        }

        public void removeEvent(DayEvent dayEvent) {
            remove(dayEvent);
            recalculateEventWidths();
        }

        public void onMouseDown(MouseDownEvent event) {
            Element e = Element.as(event.getNativeEvent().getEventTarget());
            if (e.getClassName().contains("reserved") || readOnly) {
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
                    int temp = eventRangeStart;
                    eventRangeStart = eventRangeStop;
                    eventRangeStop = temp;
                }
                boolean reservedFound = false;
                NodeList<Node> nodes = main.getChildNodes();
                int slot = (eventRangeStart - (eventRangeStart % 19)) / 19;
                int slotEnd = (eventRangeStop - (eventRangeStop % 19)) / 19;
                int slotEndBeforeReserved = slotEnd;
                for (int i = slot; i <= slotEnd; i++) {
                    Element c = (Element) nodes.getItem(i);
                    c.removeClassName("daterange");
                    if (!reservedFound && c.getClassName().contains("reserved")) {
                        reservedFound = true;
                        slotEndBeforeReserved = i - 1;
                    }
                }

                int startMinutes = slot * 30;
                int endMinutes = (slotEndBeforeReserved + 1) * 30;
                VCalendar schedule = (VCalendar) this.weekgrid.getParent()
                        .getParent();
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
                    int elemStop = elemStart + DayEvent.HALFHOUR_IN_PX;
                    if (elemStart >= fromY && elemStart <= toY) {
                        c.addClassName("daterange");
                    } else if (elemStop >= fromY && elemStop <= toY) {
                        c.addClassName("daterange");
                    } else if (elemStop >= fromY && elemStart <= toY) {
                        c.addClassName("daterange");
                    } else {
                        c.removeClassName("daterange");
                    }
                }
            }
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
                this.items.add(index);
            }
        }

        public static class DayEvent extends HTML implements MouseDownHandler,
                MouseUpHandler, MouseMoveHandler {
            private Element caption = null;
            private Element dayEvent = null;
            private Element eventContent;
            private CalendarEvent calendarEvent = null;
            private HandlerRegistration moveRegistration;
            private int startY = -1;
            private int startX = -1;
            private String moveWidth;
            public static final int halfHourInMilliSeconds = 1800 * 1000;
            public static final int HOUR_IN_PX = 38;
            public static final int HALFHOUR_IN_PX = 19;
            private static final int CAPTIONHEIGHT = 14;
            private Date startDatetimeFrom;
            private Date startDatetimeTo;
            private boolean mouseMoveStarted;
            private int top;
            private int bufferInPX;
            private int startYrelative;
            private int startXrelative;
            private boolean readOnly;

            public DayEvent(CalendarEvent e2) {
                super();

                setCalendarEvent(e2);
                Style s = getElement().getStyle();
                addStyleName("buffer");
                if (e2.getStyleName().length() > 0) {
                    addStyleName(e2.getStyleName());
                }
                s.setPosition(Position.ABSOLUTE);
                s.setMarginLeft(1, Unit.PX);
                Element e = getElement();

                dayEvent = DOM.createDiv();
                dayEvent.addClassName("dayevent");
                caption = DOM.createDiv();
                caption.addClassName("caption");
                e.appendChild(dayEvent);
                dayEvent.appendChild(caption);
                eventContent = DOM.createDiv();
                eventContent.addClassName("content");
                dayEvent.appendChild(eventContent);
                addMouseDownHandler(this);
                addMouseUpHandler(this);
            }

            public void updatePosition(long startFromMinutes,
                    long durationInMinutes) {
                top = (int) ((((double) HOUR_IN_PX / 60)) * startFromMinutes);
                getElement().getStyle().setTop(top, Unit.PX);
                if (durationInMinutes > 30) {
                    int heightMinutes = (int) (((double) HOUR_IN_PX / 60) * durationInMinutes);
                    setHeight(heightMinutes - 3);
                    updateCaptions(true);
                } else {
                    setHeight(16);
                    updateCaptions(false);
                }
            }

            public int getTop() {
                return top;
            }

            public void setMoveWidth(int width) {
                moveWidth = (width - 4) + "px";
            }

            public void setHeight(int i) {
                getElement().getStyle().setHeight(i, Unit.PX);
                dayEvent.getStyle().setHeight(i, Unit.PX);
                eventContent.getStyle().setHeight(i - CAPTIONHEIGHT, Unit.PX);
            }

            /**
             * @param bigMode
             *            If false, event is so small that caption must be in
             *            time-row
             */
            private void updateCaptions(boolean bigMode) {
                caption.setInnerHTML(calendarEvent.getCaption());
                eventContent.setInnerHTML("");
            }

            @Override
            public void setText(String text) {
                caption.setInnerText(text);
            }

            public void onMouseDown(MouseDownEvent event) {
                EventTarget et = event.getNativeEvent().getEventTarget();
                Element e = Element.as(et);
                moveRegistration = addMouseMoveHandler(this);
                startX = event.getClientX();
                startY = event.getClientY();
                startYrelative = event.getRelativeY(caption) % HALFHOUR_IN_PX;
                startXrelative = (event.getRelativeX(getParent().getParent()
                        .getParent().getElement()) - 50)
                        % getDateCellWidth();
                mouseMoveStarted = false;
                Style s = getElement().getStyle();
                startDatetimeFrom = (Date) calendarEvent.getStartTime().clone();
                startDatetimeTo = (Date) calendarEvent.getEndTime().clone();
                // s.setOpacity(1);
                s.setZIndex(1000);
                if (e == caption || e == dayEvent || e == eventContent) {
                    Event.setCapture(getElement());
                } else if (e == getElement()) {
                    Event.setCapture(getElement());
                } else if (e == dayEvent) {
                    // System.out.println("event");
                }
                event.getNativeEvent().stopPropagation();
            }

            public void onMouseUp(MouseUpEvent event) {
                Event.releaseCapture(getElement());
                moveRegistration.removeHandler();
                int endX = event.getClientX();
                int endY = event.getClientY();
                int xDiff = startX - endX;
                int yDiff = startY - endY;
                startX = -1;
                startY = -1;
                mouseMoveStarted = false;
                Style s = getElement().getStyle();
                s.setZIndex(1);
                if (xDiff < -3 || xDiff > 3 || yDiff < -3 || yDiff > 3) {
                    DateCell parent = (DateCell) getParent();
                    WeekGrid wk = (WeekGrid) parent.getParent().getParent();
                    wk.eventMoved(this);
                } else {
                    EventTarget et = event.getNativeEvent().getEventTarget();
                    Element e = Element.as(et);
                    if (e == caption || e == eventContent) {
                        DateCell parent = (DateCell) getParent();
                        VCalendar wk = (VCalendar) parent.getParent()
                                .getParent().getParent().getParent();

                        if (wk.getClient().hasEventListeners(wk,
                                CalendarEventId.EVENTCLICK)) {
                            wk.getClient().updateVariable(wk.getPID(),
                                    CalendarEventId.EVENTCLICK,
                                    calendarEvent.getIndex(), true);
                        }
                    } else if (e == getElement()) {
                    } else if (e == dayEvent) {
                    }
                }
            }

            @SuppressWarnings("deprecation")
            public void onMouseMove(MouseMoveEvent event) {
                if (startY < 0 && startX < 0) {
                    return;
                }
                if (readOnly) {
                    Event.releaseCapture(getElement());
                    mouseMoveStarted = false;
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
                if (!mouseMoveStarted) {
                    setWidth(moveWidth);
                    mouseMoveStarted = true;
                }

                Widget parent = getParent().getParent();
                int relativeX = event.getRelativeX(parent.getElement()) - 50;
                int halfHourDiff = 0;
                if (moveY > 0) {
                    halfHourDiff = (startYrelative + moveY) / HALFHOUR_IN_PX;
                } else {
                    halfHourDiff = (moveY - startYrelative) / HALFHOUR_IN_PX;
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
                dayOffset = dayOffset * dateCellWidth;
                if (relativeX < 0 || relativeX >= getDatesWidth()) {
                    return;
                }

                Style s = getElement().getStyle();

                Date from = calendarEvent.getStartTime();
                Date to = calendarEvent.getEndTime();
                long duration = to.getTime() - from.getTime();

                long daysMs = dayDiff * VCalendar.DAYINMILLIS;
                from.setTime(startDatetimeFrom.getTime() + daysMs);
                from.setTime(from.getTime()
                        + ((long) halfHourInMilliSeconds * halfHourDiff));
                to.setTime((long) (from.getTime() + duration));
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
                    long minutesOnPrevDay = (getTargetDateByCurrentPosition()
                            .getTime() - from.getTime())
                            / VCalendar.MINUTEINMILLIS;
                    startFromMinutes = -1 * minutesOnPrevDay;
                }
                updatePosition(startFromMinutes, range);
                s.setLeft(dayOffset + 50, Unit.PX);
            }

            private Date getTargetDateByCurrentPosition() {
                Style s = getElement().getStyle();
                DateCell dateCell = (DateCell) getParent();
                WeekGrid wk = (WeekGrid) dateCell.getParent().getParent();
                int left = Integer.parseInt(s.getLeft().substring(0,
                        s.getLeft().length() - 2));
                int datesWidth = wk.width - 67;
                int count = wk.content.getWidgetCount();
                int cellWidth = datesWidth / (count - 1);
                DateCell newParent = (DateCell) wk.content
                        .getWidget((left / cellWidth) + 1);
                Date targetDate = newParent.getDate();
                return targetDate;
            }

            private int getDateCellWidth() {
                WeekGrid wk = (WeekGrid) getParent().getParent().getParent();
                int count = wk.content.getWidgetCount() - 1;
                int margin = count + 10; // margin + scrollbar's space
                int datesWidth = wk.width - 50 - margin;
                int cellWidth = datesWidth / count;
                return cellWidth;
            }

            /* Returns total width of all date cells. */
            private int getDatesWidth() {
                WeekGrid wk = (WeekGrid) getParent().getParent().getParent();
                int cellCount = wk.content.getWidgetCount() - 1;
                int datesWidth = getDateCellWidth() * cellCount;
                return datesWidth;
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

        @SuppressWarnings("deprecation")
        public void setToday(Date today) {
            Element lastChild = (Element) getElement().getLastChild();
            Element todaybar = null;
            if (lastChild.getClassName().equals("todaybar")) {
                todaybar = lastChild;
            } else {
                todaybar = DOM.createDiv();
                todaybar.setClassName("todaybar");
                getElement().appendChild(todaybar);
            }
            int h = (23 - today.getHours());
            int m = (60 - today.getMinutes());
            int mInPx = (int) (((double) 38 / 60) * m);
            int px = ((h * 38) + mInPx);
            todaybar.getStyle().setTop(-px, Unit.PX);
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
            this.readOnly = readOnly;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public void setDateColor(String styleName) {
            this.setStyleName("v-calendar-datecell " + styleName);
        }
    }

    public void addEvent(CalendarEvent e) {
        int dateCount = content.getWidgetCount();
        Date from = e.getStart();
        Date to = e.getEnd();
        for (int i = 1; i < dateCount; i++) {
            DateCell dc = (DateCell) content.getWidget(i);
            Date dcDate = dc.getDate();
            int comp = dcDate.compareTo(from);
            int comp2 = dcDate.compareTo(to);
            if (comp >= 0 && comp2 <= 0) {
                // Same event may be over two DateCells if event's date
                // range floats over one day. It can't float over two days,
                // because event which range is over 24 hours, will be handled
                // as a "fullDay" event.
                dc.addEvent(dcDate, e);
            }
        }
    }

    public void eventMoved(DayEvent dayEvent) {
        Style s = dayEvent.getElement().getStyle();
        int left = Integer.parseInt(s.getLeft().substring(0,
                s.getLeft().length() - 2));
        int datesWidth = width - 67;
        int count = content.getWidgetCount();
        int cellWidth = datesWidth / (count - 1);
        DateCell previousParent = (DateCell) dayEvent.getParent();
        DateCell newParent = (DateCell) content
                .getWidget((left / cellWidth) + 1);
        CalendarEvent se = dayEvent.getCalendarEvent();
        previousParent.removeEvent(dayEvent);
        newParent.addEvent(dayEvent);
        newParent.recalculateEventWidths();
        DateTimeFormat dateformat_date = DateTimeFormat.getFormat("yyyy-MM-dd");
        DateTimeFormat dateformat_time = DateTimeFormat.getFormat("HH-mm");
        String eventMove = se.getIndex() + ":"
                + dateformat_date.format(se.getStart()) + "-"
                + dateformat_time.format(se.getStartTime());

        if (schedule.getClient().hasEventListeners(schedule,
                CalendarEventId.EVENTMOVE)) {
            schedule.getClient().updateVariable(schedule.getPID(),
                    CalendarEventId.EVENTMOVE, eventMove, true);
        }
    }

    public void setToday(Date todayDate, Date todayTimestamp) {
        int count = content.getWidgetCount();
        if (count > 1) {
            for (int i = 1; i < count; i++) {
                DateCell dc = (DateCell) content.getWidget(i);
                if (dc.getDate().getTime() == todayDate.getTime()) {
                    dc.setToday(todayTimestamp);
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

}
