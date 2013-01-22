/**
 * Copyright (C) 2010 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
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
 * @author Vaadin Ltd.
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

    /**
     * Does the dropped item have a time associated with it
     * 
     * @param hasDropTime
     */
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
