package vsyrov.github;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.List;
import java.util.stream.Collectors;

public class RepositoryDescription {
    private final String repoName;
    private final String repoUrl;
    private boolean pullRequestExists;
    private List<String> pullRequestUrls;

    public RepositoryDescription(GHRepository repository, List<GHPullRequest> pullRequests) {
        this.repoName = repository.getName();
        this.repoUrl = repository.getHtmlUrl().toString();
        if (pullRequests != null) {
            this.pullRequestExists = pullRequests.size() != 0;
            this.pullRequestUrls = pullRequests.stream()
                    .map(pr -> pr.getHtmlUrl().toString())
                    .collect(Collectors.toList());
        }
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public boolean isPullRequestExists() {
        return pullRequestExists;
    }

    public List<String> getPullRequestUrls() {
        return pullRequestUrls;
    }
}
