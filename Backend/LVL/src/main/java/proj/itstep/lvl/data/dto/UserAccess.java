/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proj.itstep.lvl.data.dto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserAccess {

    private UUID id;
    private UUID userId;
    private String roleId;
    private String login;
    private String salt;
    private String dk;

    private User user;

    public static UserAccess fromResultSet(ResultSet rs) throws SQLException {
        UserAccess ua = new UserAccess();
        ua.setId(UUID.fromString(rs.getString("id")));
        ua.setUserId(UUID.fromString(rs.getString("user_id")));
        ua.setRoleId(rs.getString("role_id"));
        ua.setLogin(rs.getString("login"));
        ua.setSalt(rs.getString("salt"));
        ua.setDk(rs.getString("dk"));

        try {
            ua.setUser(User.fromResultSet(rs));
        } catch (SQLException ignore) {

        }

        return ua;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }
}

