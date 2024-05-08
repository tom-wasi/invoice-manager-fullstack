package invoicemanagerv2.invoice;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository("invoice_jdbc")
public class InvoiceJDBCDataAccessService implements InvoiceDao {

    private final JdbcTemplate jdbcTemplate;

    private final InvoiceRowMapper invoiceRowMapper;

    public InvoiceJDBCDataAccessService(JdbcTemplate jdbcTemplate, InvoiceRowMapper invoiceRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.invoiceRowMapper = invoiceRowMapper;
    }

    @Override
    public Optional<Invoice> selectInvoiceByInvoiceId(Integer invoiceId) {
        var sql = """
                SELECT *
                FROM invoice
                WHERE id = ?;
                """;
        return jdbcTemplate.query(sql, invoiceRowMapper, invoiceId)
                .stream()
                .findFirst();
    }

    @Override
    public List<Invoice> selectInvoicesByInvoiceIDs(List<Integer> invoiceIds) {

        String ids = invoiceIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        var sql = "SELECT * FROM invoice WHERE id IN (" + ids + ")";

        // Execute the query and return the results
        return jdbcTemplate.query(sql, invoiceRowMapper);
    }

    @Override
    public void insertInvoice(Invoice invoice) {
        var sql = """
                INSERT INTO invoice (
                id,
                invoice_file_id,
                invoice_description,
                company_id,
                is_pending,
                uploaded)
                VALUES (?, ?, ?, ?, ?, ?);
                """;

        jdbcTemplate.update(
                sql,
                invoice.getId(),
                invoice.getInvoice_file_id(),
                invoice.getDescription(),
                invoice.getCompany(),
                invoice.isPending(),
                invoice.getLocalDate()
        );
    }

    @Override
    public boolean existsInvoiceWithId(Integer invoiceId) {
        var sql = """
                SELECT count(id)
                FROM invoice
                WHERE id = ?;
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, invoiceId);
        return count != null && count > 0;
    }

    @Override
    public void deleteInvoice(Integer invoiceId) {
        var sql = """
                DELETE
                FROM invoice
                WHERE id = ?;
                """;
        jdbcTemplate.update(sql, invoiceId);
    }

    @Override
    public void updateInvoice(Invoice update) {
        if(update.getDescription() != null) {
            var sql = """
                    UPDATE invoice
                    SET invoice_description = ?
                    WHERE id = ?;
                    """;
            jdbcTemplate.update(sql, update.getDescription(), update.getId());
        }

        if(update.isPending()) {
            var sql = """
                    UPDATE invoice
                    SET is_pending = ?
                    WHERE id = ?;
                    """;
            jdbcTemplate.update(sql, false, update.getId());
        }
    }

    @Override
    public List<Invoice> findAllCompanyInvoices(String companyId) {
        var sql = """
                SELECT *
                FROM invoice
                WHERE company_id = ?;
                """;

        return jdbcTemplate.query(sql, invoiceRowMapper, companyId);
    }
}
