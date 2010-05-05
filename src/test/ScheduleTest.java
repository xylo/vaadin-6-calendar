package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.Application;
import com.vaadin.addon.calendar.ui.Schedule;
import com.vaadin.addon.calendar.ui.Schedule.CalendarFormat;
import com.vaadin.addon.calendar.ui.Schedule.EventMoveListener;
import com.vaadin.addon.calendar.ui.Schedule.EventReader;
import com.vaadin.addon.calendar.ui.Schedule.NavigationListener;
import com.vaadin.addon.calendar.ui.Schedule.RangeSelectListener;
import com.vaadin.addon.calendar.ui.Schedule.ScheduleEvent;
import com.vaadin.addon.calendar.ui.Schedule.WeekClickListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
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

/** Scheduler component test application */
public class ScheduleTest extends Application implements EventReader {

	private static final long serialVersionUID = -5436777475398410597L;

	private enum Mode {
		MONTH, WEEK, DAY;
	}

	private GregorianCalendar calendar = new GregorianCalendar();

	private Schedule schedule;

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

	private Window scheduleEventPopup;

	private final Form scheduleEventForm = new Form();

	private Button deleteEventButton;

	private Mode viewMode = Mode.MONTH;

	private List<ScheduleEvent> dataSource = new ArrayList<ScheduleEvent>();

	@Override
	public void init() {
		Window w = new Window();
		setMainWindow(w);
		setTheme("calendar");

		// Set default Locale for this application
		setLocale(Locale.getDefault());

		// Initialize locale, timezone and timeformat selects.
		localeSelect = createLocaleSelect();
		timeZoneSelect = createTimeZoneSelect();
		formatSelect = createCalendarFormatSelect();

		initScheduler();

		w.setContent(initLayout(w));
		w.setSizeFull();
	}

