/**
 * Copyright (C) 2010 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.addon.calendar.gwt.client.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.BackwardListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.DateClickListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.EventClickListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.EventMovedListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.EventResizeListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.ForwardListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.MouseEventListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.RangeSelectListener;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar.WeekClickListener;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarDay;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEvent;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEventId;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.DateUtil;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.HasTooltipKey;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleDayCell;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell.DateCellSlot;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell.DayEvent;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.dd.CalendarDropHandler;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.TooltipInfo;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.client.ui.Action;
import com.vaadin.client.ui.ActionOwner;
import com.vaadin.client.ui.SimpleManagedLayout;
import com.vaadin.client.ui.dd.VHasDropHandler;
import com.vaadin.shared.ui.Connect;

/**
 * Handles communication between {@link Calendar} on the server side and
 * {@link VCalendar} on the client side.
 * 
 * @since 1.0.0
 * @version
 * ${pom.version}
 */
@Connect(com.vaadin.addon.calendar.ui.Calendar.class)
public class CalendarConnector extends AbstractComponentConnector implements
        VHasDropHandler, ActionOwner, SimpleManagedLayout {

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

    private CalendarServerRpc rpc = RpcProxy.create(CalendarServerRpc.class,
            this);

    private CalendarDropHandler dropHandler;

    private final HashMap<String, String> actionMap = new HashMap<String, String>();
    private HashMap<Object, String> tooltips = new HashMap<Object, String>();

    /**
     * 
     */
    public CalendarConnector() {

        // Listen to events
        registerListeners();
    }

    @Override
    protected void init() {
        super.init();
        registerRpc(CalendarClientRpc.class, new CalendarClientRpc() {
            @Override
            public void scroll(int scrollPosition) {
                // TODO widget scroll
            }
        });
        getLayoutManager().registerDependency(this, getWidget().getElement());
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        getLayoutManager().unregisterDependency(this, getWidget().getElement());
    }

    @Override
    public VCalendar getWidget() {
        return (VCalendar) super.getWidget();
    }

    @Override
    public CalendarState getState() {
        return (CalendarState) super.getState();
    }

    /**
     * Registers listeners on the calendar so server can be notified of the
     * events
     */
    protected void registerListeners() {
        getWidget().setListener(new DateClickListener() {
            public void dateClick(String date) {
                if (!getWidget().isDisabledOrReadOnly()
                        && hasEventListener(CalendarEventId.DATECLICK)) {
                    rpc.dateClick(date);
                }
            }
        });
        getWidget().setListener(new ForwardListener() {
            public void forward() {
                if (hasEventListener(CalendarEventId.FORWARD)) {
                    rpc.forward();
                }
            }
        });
        getWidget().setListener(new BackwardListener() {
            public void backward() {
                if (hasEventListener(CalendarEventId.BACKWARD)) {
                    rpc.backward();
                }
            }
        });
        getWidget().setListener(new RangeSelectListener() {
            public void rangeSelected(String value) {
                if (hasEventListener(CalendarEventId.RANGESELECT)) {
                    rpc.rangeSelect(value);
                }
            }
        });
        getWidget().setListener(new WeekClickListener() {
            public void weekClick(String event) {
                if (!getWidget().isDisabledOrReadOnly()
                        && hasEventListener(CalendarEventId.WEEKCLICK)) {
                    rpc.weekClick(event);
                }
            }
        });
        getWidget().setListener(new EventMovedListener() {
            public void eventMoved(CalendarEvent event) {
                if (hasEventListener(CalendarEventId.EVENTMOVE)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(DateUtil.formatClientSideDate(event.getStart()));
                    sb.append("-");
                    sb.append(DateUtil.formatClientSideTime(event
                            .getStartTime()));
                    rpc.eventMove(event.getIndex(), sb.toString());
                }
            }
        });
        getWidget().setListener(new EventResizeListener() {
            public void eventResized(CalendarEvent event) {
                if (hasEventListener(CalendarEventId.EVENTRESIZE)) {
                    StringBuilder buffer = new StringBuilder();

                    buffer.append(DateUtil.formatClientSideDate(event
                            .getStart()));
                    buffer.append("-");
                    buffer.append(DateUtil.formatClientSideTime(event
                            .getStartTime()));

                    String newStartDate = buffer.toString();

                    buffer = new StringBuilder();
                    buffer.append(DateUtil.formatClientSideDate(event.getEnd()));
                    buffer.append("-");
                    buffer.append(DateUtil.formatClientSideTime(event
                            .getEndTime()));

                    String newEndDate = buffer.toString();

                    rpc.eventResize(event.getIndex(), newStartDate, newEndDate);
                }
            }
        });
        getWidget().setListener(new VCalendar.ScrollListener() {
            public void scroll(int scrollPosition) {
                // This call is @Delayed (== non-immediate)
                rpc.scroll(scrollPosition);
            }
        });
        getWidget().setListener(new EventClickListener() {
            public void eventClick(CalendarEvent event) {
                if (hasEventListener(CalendarEventId.EVENTCLICK)) {
                    rpc.eventClick(event.getIndex());
                }
            }
        });
        getWidget().setListener(new MouseEventListener() {
            public void contextMenu(ContextMenuEvent event, final Widget widget) {
                final NativeEvent ne = event.getNativeEvent();
                int left = ne.getClientX();
                int top = ne.getClientY();
                top += Window.getScrollTop();
                left += Window.getScrollLeft();
                getClient().getContextMenu().showAt(new ActionOwner() {
                    public String getPaintableId() {
                        return CalendarConnector.this.getPaintableId();
                    }

                    public ApplicationConnection getClient() {
                        return CalendarConnector.this.getClient();
                    }

                    @SuppressWarnings("deprecation")
                    public Action[] getActions() {
                        if (widget instanceof SimpleDayCell) {
                            /*
                             * Month view
                             */
                            SimpleDayCell cell = (SimpleDayCell) widget;
                            Date start = new Date(cell.getDate().getYear(),
                                    cell.getDate().getMonth(), cell.getDate()
                                            .getDate(), 0, 0, 0);

                            Date end = new Date(cell.getDate().getYear(), cell
                                    .getDate().getMonth(), cell.getDate()
                                    .getDate(), 23, 59, 59);

                            return CalendarConnector.this.getActionsBetween(
                                    start, end);
                        } else if (widget instanceof DateCell) {
                            /*
                             * Week and Day view
                             */
                            DateCell cell = (DateCell) widget;
                            int slotIndex = DOM.getChildIndex(
                                    cell.getElement(), (Element) ne
                                            .getEventTarget().cast());
                            DateCellSlot slot = cell.getSlot(slotIndex);
                            return CalendarConnector.this.getActionsBetween(
                                    slot.getFrom(), slot.getTo());
                        } else if (widget instanceof DayEvent) {
                            /*
                             * Context menu on event
                             */
                            DayEvent dayEvent = (DayEvent) widget;
                            CalendarEvent event = dayEvent.getCalendarEvent();
                            Action[] actions = CalendarConnector.this
                                    .getActionsBetween(event.getStartTime(),
                                            event.getEndTime());
                            for (Action action : actions) {
                                ((VCalendarAction) action).setEvent(event);
                            }
                            return actions;

                        }
                        return null;
                    }
                }, left, top);
            }
        });
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        CalendarState state = getState();
        VCalendar widget = getWidget();
        boolean monthView = state.getDays().size() > 7;

        // Enable or disable the forward and backward navigation buttons
        widget.setForwardNavigationEnabled(hasEventListener(CalendarEventId.FORWARD));
        widget.setBackwardNavigationEnabled(hasEventListener(CalendarEventId.BACKWARD));

        widget.set24HFormat(state.isFormat24H());
        widget.setDayNames(state.getDayNames());
        widget.setMonthNames(state.getMonthNames());
        widget.setFirstDayNumber(state.getFirstVisibleDayOfWeek());
        widget.setLastDayNumber(state.getLastVisibleDayOfWeek());
        widget.setFirstHourOfTheDay(state.getFirstHourOfDay());
        widget.setLastHourOfTheDay(state.getLastHourOfDay());
        widget.setReadOnly(state.readOnly);
        widget.setDisabled(!state.enabled);

        widget.setRangeSelectAllowed(hasEventListener(CalendarEventId.RANGESELECT));
        widget.setRangeMoveAllowed(hasEventListener(CalendarEventId.EVENTMOVE));
        widget.setEventMoveAllowed(hasEventListener(CalendarEventId.EVENTMOVE));
        widget.setEventResizeAllowed(hasEventListener(CalendarEventId.EVENTRESIZE));

        List<CalendarState.Day> days = state.getDays();
        List<CalendarState.Event> events = state.getEvents();

        if (monthView) {
            updateMonthView(days, events);
        } else {
            updateWeekView(days, events);
        }

        updateSizes();

        registerEventToolTips(state.getEvents());
        updateActionMap(state.getActions());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.Paintable#updateFromUIDL(com.vaadin.terminal
     * .gwt.client.UIDL, com.vaadin.terminal.gwt.client.ApplicationConnection)
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        // check for DD -related access criteria
        // Iterator<Object> childIterator = uidl.getChildIterator();
        // while (childIterator.hasNext()) {
        // UIDL child = (UIDL) childIterator.next();
        //
        // // Drag&drop
        // if (ACCESSCRITERIA.equals(child.getTag())) {
        // if (monthView
        // && !(getDropHandler() instanceof CalendarMonthDropHandler)) {
        // setDropHandler(new CalendarMonthDropHandler());
        //
        // } else if (!monthView
        // && !(getDropHandler() instanceof CalendarWeekDropHandler)) {
        // setDropHandler(new CalendarWeekDropHandler());
        // }
        //
        // getDropHandler().setCalendarPaintable(this);
        // getDropHandler().updateAcceptRules(child);
        //
        // } else {
        // setDropHandler(null);
        // }
        //
        // }
    }

    /**
     * Returns the ApplicationConnection used to connect to the server side
     */
    public ApplicationConnection getClient() {
        return getConnection();
    }

    /**
     * Register the description of the events as tooltips. This way, any event
     * displaying widget can use the event index as a key to display the
     * tooltip.
     */
    private void registerEventToolTips(List<CalendarState.Event> events) {
        for (CalendarState.Event e : events) {
            if (e.getDescription() != null && !"".equals(e.getDescription())) {
                tooltips.put(e.getIndex(), e.getDescription());
            } else {
                tooltips.remove(e.getIndex());
            }
        }
    }

    @Override
    public TooltipInfo getTooltipInfo(com.google.gwt.dom.client.Element element) {
        TooltipInfo tooltipInfo = null;
        Widget w = Util.findWidget((Element) element, null);
        if (w instanceof HasTooltipKey) {
            tooltipInfo = GWT.create(TooltipInfo.class);
            String title = tooltips.get(((HasTooltipKey) w).getTooltipKey());
            tooltipInfo.setTitle(title != null ? title : "");
        }
        if (tooltipInfo == null) {
            tooltipInfo = super.getTooltipInfo(element);
        }
        return tooltipInfo;
    }

    private void updateMonthView(List<CalendarState.Day> days,
            List<CalendarState.Event> events) {
        CalendarState state = getState();
        getWidget().updateMonthView(state.getFirstDayOfWeek(),
                getWidget().getDateTimeFormat().parse(state.getNow()),
                days.size(), calendarEventListOf(events, state.isFormat24H()),
                calendarDayListOf(days));
    }

    private void updateWeekView(List<CalendarState.Day> days,
            List<CalendarState.Event> events) {
        CalendarState state = getState();
        getWidget().updateWeekView(state.getScroll(),
                getWidget().getDateTimeFormat().parse(state.getNow()),
                days.size(), state.getFirstDayOfWeek(),
                calendarEventListOf(events, state.isFormat24H()),
                calendarDayListOf(days));
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

    private Action[] getActionsBetween(Date start, Date end) {
        List<Action> actions = new ArrayList<Action>();
        for (int i = 0; i < actionKeys.size(); i++) {
            final String actionKey = actionKeys.get(i);
            Date actionStartDate;
            Date actionEndDate;
            try {
                actionStartDate = getActionStartDate(actionKey);
                actionEndDate = getActionEndDate(actionKey);
            } catch (ParseException pe) {
                VConsole.error("Failed to parse action date");
                continue;
            }

            boolean startIsValid = start.compareTo(actionStartDate) >= 0;
            boolean endIsValid = end.compareTo(actionEndDate) <= 0;
            if (startIsValid && endIsValid) {
                VCalendarAction a = new VCalendarAction(this, rpc, actionKey);
                a.setCaption(getActionCaption(actionKey));
                a.setIconUrl(getActionIcon(actionKey));
                a.setActionStartDate(start);
                a.setActionEndDate(end);
                actions.add(a);
            }
        }

        return actions.toArray(new Action[actions.size()]);
    }

    private List<String> actionKeys = new ArrayList<String>();

    private void updateActionMap(List<CalendarState.Action> actions) {
        actionMap.clear();
        actionKeys.clear();

        if (actions == null) {
            return;
        }

        for (CalendarState.Action action : actions) {
            String id = action.getActionKey() + "-" + action.getStartDate()
                    + "-" + action.getEndDate();
            actionMap.put(id + "_c", action.getCaption());
            actionMap.put(id + "_s", action.getStartDate());
            actionMap.put(id + "_e", action.getEndDate());
            actionKeys.add(id);
            if (action.getIconKey() != null) {
                actionMap.put(id + "_i", getResourceUrl(action.getIconKey()));

            } else {
                actionMap.remove(id + "_i");
            }
        }
    }

    /**
     * Get the text that is displayed for a context menu item
     * 
     * @param actionKey
     *            The unique action key
     * @return
     */
    public String getActionCaption(String actionKey) {
        return actionMap.get(actionKey + "_c");
    }

    /**
     * Get the icon url for a context menu item
     * 
     * @param actionKey
     *            The unique action key
     * @return
     */
    public String getActionIcon(String actionKey) {
        return actionMap.get(actionKey + "_i");
    }

    /**
     * Get the start date for an action item
     * 
     * @param actionKey
     *            The unique action key
     * @return
     * @throws ParseException
     */
    public Date getActionStartDate(String actionKey) throws ParseException {
        String dateStr = actionMap.get(actionKey + "_s");
        DateTimeFormat formatter = DateTimeFormat
                .getFormat(VCalendarAction.ACTION_DATE_FORMAT_PATTERN);
        return formatter.parse(dateStr);
    }

    /**
     * Get the end date for an action item
     * 
     * @param actionKey
     *            The unique action key
     * @return
     * @throws ParseException
     */
    public Date getActionEndDate(String actionKey) throws ParseException {
        String dateStr = actionMap.get(actionKey + "_e");
        DateTimeFormat formatter = DateTimeFormat
                .getFormat(VCalendarAction.ACTION_DATE_FORMAT_PATTERN);
        return formatter.parse(dateStr);
    }

    /**
     * Returns ALL currently registered events. Use {@link #getActions(Date)} to
     * get the actions for a specific date
     */
    public Action[] getActions() {
        List<Action> actions = new ArrayList<Action>();
        for (int i = 0; i < actionKeys.size(); i++) {
            final String actionKey = actionKeys.get(i);
            final VCalendarAction a = new VCalendarAction(this, rpc, actionKey);
            a.setCaption(getActionCaption(actionKey));
            a.setIconUrl(getActionIcon(actionKey));

            try {
                a.setActionStartDate(getActionStartDate(actionKey));
                a.setActionEndDate(getActionEndDate(actionKey));
            } catch (ParseException pe) {
                VConsole.error(pe);
            }

            actions.add(a);
        }
        return actions.toArray(new Action[actions.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.ActionOwner#getPaintableId()
     */
    public String getPaintableId() {
        return getConnectorId();
    }

    private List<CalendarEvent> calendarEventListOf(
            List<CalendarState.Event> events, boolean format24h) {
        List<CalendarEvent> list = new ArrayList<CalendarEvent>(events.size());
        for (CalendarState.Event event : events) {
            final String dateFrom = event.getDateFrom();
            final String dateTo = event.getDateTo();
            final String timeFrom = event.getTimeFrom();
            final String timeTo = event.getTimeTo();
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setAllDay(event.isAllDay());
            calendarEvent.setCaption(event.getCaption());
            calendarEvent.setDescription(event.getDescription());
            calendarEvent.setStart(getWidget().getDateFormat().parse(dateFrom));
            calendarEvent.setEnd(getWidget().getDateFormat().parse(dateTo));
            calendarEvent.setFormat24h(format24h);
            calendarEvent.setStartTime(getWidget().getDateTimeFormat().parse(
                    dateFrom + " " + timeFrom));
            calendarEvent.setEndTime(getWidget().getDateTimeFormat().parse(
                    dateTo + " " + timeTo));
            calendarEvent.setStyleName(event.getStyleName());
            calendarEvent.setIndex(event.getIndex());
            list.add(calendarEvent);
        }
        return list;
    }

    private List<CalendarDay> calendarDayListOf(List<CalendarState.Day> days) {
        List<CalendarDay> list = new ArrayList<CalendarDay>(days.size());
        for (CalendarState.Day day : days) {
            CalendarDay d = new CalendarDay(day.getDate(),
                    day.getLocalizedDateFormat(), day.getDayOfWeek(),
                    day.getWeek());

            list.add(d);
        }
        return list;
    }

    @Override
    public void layout() {
        updateSizes();
    }

    private void updateSizes() {
        int height = getLayoutManager()
                .getOuterHeight(getWidget().getElement());
        int width = getLayoutManager().getOuterWidth(getWidget().getElement());

        if (isUndefinedWidth()) {
            width = -1;
        }
        if (isUndefinedHeight()) {
            height = -1;
        }

        getWidget().setSizeForChildren(width, height);

    }
}
