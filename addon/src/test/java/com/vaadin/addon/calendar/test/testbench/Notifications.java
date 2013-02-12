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
package com.vaadin.addon.calendar.test.testbench;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class Notifications extends CalendarTestBenchTest {

    public Notifications(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testNotifications() throws Exception {
        driver.get(concatUrl(BASEURL,
                "/com.vaadin.addon.calendar.test.NotificationTestUI?restartApplication"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[1]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[2]/domChild[0]/domChild[0]")))
                .click(83, 11);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VNotification[0]/HTML[0]/domChild[0]")))
                .closeNotification();
        compareScreen("no-notification");
    }
}
