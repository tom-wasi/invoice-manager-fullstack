package com.tmszw.invoicemanagerv2.company;

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
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/{companyId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public CompanyDTO getCompany(@PathVariable("companyId") Integer companyId) {
        return companyService.getCompanyDTOById(companyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addCompany(@RequestBody CompanyRequest company, @RequestParam String userId) {
        companyService.addCompany(userId, company);
        return ResponseEntity.ok().body("Company created successfully!");
    }

    @DeleteMapping("/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> deleteCompany(@PathVariable("companyId") Integer companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{companyId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateCompany(@PathVariable("companyId") Integer companyId, @RequestBody CompanyUpdateRequest company) {
        companyService.updateCompany(companyId, company);
        return ResponseEntity.ok().build();
    }
    private void logValidationErrors(BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(error ->
                logger.warn("Validation error - Field: {}, Message: {}", error.getField(), error.getDefaultMessage()));
    }

    private Map<String, String> validationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();

        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return errors;
    }
}