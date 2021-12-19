package vsyrov.github;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.stream.Collectors;

public class GitHubJob {

    private final GitHub github;
    private final Set<Long> viewedPrIds = new HashSet<>();

    public GitHubJob() {
        try {
            github = new GitHubBuilder()
                    //токен можно получить на сайте github
                    .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                    .build();
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        GHMyself myself = github.getMyself();
        markExistingRepositoriesAsViewed(myself.getAllRepositories());

        System.out.println("!!!TASK STARTING!!!");
        new Timer().schedule(new TaskExecutor(myself, viewedPrIds), 1000, 1000);
    }

    private void markExistingRepositoriesAsViewed(Map<String, GHRepository> allRepositories) {
        allRepositories.values()
                .forEach(repository -> {
                    try {
                        viewedPrIds.addAll(repository.getPullRequests(GHIssueState.ALL)
                                .stream()
                                .map(GHPullRequest::getId)
                                .collect(Collectors.toSet()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
