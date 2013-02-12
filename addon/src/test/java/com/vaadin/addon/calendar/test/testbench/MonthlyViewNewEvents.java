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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class MonthlyViewNewEvents extends CalendarTestBenchTest {

    public MonthlyViewNewEvents(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testMonthlyViewNewEvents() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&width=1000px&height=600px&restartApplication"));

        // Create new event by dragging, make it all-day
        Actions dnd = new Actions(driver)
                .moveToElement(
                        driver.findElement(By
                                .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]")))
                .clickAndHold();
        dnd.moveToElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[5]/domChild[0]")),
                98, 83).release().perform();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field")))
                .click(8, 8);
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]"))
                .click();
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
        assertTrue(driver
                .findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]"))
                .isSelected());
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]",
                "Test event");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]",
                "Description");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Create new event by dragging
        dnd = new Actions(driver)
                .moveToElement(
                        driver.findElement(By
                                .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[0]/domChild[4]")))
                .clickAndHold();
        dnd.moveToElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[3]/domChild[0]/domChild[4]")),
                38, 8).release().perform();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]")))
                .click(7, 9);
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]",
                "Second test event");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]",
                "Desc");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Create new event by dragging, make it blue
        dnd = new Actions(driver)
                .moveToElement(
                        driver.findElement(By
                                .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[4]/domChild[0]")))
                .clickAndHold();
        dnd.moveToElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[6]/domChild[0]")),
                125, 80).release().perform();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]")))
                .click(10, 8);
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]",
                "Third test event");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]",
                "Testing");
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[1]")))
                .click(6, 9);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item2")))
                .click(49, 10);
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[3]/domChild[0]")))
                .click(45, 85);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]")))
                .click(5, 9);
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]",
                "Fourth test event");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]",
                "Fourth event");
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[1]")))
                .click(13, 18);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item3")))
                .click(57, 9);
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Start asserting the previously entered events
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]")))
                .click(12, 7);
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
        assertTrue(driver
                .findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]"))
                .isSelected());
        assertEquals(
                "Test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Description",
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
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[5]/domChild[0]/domChild[1]")))
                .click(72, 6);
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
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[0]/domChild[2]")))
                .click(44, 10);
        assertEquals(
                "12/26/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "12/29/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Second test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Desc",
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
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[3]/domChild[0]/domChild[2]")))
                .click(79, 5);
        assertEquals(
                "12/26/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "12/29/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Second test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Desc",
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
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[4]/domChild[0]/domChild[2]")))
                .click(47, 9);
        assertEquals(
                "12/30/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/1/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Third test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Testing",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Blue",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[6]/domChild[0]/domChild[2]")))
                .click(72, 9);
        assertEquals(
                "12/30/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/1/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Third test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Testing",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Blue",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[3]/domChild[0]/domChild[3]")))
                .click(37, 9);
        assertEquals(
                "12/29/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "12/29/99",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Fourth test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Fourth event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Red",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                .click();
    }
}
