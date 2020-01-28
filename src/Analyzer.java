import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("UnnecessaryReturnStatement")
public class Analyzer {
    private InstagramDriver driver;
    private List<Profile> profiles;
    private File outputDir;

    public Analyzer(InstagramDriver driver, List<Profile> profiles) {
        this.driver = driver;
        this.profiles = profiles;
        createOutputDirectories();              //create output directories for all profiles
    }

    private void createOutputDirectories() {
        for (Profile profile : profiles) {
            String dirName = profile.getLink().substring(26);
            outputDir = new File("profiles/" + dirName + "/");
            if(!outputDir.exists()){
                if(outputDir.mkdir())
                    log("created new output directory: " + outputDir.getAbsolutePath());
                else
                    log("failed to create output directory: " + outputDir.getAbsolutePath());
            }
        }
    }

    private static void log(String message) {
        //this function logs all actions performed by the program.
        if(Main.isLogToFile()){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("[DD-MM-YYYY-HH:mm:ss]: ");
                String ts = sdf.format(Calendar.getInstance().getTime());
                FileWriter fileWriter = new FileWriter("log.txt",true);
                fileWriter.write(ts + "[Analyze]" + message + "\n");
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("an error occurred while logging to file...");
                e.printStackTrace();
            }
        }
        if (Main.isLogToConsole())
            System.out.println(message);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private void analyze(Profile profile) {
        Report newReport = new Report(profile);
        try {
            //generating new report
            newReport.generate(driver);

            //saving new report to output file
            if(newReport.writeToFile(outputDir))
                log("report saved to: " + newReport.getOutputFile().getAbsolutePath());
            else
                log("failed to save report to file");

            //setting new reports
            if(profile.getCurrentReport() == null){
                profile.setCurrentReport(newReport);
            }
            else {
                profile.setPreviousReport(profile.getCurrentReport());
                profile.setCurrentReport(newReport);
            }

            //check if two reports are present
            if(profile.getPreviousReport() == null){
                log("previous report for profile " + profile + " is empty");
                return;
            }
            else {
                //analyze current report compared to previous one
                List<Profile> currentFollowers = new ArrayList<>(profile.getCurrentReport().getFollowers());
                List<Profile> previousFollowers = new ArrayList<>(profile.getPreviousReport().getFollowers());
                log("both followers list loaded, analyzing...");

                List<Profile> newFollowers = new ArrayList<>(currentFollowers);
                newFollowers.removeAll(previousFollowers);

                List<Profile> unFollowed = previousFollowers;
                unFollowed.removeAll(currentFollowers);

                if(newFollowers.size() != 0){
                    StringBuilder result = new StringBuilder("new followers:\n");
                    for (Profile newFollower : newFollowers) {
                        result.append(newFollower.toString()).append("\n");
                    }
                    log(result.toString());
                    if(profile.getCurrentReport().appendToReport(result.toString()))
                        log("new followers appended successfully");
                    else
                        log("could not not append new followers");
                }

                if(unFollowed.size() != 0){
                    StringBuilder result = new StringBuilder("people who un-followed you:\n");
                    for (Profile unFollower : unFollowed) {
                        result.append(unFollower.toString()).append("\n");
                    }
                    log(result.toString());
                    if(profile.getCurrentReport().appendToReport(result.toString()))
                        log("un-followers appended successfully");
                    else
                        log("could not append un-followers");
                }
            }
        } catch (InterruptedException e) {
            log("failed to generate report for profile " + profile);
            log(e.getMessage());
        }
    }

    void analyzeAll() {
        for (Profile profile : profiles) {
            analyze(profile);
        }
    }
}
