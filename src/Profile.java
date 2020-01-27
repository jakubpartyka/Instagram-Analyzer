public class Profile {
    private String link;
    private String name;
    Report currentReport;
    Report previousReport;

    Profile(String link) {
        this.link = link;
    }

    Profile(String link, String name) {
        this.link = link;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.link + " name: " + this.name;
    }

    String getLink() {
        return link;
    }

    public void setCurrentReport(Report currentReport) {
        this.currentReport = currentReport;
    }

    public void setPreviousReport(Report previousReport) {
        this.previousReport = previousReport;
    }
}
