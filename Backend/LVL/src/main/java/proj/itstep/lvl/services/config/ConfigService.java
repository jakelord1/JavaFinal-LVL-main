/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package java.itstep.LVL.services.config;

/**
 *
 * @author pronc
 */
public interface ConfigService {
    void addFile(String filename);
    String get(String path);
}
