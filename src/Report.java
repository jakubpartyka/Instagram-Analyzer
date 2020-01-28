import org.openqa.selenium.*;
import org.openqa.selenium.Point;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class Report {
    private int mouseWheelValue = -30;
    private Profile target;
    Date date;
    private String status;

    Report(Profile target) {
        this.target = target;
    }

    void generate(InstagramDriver driver) throws InterruptedException {
        //this method generates a new report
        driver.logIn();
        driver.get(target.getLink());

        WebElement followers = driver.findElements(By.xpath("//*[@class='g47SY ']")).get(1);
        int followersCount = Integer.parseInt(followers.getText());
        if(followersCount > 5000){
            log("skipping profile because follower count is over 5000: " + target);
            return;
        }

        followers.click();
        Thread.sleep(500);

        //locating first friend tab element
        WebElement firstProfile;
        try {
            firstProfile = driver.findElement(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
        }
        catch (NoSuchElementException e){
            //if now found wait for rest of the page to load and try again
            Thread.sleep(700);
            try {
                firstProfile = driver.findElement(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
            }
            catch (NoSuchElementException e1) {
                log("no profiles were  found - account may be private");
                log(e.getMessage());
                this.setStatus("private");
                return;
            }
        }

        Robot robot;
        //focus on popup window
        try {
            Point cords = firstProfile.getLocation();
            robot = new Robot();
            robot.mouseMove(cords.getX() + 200, cords.getY() + 300);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
        catch (AWTException e){
            log("AWTException was thrown");
            log(e.getMessage());
            this.setStatus("unsuccessful");
            return;
        }

        Thread.sleep(100);

        List<WebElement> profiles = driver.findElements(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
        int initialSize;
        do {
            //in case of problems at this point try changing mouseWheelValue to +30 (positive) instead of -30
            //! - javaScriptExecutors does not work here! Robot class has to be used instead.
            //! - only mouseWheel(int amt) method works. Instagram anti-bot system seems pretty effective
            initialSize = profiles.size();
            robot.mouseWheel(mouseWheelValue);
            robot.mouseWheel(mouseWheelValue);
            robot.mouseWheel(mouseWheelValue);
            Thread.sleep(50);
            robot.mouseWheel(mouseWheelValue);
            robot.mouseWheel(mouseWheelValue);
            Thread.sleep(300);
            profiles = driver.findElements(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
            if(profiles.size() == initialSize){
                Thread.sleep(200);
                profiles = driver.findElements(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
                if(profiles.size() == initialSize){
                    //wait for the rest page to load
                    Thread.sleep(500);
                    profiles = driver.findElements(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
                }
            }
        } while (profiles.size() > initialSize);        //scrolling down until new profiles appear
    }

    private static void log(String message) {
        //this function logs all actions performed by the program.
        if(Main.isLogToFile()){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("[DD-MM-YYYY-HH:mm:ss]: ");
                String ts = sdf.format(Calendar.getInstance().getTime());
                FileWriter fileWriter = new FileWriter("log.txt",true);
                fileWriter.write(ts + "[Report]" + message + "\n");
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("an error occurred while logging to file...");
                e.printStackTrace();
            }
        }
        if (Main.isLogToConsole())
            System.out.println(message);
    }

    private void setStatus(String status) {
        log("setting status to " + status);
        this.status = status;
    }
}
