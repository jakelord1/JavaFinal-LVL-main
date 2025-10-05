/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package java.itstep.LVL.services.hash;

/**
 *
 * @author pronc
 */
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5HashService implements HashService {
    private final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    @Override
    public String digest(String input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignore) {
            throw new RuntimeException("MessageDigest getInstance error");
        }
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        char[] chars = new char[32];
        for (int i = 0; i < 16; i += 1) {
            int b = (hashBytes[i] >> 4) & 0x0F;
            chars[2 * i] = hex[b];
            b = (hashBytes[i]) & 0x0F;
            chars[2 * i + 1] = hex[b];
        }
        return new String(chars);
    }

}
