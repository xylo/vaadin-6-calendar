package com.vaadin.addon.calendar.demo;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.vaadin.Application;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.Calendar.TimeFormat;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class CalendarTestMonthly extends Application implements
        CalendarEventProvider {

    private static final long serialVersionUID = -5436777475398410597L;

    GregorianCalendar calendar = new GregorianCalendar();

    private Calendar calendarComponent;

    private Date currentMonthsFirstDate = null;

    private Label label = new Label("");

    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        setTheme("calendartest");

        calendarComponent = new Calendar(this);
        calendarComponent.setTimeFormat(TimeFormat.Format24H);

        Date today = new Date();
        calendar.setTime(today);
        calendar.get(GregorianCalendar.MONTH);

        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
        label.setValue(month + " " + calendar.get(GregorianCalendar.YEAR));
        int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
        currentMonthsFirstDate = calendar.getTime();
        calendarComponent.setStartDate(currentMonthsFirstDate);
        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        calendarComponent.setEndDate(calendar.getTime());
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setMargin(true);
        w.setContent(vl);
        w.setSizeFull();

        Button next = new Button("next", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                calendar.setTime(currentMonthsFirstDate);
                calendar.add(GregorianCalendar.MONTH, 1);
                currentMonthsFirstDate = calendar.getTime();
                calendarComponent.setStartDate(currentMonthsFirstDate);
                DateFormatSymbols s = new DateFormatSymbols(getLocale());
                String month = s.getShortMonths()[calendar
                        .get(GregorianCalendar.MONTH)];
                label.setValue(month + " "
                        + calendar.get(GregorianCalendar.YEAR));
                calendar.add(GregorianCalendar.MONTH, 1);
                calendar.add(GregorianCalendar.DATE, -1);
                calendarComponent.setEndDate(calendar.getTime());
            }

        });

        vl.addComponent(label);
        vl.addComponent(next);

        Button prev = new Button("prev", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                calendar.setTime(currentMonthsFirstDate);
                calendar.add(GregorianCalendar.MONTH, -1);
                currentMonthsFirstDate = calendar.getTime();
                calendarComponent.setStartDate(currentMonthsFirstDate);
                DateFormatSymbols s = new DateFormatSymbols(getLocale());
                String month = s.getShortMonths()[calendar
                        .get(GregorianCalendar.MONTH)];
                label.setValue(month + " "
                        + calendar.get(GregorianCalendar.YEAR));
                calendar.add(GregorianCalendar.MONTH, 1);
                calendar.add(GregorianCalendar.DATE, -1);
                calendarComponent.setEndDate(calendar.getTime());
            }

        });

        vl.addComponent(prev);
        vl.addComponent(calendarComponent);
        vl.setExpandRatio(calendarComponent, 1);
    }

    public List<CalendarEvent> getEvents(Date fromStartDate, Date toEndDate) {
        // return getEventsOverlappingForMonthlyTest(fromStartDate, toEndDate);
        return getEventsOverlappingForMonthlyTest(fromStartDate, toEndDate);
    }

    private List<CalendarEvent> getEventsOverlappingForMonthlyTest(
            Date fromStartDate, Date toEndDate) {
        calendar.setTime(fromStartDate);
        calendar.add(GregorianCalendar.DATE, 5);

        List<CalendarEvent> e = new ArrayList<CalendarEvent>();

        CalendarTestEvent event = getNewEvent("Phase1", fromStartDate, calendar
                .getTime());
        event.setDescription("asdgasdgj asdfg adfga fsdgafdsgasdga asdgadfsg");
        event.setStyleName("color1");
        e.add(event);

        calendar.add(GregorianCalendar.DATE, 3);
        Date d = calendar.getTime();
        calendar.add(GregorianCalendar.DATE, 3);
        Date d2 = calendar.getTime();
        event = getNewEvent("Phase2", d, d2);
        event.setStyleName("color2");
        e.add(event);

        calendar.add(GregorianCalendar.DATE, 1);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.DATE, 10);
        d2 = calendar.getTime();
        event = getNewEvent("Phase3", d, d2);
        event.setStyleName("color3");
        e.add(event);
        calendar.add(GregorianCalendar.DATE, -1);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.DATE, 3);
        d2 = calendar.getTime();
        event = getNewEvent("Phase4", d, d2);
        event.setStyleName("color4");
        e.add(event);

        calendar.add(GregorianCalendar.DATE, -1);
        calendar.add(GregorianCalendar.HOUR, -6);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 1);
        d2 = calendar.getTime();
        event = getNewEvent("Session 1", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 1);
        d2 = calendar.getTime();
        event = getNewEvent("Session 2", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = getNewEvent("Session 3", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = getNewEvent("Session 4", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = getNewEvent("Session 5", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = getNewEvent("Session 6", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = getNewEvent("Session 7", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 1);
        d2 = calendar.getTime();
        event = getNewEvent("Session 8", d, d2);

        calendar.add(GregorianCalendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 1);
        d2 = calendar.getTime();
        event = getNewEvent("Session 9", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 1);
        d2 = calendar.getTime();
        event = getNewEvent("Session 10", d, d2);
        e.add(event);
        e.add(event);
        return e;
    }

    private CalendarTestEvent getNewEvent(String caption, Date start, Date end) {
        CalendarTestEvent event = new CalendarTestEvent();
        event.setCaption(caption);
        event.setStart(start);
        event.setEnd(end);

        return event;
    }
}
