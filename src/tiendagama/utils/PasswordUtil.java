package tiendagama.utils;

import java.security.MessageDigest;

public class PasswordUtil {

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 
     * Compara: si stored parece ser hash (64 hex chars) lo compara con hash(input),
     * si stored no es hash, compara directamente (compatibilidad con DB existente).
     */
    public static boolean matches(String inputPlain, String stored) {
        if (stored == null) return false;
        if (stored.matches("^[0-9a-fA-F]{64}$")) {
            return sha256(inputPlain).equalsIgnoreCase(stored);
        } else {
            return inputPlain.equals(stored);
        }
    }
}
