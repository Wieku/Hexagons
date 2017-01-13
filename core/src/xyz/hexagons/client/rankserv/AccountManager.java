package xyz.hexagons.client.rankserv;

import com.google.common.eventbus.Subscribe;
import org.javatuples.Pair;
import xyz.hexagons.client.Instance;
import xyz.hexagons.client.menu.settings.Settings;
import xyz.hexagons.client.menu.settings.SettingsManager;
import xyz.hexagons.client.utils.REST;

import java.io.Serializable;

public abstract class AccountManager {
    public abstract Account loginGoogle();
	public abstract Account loginSteam();

    public abstract class AuthInfo {
        public abstract String toString();
    }

    public abstract class Account {
        public abstract String nick();
        public abstract AuthInfo authToken();
    }

    public final void loginSaved() {
        if(Instance.currentAccount == null && Settings.instance.ranking.authToken != null) {
            Instance.executor.execute(() -> {
                Pair<AccountResponse, String> acc = REST.getJWS(Settings.instance.ranking.server + "/v1/nick?token=" + Settings.instance.ranking.authToken, AccountResponse.class);
                Instance.eventBus.post((EventUpdateNick) () -> acc.getValue0().account);
                Settings.instance.ranking.authToken = acc.getValue1();
                SettingsManager.saveSettings();

                Account account = new AccountManager.Account() {
                    @Override
                    public String nick() {
                        return acc.getValue0().account;
                    }

                    @Override
                    public AuthInfo authToken() {
                        return new AuthInfo() {
                            @Override
                            public String toString() {
                                return acc.getValue1();
                            }
                        };
                    }
                };
                
                Instance.currentAccount = account;
                
                Instance.eventBus.post(new EventLogin() {
                    @Override
                    public AccountManager.Account getAccount() {
                        return account;
                    }
                });
                
            });
        }
    }

    @Subscribe
    public void onLogin(EventLogin event) {
        Settings.instance.ranking.authToken = event.getAccount().authToken().toString();
        SettingsManager.saveSettings();
    }

    private static class AccountResponse implements Serializable {
        public String account;
        public int id;
    }
}
