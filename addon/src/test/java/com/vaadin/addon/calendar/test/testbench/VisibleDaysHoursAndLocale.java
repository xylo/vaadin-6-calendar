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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class VisibleDaysHoursAndLocale extends CalendarTestBenchTest {

    public VisibleDaysHoursAndLocale(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testVisibleDaysHoursAndLocale() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&firstDay=1&lastDay=5&firstHour=8&lastHour=16&restartApplication"));
        testBench(driver).waitForVaadin();
        assertEquals(
                "Sun",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]"))
                        .getText());
        assertEquals(
                "Thu",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[4]/domChild[0]"))
                        .getText());
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[2]/domChild[0]/domChild[0]")))
                .click(10, 50);
        assertEquals(
                "Sun 1/9/00",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[1]/domChild[0]"))
                        .getText());
        assertEquals(
                "Thu 1/13/00",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[5]/domChild[0]"))
                        .getText());
        assertEquals(
                "9",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[1]/domChild[0]"))
                        .getText());
        assertEquals(
                "4",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[8]/domChild[0]"))
                        .getText());
        assertNotNull(driver
                .findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[2]/domChild[0]/domChild[18]/domChild[3]")));
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]/domChild[0]"))
                .click();
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&firstDay=1&lastDay=5&firstHour=8&lastHour=16&restartApplication&locale=fi_FI"));
        assertEquals(
                "ma",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]"))
                        .getText());
        assertEquals(
                "pe",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[4]/domChild[0]"))
                        .getText());
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[2]/domChild[0]/domChild[0]")))
                .click(10, 55);
        assertEquals(
                "ma 10.1.2000",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[1]/domChild[0]"))
                        .getText());
        assertEquals(
                "pe 14.1.2000",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[5]/domChild[0]"))
                        .getText());
    }
}
