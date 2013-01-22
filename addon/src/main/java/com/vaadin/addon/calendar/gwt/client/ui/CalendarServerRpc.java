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
package com.vaadin.addon.calendar.gwt.client.ui;

import com.vaadin.shared.annotations.Delayed;
import com.vaadin.shared.communication.ServerRpc;

/**
 * 
 * @author Johannes
 * 
 */
public interface CalendarServerRpc extends ServerRpc {
    void eventMove(int eventIndex, String newDate);

    void rangeSelect(String range);

    void forward();

    void backward();

    void dateClick(String date);

    void weekClick(String event);

    void eventClick(int eventIndex);

    void eventResize(int eventIndex, String newStartDate, String newEndDate);

    void actionOnEmptyCell(String actionKey, String startDate, String endDate);

    void actionOnEvent(String actionKey, String startDate, String endDate,
            int eventIndex);

    @Delayed(lastOnly = true)
    void scroll(int scrollPosition);
}
