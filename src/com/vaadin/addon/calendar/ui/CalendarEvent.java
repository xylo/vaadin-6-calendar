package com.vaadin.addon.calendar.ui;

import com.vaadin.ui.Component;

/** All Calendar events extends this class. */
@SuppressWarnings("serial")
public class CalendarEvent extends Component.Event {

    public CalendarEvent(Component source) {
        super(source);
    }

    @Override
    public Calendar getComponent() {
        return (Calendar) super.getComponent();
    }
}
