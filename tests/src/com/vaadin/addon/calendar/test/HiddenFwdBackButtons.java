package com.vaadin.addon.calendar.test;

import java.util.Date;
import java.util.Locale;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardHandler;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class HiddenFwdBackButtons extends VerticalLayout {

    public HiddenFwdBackButtons() {
        setSizeFull();

        final Calendar calendar = new Calendar();
        calendar.setLocale(new Locale("fi", "FI"));

        calendar.setSizeFull();
        calendar.setStartDate(new Date(100, 1, 1));
        calendar.setEndDate(new Date(100, 1, 7));

        addComponent(calendar);
        addComponent(new Button("Hide forward and back buttons",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        // This should hide the forward and back buttons.
                        calendar.setHandler((ForwardHandler) null);
                        calendar.setHandler((BackwardHandler) null);
                    }
                }));

        setExpandRatio(calendar, 1);

    }

}
