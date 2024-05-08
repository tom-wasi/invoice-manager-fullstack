package invoicemanagerv2.company;

import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.appuser.AppUserService;
import invoicemanagerv2.exception.CompanyNotFoundException;
import invoicemanagerv2.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    private final CompanyDao companyDao = mock(CompanyDao.class);
    private final AppUserService appUserService = mock(AppUserService.class);
    private CompanyService underTest;
    private CompanyService underTestSpy;
    private final CompanyDTOMapper companyDTOMapper = new CompanyDTOMapper();
    private final AppUser appUser = mock(AppUser.class);
    private Company company;

    @BeforeEach
    void setUp() {
        underTest = new CompanyService(
                companyDao,
                appUserService,
                companyDTOMapper
        );

        underTestSpy = spy(underTest);

        this.company = new Company(
                UUID.randomUUID().toString(),
                FAKER.company().name(),
                appUser,
                FAKER.internet().safeEmailAddress()
        );
    }


    @Test
    void getCompany_ShouldReturnCompanyDTO() {
        //given
        when(companyDao.selectCompanyByCompanyId(company.getCompanyId())).thenReturn(Optional.of(company));
        CompanyDTO expected = companyDTOMapper.apply(company);

        //when
        CompanyDTO actual = underTestSpy.getCompanyDTOById(company.getCompanyId());

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getCompany_ShouldThrowWhenReturnEmptyOptional() {
        //given
        String id = UUID.randomUUID().toString();

        when(companyDao.selectCompanyByCompanyId(id)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(() -> underTestSpy.getCompanyById(id)).isInstanceOf(CompanyNotFoundException.class).hasMessage("company with id: [%s] not found".formatted(id));
    }

    @Test
    void getCompanies_ShouldReturnListOfCompanyDTOs() {
        //given
        when(companyDao.findAllUserCompanies(appUser.getId())).thenReturn(List.of(company));

        //when
        List<Company> companies = companyDao.findAllUserCompanies(appUser.getId());

        List<CompanyDTO> expected = new ArrayList<>();
        for (Company c : companies) {
            expected.add(companyDTOMapper.apply(c));
        }
        List<CompanyDTO> actual = underTestSpy.getAllUserCompanies(appUser.getId());

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addCompany_ShouldAddCompany() {
        //given
        String userId = appUser.getId();
        CompanyRequest companyRequest = new CompanyRequest(FAKER.company().name(), FAKER.internet().safeEmailAddress());
        when(appUserService.getAppUser(userId)).thenReturn(appUser);

        //when
        underTest.addCompany(userId, companyRequest);

        //then
        verify(companyDao, times(1)).insertCompany(any(Company.class));
    }

    @Test
    void addCompany_ShouldThrowWhenNoCompanyName() {
        //given
        String userId = appUser.getId();
        CompanyRequest companyRequest = new CompanyRequest("", company.getAccountantEmail());
        when(appUserService.getAppUser(userId)).thenReturn(appUser);

        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.addCompany(userId, companyRequest));

        //then
        assertEquals("company name cannot be null nor blank", exception.getMessage());
    }

    @Test
    void addCompany_ShouldThrowWhenUserNotPresent() {
        //given
        String userId = appUser.getId();
        CompanyRequest companyRequest = new CompanyRequest(company.getCompanyName(), company.getAccountantEmail());
        when(appUserService.getAppUser(userId)).thenReturn(null);

        //when
        Exception exception = assertThrows(UserNotFoundException.class, () -> underTest.addCompany(userId, companyRequest));

        //then
        assertEquals("user with id [%s] not found".formatted(userId), exception.getMessage());
    }

    @Test
    void updateCompany_ShouldUpdateCompany() {
        //given
        when(appUserService.getAppUser(appUser.getId())).thenReturn(appUser);
        when(companyDao.selectCompanyByCompanyId(company.getCompanyId())).thenReturn(Optional.of(company));

        CompanyUpdateRequest companyUpdateRequest = new CompanyUpdateRequest(
                FAKER.company().name(),
                FAKER.internet().safeEmailAddress()
        );

        doAnswer(invocation -> {
            Company updatedCompany = invocation.getArgument(0);
            when(companyDao.selectCompanyByCompanyId(updatedCompany.getCompanyId())).thenReturn(Optional.of(updatedCompany));
            return null;
        }).when(companyDao).updateCompany(any(Company.class));

        //when
        underTestSpy.updateCompany(company.getCompanyId(), companyUpdateRequest);
        Company actual = companyDao.selectCompanyByCompanyId(company.getCompanyId()).orElseThrow();

        //then
        assertThat(actual.getCompanyId()).isEqualTo(company.getCompanyId());
        assertThat(actual.getCompanyName()).isEqualTo(companyUpdateRequest.companyName());
        assertThat(actual.getAccountantEmail()).isEqualTo(companyUpdateRequest.accountantEmail());
    }

    @Test
    void updateCompany_ShouldThrowWhenNothingToUpdate() {
        //given
        when(appUserService.getAppUser(appUser.getId())).thenReturn(appUser);
        when(companyDao.selectCompanyByCompanyId(company.getCompanyId())).thenReturn(Optional.of(company));

        CompanyUpdateRequest updateRequest = new CompanyUpdateRequest(
                company.getCompanyName(),
                company.getAccountantEmail()
        );

        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> underTest.updateCompany(company.getCompanyId(), updateRequest));

        //then
        assertEquals(exception.getMessage(), "No changes to update");
    }
    @Test
    void deleteCompany_ShouldDeleteCompany() {
        //given
        when(companyDao.selectCompanyByCompanyId(company.getCompanyId())).thenReturn(Optional.of(company));
        assertThat(underTestSpy.getCompanyById(company.getCompanyId())).isEqualTo(company);

        //when
        underTestSpy.deleteCompany(company.getCompanyId());

        //then
        verify(companyDao, times(1)).deleteCompanyByCompanyId(company.getCompanyId());
    }

    @Test
    void deleteCompany_ShouldThrowWhenCompanyNotFound() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        when(companyDao.selectCompanyByCompanyId(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(CompanyNotFoundException.class, () -> underTest.deleteCompany(id));

        //then
        assertEquals(exception.getMessage(), "company with id: [%s] not found".formatted(id));
    }
}