	private Layout initLayout(Window w) {
		initNavigationButtons();
		initHideWeekEndButton();

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(true);
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
		controlPanel.setMargin(true);
		controlPanel.setWidth("80%");
		controlPanel.addComponent(localeSelect);
		controlPanel.addComponent(timeZoneSelect);
		controlPanel.addComponent(formatSelect);
		controlPanel.addComponent(hideWeekendsButton);
		controlPanel.setComponentAlignment(timeZoneSelect, Alignment.MIDDLE_LEFT);
		controlPanel.setComponentAlignment(formatSelect, Alignment.MIDDLE_LEFT);
		controlPanel.setComponentAlignment(localeSelect, Alignment.MIDDLE_LEFT);
		controlPanel.setComponentAlignment(hideWeekendsButton, Alignment.MIDDLE_LEFT);

		layout.addComponent(controlPanel);
		layout.addComponent(hl);
		layout.addComponent(schedule);
		layout.setExpandRatio(schedule, 1);
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
				switchToWeekView(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.YEAR));
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
				schedule.setHideWeekends((Boolean) event.getButton().getValue());
			}
		});
	}

	private void initScheduler() {
		schedule = new Schedule(this);
		schedule.setHideWeekends(false);
		schedule.setLocale(getLocale());
		schedule.setImmediate(true);

		Date today = new Date();
		calendar.setTimeZone(TimeZone.getTimeZone((String) timeZoneSelect.getValue()));
		calendar.setTime(today);

		updateCaptionLabel();

		int rollAmount = calendar.get(Calendar.DAY_OF_MONTH) - 1;
		calendar.add(Calendar.DAY_OF_MONTH, -rollAmount);
		resetTime(false);
		currentMonthsFirstDate = calendar.getTime();
		schedule.setStartDate(currentMonthsFirstDate);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		schedule.setEndDate(calendar.getTime());

		addScheduleEventListeners();
	}

	private void addScheduleEventListeners() {
		// Register week clicks by changing the schedules start and end dates.
		schedule.addWeekClickListener(new WeekClickListener() {

			public void weekClicked(int week, int year) {
				switchToWeekView(week, year);
			}
		});

		// Navigation listener is actually a container of few listeners.
		schedule.addNavigationListener(new NavigationListener() {

			public void onScheduleForward() {
			}

			public void onScheduleBackward() {
			}

			public void eventClicked(ScheduleEvent e) {
				showEventPopup(e, false);
			}

			public void dateClicked(Date d) {
				// Schedules start and end dates will be changed.
				handleDateClick(d);
			}
		});

		schedule.addRangeSelectListener(new RangeSelectListener() {

			public void rangeSelected(Date startDate, Date endDate) {
				showEventPopup(createNewEvent(startDate, endDate), true);
			}
		});

		schedule.addEventMoveListener(new EventMoveListener() {

			public void eventMoved(ScheduleEvent e, Date newFromDatetime) {
				applyEventMove(e, newFromDatetime);
			}
		});
	}

	private Select createTimeZoneSelect() {
		Select s = new Select("Timezone");
		s.addContainerProperty("caption", String.class, "");
		s.setItemCaptionPropertyId("caption");
		s.setFilteringMode(Select.FILTERINGMODE_CONTAINS);

		for (String id : TimeZone.getAvailableIDs()) {
			if (!s.containsId(id)) {
				Item i = s.addItem(id);
				i.getItemProperty("caption").setValue(id);
			}
		}

		s.select(TimeZone.getDefault().getID());
		s.setImmediate(true);
		s.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				updateScheduleTimeZone((String) event.getProperty().getValue());
			}
		});

		return s;
	}

	private Select createCalendarFormatSelect() {
		Select s = new Select("Calendar format");

		s.addItem(CalendarFormat.FORMAT_12H);
		s.addItem(CalendarFormat.FORMAT_24H);

		s.select(CalendarFormat.FORMAT_24H);
		s.setImmediate(true);
		s.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				updateScheduleFormat((CalendarFormat) event.getProperty().getValue());
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
				updateScheduleLocale((Locale) event.getProperty().getValue());
			}
		});

		return s;
	}

	private void updateScheduleTimeZone(String timezoneId) {
		TimeZone tz = TimeZone.getTimeZone(timezoneId);
		calendar.setTimeZone(tz);
		schedule.setTimeZone(tz);
	}

	private void updateScheduleFormat(CalendarFormat format) {
		schedule.setCalendarFormat(format);
		schedule.requestRepaint();
	}

	private String getLocaleItemCaption(Locale l) {
		String country = l.getDisplayCountry(getLocale());
		String language = l.getDisplayLanguage(getLocale());
		StringBuilder caption = new StringBuilder(country);
		if (caption.length() != 0)
			caption.append(", ");
		caption.append(language);
		return caption.toString();
	}

	private void updateScheduleLocale(Locale l) {
		schedule.setLocale(l);
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
		switchToDayView(calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR));
	}

	private void applyEventMove(ScheduleEvent e, Date newFromDatetime) {
		/* Update event dates */
		long length = e.getWhenTo().getTime() - e.getWhenFrom().getTime();
		e.setWhenFrom(newFromDatetime);
		e.setWhenTo(new Date(newFromDatetime.getTime() + length));
		schedule.requestRepaint();
	}

	private void showEventPopup(ScheduleEvent event, boolean newEvent) {
		if (event == null)
			return;

		updateScheduleEventPopup(newEvent);
		updateScheduleEventForm(event);

		getMainWindow().addWindow(scheduleEventPopup);
	}

	/* Initializes a modal window to edit schedule event. */
	private void createScheduleEventPopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		scheduleEventPopup = new Window(null, layout);
		scheduleEventPopup.setWidth("400px");
		scheduleEventPopup.setModal(true);
		scheduleEventPopup.center();

		layout.addComponent(scheduleEventForm);

		Button apply = new Button("Apply", new ClickListener() {

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				commitScheduleEvent();
			}
		});
		Button cancel = new Button("Cancel", new ClickListener() {

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				discardScheduleEvent();
			}
		});
		deleteEventButton = new Button("Delete", new ClickListener() {

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				deleteScheduleEvent();
			}
		});
		scheduleEventPopup.addListener(new CloseListener() {

			private static final long serialVersionUID = 1L;

			public void windowClose(CloseEvent e) {
				discardScheduleEvent();
			}
		});

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.addComponent(deleteEventButton);
		buttons.addComponent(apply);
		buttons.addComponent(cancel);
		layout.addComponent(buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
	}

	private void updateScheduleEventPopup(boolean newEvent) {
		if (scheduleEventPopup == null) {
			createScheduleEventPopup();
		}

		if (newEvent)
			scheduleEventPopup.setCaption("New event");
		else
			scheduleEventPopup.setCaption("Edit event");

		deleteEventButton.setVisible(!newEvent);
	}

	private void updateScheduleEventForm(ScheduleEvent event) {
		// Lets create a ScheduleEvent BeanItem and pass it to the form's data
		// source.
		BeanItem<ScheduleEvent> item = new BeanItem<ScheduleEvent>(event);
		scheduleEventForm.setWriteThrough(false);
		scheduleEventForm.setItemDataSource(item);
		scheduleEventForm.setFormFieldFactory(new FormFieldFactory() {

			private static final long serialVersionUID = 1L;

			public Field createField(Item item, Object propertyId, Component uiContext) {
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
				}
				return null;
			}

			private TextField createTextField(String caption) {
				TextField f = new TextField(caption);
				f.setNullRepresentation("");
				return f;
			}
		});

		scheduleEventForm.setVisibleItemProperties(new Object[] { "caption", "where", "description" });
	}

	private ScheduleEvent createNewEvent(Date startDate, Date endDate) {
		ScheduleEvent event = schedule.new ScheduleEvent("", startDate, endDate);
		switch (viewMode) {
		case MONTH:
			event.setStyleName("color1");
			break;

		case WEEK:
		case DAY:
			event.setStyleName("color2");
			break;
		}

		return event;
	}

	/* Removes the event from the data source and requests repaint. */
	private void deleteScheduleEvent() {
		ScheduleEvent event = getFormScheduleEvent();
		if (dataSource.contains(event))
			dataSource.remove(event);
		getMainWindow().removeWindow(scheduleEventPopup);
		schedule.requestRepaint();
	}

	/* Adds/updates the event in the data source and requests repaint. */
	private void commitScheduleEvent() {
		scheduleEventForm.commit();
		ScheduleEvent event = getFormScheduleEvent();
		if (!dataSource.contains(event))
			dataSource.add(event);

		getMainWindow().removeWindow(scheduleEventPopup);
		schedule.requestRepaint();
	}

	private void discardScheduleEvent() {
		scheduleEventForm.discard();
		getMainWindow().removeWindow(scheduleEventPopup);
	}

	@SuppressWarnings("unchecked")
	private ScheduleEvent getFormScheduleEvent() {
		BeanItem<ScheduleEvent> item = (BeanItem<ScheduleEvent>) scheduleEventForm.getItemDataSource();
		ScheduleEvent event = item.getBean();
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
		calendar.add(Calendar.MONTH, direction);
		resetTime(false);
		currentMonthsFirstDate = calendar.getTime();
		schedule.setStartDate(currentMonthsFirstDate);

		updateCaptionLabel();

		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		resetTime(true);
		schedule.setEndDate(calendar.getTime());
	}

	private void rollWeek(int direction) {
		calendar.add(Calendar.WEEK_OF_YEAR, direction);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		resetTime(false);
		schedule.setStartDate(calendar.getTime());

		updateCaptionLabel();

		resetTime(true);
		calendar.add(Calendar.DATE, 6);
		schedule.setEndDate(calendar.getTime());
	}

	private void rollDate(int direction) {
		calendar.add(Calendar.DATE, direction);
		resetTime(false);
		schedule.setStartDate(calendar.getTime());

		updateCaptionLabel();

		resetTime(true);
		schedule.setEndDate(calendar.getTime());
	}

	private void updateCaptionLabel() {
		String cap = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getLocale()) + " " + calendar.get(Calendar.YEAR);
		captionLabel.setValue(cap);
	}

	/*
	 * Switch the Schedule component's start and end dates to range to the
	 * target week only. (sample range: 04.01.2010 00:00.000 - 10.01.2010
	 * 23:59.999)
	 */
	public void switchToWeekView(int week, int year) {
		viewMode = Mode.WEEK;
		weekButton.setVisible(false);
		monthButton.setVisible(true);

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.WEEK_OF_YEAR, week);
		calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
		resetTime(false);
		schedule.setStartDate(calendar.getTime());

		updateCaptionLabel();

		resetTime(true);
		calendar.add(Calendar.DATE, 6);
		schedule.setEndDate(calendar.getTime());
	}

	/*
	 * Switch the Schedule component's start and end dates to range to the
	 * target month only. (sample range: 01.01.2010 00:00.000 - 31.01.2010
	 * 23:59.999)
	 */
	public void switchToMonthView() {
		viewMode = Mode.MONTH;
		monthButton.setVisible(false);
		weekButton.setVisible(false);

		calendar.setTime(currentMonthsFirstDate);
		schedule.setStartDate(currentMonthsFirstDate);

		updateCaptionLabel();

		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		resetTime(true);
		schedule.setEndDate(calendar.getTime());
	}

	/*
	 * Switch the Schedule component's start and end dates to range to the
	 * target day only. (sample range: 01.01.2010 00:00.000 - 01.01.2010
	 * 23:59.999)
	 */
	public void switchToDayView(int date, int year) {
		viewMode = Mode.DAY;
		monthButton.setVisible(true);
		weekButton.setVisible(true);

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.DATE, date);
		resetTime(false);
		schedule.setStartDate(calendar.getTime());

		updateCaptionLabel();

		resetTime(true);
		schedule.setEndDate(calendar.getTime());
	}

	/*
	 * Resets the calendar time (hour, minute second and millisecond) either to
	 * zero or maximum value.
	 */
	private void resetTime(boolean max) {
		if (max) {
			calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
			calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
		} else {
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.addon.calendar.ui.Schedule.EventReader#getEvents(java.util
	 * .Date, java.util.Date)
	 */
	public ArrayList<ScheduleEvent> getEvents(Date fromStartDate, Date toEndDate) {
		ArrayList<ScheduleEvent> activeEvents = new ArrayList<ScheduleEvent>();

		for (ScheduleEvent ev : dataSource) {
			long from = fromStartDate.getTime();
			long to = toEndDate.getTime();

			long f = ev.getWhenFrom().getTime();
			long t = ev.getWhenTo().getTime();
			// Select only events that overlaps with fromStartDate and
			// toEndDate.
			if ((f <= to && f >= from) || (t >= from && t <= to) || (f <= from && t >= to)) {
				activeEvents.add(ev);
			}
		}
		return activeEvents;
	}
}
