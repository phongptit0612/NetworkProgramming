package vn.edu.network.common.crypto;

import java.util.Locale;
import java.util.Objects;

/** Classical A-Z Vigenere. Non-ASCII letters are preserved and do not consume key positions. */
public final class VigenereCipher {
    private VigenereCipher() { }

    public static String encrypt(String text, String key) {
        return transform(text, key, 1);
    }

    public static String decrypt(String text, String key) {
        return transform(text, key, -1);
    }

    public static String validateKey(String key) {
        if (key == null || !key.matches("[A-Za-z]+")) {
            throw new IllegalArgumentException("Khoa phai gom cac chu cai A-Z, khong de trong.");
        }
        return key.toUpperCase(Locale.ROOT);
    }

    private static String transform(String text, String key, int direction) {
        Objects.requireNonNull(text, "text");
        String normalizedKey = validateKey(key);
        StringBuilder result = new StringBuilder(text.length());
        int position = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean upper = c >= 'A' && c <= 'Z';
            boolean lower = c >= 'a' && c <= 'z';
            if (upper || lower) {
                int base = upper ? 'A' : 'a';
                int shift = normalizedKey.charAt(position % normalizedKey.length()) - 'A';
                result.append((char) (base + Math.floorMod(c - base + direction * shift, 26)));
                position++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
