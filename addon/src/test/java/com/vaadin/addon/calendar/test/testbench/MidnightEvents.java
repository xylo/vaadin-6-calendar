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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class MidnightEvents extends CalendarTestBenchTest {

    public MidnightEvents(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testMidnightEvents() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&width=1000px&height=600px&secondsResolution&restartApplication"));

        // Add event from 0:00 to 0:00
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[1]/domChild[1]/domChild[0]/domChild[2]"))
                .click();
        createEvent("1/3/00 12:00:00 AM", "1/4/00 12:00:00 AM",
                "Midnight to midnight");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Add event from 00:00 to 00:00 on the same day
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[1]/domChild[5]/domChild[0]/domChild[1]")))
                .click(84, 10);
        createEvent("1/7/00 12:00:00 AM", "", "Zero-length midnight event");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        compareScreen("midnight-events-1");

        // Go to weekly view
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[0]")))
                .click(10, 48);

        // Assert zero-length event exists
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[6]/domChild[0]/domChild[48]/domChild[0]")))
                .click(50, 4);
        assertEquals(
                "Zero-length midnight event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Make sure that the the all day event does not overflow to the next
        // day by checking that the first time-cell is empty in the screenshot.
        compareScreen("midnight-events-no-overflow");
    }
}
