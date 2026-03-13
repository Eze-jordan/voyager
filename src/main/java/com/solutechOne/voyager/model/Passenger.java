package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.PassengerStatus;
import com.solutechOne.voyager.enums.Role;
import com.solutechOne.voyager.enums.UserStatus;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;
@Entity
@Table(name = "passengers")
public class Passenger implements UserDetails {

    @Id
    @Column(name = "passenger_id", nullable = false, length = 60, updatable = false)
    private String passengerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "birth_date", nullable = false)
    private String birthDate; // Date de naissance sous forme de String

    @Column(name = "nationality", nullable = false)
    private String nationality;

    @Column(name = "gender", nullable = false)
    private String gender; // Male, Female, Other

    @Column(name = "email", nullable = false, unique = true)
    private String email; // L'email du passager

    @Column(name = "password", nullable = false)
    private String password; // Mot de passe du passager

    @Enumerated(EnumType.STRING)
    private Role role = Role.PASSENGER;  // Attribuer un rôle par défaut, comme "USER"

    @Enumerated(EnumType.STRING)
    @Column(name="passenger_status", nullable=false, length=10)
    private PassengerStatus status;

    @PrePersist
    public void generateId() {
        if (this.passengerId == null) {
            this.passengerId = "passenger-" + UUID.randomUUID().toString(); // Génération de l'ID UUID
        }
        if (this.role == null) {
            this.role = Role.PASSENGER; // Assigner un rôle par défaut si `null`
        }
        if (this.status == null) {
            this.status = PassengerStatus.ACTIF; // Définir un statut par défaut si non spécifié
        }
    }

    public void setPassword(String password) {
        // Hachage du mot de passe avant de le stocker
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }
    @Override
    public @Nullable String getPassword() {
            return this.password;    }

    @Override
    public String getUsername() {
        return this.email;    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PassengerStatus getStatus() {
        return status;
    }

    public void setStatus(PassengerStatus status) {
        this.status = status;
    }

    // Getters et Setters...
}
