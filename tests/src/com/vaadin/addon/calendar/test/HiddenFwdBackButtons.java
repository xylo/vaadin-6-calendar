package com.vaadin.addon.calendar.test;

import java.util.Date;
import java.util.Locale;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.BackwardHandler;
import com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.UI;

public class HiddenFwdBackButtons extends UI {

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
        calendar.setEndDate(new Date(100, 1, 7));
        content.addComponent(calendar);
        Button button = new Button("Hide forward and back buttons");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // This should hide the forward and back navigation buttons
                calendar.setHandler((BackwardHandler) null);
                calendar.setHandler((ForwardHandler) null);
            }
        });
        content.addComponent(button);

        content.setRowExpandRatio(0, 1);

    }
}
