package com.tmszw.invoicemanagerv2.invoice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> uploadInvoiceFile(
            @RequestParam("companyId") Integer companyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String description) {
        try {
            invoiceService.uploadInvoiceFile(companyId, file, description);
            return ResponseEntity.status(HttpStatus.CREATED).body("Invoice uploaded successfully");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/send-invoices")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> sendInvoices(@RequestParam("companyId") Integer companyId,
                                          @RequestBody List<Integer> invoiceIds) {
        try {
            invoiceService.sendInvoices(companyId, invoiceIds);
            logger.info("invoices sent to: %s".formatted(companyId));
            return ResponseEntity.ok("Invoices sent successfully");
        } catch (Exception e) {
            logger.error("Error occurred while sending invoices: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/delete-invoices")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteInvoices(@RequestParam("companyId") Integer companyId,
                                            @RequestBody List<Integer> invoiceIds) {
        try {
            invoiceService.deleteInvoices(companyId, invoiceIds);
            logger.info("invoices successfully deleted");
            return ResponseEntity.status(HttpStatus.OK).body("Invoices deleted successfully");
        } catch (Exception e) {
            logger.error("Error occurred while deleting invoices: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(
            value = "/get-invoice-file/{invoiceId}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<byte[]> getInvoiceFile(
            @PathVariable("invoiceId") Integer invoiceId) {
        byte[] invoiceFile = invoiceService.getInvoiceFile(invoiceId);
        return ResponseEntity.status(HttpStatus.OK).body(invoiceFile);
    }

    @GetMapping("/get-invoice/{invoiceId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getInvoice(@PathVariable("invoiceId") Integer id) {
        try {
            InvoiceDTO invoiceDTO = invoiceService.getInvoice(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(invoiceDTO);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/get-invoices")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getInvoices(@RequestParam("companyId") Integer companyId) {
        try {
            List<InvoiceDTO> invoices = invoiceService.getCompanyInvoices(companyId);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PutMapping("/update-pending")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> setInvoiceStatus(@RequestParam Integer invoiceId) {
        try {
            invoiceService.updateInvoiceStatus(invoiceId);
            return ResponseEntity.status(HttpStatus.OK).body("Invoice updated successfully");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/update-invoice")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateInvoiceDescription(@RequestParam Integer invoiceId, @RequestBody InvoiceUpdateRequest invoiceUpdateRequest) {
        try {
            invoiceService.updateInvoice(invoiceId, invoiceUpdateRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Invoice updated successfully");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}