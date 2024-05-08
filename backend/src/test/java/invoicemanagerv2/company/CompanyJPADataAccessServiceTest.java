package invoicemanagerv2.company;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CompanyJPADataAccessServiceTest {


    private final CompanyRepository companyRepository = mock(CompanyRepository.class);

    private CompanyJPADataAccessService underTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CompanyJPADataAccessService(companyRepository);
    }

    @AfterEach
    void teardown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectCompanyById() {
        //given
        String companyId = UUID.randomUUID().toString();

        //when
        underTest.selectCompanyByCompanyId(companyId);

        //then
        verify(companyRepository).findById(companyId);
    }

    @Test
    void insertCompany() {
        //given
        Company company = new Company(
                UUID.randomUUID().toString(),
                FAKER.company().name(),
                FAKER.internet().safeEmailAddress()
        );

        //when
        underTest.insertCompany(company);

        //then
        verify(companyRepository).save(company);
    }

    @Test
    void selectAllUserCompanies() {
        //given
        String userId = UUID.randomUUID().toString();

        //when
        underTest.findAllUserCompanies(userId);

        //then
        verify(companyRepository).findAllUserCompanies(userId);
    }

    @Test
    void existsCompanyWithId() {
        //given
        String companyId = UUID.randomUUID().toString();

        //when
        underTest.existsCompanyWithId(companyId);

        //then
        verify(companyRepository).existsById(companyId);
    }

    @Test
    void deleteCompany() {
        //given
        String companyId = UUID.randomUUID().toString();

        //when
        underTest.deleteCompanyByCompanyId(companyId);

        //then
        verify(companyRepository).deleteById(companyId);
    }

    @Test
    void updateCompany() {
        //given
        Company company = new Company(
                UUID.randomUUID().toString(),
                FAKER.company().name(),
                FAKER.internet().safeEmailAddress()
        );

        //when
        underTest.updateCompany(company);

        //then
        verify(companyRepository).save(company);
    }
}
