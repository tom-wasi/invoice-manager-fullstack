package invoicemanagerv2.invoice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    private final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadInvoiceFile(
            @RequestParam("companyId") String companyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String description) {
        logger.info("Attempting to upload an invoice for company with id: {}", companyId);
        try {

            invoiceService.uploadInvoiceFile(companyId, file, description);
            logger.info("Invoice upload successful");
            return ResponseEntity.status(HttpStatus.CREATED).body("Invoice uploaded successfully!");
        } catch (Exception e) {
            logger.error("There was an error while uploading the invoice: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/send-invoices")
    public ResponseEntity<?> sendInvoices(@RequestParam("companyId") String companyId,
                                          @RequestBody List<Integer> invoiceIds) {
        logger.info("Attempting to send invoices to accountant's e-mail address of company with id: {}", companyId);
        try {
            invoiceService.sendInvoices(companyId, invoiceIds);
            logger.info("invoices sent to accountant; company with id: {}", companyId);
            return ResponseEntity.ok("Invoices sent successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while sending invoices: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/delete-invoices")
    public ResponseEntity<?> deleteInvoices(@RequestParam("companyId") String companyId,
                                            @RequestBody List<Integer> invoiceIds) {
        logger.info("Attempting to delete invoices of company with id: {}", companyId);
        try {
            invoiceService.deleteInvoices(companyId, invoiceIds);
            logger.info("Invoices successfully deleted from company with id: {}", companyId);
            return ResponseEntity.status(HttpStatus.OK).body("Invoices deleted successfully!");
        } catch (Exception e) {
            logger.error("Error occurred while deleting invoices: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(
            value = "/get-invoice-file/{invoiceId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<?> getInvoiceFile(
            @PathVariable("invoiceId") Integer invoiceId) {
        try {
            byte[] invoiceFile = invoiceService.getInvoiceFile(invoiceId);
            return ResponseEntity.status(HttpStatus.OK).body(invoiceFile);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving invoice file: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-invoice/{invoiceId}")
    public ResponseEntity<?> getInvoice(@PathVariable("invoiceId") Integer id) {
        try {
            InvoiceDTO invoiceDTO = invoiceService.getInvoice(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoiceDTO);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving the invoice: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get-invoices")
    public ResponseEntity<?> getInvoices(@RequestParam("companyId") String companyId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getCompanyInvoices(companyId);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update-pending")
    public ResponseEntity<?> setInvoiceStatus(@RequestParam Integer invoiceId) {
        try {
            invoiceService.updateInvoiceStatus(invoiceId);
            return ResponseEntity.status(HttpStatus.OK).body("Invoice updated successfully!");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/update-invoice")
    public ResponseEntity<?> updateInvoiceDescription(@RequestParam Integer invoiceId, @RequestBody InvoiceUpdateRequest invoiceUpdateRequest) {
        try {
            invoiceService.updateInvoice(invoiceId, invoiceUpdateRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Invoice updated successfully!");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}