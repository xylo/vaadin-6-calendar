package com.vaadin.addon.calendar.test;

import java.util.TimeZone;

import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class TestingUIProvider extends UIProvider {

    private static final String classNamePrefix = "/";

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        String pathInfo = event.getRequest().getPathInfo();
        if (pathInfo != null && pathInfo.length() <= classNamePrefix.length()) {
            return null;
        }

        String className = pathInfo.substring(classNamePrefix.length());
        ClassLoader classLoader = event.getRequest().getService()
                .getClassLoader();
        if (classLoader == null) {
            classLoader = getClass().getClassLoader();
        }
        try {
            Class<? extends UI> uiClass = Class.forName(className, true,
                    classLoader).asSubclass(UI.class);
            return uiClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find UI class", e);
        }
    }
}
