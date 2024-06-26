package invoicemanagerv2.invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    @Query(value = "SELECT * FROM invoice i WHERE i.company_id = ?1", nativeQuery = true)
    List<Invoice> findAllByCompanyId(String companyId);
}
