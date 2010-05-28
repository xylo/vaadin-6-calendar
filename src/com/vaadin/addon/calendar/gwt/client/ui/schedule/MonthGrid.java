/**
 * 
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.user.client.ui.Grid;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;

public class MonthGrid extends Grid {

    private SimpleDayCell selectionStart;
    private SimpleDayCell selectionEnd;
    private VCalendar calendar;
    private boolean rangeSelectDisabled;
    private boolean readOnly;

    public MonthGrid(VCalendar parent, int rows, int columns) {
        super(rows, columns);
        this.calendar = parent;
        setCellSpacing(0);
        setCellPadding(0);
        setStylePrimaryName("v-calendar-month");
    }

    public void setSelectionEnd(SimpleDayCell simpleDayCell) {
        if (simpleDayCell.isEnabled()) {
            selectionEnd = simpleDayCell;
        }
        updateSelection();
    }

    public void setSelectionStart(SimpleDayCell simpleDayCell) {
        if (simpleDayCell.isEnabled() && !rangeSelectDisabled && !readOnly) {
            selectionStart = simpleDayCell;
        }

    }

    private void updateSelection() {
        if (selectionStart == null) {
            return;
        }
        if (selectionStart != null && selectionEnd != null) {
            Date startDate = selectionStart.getDate();
            Date endDate = selectionEnd.getDate();
            for (int row = 0; row < getRowCount(); row++) {
                for (int cell = 0; cell < getCellCount(row); cell++) {
                    SimpleDayCell sdc = (SimpleDayCell) getWidget(row, cell);
                    if (sdc == null)
                        return;
                    Date d = sdc.getDate();
                    if (startDate.compareTo(d) <= 0
                            && endDate.compareTo(d) >= 0) {
                        sdc.addStyleDependentName("selected");
                    } else if (startDate.compareTo(d) >= 0
                            && endDate.compareTo(d) <= 0) {
                        sdc.addStyleDependentName("selected");
                    } else {
                        sdc.removeStyleDependentName("selected");
                    }
                }
            }
        }
    }

    public void setSelectionReady() {
        if (selectionStart != null && selectionEnd != null) {
            String value = "";
            Date startDate = selectionStart.getDate();
            Date endDate = selectionEnd.getDate();
            if (startDate.compareTo(endDate) > 0) {
                Date temp = startDate;
                startDate = endDate;
                endDate = temp;
            }

            value = calendar.getDateFormat().format(startDate) + "TO"
                    + calendar.getDateFormat().format(endDate);
            if (calendar.getClient().hasEventListeners(calendar,
                    CalendarEventId.RANGESELECT)) {
                calendar.getClient().updateVariable(calendar.getPID(),
                        CalendarEventId.RANGESELECT, value, true);
            }

            selectionStart = null;
            selectionEnd = null;
        }
    }

    public void updateCellSizes(int totalWidthPX, int totalHeightPX) {
        if (totalWidthPX > 0 && totalHeightPX > 0) {
            int rows = getRowCount();
            int cells = getCellCount(0);
            int cellWidth = (totalWidthPX / cells) - 1;
            int remainder = totalWidthPX % cells;
            // Division for cells might not be even. Distribute it evenly to
            // will whole space.
            int heightPX = totalHeightPX;
            int cellHeight = heightPX / rows;
            int heightRemainder = heightPX % rows;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cells; j++) {
                    SimpleDayCell sdc = (SimpleDayCell) getWidget(i, j);
                    if (remainder > 0) {
                        sdc.setWidth(cellWidth + 1 + "px");
                        remainder--;
                    } else {
                        sdc.setWidth(cellWidth + "px");
                    }
                    if (heightRemainder > 0) {
                        sdc.setHeightPX(cellHeight + 1, true);
                    } else {
                        sdc.setHeightPX(cellHeight, true);
                    }
                }
                heightRemainder--;
            }
        }
    }

    /**
     * Disable or enable possibility to select ranges
     */
    public void setRangeSelect(boolean b) {
        rangeSelectDisabled = !b;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}