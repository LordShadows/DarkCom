package implementationclasses;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * Created by DELL on 31.01.2017.
 * @author Daniel Sandrutski
 */

public class Crypt {
    private static final String PRIVATE_KEY_FILE = "/privatekey/PrivateKey.txt";
    private Key serverAESKey128; //Сеансовый ключ для симметричного шифрования

    private static final byte[] SALT = new byte[]{
            (byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c,
            (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99,
            (byte) 0x77, (byte) 0xe3, (byte) 0x21, (byte) 0xfc,
            (byte) 0x75, (byte) 0xc6, (byte) 0x1e, (byte) 0xf9};


    /**
     * Установка сеансового ключа
     */
    public void getDESKey (byte[] key){
        serverAESKey128 = new SecretKeySpec(key, 0, key.length, "AES");
    }

    /**
     * Симмитричное шифрование
     */
    public byte[] encodeServerMessage(String cleartext) {
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
    public String decodeServerMessage(byte[] ciphertext) {
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
    private byte[] crypt(byte[] input, Key key, int mode, String cipher) {
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
     * Метод ассимитричной расшифровки сеансового ключа
     */
    public String decryptRSA(byte[] text)
    {
        byte[] bytetext = text;
        byte[] dectyptedText = null;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, restoreRSAPrivateKEy());
            dectyptedText = cipher.doFinal(bytetext);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String(dectyptedText);
    }

    /**
     * Чтение ключа из файла
     */
    private byte[] fileToKey(String file) throws IOException
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
     * Подготовка приватного ключа для ассимитричной расшифровки
     */
    PrivateKey restoreRSAPrivateKEy() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(fileToKey(PRIVATE_KEY_FILE));
        return keyFactory.generatePrivate(privateKeySpec);
    }

    /**
     * Генерация ассимитричного ключа
     */
    public String[] generateKeysRSA() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048, new SecureRandom());
            final KeyPair key = keyGen.generateKeyPair();

            return new String[] { byte2Hex(key.getPublic().getEncoded()), byte2Hex(key.getPrivate().getEncoded()) };
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private String byte2Hex(byte b[])
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

    private byte hex2Byte(char a1, char a2)
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

    private byte[] hex2Byte(String str)
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
