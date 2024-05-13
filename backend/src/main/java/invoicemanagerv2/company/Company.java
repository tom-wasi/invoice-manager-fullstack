package invoicemanagerv2.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.invoice.Invoice;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "company")
public class Company {

    @Id
    @SequenceGenerator(
            name = "company_id_seq",
            sequenceName = "company_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private String companyId;

    @Column(
            name = "company_name",
            nullable = false
    )
    private String companyName;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @JsonIgnore
    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Invoice> invoices = new HashSet<>();

    @Column(name = "accountant_email")
    @Email
    private String accountantEmail;

    public Company(String companyId, String companyName, AppUser user, String accountantEmail) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.user = user;
        this.accountantEmail = accountantEmail;
    }

    public Company(String companyId, String companyName, String accountantEmail) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.accountantEmail = accountantEmail;
    }
}
