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

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class SizeTest extends CalendarTestBenchTest {

    public SizeTest(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void test1000x600px() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&restartApplication&width=1000px&height=600px"));
        verifySizes("1000x600px");
    }

    private void verifySizes(String screenshotPrefix) throws IOException,
            AssertionError {
        assertEquals(
                "Jan 2000",
                driver.findElement(
                        By.vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[1]/VHorizontalLayout[0]/ChildComponentContainer[1]/VLabel[0]"))
                        .getText());
        compareScreen(screenshotPrefix + "-1");
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[2]/VCalendar[0]/domChild[0]/domChild[1]/domChild[0]/domChild[0]/domChild[1]/domChild[2]/domChild[0]/domChild[0]")))
                .click(7, 53);
        compareScreen(screenshotPrefix + "-2");
    }

    @Test
    public void test100percentXundefined() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&restartApplication&width=100%25"));
        verifySizes("100percentXundefined");
    }

    @Test
    public void test100x100percent() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&restartApplication&width=100%25&height=100%25"));
        verifySizes("100x100percent");
    }

    @Test
    public void test300pxXundefined() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest?testBench&restartApplication&width=300px"));
        verifySizes("300pxXundefined");
    }

    @Test
    public void testSizeFull() throws Exception {
        driver.get(concatUrl(BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest??testBench&restartApplication"));
        verifySizes("SizeFull");
    }

    @Test
    public void test300px() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest??testBench&restartApplication&height=300px"));
        verifySizes("300px");
    }

    @Test
    public void testUndefinedXUndefined() throws Exception {
        driver.get(concatUrl(
                BASEURL,
                "/com.vaadin.addon.calendar.demo.CalendarTest??testBench&restartApplication&width=&height="));
        verifySizes("UndefinedXUndefined");
    }
}
