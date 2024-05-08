package invoicemanagerv2.appuser;

import invoicemanagerv2.AbstractTestcontainers;
import invoicemanagerv2.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
public class AppUserRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private AppUserRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsAppUserByEmail() {
        //given

        AppUser appUser = new AppUser();
        appUser.setUsername(FAKER.name().fullName());
        appUser.setEmail(FAKER.internet().safeEmailAddress());
        appUser.setPassword("password");
        appUser.setEnabled(true);
        entityManager.persist(appUser);
        entityManager.flush();

        //when
        var actual = underTest.existsAppUserByEmail(appUser.getEmail());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsAppUserByEmailFailsWhenEmailNotPresent() {
        //given
        String email = FAKER.internet().safeEmailAddress();

        //when
        var actual = underTest.existsAppUserByEmail(email);

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void existsAppUserById() {
        //given
        AppUser appUser = new AppUser();
        appUser.setUsername(FAKER.name().fullName());
        appUser.setEmail(FAKER.internet().safeEmailAddress());
        appUser.setPassword("password");
        appUser.setEnabled(true);
        entityManager.persist(appUser);
        entityManager.flush();

        //when
        var actual = underTest.existsAppUserById(appUser.getId());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsAppUserByIdFailsWhenIdNotPresent() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        var actual = underTest.existsAppUserById(id);

        //then
        assertThat(actual).isFalse();
    }
}
