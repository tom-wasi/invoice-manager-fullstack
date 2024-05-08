package invoicemanagerv2.invoice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static invoicemanagerv2.AbstractTestcontainers.FAKER;
import static org.mockito.Mockito.*;

public class InvoiceJPADataAccessServiceTest {

    private final InvoiceRepository invoiceRepository = mock(InvoiceRepository.class);

    private InvoiceJPADataAccessService underTest;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new InvoiceJPADataAccessService(invoiceRepository);
    }

    @AfterEach
    void teardown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectInvoiceById() {
        //given
        Integer id = FAKER.number().randomDigit();

        //when
        underTest.selectInvoiceByInvoiceId(id);

        //then
        verify(invoiceRepository).findById(id);
    }

    @Test
    void selectInvoicesByInvoiceIDs() {
        //given
        List<Integer> invoiceIds = new ArrayList<>();

        invoiceIds.add(FAKER.number().randomDigit());

        //when
        underTest.selectInvoicesByInvoiceIDs(invoiceIds);

        //then
        verify(invoiceRepository).findAllById(invoiceIds);
    }

    @Test
    void findAllCompanyInvoices() {
        //given
        String id = UUID.randomUUID().toString();

        //when
        underTest.findAllCompanyInvoices(id);

        //then
        verify(invoiceRepository).findAllByCompanyId(id);
    }

    @Test
    void insertInvoice() {
        //given
        Invoice invoice = new Invoice();
        invoice.setInvoice_file_id("11111");
        invoice.setDescription("Test description");
        invoice.setLocalDate(LocalDate.now());
        invoice.setPending(true);

        //when
        underTest.insertInvoice(invoice);

        //then
        verify(invoiceRepository, times(1)).save(invoice);
    }

    @Test
    void updateInvoice() {
        //given
        Invoice invoice = new Invoice();
        invoice.setInvoice_file_id("11111");
        invoice.setDescription("Test description");
        invoice.setLocalDate(LocalDate.now());
        invoice.setPending(true);

        //when
        //then
        underTest.insertInvoice(invoice);
        verify(invoiceRepository, times(1)).save(invoice);

        //when
        //then
        invoice.setDescription("New test description");
        underTest.updateInvoice(invoice);
        verify(invoiceRepository, times(2)).save(invoice);
    }

    @Test
    void deleteInvoice() {
        //given
        Integer id = FAKER.number().randomDigit();

        //when
        underTest.deleteInvoice(id);

        //then
        verify(invoiceRepository).deleteById(id);
    }
}
