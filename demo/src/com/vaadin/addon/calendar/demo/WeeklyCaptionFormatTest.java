package com.vaadin.addon.calendar.demo;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class WeeklyCaptionFormatTest extends UI {

    @Override
    public void init(WrappedRequest request) {
        Calendar calendar = new Calendar();
        calendar.setWeeklyCaptionFormat("MMM d, ''yy");

        getContent().addComponent(calendar);
    }
}
