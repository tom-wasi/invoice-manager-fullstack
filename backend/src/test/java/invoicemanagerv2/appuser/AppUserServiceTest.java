package invoicemanagerv2.appuser;

import invoicemanagerv2.exception.UserNotFoundException;
import invoicemanagerv2.mail.MailService;
import invoicemanagerv2.mail.confirmation.ConfirmationToken;
import invoicemanagerv2.mail.confirmation.ConfirmationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.UUID;

import static invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppUserServiceTest {
    @Mock
    private MailService mailService;
    AppUserDao appUserDao = mock(AppUserDao.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    ConfirmationTokenRepository confirmationTokenRepository = mock(ConfirmationTokenRepository.class);
    AppUserService underTest;
    private AppUserService underTestSpy;
    private final AppUserDTOMapper appUserDTOMapper = new AppUserDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new AppUserService(appUserDao, appUserDTOMapper, passwordEncoder, confirmationTokenRepository, mailService);
        underTestSpy = spy(underTest);
    }

    @Test
    void getAppUser_ShouldReturnAppUser() {
        //given
        AppUser appUser = new AppUser(UUID.randomUUID().toString(), FAKER.name().fullName(), FAKER.internet().safeEmailAddress(), FAKER.internet().password(), true);

        when(appUserDao.selectAppUserByUserId(appUser.getId())).thenReturn(Optional.of(appUser));
        AppUserDTO expected = appUserDTOMapper.apply(appUser);

        //when
        AppUserDTO actual = underTestSpy.getAppUserDTO(appUser.getId());

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetAppUserReturnEmptyOptional() {
        //given
        String id = UUID.randomUUID().toString();

        when(appUserDao.selectAppUserByUserId(id)).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTestSpy.getAppUser(id)).isInstanceOf(UserNotFoundException.class).hasMessage("user with id: [%s] not found".formatted(id));
    }

    @Test
    void registerUser_ShouldAddUser() {
        //given
        String email = FAKER.internet().safeEmailAddress();

        when(appUserDao.existsAppUserWithEmail(email)).thenReturn(false);

        AppUserRegistrationRequest request = new AppUserRegistrationRequest(
                FAKER.name().fullName(),
                email,
                "password"
        );

        String passwordHash = "%ghj#;j3s";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "appUserRegistrationRequest");

        //when
        underTestSpy.registerUser(request, bindingResult);

        //then
        assertThat(bindingResult.hasErrors()).isFalse();

        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        verify(appUserDao).insertAppUser(appUserArgumentCaptor.capture());
        AppUser capturedAppUser = appUserArgumentCaptor.getValue();

        assertThat(capturedAppUser.getUsername()).isEqualTo(request.username());
        assertThat(capturedAppUser.getEmail()).isEqualTo(request.email());
        assertThat(capturedAppUser.getPassword()).isEqualTo(passwordHash);

        ArgumentCaptor<ConfirmationToken> confirmationTokenArgumentCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);
        verify(confirmationTokenRepository).save(confirmationTokenArgumentCaptor.capture());
        ConfirmationToken capturedConfirmationToken = confirmationTokenArgumentCaptor.getValue();
        assertThat(capturedConfirmationToken.getUser()).isEqualTo(capturedAppUser);

        verify(underTestSpy).sendConfirmationEmail(capturedAppUser, capturedConfirmationToken);
    }

    @Test
    void deleteAppUserById_ShouldDeleteAppUser() {
        //given
        String id = UUID.randomUUID().toString();
        when(appUserDao.existsAppUserByUserId(id)).thenReturn(true);

        //when
        underTestSpy.deleteAppUser(id);

        //then
        verify(appUserDao).deleteAppUserByUserId(id);
    }

    @Test
    void deleteAppUserById_ShouldThrowWhenNotExists() {
        //given
        String id = UUID.randomUUID().toString();

        when(appUserDao.existsAppUserByUserId(id)).thenReturn(false);

        //when
        assertThatThrownBy(() -> underTestSpy.deleteAppUser(id)).isInstanceOf(UserNotFoundException.class).hasMessage("user with id: [%s] not found".formatted(id));

        //then
        verify(appUserDao, never()).deleteAppUserByUserId(id);
    }

    @Test
    void updateAppUser_ShouldUpdateAppUser() {
        //given
        String id = UUID.randomUUID().toString();
        AppUser appUser = new AppUser(id, FAKER.name().fullName(), FAKER.internet().safeEmailAddress(), "oldPassword", true);
        when(appUserDao.selectAppUserByUserId(id)).thenReturn(Optional.of(appUser));

        String email = appUser.getEmail();
        String newUsername = "newUsername";
        String newPassword = "newPassword";

        AppUserUpdateRequest request = new AppUserUpdateRequest(newUsername, email, newPassword);
        when(appUserDao.selectAppUserByEmail(email)).thenReturn(Optional.of(appUser));

        String passwordHash = "%ghj#;j3s";
        when(passwordEncoder.encode(request.newPassword())).thenReturn(passwordHash);

        //when
        underTestSpy.updateAppUser(request);

        //then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        verify(appUserDao).updateAppUser(appUserArgumentCaptor.capture());
        AppUser capturedAppUser = appUserArgumentCaptor.getValue();

        assertEquals(capturedAppUser.getUsername(), request.newUsername());
        assertEquals(capturedAppUser.getEmail(), request.email());
        assertThat(passwordEncoder.matches(capturedAppUser.getPassword(), request.newPassword()));
    }

    @Test
    void updateAppUser_WillThrowWhenNothingToUpdate() {
        //given
        String id = UUID.randomUUID().toString();
        AppUser appUser = new AppUser(id, FAKER.name().fullName(), FAKER.internet().safeEmailAddress(), "password", true);

        String newUsername = appUser.getUsername();
        String newEmail = appUser.getEmail();
        String newPassword = "password";

        AppUserUpdateRequest request = new AppUserUpdateRequest(
                newUsername,
                newEmail,
                newPassword
        );

        //when
        String passwordHash = "%ghj#;j3s";
        when(passwordEncoder.encode(request.newPassword())).thenReturn(passwordHash);
        when(passwordEncoder.matches(newPassword, appUser.getPassword())).thenReturn(true);


        when(appUserDao.existsAppUserWithEmail(newEmail)).thenReturn(true);
        when(appUserDao.selectAppUserByEmail(appUser.getEmail())).thenReturn(Optional.of(appUser));

        assertThatThrownBy(() -> underTestSpy.updateAppUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No changes to update");

        //then
        verify(appUserDao, never()).updateAppUser(any());
    }

    @Test
    void updateAppUser_ShouldUpdateOnlyUsername() {
        //given
        String id = UUID.randomUUID().toString();
        AppUser appUser = new AppUser(id, FAKER.name().fullName(), FAKER.internet().safeEmailAddress(), FAKER.internet().password(), true);
        when(appUserDao.selectAppUserByUserId(id)).thenReturn(Optional.of(appUser));

        String email = appUser.getEmail();
        String newUsername = "newUsername";
        String newPassword = "newPassword";

        AppUserUpdateRequest request = new AppUserUpdateRequest(newUsername, email, newPassword);
        when(appUserDao.selectAppUserByEmail(email)).thenReturn(Optional.of(appUser));
        //when

        underTestSpy.updateAppUser(request);
        //then
        ArgumentCaptor<AppUser> appUserArgumentCaptor = ArgumentCaptor.forClass(AppUser.class);

        verify(appUserDao).updateAppUser(appUserArgumentCaptor.capture());
        AppUser capturedAppUser = appUserArgumentCaptor.getValue();

        assertEquals(capturedAppUser.getUsername(), request.newUsername());
        assertEquals(capturedAppUser.getEmail(), request.email());
    }
}