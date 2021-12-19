package vsyrov.github;

public class PullRequestModel {
    private final long id;
    private final String URL;
    private final String title;
    private final String shortName;

    public PullRequestModel(long id, String URL, String title) {
        this.id = id;
        this.URL = URL;
        this.title = title;
        //зададим максимальное отображаемое название PR
        if (title.length() > 25) {
            this.shortName = String.format("%s...", title.substring(0, 23));
        } else {
            this.shortName = title;
        }
    }

    public long getId() {
        return id;
    }

    public String getURL() {
        return URL;
    }

    public String getTitle() {
        return title;
    }

    public String getShortName() {
        return shortName;
    }
}
