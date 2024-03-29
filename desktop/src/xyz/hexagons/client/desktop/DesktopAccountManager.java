package xyz.hexagons.client.desktop;

import org.javatuples.Pair;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.rankserv.AccountManager;
import xyz.hexagons.client.rankserv.EventLogin;
import xyz.hexagons.client.rankserv.EventUpdateNick;
import xyz.hexagons.client.utils.Holder;
import xyz.hexagons.client.utils.REST;

import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class DesktopAccountManager extends AccountManager {

    public DesktopAccountManager() {
        Instance.eventBus.register(this);
    }

    @Override
    public AccountManager.Account loginGoogle() {
        if(Desktop.isDesktopSupported()) {
            Challenge c = REST.get(Settings.instance.ranking.server + "/v1/auth/challenge", Challenge.class);
            if(c != null && c.challenge != null) {
                try {
                    Desktop.getDesktop().browse(new URI(Settings.instance.ranking.server + "/v1/auth/google/in?challenge=" + c.challenge));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                return pollAccount(c);
            }
        }
        return null;
    }

	@Override
	public AccountManager.Account loginSteam() {
        if(Desktop.isDesktopSupported()) {
            Challenge c = REST.get(Settings.instance.ranking.server + "/v1/auth/challenge", Challenge.class);
            if(c != null && c.challenge != null) {
                try {
                    Desktop.getDesktop().browse(new URI(Settings.instance.ranking.server + "/v1/auth/steam/in?challenge=" + c.challenge));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                return pollAccount(c);
            }
        }
		return null;
	}

	private AccountManager.Account pollAccount(Challenge c) {
        Pair<Account, String> acc = REST.getJWS(Settings.instance.ranking.server + "/v1/auth/poll?challenge=" + c.challenge, Account.class);
        System.out.println("ACCOUNT: " + (acc == null ? "null" : acc.getValue0().account));
        if(acc != null) {
            AccountManager.Account account = onLogin(acc.getValue0(), acc.getValue1());
            Instance.eventBus.post(new EventLogin() {
                @Override
                public AccountManager.Account getAccount() {
                    return account;
                }
            });
            return account;
        }
        return null;
    }

	private AccountManager.Account onLogin(Account account, String t) {
        Holder<String> token = new Holder<>(t);

        if(account.account.matches("^u\\d+$")) {
            Holder<ScheduledFuture> f  = new Holder<>();

            f.value = Instance.executor.scheduleWithFixedDelay(() -> {
                if(f.value != null) {
                    Pair<Account, String> newAcc = REST.getJWS(Settings.instance.ranking.server + "/v1/nick?token=" + token.value, Account.class);
                    if(newAcc != null && !newAcc.getValue0().account.matches("^u\\d+$")) {
                        f.value.cancel(false);
                        f.value = null;
                        System.out.println("Update nick!");
                        token.value = newAcc.getValue1();
                        Instance.eventBus.post((EventUpdateNick) () -> newAcc.getValue0().account);
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
            public AuthInfo authToken() {
                return new AuthInfo() {
                    @Override
                    public String toString() {
                        return token.value;
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
