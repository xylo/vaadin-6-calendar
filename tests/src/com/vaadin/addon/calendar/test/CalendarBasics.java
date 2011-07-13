/*
@VaadinAddonLicenseForJavaFiles@
 */
package com.vaadin.addon.calendar.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;

public class CalendarBasics {

    @Test
    public void dummy() {
        Calendar s = new Calendar(new CalendarEventProvider() {

            public List<CalendarEvent> getEvents(Date fromStartDate,
                    Date toEndDate) {
                return new ArrayList<CalendarEvent>();
            }

        });

        Date startDate = new Date();
        s.setStartDate(startDate);
        s.setEndDate(startDate);

        assert (s.getStartDate().getTime() == startDate.getTime());

    }
}
