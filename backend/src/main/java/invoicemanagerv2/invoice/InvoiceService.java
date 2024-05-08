package invoicemanagerv2.invoice;

import invoicemanagerv2.company.Company;
import invoicemanagerv2.company.CompanyService;
import invoicemanagerv2.exception.InvoiceNotFoundException;
import invoicemanagerv2.s3.S3Buckets;
import invoicemanagerv2.s3.S3Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class InvoiceService {
    private final S3Service s3Service;
    private final S3Buckets s3Buckets;
    private final InvoiceDao invoiceDao;
    private final CompanyService companyService;
    private final InvoiceDTOMapper invoiceDTOMapper;
    private final JavaMailSender mailSender;

    public InvoiceService(@Qualifier("invoice_jpa") InvoiceDao invoiceDao,
                          S3Service s3Service,
                          S3Buckets s3Buckets,
                          CompanyService companyService,
                          InvoiceDTOMapper invoiceDTOMapper,
                          JavaMailSender mailSender) {
        this.s3Service = s3Service;
        this.s3Buckets = s3Buckets;
        this.invoiceDao = invoiceDao;
        this.companyService = companyService;
        this.invoiceDTOMapper = invoiceDTOMapper;
        this.mailSender = mailSender;
    }

    public void uploadInvoiceFile(String companyId,
                                  MultipartFile file,
                                  String description) {

        checkIfCompanyExistsOrThrow(companyId);
        String fileId = UUID.randomUUID().toString();
        Invoice invoice = new Invoice();

        try {
            s3Service.putObject(
                    s3Buckets.getInvoice(),
                    "invoice-files/%s/%s".formatted(companyId, fileId),
                    file.getBytes()
            );
            System.out.println("File uploaded successfully to S3");
        } catch (IOException e) {
            System.err.println("Failed to upload invoice: " + e.getMessage());
            throw new RuntimeException("Failed to upload the invoice");
        }

        invoice.setCompany(companyService.getCompanyById(companyId));
        invoice.setDescription(description);
        invoice.setInvoice_file_id(fileId);
        invoice.setPending(true);
        invoice.setLocalDate(LocalDate.now());
        invoiceDao.insertInvoice(invoice);
    }

    public byte[] getInvoiceFile(Integer invoiceId) {
        Invoice invoice = invoiceDao.selectInvoiceByInvoiceId(invoiceId)
                .orElseThrow(
                        () -> new InvoiceNotFoundException(
                                "invoice with id [%s] not found".formatted(invoiceId)
                        ));

        return s3Service.getObject(
                s3Buckets.getInvoice(),
                "invoice-files/%s/%s".formatted(invoice.getCompany().getCompanyId(), invoice.getInvoice_file_id())
        );
    }

    public void updateInvoiceStatus(Integer invoiceId) {
        Invoice invoice = invoiceDao.selectInvoiceByInvoiceId(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(
                        "invoice with id [%s] not found".formatted(invoiceId)
                ));

        invoice.setPending(false);
        invoiceDao.updateInvoice(invoice);
    }
    private void checkIfCompanyExistsOrThrow(String companyId) {
        if (!companyService.existsById(companyId)) {
            throw new InvoiceNotFoundException(
                    "company with id: [%s] not found".formatted(companyId)
            );
        }
    }

    public List<InvoiceDTO> getCompanyInvoices(String companyId) {

        List<Invoice> invoices = invoiceDao.findAllCompanyInvoices(companyId);
        List<InvoiceDTO> invoiceDTOs = new ArrayList<>();
        for (Invoice c : invoices) {
            invoiceDTOs.add(invoiceDTOMapper.apply(c));
        }
        return invoiceDTOs;
    }

    public InvoiceDTO getInvoice(Integer invoiceId) {
        Invoice invoice = invoiceDao.selectInvoiceByInvoiceId(invoiceId)
                .orElseThrow(
                        () -> new InvoiceNotFoundException(("Invoice with id [%s] not found".formatted(invoiceId)))
                );
        return invoiceDTOMapper.apply(invoice);
    }

    public void deleteInvoice(String companyId, Integer invoiceId) {

        checkIfCompanyExistsOrThrow(companyId);
        Invoice invoice = invoiceDao.selectInvoiceByInvoiceId(invoiceId).orElseThrow(
                () -> new InvoiceNotFoundException("invoice with id: [%s] not found".formatted(invoiceId))
        );

        invoiceDao.deleteInvoice(invoiceId);
        try {
            s3Service.deleteObject(
                    s3Buckets.getInvoice(),
                    "invoice-files/%s/%s".formatted(companyId, invoice.getInvoice_file_id())
            );

        } catch (Exception e) {
            throw new RuntimeException("Invoice not present");
        }
    }

    public void deleteInvoices(String companyId, List<Integer> invoiceIds) {

        List<Invoice> invoices = invoiceDao.selectInvoicesByInvoiceIDs(invoiceIds);
        try {
            for (Invoice i : invoices) {
                deleteInvoice(companyId, i.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    public void sendInvoices(String companyId, List<Integer> invoiceIds) throws MessagingException {
        Company company = companyService.getCompanyById(companyId);

        if(company.getAccountantEmail() == null || company.getAccountantEmail().length() < 3) return;

        List<Invoice> invoices = invoiceDao.selectInvoicesByInvoiceIDs(invoiceIds);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Locale englishLocale = Locale.ENGLISH;
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM", englishLocale);
        String currentMonthName = monthFormatter.format(Calendar.getInstance().getTime());

        try {
            helper.setTo(company.getAccountantEmail());
            helper.setSubject("Invoices to settle - " + currentMonthName);
            helper.setText("Please find the attached invoices.");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (Invoice i : invoices) {
                byte[] invoiceFile = getInvoiceFile(i.getId());
                i.setPending(false);
                invoiceDao.updateInvoice(i);
                ZipEntry entry = new ZipEntry("invoice_" + i.getId() + ".jpg");
                entry.setSize(invoiceFile.length);
                zos.putNextEntry(entry);
                zos.write(invoiceFile);
                zos.closeEntry();
            }

            zos.close();
            helper.addAttachment("invoices.zip", new ByteArrayResource(baos.toByteArray()));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void updateInvoice(Integer invoiceId, InvoiceUpdateRequest invoiceUpdateRequest) {
        Invoice invoice = invoiceDao.selectInvoiceByInvoiceId(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(
                        "invoice with id [%s] not found".formatted(invoiceId)
                ));

        invoice.setDescription(invoiceUpdateRequest.invoiceDescription());
        invoiceDao.updateInvoice(invoice);
    }
}
