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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

/**
 * A class representing a single cell within the calendar in month-view
 */
public class SimpleDayCell extends VerticalPanel implements MouseUpHandler,
        MouseDownHandler, MouseOverHandler, NativePreviewHandler {

    private final VCalendar schedule;
    private Date date;
    private boolean enabled = true;
    private int intHeight;
    private HTML bottomspacer;
    private Label caption;
    private static final int LINEHEIGHT = 18;
    private static final int EVENTHEIGHT = 14;
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
        setStyleName("v-calendar-monthly-day");
        caption = new Label();
        bottomspacer = new HTML();
        bottomspacer.setStyleName("bottomspacer");
        caption.setStyleName("daycaption");
        caption.setHeight(LINEHEIGHT + "px");
        caption.setHorizontalAlignment(ALIGN_RIGHT);
        caption.addMouseDownHandler(this);
        caption.addMouseUpHandler(this);
        add(caption);
        add(bottomspacer);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            addStyleName("disabled");
        } else {
            removeStyleName("disabled");
        }

    }

    @SuppressWarnings("deprecation")
    public void setDate(Date date) {
        int dateOfMonth = date.getDate();
        if (monthNameVisible) {
            caption.setText(dateOfMonth + " "
                    + this.schedule.getMonthNames()[date.getMonth()]);
        } else {
            caption.setText("" + dateOfMonth);
        }
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setHeightPX(int px) {
        this.intHeight = px - BORDERPADDINGHEIGHT;
        while (getWidgetCount() > 1) {
            remove(1);
        }
        // How many events can be shown in UI
        int slots = (intHeight - (2 * LINEHEIGHT)) / EVENTHEIGHT;
        if (slots > 10) {
            slots = 10;
        }
        int eventsAdded = 0;
        for (int i = 0; i < slots; i++) {
            CalendarEvent e = events[i];
            if (e == null) {
                HTML slot = new HTML();
                slot.addStyleName("spacer");
                add(slot);
            } else {
                eventsAdded++;
                add(createMonthEventLabel(e));
            }
        }
        int remainingSpace = intHeight
                - ((slots * EVENTHEIGHT) + (2 * LINEHEIGHT));
        bottomspacer.setHeight(remainingSpace + LINEHEIGHT + "px");
        add(bottomspacer);

        int more = eventCount - eventsAdded;
        if (more > 0) {
            bottomspacer.setText("+ " + more);
        }
    }

    private MonthEventLabel createMonthEventLabel(CalendarEvent e) {
        MonthEventLabel eventDiv = new MonthEventLabel();
        if (e.getFromDate().compareTo(e.getToDate()) == 0) {
            Date fromDatetime = e.getFromDatetime();
            eventDiv.addStyleName("month-event short");
            eventDiv.addMouseDownHandler(this);
            eventDiv.addMouseUpHandler(this);
            eventDiv.setHTML(schedule.getTimeFormat().format(fromDatetime)
                    + " " + e.getCaption());
        } else {
            Date from = e.getFromDate();
            Date to = e.getToDate();
            MonthGrid monthGrid = (MonthGrid) getParent();
            eventDiv.addMouseDownHandler(this);
            eventDiv.addMouseUpHandler(this);
            if (e.getStyleName().length() > 0) {
                eventDiv.addStyleName("month-event " + e.getStyleName());
            } else {
                eventDiv.addStyleName("month-event");
            }
            int fromCompareToDate = from.compareTo(date);
            int toCompareToDate = to.compareTo(date);
            if (fromCompareToDate == 0) {
                eventDiv.addStyleName("event-start");
                eventDiv.setHTML(e.getCaption());
            } else if (fromCompareToDate < 0 && cell == 0) {
                eventDiv.addStyleName("event-continue-left");
                eventDiv.setHTML(e.getCaption());
            }
            if (toCompareToDate == 0) {
                eventDiv.addStyleName("event-end");
            } else if (toCompareToDate > 0
                    && (cell + 1) == monthGrid.getCellCount(row)) {
                eventDiv.addStyleName("event-continue-right");
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
        this.monthNameVisible = b;
        int dateOfMonth = date.getDate();
        caption.setText(dateOfMonth + " "
                + this.schedule.getMonthNames()[date.getMonth()]);
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

    }

    public void setToday(boolean today) {
        if (today) {
            addStyleName("today");
        } else {
            removeStyleName("today");
        }
    }

    public CalendarEvent getScheduleEvent(int i) {
        return events[i];
    }

}