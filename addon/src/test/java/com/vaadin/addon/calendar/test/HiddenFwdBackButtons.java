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
