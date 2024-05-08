package invoicemanagerv2.company;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("company_jpa")
@RequiredArgsConstructor
public class CompanyJPADataAccessService implements CompanyDao {

    private final CompanyRepository companyRepository;

    @Override
    public Optional<Company> selectCompanyByCompanyId(String companyId) {
        return companyRepository.findById(companyId);
    }
    @Override
    public void insertCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public boolean existsCompanyWithId(String companyId) {
        return companyRepository.existsById(companyId);
    }

    @Override
    public void deleteCompanyByCompanyId(String companyId) {
        companyRepository.deleteById(companyId);
    }

    @Override
    public void updateCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public List<Company> findAllUserCompanies(String userId) {
        return companyRepository.findAllUserCompanies(userId);
    }
}
