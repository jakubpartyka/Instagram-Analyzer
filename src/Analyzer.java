import java.util.List;

public class Analyzer {
    private InstagramDriver driver;
    private List<Profile> profiles;

    public Analyzer(InstagramDriver driver, List<Profile> profiles) {
        this.driver = driver;
        this.profiles = profiles;
    }
}
