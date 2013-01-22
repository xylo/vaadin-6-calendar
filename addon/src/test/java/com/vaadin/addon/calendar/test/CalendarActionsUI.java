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

import java.util.Date;
import java.util.Locale;

import com.vaadin.addon.calendar.event.BasicEvent;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarDateRange;
import com.vaadin.event.Action;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.UI;

public class CalendarActionsUI extends UI {

    @SuppressWarnings("deprecation")
    @Override
    protected void init(VaadinRequest request) {
        GridLayout content = new GridLayout(1, 2);
        content.setSizeFull();
        setContent(content);

        final Calendar calendar = new Calendar();
        calendar.setLocale(new Locale("fi", "FI"));

        calendar.setSizeFull();
        calendar.setStartDate(new Date(100, 1, 1));
        calendar.setEndDate(new Date(100, 2, 1));

        calendar.addActionHandler(new Action.Handler() {

            public final Action NEW_EVENT = new Action("Add event");
            public final Action EDIT_EVENT = new Action("Edit event");
            public final Action REMOVE_EVENT = new Action("Remove event");

            /*
             * (non-Javadoc)
             * 
             * @see
             * com.vaadin.event.Action.Handler#handleAction(com.vaadin.event
             * .Action, java.lang.Object, java.lang.Object)
             */
            public void handleAction(Action action, Object sender, Object target) {
                Date date = (Date) target;
                if (action == NEW_EVENT) {
                    BasicEvent event = new BasicEvent("New event",
                            "Hello world", date, date);
                    calendar.addEvent(event);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see com.vaadin.event.Action.Handler#getActions(java.lang.Object,
             * java.lang.Object)
             */
            public Action[] getActions(Object target, Object sender) {
                CalendarDateRange date = (CalendarDateRange) target;

                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(2000, 1, 1, 12, 0, 0);

                if (date.inRange(cal.getTime())) {
                    return new Action[] { NEW_EVENT, };
                }

                cal.add(java.util.Calendar.DAY_OF_WEEK, 1);

                if (date.inRange(cal.getTime())) {
                    return new Action[] { REMOVE_EVENT };
                }

                return null;
            }
        });

        content.addComponent(calendar);

        content.addComponent(new Button("Set week view", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                calendar.setEndDate(new Date(100, 1, 7));
            }
        }));

        content.setRowExpandRatio(0, 1);

    }
}
