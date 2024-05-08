package invoicemanagerv2.journey;

import invoicemanagerv2.TestcontainersTest;
import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.appuser.AppUserDTO;
import invoicemanagerv2.appuser.AppUserDao;
import invoicemanagerv2.appuser.AppUserRegistrationRequest;
import invoicemanagerv2.auth.AuthenticationRequest;
import invoicemanagerv2.auth.AuthenticationResponse;
import invoicemanagerv2.jwt.JWTUtil;
import invoicemanagerv2.mail.confirmation.ConfirmationToken;
import invoicemanagerv2.mail.confirmation.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersTest.class)
public class AuthenticationIT {

    @Autowired
    private WebTestClient webTestClient;

    @Qualifier("app_user_jdbc")
    @Autowired
    private AppUserDao appUserDao;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private JWTUtil jwtUtil;
    private static final String AUTHENTICATION_PATH = "/api/v1/auth";
    private static final String USER_PATH = "/api/v1/users";

    @Test
    void canLogin() {
        //given
        //create appUserRegistrationRequest

        String name = FAKER.name().fullName();
        String email = FAKER.internet().safeEmailAddress();

        String password = "password";

        AppUserRegistrationRequest appUserRegistrationRequest = new AppUserRegistrationRequest(
                name, email, password
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                password
        );

        //send a post with authenticationRequest to check if a user can't log in unless account is created and confirmed
        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.NOT_FOUND);

        //send a post with appUserRegistrationRequest
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

        //check again to see if still cannot log in when not verified
        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        AppUser user = appUserDao.selectAppUserByEmail(email).orElseThrow();
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByUserId(user.getId());
        String token = confirmationToken.getConfirmationToken();

        //send a post with confirmationToken
        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/confirm-account?token=" + token)
                .exchange()
                .expectStatus()
                .isOk();

        //send an authenticationRequest to correctly log in
        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        String jwtToken = Objects.requireNonNull(result.getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)).get(0);

        String decodedIdFromToken = jwtUtil.getClaims(jwtToken).getSubject();

        EntityExchangeResult<AppUserDTO> fetchedUser = webTestClient.get()
                .uri(USER_PATH + "/" + decodedIdFromToken)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AppUserDTO>() {
                })
                .returnResult();

        AppUserDTO appUserDTO = fetchedUser.getResponseBody();

        //then
        //assertThat(jwtUtil.validateToken(
        //        jwtToken,
        //        Objects.requireNonNull(appUserDTO).id())).isTrue();

        assertThat(appUserDTO.email()).isEqualTo(email);
        assertThat(appUserDTO.username()).isEqualTo(name);
    }
}
