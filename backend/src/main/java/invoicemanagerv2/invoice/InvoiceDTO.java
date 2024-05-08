package com.tmszw.invoicemanagerv2.invoice;

import java.time.LocalDate;

public record InvoiceDTO(
        Integer id,
        String invoice_file_id,
        String description,
        boolean isPending,
        LocalDate uploaded
) {}