package implementationclasses;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by DELL on 31.01.2017.
 * @author Daniel Sandrutski
 */

public class Crypt {
    private static final String PUBLIC_KEY_FILE = "/publickey/PublicKey.txt";
    private static Key serverAESKey128; //Сеансовый ключ для симметричного шифрования
    private static Key clientAESKey128; //Сеансовый ключ для секретного диалога

    private static final byte[] SALT = new byte[]{
            (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
            (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99,
            (byte) 0x77, (byte) 0xe3, (byte) 0x21, (byte) 0xfc,
            (byte) 0x75, (byte) 0xc6, (byte) 0x1e, (byte) 0xf9};


    /**
     * Симмитричное шифрование
     */
    public static byte[] encodeServerMessage(String cleartext) {
        try {
            return crypt(cleartext.getBytes("UTF-8"), serverAESKey128, Cipher.ENCRYPT_MODE, "AES/CBC/PKCS5Padding");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Симметричная расшифровка
     */
    public static String decodeServerMessage(byte[] ciphertext) {
        try {
            return new String(crypt(ciphertext, serverAESKey128, Cipher.DECRYPT_MODE, "AES/CBC/PKCS5Padding"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Основной метод симметричного шифрование
     */
    private static byte[] crypt(byte[] input, Key key, int mode, String cipher) {
        IvParameterSpec iv = new IvParameterSpec(SALT);
        Cipher c;
        try {
            c = Cipher.getInstance(cipher);
            c.init(mode, key, iv);
            return c.doFinal(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Метод для ассимметричного шифрования сеансового ключа
     */
    public static byte[] encryptKeyRSA(String key)
    {
        byte[] cipherText = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, restoreRSAPublicKey());
            cipherText = cipher.doFinal(key.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert cipherText != null;
        return cipherText;
    }

    /**
     * Чтение ключа из файла
     */
    private static byte[] fileToKey(String file) throws IOException
    {
        InputStream in = Crypt.class.getResourceAsStream(file);
        BufferedReader pubIn = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String tmp;
        do {
            tmp = pubIn.readLine();
            if (tmp != null) sb.append(tmp);
        } while (tmp != null);
        return hex2Byte(sb.toString());
    }

    /**
     * Подготовка публичного ключа для шифрования сеансового ключа
     */
    static PublicKey restoreRSAPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(fileToKey(PUBLIC_KEY_FILE));
        return keyFactory.generatePublic(publicKeySpec);
    }

    /**
     * Генерация сеансового ключа
     */
    public static byte[] generateKeyAES128() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey secretKey = keyGen.generateKey();
            serverAESKey128 = secretKey;
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byte2Hex(byte b[])
    {
        String hs = "";
        String stmp;
        for (byte aB : b) {
            stmp = Integer.toHexString(aB & 0xff);
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toLowerCase();
    }

    private static byte hex2Byte(char a1, char a2)
    {
        int k;
        if (a1 >= '0' && a1 <= '9') k = a1 - 48;
        else if (a1 >= 'a' && a1 <= 'f') k = (a1 - 97) + 10;
        else if (a1 >= 'A' && a1 <= 'F') k = (a1 - 65) + 10;
        else k = 0;
        k <<= 4;
        if (a2 >= '0' && a2 <= '9') k += a2 - 48;
        else if (a2 >= 'a' && a2 <= 'f') k += (a2 - 97) + 10;
        else if (a2 >= 'A' && a2 <= 'F') k += (a2 - 65) + 10;
        else k += 0;
        return (byte) (k & 0xff);
    }

    private static byte[] hex2Byte(String str)
    {
        int len = str.length();
        if (len % 2 != 0) return null;
        byte r[] = new byte[len / 2];
        int k = 0;
        for (int i = 0; i < str.length() - 1; i += 2)
        {
            r[k] = hex2Byte(str.charAt(i), str.charAt(i + 1));
            k++;
        }
        return r;
    }
}
