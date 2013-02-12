/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule.dd;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.vaadin.addon.calendar.gwt.client.ui.schedule.SimpleDayCell;
import com.vaadin.client.Util;
import com.vaadin.client.ui.dd.VAcceptCallback;
import com.vaadin.client.ui.dd.VDragEvent;

/**
 * Handles DD when the monthly view is showing in the Calendar. In the monthly
 * view, drops are only allowed in the the day cells. Only the day index is
 * included in the drop details sent to the server.
 */
public class CalendarMonthDropHandler extends CalendarDropHandler {

    private Element currentTargetElement;
    private SimpleDayCell currentTargetDay;

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
        currentTargetDay = Util.findWidget(currentTargetElement,
                SimpleDayCell.class);
        emphasis();
    }

    /**
     * Removed the emphasis CSS style name from the currently emphasized day
     */
    private void deEmphasis() {
        if (currentTargetElement != null && currentTargetDay != null) {
            currentTargetDay.removeEmphasisStyle();
            currentTargetElement = null;
        }
    }

    /**
     * Add CSS style name for the currently emphasized day
     */
    private void emphasis() {
        if (currentTargetElement != null && currentTargetDay != null) {
            currentTargetDay.addEmphasisStyle();
        }
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
     * Checks if the one can perform a drop in a element
     * 
     * @param elementOver
     *            The element to check
     * @return
     */
    private boolean isLocationValid(
            com.google.gwt.user.client.Element elementOver) {
        com.google.gwt.user.client.Element monthGridElement = calendarPaintable
                .getWidget().getMonthGrid().getElement();

        // drops are not allowed in:
        // - weekday header
        // - week number bart
        return DOM.isOrHasChild(monthGridElement, elementOver);
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
     * Updates the drop details sent to the server
     * 
     * @param drag
     *            The drag event
     */
    private void updateDropDetails(VDragEvent drag) {
        int dayIndex = calendarPaintable.getWidget().getMonthGrid()
                .getDayCellIndex(currentTargetDay);

        drag.getDropDetails().put("dropDayIndex", dayIndex);
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
