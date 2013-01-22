/**
 * Copyright (C) 2010 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.addon.calendar.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.vaadin.addon.calendar.event.BasicEventProvider;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.Calendar.TimeFormat;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventResize;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.WeekClick;

/**
 * Basic API tests for the calendar
 */
public class CalendarBasics {

    @Test
    public void testEmptyConstructorInitialization() {

        Calendar calendar = new Calendar();

        // The calendar should have a basic event provider with no events
        CalendarEventProvider provider = calendar.getEventProvider();
        assertNotNull("Event provider should not be null", provider);

        // Basic event handlers should be registered
        assertNotNull(calendar.getHandler(BackwardEvent.EVENT_ID));
        assertNotNull(calendar.getHandler(ForwardEvent.EVENT_ID));
        assertNotNull(calendar.getHandler(WeekClick.EVENT_ID));
        assertNotNull(calendar.getHandler(DateClickEvent.EVENT_ID));
        assertNotNull(calendar.getHandler(MoveEvent.EVENT_ID));
        assertNotNull(calendar.getHandler(EventResize.EVENT_ID));

        // Calendar should have undefined size
        assertTrue(calendar.getWidth() < 0);
        assertTrue(calendar.getHeight() < 0);
    }

    @Test
    public void testConstructorWithCaption() {
        final String caption = "My Calendar Caption";
        Calendar calendar = new Calendar(caption);
        assertEquals(caption, calendar.getCaption());
    }

    @Test
    public void testConstructorWithCustomEventProvider() {
        BasicEventProvider myProvider = new BasicEventProvider();
        Calendar calendar = new Calendar(myProvider);
        assertEquals(myProvider, calendar.getEventProvider());
    }

    @Test
    public void testConstructorWithCustomEventProviderAndCaption() {
        BasicEventProvider myProvider = new BasicEventProvider();
        final String caption = "My Calendar Caption";
        Calendar calendar = new Calendar(caption, myProvider);
        assertEquals(caption, calendar.getCaption());
        assertEquals(myProvider, calendar.getEventProvider());
    }

    @Test
    public void testDefaultStartAndEndDates() {
        Calendar calendar = new Calendar();

        // If no start and end date is set the calendar will display the current
        // week
        java.util.Calendar c = new GregorianCalendar();
        java.util.Calendar c2 = new GregorianCalendar();

        c2.setTime(calendar.getStartDate());
        assertEquals(c.getFirstDayOfWeek(),
                c2.get(java.util.Calendar.DAY_OF_WEEK));
        c2.setTime(calendar.getEndDate());

        c.set(java.util.Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        assertEquals(c.get(java.util.Calendar.DAY_OF_WEEK),
                c2.get(java.util.Calendar.DAY_OF_WEEK));
    }

    @Test
    public void testCustomStartAndEndDates() {
        Calendar calendar = new Calendar();
        java.util.Calendar c = new GregorianCalendar();

        Date start = c.getTime();
        c.add(java.util.Calendar.DATE, 3);
        Date end = c.getTime();

        calendar.setStartDate(start);
        calendar.setEndDate(end);

        assertEquals(start.getTime(), calendar.getStartDate().getTime());
        assertEquals(end.getTime(), calendar.getEndDate().getTime());
    }

    @Test
    public void testCustomLocale() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.CANADA_FRENCH);

        // Setting the locale should set the internal calendars locale
        assertEquals(Locale.CANADA_FRENCH, calendar.getLocale());
        java.util.Calendar c = new GregorianCalendar(Locale.CANADA_FRENCH);
        assertEquals(c.getTimeZone().getRawOffset(), calendar
                .getInternalCalendar().getTimeZone().getRawOffset());
    }

    @Test
    public void testTimeFormat() {
        Calendar calendar = new Calendar();

        // The default timeformat depends on the current locale
        calendar.setLocale(Locale.ENGLISH);
        assertEquals(TimeFormat.Format12H, calendar.getTimeFormat());

        calendar.setLocale(Locale.ITALIAN);
        assertEquals(TimeFormat.Format24H, calendar.getTimeFormat());

        // Setting a specific time format overrides the locale
        calendar.setTimeFormat(TimeFormat.Format12H);
        assertEquals(TimeFormat.Format12H, calendar.getTimeFormat());
    }

    @Test
    public void testTimeZone() {
        Calendar calendar = new Calendar();
        calendar.setLocale(Locale.CANADA_FRENCH);

        // By default the calendars timezone is returned
        assertEquals(calendar.getInternalCalendar().getTimeZone(),
                calendar.getTimeZone());

        // One can override the default behaviour by specifying a timezone
        TimeZone customTimeZone = TimeZone.getTimeZone("Europe/Helsinki");
        calendar.setTimeZone(customTimeZone);
        assertEquals(customTimeZone, calendar.getTimeZone());
    }

    @Test
    public void testVisibleDaysOfWeek() {
        Calendar calendar = new Calendar();

        // The defaults are the whole week
        assertEquals(1, calendar.getFirstVisibleDayOfWeek());
        assertEquals(7, calendar.getLastVisibleDayOfWeek());

        calendar.setFirstVisibleDayOfWeek(0); // Invalid input
        assertEquals(1, calendar.getFirstVisibleDayOfWeek());

        calendar.setLastVisibleDayOfWeek(0); // Invalid input
        assertEquals(7, calendar.getLastVisibleDayOfWeek());

        calendar.setFirstVisibleDayOfWeek(8); // Invalid input
        assertEquals(1, calendar.getFirstVisibleDayOfWeek());

        calendar.setLastVisibleDayOfWeek(8); // Invalid input
        assertEquals(7, calendar.getLastVisibleDayOfWeek());

        calendar.setFirstVisibleDayOfWeek(4);
        assertEquals(4, calendar.getFirstVisibleDayOfWeek());

        calendar.setLastVisibleDayOfWeek(6);
        assertEquals(6, calendar.getLastVisibleDayOfWeek());

        calendar.setFirstVisibleDayOfWeek(7); // Invalid since last day is 6
        assertEquals(4, calendar.getFirstVisibleDayOfWeek());

        calendar.setLastVisibleDayOfWeek(2); // Invalid since first day is 4
        assertEquals(6, calendar.getLastVisibleDayOfWeek());
    }

    @Test
    public void testVisibleHoursInDay() {
        Calendar calendar = new Calendar();

        // Defaults are the whole day
        assertEquals(0, calendar.getFirstVisibleHourOfDay());
        assertEquals(23, calendar.getLastVisibleHourOfDay());
    }

}
