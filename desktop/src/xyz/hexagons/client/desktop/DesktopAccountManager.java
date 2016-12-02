package xyz.hexagons.client.desktop;

import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.rankserv.AccountManager;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;


public class DesktopAccountManager implements AccountManager {
    @Override
    public void loginGoogle() {
        if(Desktop.isDesktopSupported()) {
            Challenge c = REST.get(Settings.instance.ranking.server + "/auth/google/challenge", Challenge.class);
            if(c != null && c.challenge != null) {
                try {
                    Desktop.getDesktop().browse(new URI(Settings.instance.ranking.server + "/auth/google/in?challenge=" + c.challenge));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                Account acc = REST.get(Settings.instance.ranking.server + "/auth/google/poll?challenge=" + c.challenge, Account.class);
                System.out.println("ACCOUNT: " + (acc == null ? "null" : acc.account));
            }
        }
    }

    private static class Challenge implements Serializable {
        public String challenge;
    }

    private static class Account implements Serializable {
        public String account;
    }
}
