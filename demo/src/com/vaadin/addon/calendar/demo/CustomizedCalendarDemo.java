package com.vaadin.addon.calendar.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventEditor;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;
import com.vaadin.addon.calendar.ui.handler.BasicBackwardHandler;
import com.vaadin.addon.calendar.ui.handler.BasicDateClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicEventMoveHandler;
import com.vaadin.addon.calendar.ui.handler.BasicEventResizeHandler;
import com.vaadin.addon.calendar.ui.handler.BasicForwardHandler;
import com.vaadin.addon.calendar.ui.handler.BasicWeekClickHandler;
import com.vaadin.annotations.Theme;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.server.WrappedRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Theme("calendartest")
public class CustomizedCalendarDemo extends UI {

    private static final long serialVersionUID = -6310422191810341994L;

    private MyEventProvider provider = new MyEventProvider();

    @Override
    public void init(WrappedRequest request) {
        setLocale(Locale.US);

        final Calendar cal = new Calendar(provider);
        cal.setSizeFull();

        cal.setStartDate(new Date());
        cal.setEndDate(new Date());

        // Make sure the date is in the same year as today
        cal.setHandler(new BasicBackwardHandler() {

            private static final long serialVersionUID = 4473964449621632059L;

            @Override
            protected void setDates(BackwardEvent event, Date start, Date end) {
                java.util.Calendar calendar = event.getComponent()
                        .getInternalCalendar();
                if (isThisYear(calendar, end) && isThisYear(calendar, start)) {
                    super.setDates(event, start, end);
                }
            }
        });

        // Make sure the date is in the same year as today
        cal.setHandler(new BasicForwardHandler() {

            private static final long serialVersionUID = -4718745721954015665L;

            @Override
            protected void setDates(ForwardEvent event, Date start, Date end) {
                java.util.Calendar calendar = event.getComponent()
                        .getInternalCalendar();
                if (isThisYear(calendar, start) && isThisYear(calendar, end)) {
                    super.setDates(event, start, end);
                }
            }
        });

        // Set a date click handler
        cal.setHandler(new BasicDateClickHandler() {

            private static final long serialVersionUID = -5736213235806322345L;

            @Override
            public void dateClick(DateClickEvent event) {
                Calendar cal = event.getComponent();
                long currentCalDateRange = cal.getEndDate().getTime()
                        - cal.getStartDate().getTime();

                if (currentCalDateRange < VCalendar.DAYINMILLIS) {
                    // Change the date range to the current week
                    cal.setStartDate(cal.getFirstDateForWeek(event.getDate()));
                    cal.setEndDate(cal.getLastDateForWeek(event.getDate()));

                } else {
                    // Default behaviour, change date range to one day
                    super.dateClick(event);
                }
            }
        });

        // allow moving to week view by clicking the weeknumber only if the
        // weeks start and end dates are on the current month
        cal.setHandler(new BasicWeekClickHandler() {

            private static final long serialVersionUID = -4996892647238456687L;

            @Override
            protected void setDates(WeekClick event, Date start, Date end) {
                java.util.Calendar calendar = event.getComponent()
                        .getInternalCalendar();
                if (isThisMonth(calendar, start) && isThisMonth(calendar, end)) {
                    super.setDates(event, start, end);
                }
            }
        });

        cal.setHandler(new EventClickHandler() {

            private static final long serialVersionUID = 4548304318112120161L;

            public void eventClick(EventClick event) {
                BasicEvent e = (BasicEvent) event.getCalendarEvent();
                new Notification("Event clicked: " + e.getCaption(), e
                        .getDescription()).show(Page.getCurrent());
            }
        });

        // Set the event move listener
        cal.setHandler(new BasicEventMoveHandler() {

            private static final long serialVersionUID = -3196912587103065037L;
            private java.util.Calendar javaCalendar;

            @Override
            public void eventMove(MoveEvent event) {
                javaCalendar = event.getComponent().getInternalCalendar();
                super.eventMove(event);

            }

            @Override
            protected void setDates(CalendarEventEditor event, Date start,
                    Date end) {
                if (isThisMonth(javaCalendar, start)
                        && isThisMonth(javaCalendar, end)) {
                    super.setDates(event, start, end);
                }
            }
        });

        // Set the drag selection handler
        cal.setHandler(new RangeSelectHandler() {

            private static final long serialVersionUID = 8078355786341501794L;

            public void rangeSelect(RangeSelectEvent event) {
                BasicEvent calendarEvent = new BasicEvent();
                calendarEvent.setStart(event.getStart());
                calendarEvent.setEnd(event.getEnd());

                // Create popup window and add a form in it.
                VerticalLayout layout = new VerticalLayout();
                layout.setMargin(true);
                layout.setSpacing(true);

                final Window w = new Window(null, layout);
                w.setWidth("400px");
                w.setModal(true);
                w.center();

                // Wrap the calendar event to a BeanItem and pass it to the form
                final BeanItem<CalendarEvent> item = new BeanItem<CalendarEvent>(
                        calendarEvent);

                final FieldGroup fieldGroup = new FieldGroup();
                fieldGroup.setBuffered(true);
                fieldGroup.setItemDataSource(item);

                TextField f = new TextField("Caption");
                f.setNullRepresentation("");
                f.focus();
                fieldGroup.bind(f, "caption");

                FormLayout formLayout = new FormLayout();
                formLayout.addComponent(f);

                layout.addComponent(formLayout);

                HorizontalLayout buttons = new HorizontalLayout();
                buttons.setSpacing(true);
                buttons.addComponent(new Button("OK", new ClickListener() {

                    private static final long serialVersionUID = 7174826216293514881L;

                    public void buttonClick(ClickEvent event) {
                        try {
                            fieldGroup.commit();
                        } catch (CommitException e) {
                            e.printStackTrace();
                        }
                        // Update event provider's data source
                        provider.addEvent(item.getBean());
                        // Calendar needs to be repainted
                        cal.markAsDirty();

                        removeWindow(w);
                    }
                }));
                buttons.addComponent(new Button("Cancel", new ClickListener() {

                    private static final long serialVersionUID = 3909972672766063318L;

                    public void buttonClick(ClickEvent event) {
                        removeWindow(w);
                    }
                }));
                layout.addComponent(buttons);
                layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);

                addWindow(w);
            }
        });

