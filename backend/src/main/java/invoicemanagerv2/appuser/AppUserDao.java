package com.tmszw.invoicemanagerv2.appuser;

import java.util.Optional;

public interface AppUserDao {
    Optional<AppUser> selectAppUserByUserId(String userId);
    void insertAppUser(AppUser appUser);
    boolean existsAppUserWithEmail(String email);
    boolean existsAppUserByUserId(String userId);
    void deleteAppUserByUserId(String userId);
    void updateAppUser(AppUser update);
    Optional<AppUser> selectAppUserByEmail(String email);
}