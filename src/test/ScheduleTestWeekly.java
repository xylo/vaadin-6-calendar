package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.Application;
import com.vaadin.addon.calendar.ui.Schedule;
import com.vaadin.addon.calendar.ui.Schedule.CalendarFormat;
import com.vaadin.addon.calendar.ui.Schedule.EventReader;
import com.vaadin.addon.calendar.ui.Schedule.ScheduleEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

public class ScheduleTestWeekly extends Application implements EventReader {
    private static final long serialVersionUID = -5436777475398410597L;
    GregorianCalendar calendar = new GregorianCalendar();
    private final long HOURINMILLIS = 60 * 60 * 1000;
    private final long DAYINMILLIS = 24 * HOURINMILLIS;
    private final long WEEKINMILLIS = 7 * DAYINMILLIS;
    private Schedule schedule;
    private Date selectedDate = null;
    private Label label = new Label("");

    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        setTheme("teagle");
        schedule = new Schedule(this);
        for (String s : TimeZone.getAvailableIDs()) {
            System.out.println(s);
        }
        schedule.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
        schedule.setCalendarFormat(CalendarFormat.FORMAT_24H);
        Date today = new Date();
        calendar.setTime(today);
        String cap = calendar.get(Calendar.WEEK_OF_YEAR) + " "
                + calendar.get(Calendar.YEAR);
        label.setValue(cap);
        selectedDate = calendar.getTime();
        schedule.setStartDate(selectedDate);
        calendar.add(Calendar.DATE, 6);
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
                calendar.setTime(selectedDate);
                calendar.add(Calendar.DATE, 7);
                selectedDate = calendar.getTime();
                schedule.setStartDate(selectedDate);
                String cap = calendar.get(Calendar.WEEK_OF_YEAR) + " "
                        + calendar.get(Calendar.YEAR);
                label.setValue(cap);
                calendar.add(Calendar.DATE, 6);
                schedule.setEndDate(calendar.getTime());
            }

        });
        vl.addComponent(label);
        vl.addComponent(next);
        Button prev = new Button("prev", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                calendar.setTime(selectedDate);
                calendar.add(Calendar.MONTH, -1);
                selectedDate = calendar.getTime();
                schedule.setStartDate(selectedDate);
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
        MenuBar menu = new MenuBar();
        MenuItem i = menu.addItem("Weekends hidden", null);
        i.addItem("Show weekends", new Command() {
            private static final long serialVersionUID = -4393494935292771814L;

            @Override
            public void menuSelected(MenuItem selectedItem) {
                schedule.setHideWeekends(false);

            }

        });
        i.addItem("Hide weekends", new Command() {
            private static final long serialVersionUID = -2176043834276176494L;

            @Override
            public void menuSelected(MenuItem selectedItem) {
                schedule.setHideWeekends(true);

            }

        });
        vl.addComponent(menu);
        vl.setComponentAlignment(menu, Alignment.BOTTOM_RIGHT);
        vl.addComponent(schedule);
        vl.setExpandRatio(schedule, 1);
    }

    @Override
    public ArrayList<ScheduleEvent> getEvents(Date fromStartDate, Date toEndDate) {
        calendar.setTime(fromStartDate);
        calendar.add(Calendar.DATE, 1);
        ArrayList<ScheduleEvent> e = new ArrayList<ScheduleEvent>();
        ScheduleEvent event = schedule.new ScheduleEvent("Phase1",
                fromStartDate, calendar.getTime());
        event.setDescription("asdgasdgj asdfg adfga fsdgafdsgasdga asdgadfsg");
        event.setStyleName("color1");
        e.add(event);

        calendar.add(Calendar.DATE, 3);
        Date d = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Phase2", d, d2);
        event.setStyleName("color2");
        e.add(event);

        calendar.add(Calendar.DATE, -5);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 2);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Event 1", d, d2);
        e.add(event);

        calendar.add(Calendar.HOUR, 2);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 3);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Event 2", d, d2);
        e.add(event);

        calendar.add(Calendar.HOUR, -2);
        d = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        calendar.add(Calendar.MINUTE, 15);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Event 3", d, d2);
        e.add(event);

        calendar.add(Calendar.MINUTE, -90);
        d = calendar.getTime();
        calendar.add(Calendar.MINUTE, 5);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 1", d, d2);
        e.add(event);

        calendar.add(Calendar.MINUTE, 250);
        calendar.add(Calendar.DATE, 2);
        d = calendar.getTime();
        calendar.add(Calendar.MINUTE, 5);
        d2 = calendar.getTime();
        event = schedule.new ScheduleEvent("Session 2", d, d2);
        event.setStyleName("test");
        e.add(event);

        return e;
    }
}
