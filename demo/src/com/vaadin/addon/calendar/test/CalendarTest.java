package com.vaadin.addon.calendar.test;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.Application;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendar;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.Calendar.TimeFormat;
import com.vaadin.addon.calendar.ui.CalendarEvents.BackwardEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.BackwardListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.DateClickListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarEvents.EventClickListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.EventMoveListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.ForwardEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.ForwardListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.MoveEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarEvents.RangeSelectListener;
import com.vaadin.addon.calendar.ui.CalendarEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarEvents.WeekClickListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/** Calendar component test application */
public class CalendarTest extends Application implements Calendar.EventProvider {

    private static final long serialVersionUID = -5436777475398410597L;

    private static final String DEFAULT_ITEMID = "DEFAULT";

    private enum Mode {
        MONTH, WEEK, DAY;
    }

    /**
     * This Gregorian calendar is used to control dates and time inside of this
     * test application.
     */
    private GregorianCalendar calendar;

    /** Target calendar component that this test application is made for. */
    private Calendar calendarComponent;

    private Date currentMonthsFirstDate;

    private Label captionLabel = new Label("");

    private Button monthButton;

    private Button weekButton;

    private Button nextButton;

    private Button prevButton;

    private Select timeZoneSelect;

    private Select formatSelect;

    private Select localeSelect;

    private Button hideWeekendsButton;

    private Button readOnlyButton;

    private Window scheduleEventPopup;

    private final Form scheduleEventForm = new Form();

    private Button deleteEventButton;

    private Button applyEventButton;

    private Mode viewMode = Mode.MONTH;

    private List<Calendar.Event> dataSource = new ArrayList<Calendar.Event>();

    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        setTheme("calendartest");

        // Set default Locale for this application
        setLocale(Locale.getDefault());

        // Initialize locale, timezone and timeformat selects.
        localeSelect = createLocaleSelect();
        timeZoneSelect = createTimeZoneSelect();
        formatSelect = createCalendarFormatSelect();

        initCalendar();

        w.setContent(initLayout(w));
        w.setSizeFull();

