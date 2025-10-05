/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.data.jwt;

/**
 *
 * @author pronc
 */
public class JwtHeader {
    private String alg;
    private String typ;

    public JwtHeader() {
        alg = "HS256";
        typ = "JWT";
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }
    
    
    
}