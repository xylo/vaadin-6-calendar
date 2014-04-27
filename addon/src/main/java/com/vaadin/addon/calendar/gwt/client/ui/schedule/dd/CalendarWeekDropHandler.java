/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule.dd;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.WeekGrid.DateCell.DayEvent;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.ui.dd.VAcceptCallback;
import com.vaadin.terminal.gwt.client.ui.dd.VDragEvent;

/**
 * Handles DD when the weekly view is showing in the Calendar. In the weekly
 * view, drops are only allowed in the the time slots for each day. The slot
 * index and the day index are included in the drop details sent to the server.
 * 
 * @version
 * @VERSION@
 */
public class CalendarWeekDropHandler extends CalendarDropHandler {

    private com.google.gwt.user.client.Element currentTargetElement;
    private DateCell currentTargetDay;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragAccepted
     * (com.vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    protected void dragAccepted(VDragEvent drag) {
        deEmphasis();
        currentTargetElement = drag.getElementOver();
        currentTargetDay = Util
                .findWidget(currentTargetElement, DateCell.class);
        emphasis();
    }

    /**
     * Removes the CSS style name from the emphasized element
     */
    private void deEmphasis() {
        if (currentTargetElement != null) {
            currentTargetDay.removeEmphasisStyle(currentTargetElement);
            currentTargetElement = null;
        }
    }

    /**
     * Add a CSS stylen name to current target element
     */
    private void emphasis() {
        currentTargetDay.addEmphasisStyle(currentTargetElement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragOver(com
     * .vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public void dragOver(final VDragEvent drag) {
        if (isLocationValid(drag.getElementOver())) {
            validate(new VAcceptCallback() {
                public void accepted(VDragEvent event) {
                    dragAccepted(drag);
                }
            }, drag);
        }
    }

    /**
     * Checks if the location is a valid drop location
     * 
     * @param elementOver
     *            The element to check
     * @return
     */
    private boolean isLocationValid(
            com.google.gwt.user.client.Element elementOver) {
        com.google.gwt.user.client.Element weekGridElement = calendarPaintable
                .getWeekGrid().getElement();
        com.google.gwt.user.client.Element timeBarElement = calendarPaintable
                .getWeekGrid().getTimeBar().getElement();

        com.google.gwt.user.client.Element todayBarElement = null;
        if (calendarPaintable.getWeekGrid().hasToday()) {
            todayBarElement = (Element) calendarPaintable.getWeekGrid()
                    .getDateCellOfToday().getTodaybarElement();
        }

        // drops are not allowed in:
        // - weekday header
        // - allday event list
        // - todaybar
        // - timebar
        // - events
        return DOM.isOrHasChild(weekGridElement, elementOver)
                && !DOM.isOrHasChild(timeBarElement, elementOver)
                && todayBarElement != elementOver
                && (Util.findWidget(elementOver, DayEvent.class) == null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragEnter(com
     * .vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public void dragEnter(VDragEvent drag) {
        // NOOP, we determine drag acceptance in dragOver
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#drop(com.vaadin
     * .terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public boolean drop(VDragEvent drag) {
        if (isLocationValid(drag.getElementOver())) {
            updateDropDetails(drag);
            deEmphasis();
            return super.drop(drag);

        } else {
            deEmphasis();
            return false;
        }
    }

    /**
     * Update the drop details sent to the server
     * 
     * @param drag
     *            The drag event
     */
    private void updateDropDetails(VDragEvent drag) {
        int slotIndex = currentTargetDay.getSlotIndex(currentTargetElement);
        int dayIndex = calendarPaintable.getWeekGrid().getDateCellIndex(
                currentTargetDay);

        drag.getDropDetails().put("dropDayIndex", dayIndex);
        drag.getDropDetails().put("dropSlotIndex", slotIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#dragLeave(com
     * .vaadin.terminal.gwt.client.ui.dd.VDragEvent)
     */
    @Override
    public void dragLeave(VDragEvent drag) {
        deEmphasis();
        super.dragLeave(drag);
    }
}
