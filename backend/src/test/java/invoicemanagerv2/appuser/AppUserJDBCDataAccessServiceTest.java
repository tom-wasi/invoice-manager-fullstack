package com.tmszw.invoicemanagerv2.appuser;

import com.tmszw.invoicemanagerv2.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AppUserJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private AppUserJDBCDataAccessService underTest;

    private final AppUserRowMapper appUserRowMapper = new AppUserRowMapper();

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        underTest = new AppUserJDBCDataAccessService(
                getJdbcTemplate(),
                appUserRowMapper
        );
    }

    @Test
    void selectAppUserById() {
        //given

        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );

        //when
        underTest.insertAppUser(appUser);
        Optional<AppUser> actual = underTest.selectAppUserByUserId(appUser.getId());

        //then
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(appUser.getId());
            assertThat(a.getUsername()).isEqualTo(appUser.getUsername());
            assertThat(a.getEmail()).isEqualTo(appUser.getEmail());
        });
    }

    @Test
    void selectAppUserByEmail() {
        //given
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );

        //when
        underTest.insertAppUser(appUser);
        Optional<AppUser> actual = underTest.selectAppUserByEmail(appUser.getEmail());

        //then
        assertThat(actual).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(appUser.getId());
            assertThat(a.getUsername()).isEqualTo(appUser.getUsername());
            assertThat(a.getEmail()).isEqualTo(appUser.getEmail());
        });
    }

    @Test
    void willReturnEmptyWhenSelectAppUserById() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        var actual = underTest.selectAppUserByUserId(id);

        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void existsAppUserWithEmail() {
        //given
        ;
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );

        //when
        underTest.insertAppUser(appUser);
        boolean actual = underTest.existsAppUserWithEmail(appUser.getEmail());

        //then
        assertThat(actual).isEqualTo(true);
    }

    @Test
    void existsAppUserByUserId() {
        //given
        String email = FAKER.internet().safeEmailAddress();
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                email,
                "password",
                true
        );

        //when
        underTest.insertAppUser(appUser);
        boolean actual = underTest.existsAppUserByUserId(appUser.getId());

        //then
        assertThat(actual).isEqualTo(true);
    }

    @Test
    void canDeleteAppUserById() {
        //given
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );

        //when
        underTest.insertAppUser(appUser);
        boolean actual = underTest.existsAppUserByUserId(appUser.getId());

        //check if correctly inserted
        assertThat(actual).isEqualTo(true);

        //delete the user
        underTest.deleteAppUserByUserId(appUser.getId());
        var deleted = underTest.selectAppUserByUserId(appUser.getId());

        //then
        assertThat(deleted).isEmpty();
    }

    @Test
    void canUpdateUsername() {
        //given
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );

        //when
        underTest.insertAppUser(appUser);
        boolean actual = underTest.existsAppUserByUserId(appUser.getId());
        assertThat(actual).isEqualTo(true);

        //update the user
        String newUsername = "fooName";

        AppUser appUserToUpdate = new AppUser();
        appUserToUpdate.setId(appUser.getId());
        appUserToUpdate.setUsername(newUsername);
        underTest.updateAppUser(appUserToUpdate);

        Optional<AppUser> actualUser = underTest.selectAppUserByUserId(appUser.getId());
        //then
        assertThat(actualUser).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(appUser.getId());
            assertThat(a.getUsername()).isEqualTo(newUsername);
            assertThat(a.getEmail()).isEqualTo(appUser.getEmail());
        });
    }

    @Test
    void canUpdateEmail() {
        //given
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );

        //when
        underTest.insertAppUser(appUser);
        boolean actual = underTest.existsAppUserByUserId(appUser.getId());
        assertThat(actual).isEqualTo(true);

        //update the user
        String newEmail = "new@email.com";

        AppUser appUserToUpdate = new AppUser();
        appUserToUpdate.setId(appUser.getId());
        appUserToUpdate.setEmail(newEmail);

        underTest.updateAppUser(appUserToUpdate);

        Optional<AppUser> actualUser = underTest.selectAppUserByUserId(appUser.getId());

        //then
        assertThat(actualUser).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(appUser.getId());
            assertThat(a.getUsername()).isEqualTo(appUser.getUsername());
            assertThat(a.getEmail()).isEqualTo(newEmail);
        });
    }

    @Test
    void canUpdatePassword() {
        //given
        AppUser appUser = new AppUser();

        appUser.setId(UUID.randomUUID().toString());
        appUser.setUsername(FAKER.name().fullName());
        appUser.setEmail(FAKER.internet().safeEmailAddress());
        appUser.setPassword(passwordEncoder.encode("password"));
        appUser.setEnabled(true);

        //when
        underTest.insertAppUser(appUser);

        boolean actual = underTest.existsAppUserWithEmail(appUser.getEmail());
        assertThat(actual).isEqualTo(true);

        //update the user
        String newPassword = "newpassword1234";

        appUser.setPassword(newPassword);

        underTest.updateAppUser(appUser);

        Optional<AppUser> actualUser = underTest.selectAppUserByEmail(appUser.getEmail());

        //then
        assertThat(actualUser).isPresent().hasValueSatisfying(a -> {
            assertThat(a.getId()).isEqualTo(appUser.getId());
            assertThat(a.getUsername()).isEqualTo(appUser.getUsername());
            assertThat(a.getEmail()).isEqualTo(appUser.getEmail());
            assertThat(passwordEncoder.matches(a.getPassword(), newPassword));
        });
    }

}
