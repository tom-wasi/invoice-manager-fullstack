package invoicemanagerv2.invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceDao {

    Optional<Invoice> selectInvoiceByInvoiceId(Integer invoiceId);
    List<Invoice> selectInvoicesByInvoiceIDs(List<Integer> invoiceIds);
    void insertInvoice(Invoice invoice);
    boolean existsInvoiceWithId(Integer invoiceId);
    void deleteInvoice(Integer invoiceId);
    void updateInvoice(Invoice invoice);
    List<Invoice> findAllCompanyInvoices(String companyId);
}