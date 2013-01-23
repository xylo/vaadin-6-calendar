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
