package invoicemanagerv2;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestcontainers {

    public AbstractTestcontainers(){}

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway
                .configure()
                .dataSource(
                        postgreSQLContainer.getJdbcUrl(),
                        postgreSQLContainer.getUsername(),
                        postgreSQLContainer.getPassword()
                ).load();
        flyway.migrate();
    }

    @SuppressWarnings("resource")
    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("invoice-manager-test")
                    .withUsername("postgres")
                    .withPassword("mysecrettestpassword");

    @DynamicPropertySource
    protected static void registerDataSourceProperties(
            DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registry.add("spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registry.add("spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }

    private static DataSource getDataSource() {
        return DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .build();
    }

    @Contract(" -> new")
    protected static @NotNull JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    public static final Faker FAKER = new Faker();

}
