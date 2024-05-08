package com.tmszw.invoicemanagerv2.auth;

import com.tmszw.invoicemanagerv2.appuser.*;
import com.tmszw.invoicemanagerv2.exception.UserNotVerifiedException;
import com.tmszw.invoicemanagerv2.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final AppUserDTOMapper appUserDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthenticationResponse login(AuthenticationRequest request) {

        boolean isEnabled = appUserService.getIsEnabled(request.email());

        if (!isEnabled) {
            throw new UserNotVerifiedException("Please confirm account with the link provided in e-mail message");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            AppUser principal = (AppUser) authentication.getPrincipal();
            AppUserDTO appUserDTO = appUserDTOMapper.apply(principal);
            String token = jwtUtil.issueToken(appUserDTO.id(), appUserDTO.roles());
            return new AuthenticationResponse(token);

        } catch (AuthenticationException e) {
            // Log the exception message
            System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        }
    }
}