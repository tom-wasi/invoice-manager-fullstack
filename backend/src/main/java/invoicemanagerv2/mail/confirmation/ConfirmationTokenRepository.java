package invoicemanagerv2.mail.confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("confirmationTokenRepository")
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    ConfirmationToken findByConfirmationToken(String confirmationToken);
    @Query(value = "SELECT * FROM confirmation_token WHERE user_id =?1", nativeQuery = true)
    ConfirmationToken findByUserId(String id);
}
