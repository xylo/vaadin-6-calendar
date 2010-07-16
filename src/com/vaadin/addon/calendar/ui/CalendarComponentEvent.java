package com.vaadin.addon.calendar.ui;

import com.vaadin.ui.Component;

/**
 * All Calendar events extends this class.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * */
@SuppressWarnings("serial")
public class CalendarComponentEvent extends Component.Event {

    public CalendarComponentEvent(Component source) {
        super(source);
    }

    @Override
    public Calendar getComponent() {
        return (Calendar) super.getComponent();
    }
}
