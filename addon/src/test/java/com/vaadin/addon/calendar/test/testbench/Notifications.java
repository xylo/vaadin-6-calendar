package com.vaadin.addon.calendar.test.testbench;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

public class Notifications extends CalendarTestBenchTest {

    public Notifications(DesiredCapabilities capabilities) {
        super(capabilities);
    }

    @Test
    public void testNotifications() throws Exception {
        driver.get(concatUrl(BASEURL,
                "/com.vaadin.addon.calendar.test.NotificationTestUI?restartApplication"));
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::/VGridLayout[0]/ChildComponentContainer[1]/VCalendar[0]/domChild[0]/domChild[1]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[2]/domChild[0]/domChild[0]")))
                .click(83, 11);
        testBenchElement(
                driver.findElement(By
                        .vaadin("ROOT::Root/VNotification[0]/HTML[0]/domChild[0]")))
                .closeNotification();
        compareScreen("no-notification");
    }
}
