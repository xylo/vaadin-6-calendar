package com.vaadin.addon.calendar.test;

import java.util.Date;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarDateRange;
import com.vaadin.event.Action;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class CalendarActionsApp extends VerticalLayout {

    /**
     * Construct
     */
    public CalendarActionsApp() {
        setSizeFull();

        final Calendar calendar = new Calendar();
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
                System.out.println(action.getCaption() + "," + sender + ","
                        + target);

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
                    return new Action[] { NEW_EVENT, EDIT_EVENT, REMOVE_EVENT };
                }
                return null;
            }
        });

        addComponent(calendar);

        addComponent(new Button("Set week view", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                calendar.setEndDate(new Date(100, 1, 7));
            }
        }));

        setExpandRatio(calendar, 1);

    }

}
