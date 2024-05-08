package invoicemanagerv2.auth;

import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.appuser.AppUserService;
import invoicemanagerv2.company.CompanyController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AppUserService appUserService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        logger.info("Attempting to authenticate a user with e-mail: {}", request.email());
        try {
            Optional<AppUser> appUser = appUserService.getUserByEmail(request.email());
            if (appUser.isEmpty()) {
                logger.warn("Invalid credentials");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid credentials");
            }

            AuthenticationResponse response = authenticationService.login(request);
            logger.info("Authentication successful for user with e-mail: {}", request.email());
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, response.token())
                    .body(response);
        } catch (Exception e) {
            logger.warn("Failed authentication: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {
        logger.info("Attempting to activate an account");
        try {
            return appUserService.confirmEmail(confirmationToken);
        } catch (Exception e) {
            logger.error("Failed to activate an account: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
