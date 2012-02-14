/*
@VaadinAddonLicenseForJavaFiles@
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
