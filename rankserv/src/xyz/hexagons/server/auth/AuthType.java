package xyz.hexagons.server.auth;

public enum AuthType {
    GOOGLE(0),
	STEAM(1);

    public final int type;
    AuthType(int type) {
        this.type = type;
    }
}
