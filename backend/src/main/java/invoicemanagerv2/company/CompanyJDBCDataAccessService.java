package com.tmszw.invoicemanagerv2.company;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("company_jdbc")
public class CompanyJDBCDataAccessService implements CompanyDao {

    private final JdbcTemplate jdbcTemplate;
    private final CompanyRowMapper companyRowMapper;

    public CompanyJDBCDataAccessService(JdbcTemplate jdbcTemplate, CompanyRowMapper companyRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.companyRowMapper = companyRowMapper;
    }

    @Override
    public Optional<Company> selectCompanyByCompanyId(Integer companyId) {
        var sql = """
                SELECT
                    company_id,
                    company_name,
                    user_id,
                    accountant_email
                FROM company
                WHERE company_id = ?;
                """;
        return jdbcTemplate.query(sql, companyRowMapper, companyId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCompany(Company company) {
        var sql = """
                INSERT INTO company (
                    company_id,
                    company_name,
                    user_id,
                    accountant_email)
                VALUES (?, ?, ?, ?);
                """;

        jdbcTemplate.update(
                sql,
                company.getCompanyId(),
                company.getCompanyName(),
                company.getUser().getId(),
                company.getAccountantEmail()
        );
    }

    @Override
    public boolean existsCompanyWithId(Integer companyId) {
        var sql = """
                SELECT count(company_id)
                FROM company
                WHERE company_id = ?;
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, companyId);
        return count != null && count > 0;
    }

    @Override
    public void deleteCompanyByCompanyId(Integer companyId) {
        var sql = """
                DELETE
                FROM company
                WHERE company_id = ?;
                """;

        jdbcTemplate.update(sql, companyId);
    }

    @Override
    public void updateCompany(Company update) {
        if (update.getCompanyName() != null) {
            var sql = """
                    UPDATE company
                    SET company_name = ?
                    WHERE company_id = ?;
                    """;
            jdbcTemplate.update(sql, update.getCompanyName(), update.getCompanyId());
        }
        if (update.getAccountantEmail() != null) {
            var sql = """
                    UPDATE company
                    SET accountant_email = ?
                    WHERE company_id = ?;
                    """;
            jdbcTemplate.update(sql, update.getAccountantEmail(), update.getCompanyId());
        }
    }

    public List<Company> findAllUserCompanies(String userId) {
        var sql = """
                SELECT *
                FROM company
                WHERE user_id = ?;
                """;

        return jdbcTemplate.query(sql, companyRowMapper, userId);
    }
}