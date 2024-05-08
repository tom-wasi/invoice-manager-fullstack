package com.tmszw.invoicemanagerv2.appuser;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppUserController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserController.class);

    private final AppUserService appUserService;

    @GetMapping("/{appUserId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public AppUserDTO getAppUser(@PathVariable("appUserId") String appUserId) {
        return appUserService.getAppUserDTO(appUserId);
    }

    @ApiOperation(value = "Register a new user", notes = "Registers a new user with provided details. User has to confirm registration by clicking the link in the email message.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User registered successfully"),
            @ApiResponse(code = 400, message = "Invalid registration request"),
    })
    @PostMapping("/register-user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> registerAppUser(@Valid @RequestBody AppUserRegistrationRequest request, BindingResult bindingResult) {

        logger.info("Attempting to register user with email: {}", request.email());

        appUserService.registerUser(request, bindingResult);

        if (bindingResult.hasErrors()) {
            logValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(validationErrors(bindingResult));
        }

        logger.info("User registration successful for email: {}", request.email());
        return ResponseEntity.ok("User registration successful!");
    }

    @PutMapping("/{appUserId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateUser(@RequestBody AppUserUpdateRequest appUserUpdateRequest) {
        logger.info("Attempting to update user with email: {}", appUserUpdateRequest.email());
        try {
            appUserService.updateAppUser(appUserUpdateRequest);
            logger.info("User update successful for user with email: {}", appUserUpdateRequest.email());
            return ResponseEntity.ok("User updated successfully!");
        } catch (Exception e) {
            logger.info("User update failed.");
            return ResponseEntity.badRequest().body(e);
        }
    }

    @DeleteMapping("/{appUserId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteUser(@PathVariable("appUserId") String appUserId) {
        logger.info("Deleting user with id {}", appUserId);
        appUserService.deleteAppUser(appUserId);
        return ResponseEntity.ok().build();
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
