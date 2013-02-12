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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class CalendarActions extends CalendarTestBenchTest {

    public CalendarActions(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testActions() throws Exception {
        driver.get(concatUrl(BASEURL,
                "/com.vaadin.addon.calendar.test.CalendarActionsUI?restartApplication"));
        WebElement e = driver
                .findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[7]"));
        new Actions(driver).moveToElement(e, 100, 20).contextClick().build()
                .perform();
        compareScreen("addEventContextMenu");
        e = driver
                .findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[2]/domChild[0]/domChild[6]"));
        new Actions(driver).moveToElement(e, 100, 20).contextClick().build()
                .perform();
        compareScreen("removeEventContextMenu");
        driver.findElement(
                By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[0]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[3]/domChild[0]/domChild[5]"))
                .click();
        compareScreen("NoContextMenu");
    }
}
