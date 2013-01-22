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
import java.util.GregorianCalendar;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * A testing application to demonstrate the use of the JPAContainer with the
 * Calendar
 */
public class JPAContainerTestUI extends UI {

    public static final String PERSISTANCE_UNIT = "jpa-container-test-persistance-unit";

    private JPAContainer<PersistentEvent> eventContainer;

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        setContent(content);

        // Create the JPA Container
        eventContainer = JPAContainerFactory.make(PersistentEvent.class,
                PERSISTANCE_UNIT);

        // Ensure we have no events in database from previous sessions
        eventContainer.removeAllItems();

        // Create the calendar instance
        Calendar calendar = new Calendar();
        calendar.setSizeFull();

        // Attach the container to the calendar
        calendar.setContainerDataSource(eventContainer);

        // Add an event to the calendar
        calendar.addEvent(createEvent(new Date()));

        content.addComponent(calendar);
    }

    /**
     * Creates a new event at the desired date. The event is by default three
     * hours.
     * 
     * @param date
     * @return
     */
    private PersistentEvent createEvent(Date date) {
        java.util.Calendar cal = new GregorianCalendar();
        PersistentEvent event = new PersistentEvent();
        event.setStart(cal.getTime());
        cal.add(java.util.Calendar.HOUR, 3);
        event.setEnd(cal.getTime());
        event.setCaption("My event");
        event.setDescription("My event description");
        return event;
    }
}
