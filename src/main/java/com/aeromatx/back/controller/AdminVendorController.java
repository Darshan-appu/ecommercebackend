package com.aeromatx.back.controller;

import com.aeromatx.back.dto.admin.ProductRequestSubmissionResponseDTO;
import com.aeromatx.back.dto.vendor.VendorSummaryDTO;
import com.aeromatx.back.entity.ProductRequestSubmission;
import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.enums.RequestStatus;
import com.aeromatx.back.repository.ProductRequestSubmissionRepository;
import com.aeromatx.back.repository.VendorRepository;
import com.aeromatx.back.service.VendorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.aeromatx.back.dto.vendor.VendorDetailsDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/vendors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminVendorController {

    private final VendorService vendorService;
    private final VendorRepository vendorRepository;
    private final ProductRequestSubmissionRepository submissionRepository;
    private final ObjectMapper objectMapper;

    // ✅ 1. Approve Vendor
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{vendorId}/approve")
    public ResponseEntity<Vendor> approveVendor(@PathVariable Long vendorId) {
        Vendor approved = vendorService.approveVendor(vendorId);
        return ResponseEntity.ok(approved);
    }

    // ✅ 2. Reject Vendor
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{vendorId}/reject")
    public ResponseEntity<Vendor> rejectVendor(@PathVariable Long vendorId) {
        Vendor rejected = vendorService.rejectVendor(vendorId);
        return ResponseEntity.ok(rejected);
    }

    // ✅ 3. Get All Vendors (Full Details)
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("")
public ResponseEntity<List<VendorDetailsDTO>> getAllVendors() {
    List<VendorDetailsDTO> list = vendorRepository.findAll().stream()
        .map(this::toDetailsDto)
        .collect(Collectors.toList());
    return ResponseEntity.ok(list);
}

private VendorDetailsDTO toDetailsDto(Vendor v) {
    return VendorDetailsDTO.builder()
        .vendorId(v.getVendorId())
        .businessName(v.getBusinessName())
        .email(v.getEmail())
        .status(v.getStatus())
        .ratings(v.getRatings())
        .firstName(v.getFirstName())
        .lastName(v.getLastName())
        .companyAddress(v.getCompanyAddress())
        .city(v.getCity())
        .state(v.getState())
        .zipCode(v.getZipCode())
        .phoneDay(v.getPhoneDay())
        .phoneEvening(v.getPhoneEvening())
        .position(v.getPosition())
        .serviceDetails(v.getServiceDetails())
        .establishmentDate(v.getEstablishmentDate()) // it's a String in entity
        .serviceArea(v.getServiceArea())
        .businessType(v.getBusinessType())
        .insured(v.isInsured())     // ✅ correct getter
        .licensed(v.isLicensed())   // ✅ correct getter
        .licenseNumber(v.getLicenseNumber())
        .annualSales(v.getAnnualSales())
        .bankName(v.getBankName())
        .beneficiaryName(v.getBeneficiaryName())
        .submissionDate(v.getSubmissionDate()) // also String
        .signature(v.getSignature())
        .website(v.getWebsite())
        .build();
}


    // ✅ 4. Delete Vendor
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{vendorId}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Long vendorId) {
        vendorService.deleteVendorById(vendorId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // ✅ 5. View All Product Requests Submitted by Vendors
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/product-requests")
    public ResponseEntity<List<ProductRequestSubmissionResponseDTO>> getAllProductRequests() {
        List<ProductRequestSubmission> submissions = submissionRepository.findAll();

        List<ProductRequestSubmissionResponseDTO> response = submissions.stream().map(sub -> {
            ProductRequestSubmissionResponseDTO dto = new ProductRequestSubmissionResponseDTO();
            dto.setId(sub.getId());
            dto.setVendorEmail(sub.getVendorEmail());
            dto.setProductName(sub.getProductName());
            dto.setDescription(sub.getDescription());
            dto.setPrice(sub.getPrice());
            dto.setStock(sub.getStock());
            dto.setSelectedCategory(sub.getSelectedCategory());
            dto.setSelectedSubCategory(sub.getSelectedSubCategory());
            dto.setSuggestedCategory(sub.getSuggestedCategory());
            dto.setSuggestedSubCategory(sub.getSuggestedSubCategory());
            dto.setProductImageUrl(sub.getProductImageUrl());
            dto.setDatasheetUrl(sub.getDatasheetUrl());
            dto.setCategoryImageUrl(sub.getCategoryImageUrl());
            dto.setSubmittedAt(sub.getSubmittedAt().toString()); // Convert to ISO 8601 string
            dto.setStatus(sub.getStatus().name());
            dto.setRejectionReason(sub.getRejectionReason());


            // ✅ Parse specificationsJson into a Map<String, String>
            try {
                List<Map<String, String>> specsList = objectMapper.readValue(sub.getSpecificationsJson(), List.class);
                Map<String, String> specMap = specsList.stream()
                    .collect(Collectors.toMap(m -> m.get("key"), m -> m.get("value")));
                dto.setSpecifications(specMap);
            } catch (Exception e) {
                dto.setSpecifications(Map.of()); // fallback
            }

            return dto;
        }).toList();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
@PutMapping("/product-requests/{requestId}/approve")
public ResponseEntity<String> approveProductRequest(@PathVariable Long requestId) {
    ProductRequestSubmission submission = submissionRepository.findById(requestId)
        .orElseThrow(() -> new RuntimeException("Product request not found"));

    submission.setStatus(RequestStatus.APPROVED);
    submission.setRejectionReason(null);
    submissionRepository.save(submission);

    return ResponseEntity.ok("Product request approved");
}

@PreAuthorize("hasRole('ADMIN')")
@PutMapping("/product-requests/{requestId}/reject")
public ResponseEntity<String> rejectProductRequest(
        @PathVariable Long requestId,
        @RequestBody Map<String, String> body) {

    String reason = body.get("reason");
    if (reason == null || reason.isBlank()) {
        return ResponseEntity.badRequest().body("Rejection reason is required");
    }

    ProductRequestSubmission submission = submissionRepository.findById(requestId)
        .orElseThrow(() -> new RuntimeException("Product request not found"));

    submission.setStatus(RequestStatus.REJECTED);
    submission.setRejectionReason(reason);
    submissionRepository.save(submission);

    return ResponseEntity.ok("Product request rejected");
}
}
