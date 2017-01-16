package xyz.hexagons.server.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import xyz.hexagons.server.Settings;

import java.security.SecureRandom;

public class RuntimeSecrets {
    public static final MACSigner sessionSigner;
    public static final MACVerifier sessionVerifier;

    static {
        MACSigner signer = null;
        MACVerifier verifier = null;
        try {
            signer = new MACSigner(Settings.instance.signSecret);
        } catch (KeyLengthException e) {
            e.printStackTrace();
        }
        try {
            verifier = new MACVerifier(Settings.instance.signSecret);
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        sessionSigner = signer;
        sessionVerifier = verifier;
    }

    public static String signSession(String payload) {
        JWSObject o = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload));
        try {
            o.sign(sessionSigner);
            return o.serialize();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean check(JWSObject object) {
        try {
            return object.verify(sessionVerifier);
        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return false;
    }
}
