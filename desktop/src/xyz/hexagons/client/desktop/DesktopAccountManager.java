package xyz.hexagons.client.desktop;

import org.javatuples.Pair;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.rankserv.AccountManager;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;


public class DesktopAccountManager implements AccountManager {
    @Override
    public AccountManager.Account loginGoogle() {
        if(Desktop.isDesktopSupported()) {
            Challenge c = REST.get(Settings.instance.ranking.server + "/auth/google/challenge", Challenge.class);
            if(c != null && c.challenge != null) {
                try {
                    Desktop.getDesktop().browse(new URI(Settings.instance.ranking.server + "/auth/google/in?challenge=" + c.challenge));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                Pair<Account, String> acc = REST.getJWS(Settings.instance.ranking.server + "/auth/google/poll?challenge=" + c.challenge, Account.class);
                System.out.println("ACCOUNT: " + (acc == null ? "null" : acc.getValue0().account));
                if(acc != null) {
                    return new AccountManager.Account() {
                        @Override
                        public String nick() {
                            return acc.getValue0().account;
                        }

                        @Override
                        public AuthInfo authInfo() {
                            return new AuthInfo() {
                                @Override
                                public String toString() {
                                    return acc.getValue1();
                                }
                            };
                        }
                    };
                } else return null;
            }
        }
        return null;
    }

    private static class Challenge implements Serializable {
        public String challenge;
    }

    private static class Account implements Serializable {
        public String account;
    }
}
