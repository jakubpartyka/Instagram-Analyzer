public class Profile {
    private String link;
    private String name;

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

    public String getLink() {
        return link;
    }
}
