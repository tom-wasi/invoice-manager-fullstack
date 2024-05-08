package com.tmszw.invoicemanagerv2.auth;

public record AuthenticationRequest(
        String email,
        String password
) {
}
