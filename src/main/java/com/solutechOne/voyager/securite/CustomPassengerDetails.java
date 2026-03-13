package com.solutechOne.voyager.securite;

import com.solutechOne.voyager.enums.PassengerStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class CustomPassengerDetails implements UserDetails {

    private final String passengerId;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private final PassengerStatus status;
    private final String role;

    public CustomPassengerDetails(
            String passengerId,
            String email,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            String name,
            PassengerStatus status,
            String role
    ) {
        this.passengerId = passengerId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.name = name;
        this.status = status;
        this.role = role;
    }

    // Getters pour tous les champs

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isEnabled() { return true; }
    // Autres méthodes de UserDetails...
}