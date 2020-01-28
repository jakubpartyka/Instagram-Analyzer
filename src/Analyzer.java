import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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

    private void analyze(Profile profile) {
        Report newReport = new Report(profile);
        try {
            newReport.generate(driver);
            newReport.writeToFile(outputDir);
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
