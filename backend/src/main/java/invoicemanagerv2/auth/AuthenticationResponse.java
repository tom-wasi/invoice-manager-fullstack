package invoicemanagerv2.auth;


import invoicemanagerv2.appuser.AppUserDTO;

public record AuthenticationResponse(
        String token,
        AppUserDTO appUserDTO
) {
}
