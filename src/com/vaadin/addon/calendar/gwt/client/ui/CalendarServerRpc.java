package com.vaadin.addon.calendar.gwt.client.ui;

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

    void scroll(int scrollPosition);
}
