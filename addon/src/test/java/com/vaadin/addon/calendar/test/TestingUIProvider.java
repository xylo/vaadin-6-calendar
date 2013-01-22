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
