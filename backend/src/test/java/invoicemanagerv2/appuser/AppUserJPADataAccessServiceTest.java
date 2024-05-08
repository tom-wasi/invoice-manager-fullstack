package com.tmszw.invoicemanagerv2.appuser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static com.tmszw.invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AppUserJPADataAccessServiceTest {

    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private AppUserJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setup() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new AppUserJPADataAccessService(appUserRepository);
    }
    @AfterEach
    void teardown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAppUserById() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        underTest.selectAppUserByUserId(id);

        //then
        verify(appUserRepository).findById(id);
    }

    @Test
    void insertAppUser() {
        //given
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "12341234",
                true
        );

        //when
        underTest.insertAppUser(appUser);

        //then
        verify(appUserRepository).save(appUser);
    }

    @Test
    void existsAppUserWithEmail() {
        //given
        String email = FAKER.internet().safeEmailAddress();

        //when
        underTest.existsAppUserWithEmail(email);

        //then
        verify(appUserRepository).existsAppUserByEmail(email);
    }

    @Test
    void existsAppUserById() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        underTest.existsAppUserByUserId(id);

        //then
        verify(appUserRepository).existsAppUserById(id);
    }

    @Test
    void deleteAppUser() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        underTest.deleteAppUserByUserId(id);

        //then
        verify(appUserRepository).deleteById(id);
    }

    @Test
    void updateAppUser() {
        //given
        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "12341234",
                true
        );

        //when
        underTest.updateAppUser(appUser);
        //then
        verify(appUserRepository).save(appUser);
    }

}
