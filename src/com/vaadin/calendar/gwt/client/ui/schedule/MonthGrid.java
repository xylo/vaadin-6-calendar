/**
 * 
 */
package com.vaadin.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.user.client.ui.Grid;
import com.vaadin.calendar.gwt.client.ui.VSchedule;

public class MonthGrid extends Grid {

    private static final int BORDERHEIGHT = 1;
    private static final int BORDERWIDTH = 1;
    private SimpleDayCell selectionStart;
    private SimpleDayCell selectionEnd;
    private VSchedule schedule;
    private boolean rangeSelectDisabled;
    private boolean readOnly;

    public MonthGrid(VSchedule parent, int rows, int columns) {
        super(rows, columns);
        this.schedule = parent;
        setCellSpacing(0);
        setCellPadding(0);
        setStyleName("v-schedule-month");
    }

    public void setSelectionEnd(SimpleDayCell simpleDayCell) {
        if (simpleDayCell.isEnabled()) {
            this.selectionEnd = simpleDayCell;
        }
        updateSelection();
    }

    public void setSelectionStart(SimpleDayCell simpleDayCell) {
        if (simpleDayCell.isEnabled() && !rangeSelectDisabled && !readOnly) {
            this.selectionStart = simpleDayCell;
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
                    Date d = sdc.getDate();
                    if (startDate.compareTo(d) <= 0
                            && endDate.compareTo(d) >= 0) {
                        selectDay(sdc, true);
                    } else if (startDate.compareTo(d) >= 0
                            && endDate.compareTo(d) <= 0) {
                        selectDay(sdc, true);
                    } else {
                        selectDay(sdc, false);
                    }
                }
            }
        }
    }

    private void selectDay(SimpleDayCell sdc, boolean b) {
        setStyleName(sdc.getElement(), "selected", b);
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

            value = schedule.getDateFormat().format(startDate) + "TO"
                    + schedule.getDateFormat().format(endDate);
            schedule.getClient().updateVariable(schedule.getPID(),
                    "rangeSelect", value, true);

            selectionStart = null;
            selectionEnd = null;
        }
    }

    public void updateCellSizes(int totalWidthPX, int totalHeightPX) {
        if (totalWidthPX > 0 && totalHeightPX > 0) {
            int rows = getRowCount();
            int cells = getCellCount(0);
            int testW = totalWidthPX - (0);
            int cellWidth = testW / cells;
            int remainder = testW % cells;
            // Division for cells might not be even. Distribute it evenly to
            // will whole space.
            String cellWidth2 = (cellWidth + 1) + "px";
            String cellwidth = cellWidth + "px";
            int heightPX = totalHeightPX;
            int cellHeight = heightPX / rows;
            int heightRemainder = heightPX % rows;
            int cellHeight2 = cellHeight + 1;

            for (int i = 0; i < rows; i++) {
                int rowRemainder = remainder;
                for (int j = 0; j < cells; j++) {
                    SimpleDayCell sdc = (SimpleDayCell) getWidget(i, j);
                    if (rowRemainder > 0) {
                        sdc.setWidth(cellWidth2);
                        rowRemainder--;
                    } else {
                        sdc.setWidth(cellwidth);
                    }
                    if (heightRemainder > 0) {
                        sdc.setHeightPX(cellHeight2);
                    } else {
                        sdc.setHeightPX(cellHeight);
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
        return this.readOnly;
    }
}