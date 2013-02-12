/**
 * Copyright 2013 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

        if (pathInfo.contains("favicon.ico")) {
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
