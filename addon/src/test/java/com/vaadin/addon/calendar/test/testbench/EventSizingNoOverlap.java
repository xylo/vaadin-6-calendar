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
