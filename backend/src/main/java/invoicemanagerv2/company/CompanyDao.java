package com.tmszw.invoicemanagerv2.company;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {

    Optional<Company> selectCompanyByCompanyId(Integer companyId);
    void insertCompany(Company company);
    boolean existsCompanyWithId(Integer companyId);
    void deleteCompanyByCompanyId(Integer companyId);
    void updateCompany(Company company);
    List<Company> findAllUserCompanies(String userId);
}
