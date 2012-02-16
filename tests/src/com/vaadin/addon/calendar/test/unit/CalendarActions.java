package com.vaadin.addon.calendar.test.unit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.gwt.dev.util.collect.HashMap;
import com.vaadin.addon.calendar.gwt.client.ui.VCalendarAction;
import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.event.Action;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Paintable;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamVariable;
import com.vaadin.terminal.VariableOwner;

public class CalendarActions extends TestCase {

    private Calendar calendar;

    /**
     * Action handler for testing purpose
     */
    private static class TestingActionHandler implements Action.Handler {

        public static final Action ACTION = new Action("Action");

        // For testing purposed
        private Object lastGetActionsTarget;
        private Object lastGetActionsSender;
        private Action lastHandleActionAction;
        private Object lastHandleActionTarget;
        private Object lastHandleActionSender;

        public Action[] getActions(Object target, Object sender) {
            lastGetActionsSender = sender;
            lastGetActionsTarget = target;
            return new Action[] { ACTION };
        }

        public void handleAction(Action action, Object sender, Object target) {
            lastHandleActionAction = action;
            lastHandleActionTarget = target;
            lastHandleActionSender = sender;
        }

        public Object getLastGetActionsTarget() {
            return lastGetActionsTarget;
        }

        public Object getLastGetActionsSender() {
            return lastGetActionsSender;
        }

        public Action getLastHandleActionAction() {
            return lastHandleActionAction;
        }

        public Object getLastHandleActionTarget() {
            return lastHandleActionTarget;
        }

        public Object getLastHandleActionSender() {
            return lastHandleActionSender;
        }
    }

    /**
     * Paint target to use for testing
     */
    private static class DummyPaintTarget implements PaintTarget {
        public void addSection(String sectionTagName, String sectionData)
                throws PaintException {
        }

        public boolean startTag(Paintable paintable, String tag)
                throws PaintException {
            return true;
        }

        public void paintReference(Paintable paintable, String referenceName)
                throws PaintException {
        }

        public void startTag(String tagName) throws PaintException {
        }

        public void endTag(String tagName) throws PaintException {
        }

        public void addAttribute(String name, boolean value)
                throws PaintException {
        }

        public void addAttribute(String name, int value) throws PaintException {
        }

        public void addAttribute(String name, Resource value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name,
                StreamVariable value) throws PaintException {
        }

        public void addAttribute(String name, long value) throws PaintException {
        }

        public void addAttribute(String name, float value)
                throws PaintException {
        }

        public void addAttribute(String name, double value)
                throws PaintException {
        }

        public void addAttribute(String name, String value)
                throws PaintException {
        }

        public void addAttribute(String name, Map<?, ?> value)
                throws PaintException {
        }

        public void addAttribute(String name, Paintable value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name, String value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name, int value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name, long value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name, float value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name, double value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name, boolean value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name, String[] value)
                throws PaintException {
        }

        public void addVariable(VariableOwner owner, String name,
                Paintable value) throws PaintException {
        }

        public void addUploadStreamVariable(VariableOwner owner, String name)
                throws PaintException {
        }

        public void addXMLSection(String sectionTagName, String sectionData,
                String namespace) throws PaintException {
        }

        public void addUIDL(String uidl) throws PaintException {
        }

        public void addText(String text) throws PaintException {
        }

        public void addCharacterData(String text) throws PaintException {
        }

        public void addAttribute(String string, Object[] keys) {
        }

        public String getTag(Paintable paintable) {
            return null;
        }

        public boolean isFullRepaint() {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() {
        calendar = new Calendar();
        calendar.setLocale(new Locale("FI", "fi"));
    }

    /**
     * Simulates user right clicking on calendar for an action
     */
    @Test
    public void testGetAndExecuteAction() {

        // Setup
        java.util.Calendar cal = new GregorianCalendar();
        calendar.setStartDate(cal.getTime());
        cal.add(java.util.Calendar.MONTH, 1);
        calendar.setEndDate(cal.getTime());

        // Set action handler
        TestingActionHandler handler = new TestingActionHandler();
        calendar.addActionHandler(handler);

        // Simulate painting the actions to the client
        try {
            calendar.paintContent(new DummyPaintTarget());
        } catch (PaintException e) {
            // Ignore
        }

        // Was getActions called when painting
        assertEquals(calendar, handler.getLastGetActionsSender());
        assertNotNull("GetActions target was null",
                handler.getLastGetActionsTarget());

        // Simulate right click on calendar
        SimpleDateFormat formatter = new SimpleDateFormat(
                VCalendarAction.ACTION_DATE_FORMAT_PATTERN);

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("action",
                "1," + formatter.format(calendar.getStartDate()));
        calendar.changeVariables(calendar, variables);

        // Did the action get handled?
        assertEquals(TestingActionHandler.ACTION.getCaption(), handler
                .getLastHandleActionAction().getCaption());

        // Was the sender of the handled action the calendar instance
        assertEquals(calendar, handler.getLastHandleActionSender());

        // Was the start date the given start date
        assertEquals(formatter.format(calendar.getStartDate()),
                formatter.format((Date) handler.getLastHandleActionTarget()));

    }
}
