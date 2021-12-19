package vsyrov.github;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class TaskExecutor extends TimerTask {
    private final Gui gui = new Gui();
    private final GHMyself myself;
    private final Set<Long> viewedPrIds;

    public TaskExecutor(GHMyself myself, Set<Long> viewedPrIds) {
        this.myself = myself;
        this.viewedPrIds = viewedPrIds;
    }

    @Override
    public void run() {
        try {
            Set<GHPullRequest> newPullRequests = new HashSet<>();
            List<RepoDescription> repoDescriptions =
                    getRepoDescriptions(newPullRequests, myself.getAllRepositories());
            gui.setTrayMenu(myself.getLogin(), repoDescriptions);

            newPullRequests.forEach(pr ->
                    gui.showNotifications("New MR in " + pr.getRepository().getFullName(),
                            pr.getTitle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<RepoDescription> getRepoDescriptions(Set<GHPullRequest> newPullRequests,
                                                      Map<String, GHRepository> allRepositories) {
        return allRepositories.values()
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

                        return new RepoDescription(repository, pullRequests);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }
}
