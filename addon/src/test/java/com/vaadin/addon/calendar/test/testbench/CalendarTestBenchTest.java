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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.vaadin.addon.calendar.test.TestServer;
import com.vaadin.testbench.By;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.TestBench;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.commands.TestBenchCommands;

@RunWith(Parameterized.class)
public abstract class CalendarTestBenchTest extends TestBenchTestCase {
    protected static final int TESTPORT = 5678;
    protected static String BASEURL = "http://localhost:" + TESTPORT + "/";
    private static final String REF_IMAGE_ROOT = "src/test/resources/screenshots/reference";
    protected WebDriver driver;
    protected TestBenchCommands testBench;
    private Server server;
    protected static final String ERROR_IMAGE_ROOT = "target/testbench/errors/";

    @Parameterized.Parameters
    public static List<DesiredCapabilities[]> parameters() {
        DesiredCapabilities chrome = DesiredCapabilities.chrome();
        chrome.setPlatform(Platform.VISTA);
        DesiredCapabilities firefox = DesiredCapabilities.firefox();
        return Arrays.asList(new DesiredCapabilities[][] { { chrome },
                { firefox } });
    }

    // Called with each of the DesiredCapabilities returned by the parameters
    // method above.
    public CalendarTestBenchTest(DesiredCapabilities capabilities) {
        String hubhost = System.getProperty("tb.hub");
        if (hubhost != null && !hubhost.isEmpty()) {
            try {
                BASEURL = "http://"
                        + InetAddress.getLocalHost().getHostAddress() + ":"
                        + TESTPORT + "/";
                URL remoteAddress = new URL("http://" + hubhost
                        + ":4444/wd/hub");
                driver = TestBench.createDriver(new RemoteWebDriver(
                        remoteAddress, capabilities));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e1);
            }
        } else {
            WebDriver wd = null;
            if ("firefox".equalsIgnoreCase(capabilities.getBrowserName())) {
                wd = new FirefoxDriver(capabilities);
            } else if ("chrome".equalsIgnoreCase(capabilities.getBrowserName())) {
                wd = new ChromeDriver(capabilities);
            }
            // Throws an NPE if wd is null.
            driver = TestBench.createDriver(wd);
        }
    }

    protected void startBrowser() {
        // prepareDriver();
        testBench = (TestBenchCommands) driver;
        // dimension includes browser chrome, use TestBench utility to fix
        // actual viewport size -> survive from browser upgrades and varying
        // settings
        testBench.resizeViewPortTo(1024, 768);
    }

    protected void prepareDriver() {
        String hubhost = System.getProperty("tb.hub");
        if (hubhost != null && !hubhost.isEmpty()) {
            try {
                BASEURL = InetAddress.getLocalHost().getHostName() + ":"
                        + TESTPORT + "/";
                Capabilities cap = DesiredCapabilities.firefox();
                URL remoteAddress = new URL("http://" + hubhost
                        + ":4444/wd/hub");
                driver = TestBench.createDriver(new RemoteWebDriver(
                        remoteAddress, cap));
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e1);
            }
        } else {
            driver = TestBench.createDriver(new FirefoxDriver());
            // driver = TestBench.createDriver(new ChromeDriver());
        }
    }

    public File getReferenceImage(String name) {
        return new File(REF_IMAGE_ROOT, name);
    }

    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        new File(ERROR_IMAGE_ROOT).mkdirs();
        Parameters.setScreenshotErrorDirectory(ERROR_IMAGE_ROOT);
        Parameters.setScreenshotReferenceDirectory(REF_IMAGE_ROOT);
        Parameters.setScreenshotComparisonTolerance(0.01);
        Parameters.setCaptureScreenshotOnFailure(true);
        server = TestServer.startServer(TESTPORT);

        startBrowser();
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }

        driver.quit();

        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }

    }

    // Calendar utilities
    protected void createEvent(String startTime, String endTime, String caption) {
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[0]#field",
                startTime);
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VPopupCalendar[1]#field",
                endTime);
        clearAndType(
                "ROOT::/VWindow[0]/FocusableScrollPanel[0]/VVerticalLayout[0]/ChildComponentContainer[0]/VFormLayout[0]/VFormLayout$VFormLayoutTable[0]/VTextField[0]",
                caption);
    }

    protected void compareScreen(String imageQualifier) throws IOException {
        try {
            sleep(500);
            if (!testBench.compareScreen(imageQualifier)) {
                verificationErrors.append("Screen comparison failed for ")
                        .append(imageQualifier).append("\n");
            }
        } catch (AssertionError e) {
            verificationErrors.append(e.getMessage()).append("\n");
        }
    }

    protected void clearAndType(String vaadinLocator, String keys) {
        WebElement element = driver.findElement(By.vaadin(vaadinLocator));
        element.clear();
        element.sendKeys(keys);
    }

}
