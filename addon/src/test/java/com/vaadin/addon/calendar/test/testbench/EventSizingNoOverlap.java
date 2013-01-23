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

public class EventSizingNoOverlap extends CalendarTestBenchTest {
    public EventSizingNoOverlap(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testEventSizingNoOverlap() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&firstDay=1&lastDay=5&firstHour=8&lastHour=16&restartApplication"));
        // Go to week view
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[2]/domChild[0]/domChild[0]")))
                .click(4, 57);

        // Open add-event popup, enter event between 12:45-13:15
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[6]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        createEvent("1/9/00 12:45 PM", "1/9/00 13:15 PM", "12:45-13:15");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Open add-event popup, enter event between 13:15-13:25
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[6]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        createEvent("1/9/00 13:15 PM", "1/9/00 13:25 PM", "13:15-13:25");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Open add-event popup, enter event between 13:25-13:55
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[6]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        createEvent("1/9/00 13:25 PM", "1/9/00 13:55 PM", "13:25-13:55");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        compareScreen("eventsizingnooverlap");
    }
}
