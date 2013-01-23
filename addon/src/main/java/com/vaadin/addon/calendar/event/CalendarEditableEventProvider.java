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
package com.vaadin.addon.calendar.event;

/**
 * An event provider which allows adding and removing events
 * 
 * @since 1.3.0
 * @version
 * ${pom.version}
 */
public interface CalendarEditableEventProvider extends CalendarEventProvider {

    /**
     * Adds an event to the event provider
     * 
     * @param event
     *            The event to add
     */
    void addEvent(CalendarEvent event);

    /**
     * Removes an event from the event provider
     * 
     * @param event
     *            The event
     */
    void removeEvent(CalendarEvent event);
}
