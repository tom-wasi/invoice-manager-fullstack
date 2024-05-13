package invoicemanagerv2.journey;


import invoicemanagerv2.TestcontainersTest;
import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.appuser.AppUserDao;
import invoicemanagerv2.appuser.AppUserRegistrationRequest;
import invoicemanagerv2.appuser.AppUserUpdateRequest;
import invoicemanagerv2.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersTest.class)
public class AppUserIT {

    @Autowired
    private WebTestClient webTestClient;

    @Qualifier("app_user_jpa")
    @Autowired
    private AppUserDao appUserDao;

    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final String USER_PATH = "/api/v1/users";

    @Test
    void canRegister() {

        String name = FAKER
                .name()
                .fullName();

        String email = FAKER
                .internet()
                .safeEmailAddress();

        String password = "password";

        AppUserRegistrationRequest appUserRegistrationRequest = new AppUserRegistrationRequest(
                name,
                email,
                password
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

        AppUser appUser = appUserDao.selectAppUserByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(("User with email [%s] not found".formatted(email)))
                );

        //assert that
        assertThat(appUser).isNotNull();
    }

    @Test
    void canUpdate() {

        //given
        String name = FAKER
                .name()
                .fullName();

        String email = FAKER
                .internet()
                .safeEmailAddress();

        String password = "password";

        AppUserRegistrationRequest appUserRegistrationRequest = new AppUserRegistrationRequest(
                name, email, password
        );

        //when
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

        AppUser appUser = appUserDao.selectAppUserByEmail(email).orElseThrow();

        assertThat(appUser).isNotNull();


        String nameToUpdate = "test name";
        String passwordToUpdate = "12341234";

        AppUserUpdateRequest appUserUpdateRequest = new AppUserUpdateRequest(
                nameToUpdate, email, passwordToUpdate
        );

        String appUserId = appUser.getId();

        webTestClient.put()
                .uri(USER_PATH + "/{appUserId}", appUserId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(appUserUpdateRequest), AppUserUpdateRequest.class
                )
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);

        AppUser userAfterUpdate = appUserDao.selectAppUserByEmail(email).orElseThrow();

        //then
        assertThat(userAfterUpdate.getUsername()).isEqualTo(nameToUpdate);
        assertThat(userAfterUpdate.getEmail()).isEqualTo(email);
        assertThat(passwordEncoder.matches(passwordToUpdate, userAfterUpdate.getPassword()));
    }

    @Test
    void canDelete() {
        String name = FAKER
                .name()
                .fullName();

        String email = FAKER
                .internet()
                .safeEmailAddress();

        String password = "password";

        AppUserRegistrationRequest appUserRegistrationRequest = new AppUserRegistrationRequest(
                name, email, password
        );

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

        AppUser appUser = appUserDao.selectAppUserByEmail(email).orElseThrow();

        String appUserId = appUser.getId();

        webTestClient.delete()
                .uri(USER_PATH + "/{appUserId}", appUserId)
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(appUserDao.selectAppUserByEmail(email)).isEqualTo(Optional.empty());
    }
}
