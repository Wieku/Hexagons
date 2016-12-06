package xyz.hexagons.client.rankserv;

public interface AccountManager {
    Account loginGoogle();

    abstract class AuthInfo {
        public abstract String toString();
    }

    abstract class Account {
        public abstract String nick();
        public abstract AuthInfo authToken();
    }
}
