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

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class VisibleHours24H extends CalendarTestBenchTest {

    public VisibleHours24H(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testVisibleHours24H() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&firstDay=1&lastDay=5&firstHour=8&lastHour=16&restartApplication"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[2]/domChild[0]/domChild[0]")))
                .click(9, 55);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VFilterSelect[0]/domChild[1]")))
                .click(7, 14);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VFilterSelect$SuggestionPopup[0]/VFilterSelect$SuggestionMenu[0]#item3")))
                .click(120, 15);
        assertEquals(
                "9:00",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[1]"))
                        .getText());
        assertEquals(
                "16:00",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[3]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[0]/domChild[8]"))
                        .getText());
    }

}
