/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.addon.calendar.test;

/*
 * #%L
 * Vaadin Calendar
 * %%
 * Copyright (C) 2010 - 2013 Vaadin Ltd
 * %%
 * This program is available under GNU Affero General Public License (version
 * 3 or later at your option).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.event.CalendarEvent;
import com.vaadin.addon.calendar.event.CalendarEventProvider;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickEvent;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.DateClickHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class NotificationTestUI extends UI {

    private DummyEventProvider provider;

    private static class DummyEventProvider implements CalendarEventProvider {

        private int index;
        private List<CalendarEvent> events = new ArrayList<CalendarEvent>();

        public void addEvent(Date date) {
            BasicEvent e = new BasicEvent();
            e.setAllDay(true);
            e.setStart(date);
            e.setEnd(date);
            e.setCaption("Some event " + ++index);
            events.add(e);
        }

        public List<CalendarEvent> getEvents(Date startDate, Date endDate) {
            return events;
        }

    }

    @Override
    protected void init(com.vaadin.server.VaadinRequest request) {
        GridLayout content = new GridLayout(1, 2);
        content.setSizeFull();
        content.setRowExpandRatio(1, 1.0f);
        setContent(content);
        final Button btn = new Button("Show working notification",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        Notification
                                .show("This will disappear when you move your mouse!");
                    }
                });
        content.addComponent(btn);

        provider = new DummyEventProvider();
        final Calendar cal = new Calendar(provider);
        cal.setLocale(Locale.US);
        cal.setSizeFull();
        cal.setHandler(new DateClickHandler() {
            public void dateClick(DateClickEvent event) {
                provider.addEvent(event.getDate());
                Notification
                        .show("This should disappear, but if wont unless clicked.");

                // this requestRepaint call interferes with the notification
                cal.markAsDirty();
            }
        });
        content.addComponent(cal);

        java.util.Calendar javaCal = java.util.Calendar.getInstance();
        javaCal.set(java.util.Calendar.YEAR, 2000);
        javaCal.set(java.util.Calendar.MONTH, 0);
        javaCal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        Date start = javaCal.getTime();
        javaCal.set(java.util.Calendar.DAY_OF_MONTH, 31);
        Date end = javaCal.getTime();

        cal.setStartDate(start);
        cal.setEndDate(end);
    }
}
