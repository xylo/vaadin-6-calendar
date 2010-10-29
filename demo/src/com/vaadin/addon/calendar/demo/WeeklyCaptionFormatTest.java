package com.vaadin.addon.calendar.demo;

import com.vaadin.Application;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class WeeklyCaptionFormatTest extends Application {

    @Override
    public void init() {
        Calendar calendar = new Calendar();
        calendar.setWeeklyCaptionFormat("MMM d, ''yy");

        Window window = new Window();
        window.addComponent(calendar);

        setMainWindow(window);
    }
}
