package invoicemanagerv2.journey;

import invoicemanagerv2.TestcontainersTest;
import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.appuser.AppUserDao;
import invoicemanagerv2.appuser.AppUserRegistrationRequest;
import invoicemanagerv2.company.Company;
import invoicemanagerv2.company.CompanyDTO;
import invoicemanagerv2.company.CompanyDao;
import invoicemanagerv2.company.CompanyRequest;
import invoicemanagerv2.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersTest.class)
public class CompanyIT {

    @Autowired
    private WebTestClient webTestClient;

    @Qualifier("company_jpa")
    @Autowired
    private CompanyDao companyDao;

    @Qualifier("app_user_jpa")
    @Autowired
    private AppUserDao appUserDao;
    private static final String USER_PATH = "/api/v1/users";
    private static final String COMPANY_PATH = "/api/v1/companies";
    AppUser appUser;
    @BeforeEach
    void setUp() {
        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress();

        String password = "password";

        AppUserRegistrationRequest appUserRegistrationRequest = new AppUserRegistrationRequest(
                name, email, password
        );

        //send a register request
        webTestClient.post()
                .uri(USER_PATH + "/register-user")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(appUserRegistrationRequest),
                        AppUserRegistrationRequest.class
                )
                .exchange()
                .expectStatus()
                .isOk();

        this.appUser = appUserDao.selectAppUserByEmail(email).orElseThrow(
                () -> new UserNotFoundException("user with email: [%s] not found".formatted(email))
        );
    }
    @Test
    void canAddCompany() {
        String companyName = FAKER.company().name();
        String accountantEmail = FAKER.internet().safeEmailAddress();

        CompanyRequest companyRequest = new CompanyRequest(
                companyName, accountantEmail
        );


        // Send a request to add a company
        webTestClient.post()
                .uri(COMPANY_PATH + "?userId=" + appUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(companyRequest),
                        CompanyRequest.class
                )
                .exchange()
                .expectStatus()
                .isOk();

        List<CompanyDTO> fetchedCompanies = webTestClient.get()
                .uri(COMPANY_PATH + "/get-companies?userId=" + appUser.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(CompanyDTO.class)
                .getResponseBody()
                .collectList()
                .block();

        assertThat(fetchedCompanies).isNotNull();
        CompanyDTO company = Objects.requireNonNull(fetchedCompanies).get(0);
        assertThat(company).isNotNull();
    }

    @Test
    void canUpdateCompany() {
        String companyName = FAKER.company().name();
        String accountantEmail = FAKER.internet().safeEmailAddress();

        CompanyRequest companyRequest = new CompanyRequest(
                companyName, accountantEmail
        );

        // Send a request to add a company
        webTestClient.post()
                .uri(COMPANY_PATH + "?userId=" + appUser.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(companyRequest),
                        CompanyRequest.class
                )
                .exchange()
                .expectStatus()
                .isOk();

        List<CompanyDTO> fetchedCompanies = webTestClient.get()
                .uri(COMPANY_PATH + "/get-companies?userId=" + appUser.getId())
                .exchange()
                .expectStatus().isOk()
                .returnResult(CompanyDTO.class)
                .getResponseBody()
                .collectList()
                .block();

        assertThat(fetchedCompanies).isNotNull();

        CompanyDTO company = Objects.requireNonNull(fetchedCompanies).get(0);

        assertThat(company).isNotNull();

        // Update the company
        String updatedCompanyName = companyName + " Updated";
        String updatedAccountantEmail = "updated_" + accountantEmail;

        CompanyRequest updateRequest = new CompanyRequest(
                updatedCompanyName, updatedAccountantEmail
        );

        String companyId = company.id();

        webTestClient.put()
                .uri(COMPANY_PATH + "/{companyId}", companyId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(updateRequest), CompanyRequest.class
                )
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);

        Company updatedCompany = companyDao.selectCompanyByCompanyId(companyId).get();

        assertThat(updatedCompany.getCompanyName()).isEqualTo(updatedCompanyName);
        assertThat(updatedCompany.getAccountantEmail()).isEqualTo(updatedAccountantEmail);
    }
}