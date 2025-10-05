/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package java.itstep.LVL.services.kdf;

/**
 *
 * @author pronc
 */
public interface KdfService {
    String dk(String password, String salt);
}
