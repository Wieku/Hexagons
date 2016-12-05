package xyz.hexagons.server.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import java.security.SecureRandom;

public class RuntimeSecrets {
    public static final MACSigner sessionSigner;
    public static final MACVerifier sessionVerifier;

    static {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32];
        random.nextBytes(key);
        MACSigner signer = null;
        MACVerifier verifier = null;
        try {
            signer = new MACSigner(key);
        } catch (KeyLengthException e) {
            e.printStackTrace();
        }
        try {
            verifier = new MACVerifier(key);
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
}
