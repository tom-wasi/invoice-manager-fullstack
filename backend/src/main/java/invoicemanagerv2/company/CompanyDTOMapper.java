package invoicemanagerv2.company;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CompanyDTOMapper implements Function<Company, CompanyDTO> {
    @Override
    public CompanyDTO apply(Company company) {
        return new CompanyDTO(
                company.getCompanyId(),
                company.getCompanyName(),
                company.getAccountantEmail()
        );
    }
}
