package com.tmszw.invoicemanagerv2.appuser;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppUserRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        //given
        AppUserRowMapper appUserRowMapper = new AppUserRowMapper();

        ResultSet rs = mock(ResultSet.class);
        when(rs.getString("user_id")).thenReturn("abcdefgh");
        when(rs.getString("username")).thenReturn("username");
        when(rs.getString("email")).thenReturn("user@example.com");
        when(rs.getString("password")).thenReturn("password");
        when(rs.getBoolean("is_enabled")).thenReturn(true);

        AppUser actual = appUserRowMapper.mapRow(rs, 1);

        AppUser expected = new AppUser(
                "abcdefgh",
                "username",
                "user@example.com",
                "password",
                true
        );

        assertThat(actual).isEqualTo(expected);
    }
}
