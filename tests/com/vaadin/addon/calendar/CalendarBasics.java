package com.vaadin.addon.calendar;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.vaadin.addon.calendar.ui.Schedule;
import com.vaadin.addon.calendar.ui.Schedule.EventReader;

public class CalendarBasics {

    @Test
    public void dummy() {
        Schedule s = new Schedule(new EventReader() {

            public ArrayList<ScheduleEvent> getEvents(Date fromStartDate,
                    Date toEndDate) {
                return new ArrayList<ScheduleEvent>();
            }

        });

        Date startDate = new Date();
        s.setStartDate(startDate);
        s.setEndDate(startDate);

        assert (s.getStartDate().getTime() == startDate.getTime());

    }
}
