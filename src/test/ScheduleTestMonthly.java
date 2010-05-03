package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.vaadin.Application;
import com.vaadin.addon.calendar.ui.Schedule;
import com.vaadin.addon.calendar.ui.Schedule.CalendarFormat;
import com.vaadin.addon.calendar.ui.Schedule.EventReader;
import com.vaadin.addon.calendar.ui.Schedule.ScheduleEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

public class ScheduleTestMonthly extends Application implements EventReader {
    private static final long serialVersionUID = -5436777475398410597L;
    GregorianCalendar calendar = new GregorianCalendar();
    private final long HOURINMILLIS = 60 * 60 * 1000;
    private final long DAYINMILLIS = 24 * HOURINMILLIS;
    private final long WEEKINMILLIS = 7 * DAYINMILLIS;
    private Schedule schedule;
    private Date currentMonthsFirstDate = null;
    private Label label = new Label("");

    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        setTheme("calendar");
        schedule = new Schedule(this);
        schedule.setCalendarFormat(CalendarFormat.FORMAT_24H);
        Date today = new Date();
        calendar.setTime(today);
        calendar.get(Calendar.MONTH);
        String cap = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.getDefault())
                + " " + calendar.get(Calendar.YEAR);
        label.setValue(cap);
        int rollAmount = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -rollAmount);
        currentMonthsFirstDate = calendar.getTime();
        schedule.setStartDate(currentMonthsFirstDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        schedule.setEndDate(calendar.getTime());
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setMargin(true);
        w.setContent(vl);
        w.setSizeFull();
        Button next = new Button("next", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                calendar.setTime(currentMonthsFirstDate);
                calendar.add(Calendar.MONTH, 1);
                currentMonthsFirstDate = calendar.getTime();
                schedule.setStartDate(currentMonthsFirstDate);
                String cap = calendar.getDisplayName(Calendar.MONTH,
                        Calendar.SHORT, Locale.getDefault())
                        + " " + calendar.get(Calendar.YEAR);
                label.setValue(cap);
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                schedule.setEndDate(calendar.getTime());
            }

        });
        vl.addComponent(label);
        vl.addComponent(next);
        Button prev = new Button("prev", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                calendar.setTime(currentMonthsFirstDate);
                calendar.add(Calendar.MONTH, -1);
                currentMonthsFirstDate = calendar.getTime();
                schedule.setStartDate(currentMonthsFirstDate);
                String cap = calendar.getDisplayName(Calendar.MONTH,
                        Calendar.SHORT, Locale.getDefault())
                        + " " + calendar.get(Calendar.YEAR);
                label.setValue(cap);
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                schedule.setEndDate(calendar.getTime());
            }

        });

        vl.addComponent(prev);
        vl.addComponent(schedule);
        vl.setExpandRatio(schedule, 1);
    }

    @Override
    public ArrayList<ScheduleEvent> getEvents(Date fromStartDate, Date toEndDate) {
        // return getEventsOverlappingForMonthlyTest(fromStartDate, toEndDate);
        return getEventsOverlappingForMonthlyTest(fromStartDate, toEndDate);
    }

    private ArrayList<ScheduleEvent> getEventsOverlappingForMonthlyTest(
            Date fromStartDate, Date toEndDate) {
        calendar.setTime(fromStartDate);
        calendar.add(Calendar.DATE, 5);
        ArrayList<ScheduleEvent> e = new ArrayList<ScheduleEvent>();
        ScheduleEvent event = schedule.new ScheduleEvent("Phase1",
                fromStartDate, calendar.getTime());
        event.setDescription("asdgasdgj asdfg adfga fsdgafdsgasdga asdgadfsg");
        event.setStyleName("color1");
        e.add(event);

        calendar.add(Calendar.DATE, 3);
        Date d = calendar.getTime();
        calendar.add(Calendar.DATE, 3);
        Date d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Phase2", d, d2);
        event.setStyleName("color2");
        e.add(event);

        calendar.add(Calendar.DATE, 1);
        d = calendar.getTime();
        calendar.add(Calendar.DATE, 10);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Phase3", d, d2);
        event.setStyleName("color3");
        e.add(event);
        calendar.add(Calendar.DATE, -1);
        d = calendar.getTime();
        calendar.add(Calendar.DATE, 3);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Phase4", d, d2);
        event.setStyleName("color4");
        e.add(event);

        calendar.add(Calendar.DATE, -1);
        calendar.add(Calendar.HOUR, -6);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 1", d, d2);
        e.add(event);

        calendar.add(Calendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 2", d, d2);
        e.add(event);

        calendar.add(Calendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(Calendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 3", d, d2);
        e.add(event);

        calendar.add(Calendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(Calendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 4", d, d2);
        e.add(event);

        calendar.add(Calendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(Calendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 5", d, d2);
        e.add(event);

        calendar.add(Calendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(Calendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 6", d, d2);
        e.add(event);

        calendar.add(Calendar.MINUTE, 30);
        d = calendar.getTime();
        calendar.add(Calendar.MINUTE, 30);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 7", d, d2);
        e.add(event);

        calendar.add(Calendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 8", d, d2);

        calendar.add(Calendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 9", d, d2);
        e.add(event);

        calendar.add(Calendar.HOUR, 1);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 10", d, d2);
        e.add(event);
        e.add(event);
        return e;
    }
}
