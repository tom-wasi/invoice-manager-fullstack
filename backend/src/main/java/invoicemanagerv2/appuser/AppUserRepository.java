package com.tmszw.invoicemanagerv2.appuser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    boolean existsAppUserByEmail(String email);
    @Query(value = "SELECT EXISTS(SELECT 1 FROM app_user a WHERE a.user_id = ?1)", nativeQuery = true)
    boolean existsAppUserById(String id);
    Optional<AppUser> findAppUserByEmail(String email);
}
