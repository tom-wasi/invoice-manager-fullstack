package com.tmszw.invoicemanagerv2.appuser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tmszw.invoicemanagerv2.company.Company;
import com.tmszw.invoicemanagerv2.mail.confirmation.ConfirmationToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = "email"
                )})
public class AppUser implements UserDetails {

    public AppUser(String id, String username, String email, String password, boolean isEnabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
    }

    @Id
    @SequenceGenerator(
            name = ("app_user_id_seq"),
            sequenceName = ("user_id_seq"),
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.UUID
    )
    @Column(name = "user_id")
    private String id;

    @Column(
            name = "username",
            nullable = false
    )
    private String username;

    @Column(
            name = "email",
            nullable = false
    )
    private String email;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @JsonIgnore
    @OneToMany(
            mappedBy = "user",
            orphanRemoval = true,
            cascade = CascadeType.PERSIST
    )
    Set<Company> companies;

    @JsonIgnore
    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ConfirmationToken confirmationToken;

    @Column(
            name = "is_enabled",
            nullable = false
    )

    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
