/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.addon.calendar.gwt.client.ui.schedule.dd;

import com.vaadin.addon.calendar.gwt.client.ui.CalendarConnector;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.dd.VAbstractDropHandler;

/**
 * Abstract base class for calendar drop handlers.
 * 
 */
public abstract class CalendarDropHandler extends VAbstractDropHandler {

    protected CalendarConnector calendarPaintable;

    /**
     * Set the calendar instance
     * 
     * @param calendarPaintable
     */
    public void setCalendarPaintable(CalendarConnector calendarPaintable) {
        this.calendarPaintable = calendarPaintable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VAbstractDropHandler#getConnector()
     */
    @Override
    public ComponentConnector getConnector() {
        return calendarPaintable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.ui.dd.VDropHandler#getApplicationConnection
     * ()
     */
    public ApplicationConnection getApplicationConnection() {
        return calendarPaintable.getClient();
    }

}
