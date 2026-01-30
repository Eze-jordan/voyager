package com.solutechOne.voyager.securite;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String id; // String pour supporter Manager (6 chiffres) et les Long (Company/User)
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private final String displayName;
    private final String typeCompte; // "MANAGER" | "COMPANY" | "USER"
    private final String status;

    private final String role;       // ex: ADMINISTRATEUR / COMPANY / SUPER_ADMIN etc.
    private final String companyId;    // seulement pour USER (sinon null)

    public CustomUserDetails(
            String id,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String displayName,
            String typeCompte,
            String status,
            String role,
            String companyId
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.displayName = displayName;
        this.typeCompte = typeCompte;
        this.status = status;
        this.role = role;
        this.companyId = companyId;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getTypeCompte() { return typeCompte; }
    public String getStatus() { return status; }
    public String getRole() { return role; }

    public String getEmail() { return email; }

    public String getCompanyId() { return companyId; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isEnabled() { return true; }
}
