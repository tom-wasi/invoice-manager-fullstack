package invoicemanagerv2.appuser;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AppUserController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    private final AppUserService appUserService;

    @GetMapping("/{appUserId}")
    public AppUserDTO getAppUser(@PathVariable("appUserId") String appUserId) {
        return appUserService.getAppUserDTO(appUserId);
    }

    @PostMapping("/register-user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerAppUser(@Valid @RequestBody AppUserRegistrationRequest request, BindingResult bindingResult) {

        logger.info("Attempting to register user with email: {}", request.email());

        try {
            appUserService.registerUser(request, bindingResult);
            if (bindingResult.hasErrors()) {
                logValidationErrors(bindingResult);
                return ResponseEntity.badRequest().body(validationErrors(bindingResult));
            }
            logger.info("User registration successful for e-mail: {}", request.email());
            return ResponseEntity.ok("User registration successful!");
        } catch (Exception e) {
            logger.error("Error while registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody AppUserUpdateRequest appUserUpdateRequest) {
        logger.info("Attempting to update user with email: {}", appUserUpdateRequest.email());
        try {
            appUserService.updateAppUser(appUserUpdateRequest);
            logger.info("User update successful for user with email: {}", appUserUpdateRequest.email());
            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            logger.info("User update failed.");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{appUserId}")
    public ResponseEntity<?> deleteUser(@PathVariable("appUserId") String appUserId) {
        logger.info("Deleting user with id {}", appUserId);
        try {
            appUserService.deleteAppUser(appUserId);
            return ResponseEntity.ok("User deleted successfully!");
        } catch (Exception e) {
            logger.error("Error while deleting a user with id: {}, message: {}", appUserId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private void logValidationErrors(BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(error ->
                logger.warn("Validation error - Field: {}, Message: {}", error.getField(), error.getDefaultMessage()));
    }

    private Map<String, String> validationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();

        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }
}
