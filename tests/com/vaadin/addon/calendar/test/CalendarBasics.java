package com.vaadin.addon.calendar.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.vaadin.addon.calendar.ui.Calendar;

public class CalendarBasics {

    @Test
    public void dummy() {
        Calendar s = new Calendar(new Calendar.EventProvider() {

            public List<Calendar.Event> getEvents(Date fromStartDate,
                    Date toEndDate) {
                return new ArrayList<Calendar.Event>();
            }

        });

        Date startDate = new Date();
        s.setStartDate(startDate);
        s.setEndDate(startDate);

        assert (s.getStartDate().getTime() == startDate.getTime());

    }
}
