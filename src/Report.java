import java.util.Date;

class Report {
    private Profile target;
    Date date;

    Report(Profile target) {
        this.target = target;
    }

    void generate(InstagramDriver driver) throws InterruptedException {
        //this method generates a new report
        driver.logIn();
        driver.get(target.getLink());

    }
}
