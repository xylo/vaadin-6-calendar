package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEvent;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.DateUtil;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.dd.CalendarDropHandler;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.dd.CalendarMonthDropHandler;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.dd.CalendarWeekDropHandler;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.TooltipInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler;

/**
 * Handles communication between {@link Calendar} on the server side and
 * {@link GWTCalendar} on the client side.
 * 
 * @author John Ahlroos / Vaadin Ltd 2012
 * @since 1.0.0
 */
public class VCalendar extends GWTCalendar implements Paintable,
        VHasDropHandler {

    public static final String ACCESSCRITERIA = "-ac";
    public static final String ATTR_WEEK = "w";
    public static final String ATTR_DOW = "dow";
    public static final String ATTR_FDATE = "fdate";
    public static final String ATTR_DATE = "date";
    public static final String ATTR_STYLE = "extracss";
    public static final String ATTR_DESCRIPTION = "desc";
    public static final String ATTR_TIMETO = "tto";
    public static final String ATTR_TIMEFROM = "tfrom";
    public static final String ATTR_DATETO = "dto";
    public static final String ATTR_DATEFROM = "dfrom";
    public static final String ATTR_CAPTION = "caption";
    public static final String ATTR_INDEX = "i";
    public static final String ATTR_SCROLL = "scroll";
    public static final String ATTR_FDOW = "fdow";
    public static final String ATTR_NOW = "now";
    public static final String ATTR_READONLY = "readonly";
    public static final String ATTR_DISABLED = "disabled";
    public static final String ATTR_MONTH_NAMES = "mNames";
    public static final String ATTR_DAY_NAMES = "dNames";
    public static final String ATTR_FORMAT24H = "format24h";
    public static final String ATTR_ALLDAY = "allday";
    public static final String ATTR_NAVIGATION = "navigation";

    private ApplicationConnection client;

    private CalendarDropHandler dropHandler;

    private String PID;

    public VCalendar() {

        // Listen to events
        registerListeners();

    }

    protected void registerListeners() {
        setListener(new DateClickListener() {
            public void dateClick(String date) {
                if (!isDisabledOrReadOnly()
                        && getClient().hasEventListeners(VCalendar.this,
                                CalendarEventId.DATECLICK)) {
                    client.updateVariable(PID, CalendarEventId.DATECLICK, date, true);
                }
            }
        });
        setListener(new ForwardListener() {
            public void forward() {
                if (client.hasEventListeners(VCalendar.this,
                        CalendarEventId.FORWARD)) {
                    client.updateVariable(PID, ATTR_NAVIGATION, true, true);
                }
            }
        });
        setListener(new BackwardListener() {
            public void backward() {
                if (client.hasEventListeners(VCalendar.this,
                        CalendarEventId.BACKWARD)) {
                    client.updateVariable(PID, ATTR_NAVIGATION, false, true);
                }
            }
        });
        setListener(new RangeSelectListener() {
            public void rangeSelected(String value) {
                if (client.hasEventListeners(VCalendar.this,
                        CalendarEventId.RANGESELECT)) {
                    client.updateVariable(PID, CalendarEventId.RANGESELECT,
                            value, true);
                }
            }
        });
        setListener(new WeekClickListener() {
            public void weekClick(String event) {
                if (!isDisabledOrReadOnly()
                        && client.hasEventListeners(VCalendar.this,
                                CalendarEventId.WEEKCLICK)) {
                    client.updateVariable(PID, CalendarEventId.WEEKCLICK,
                            event, true);
                }
            }
        });
        setListener(new EventMovedListener() {
            public void eventMoved(CalendarEvent event) {
                if (client.hasEventListeners(VCalendar.this,
                        CalendarEventId.EVENTMOVE)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(event.getIndex());
                    sb.append(":");
                    sb.append(DateUtil.formatClientSideDate(event.getStart()));
                    sb.append("-");
                    sb.append(DateUtil.formatClientSideTime(event
                            .getStartTime()));
                    client.updateVariable(PID, CalendarEventId.EVENTMOVE,
                            sb.toString(), true);
                }
            }
        });
        setListener(new EventResizeListener() {
            public void eventResized(CalendarEvent event) {
                if (client.hasEventListeners(VCalendar.this,
                        CalendarEventId.EVENTRESIZE)) {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append(event.getIndex());
                    buffer.append(",");

                    buffer.append(DateUtil.formatClientSideDate(event
                            .getStart()));
                    buffer.append("-");
                    buffer.append(DateUtil.formatClientSideTime(event
                            .getStartTime()));

                    buffer.append(",");

                    buffer.append(DateUtil.formatClientSideDate(event.getEnd()));
                    buffer.append("-");
                    buffer.append(DateUtil.formatClientSideTime(event
                            .getEndTime()));

                    client.updateVariable(PID, CalendarEventId.EVENTRESIZE,
                            buffer.toString(), true);
                }
            }
        });
        setListener(new ScrollListener() {
            public void scroll(int scrollPosition) {
                client.updateVariable(PID, ATTR_SCROLL, scrollPosition, false);
            }
        });
        setListener(new EventClickListener() {
            public void eventClick(CalendarEvent event) {
                if (client.hasEventListeners(VCalendar.this,
                        CalendarEventId.EVENTCLICK)) {
                    client.updateVariable(PID, CalendarEventId.EVENTCLICK,
                            event.getIndex(), true);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // This call should be made first. Ensure correct implementation,
        // and let the containing layout manage caption, etc.
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        // Save reference to server connection object to be able to send
        // user interaction later
        this.client = client;
        PID = uidl.getId();
        set24HFormat(uidl.getBooleanAttribute(ATTR_FORMAT24H));
        setDayNames(uidl.getStringArrayAttribute(ATTR_DAY_NAMES));
        setMonthNames(uidl.getStringArrayAttribute(ATTR_MONTH_NAMES));

        setFirstDayNumber(uidl.getIntAttribute(ATTR_FIRSTDAYOFWEEK));
        setLastDayNumber(uidl.getIntAttribute(ATTR_LASTDAYOFWEEK));

        setFirstHourOfTheDay(uidl.getIntAttribute(ATTR_FIRSTHOUROFDAY));
        setLastHourOfTheDay(uidl.getIntAttribute(ATTR_LASTHOUROFDAY));

        setReadOnly(uidl.hasAttribute(ATTR_READONLY)
                && uidl.getBooleanAttribute(ATTR_READONLY));

        setDisabled(uidl.hasAttribute(ATTR_DISABLED)
                && uidl.getBooleanAttribute(ATTR_DISABLED));

        setRangeSelectAllowed(client.hasEventListeners(this,
                CalendarEventId.RANGESELECT));
        setRangeMoveAllowed(client.hasEventListeners(this,
                CalendarEventId.EVENTMOVE));
        setEventResizeAllowed(client.hasEventListeners(this,
                CalendarEventId.EVENTRESIZE));
        setEventMoveAllowed(client.hasEventListeners(this,
                CalendarEventId.EVENTMOVE));

        UIDL daysUidl = uidl.getChildUIDL(0);
        int daysCount = daysUidl.getChildCount();
        boolean monthView = daysCount > 7;
        if (monthView) {
            updateMonthView(uidl, daysUidl);
        } else {
            updateWeekView(uidl, daysUidl);
        }

        // check for DD -related access criteria
        Iterator<Object> childIterator = uidl.getChildIterator();
        while (childIterator.hasNext()) {
            UIDL child = (UIDL) childIterator.next();
            if (ACCESSCRITERIA.equals(child.getTag())) {
                GWT.log("DD access criteria found");

                if (monthView
                        && !(getDropHandler() instanceof CalendarMonthDropHandler)) {
                    setDropHandler(new CalendarMonthDropHandler());

                } else if (!monthView
                        && !(getDropHandler() instanceof CalendarWeekDropHandler)) {
                    setDropHandler(new CalendarWeekDropHandler());
                }

                getDropHandler().setCalendarPaintable(this);
                getDropHandler().updateAcceptRules(child);

            } else {
                setDropHandler(null);
            }
        }
    }

    /**
     * Returns the ApplicationConnection used to connect to the server side
     */
    public ApplicationConnection getClient() {
        return client;
    }

    /** Transforms uidl to list of CalendarEvents */
    protected ArrayList<CalendarEvent> getEvents(UIDL childUIDL) {
        int eventCount = childUIDL.getChildCount();
        ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
        for (int i = 0; i < eventCount; i++) {
            UIDL eventUIDL = childUIDL.getChildUIDL(i);

            int index = eventUIDL.getIntAttribute(ATTR_INDEX);
            String caption = eventUIDL.getStringAttribute(ATTR_CAPTION);
            String datefrom = eventUIDL.getStringAttribute(ATTR_DATEFROM);
            String dateto = eventUIDL.getStringAttribute(ATTR_DATETO);
            String timefrom = eventUIDL.getStringAttribute(ATTR_TIMEFROM);
            String timeto = eventUIDL.getStringAttribute(ATTR_TIMETO);
            String desc = eventUIDL.getStringAttribute(ATTR_DESCRIPTION);
            String style = eventUIDL.getStringAttribute(ATTR_STYLE);
            boolean allDay = eventUIDL.getBooleanAttribute(ATTR_ALLDAY);

            CalendarEvent e = new CalendarEvent();

            e.setCaption(caption);
            e.setDescription(desc);
            e.setIndex(index);
            e.setEnd(dateformat_date.parse(dateto));
            e.setStart(dateformat_date.parse(datefrom));
            e.setStartTime(dateformat_datetime.parse(datefrom + " " + timefrom));
            e.setEndTime(dateformat_datetime.parse(dateto + " " + timeto));
            e.setStyleName(style);
            e.setFormat24h(is24HFormat());
            e.setAllDay(allDay);

            events.add(e);

            registerEventToolTip(e);
        }
        return events;
    }

    /**
     * Register the description of the event as a tooltip for this paintable.
     * This way, any event displaying widget can use the event index as a key to
     * display the tooltip.
     */
    private void registerEventToolTip(CalendarEvent e) {
        if (e.getDescription() != null && !"".equals(e.getDescription())) {
            TooltipInfo info = new TooltipInfo(e.getDescription());
            client.registerTooltip(this, e.getIndex(), info);

        } else {
            client.registerTooltip(this, e.getIndex(), null);
        }
    }

    private List<Day> getDaysFromUIDL(UIDL daysUIDL){
        List<Day> days = new ArrayList<GWTCalendar.Day>();
        for (int i = 0; i < daysUIDL.getChildCount(); i++) {
            UIDL dayUidl = daysUIDL.getChildUIDL(i);
            String date = dayUidl.getStringAttribute(ATTR_DATE);
            String localized_date_format = dayUidl
                    .getStringAttribute(ATTR_FDATE);
            int dayOfWeek = dayUidl.getIntAttribute(ATTR_DOW);
            int week = dayUidl.getIntAttribute(ATTR_WEEK);
            days.add(new Day(date,localized_date_format,dayOfWeek,week));
        }
        return days;
    }

    private void updateMonthView(UIDL uidl, UIDL daysUidl) {
        int firstDayOfWeek = uidl.getIntAttribute(ATTR_FDOW);
        Date today = dateformat_datetime.parse(uidl
                .getStringAttribute(ATTR_NOW));
        super.updateMonthView(firstDayOfWeek, today, daysUidl.getChildCount(),
                getEvents(uidl.getChildUIDL(1)), getDaysFromUIDL(daysUidl));
    }

    private void updateWeekView(UIDL uidl, UIDL daysUidl) {
        int scroll = uidl.getIntVariable(ATTR_SCROLL);
        Date today = dateformat_datetime.parse(uidl
                .getStringAttribute(ATTR_NOW));
        int daysCount = daysUidl.getChildCount();
        int firstDayOfWeek = uidl.getIntAttribute(ATTR_FDOW);

        super.updateWeekView(scroll, today, daysCount, firstDayOfWeek,
                getEvents(uidl.getChildUIDL(1)), getDaysFromUIDL(daysUidl));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.gwt.client.ui.GWTCalendar#handleTooltipEvent
     * (com.google.gwt.user.client.Event)
     */
    @Override
    public void handleTooltipEvent(Event event, Object key) {
        if (client != null) {
            client.handleTooltipEvent(event, this, key);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VHasDropHandler#getDropHandler()
     */
    public CalendarDropHandler getDropHandler() {
        return dropHandler;
    }

    /**
     * Set the drop handler
     * 
     * @param dropHandler
     *            The drophandler to use
     */
    public void setDropHandler(CalendarDropHandler dropHandler) {
        this.dropHandler = dropHandler;
    }
}
