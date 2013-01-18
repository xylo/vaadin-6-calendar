package com.vaadin.addon.calendar.gwt.client.ui;

import com.vaadin.shared.communication.ClientRpc;

public interface CalendarClientRpc extends ClientRpc {
    void scroll(int scrollPosition);
}