        cal.setHandler(new BasicEventResizeHandler() {

            private static final long serialVersionUID = 1801188236742274733L;

            private static final long twelveHoursInMs = 12 * 60 * 60 * 1000;

            @Override
            protected void setDates(CalendarEventEditor event, Date start,
                    Date end) {
                long eventLength = end.getTime() - start.getTime();
                if (eventLength <= twelveHoursInMs) {
                    super.setDates(event, start, end);
                }
            }
        });

        Button monthViewButton = new Button("Show month");
        monthViewButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = -3290020614808289426L;

            public void buttonClick(ClickEvent event) {
                GregorianCalendar gc = new GregorianCalendar(cal.getTimeZone(),
                        cal.getLocale());
                gc.setTime(cal.getStartDate());

                long currentCalDateRange = cal.getEndDate().getTime()
                        - cal.getStartDate().getTime();
                if (currentCalDateRange > (VCalendar.DAYINMILLIS * 7)) {
                    return;
                }

                // Reset calendar's start time to the target month's first day.
                gc.set(GregorianCalendar.DATE,
                        gc.getMinimum(GregorianCalendar.DATE));
                gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
                gc.set(GregorianCalendar.MINUTE, 0);
                gc.set(GregorianCalendar.SECOND, 0);
                gc.set(GregorianCalendar.MILLISECOND, 0);
                cal.setStartDate(gc.getTime());
                gc.add(GregorianCalendar.MONTH, 1);
                gc.add(GregorianCalendar.DATE, -1);
                cal.setEndDate(gc.getTime());

            }
        });

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.addComponent(monthViewButton);
        layout.addComponent(cal);
        layout.setExpandRatio(cal, 1);
        setContent(layout);
    }

    public static boolean isThisYear(java.util.Calendar calendar, Date date) {
        calendar.setTime(new Date());
        int thisYear = calendar.get(java.util.Calendar.YEAR);
        calendar.setTime(date);
        return calendar.get(java.util.Calendar.YEAR) == thisYear;
    }

    public static boolean isThisMonth(java.util.Calendar calendar, Date date) {
        calendar.setTime(new Date());
        int thisMonth = calendar.get(java.util.Calendar.MONTH);
        calendar.setTime(date);
        return calendar.get(java.util.Calendar.MONTH) == thisMonth;
    }

    public static class MyEventProvider implements CalendarEventProvider {

        private static final long serialVersionUID = -3655982234130426761L;

        private List<CalendarEvent> events = new ArrayList<CalendarEvent>();

        public MyEventProvider() {
            events = new ArrayList<CalendarEvent>();
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date());

            Date start = cal.getTime();
            cal.add(GregorianCalendar.HOUR, 5);
            Date end = cal.getTime();
            BasicEvent event = new BasicEvent();
            event.setCaption("My Event");
            event.setDescription("My Event Description");
            event.setStart(start);
            event.setEnd(end);
            events.add(event);
        }

        public void addEvent(CalendarEvent BasicEvent) {
            events.add(BasicEvent);
        }

        public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
            return events;
        }
    }

    public static BasicEvent getEvent(String caption, Date start, Date end) {
        BasicEvent event = new BasicEvent();
        event.setCaption(caption);
        event.setStart(start);
        event.setEnd(end);

        return event;
    }
}
