/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package java.itstep.LVL.data.dto;

import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;
    private Date birthdate;
    private Date registeredAt;
    private Date DeletedAt;

    public static User fromResultSet(ResultSet rs) {
        return null;

    }

    public Date getBirthdate() {
        return birthdate;
    }

    public User setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
        return this;

    }

    public Date getRegisteredAt() {
        return registeredAt;
    }

    public User setRegisteredAt(Date registeredAt) {
        this.registeredAt = registeredAt;
        return this;

    }

    public Date getDeletedAt() {
        return DeletedAt;
    }

    public User setDeletedAt(Date DeletedAt) {
        this.DeletedAt = DeletedAt;
        return this;

    }

    public UUID getId() {
        return id;
    }

    public User setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

}

