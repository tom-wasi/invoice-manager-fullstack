package invoicemanagerv2.invoice;

import invoicemanagerv2.company.Company;
import invoicemanagerv2.company.CompanyService;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InvoiceRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //given
        CompanyService companyService = mock(CompanyService.class);
        InvoiceRowMapper invoiceRowMapper = new InvoiceRowMapper(companyService);

        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("invoice_file_id")).thenReturn("11111");
        when(rs.getString("invoice_description")).thenReturn("Test invoice description");
        when(rs.getBoolean("is_pending")).thenReturn(true);
        when(rs.getDate("uploaded")).thenReturn(Date.valueOf(LocalDate.now()));

        Company company = new Company();

        when(companyService.getCompanyById("12345678")).thenReturn(company);

        //when
        Invoice actual = invoiceRowMapper.mapRow(rs, 1);

        //then
        Invoice expected = new Invoice();
        expected.setId(1);
        expected.setInvoice_file_id("11111");
        expected.setDescription("Test invoice description");
        expected.setPending(true);
        expected.setLocalDate(LocalDate.now());

        assertThat(actual).isEqualTo(expected);
    }
}
