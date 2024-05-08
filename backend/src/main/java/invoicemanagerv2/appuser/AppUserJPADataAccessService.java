package com.tmszw.invoicemanagerv2.appuser;

import com.tmszw.invoicemanagerv2.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("app_user_jpa")
@RequiredArgsConstructor
public class AppUserJPADataAccessService implements AppUserDao {

    private final AppUserRepository appUserRepository;

    @Override
    public Optional<AppUser> selectAppUserByUserId(String id) {
        return appUserRepository.findById(id);
    }

    @Override
    @Transactional
    public void insertAppUser(AppUser appUser) {
        appUserRepository.save(appUser);
    }

    @Override
    public boolean existsAppUserWithEmail(String email) {
        return appUserRepository.existsAppUserByEmail(email);
    }

    @Override
    public boolean existsAppUserByUserId(String id) {
        return appUserRepository.existsAppUserById(id);
    }

    @Override
    @Transactional
    public void deleteAppUserByUserId(String appUserId) {
        appUserRepository.deleteById(appUserId);
    }

    @Override
    @Transactional
    public void updateAppUser(AppUser update) {
        appUserRepository.save(update);
    }

    @Override
    public Optional<AppUser> selectAppUserByEmail(String email) {
        return appUserRepository.findAppUserByEmail(email);
    }
}
