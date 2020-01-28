import java.util.Objects;

public class Profile {
    private String link;
    private String name;
    private Report currentReport;
    private Report previousReport;

    Profile(String link) {
        this.link = link;
    }

    Profile(String link, String name) {
        this.link = link;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Profile profile = (Profile) o;
        return Objects.equals(this.link, profile.link);
    }
}
