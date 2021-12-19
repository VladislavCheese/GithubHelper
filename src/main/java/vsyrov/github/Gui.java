package vsyrov.github;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Gui {

    private final TrayIcon trayIcon;

    public Gui() {
        try {
            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/github_icon1.png"));
            trayIcon = new TrayIcon(image, "GitHub helper");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("GitHub helper");
            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTrayMenu(String userLogin, List<RepositoryDescription> repositoryDescriptions) {
        MenuItem accountMI = new MenuItem(userLogin);
        accountMI.addActionListener(e -> openInBrowser("https://github.com/" + userLogin));

        MenuItem notificationMI = new MenuItem("notification");
        notificationMI.addActionListener(e -> openInBrowser("https://github.com/notification"));

        Menu repositoriesMenu = new Menu("repositories");
        repositoryDescriptions.forEach(repoDesc -> {
            String name = repoDesc.isPullRequestExists()
                    ? String.format("(%d) %s", repoDesc.getPullRequestUrls().size(), repoDesc.getRepoName())
                    : repoDesc.getRepoName();
            Menu repoSubMenu = new Menu(name);

            MenuItem openInBrowser = new MenuItem("Open in Browser");
            openInBrowser.addActionListener(e ->
                    openInBrowser(repoDesc.getRepoUrl())
            );
            repoSubMenu.add(openInBrowser);

            if (repoDesc.isPullRequestExists()) {
                repoSubMenu.addSeparator();

                repoDesc.getPullRequestUrls().forEach(prURL -> {
                    MenuItem pullRequestMI = new MenuItem("View pull request");
                    pullRequestMI.addActionListener(e -> openInBrowser(prURL));
                    repoSubMenu.add(pullRequestMI);
                });
            }

            repositoriesMenu.add(repoSubMenu);
        });

        PopupMenu popup = new PopupMenu();
        popup.add(accountMI);
        popup.addSeparator();
        popup.add(notificationMI);
        popup.add(repositoriesMenu);

        trayIcon.setPopupMenu(popup);
    }

    private void openInBrowser(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void showNotifications(String title, String text) {
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
