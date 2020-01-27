import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InstagramDriver implements WebDriver {
    private WebDriver driver;

    InstagramDriver(){
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--user-agent=Android 4.2.1");
        if (Main.isHeadless())
            options.addArguments("--headless");
        driver = new ChromeDriver();
    }

    void logIn() throws InterruptedException {
        log("logging in...");
        driver.get("https://www.instagram.com/accounts/login/");

        WebElement username = ((ChromeDriver)driver).findElementByXPath("//*[@name='username']");
        WebElement password = ((ChromeDriver)driver).findElementByXPath("//*[@name='password']");
        WebElement submit = ((ChromeDriver)driver).findElementByXPath("//*[@type='submit']");
        username.sendKeys(Main.getLogin());
        password.sendKeys(Main.getPassword());
        Thread.sleep(100);
        submit.click();
        denyNotifications();
    }

    private void denyNotifications() {
        try {
            Thread.sleep(500);
            WebElement allow = ((ChromeDriver)driver).findElementByXPath("//*[@class='aOOlW   HoLwm']");
            allow.click();
        }
        catch (NoSuchElementException ignored){}
        catch (InterruptedException e){
            e.printStackTrace();
            log(e.getMessage());
        }
    }


    @Override
    public void get(String url) {
        log("navigating to " + url);
        driver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        log("locating elements by: " + by);
        return driver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        log("locating element by: " + by);
        return driver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        log("driver closing");
        driver.close();
    }

    @Override
    public void quit() {
        log("driver quitting");
        driver.close();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        log("driver's navigate() method was called");
        return driver.navigate();
    }

    @Override
    public Options manage() {
        log("driver's manage() method was called");
        log("driver returning: " + driver.manage().toString());
        return driver.manage();
    }

    private static void log(String message) {
        //this function logs all actions performed by the program.
        if(Main.isLogToFile()){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("[DD-MM-YYYY-HH:mm:ss]: ");
                String ts = sdf.format(Calendar.getInstance().getTime());
                FileWriter fileWriter = new FileWriter("log.txt",true);
                fileWriter.write(ts + "[DRIVER]" + message + "\n");
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("an error occurred while logging to file...");
                e.printStackTrace();
            }
        }
        if (Main.isLogToConsole())
            System.out.println(message);
    }
}