        addInitialEvents();
    }

    private void addInitialEvents() {
        Date originalDate = calendar.getTime();

        // Add a event that last a whole week
        Date start = calendarComponent.getFirstDateForWeek(new Date());
        Date end = calendarComponent.getLastDateForWeek(new Date());
        CalendarTestEvent event = new CalendarTestEvent("Whole week event",
                start, end);
        event.setStyleName("color4");
        dataSource.add(event);

        // Add a allday event
        calendar.setTime(start);
        calendar.add(GregorianCalendar.DATE, 3);
        start = calendar.getTime();
        end = start;
        event = new CalendarTestEvent("Allday event", start, end);
        event.setDescription("Some description.");
        event.setStyleName("color3");
        dataSource.add(event);

        calendar.add(GregorianCalendar.DATE, -2);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 30);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 5);
        calendar.set(GregorianCalendar.MINUTE, 0);
        end = calendar.getTime();
        event = new CalendarTestEvent("Appointment", start, end);
        event.setWhere("Office");
        event.setStyleName("color1");
        dataSource.add(event);

        calendar.add(GregorianCalendar.DATE, 1);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 11);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 8);
        end = calendar.getTime();
        event = new CalendarTestEvent("Training", start, end);
        event.setStyleName("color2");
        dataSource.add(event);

        calendar.add(GregorianCalendar.DATE, 4);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 9);
        end = calendar.getTime();
        event = new CalendarTestEvent("Free time", start, end);
        dataSource.add(event);

        calendar.setTime(originalDate);
    }

    private Layout initLayout(Window w) {
        initNavigationButtons();
        initHideWeekEndButton();
        initReadOnlyButtonButton();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setSpacing(true);
        hl.setMargin(false, false, true, false);
        hl.addComponent(prevButton);
        hl.addComponent(captionLabel);
        hl.addComponent(monthButton);
        hl.addComponent(weekButton);
        hl.addComponent(nextButton);
        hl.setComponentAlignment(prevButton, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(monthButton, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(weekButton, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);

        monthButton.setVisible(viewMode == Mode.WEEK);
        weekButton.setVisible(viewMode == Mode.DAY);

        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.setSpacing(true);
        controlPanel.setMargin(false, false, true, false);
        controlPanel.setWidth("100%");
        controlPanel.addComponent(localeSelect);
        controlPanel.addComponent(timeZoneSelect);
        controlPanel.addComponent(formatSelect);
        controlPanel.addComponent(hideWeekendsButton);
        controlPanel.addComponent(readOnlyButton);
        controlPanel.setComponentAlignment(timeZoneSelect,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(formatSelect, Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(localeSelect, Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(hideWeekendsButton,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(readOnlyButton,
                Alignment.MIDDLE_LEFT);
        layout.addComponent(controlPanel);
        layout.addComponent(hl);
        layout.addComponent(calendarComponent);
        layout.setExpandRatio(calendarComponent, 1);
        return layout;
    }

    private void initNavigationButtons() {
        monthButton = new Button("Month view", new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                switchToMonthView();
            }
        });

        weekButton = new Button("Week view", new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                switchToWeekView(calendar.get(GregorianCalendar.WEEK_OF_YEAR),
                        calendar.get(GregorianCalendar.YEAR));
            }
        });

        nextButton = new Button("Next", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                handleNextButtonClick();
            }
        });

        prevButton = new Button("Prev", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                handlePreviousButtonClick();
            }
        });
    }

    private void initHideWeekEndButton() {
        hideWeekendsButton = new Button("Hide weekends");
        hideWeekendsButton.setSwitchMode(true);
        hideWeekendsButton.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                calendarComponent.setHideWeekends((Boolean) event.getButton()
                        .getValue());
            }
        });
    }

    private void initReadOnlyButtonButton() {
        readOnlyButton = new Button("Read-only mode");
        readOnlyButton.setSwitchMode(true);
        readOnlyButton.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                calendarComponent.setReadOnly((Boolean) event.getButton()
                        .getValue());
            }
        });
    }

    private void initCalendar() {
        calendarComponent = new Calendar(this);
        calendarComponent.setHideWeekends(false);
        calendarComponent.setLocale(getLocale());
        calendarComponent.setImmediate(true);

        Date today = new Date();
        calendar = new GregorianCalendar(getLocale());
        calendar.setTime(today);

        updateCaptionLabel();

        int rollAmount = calendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
        calendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
        resetTime(false);
        currentMonthsFirstDate = calendar.getTime();
        calendarComponent.setStartDate(currentMonthsFirstDate);
        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        calendarComponent.setEndDate(calendar.getTime());

        addCalendarEventListeners();
    }

    @SuppressWarnings("serial")
    private void addCalendarEventListeners() {
        // Register week clicks by changing the schedules start and end dates.
        calendarComponent.addListener(new WeekClickListener() {

            public void weekClick(WeekClick event) {
                switchToWeekView(event.getWeek(), event.getYear());
            }
        });
        calendarComponent.addListener(new ForwardListener() {

            public void forward(ForwardEvent event) {
            }
        });
        calendarComponent.addListener(new BackwardListener() {

            public void backward(BackwardEvent event) {
            }
        });
        calendarComponent.addListener(new EventClickListener() {

            public void eventClick(EventClick event) {
                showEventPopup(event.getCalendarEvent(), false);
            }
        });
        calendarComponent.addListener(new DateClickListener() {

            public void dateClick(DateClickEvent event) {
                // Calendar start and end dates will be changed.
                handleDateClick(event.getDate());
            }
        });

        calendarComponent.addListener(new RangeSelectListener() {

            public void rangeSelect(RangeSelectEvent event) {
                handleRangeSelect(event);
            }
        });

        calendarComponent.addListener(new EventMoveListener() {

            public void eventMove(MoveEvent event) {
                applyEventMove(event.getCalendarEvent(), event.getNewStart());
            }
        });
    }

    private Select createTimeZoneSelect() {
        Select s = new Select("Timezone");
        s.addContainerProperty("caption", String.class, "");
        s.setItemCaptionPropertyId("caption");
        s.setFilteringMode(Select.FILTERINGMODE_CONTAINS);

        Item i = s.addItem(DEFAULT_ITEMID);
        i.getItemProperty("caption").setValue(
                "Default (" + TimeZone.getDefault().getID() + ")");
        for (String id : TimeZone.getAvailableIDs()) {
            if (!s.containsId(id)) {
                i = s.addItem(id);
                i.getItemProperty("caption").setValue(id);
            }
        }

        s.select(DEFAULT_ITEMID);
        s.setImmediate(true);
        s.addListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            public void valueChange(ValueChangeEvent event) {

                updateCalendarTimeZone(event.getProperty().getValue());
            }
        });

        return s;
    }

    private Select createCalendarFormatSelect() {
        Select s = new Select("Calendar format");
        s.addContainerProperty("caption", String.class, "");
        s.setItemCaptionPropertyId("caption");

        Item i = s.addItem(DEFAULT_ITEMID);
        i.getItemProperty("caption").setValue("Default by locale");
        i = s.addItem(TimeFormat.Format12H);
        i.getItemProperty("caption").setValue("12H");
        i = s.addItem(TimeFormat.Format24H);
        i.getItemProperty("caption").setValue("24H");

        s.select(DEFAULT_ITEMID);
        s.setImmediate(true);
        s.addListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            public void valueChange(ValueChangeEvent event) {
                updateCalendarFormat(event.getProperty().getValue());
            }
        });

        return s;
    }

    private Select createLocaleSelect() {
        Select s = new Select("Locale");
        s.addContainerProperty("caption", String.class, "");
        s.setItemCaptionPropertyId("caption");
        s.setFilteringMode(Select.FILTERINGMODE_CONTAINS);

        for (Locale l : Locale.getAvailableLocales()) {
            if (!s.containsId(l)) {
                Item i = s.addItem(l);
                i.getItemProperty("caption").setValue(getLocaleItemCaption(l));
            }
        }

        s.select(getLocale());
        s.setImmediate(true);
        s.addListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            public void valueChange(ValueChangeEvent event) {
                updateCalendarLocale((Locale) event.getProperty().getValue());
            }
        });

        return s;
    }

    private void updateCalendarTimeZone(Object timezoneId) {
        TimeZone tz = null;
        if (!DEFAULT_ITEMID.equals(timezoneId)) {
            tz = TimeZone.getTimeZone((String) timezoneId);
        }
        calendarComponent.setTimeZone(tz);
        calendar.setTimeZone(calendarComponent.getTimeZone());
    }

    private void updateCalendarFormat(Object format) {
        TimeFormat calFormat = null;
        if (format instanceof TimeFormat) {
            calFormat = (TimeFormat) format;
        }
        calendarComponent.setTimeFormat(calFormat);
    }

    private String getLocaleItemCaption(Locale l) {
        String country = l.getDisplayCountry(getLocale());
        String language = l.getDisplayLanguage(getLocale());
        StringBuilder caption = new StringBuilder(country);
        if (caption.length() != 0) {
            caption.append(", ");
        }
        caption.append(language);
        return caption.toString();
    }

    private void updateCalendarLocale(Locale l) {
        calendarComponent.setLocale(l);
        calendar = new GregorianCalendar(l);
    }

    private void handleNextButtonClick() {
        switch (viewMode) {
        case MONTH:
            nextMonth();
            break;
        case WEEK:
            nextWeek();
            break;
        case DAY:
            nextDay();
            break;
        }
    }

    private void handlePreviousButtonClick() {
        switch (viewMode) {
        case MONTH:
            previousMonth();
            break;
        case WEEK:
            previousWeek();
            break;
        case DAY:
            previousDay();
            break;
        }
    }

    private void handleDateClick(Date date) {
        calendar.setTime(date);
        switchToDayView(calendar.get(GregorianCalendar.DATE), calendar
                .get(GregorianCalendar.YEAR));
    }

    private void handleRangeSelect(RangeSelectEvent event) {
        Date start = event.getStart();
        Date end = event.getEnd();
        if (!event.isMonthlyMode()
                && event.getEnd().getTime() - event.getStart().getTime() == VCalendar.DAYINMILLIS) {
            /*
             * A whole day was selected in the weekly view. Lets create a
             * full-day event by setting start and end dates to the same date
             * with a zero length time range. Otherwise event would be shown as
             * a two days long event because its start and end days would be
             * different.
             */
            end = (Date) start.clone();
        }
        showEventPopup(createNewEvent(start, end), true);
    }

    private void applyEventMove(Calendar.Event event, Date newFromDatetime) {
        if (event instanceof CalendarTestEvent) {
            CalendarTestEvent e = (CalendarTestEvent) event;
            /* Update event dates */
            long length = e.getEnd().getTime() - e.getStart().getTime();
            e.setStart(newFromDatetime);
            e.setEnd(new Date(newFromDatetime.getTime() + length));
            calendarComponent.requestRepaint();
        }
    }

    private void showEventPopup(Calendar.Event event, boolean newEvent) {
        if (event == null) {
            return;
        }

        updateCalendarEventPopup(newEvent);
        updateCalendarEventForm(event);

        if (!getMainWindow().getChildWindows().contains(scheduleEventPopup))
            getMainWindow().addWindow(scheduleEventPopup);
    }

    /* Initializes a modal window to edit schedule event. */
    private void createCalendarEventPopup() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        scheduleEventPopup = new Window(null, layout);
        scheduleEventPopup.setWidth("400px");
        scheduleEventPopup.setModal(true);
        scheduleEventPopup.center();

        layout.addComponent(scheduleEventForm);

        applyEventButton = new Button("Apply", new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                commitCalendarEvent();
            }
        });
        Button cancel = new Button("Cancel", new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                discardCalendarEvent();
            }
        });
        deleteEventButton = new Button("Delete", new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                deleteCalendarEvent();
            }
        });
        scheduleEventPopup.addListener(new CloseListener() {

            private static final long serialVersionUID = 1L;

            public void windowClose(CloseEvent e) {
                discardCalendarEvent();
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        buttons.addComponent(deleteEventButton);
        buttons.addComponent(applyEventButton);
        buttons.addComponent(cancel);
        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
    }

    private void updateCalendarEventPopup(boolean newEvent) {
        if (scheduleEventPopup == null) {
            createCalendarEventPopup();
        }

        if (newEvent) {
            scheduleEventPopup.setCaption("New event");
        } else {
            scheduleEventPopup.setCaption("Edit event");
        }

        deleteEventButton.setVisible(!newEvent);
        deleteEventButton.setEnabled(!calendarComponent.isReadOnly());
        applyEventButton.setEnabled(!calendarComponent.isReadOnly());
    }

    private void updateCalendarEventForm(Calendar.Event event) {
        // Lets create a Calendar.Event BeanItem and pass it to the form's data
        // source.
        BeanItem<Calendar.Event> item = new BeanItem<Calendar.Event>(event);
        scheduleEventForm.setWriteThrough(false);
        scheduleEventForm.setItemDataSource(item);
        scheduleEventForm.setFormFieldFactory(new FormFieldFactory() {

            private static final long serialVersionUID = 1L;

            public Field createField(Item item, Object propertyId,
                    Component uiContext) {
                if (propertyId.equals("caption")) {
                    TextField f = createTextField("Caption");
                    f.focus();
                    return f;

                } else if (propertyId.equals("where")) {
                    return createTextField("Where");

                } else if (propertyId.equals("description")) {
                    TextField f = createTextField("Description");
                    f.setRows(3);
                    return f;

                } else if (propertyId.equals("styleName")) {
                    return createStyleNameSelect();

                } else if (propertyId.equals("start")) {
                    return createDateField("Start date");
                } else if (propertyId.equals("end")) {
                    return createDateField("End date");
                }
                return null;
            }

            private TextField createTextField(String caption) {
                TextField f = new TextField(caption);
                f.setNullRepresentation("");
                return f;
            }

            private DateField createDateField(String caption) {
                DateField f = new DateField(caption);
                f.setResolution(DateField.RESOLUTION_MIN);
                return f;
            }

            private Select createStyleNameSelect() {
                Select s = new Select("Color");
                s.addContainerProperty("c", String.class, "");
                s.setItemCaptionPropertyId("c");
                Item i = s.addItem("color1");
                i.getItemProperty("c").setValue("Green");
                i = s.addItem("color2");
                i.getItemProperty("c").setValue("Blue");
                i = s.addItem("color3");
                i.getItemProperty("c").setValue("Red");
                i = s.addItem("color4");
                i.getItemProperty("c").setValue("Orange");
                return s;
            }
        });

        scheduleEventForm.setVisibleItemProperties(new Object[] { "start",
                "end", "caption", "where", "description", "styleName" });
    }

    private Calendar.Event createNewEvent(Date startDate, Date endDate) {

        CalendarTestEvent event = new CalendarTestEvent("", startDate, endDate);
        event.setStyleName("color1");
        return event;
    }

    /* Removes the event from the data source and requests repaint. */
    private void deleteCalendarEvent() {
        Calendar.Event event = getFormCalendarEvent();
        if (dataSource.contains(event)) {
            dataSource.remove(event);
        }
        getMainWindow().removeWindow(scheduleEventPopup);
        calendarComponent.requestRepaint();
    }

    /* Adds/updates the event in the data source and requests repaint. */
    private void commitCalendarEvent() {
        scheduleEventForm.commit();
        Calendar.Event event = getFormCalendarEvent();
        if (!dataSource.contains(event)) {
            dataSource.add(event);
        }

        getMainWindow().removeWindow(scheduleEventPopup);
        calendarComponent.requestRepaint();
    }

    private void discardCalendarEvent() {
        scheduleEventForm.discard();
        getMainWindow().removeWindow(scheduleEventPopup);
    }

    @SuppressWarnings("unchecked")
    private Calendar.Event getFormCalendarEvent() {
        BeanItem<Calendar.Event> item = (BeanItem<Calendar.Event>) scheduleEventForm
                .getItemDataSource();
        Calendar.Event event = item.getBean();
        return event;
    }

    private void nextMonth() {
        rollMonth(1);
    }

    private void previousMonth() {
        rollMonth(-1);
    }

    private void nextWeek() {
        rollWeek(1);
    }

    private void previousWeek() {
        rollWeek(-1);
    }

    private void nextDay() {
        rollDate(1);
    }

    private void previousDay() {
        rollDate(-1);
    }

    private void rollMonth(int direction) {
        calendar.setTime(currentMonthsFirstDate);
        calendar.add(GregorianCalendar.MONTH, direction);
        resetTime(false);
        currentMonthsFirstDate = calendar.getTime();
        calendarComponent.setStartDate(currentMonthsFirstDate);

        updateCaptionLabel();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        resetCalendarTime(true);
    }

    private void rollWeek(int direction) {
        calendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
        calendar.set(GregorianCalendar.DAY_OF_WEEK, calendar
                .getFirstDayOfWeek());
        resetCalendarTime(false);
        resetTime(true);
        calendar.add(GregorianCalendar.DATE, 6);
        calendarComponent.setEndDate(calendar.getTime());
    }

    private void rollDate(int direction) {
        calendar.add(GregorianCalendar.DATE, direction);
        resetCalendarTime(false);
        resetCalendarTime(true);
    }

    private void updateCaptionLabel() {
        DateFormatSymbols s = new DateFormatSymbols(getLocale());
        String month = s.getShortMonths()[calendar.get(GregorianCalendar.MONTH)];
        captionLabel.setValue(month + " "
                + calendar.get(GregorianCalendar.YEAR));
    }

    /*
     * Switch the Calendar component's start and end date range to the target
     * week only. (sample range: 04.01.2010 00:00.000 - 10.01.2010 23:59.999)
     */
    public void switchToWeekView(int week, int year) {
        viewMode = Mode.WEEK;
        weekButton.setVisible(false);
        monthButton.setVisible(true);

        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.WEEK_OF_YEAR, week);
        calendar.set(GregorianCalendar.DAY_OF_WEEK, calendar
                .getFirstDayOfWeek());
        resetCalendarTime(false);
        resetTime(true);
        calendar.add(GregorianCalendar.DATE, 6);
        calendarComponent.setEndDate(calendar.getTime());
    }

    /*
     * Switch the Calendar component's start and end date range to the target
     * month only. (sample range: 01.01.2010 00:00.000 - 31.01.2010 23:59.999)
     */
    public void switchToMonthView() {
        viewMode = Mode.MONTH;
        monthButton.setVisible(false);
        weekButton.setVisible(false);

        calendar.setTime(currentMonthsFirstDate);
        calendarComponent.setStartDate(currentMonthsFirstDate);

        updateCaptionLabel();

        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.add(GregorianCalendar.DATE, -1);
        resetCalendarTime(true);
    }

    /*
     * Switch the Calendar component's start and end date range to the target
     * day only. (sample range: 01.01.2010 00:00.000 - 01.01.2010 23:59.999)
     */
    public void switchToDayView(int date, int year) {
        viewMode = Mode.DAY;
        monthButton.setVisible(true);
        weekButton.setVisible(true);

        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.DATE, date);
        resetCalendarTime(false);
        resetCalendarTime(true);
    }

    private void resetCalendarTime(boolean resetEndTime) {
        resetTime(resetEndTime);
        if (resetEndTime) {
            calendarComponent.setEndDate(calendar.getTime());
        } else {
            calendarComponent.setStartDate(calendar.getTime());
            updateCaptionLabel();
        }
    }

    /*
     * Resets the calendar time (hour, minute second and millisecond) either to
     * zero or maximum value.
     */
    private void resetTime(boolean max) {
        if (max) {
            calendar.set(GregorianCalendar.HOUR_OF_DAY, calendar
                    .getMaximum(GregorianCalendar.HOUR_OF_DAY));
            calendar.set(GregorianCalendar.MINUTE, calendar
                    .getMaximum(GregorianCalendar.MINUTE));
            calendar.set(GregorianCalendar.SECOND, calendar
                    .getMaximum(GregorianCalendar.SECOND));
            calendar.set(GregorianCalendar.MILLISECOND, calendar
                    .getMaximum(GregorianCalendar.MILLISECOND));
        } else {
            calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
            calendar.set(GregorianCalendar.MINUTE, 0);
            calendar.set(GregorianCalendar.SECOND, 0);
            calendar.set(GregorianCalendar.MILLISECOND, 0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.Calendar.EventProvider#getEvents(java.util
     * .Date, java.util.Date)
     */
    public List<Calendar.Event> getEvents(Date fromStartDate, Date toEndDate) {
        ArrayList<Calendar.Event> activeEvents = new ArrayList<Calendar.Event>();

        for (Calendar.Event ev : dataSource) {
            long from = fromStartDate.getTime();
            long to = toEndDate.getTime();

            long f = ev.getStart().getTime();
            long t = ev.getEnd().getTime();
            // Select only events that overlaps with fromStartDate and
            // toEndDate.
            if ((f <= to && f >= from) || (t >= from && t <= to)
                    || (f <= from && t >= to)) {
                activeEvents.add(ev);
            }
        }
        return activeEvents;
    }
}
