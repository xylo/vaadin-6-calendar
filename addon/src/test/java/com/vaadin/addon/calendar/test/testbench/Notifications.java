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
