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
package com.vaadin.addon.calendar.test.unit;

import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.addon.calendar.ui.Calendar;
import com.vaadin.event.Action;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VariableOwner;
import com.vaadin.ui.Component;

public class CalendarActions {

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
        @Override
        public void addSection(String sectionTagName, String sectionData)
                throws PaintException {
        }

        @Override
        public void startTag(String tagName) throws PaintException {
        }

        @Override
        public void endTag(String tagName) throws PaintException {
        }

        @Override
        public void addAttribute(String name, boolean value)
                throws PaintException {
        }

        @Override
        public void addAttribute(String name, int value) throws PaintException {
        }

        @Override
        public void addAttribute(String name, Resource value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name,
                StreamVariable value) throws PaintException {
        }

        @Override
        public void addAttribute(String name, long value) throws PaintException {
        }

        @Override
        public void addAttribute(String name, float value)
                throws PaintException {
        }

        @Override
        public void addAttribute(String name, double value)
                throws PaintException {
        }

        @Override
        public void addAttribute(String name, String value)
                throws PaintException {
        }

        @Override
        public void addAttribute(String name, Map<?, ?> value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name, String value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name, int value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name, long value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name, float value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name, double value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name, boolean value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name, String[] value)
                throws PaintException {
        }

        @Override
        public void addUploadStreamVariable(VariableOwner owner, String name)
                throws PaintException {
        }

        @Override
        public void addXMLSection(String sectionTagName, String sectionData,
                String namespace) throws PaintException {
        }

        @Override
        public void addUIDL(String uidl) throws PaintException {
        }

        @Override
        public void addText(String text) throws PaintException {
        }

        @Override
        public void addCharacterData(String text) throws PaintException {
        }

        @Override
        public void addAttribute(String string, Object[] keys) {
        }

        @Override
        public boolean isFullRepaint() {
            return false;
        }

        @Override
        public PaintStatus startPaintable(Component paintable, String tag)
                throws PaintException {
            return null;
        }

        @Override
        public void endPaintable(Component paintable) throws PaintException {
        }

        @Override
        public void addAttribute(String name, Component value)
                throws PaintException {
        }

        @Override
        public void addVariable(VariableOwner owner, String name,
                Component value) throws PaintException {
        }

        @Override
        public String getTag(ClientConnector paintable) {
            return null;
        }
    }

    @Before
    public void setUp() {
        calendar = new Calendar();
        calendar.setLocale(new Locale("FI", "fi"));
    }

    /**
     * Simulates user right clicking on calendar for an action
     */
    @Test
    @Ignore("Needs refactoring")
    public void testGetAndExecuteAction() {
        // If needed, figure out to do this without paintContent and
        // changeVariables
        // // Setup
        // java.util.Calendar cal = new GregorianCalendar();
        // calendar.setStartDate(cal.getTime());
        // cal.add(java.util.Calendar.MONTH, 1);
        // calendar.setEndDate(cal.getTime());
        //
        // // Set action handler
        // TestingActionHandler handler = new TestingActionHandler();
        // calendar.addActionHandler(handler);
        //
        // // Simulate painting the actions to the client
        // try {
        // calendar.paintContent(new DummyPaintTarget());
        // } catch (PaintException e) {
        // // Ignore
        // }
        //
        // // Was getActions called when painting
        // assertEquals(calendar, handler.getLastGetActionsSender());
        // assertNotNull("GetActions target was null",
        // handler.getLastGetActionsTarget());
        //
        // // Simulate right click on calendar
        // SimpleDateFormat formatter = new SimpleDateFormat(
        // VCalendarAction.ACTION_DATE_FORMAT_PATTERN);
        //
        // Map<String, Object> variables = new HashMap<String, Object>();
        // variables.put("action",
        // "1," + formatter.format(calendar.getStartDate()));
        // calendar.changeVariables(calendar, variables);
        //
        // // Did the action get handled?
        // assertEquals(TestingActionHandler.ACTION.getCaption(), handler
        // .getLastHandleActionAction().getCaption());
        //
        // // Was the sender of the handled action the calendar instance
        // assertEquals(calendar, handler.getLastHandleActionSender());
        //
        // // Was the start date the given start date
        // assertEquals(formatter.format(calendar.getStartDate()),
        // formatter.format((Date) handler.getLastHandleActionTarget()));
        //
    }
}
