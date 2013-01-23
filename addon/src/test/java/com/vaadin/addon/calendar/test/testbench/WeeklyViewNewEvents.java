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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class WeeklyViewNewEvents extends CalendarTestBenchTest {

    public WeeklyViewNewEvents(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testWeeklyViewNewEvents() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&width=1000px&height=600px&restartApplication"));

        // Go to weekly view
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[2]/domChild[0]/domChild[0]")))
                .click(11, 53);

        // Assert the default event contents
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[2]/domChild[0]/domChild[48]/domChild[0]/domChild[0]")))
                .click(10, 6);
        assertEquals(
                "1/10/00 09:30 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/10/00 02:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertFalse(driver
                .findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]"))
                .isSelected());
        assertEquals(
                "Appointment",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Office",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[1]"))
                        .getAttribute("value"));
        assertEquals(
                "A longer description, which should display correctly.",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Green",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[48]/domChild[0]/domChild[0]")))
                .click(16, 7);
        assertEquals(
                "1/11/00 11:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 07:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Training",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Blue",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[7]/domChild[0]/domChild[48]/domChild[0]/domChild[0]")))
                .click(9, 11);
        assertEquals(
                "1/15/00 09:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/15/00 06:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Free time",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]")))
                .click(60, 3);
        assertEquals(
                "1/9/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/15/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "on",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VCheckBox[0]/domChild[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Whole week event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Description for the whole week event.",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Orange",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Assert the all-day events
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[1]")))
                .click(53, 7);
        assertEquals(
                "1/12/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/12/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Allday event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Some description.",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Red",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[4]/domChild[0]/domChild[1]")))
                .click(36, 10);
        assertEquals(
                "1/13/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/13/00",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Second allday event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Some description.",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Blue",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Enter new event
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[6]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        createEvent("1/13/00 9:00 AM", "1/13/00 2:00 PM", "Test event");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]",
                "Test event description");
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[1]")))
                .click(3, 15);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item4")))
                .click(36, 6);
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Assert previously created event
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[5]/domChild[0]/domChild[48]/domChild[0]")))
                .click(27, 15);
        assertEquals(
                "1/13/00 09:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/13/00 02:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Test event description",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]"))
                        .getAttribute("value"));
        assertEquals(
                "Orange",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VFilterSelect[0]/domChild[0]"))
                        .getAttribute("value"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[1]")))
                .click(8, 8);

        // Edit previously created events and change properties
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[5]/domChild[0]/domChild[48]/domChild[1]")))
                .click(55, 62);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#popupButton")))
                .click(10, 11);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VOverlay[0]/VCalendarPanel[0]#day11")))
                .click(16, 11);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#popupButton")))
                .click(14, 14);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VOverlay[0]/VCalendarPanel[0]#day11")))
                .click(14, 10);
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Assert the edited values
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[49]/domChild[1]")))
                .click(27, 73);
        assertEquals(
                "1/11/00 09:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 02:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Create new event
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[6]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        createEvent("1/11/00 10:00 AM", "1/11/00 8:00 PM", "Test event 2");
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextArea[0]",
                "Second test event");
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[0]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Assert previously created event still exists in the right place (as
        // multiple events occupy the same time)
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[50]/domChild[1]")))
                .click(7, 73);
        assertEquals(
                "1/11/00 09:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 02:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();

        // Assert previously created event still exists in the right place (as
        // multiple events occupy the same time)
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[49]/domChild[0]")))
                .click(12, 32);
        assertEquals(
                "1/11/00 11:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 07:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[1]")))
                .click(11, 8);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[48]/domChild[1]")))
                .click(17, 83);
        assertEquals(
                "1/11/00 10:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 08:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[50]/domChild[1]")))
                .click(14, 71);
        assertEquals(
                "1/11/00 09:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 02:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[48]/domChild[1]")))
                .click(16, 111);
        assertEquals(
                "1/11/00 10:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 08:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Test event 2",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[48]/domChild[1]")))
                .click(14, 209);
        assertEquals(
                "1/11/00 10:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 08:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Test event 2",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[49]/domChild[1]")))
                .click(20, 113);
        assertEquals(
                "1/11/00 11:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 07:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Training",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[1]")))
                .click(7, 8);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[3]/domChild[0]/domChild[50]/domChild[1]")))
                .click(21, 87);
        assertEquals(
                "1/11/00 09:00 AM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field"))
                        .getAttribute("value"));
        assertEquals(
                "1/11/00 02:00 PM",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field"))
                        .getAttribute("value"));
        assertEquals(
                "Test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VWindow[0]/domChild[0]/domChild[0]/domChild[1]")))
                .click(12, 10);

        // Go to monthly view and assert inserted events
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[2]/domChild[2]/domChild[0]/domChild[4]")))
                .click(36, 10);
        assertEquals(
                "Test event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[2]/domChild[2]/domChild[0]/domChild[3]")))
                .click(53, 6);
        assertEquals(
                "Training",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[2]/domChild[2]/domChild[0]/domChild[2]")))
                .click(48, 7);
        assertEquals(
                "Test event 2",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[2]/domChild[2]/domChild[0]/domChild[1]")))
                .click(50, 6);
        assertEquals(
                "Whole week event",
                driver.findElement(
                        By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]"))
                        .getAttribute("value"));
        driver.findElement(
                By.vaadin("ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
    }
}
