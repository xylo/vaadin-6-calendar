package com.vaadin.addon.calendar.gwt.client.ui;

import java.util.Date;

import com.vaadin.terminal.gwt.client.ui.Action;

public class VCalendarAction extends Action {

    private String actionKey = "";

    private Date actionStartDate;

    private Date actionEndDate;

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
                actionKey.split("-")[0] + "," + actionStartDate.getTime() + ","
                        + actionEndDate.getTime(), true);
        owner.getClient().getContextMenu().hide();
    }

    public Date getActionStartDate() {
        return actionStartDate;
    }

    public void setActionStartDate(Date actionStartDate) {
        this.actionStartDate = actionStartDate;
    }

    public Date getActionEndDate() {
        return actionEndDate;
    }

    public void setActionEndDate(Date actionEndDate) {
        this.actionEndDate = actionEndDate;
    }

}
