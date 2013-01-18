/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.CalendarEvent;
import com.vaadin.client.ui.Action;

/**
 * Action performed by the calendar
 * 
 * @since 1.3.0
 * @version
 * @VERSION@
 * 
 */
public class VCalendarAction extends Action {

    private CalendarServerRpc rpc;

    private String actionKey = "";

    private Date actionStartDate;

    private Date actionEndDate;

    private CalendarEvent event;

    public static final String ACTION_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final DateTimeFormat dateformat_datetime = DateTimeFormat
            .getFormat(ACTION_DATE_FORMAT_PATTERN);

    /**
     * 
     * @param owner
     */
    public VCalendarAction(CalendarConnector owner) {
        super(owner);
    }

    /**
     * Constructor
     * 
     * @param owner
     *            The owner who trigger this kinds of events
     * @param rpc
     *            The CalendarRpc which is used for executing actions
     * @param key
     *            The unique action key which identifies this particular action
     */
    public VCalendarAction(CalendarConnector owner, CalendarServerRpc rpc,
            String key) {
        this(owner);
        this.rpc = rpc;
        actionKey = key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.Action#execute()
     */
    @Override
    public void execute() {
        String startDate = dateformat_datetime.format(actionStartDate);
        String endDate = dateformat_datetime.format(actionEndDate);

        if (event == null) {
            rpc.actionOnEmptyCell(actionKey.split("-")[0], startDate, endDate);
        } else {
            rpc.actionOnEvent(actionKey.split("-")[0], startDate, endDate,
                    event.getIndex());
        }

        owner.getClient().getContextMenu().hide();
    }

    /**
     * Get the date and time when the action starts
     * 
     * @return
     */
    public Date getActionStartDate() {
        return actionStartDate;
    }

    /**
     * Set the date when the actions start
     * 
     * @param actionStartDate
     *            The date and time when the action starts
     */
    public void setActionStartDate(Date actionStartDate) {
        this.actionStartDate = actionStartDate;
    }

    /**
     * Get the date and time when the action ends
     * 
     * @return
     */
    public Date getActionEndDate() {
        return actionEndDate;
    }

    /**
     * Set the date and time when the action ends
     * 
     * @param actionEndDate
     *            The date and time when the action ends
     */
    public void setActionEndDate(Date actionEndDate) {
        this.actionEndDate = actionEndDate;
    }

    public CalendarEvent getEvent() {
        return event;
    }

    public void setEvent(CalendarEvent event) {
        this.event = event;
    }

}