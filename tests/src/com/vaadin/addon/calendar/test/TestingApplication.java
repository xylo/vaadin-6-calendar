package com.vaadin.addon.calendar.test;

import java.util.TimeZone;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This application is used as a base for all testing application. Pass it a
 * qualified class name and it will load the testing application class. For
 * example doing
 * http://localhost:8080/Calendar/test/com.vaadin.addon.calendar.test
 * .NotificationTestApp will load that application.
 * 
 * @author "John Ahlroos / Vaadin Ltd"
 * 
 */
@Theme("calendartest")
public class TestingApplication extends UI {

    @Override
    public void init(VaadinRequest request) {

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        String pathInfo = request.getRequestPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/com.vaadin")) {
            String className = pathInfo.substring(1);
            try {
                setContentFromClass(className);
            } catch (Exception e) {
                setDefaultContent();
                e.printStackTrace();
            }
        } else {
            setDefaultContent();
        }
    }

    private void setContentFromClass(String className) throws Exception {
        Class<?> c = Class.forName(className);
        if (getContent().getClass() != c) {
            ComponentContainer component = (ComponentContainer) c.newInstance();
            setContent(component);
        }
    }

    private void setDefaultContent() {
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new Label("Test not found."));
        setContent(vl);
    }
}