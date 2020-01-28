import org.openqa.selenium.*;
import org.openqa.selenium.Point;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("FieldCanBeLocal")
class Report {
    private List<Profile> followers = new ArrayList<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("DD.MM.YYYY HH:mm");
    private int mouseWheelValue = -30;
    private Profile target;
    private String status;
    private Date date;
    private File outputFile;
    private boolean completed = false;

    Report(Profile target) {
        this.target = target;
    }

    void generate(InstagramDriver driver) throws InterruptedException {
        //this method generates a new report
        driver.logIn();
        driver.get(target.getLink());

        WebElement followers = driver.findElements(By.xpath("//*[@class='g47SY ']")).get(1);
        int expectedFollowersSize = 0;
        try {
            expectedFollowersSize = Integer.parseInt(followers.getText());
        }
        catch (Exception ignored){}

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


        //focus on popup window
        Robot robot;
        try {
            robot = new Robot();
            Point cords = firstProfile.getLocation();
            //moving to right side of the first profile in popup window to focus
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
        int initialSize,
                failCounter = 0;    //this counts how many times number of loaded profiles was incorrect (different from expected by 10 or more)
        log("loading profiles");
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
                robot.mouseWheel(mouseWheelValue);
                failCounter++;
                profiles = driver.findElements(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
                if(profiles.size() == initialSize){
                    //wait for the rest page to load
                    Thread.sleep(400);
                    robot.mouseWheel(mouseWheelValue);
                    failCounter++;
                    profiles = driver.findElements(By.xpath("//*[@class='FPmhX notranslate  _0imsa ']"));
                    if(expectedFollowersSize - followersCount > 5){
                        Thread.sleep(500);      //additional wait based on expected profile count
                        robot.mouseWheel(mouseWheelValue);
                        failCounter++;
                    }
                }
            }
            if(failCounter > 10) {
                log("breaking loop because of fail count");
                break;              //if 10 fails occurs there's a different problem and loop should be exited
            }
        } while (profiles.size() > initialSize || expectedFollowersSize - profiles.size() < 10);        //scrolling down as long as new profiles appear
        log("loading profiles finished. Loaded profiles: " + profiles.size());

        profiles.forEach(profile -> {
            String link = profile.getAttribute("href");
            //name location could be handled here
            this.followers.add(new Profile(link));      //adding to report list
            if(Main.isLogToConsole())
                System.out.println("added: " + link);
        });
        log("listing followers complete. Number of followers listed: " + this.followers.size());

        completed = true;
        setDateNow();   //set the date of report. This should be the last instruction in this method
        log("generating report completed");
    }

    private void setDateNow() {
        this.date = Calendar.getInstance().getTime();
        log("date set to " + dateFormat.format(date));
    }

    boolean writeToFile(File directory){
        if(!completed){
            log("report for: " + target + " was not completed successfully");
            return false;
        }
        outputFile = new File(directory + "/" + dateFormat.format(date) + ".txt");
        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(this.toString());
            fileWriter.close();
            return true;
        } catch (IOException e) {
            log(e.getMessage());
            log("failed to write report");
            return false;
        }
    }

    boolean appendToReport(String message){
        if(!completed || outputFile == null){
            log("report for: " + target + " was not completed successfully");
            return false;
        }
        try {
            FileWriter fileWriter = new FileWriter(outputFile,true);
            fileWriter.write("\n\n\n" + message);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            log(e.getMessage());
            log("failed to write report");
            return false;
        }
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

    @Override
    public String toString() {
        AtomicReference<String> result = new AtomicReference<>("");
        followers.forEach(follower -> result.updateAndGet(v -> v + follower + "\n"));
        return result.get();
    }

    File getOutputFile() {
        return outputFile;
    }

    List<Profile> getFollowers() {
        return followers;
    }
}
