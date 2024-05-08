package invoicemanagerv2.invoice;

import invoicemanagerv2.AbstractTestcontainers;
import invoicemanagerv2.TestConfig;
import invoicemanagerv2.appuser.AppUser;
import invoicemanagerv2.company.Company;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
public class InvoiceRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private InvoiceRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ApplicationContext applicationContext;

    private final AutoCloseable autoCloseable = mock(AutoCloseable.class);

    private Company company;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());

        AppUser appUser = new AppUser(
                UUID.randomUUID().toString(),
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress(),
                "password",
                true
        );
        entityManager.merge(appUser);
        entityManager.flush();

        this.company = new Company(
                UUID.randomUUID().toString(),
                FAKER.company().name(),
                appUser,
                FAKER.internet().safeEmailAddress()
        );
        entityManager.merge(company);
        entityManager.flush();
    }

    @AfterEach
    void teardown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void existsInvoiceById() {
        //given
        Invoice invoice = new Invoice();
        invoice.setInvoice_file_id("11111");
        invoice.setDescription("Test description");
        invoice.setCompany(company);
        invoice.setLocalDate(LocalDate.now());

        entityManager.persist(invoice);
        entityManager.flush();

        //when
        var actual = underTest.existsById(invoice.getId());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void existsInvoiceById_WillFailWhenNotInDB() {
        //given
        Integer id = FAKER.number().randomDigit();

        //when
        var actual = underTest.existsById(id);

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void whenFindAllCompanyInvoices_ThenReturnInvoicesList() {
        //given

        Invoice invoice = new Invoice();
        invoice.setInvoice_file_id("11111");
        invoice.setDescription("Test description");
        invoice.setCompany(company);
        invoice.setLocalDate(LocalDate.now());

        entityManager.persist(invoice);
        entityManager.flush();

        Invoice invoice2 = new Invoice();
        invoice2.setInvoice_file_id("11112");
        invoice2.setDescription("Test description");
        invoice2.setCompany(company);
        invoice2.setLocalDate(LocalDate.now());

        entityManager.persist(invoice2);
        entityManager.flush();

        //when
        List<Invoice> actual = underTest.findAllByCompanyId(company.getCompanyId());

        //then
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual.get(0)).isEqualTo(invoice);
        assertThat(actual.get(1)).isEqualTo(invoice2);
    }

    @Test
    void whenFindInvoiceById_ThenReturnInvoice() {
        //given
        Invoice invoice = new Invoice();
        invoice.setInvoice_file_id("11111");
        invoice.setDescription("Test description");
        invoice.setCompany(company);
        invoice.setLocalDate(LocalDate.now());

        entityManager.persist(invoice);
        entityManager.flush();

        //when
        var actual = underTest.findById(invoice.getId());

        //then
        assertThat(actual).isPresent().hasValueSatisfying(i -> {
            assertThat(i.getCompany()).isEqualTo(company);
            assertThat(i.getInvoice_file_id()).isEqualTo(invoice.getInvoice_file_id());
            assertThat(i.getLocalDate()).isEqualTo(invoice.getLocalDate());
            assertThat(i.isPending()).isEqualTo(invoice.isPending());
        });
    }




}
