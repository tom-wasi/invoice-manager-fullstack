package invoicemanagerv2.appuser;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("app_user_jdbc")
public class AppUserJDBCDataAccessService implements AppUserDao {

    private final JdbcTemplate jdbcTemplate;
    private final AppUserRowMapper rowMapper;

    public AppUserJDBCDataAccessService(JdbcTemplate jdbcTemplate, AppUserRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<AppUser> selectAppUserByUserId
            (String appUserId) {

        var sql = """
                SELECT *
                FROM app_user
                WHERE user_id = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, appUserId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertAppUser(AppUser appUser) {
        var sql = """
                INSERT INTO app_user (
                user_id,
                username,
                email,
                password,
                is_enabled
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update
                (
                        sql,
                        appUser.getId(),
                        appUser.getUsername(),
                        appUser.getEmail(),
                        appUser.getPassword(),
                        false
                );
    }

    @Override
    public boolean existsAppUserWithEmail(String email) {
        var sql = """
                SELECT count(user_id)
                FROM app_user
                WHERE email = ?;
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsAppUserByUserId(String id) {
        var sql = """
                SELECT count(user_id)
                FROM app_user
                WHERE user_id = ?;
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public void deleteAppUserByUserId(String appUserId) {
        var sql = """
                DELETE
                FROM app_user
                WHERE user_id = ?;
                """;
        jdbcTemplate.update(sql, appUserId);
    }

    @Override
    public void updateAppUser(AppUser update) {

        if (update.getUsername() != null) {
            var sql = """
                    UPDATE app_user
                    SET username = ?
                    WHERE user_id = ?;
                    """;
            jdbcTemplate.update(sql, update.getUsername(), update.getId());
        }
        if (update.getPassword() != null) {
            var sql = """
                    UPDATE app_user
                    SET password = ?
                    WHERE user_id = ?;
                    """;
            jdbcTemplate.update(sql, update.getPassword(), update.getId());
        }
        if (update.getEmail() != null) {
            var sql = """
                    UPDATE app_user
                    SET email = ?
                    WHERE user_id = ?;
                    """;
            jdbcTemplate.update(sql, update.getEmail(), update.getId());
        }
    }

    @Override
    public Optional<AppUser> selectAppUserByEmail(String email) {

        var sql = """
                SELECT *
                FROM app_user
                WHERE email = ?;
                """;
        return jdbcTemplate.query(sql, rowMapper, email)
                .stream()
                .findFirst();
    }
}