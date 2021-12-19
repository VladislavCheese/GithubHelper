package vsyrov.github;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RepoDescription {
    private final String repoName;
    private final String repoUrl;
    private final boolean pullRequestExists;
    private final int pullRequestsCount;
    private final List<PullRequestModel> pullRequestModels;

    public RepoDescription(GHRepository repository, List<GHPullRequest> pullRequests) {
        this.repoName = repository.getName();
        this.repoUrl = repository.getHtmlUrl().toString();
        if (pullRequests != null) {
            this.pullRequestExists = pullRequests.size() != 0;
            this.pullRequestsCount = pullRequests.size();
            this.pullRequestModels = pullRequests.stream()
                    .map(pr -> new PullRequestModel(pr.getId(), pr.getHtmlUrl().toString(), pr.getTitle()))
                    .collect(Collectors.toList());
        } else {
            this.pullRequestExists = false;
            this.pullRequestsCount = 0;
            this.pullRequestModels = Collections.emptyList();
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

    public int getPullRequestsCount() {
        return pullRequestsCount;
    }

    public List<PullRequestModel> getPullRequestModels() {
        return pullRequestModels;
    }
}
