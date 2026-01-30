package com.solutechOne.voyager.dto;

import com.solutechOne.voyager.enums.UserRole;
import com.solutechOne.voyager.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class UserDTO {

    private String id;
    private String companyId;

    private String name;
    private String firstname;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // jamais renvoyé

    private String photoProfil;
    private UserRole role;
    private LocalDateTime lastConnection;
    private UserStatus status;

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public LocalDateTime getLastConnection() { return lastConnection; }
    public void setLastConnection(LocalDateTime lastConnection) { this.lastConnection = lastConnection; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
