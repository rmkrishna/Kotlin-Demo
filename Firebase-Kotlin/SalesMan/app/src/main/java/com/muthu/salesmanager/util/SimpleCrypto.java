
package com.muthu.salesmanager.util;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Usage:
 * <p>
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * <p>
 * <pre>
 * ferenc.hechler from:
 *   http://www.androidsnippets.com/encryptdecrypt-strings
 * also see:
 *   http://android-developers.blogspot.se/2013/02/using-cryptography-to-store-credentials.html
 * </pre>
 */
public class SimpleCrypto {
    private static final String TAG = SimpleCrypto.class.getSimpleName();

    private static final byte iv[] = {
            (byte) 0xB2, 0x12, (byte) 0xD5, (byte) 0xB2,
            0x44, 0x21, (byte) 0xC3, (byte) 0xC3
    };

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String desEncryptWithKey(String text, String key) {

        String result = null;

        try {
            byte[] textBytes = text.getBytes("UTF-8");

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "DES");

            IvParameterSpec ivParam = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParam);
            byte[] cipherText = new byte[cipher.getOutputSize(textBytes.length)];
            int ctLength = cipher.update(textBytes, 0, textBytes.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);

            result = Base64.encodeToString(cipherText, Base64.DEFAULT);

            result = result.trim();

        } catch (UnsupportedEncodingException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
        } catch (InvalidKeyException e) {
        } catch (ShortBufferException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (BadPaddingException e) {
        } catch (InvalidAlgorithmParameterException e) {
        }

        return result;
    }

    public static String encrypt(String cleartext, String seed) { // throws Exception {
        if (TextUtils.isEmpty(cleartext)) {
            return "";
        }

        byte[] rawKey;
        byte[] result;
        try {
            rawKey = getRawKey(seed.getBytes());
            result = encrypt(rawKey, cleartext.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return toHex(result);
    }

    public static String decrypt(String encrypted, String seed) { // throws Exception {
        if (TextUtils.isEmpty(encrypted)) {
            return "";
        }
        byte[] rawKey;
        byte[] result;
        try {
            rawKey = getRawKey(seed.getBytes());
            byte[] enc = toByte(encrypted);
            result = decrypt(rawKey, enc);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return new String(result);

        // // it's the same method now
        // return encrypt(seed, encrypted);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5Bytes = md.digest(seed); // 128 Bit = 16 byte SecretKey
        SecretKeySpec skey = new SecretKeySpec(md5Bytes, "AES");
        byte[] raw = skey.getEncoded();
        return raw;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

}
