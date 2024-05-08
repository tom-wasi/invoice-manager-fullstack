package invoicemanagerv2.company;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @GetMapping("/get-companies")
    public ResponseEntity<?> getAllCompanies(@RequestParam("userId") String userId) {
        try {
            List<CompanyDTO> companies = companyService.getAllUserCompanies(userId);
            return ResponseEntity.ok(companies);
        } catch (Exception e) {
            logger.error("There was a problem retrieving companies for user: {}, message: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<?> getCompany(@PathVariable("companyId") String companyId) {
        try {
            CompanyDTO companyDTO = companyService.getCompanyDTOById(companyId);
            return ResponseEntity.ok(companyDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addCompany(@RequestBody CompanyRequest company, @RequestParam String userId) {
        logger.info("Adding a company for user with id: {}", userId);
        try {
            companyService.addCompany(userId, company);
            logger.info("Successfully added a company for user with id: {}", userId);
            return ResponseEntity.ok().body("Company created successfully!");
        } catch (Exception e) {
            logger.warn("There was a problem creating the company: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteCompany(@PathVariable("companyId") String companyId) {
        logger.info("Attempting to delete company with id: {}", companyId);
        try {
            companyService.deleteCompany(companyId);
            logger.info("Successfully deleted a company with id: {}", companyId);
            return ResponseEntity.ok("Company deleted successfully!");
        } catch (Exception e) {
            logger.error("There was a problem while deleting the company with id: {}, message: {}", companyId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updateCompany(@PathVariable("companyId") String companyId, @RequestBody CompanyUpdateRequest company) {
        logger.info("Attempting to update a company with id: {}", companyId);
        try {
            companyService.updateCompany(companyId, company);
            logger.info("Successfully updated a company with id: {}", companyId);
            return ResponseEntity.ok("Company updated successfully!");
        } catch (Exception e) {
            logger.error("Error when updating a company with id: {}, message: {}", companyId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}