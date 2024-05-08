package invoicemanagerv2.invoice;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class InvoiceDTOMapper implements Function<Invoice, InvoiceDTO> {
    @Override
    public InvoiceDTO apply(Invoice invoice) {
        return new InvoiceDTO(
                invoice.getId(),
                invoice.getInvoice_file_id(),
                invoice.getDescription(),
                invoice.isPending(),
                invoice.getLocalDate()
        );
    }
}
