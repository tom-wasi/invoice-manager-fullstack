package com.tmszw.invoicemanagerv2.invoice;

import com.tmszw.invoicemanagerv2.company.Company;
import com.tmszw.invoicemanagerv2.company.CompanyService;
import com.tmszw.invoicemanagerv2.s3.S3Buckets;
import com.tmszw.invoicemanagerv2.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    private final InvoiceDao invoiceDao = mock(InvoiceDao.class);
    private final CompanyService companyService = mock(CompanyService.class);
    private final S3Service s3Service = mock(S3Service.class);
    private final S3Buckets s3Buckets = mock(S3Buckets.class);
    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final InvoiceDTOMapper invoiceDTOMapper = new InvoiceDTOMapper();
    private InvoiceService underTest;
    private InvoiceService underTestSpy;
    Invoice invoice = mock(Invoice.class);

    @BeforeEach
    void setUp() {
        underTest = new InvoiceService(
                invoiceDao,
                s3Service,
                s3Buckets,
                companyService,
                invoiceDTOMapper,
                mailSender
        );

        underTestSpy = spy(underTest);
    }

    @Test
    void getInvoice_ShouldReturnInvoiceDTO() {
        //given
        when(invoiceDao.selectInvoiceByInvoiceId(invoice.getId())).thenReturn(Optional.of(invoice));
        InvoiceDTO expected = invoiceDTOMapper.apply(invoice);

        //when
        InvoiceDTO actual = underTestSpy.getInvoice(invoice.getId());

        //then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getInvoiceFile_ShouldReturnInvoiceFile() {
        //given
        Integer invoiceId = 1;
        Invoice mockInvoice = mock(Invoice.class);
        Company mockCompany = mock(Company.class);
        Integer companyId = 1;
        String invoiceFileId = "111111";
        byte[] expectedFile = new byte[0];

        when(invoiceDao.selectInvoiceByInvoiceId(invoiceId)).thenReturn(Optional.of(mockInvoice));
        when(mockInvoice.getCompany()).thenReturn(mockCompany);
        when(mockCompany.getCompanyId()).thenReturn(companyId);
        when(mockInvoice.getInvoice_file_id()).thenReturn(invoiceFileId);
        when(s3Service.getObject(s3Buckets.getInvoice(), "invoice-files/" + companyId + "/" + invoiceFileId)).thenReturn(expectedFile);

        //when
        byte[] actualFile = underTest.getInvoiceFile(invoiceId);

        //then
        assertEquals(expectedFile, actualFile);
    }
}