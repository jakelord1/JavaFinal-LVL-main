/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.services.Signature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author pronc
 */
public class HS256SignatureService implements SignatureService {

    @Override
    public String getSignature(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("SHA-256");
            mac.init(new SecretKeySpec(secret.getBytes(), "SHA-256"));
            return bytesToHex(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private String bytesToHex(byte[] input) {
        char[] chars = new char[64];
        for(int i = 0; i < 32; i += 1) {
            int b = (input[i] >> 4) & 0x0F;
            chars[2*i] = hex[b];
            b = (input[i]) & 0x0F;            
            chars[2*i + 1] = hex[b];
        }
        return new String(chars);
    }
}