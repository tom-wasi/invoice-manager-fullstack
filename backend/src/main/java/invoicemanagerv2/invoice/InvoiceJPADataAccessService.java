package com.tmszw.invoicemanagerv2.invoice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("invoice_jpa")
@RequiredArgsConstructor
public class InvoiceJPADataAccessService implements InvoiceDao {

    private final InvoiceRepository invoiceRepository;

    @Override
    public Optional<Invoice> selectInvoiceByInvoiceId(Integer invoiceId) {
        return invoiceRepository.findById(invoiceId);
    }

    @Override
    public List<Invoice> selectInvoicesByInvoiceIDs(List<Integer> invoiceIds) {
        return invoiceRepository.findAllById(invoiceIds);
    }

    @Override
    public void insertInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    @Override
    public boolean existsInvoiceWithId(Integer invoiceId) {
        return invoiceRepository.existsById(invoiceId);
    }

    @Override
    public void deleteInvoice(Integer invoiceId) {
        invoiceRepository.deleteById(invoiceId);
    }

    @Override
    public void updateInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> findAllCompanyInvoices(Integer companyId) {
        return invoiceRepository.findAllByCompanyId(companyId);
    }
}
