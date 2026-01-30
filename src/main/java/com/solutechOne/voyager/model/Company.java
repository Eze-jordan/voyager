package com.solutechOne.voyager.model;

import com.solutechOne.voyager.enums.CompanyStatus;
import com.solutechOne.voyager.enums.CompanyType;
import com.solutechOne.voyager.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company implements UserDetails {

    @Id
    @Column(name = "company_id", nullable = false)
    private String id; // Numérique (50000-599999) - auto (côté DB ou service)

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "company_type", length = 20, nullable = false)
    private CompanyType type;

    @NotBlank
    @Size(max = 50)
    @Column(name = "company_name", length = 50, nullable = false)
    private String name;


    @Column(name = "company_url_logo")
    private String urlLogo;

    @Size(max = 12)
    @Column(name = "company_nif", length = 12)
    private String nif;

    @Size(max = 15)
    @Column(name = "company_rccm", length = 15)
    private String rccm;

    @Size(max = 10)
    @Column(name = "company_app_id", length = 10)
    private String appId;

    @Size(max = 50)
    @Column(name = "company_address", length = 50)
    private String address;

    @Column(name = "company_phone")
    private Long phone; // "Chiffre 9" -> on stocke en Long (ou String si tu veux préserver les 0)

    @NotBlank
    @Email
    @Size(max = 50)
    @Column(name = "company_email", length = 50, nullable = false)
    private String email;

    @NotBlank
    @Size(max = 100)
    @Column(name = "company_aggrement", length = 100, nullable = false)
    private String aggrement;

    @NotNull
    @Column(name = "company_rate_fees", nullable = false, precision = 10, scale = 4)
    private BigDecimal rateFees; // taux de frais de service

    @NotBlank
    @Size(max = 255)
    @Column(name = "company_passwd", nullable = false, length = 255)
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "company_status", length = 10, nullable = false)
    private CompanyStatus status;

    // Relation: 1 company -> N users (table users.company_id)
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private List<User> users = new ArrayList<>();

    // --- constructors ---
    public Company() {}

    // --- getters/setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CompanyType getType() { return type; }
    public void setType(CompanyType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrlLogo() { return urlLogo; }
    public void setUrlLogo(String urlLogo) { this.urlLogo = urlLogo; }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getRccm() { return rccm; }
    public void setRccm(String rccm) { this.rccm = rccm; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Long getPhone() { return phone; }
    public void setPhone(Long phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAggrement() { return aggrement; }
    public void setAggrement(String aggrement) { this.aggrement = aggrement; }

    public BigDecimal getRateFees() { return rateFees; }
    public void setRateFees(BigDecimal rateFees) { this.rateFees = rateFees; }

    public CompanyStatus getStatus() { return status; }
    public void setStatus(CompanyStatus status) { this.status = status; }

    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }

    // ---- UserDetails ----

    @Override public String getUsername() { return email; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_COMPANY"));
    }


    @Override public String getPassword() { return password; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return status != CompanyStatus.SUSPENDUE; }
    @Override public boolean isEnabled() { return status == CompanyStatus.ACTIVE; }


    public void setPassword(@NotBlank @Size(max = 50) String password) {
        this.password = password;
    }
}