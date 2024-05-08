package com.tmszw.invoicemanagerv2.appuser;

import com.tmszw.invoicemanagerv2.exception.UserNotFoundException;
import com.tmszw.invoicemanagerv2.mail.MailService;
import com.tmszw.invoicemanagerv2.mail.confirmation.ConfirmationToken;
import com.tmszw.invoicemanagerv2.mail.confirmation.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.UUID;

@Service
public class AppUserService {

    private final AppUserDao appUserDao;
    private final AppUserDTOMapper appUserDTOMapper;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final MailService mailService;

    @Value("${app.base-url}")
    private String url;

    public AppUserService(@Qualifier("app_user_jpa")
                          AppUserDao appUserDao,
                          AppUserDTOMapper appUserDTOMapper,
                          PasswordEncoder passwordEncoder, ConfirmationTokenRepository confirmationTokenRepository, MailService mailService) {
        this.appUserDao = appUserDao;
        this.appUserDTOMapper = appUserDTOMapper;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.mailService = mailService;
    }

    public void registerUser(AppUserRegistrationRequest request, BindingResult bindingResult) {

        validateRegistrationRequest(request, bindingResult);
        if (!bindingResult.hasErrors()) {
            AppUser user = buildUser(request);

            appUserDao.insertAppUser(user);
            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            confirmationTokenRepository.save(confirmationToken);
            sendConfirmationEmail(user, confirmationToken);
        }
    }

    public AppUserDTO getAppUserDTO(String appUserId) {
        return appUserDao.selectAppUserByUserId(appUserId)
                .map(appUserDTOMapper)
                .orElseThrow(() -> new UserNotFoundException("user with id: [%s] not found".formatted(appUserId)));
    }

    public AppUser getAppUser(String appUserId) {
        return appUserDao.selectAppUserByUserId(appUserId)
                .orElseThrow(() -> new UserNotFoundException("user with id: [%s] not found".formatted(appUserId)));
    }

    public void updateAppUser(AppUserUpdateRequest updateRequest) {
        AppUser appUser = appUserDao.selectAppUserByEmail(updateRequest.email())
                .orElseThrow(() -> new UserNotFoundException("user with email: [%s] not found".formatted(updateRequest.email())));

        boolean changesFlag = false;

        if (updateRequest.newUsername() != null && !updateRequest.newUsername().equals(appUser.getUsername())) {
            appUser.setUsername(updateRequest.newUsername());
            changesFlag = true;
        }

        if (updateRequest.newPassword() != null && !passwordEncoder.matches(updateRequest.newPassword(), appUser.getPassword())) {
            appUser.setPassword(passwordEncoder.encode(updateRequest.newPassword()));
            changesFlag = true;
        }

        if (!changesFlag) {
            throw new IllegalArgumentException("No changes to update");
        }
        appUserDao.updateAppUser(appUser);
    }

    public boolean emailExists(String email) {
        return appUserDao.existsAppUserWithEmail(email);
    }

    public void validateRegistrationRequest(AppUserRegistrationRequest request, BindingResult bindingResult) {

        if (emailExists(request.email())) {
            bindingResult.rejectValue("email", "email.exists", "Email already registered");
        }

        if (request.password() != null && request.password().length() < 8) {
            bindingResult.rejectValue("password", "password.short", "Password must be at least 8 characters long");
        }
    }

    private AppUser buildUser(AppUserRegistrationRequest request) {
        String userId;
        do {
            userId = UUID.randomUUID().toString();
        } while(appUserDao.existsAppUserByUserId(userId));

        return AppUser.builder()
                .id(userId)
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .isEnabled(false)
                .build();
    }

    public void deleteAppUser(String appUserId) {

        if(!appUserDao.existsAppUserByUserId(appUserId)) {
            throw new UserNotFoundException("user with id: [%s] not found".formatted(appUserId));
        }
        appUserDao.deleteAppUserByUserId(appUserId);
    }

    void sendConfirmationEmail(AppUser user, ConfirmationToken confirmationToken) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Complete the registration!");
        mailMessage.setText("To confirm your account, please click here: "
                + url + "/api/v1/auth/confirm-account?token=" + confirmationToken.getConfirmationToken());
        mailService.sendEmail(mailMessage);
    }

    public ResponseEntity<?> confirmEmail(String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            AppUser user = appUserDao.selectAppUserByEmail(token.getUser().getEmail()).orElseThrow();
            user.setEnabled(true);
            appUserDao.insertAppUser(user);
            return ResponseEntity.ok("Email verified successfully!");
        }
        return ResponseEntity.badRequest().body("Error: Couldn't verify email");

    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserDao.selectAppUserByEmail(email);
    }

    public boolean getIsEnabled(String email) {
        AppUser appUser = appUserDao.selectAppUserByEmail(email).orElseThrow();
        return appUser.isEnabled();
    }
}