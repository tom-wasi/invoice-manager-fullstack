package com.tmszw.invoicemanagerv2.company;

import com.tmszw.invoicemanagerv2.appuser.AppUser;
import com.tmszw.invoicemanagerv2.appuser.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class CompanyRowMapper implements RowMapper<Company> {

    private final AppUserService appUserService;

    @Override
    public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
        Company company = new Company();
        company.setCompanyId(rs.getInt("company_id"));
        company.setCompanyName(rs.getString("company_name"));

        String userId = rs.getString("user_id");
        AppUser user = appUserService.getAppUser(userId);
        company.setUser(user);
        company.setAccountantEmail(rs.getString("accountant_email"));
        return company;
    }
}