package com.Eqinox.store.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class QrSecurityUtil {

    private static final String SECRET = "SUPER_SECRET_QR_KEY";

    public static String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    SECRET.getBytes(),
                    "HmacSHA256"
            ));

            byte[] hash = mac.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("QR signing failed");
        }
    }

    public static boolean verify(String payload, String signature) {
        return sign(payload).equals(signature);
    }
}
