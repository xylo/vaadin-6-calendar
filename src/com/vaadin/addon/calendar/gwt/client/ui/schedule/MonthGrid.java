/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

import java.util.Date;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.terminal.gwt.client.BrowserInfo;

public class MonthGrid extends FocusableGrid implements KeyDownHandler {

    private SimpleDayCell selectionStart;
    private SimpleDayCell selectionEnd;
    private final VCalendar calendar;
    private boolean rangeSelectDisabled;
    private boolean disabled;
    private final HandlerRegistration keyDownHandler;

    public MonthGrid(VCalendar parent, int rows, int columns) {
        super(rows, columns);
        calendar = parent;
        setCellSpacing(0);
        setCellPadding(0);
        setStylePrimaryName("v-calendar-month");

        keyDownHandler = addKeyDownHandler(this);
    }

    @Override
    protected void onUnload() {
        keyDownHandler.removeHandler();
        super.onUnload();
    }

    public void setSelectionEnd(SimpleDayCell simpleDayCell) {
        selectionEnd = simpleDayCell;
        updateSelection();
    }

    public void setSelectionStart(SimpleDayCell simpleDayCell) {
        if (!rangeSelectDisabled && !isDisabled()) {
            selectionStart = simpleDayCell;
            setFocus(true);
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
                    if (sdc == null) {
                        return;
                    }
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

            if (calendar.getRangeSelectListener() != null) {
                value = calendar.getDateFormat().format(startDate) + "TO"
                        + calendar.getDateFormat().format(endDate);
                calendar.getRangeSelectListener().rangeSelected(value);
            }
            selectionStart = null;
            selectionEnd = null;
            setFocus(false);
        }
    }

    public void cancelRangeSelection() {
        if (selectionStart != null && selectionEnd != null) {
            for (int row = 0; row < getRowCount(); row++) {
                for (int cell = 0; cell < getCellCount(row); cell++) {
                    SimpleDayCell sdc = (SimpleDayCell) getWidget(row, cell);
                    if (sdc == null) {
                        return;
                    }
                    sdc.removeStyleDependentName("selected");
                }
            }
        }
        setFocus(false);
        selectionStart = null;
    }

    public void updateCellSizes(int totalWidthPX, int totalHeightPX) {
        boolean setHeight = totalHeightPX > 0;
        boolean setWidth = totalWidthPX > 0;
        int rows = getRowCount();
        int cells = getCellCount(0);
        int cellWidth = (totalWidthPX / cells) - 1;
        int widthRemainder = totalWidthPX % cells;
        // Division for cells might not be even. Distribute it evenly to
        // will whole space.
        int heightPX = totalHeightPX;
        int cellHeight = heightPX / rows;
        int heightRemainder = heightPX % rows;

        boolean isIE = BrowserInfo.get().isIE7() || BrowserInfo.get().isIE6();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cells; j++) {
                SimpleDayCell sdc = (SimpleDayCell) getWidget(i, j);

                if (setWidth) {
                    if (widthRemainder > 0 && !isIE) {
                        sdc.setWidth(cellWidth + 1 + "px");
                        widthRemainder--;

                    } else {
                        sdc.setWidth(cellWidth + "px");
                    }
                }

                if (setHeight) {
                    if (heightRemainder > 0) {
                        sdc.setHeightPX(cellHeight + 1, true);

                    } else {
                        sdc.setHeightPX(cellHeight, true);
                    }
                } else {
                    sdc.setHeightPX(-1, true);
                }
            }
            heightRemainder--;
        }
    }

    /**
     * Disable or enable possibility to select ranges
     */
    public void setRangeSelect(boolean b) {
        rangeSelectDisabled = !b;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setHeightPX(int intHeight) {
        if (intHeight == -1) {
            addStyleDependentName("sizedheight");

        } else {
            removeStyleDependentName("sizedheight");
        }
    }

    public void setWidthPX(int intWidth) {
        if (intWidth == -1) {
            addStyleDependentName("sizedwidth");

        } else {
            removeStyleDependentName("sizedwidth");
        }
    }

    public void onKeyDown(KeyDownEvent event) {
        int keycode = event.getNativeKeyCode();
        if (KeyCodes.KEY_ESCAPE == keycode && selectionStart != null) {
            cancelRangeSelection();
        }
    }

    public int getDayCellIndex(SimpleDayCell dayCell) {
        int rows = getRowCount();
        int cells = getCellCount(0);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cells; j++) {
                SimpleDayCell sdc = (SimpleDayCell) getWidget(i, j);
                if (dayCell == sdc) {
                    return i * cells + j;
                }
            }
        }

        return -1;
    }
}