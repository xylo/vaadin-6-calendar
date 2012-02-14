/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.vaadin.terminal.gwt.client.ui.Action;

/**
 * Action performed by the calendar
 * 
 * @since 1.3.0
 * @version
 * @VERSION@
 * 
 */
public class VCalendarAction extends Action {

    private String actionKey = "";

    private Date actionStartDate;

    private Date actionEndDate;

    public static final String ACTION_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final DateTimeFormat dateformat_datetime = DateTimeFormat
            .getFormat(ACTION_DATE_FORMAT_PATTERN);

    /**
     * 
     * @param owner
     */
    public VCalendarAction(VCalendarPaintable owner) {
        super(owner);
    }

    /**
     * Constructor
     * 
     * @param owner
     *            The owner who trigger this kinds of events
     * @param key
     *            The unique action key which identifies this particular action
     * @param date
     *            The date this action represenets
     */
    public VCalendarAction(VCalendarPaintable owner, String key) {
        this(owner);
        actionKey = key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.ui.Action#execute()
     */
    @Override
    public void execute() {
        owner.getClient().updateVariable(owner.getPaintableId(), "action",
                actionKey.split("-")[0] + ","
                        + dateformat_datetime.format(actionStartDate) + ","
                        + dateformat_datetime.format(actionEndDate), true);
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

}
