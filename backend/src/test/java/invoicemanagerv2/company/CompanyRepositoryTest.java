package com.tmszw.invoicemanagerv2.company;

import com.tmszw.invoicemanagerv2.AbstractTestcontainers;
import com.tmszw.invoicemanagerv2.appuser.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompanyRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CompanyRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationContext applicationContext;

    private final AppUser appUser = new AppUser();

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());

        appUser.setId(UUID.randomUUID().toString());
        appUser.setUsername(FAKER.name().fullName());
        appUser.setEmail(FAKER.internet().safeEmailAddress());
        appUser.setPassword("password");
        appUser.setEnabled(true);
        entityManager.merge(appUser);
        entityManager.flush();
    }

    @Test
    void existsCompanyById() {
        //given
        Company company = new Company();
        company.setCompanyName(FAKER.company().name());
        company.setUser(appUser);
        company.setAccountantEmail(FAKER.internet().safeEmailAddress());

        entityManager.persist(company);
        entityManager.flush();

        //when
        var actual = underTest.existsById(company.getCompanyId());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCompanyById_WillFailWhenNotInDB() {
        //given
        Integer id = 1;

        //when
        var actual = underTest.existsById(id);

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void whenFindAllUserCompanies_ThenReturnCompanyList() {
        // given
        Company company = new Company();
        company.setCompanyName(FAKER.company().name());
        company.setUser(appUser);
        company.setAccountantEmail(FAKER.internet().safeEmailAddress());

        entityManager.persist(company);
        entityManager.flush();

        Company company2 = new Company();
        company2.setCompanyName(FAKER.company().name());
        company2.setUser(appUser);
        company2.setAccountantEmail(FAKER.internet().safeEmailAddress());

        entityManager.persist(company2);
        entityManager.flush();

        // when
        List<Company> found = underTest.findAllUserCompanies(appUser.getId());

        // then
        assertThat(found.size()).isEqualTo(2);
        assertThat(found.get(0)).isEqualTo(company);
        assertThat(found.get(1)).isEqualTo(company2);
    }

    @Test
    void whenFindCompanyById_ThenReturnCompany() {
        //given
        Company company = new Company();
        company.setCompanyName(FAKER.company().name());
        company.setUser(appUser);
        company.setAccountantEmail(FAKER.internet().safeEmailAddress());

        entityManager.persist(company);
        entityManager.flush();

        //when
        var actualCompany = underTest.findCompanyById(company.getCompanyId());

        assertThat(actualCompany).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getCompanyName()).isEqualTo(company.getCompanyName());
            assertThat(c.getUser()).isEqualTo(appUser);
            assertThat(c.getAccountantEmail()).isEqualTo(company.getAccountantEmail());
        });
    }
}