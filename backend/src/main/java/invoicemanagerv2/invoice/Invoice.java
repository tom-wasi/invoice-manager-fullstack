package invoicemanagerv2.invoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import invoicemanagerv2.company.Company;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Table( name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(
            name = "invoice_file_id",
            nullable = false
    )
    private String invoice_file_id;

    @Column(
            name = "invoice_description"
    )
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(
            name = "company_id",
            nullable = false
    )
    private Company company;

    @Column(
            name = "uploaded",
            nullable = false
    )
    private LocalDate localDate;

    @Column(
            name = "is_pending"
    )
    private boolean isPending;
}