package com.vaadin.addon.calendar.demo;

import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.vaadin.Application;
import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.Calendar.TimeFormat;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventClickHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicDateClickHandler;
import com.vaadin.addon.calendar.ui.handler.BasicWeekClickHandler;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/** Calendar component test application */
public class CalendarTest extends Application {

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

    private BasicEventProvider dataSource;

    private Button addNewEvent;

    /*
     * When testBench is set to true, CalendarTest will have static content that
     * is more suitable for Vaadin TestBench testing. Calendar will use a static
     * date Mon 10 Jan 2000. Enable by starting the application with a
     * "testBench" parameter in the URL.
     */
    private boolean testBench = false;

    private String calendarHeight = null;

    private String calendarWidth = null;

    @SuppressWarnings("serial")
    @Override
    public void init() {
        Window w = new Window();
        setMainWindow(w);
        setTheme("calendartest");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);

        w.setContent(layout);
        w.setSizeFull();

        // URL parameters must be handled before constructing layout any
        // further. To get access to the parameters, we use ParameterHandler.
        w.addParameterHandler(new ParameterHandler() {
            public void handleParameters(Map<String, String[]> parameters) {
                if (dataSource == null) {
                    handleURLParams(parameters);
                    // This needs to be called only once per a session after
                    // the first Application init-method call.
                    initContent();
                }
            }
        });
    }

    private void handleURLParams(Map<String, String[]> parameters) {
        testBench = parameters.containsKey("testBench");

        if (parameters.containsKey("width")) {
            calendarWidth = parameters.get("width")[0];
        }

        if (parameters.containsKey("height")) {
            calendarHeight = parameters.get("height")[0];
        }
    }

    public void initContent() {
        // Set default Locale for this application
        if (testBench) {
            setLocale(Locale.US);
        } else {
            setLocale(Locale.getDefault());
        }

        // Initialize locale, timezone and timeformat selects.
        localeSelect = createLocaleSelect();
        timeZoneSelect = createTimeZoneSelect();
        formatSelect = createCalendarFormatSelect();

        initCalendar();
        initLayoutContent();

        addInitialEvents();
    }

    private void addInitialEvents() {
        Date originalDate = calendar.getTime();
        Date today = getToday();
        // Add a event that last a whole week
        Date start = calendarComponent.getFirstDateForWeek(today);
        Date end = calendarComponent.getLastDateForWeek(today);
        CalendarTestEvent event = getNewEvent("Whole week event", start, end);
        event.setAllDay(true);
        event.setStyleName("color4");
        event.setDescription("Description for the whole week event.");
        dataSource.addEvent(event);

        // Add a allday event
        calendar.setTime(start);
        calendar.add(GregorianCalendar.DATE, 3);
        start = calendar.getTime();
        end = start;
        event = getNewEvent("Allday event", start, end);
        event.setAllDay(true);
        event.setDescription("Some description.");
        event.setStyleName("color3");
        dataSource.addEvent(event);

        // Add a second allday event
        calendar.add(GregorianCalendar.DATE, 1);
        start = calendar.getTime();
        end = start;
        event = getNewEvent("Second allday event", start, end);
        event.setAllDay(true);
        event.setDescription("Some description.");
        event.setStyleName("color2");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, -3);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 30);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 5);
        calendar.set(GregorianCalendar.MINUTE, 0);
        end = calendar.getTime();
        event = getNewEvent("Appointment", start, end);
        event.setWhere("Office");
        event.setStyleName("color1");
        event
                .setDescription("A longer description, which should display correctly.");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, 1);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 11);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 8);
        end = calendar.getTime();
        event = getNewEvent("Training", start, end);
        event.setStyleName("color2");
        dataSource.addEvent(event);

        calendar.add(GregorianCalendar.DATE, 4);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 9);
        calendar.set(GregorianCalendar.MINUTE, 0);
        start = calendar.getTime();
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 9);
        end = calendar.getTime();
        event = getNewEvent("Free time", start, end);
        dataSource.addEvent(event);

        calendar.setTime(originalDate);
    }

    private void initLayoutContent() {
        initNavigationButtons();
        initHideWeekEndButton();
        initReadOnlyButtonButton();
        initAddNewEventButton();

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
        controlPanel.addComponent(addNewEvent);

        controlPanel.setComponentAlignment(timeZoneSelect,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(formatSelect, Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(localeSelect, Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(hideWeekendsButton,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(readOnlyButton,
                Alignment.MIDDLE_LEFT);
        controlPanel.setComponentAlignment(addNewEvent, Alignment.MIDDLE_LEFT);

        VerticalLayout layout = (VerticalLayout) getMainWindow().getContent();
        layout.addComponent(controlPanel);
        layout.addComponent(hl);
        layout.addComponent(calendarComponent);
        layout.setExpandRatio(calendarComponent, 1);
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
                // simulate week click
                WeekClickHandler handler = (WeekClickHandler) calendarComponent
                        .getHandler(WeekClick.EVENT_ID);
                handler.weekClick(new WeekClick(calendarComponent, calendar
                        .get(GregorianCalendar.WEEK_OF_YEAR), calendar
                        .get(GregorianCalendar.YEAR)));
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

    public void initAddNewEventButton() {
        addNewEvent = new Button("Add new event");
        addNewEvent.addListener(new Button.ClickListener() {

            private static final long serialVersionUID = -8307244759142541067L;

            public void buttonClick(ClickEvent event) {
                showEventPopup(createNewEvent(new Date(), new Date()), true);
            }

        });
    }

    private void initCalendar() {
        dataSource = new BasicEventProvider();

        calendarComponent = new Calendar(dataSource);
        calendarComponent.setHideWeekends(false);
        calendarComponent.setLocale(getLocale());
        calendarComponent.setImmediate(true);

        if (calendarWidth != null || calendarHeight != null) {
            if (calendarHeight != null) {
                calendarComponent.setHeight(calendarHeight);
            }
            if (calendarWidth != null) {
                calendarComponent.setWidth(calendarWidth);
            }
        } else {
            calendarComponent.setSizeFull();
        }

        Date today = getToday();
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

    private Date getToday() {
        if (testBench) {
            GregorianCalendar testDate = new GregorianCalendar();
            testDate.set(GregorianCalendar.YEAR, 2000);
            testDate.set(GregorianCalendar.MONTH, 0);
            testDate.set(GregorianCalendar.DATE, 10);
            testDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
            testDate.set(GregorianCalendar.MINUTE, 0);
            testDate.set(GregorianCalendar.SECOND, 0);
            testDate.set(GregorianCalendar.MILLISECOND, 0);
            return testDate.getTime();
        }
        return new Date();
    }

    @SuppressWarnings("serial")
    private void addCalendarEventListeners() {
        // Register week clicks by changing the schedules start and end dates.
        calendarComponent.setHandler(new BasicWeekClickHandler() {

            @Override
            public void weekClick(WeekClick event) {
                // let BasicWeekClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.weekClick(event);
                updateCaptionLabel();
                switchToWeekView();
            }
        });

        calendarComponent.setHandler(new EventClickHandler() {

            public void eventClick(EventClick event) {
                showEventPopup(event.getCalendarEvent(), false);
            }
        });

        calendarComponent.setHandler(new BasicDateClickHandler() {

            @Override
            public void dateClick(DateClickEvent event) {
                // let BasicDateClickHandler handle calendar dates, and update
                // only the other parts of UI here
                super.dateClick(event);
                switchToDayView();
            }
        });

        calendarComponent.setHandler(new RangeSelectHandler() {

            public void rangeSelect(RangeSelectEvent event) {
                handleRangeSelect(event);
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

        if (testBench) {
            s.select("America/New_York");
        } else {
            s.select(DEFAULT_ITEMID);
        }
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
        setLocale(l);
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

    private void handleRangeSelect(RangeSelectEvent event) {
        Date start = event.getStart();
        Date end = event.getEnd();

        /*
         * If a range of dates is selected in monthly mode, we want it to end at
         * the end of the last day.
         */
        if (event.isMonthlyMode()) {
            end = Calendar.getEndOfDay(calendar, end);
        }

        showEventPopup(createNewEvent(start, end), true);
    }

    private void showEventPopup(CalendarEvent event, boolean newEvent) {
        if (event == null) {
            return;
        }

        updateCalendarEventPopup(newEvent);
        updateCalendarEventForm(event);

        if (!getMainWindow().getChildWindows().contains(scheduleEventPopup)) {
            getMainWindow().addWindow(scheduleEventPopup);
        }
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

    private void updateCalendarEventForm(CalendarEvent event) {
        // Lets create a CalendarEvent BeanItem and pass it to the form's data
        // source.
        BeanItem<CalendarEvent> item = new BeanItem<CalendarEvent>(event);
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
                } else if (propertyId.equals("allDay")) {
                    CheckBox cb = createCheckBox("All-day");

                    cb.addListener(new Property.ValueChangeListener() {

                        private static final long serialVersionUID = -7104996493482558021L;

                        public void valueChange(ValueChangeEvent event) {
                            Object value = event.getProperty().getValue();
                            if (value instanceof Boolean
                                    && Boolean.TRUE.equals(value)) {
                                setFormDateResolution(DateField.RESOLUTION_DAY);

                            } else {
                                setFormDateResolution(DateField.RESOLUTION_MIN);
                            }
                        }

                    });

                    return cb;
                }
                return null;
            }

            private CheckBox createCheckBox(String caption) {
                CheckBox cb = new CheckBox(caption);
                cb.setImmediate(true);
                return cb;
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

        scheduleEventForm
                .setVisibleItemProperties(new Object[] { "start", "end",
                        "allDay", "caption", "where", "description",
                        "styleName" });
    }

    private void setFormDateResolution(int resolution) {
        if (scheduleEventForm.getField("start") != null
                && scheduleEventForm.getField("end") != null) {
            ((DateField) scheduleEventForm.getField("start"))
                    .setResolution(resolution);
            ((DateField) scheduleEventForm.getField("start")).requestRepaint();
            ((DateField) scheduleEventForm.getField("end"))
                    .setResolution(resolution);
            ((DateField) scheduleEventForm.getField("end")).requestRepaint();
        }
    }

    private CalendarEvent createNewEvent(Date startDate, Date endDate) {

        BasicEvent event = new BasicEvent();
        event.setCaption("");
        event.setStart(startDate);
        event.setEnd(endDate);
        event.setStyleName("color1");
        return event;
    }

    /* Removes the event from the data source and fires change event. */
    private void deleteCalendarEvent() {
        BasicEvent event = getFormCalendarEvent();
        if (dataSource.containsEvent(event)) {
            dataSource.removeEvent(event);
        }
        getMainWindow().removeWindow(scheduleEventPopup);
    }

    /* Adds/updates the event in the data source and fires change event. */
    private void commitCalendarEvent() {
        scheduleEventForm.commit();
        BasicEvent event = getFormCalendarEvent();
        if (!dataSource.containsEvent(event)) {
            dataSource.addEvent(event);
        }

        getMainWindow().removeWindow(scheduleEventPopup);
    }

    private void discardCalendarEvent() {
        scheduleEventForm.discard();
        getMainWindow().removeWindow(scheduleEventPopup);
    }

    @SuppressWarnings("unchecked")
    private BasicEvent getFormCalendarEvent() {
        BeanItem<CalendarEvent> item = (BeanItem<CalendarEvent>) scheduleEventForm
                .getItemDataSource();
        CalendarEvent event = item.getBean();
        return (BasicEvent) event;
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

    private CalendarTestEvent getNewEvent(String caption, Date start, Date end) {
        CalendarTestEvent event = new CalendarTestEvent();
        event.setCaption(caption);
        event.setStart(start);
        event.setEnd(end);

        return event;
    }

    /*
     * Switch the view to week view.
     */
    public void switchToWeekView() {
        viewMode = Mode.WEEK;
        weekButton.setVisible(false);
        monthButton.setVisible(true);
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
     * Switch to day view (week view with a single day visible).
     */
    public void switchToDayView() {
        viewMode = Mode.DAY;
        monthButton.setVisible(true);
        weekButton.setVisible(true);
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
}
