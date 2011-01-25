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
 */
public class CalendarWeekDropHandler extends CalendarDropHandler {

    private com.google.gwt.user.client.Element currentTargetElement;
    private DateCell currentTargetDay;

    @Override
    protected void dragAccepted(VDragEvent drag) {
        deEmphasis();
        currentTargetElement = drag.getElementOver();
        currentTargetDay = Util
                .findWidget(currentTargetElement, DateCell.class);
        emphasis();
    }

    private void deEmphasis() {
        if (currentTargetElement != null) {
            currentTargetDay.removeEmphasisStyle(currentTargetElement);
            currentTargetElement = null;
        }
    }

    private void emphasis() {
        currentTargetDay.addEmphasisStyle(currentTargetElement);
    }

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

    @Override
    public void dragEnter(VDragEvent drag) {
        // NOOP, we determine drag acceptance in dragOver
    }

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

    private void updateDropDetails(VDragEvent drag) {
        int slotIndex = currentTargetDay.getSlotIndex(currentTargetElement);
        int dayIndex = calendarPaintable.getWeekGrid().getDateCellIndex(
                currentTargetDay);

        drag.getDropDetails().put("dropDayIndex", dayIndex);
        drag.getDropDetails().put("dropSlotIndex", slotIndex);
    }

    @Override
    public void dragLeave(VDragEvent drag) {
        deEmphasis();
        super.dragLeave(drag);
    }
}
