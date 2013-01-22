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

import com.vaadin.ui.Component;

/**
 * All Calendar events extends this class.
 * 
 * @author Vaadin Ltd.
 * @version
 * @VERSION@
 * */
@SuppressWarnings("serial")
public class CalendarComponentEvent extends Component.Event {

    /**
     * Set the source of the event
     * 
     * @param source
     *            The source calendar
     * 
     */
    public CalendarComponentEvent(Calendar source) {
        super(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.Component.Event#getComponent()
     */
    @Override
    public Calendar getComponent() {
        return (Calendar) super.getComponent();
    }
}
