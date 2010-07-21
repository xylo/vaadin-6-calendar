package com.vaadin.addon.calendar.demo;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.vaadin.Application;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.Calendar.TimeFormat;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class CalendarTestWeekly extends Application implements
        CalendarEventProvider {

    private static final long serialVersionUID = -5436777475398410597L;

    GregorianCalendar calendar = new GregorianCalendar();
    private Calendar calendarComponent;
    private Date selectedDate = null;
    private final Label label = new Label("");

    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        setTheme("calendartest");

        calendarComponent = new Calendar(this);
        for (String s : TimeZone.getAvailableIDs()) {
            System.out.println(s);
        }
        calendarComponent.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
        calendarComponent.setTimeFormat(TimeFormat.Format24H);

        Date today = new Date();
        calendar.setTime(today);
        String cap = calendar.get(GregorianCalendar.WEEK_OF_YEAR) + " "
                + calendar.get(GregorianCalendar.YEAR);
        label.setValue(cap);
        selectedDate = calendar.getTime();
        calendarComponent.setStartDate(selectedDate);
        calendar.add(GregorianCalendar.DATE, 6);
        calendarComponent.setEndDate(calendar.getTime());

        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setMargin(true);
        w.setContent(vl);
        w.setSizeFull();
        Button next = new Button("next", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                calendar.setTime(selectedDate);
                calendar.add(GregorianCalendar.DATE, 7);
                selectedDate = calendar.getTime();
                calendarComponent.setStartDate(selectedDate);
                String cap = calendar.get(GregorianCalendar.WEEK_OF_YEAR) + " "
                        + calendar.get(GregorianCalendar.YEAR);
                label.setValue(cap);
                calendar.add(GregorianCalendar.DATE, 6);
                calendarComponent.setEndDate(calendar.getTime());
            }

        });
        vl.addComponent(label);
        vl.addComponent(next);
        Button prev = new Button("prev", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                calendar.setTime(selectedDate);
                calendar.add(GregorianCalendar.MONTH, -1);
                selectedDate = calendar.getTime();
                calendarComponent.setStartDate(selectedDate);
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
        MenuBar menu = new MenuBar();
        MenuItem i = menu.addItem("Weekends hidden", null);
        i.addItem("Show weekends", new Command() {
            private static final long serialVersionUID = -4393494935292771814L;

            public void menuSelected(MenuItem selectedItem) {
                calendarComponent.setVisibleDaysOfWeek(1, 5);
            }

        });
        i.addItem("Hide weekends", new Command() {
            private static final long serialVersionUID = -2176043834276176494L;

            public void menuSelected(MenuItem selectedItem) {
                calendarComponent.setVisibleDaysOfWeek(1, 5);
            }

        });
        vl.addComponent(menu);
        vl.setComponentAlignment(menu, Alignment.BOTTOM_RIGHT);
        vl.addComponent(calendarComponent);
        vl.setExpandRatio(calendarComponent, 1);
    }

    public List<CalendarEvent> getEvents(Date fromStartDate, Date toEndDate) {
        calendar.setTime(fromStartDate);
        calendar.add(GregorianCalendar.DATE, 1);
        ArrayList<CalendarEvent> e = new ArrayList<CalendarEvent>();
        CalendarTestEvent event = getNewEvent("Phase1", fromStartDate, calendar
                .getTime());
        event.setDescription("asdgasdgj asdfg adfga fsdgafdsgasdga asdgadfsg");
        event.setStyleName("color1");
        e.add(event);

        calendar.add(GregorianCalendar.DATE, 3);
        Date d = calendar.getTime();
        calendar.add(GregorianCalendar.DATE, 1);
        Date d2 = calendar.getTime();
        event = getNewEvent("Phase2", d, d2);
        event.setStyleName("color2");
        e.add(event);

        calendar.add(GregorianCalendar.DATE, -5);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 2);
        d2 = calendar.getTime();
        event = getNewEvent("Event 1", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.HOUR, 2);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 3);
        d2 = calendar.getTime();
        event = getNewEvent("Event 2", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.HOUR, -2);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR, 1);
        calendar.add(GregorianCalendar.MINUTE, 15);
        d2 = calendar.getTime();
        event = getNewEvent("Event 3", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.MINUTE, -90);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.MINUTE, 5);
        d2 = calendar.getTime();
        event = getNewEvent("Session 1", d, d2);
        e.add(event);

        calendar.add(GregorianCalendar.MINUTE, 250);
        calendar.add(GregorianCalendar.DATE, 2);
        d = calendar.getTime();
        calendar.add(GregorianCalendar.MINUTE, 5);
        d2 = calendar.getTime();
        event = getNewEvent("Session 2", d, d2);
        event.setStyleName("test");
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
