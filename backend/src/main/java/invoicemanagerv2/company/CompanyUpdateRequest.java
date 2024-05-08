package invoicemanagerv2.company;

public record CompanyUpdateRequest(
        String companyName,
        String accountantEmail
) {
}
