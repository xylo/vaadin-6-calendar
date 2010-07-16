package com.vaadin.addon.calendar.ui;

import java.util.Date;
import java.util.Map;

import com.vaadin.event.dd.DropTarget;
import com.vaadin.event.dd.TargetDetailsImpl;

/**
 * Drop details for {@link com.vaadin.addon.calendar.ui.Calendar Calendar}. When
 * something is dropped on the Calendar, this class contains the specific
 * details of the drop point. Specifically, this class gives access to the date
 * where the drop happened. If the Calendar was in weekly mode, the date also
 * includes the start time of the slot.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 */
public class CalendarTargetDetails extends TargetDetailsImpl {

    private static final long serialVersionUID = -8555345741291476042L;
    private boolean hasDropTime;

    public CalendarTargetDetails(Map<String, Object> rawDropData,
            DropTarget dropTarget) {
        super(rawDropData, dropTarget);
    }

    /**
     * @return true if {@link #getDropTime()} will return a date object with the
     *         time set to the start of the time slot where the drop happened
     */
    public boolean hasDropTime() {
        return hasDropTime;
    }

    protected void setHasDropTime(boolean hasDropTime) {
        this.hasDropTime = hasDropTime;
    }

    /**
     * @return the date where the drop happened
     */
    public Date getDropTime() {
        if (hasDropTime) {
            return (Date) getData("dropTime");
        } else {
            return (Date) getData("dropDay");
        }
    }

    /**
     * @return the {@link com.vaadin.addon.calendar.ui.Calendar Calendar}
     *         instance which was the target of the drop
     */
    public Calendar getTargetCalendar() {
        return (Calendar) getTarget();
    }
}
