package invoicemanagerv2.company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query(value = "SELECT * FROM company c WHERE c.user_id = ?1", nativeQuery = true)
    List<Company> findAllUserCompanies(String userId);

    @Query(value = "SELECT * FROM company c WHERE c.company_id = ?1", nativeQuery = true)
    Optional<Company> findCompanyById(String companyId);

}
