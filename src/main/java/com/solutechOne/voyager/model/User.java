package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.UserRole;
import com.solutechOne.voyager.enums.UserStatus;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name="users")
public class User implements UserDetails {

    @Id
    @Column(name = "user_id", nullable = false,length = 100,updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="company_id", nullable=false)
    private Company company;

    @Column(name="user_name", nullable=false, length=50)
    private String name;

    @Column(name="user_firstname", length=50)
    private String firstname;

    @Column(name="user_email", nullable=false, length=50, unique = true)
    private String email;

    @Column(name="user_passwd", nullable=false, length=100)
    private String password;

    @Column(name="user_photo_profil", length=255)
    private String photoProfil;

    @Enumerated(EnumType.STRING)
    @Column(name="user_role", nullable=false, length=20)
    private UserRole role;

    @Column(name="user_last_conx")
    private LocalDateTime lastConnection;

    @Enumerated(EnumType.STRING)
    @Column(name="user_status", nullable=false, length=10)
    private UserStatus status;

    // ---- UserDetails ----
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return status != UserStatus.SUSPENDU; }
    @Override public boolean isEnabled() { return status == UserStatus.ACTIF; }

    // getters/setters classiques
    public String getId() { return id; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    public void setPassword(String password) { this.password = password; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public LocalDateTime getLastConnection() { return lastConnection; }
    public void setLastConnection(LocalDateTime lastConnection) { this.lastConnection = lastConnection; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public void setId(String id) {
        this.id = id;
    }
}
