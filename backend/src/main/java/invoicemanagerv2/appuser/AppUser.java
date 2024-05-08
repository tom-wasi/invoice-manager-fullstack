package invoicemanagerv2.appuser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import invoicemanagerv2.company.Company;
import invoicemanagerv2.mail.confirmation.ConfirmationToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "app_user")
public class AppUser {

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


    @OneToMany(
            mappedBy = "user",
            orphanRemoval = true,
            cascade = CascadeType.PERSIST
    )
    @JsonIgnore
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
    public String toString() {
        return "AppUser{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmationToken=" + confirmationToken +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
