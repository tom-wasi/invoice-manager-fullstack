package invoicemanagerv2.company;

import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.appuser.AppUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompanyRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //given
        AppUserService appUserService = Mockito.mock(AppUserService.class);
        CompanyRowMapper companyRowMapper = new CompanyRowMapper(appUserService);

        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("company_id")).thenReturn(1);
        when(rs.getString("company_name")).thenReturn("companyName");
        when(rs.getString("user_id")).thenReturn("userId");
        when(rs.getString("accountant_email")).thenReturn("accountant@example.com");

        AppUser user = new AppUser();
        when(appUserService.getAppUser("userId")).thenReturn(user);

        //when
        Company actual = companyRowMapper.mapRow(rs, 1);

        //then
        Company expected = new Company();
        expected.setCompanyId(UUID.randomUUID().toString());
        expected.setCompanyName("companyName");
        expected.setUser(user);
        expected.setAccountantEmail("accountant@example.com");

        assertThat(actual).isEqualTo(expected);
    }
}
