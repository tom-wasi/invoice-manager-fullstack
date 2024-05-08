package invoicemanagerv2.auth;

import invoicemanagerv2.appuser.*;
import invoicemanagerv2.exception.UserNotFoundException;
import invoicemanagerv2.exception.UserNotVerifiedException;
import invoicemanagerv2.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private AppUserDTOMapper appUserDTOMapper;

    @Autowired
    private JWTUtil jwtUtil;

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
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            AppUser appUser = appUserService.getUserByEmail(principal.getUsername())
                    .orElseThrow();

            AppUserDTO appUserDTO = appUserDTOMapper.apply(appUser);
            String token = jwtUtil.issueToken(principal.getUsername());

            return new AuthenticationResponse(token, appUserDTO);

        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        }
    }
}