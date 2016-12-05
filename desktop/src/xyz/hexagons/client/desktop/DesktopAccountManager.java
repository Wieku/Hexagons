package xyz.hexagons.client.desktop;

import org.javatuples.Pair;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.rankserv.AccountManager;
import xyz.hexagons.client.rankserv.EventUpdateNick;
import xyz.hexagons.client.utils.Holder;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


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
                    return onLogin(acc.getValue0(), acc.getValue1());
                } else return null;
            }
        }
        return null;
    }

    private AccountManager.Account onLogin(Account account, String token) {
        if(account.account.matches("^u\\d+$")) {
            Holder<ScheduledFuture> f  = new Holder<>();

            f.value = Instance.executor.scheduleWithFixedDelay(() -> {
                if(f.value != null) {
                    Account newAcc = REST.get(Settings.instance.ranking.server + "/v0/nick?token=" + token, Account.class);
                    if(newAcc != null && !newAcc.account.matches("^u\\d+$")) {
                        f.value.cancel(false);
                        f.value = null;
                        Instance.eventBus.post((EventUpdateNick) () -> newAcc.account);
                    }
                }
            }, 20, 10, TimeUnit.SECONDS);
        }

        return new AccountManager.Account() {
            @Override
            public String nick() {
                return account.account;
            }

            @Override
            public AuthInfo authInfo() {
                return new AuthInfo() {
                    @Override
                    public String toString() {
                        return token;
                    }
                };
            }
        };
    }

    private static class Challenge implements Serializable {
        public String challenge;
    }

    private static class Account implements Serializable {
        public String account;
        public int id;
    }
}
