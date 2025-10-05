/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.data.jwt;

import proj.itstep.lvl.data.dto.AccessToken;
import java.util.Date;

/**
 *
 * @author pronc
 */
public class JwtToken {

    private JwtHeader header;
    private JwtPayload payload;
    private String signature;

    public static JwtToken fromAccessToken(AccessToken at) {
        JwtToken jwt = new JwtToken();
        jwt.setHeader(new JwtHeader());
        JwtPayload payload = new JwtPayload();
        payload.setJti(at.getTokenId().toString());
        payload.setAud(at.getUserAccess().getRoleId());
        payload.setIat(at.getIssuedAt().toString());
        payload.setExp(at.getExpiredAt().toString());
        payload.setIss("JavaWeb222");
        payload.setSub(at.getUserAccess().getUserId().toString());
        Date dob = at.getUserAccess().getUser().getBirthdate();
        if (dob != null) {
            payload.setDob(dob.toString());
        }
        payload.setName(at.getUserAccess().getUser().getName());
        payload.setEmail(at.getUserAccess().getUser().getEmail());
        jwt.setPayload(payload);
        return jwt;
    }

    @Override
    public String toString() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dnZWRJbkFzIjoiYWRtaW4iLCJpYXQiOjE0MjI3Nzk2Mzh9.gzSraSYS8EXBxLN _oWnFSRgCzcmJmMjLiuyu5CSpyHI=";
    }
    
    

    public JwtHeader getHeader() {
        return header;
    }

    public void setHeader(JwtHeader header) {
        this.header = header;
    }

    public JwtPayload getPayload() {
        return payload;
    }

    public void setPayload(JwtPayload payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
