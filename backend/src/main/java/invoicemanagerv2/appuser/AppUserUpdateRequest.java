package com.tmszw.invoicemanagerv2.appuser;

public record AppUserUpdateRequest(
        String newUsername,
        String email,
        String newPassword
) {
}
