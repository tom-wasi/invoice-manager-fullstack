package invoicemanagerv2.invoice;

import invoicemanagerv2.company.Company;
import invoicemanagerv2.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class InvoiceRowMapper implements RowMapper<Invoice> {

    private final CompanyService companyService;
    @Override
    public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("id"));
        invoice.setInvoice_file_id(rs.getString("invoice_file_id"));
        invoice.setDescription(rs.getString("invoice_description"));
        invoice.setPending(rs.getBoolean("is_pending"));
        invoice.setLocalDate(rs.getDate("uploaded").toLocalDate());

        String companyId = rs.getString("company_id");
        Company company = companyService.getCompanyById(companyId);
        invoice.setCompany(company);

        return invoice;
    }
}
