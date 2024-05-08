package invoicemanagerv2.company;

import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.appuser.AppUserService;
import invoicemanagerv2.exception.CompanyNotFoundException;
import invoicemanagerv2.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyService {

    private final AppUserService appUserService;
    private final CompanyDao companyDao;
    private final CompanyDTOMapper companyDTOMapper;

    public CompanyService(
            @Qualifier("company_jpa") CompanyDao companyDao,
            AppUserService appUserService,
            CompanyDTOMapper companyDTOMapper) {
        this.appUserService = appUserService;
        this.companyDao = companyDao;
        this.companyDTOMapper = companyDTOMapper;
    }
    public List<CompanyDTO> getAllUserCompanies(String userId) {
        List<Company> companies = companyDao.findAllUserCompanies(userId);
        List<CompanyDTO> companyDTOS = new ArrayList<>();
        for (Company c : companies) {
            companyDTOS.add(companyDTOMapper.apply(c));
        }
        return companyDTOS;
    }

    public CompanyDTO getCompanyDTOById(String companyId) {
        return companyDTOMapper.apply(companyDao.selectCompanyByCompanyId(companyId).orElseThrow(
                () -> new CompanyNotFoundException(("company with id: [%s] not found".formatted(companyId)))
        ));
    }

    public Company getCompanyById(String companyId) {
        return companyDao.selectCompanyByCompanyId(companyId).orElseThrow(
                () -> new CompanyNotFoundException(("company with id: [%s] not found".formatted(companyId)))
        );
    }

    public void addCompany(String userId,
                           CompanyRequest company) {

        AppUser appUser = appUserService.getAppUser(userId);
        if (appUser == null) {
            throw new UserNotFoundException("user with id [%s] not found".formatted(userId));
        }

        if (company.companyName() == null || company.companyName().isEmpty()) {
            throw new IllegalArgumentException("company name cannot be null nor blank");
        }

        Company newCompany = new Company();
        newCompany.setCompanyName(company.companyName());
        newCompany.setAccountantEmail(company.accountantEmail());
        newCompany.setUser(appUser);
        companyDao.insertCompany(newCompany);
    }

    public void deleteCompany(String companyId) {
        Company existingCompany = companyDao.selectCompanyByCompanyId(companyId).orElseThrow(
                () -> new CompanyNotFoundException(("company with id: [%s] not found".formatted(companyId)))
        );

        if (existingCompany != null) {
            companyDao.deleteCompanyByCompanyId(companyId);
        }
    }

    public void updateCompany(String companyId, CompanyUpdateRequest updateRequest) {

        Company existingCompany = companyDao.selectCompanyByCompanyId(companyId).orElseThrow(
                () -> new CompanyNotFoundException(("company with id: [%s] not found".formatted(companyId)))
        );
        boolean changesFlag = false;

        if (updateRequest.companyName() != null && !updateRequest.companyName().equals(existingCompany.getCompanyName())) {
            existingCompany.setCompanyName(updateRequest.companyName());
            changesFlag = true;
        }

        if (updateRequest.accountantEmail() != null && !updateRequest.accountantEmail().equals(existingCompany.getAccountantEmail())) {
            existingCompany.setAccountantEmail(updateRequest.accountantEmail());
            changesFlag = true;
        }

        if (!changesFlag) {
            throw new IllegalArgumentException("No changes to update");
        }
        companyDao.updateCompany(existingCompany);
    }

    public boolean existsById(String companyId) {
        return companyDao.existsCompanyWithId(companyId);
    }
}