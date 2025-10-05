/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
/**
 *
 * @author pronc
 */
public class AccessToken {

    private UUID tokenId;
    private UUID userAccessId;
    private Date issuedAt;
    private Date expiredAt;

    private UserAccess userAccess;

    public static AccessToken fromResultSet(ResultSet rs) throws SQLException {
        AccessToken t = new AccessToken();
        t.setTokenId(UUID.fromString(rs.getString("id")));
        t.setTokenId(UUID.fromString(rs.getString("user_access_id")));
        Timestamp timestamp;
        timestamp = rs.getTimestamp("issued_at");
        t.setIssuedAt(new Date(timestamp.getTime()));
        timestamp = rs.getTimestamp("expired_at");
        t.setExpiredAt(new Date(timestamp.getTime()));

        t.setUserAccess(UserAccess.fromResultSet(rs));

        return t;
    }

    public UUID getTokenId() {
        return tokenId;
    }

    public void setTokenId(UUID tokenId) {
        this.tokenId = tokenId;
    }

    public UUID getUserAccessId() {
        return userAccessId;
    }

    public void setUserAccessId(UUID userAccessId) {
        this.userAccessId = userAccessId;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Date expiredAt) {
        this.expiredAt = expiredAt;
    }

    public UserAccess getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(UserAccess userAccess) {
        this.userAccess = userAccess;
    }

}
