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
package com.vaadin.addon.calendar.gwt.client.ui.schedule;

/**
 * For Calendar client-side internal use only.
 * 
 * @author Johannes
 * 
 */
public interface HasTooltipKey {
    /**
     * Gets the key associated for the Widget implementing this interface. This
     * key is used for getting a tooltip title identified by the key
     * 
     * @return the tooltip key
     */
    Object getTooltipKey();
}
