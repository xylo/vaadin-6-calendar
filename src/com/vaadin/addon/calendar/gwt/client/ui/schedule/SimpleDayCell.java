package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

/**
 * A class representing a single cell within the calendar in month-view
 */
public class SimpleDayCell extends FlowPanel implements MouseUpHandler,
        MouseDownHandler, MouseOverHandler, NativePreviewHandler {

    private final VCalendar schedule;
    private Date date;
    private boolean enabled = true;
    private int intHeight;
    private HTML bottomspacer;
    private Label caption;
    private static int EVENTHEIGHT = -1;
    private static final int BORDERPADDINGHEIGHT = 1;
    private CalendarEvent[] events = new CalendarEvent[10];
    private int cell;
    private int row;
    private boolean monthNameVisible;
    private HandlerRegistration registration;
    private HandlerRegistration registration2;
    private HandlerRegistration registration3;
    private HandlerRegistration registration4;
    private boolean monthEventMouseDown;
    private boolean labelMouseDown;
    private int eventCount = 0;

    public SimpleDayCell(VCalendar schedule, int row, int cell) {
        this.schedule = schedule;
        this.row = row;
        this.cell = cell;
        setStylePrimaryName("v-calendar-month-day");
        caption = new Label();
        bottomspacer = new HTML();
        bottomspacer.setStyleName("v-calendar-bottom-spacer");
        caption.setStyleName("v-calendar-day-number");
        add(caption);
        add(bottomspacer);
        caption.addMouseDownHandler(this);
        caption.addMouseUpHandler(this);
    }

    @Override
    public void onLoad() {
        EVENTHEIGHT = bottomspacer.getOffsetHeight();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            addStyleDependentName("disabled");
        } else {
            removeStyleDependentName("disabled");
        }

    }

    @SuppressWarnings("deprecation")
    public void setDate(Date date) {
        int dateOfMonth = date.getDate();
        if (monthNameVisible) {
            caption.setText(dateOfMonth + " "
                    + schedule.getMonthNames()[date.getMonth()]);
        } else {
            caption.setText("" + dateOfMonth);
        }
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setHeightPX(int px) {
        intHeight = px - BORDERPADDINGHEIGHT;
        while (getWidgetCount() > 1) {
            remove(1);
        }
        // How many events can be shown in UI
        int slots = (intHeight - caption.getOffsetHeight() - EVENTHEIGHT)
                / EVENTHEIGHT;
        if (slots > 10) {
            slots = 10;
        }
        int eventsAdded = 0;
        for (int i = 0; i < slots; i++) {
            CalendarEvent e = events[i];
            if (e == null) {
                HTML slot = new HTML();
                slot.setStyleName("v-calendar-spacer");
                add(slot);
            } else {
                eventsAdded++;
                add(createMonthEventLabel(e));
            }
        }
        int remainingSpace = intHeight
                - ((slots * EVENTHEIGHT) + EVENTHEIGHT + caption
                        .getOffsetHeight());
        bottomspacer.setHeight(remainingSpace + EVENTHEIGHT + "px");
        add(bottomspacer);

        int more = eventCount - eventsAdded;
        if (more > 0) {
            bottomspacer.setText("+ " + more);
        } else {
            bottomspacer.setText("");
        }
    }

    private MonthEventLabel createMonthEventLabel(CalendarEvent e) {
        MonthEventLabel eventDiv = new MonthEventLabel();
        eventDiv.addStyleDependentName("month");

        long rangeInMillis = e.getRangeInMilliseconds();

        if (rangeInMillis < VCalendar.DAYINMILLIS && rangeInMillis != 0) {
            Date fromDatetime = e.getStartTime();
            eventDiv.addMouseDownHandler(this);
            eventDiv.addMouseUpHandler(this);
            eventDiv.setText(schedule.getTimeFormat().format(fromDatetime)
                    + " " + e.getCaption());
            if (e.getStyleName() != null) {
                eventDiv.addStyleDependentName(e.getStyleName());
            }
        } else {
            Date from = e.getStart();
            Date to = e.getEnd();
            MonthGrid monthGrid = (MonthGrid) getParent();
            eventDiv.addMouseDownHandler(this);
            eventDiv.addMouseUpHandler(this);
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
                    && (cell + 1) == monthGrid.getCellCount(row)) {
                eventDiv.addStyleDependentName("continued-to");
            }
            if (e.getStyleName() != null) {
                eventDiv.addStyleDependentName(e.getStyleName() + "-all-day");
            }
        }
        return eventDiv;
    }

    public void addScheduleEvent(CalendarEvent e) {
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
                + schedule.getMonthNames()[date.getMonth()]);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        registration = addDomHandler(this, MouseUpEvent.getType());
        registration2 = addDomHandler(this, MouseDownEvent.getType());
        registration3 = addDomHandler(this, MouseOverEvent.getType());
        registration4 = Event.addNativePreviewHandler(this);
    }

    @Override
    protected void onDetach() {
        registration.removeHandler();
        registration2.removeHandler();
        registration3.removeHandler();
        registration4.removeHandler();
        super.onDetach();
    }

    public void onMouseUp(MouseUpEvent event) {
        Widget w = (Widget) event.getSource();
        if (w == bottomspacer && monthEventMouseDown) {

        } else if (w instanceof MonthEventLabel && monthEventMouseDown) {
            MonthEventLabel me = (MonthEventLabel) w;
            int index = getWidgetIndex(me);
            CalendarEvent e = events[index - 1];
            if (schedule.getClient().hasEventListeners(schedule,
                    CalendarEventId.EVENTCLICK)) {
                schedule.getClient().updateVariable(schedule.getPID(),
                        CalendarEventId.EVENTCLICK, e.getIndex(), true);
            }
            event.stopPropagation();
        } else if (w == this) {
            MonthGrid grid = (MonthGrid) getParent();
            grid.setSelectionReady();
        } else if (w instanceof Label && labelMouseDown) {
            String clickedDate = schedule.getDateFormat().format(date);
            if (schedule.getClient().hasEventListeners(schedule,
                    CalendarEventId.DATECLICK)) {
                schedule.getClient().updateVariable(schedule.getPID(),
                        CalendarEventId.DATECLICK, clickedDate, true);
            }
            event.stopPropagation();
        }
        monthEventMouseDown = false;
        labelMouseDown = false;
    }

    public void onMouseDown(MouseDownEvent event) {
        Widget w = (Widget) event.getSource();
        if (w == bottomspacer || w instanceof MonthEventLabel) {
            monthEventMouseDown = true;
            event.stopPropagation();
        } else if (w == this) {
            MonthGrid grid = (MonthGrid) getParent();
            if (!grid.isReadOnly()) {
                grid.setSelectionStart(this);
                grid.setSelectionEnd(this);
            }
        } else if (w instanceof Label) {
            labelMouseDown = true;
            event.stopPropagation();
        }
    }

    public void onMouseOver(MouseOverEvent event) {
        event.preventDefault();
        MonthGrid grid = (MonthGrid) getParent();
        grid.setSelectionEnd(this);
    }

    public int getRow() {
        return row;
    }

    public int getCell() {
        return cell;
    }

    public void onPreviewNativeEvent(NativePreviewEvent event) {

        if (event.getTypeInt() == Event.ONMOUSEDOWN
                && DOM.isOrHasChild(getElement(), (Element) Element.as(event
                        .getNativeEvent().getEventTarget()))) {
            event.getNativeEvent().preventDefault();
        }
    }

    public static class MonthEventLabel extends HTML {
        public MonthEventLabel() {
            setStylePrimaryName("v-calendar-event");
        }
    }

    public void setToday(boolean today) {
        if (today) {
            addStyleDependentName("today");
        } else {
            removeStyleDependentName("today");
        }
    }

    public CalendarEvent getScheduleEvent(int i) {
        return events[i];
    }

}