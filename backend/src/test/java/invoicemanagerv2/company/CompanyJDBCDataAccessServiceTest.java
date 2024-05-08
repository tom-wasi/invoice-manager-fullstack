package com.tmszw.invoicemanagerv2.company;

import com.tmszw.invoicemanagerv2.AbstractTestcontainers;
import com.tmszw.invoicemanagerv2.appuser.AppUser;
import com.tmszw.invoicemanagerv2.appuser.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CompanyJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CompanyJDBCDataAccessService underTest;
    private final AppUserService appUserService = Mockito.mock(AppUserService.class);
    private final CompanyRowMapper companyRowMapper = new CompanyRowMapper(appUserService);
    private AppUser appUser;
    @BeforeEach
    void setUp() {
        underTest = new CompanyJDBCDataAccessService(
                getJdbcTemplate(),
                companyRowMapper
        );

        Mockito.when(appUserService.getAppUser(anyString())).thenReturn(appUser);

        this.appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );
    }

    @Test
    void selectCompanyById() {
        //given
        String companyName = FAKER.company().name();

        Company company = new Company();
        company.setCompanyId(1);
        company.setCompanyName(companyName);
        company.setUser(appUser);
        company.setAccountantEmail(FAKER.internet().safeEmailAddress());

        //when
        underTest.insertCompany(company);
        Optional<Company> actual = underTest.selectCompanyByCompanyId(company.getCompanyId());

        //then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getCompanyId()).isEqualTo(company.getCompanyId());
            assertThat(c.getCompanyName()).isEqualTo(company.getCompanyName());
            assertThat(c.getAccountantEmail()).isEqualTo(company.getAccountantEmail());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCompanyByCompanyId() {
        //given
        Integer id = 1;

        //when
        var actual = underTest.selectCompanyByCompanyId(id);

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void canDeleteCompany() {
        //given
        Company company = new Company();
        company.setCompanyId(FAKER.number().randomDigitNotZero());
        company.setCompanyName(FAKER.company().name());
        company.setUser(appUser);
        company.setAccountantEmail(FAKER.internet().safeEmailAddress());

        //when
        //then
        underTest.insertCompany(company);
        Optional<Company> actual = underTest.selectCompanyByCompanyId(company.getCompanyId());
        assertThat(actual).isPresent();

        //when
        //then
        underTest.deleteCompanyByCompanyId(company.getCompanyId());
        Optional<Company> afterDelete = underTest.selectCompanyByCompanyId(company.getCompanyId());
        assertThat(afterDelete).isEmpty();
    }

    @Test
    void canUpdateCompany() {
        //given
        Company company = new Company();
        company.setCompanyId(FAKER.number().randomDigitNotZero());
        company.setCompanyName(FAKER.company().name());
        company.setUser(appUser);
        company.setAccountantEmail(FAKER.internet().safeEmailAddress());

        //when
        //then
        underTest.insertCompany(company);
        Optional<Company> actual = underTest.selectCompanyByCompanyId(company.getCompanyId());
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getCompanyId()).isEqualTo(company.getCompanyId());
            assertThat(c.getCompanyName()).isEqualTo(company.getCompanyName());
            assertThat(c.getAccountantEmail()).isEqualTo(company.getAccountantEmail());
        });

        //when
        //then
        company.setCompanyName(FAKER.company().name());
        company.setAccountantEmail(FAKER.internet().safeEmailAddress());
        underTest.updateCompany(company);
        Optional<Company> afterUpdate = underTest.selectCompanyByCompanyId(company.getCompanyId());

        assertThat(afterUpdate).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getCompanyId()).isEqualTo(company.getCompanyId());
            assertThat(c.getCompanyName()).isEqualTo(company.getCompanyName());
            assertThat(c.getAccountantEmail()).isEqualTo(company.getAccountantEmail());
        });
    }
}
