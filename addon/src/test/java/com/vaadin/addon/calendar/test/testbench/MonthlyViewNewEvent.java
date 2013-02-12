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

public class MonthlyViewNewEvent extends CalendarTestBenchTest {

    public MonthlyViewNewEvent(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testMonthlyViewNewEvent() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&width=1000px&height=600px&restartApplication"));

        // Create new event with the button
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[6]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]")))
                .click(10, 5);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field")))
                .click(52, 8);
        createEvent("12/26/99", "1/1/00", "Test event");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]",
                "Test description");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Open previously created event, assert values
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[0]/domChild[1]")))
                .click(91, 9);
        assertEquals(
                "12/26/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/1/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "on",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Test description",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Green",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Open previously created event, assert values and shorten the event
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[6]/domChild[0]/domChild[1]")))
                .click(41, 8);
        assertEquals(
                "12/26/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/1/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "on",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Test description",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Green",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field",
                "12/27/99");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field",
                "12/31/99");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Click the calendar where the event previously was, assert new event
        // creation
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[0]/domChild[1]")))
                .click(107, 8);
        assertEquals(
                "New event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]"))
                        .getText());
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Click the calendar where the event previously was, assert new event
        // creation
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[6]/domChild[0]/domChild[3]")))
                .click(50, 3);
        assertEquals(
                "New event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]"))
                        .getText());
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Edit the previously created event
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]")))
                .click(23, 3);
        assertEquals(
                "12/27/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "12/31/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[5]/domChild[0]/domChild[1]")))
                .click(76, 4);
        assertEquals(
                "12/27/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "12/31/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]")))
                .click(6, 10);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]")))
                .click(75, 2);
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]"))
                .click();
        assertEquals(
                "New event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]"))
                        .getText());
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]")))
                .click(10, 6);
    }
}
