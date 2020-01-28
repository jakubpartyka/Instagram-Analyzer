import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Profile {
    private String link;
    private String name;
    private Report currentReport = new Report(this);
    private Report previousReport;
    private File   outputDir;

    Profile(String link) {
        this.link = link;
        outputDir = new File("profiles/" + link.substring(26));
    }

    Profile(String link, String name) {
        this.link = link;
        this.name = name;
        outputDir = new File("profiles/" + link.substring(26));
    }

    @Override
    public String toString() {
        String n = "#" + this.name;
        if(this.name == null)
            n = "";
        return this.link + n;
    }

    String getLink() {
        return link;
    }

    void setCurrentReport(Report currentReport) {
        this.currentReport = currentReport;
    }

    void setPreviousReport(Report previousReport) {
        this.previousReport = previousReport;
    }

    Report getCurrentReport() {
        return currentReport;
    }

    Report getPreviousReport() {
        return previousReport;
    }

    void loadReports() {
        try (Stream<Path> walk = Files.walk(Paths.get(outputDir.getAbsolutePath()))) {

            List<String> results = walk
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".txt"))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            File currentReportFile = new File(results.get(0));
            BufferedReader reader = new BufferedReader(new FileReader(currentReportFile));
            String line = reader.readLine();
            while (!line.equals("###")) {
                if (line.contains("#")) {
                    String[] tmp = line.split("#");
                    if (tmp.length == 1) {
                        //only link given in file
                        Profile prof = new Profile(tmp[0]);
                        currentReport.getFollowers().add(prof);
                        Main.log("added to report: " + prof);
                    } else if (tmp.length == 2) {
                        //link and name given in file
                        Profile prof = new Profile(tmp[0], tmp[1]);
                        currentReport.getFollowers().add(prof);
                        Main.log("added to report: " + prof);
                    } else {
                        Main.log("incorrect formatting of line: " + line);
                        line = reader.readLine();
                        continue;
                    }
                }
                else {
                    if(!line.startsWith("https://www.instagram.com/")) {
                        Main.log("incorrect formatting of line: " + line);
                        line = reader.readLine();
                        continue;
                    }
                    Profile prof = new Profile(line);
                    currentReport.getFollowers().add(prof);
                    Main.log("added to report: " + prof);
                }
                line = reader.readLine();
            }

        } catch (IOException e) {
            Main.log(e.getMessage());
            Main.log("could not load last report");
            //return
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Profile profile = (Profile) o;
        return Objects.equals(this.link, profile.link);
    }
}
