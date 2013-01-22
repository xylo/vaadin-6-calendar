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

/*
 * #%L
 * Vaadin Calendar
 * %%
 * Copyright (C) 2010 - 2013 Vaadin Ltd
 * %%
 * This program is available under GNU Affero General Public License (version
 * 3 or later at your option).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
