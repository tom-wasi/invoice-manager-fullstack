package invoicemanagerv2.company;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {

    Optional<Company> selectCompanyByCompanyId(String companyId);
    void insertCompany(Company company);
    boolean existsCompanyWithId(String companyId);
    void deleteCompanyByCompanyId(String companyId);
    void updateCompany(Company company);
    List<Company> findAllUserCompanies(String userId);
}
