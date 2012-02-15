package com.vaadin.addon.calendar.test;

import java.net.URL;
import java.util.TimeZone;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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
public class TestingApplication extends Application {

    private Window mainWindow;

    @Override
    public void init() {

        mainWindow = new Window("Calendar testing application");
        mainWindow.addURIHandler(this);
        setMainWindow(mainWindow);

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        setTheme("calendartest");
    }

    @Override
    public DownloadStream handleURI(URL context, String relativeUri) {
        if (relativeUri.startsWith("com.vaadin")) {
            String clazz = relativeUri;
            try {
                Class<?> c = Class.forName(clazz);
                if (mainWindow.getContent().getClass() != c) {
                    ComponentContainer component = (ComponentContainer) c
                            .newInstance();
                    mainWindow.setContent(component);
                }

            } catch (Exception e) {
                VerticalLayout vl = new VerticalLayout();
                vl.addComponent(new Label("Test not found."));
                mainWindow.setContent(vl);
                e.printStackTrace();
            }
        }
        return super.handleURI(context, relativeUri);
    }

}