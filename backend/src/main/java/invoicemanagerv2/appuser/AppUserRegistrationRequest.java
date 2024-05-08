package com.tmszw.invoicemanagerv2.appuser;

public record AppUserRegistrationRequest(
        String username,
        String email,
        String password
) {
}
