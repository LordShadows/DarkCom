package implementationclasses;

import com.google.gson.Gson;
import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Created by Dell on 10.05.2017.
 * @author Daniel Sandrutski
 */
public class DiffieHellman {
    private byte alice[];

    private KeyPair kpAlice;

    private SecretKey key;

    private Gson gson = new Gson();

    private BigInteger aliceP, aliceG;
    private int aliceL;

    public String getAlice() {
        return gson.toJson(alice);
    }

    public String getAliceP() {
        return gson.toJson(aliceP);
    }

    public String getAliceG() {
        return gson.toJson(aliceG);
    }

    public String getAliceL() {
        return gson.toJson(aliceL);
    }

    public void AliceInit() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(1024);
            kpAlice = kpg.generateKeyPair();

            Class dhClass = Class.forName("javax.crypto.spec.DHParameterSpec");
            DHParameterSpec dhSpec = ((DHPublicKey) kpAlice.getPublic()).getParams();
            aliceG = dhSpec.getG();
            aliceP = dhSpec.getP();
            aliceL = dhSpec.getL();
            alice = kpAlice.getPublic().getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AliceGenKey(String bob){
        try {
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(kpAlice.getPrivate());

            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(gson.fromJson(bob, byte[].class));
            PublicKey pk = kf.generatePublic(x509Spec);
            ka.doPhase(pk, true);

            byte secret[] = ka.generateSecret();
            key = new SecretKeySpec(secret, 0, 16, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String BobInit(String aliceP, String aliceG, String aliceL, String alice) {
        byte[] bob = new byte[0];
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            DHParameterSpec dhSpec = new DHParameterSpec(
                    gson.fromJson(aliceP, BigInteger.class), gson.fromJson(aliceG, BigInteger.class), gson.fromJson(aliceL, int.class));
            kpg.initialize(dhSpec);
            KeyPair kp = kpg.generateKeyPair();
            bob = kp.getPublic().getEncoded();

            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(kp.getPrivate());

            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(gson.fromJson(alice, byte[].class));
            PublicKey pk = kf.generatePublic(x509Spec);
            ka.doPhase(pk, true);

            byte secret[] = ka.generateSecret();
            key = new SecretKeySpec(secret, 0, 16, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gson.toJson(bob);
    }

    public String encrypt(String text){
        String cipherText = null;
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, key);
            cipherText = gson.toJson(c.doFinal(text.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public String decrypt(String ciphertext){
        String plaintext = null;
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] byteLine = gson.fromJson(ciphertext, byte[].class);
            plaintext = new String(c.doFinal(byteLine));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plaintext;
    }
}
