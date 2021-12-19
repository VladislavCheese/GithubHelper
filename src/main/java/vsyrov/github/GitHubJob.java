package vsyrov.github;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class GitHubJob {

    private final GitHub github;
    private final Gui gui = new Gui();
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
        String userLogin = myself.getLogin();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Set<GHPullRequest> newPullRequests = new HashSet<>();

                    List<RepositoryDescription> repositoryDescriptions = myself.getAllRepositories()
                            .values()
                            .stream()
                            .map(repository -> {
                                try {
                                    List<GHPullRequest> pullRequests =
                                            repository.getPullRequests(GHIssueState.OPEN);

                                    Set<Long> prIds = pullRequests.stream()
                                            .map(GHPullRequest::getId)
                                            .filter(prId -> !viewedPrIds.contains(prId))
                                            .collect(Collectors.toSet());
                                    viewedPrIds.addAll(prIds);

                                    pullRequests.forEach(pr -> {
                                        if (prIds.contains(pr.getId())) {
                                            newPullRequests.add(pr);
                                        }
                                    });

                                    return new RepositoryDescription(repository, pullRequests);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }).collect(Collectors.toList());

                    gui.setTrayMenu(userLogin, repositoryDescriptions);
                    newPullRequests.forEach(pr -> {
                        gui.showNotifications("New MR in " + pr.getRepository().getFullName(),
                                pr.getTitle());
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 1000);
    }
}
