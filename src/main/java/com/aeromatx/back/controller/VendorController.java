package com.aeromatx.back.controller;

import com.aeromatx.back.dto.admin.ProductRequestSubmissionResponseDTO;
import com.aeromatx.back.dto.user.EmailChangeRequest;
import com.aeromatx.back.dto.vendor.*;
import com.aeromatx.back.entity.ERole;
import com.aeromatx.back.entity.ProductRequestSubmission;
import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.enums.RequestStatus;
import com.aeromatx.back.enums.VendorStatus;
import com.aeromatx.back.repository.ProductRequestSubmissionRepository;
import com.aeromatx.back.repository.RoleRepository;
import com.aeromatx.back.repository.VendorRepository;
import com.aeromatx.back.service.VendorService;
import com.aeromatx.back.util.JwtUtil;
import com.aeromatx.back.util.PhoneUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aeromatx.back.service.CloudinaryService;
import com.aeromatx.back.service.OtpService;

import com.aeromatx.back.entity.Role;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.List;
//import java.util.Map;





@RestController
@RequestMapping("/api/vendor")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VendorController {

    private final VendorRepository vendorRepository;
    private final VendorService vendorService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Autowired
private RoleRepository roleRepository;



    @PostMapping("/register")
public ResponseEntity<?> registerVendor(@RequestBody VendorRequest request) {
    if (vendorRepository.existsByEmail(request.getEmail())) {
        return ResponseEntity.badRequest().body("Email already registered");
    }

    VendorStatus status = request.getStatus() != null
            ? VendorStatus.valueOf(request.getStatus().toUpperCase())
            : VendorStatus.PENDING;

    // ✅ Fetch the ROLE_OEM from the roles table
    Role defaultRole = roleRepository.findByName(ERole.ROLE_OEM)
            .orElseThrow(() -> new RuntimeException("Error: Role ROLE_OEM not found."));

    Vendor vendor = Vendor.builder()
            .businessName(request.getBusinessName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .status(status)
            .role(defaultRole) // ✅ assign actual Role entity
            .ratings(0.0)
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .companyAddress(request.getCompanyAddress())
            .city(request.getCity())
            .state(request.getState())
            .zipCode(request.getZipCode())
            .phoneDay(request.getPhoneDay())
            .phoneEvening(request.getPhoneEvening())
            .position(request.getPosition())
            .serviceDetails(request.getServiceDetails())
            .establishmentDate(request.getEstablishmentDate())
            .serviceArea(request.getServiceArea())
            .businessType(request.getBusinessType())
            .insured(request.isInsured())
            .licensed(request.isLicensed())
            .licenseNumber(request.getLicenseNumber())
            .annualSales(request.getAnnualSales())
            .bankName(request.getBankName())
            .beneficiaryName(request.getBeneficiaryName())
            .accountNumber(request.getAccountNumber())
            .submissionDate(request.getSubmissionDate())
            .signature(request.getSignature())
            .emailVerified(false)
            .phoneDayVerified(false)
            .phoneEveningVerified(false)
            .companyOverview(request.getCompanyOverview())
    .panNumber(request.getPanNumber())
    .gstNumber(request.getGstNumber())
    .ifscCode(request.getIfscCode())
    .website(request.getWebsite())
            .build();

    vendorRepository.save(vendor);

    VendorResponse response = VendorResponse.builder()
            .businessName(vendor.getBusinessName())
            .email(vendor.getEmail())
            .status(vendor.getStatus().name())
            .ratings(vendor.getRatings())
            .firstName(vendor.getFirstName())
            .lastName(vendor.getLastName())
            .companyAddress(vendor.getCompanyAddress())
            .city(vendor.getCity())
            .state(vendor.getState())
            .zipCode(vendor.getZipCode())
            .phoneDay(vendor.getPhoneDay())
            .phoneEvening(vendor.getPhoneEvening())
            .position(vendor.getPosition())
            .serviceDetails(vendor.getServiceDetails())
            .establishmentDate(vendor.getEstablishmentDate())
            .serviceArea(vendor.getServiceArea())
            .businessType(vendor.getBusinessType())
            .insured(vendor.isInsured())
            .licensed(vendor.isLicensed())
            .licenseNumber(vendor.getLicenseNumber())
            .annualSales(vendor.getAnnualSales())
            .bankName(vendor.getBankName())
            .beneficiaryName(vendor.getBeneficiaryName())
            .submissionDate(vendor.getSubmissionDate())
            .signature(vendor.getSignature())
            .website(request.getWebsite())
            .build();

    return ResponseEntity.ok(response);
}


    @PostMapping("/login")
    public ResponseEntity<?> loginVendor(@RequestBody VendorLoginRequest request) {
        try {
            VendorLoginResponseDTO response = vendorService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg.contains("not approved")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
            }
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getVendorProfile(HttpServletRequest request) {
        String email = jwtUtil.extractUsernameFromRequest(request);
        Vendor vendor = vendorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        VendorProfileDTO dto = VendorProfileDTO.fromEntity(vendor);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateVendorProfile(@RequestBody VendorProfileDTO dto, HttpServletRequest request) {
        String email = jwtUtil.extractUsernameFromRequest(request);
        Vendor vendor = vendorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setFirstName(dto.getFirstName());
        vendor.setLastName(dto.getLastName());
        vendor.setPhoneDay(dto.getPhoneDay());
        vendor.setPhoneEvening(dto.getPhoneEvening());
        vendor.setCompanyAddress(dto.getCompanyAddress());
        vendor.setCity(dto.getCity());
        vendor.setState(dto.getState());
        vendor.setZipCode(dto.getZipCode());

        vendorRepository.save(vendor);
        return ResponseEntity.ok("Vendor profile updated");
    }

    @PostMapping("/send-otp/mobile")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> sendVendorMobileOtp(@RequestParam String mobile) {
        otpService.generateOtpForMobile(mobile);
        return ResponseEntity.ok("OTP sent to vendor mobile: " + mobile);
    }

    @PostMapping("/send-otp/email")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> sendVendorEmailOtp(@RequestParam String email) {
        otpService.generateOtpForEmail(email);
        return ResponseEntity.ok("OTP sent to vendor email: " + email);
    }

    @PutMapping("/verify/email")
    public ResponseEntity<String> verifyVendorEmailOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isVerified = otpService.verifyEmailOtp(email, otp);
        if (isVerified) {
            vendorService.setEmailVerified(email);
            return ResponseEntity.ok("Vendor email verified successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
    }

    @PutMapping("/verify/mobile")
    public ResponseEntity<String> verifyVendorMobileOtp(@RequestParam String mobile, @RequestParam String otp) {
        String cleanedMobile = PhoneUtil.stripCountryCode(mobile);
        boolean isVerified = otpService.verifyMobileOtp(cleanedMobile, otp);
        if (isVerified) {
            vendorService.setMobileVerified(cleanedMobile);
            return ResponseEntity.ok("Vendor mobile verified successfully.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP.");
    }

    //for email chenge
    @PostMapping("/email-change/send-otp")
public ResponseEntity<?> sendOtpForEmailChange(HttpServletRequest request) {
    String email = jwtUtil.extractUsernameFromRequest(request);

    Vendor vendor = vendorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));

    String targetMobile = null;

    // if (Boolean.TRUE.equals(vendor.getPhoneDayVerified())) {
    //     targetMobile = vendor.getPhoneDay();
    // } else if (Boolean.TRUE.equals(vendor.getPhoneEveningVerified())) {
    //     targetMobile = vendor.getPhoneEvening();
    // }

    if (vendor.isPhoneDayVerified()) {
    targetMobile = vendor.getPhoneDay();
} else if (vendor.isPhoneEveningVerified()) {
    targetMobile = vendor.getPhoneEvening();
}


    if (targetMobile == null || targetMobile.isBlank()) {
        return ResponseEntity.badRequest().body("No verified mobile number found for vendor");
    }

    // Clean and send OTP
    String cleanedMobile = PhoneUtil.stripCountryCode(targetMobile);
    String otp = otpService.generateSimpleOtp(cleanedMobile);
    System.out.println("OTP for vendor email change to mobile " + cleanedMobile + ": " + otp);

    return ResponseEntity.ok(
            Map.of("message", "OTP sent to verified mobile: " + cleanedMobile)
    );
}

@PostMapping("/email-change/verify-otp")
public ResponseEntity<?> verifyOtpAndChangeEmail(@RequestBody EmailChangeRequest request, HttpServletRequest httpRequest) {
    String email = jwtUtil.extractUsernameFromRequest(httpRequest);
    Vendor vendor = vendorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));

    String targetMobile = vendor.isPhoneDayVerified() ? vendor.getPhoneDay() :
                          vendor.isPhoneEveningVerified() ? vendor.getPhoneEvening() : null;

    if (targetMobile == null || !otpService.verifySimpleOtp(PhoneUtil.stripCountryCode(targetMobile), request.getOtp())) {
        return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }

    vendor.setEmail(request.getNewEmail());
    vendor.setEmailVerified(false);
    vendorRepository.save(vendor);

    return ResponseEntity.ok(Map.of("message", "Vendor email changed successfully"));
}

@GetMapping("/check-mobile-verification")
public ResponseEntity<?> checkMobileVerification(HttpServletRequest request) {
    String email = jwtUtil.extractUsernameFromRequest(request);
    Vendor vendor = vendorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));

    boolean isAnyVerified = vendor.isPhoneDayVerified() || vendor.isPhoneEveningVerified();
    return ResponseEntity.ok(Map.of("mobileVerified", isAnyVerified));
}

//to add products

@Autowired
private CloudinaryService cloudinaryService;


@Autowired
private ProductRequestSubmissionRepository productRequestSubmissionRepository;



@PostMapping("/product-request/submit")
public ResponseEntity<?> submitProductRequest(
        @RequestParam("product") String productJson,
        @RequestParam("productImage") MultipartFile productImage,
        @RequestParam("datasheet") MultipartFile datasheet,
        @RequestParam(value = "categoryImage", required = false) MultipartFile categoryImage,
        HttpServletRequest request) throws IOException {

    String email = jwtUtil.extractUsernameFromRequest(request);
    Vendor vendor = vendorRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Vendor not found"));

    if (vendor.getStatus() != VendorStatus.APPROVED) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Vendor is not approved");
    }

    // Parse request
    ObjectMapper mapper = new ObjectMapper();
    VendorProductSubmissionDTO dto = mapper.readValue(productJson, VendorProductSubmissionDTO.class);

    // Upload files to Cloudinary or local
    String productImageUrl = cloudinaryService.uploadFile(productImage, "products/");

String datasheetUrl = cloudinaryService.uploadFile(datasheet, "datasheets/");
String categoryImageUrl = cloudinaryService.uploadFile(categoryImage, "categories/");


    // Save entry
    ProductRequestSubmission submission = new ProductRequestSubmission();
    submission.setVendorEmail(email);
    submission.setProductName(dto.getProductName());
    submission.setDescription(dto.getDescription());
    submission.setPrice(dto.getPrice());
    submission.setStock(dto.getStock());
    submission.setSelectedCategory(dto.getSelectedCategory());
    submission.setSelectedSubCategory(dto.getSelectedSubCategory());
    submission.setSuggestedCategory(dto.getSuggestedCategory());
    submission.setSuggestedSubCategory(dto.getSuggestedSubCategory());
    submission.setProductImageUrl(productImageUrl);
    submission.setDatasheetUrl(datasheetUrl);
    submission.setCategoryImageUrl(categoryImageUrl);
    submission.setSpecificationsJson(mapper.writeValueAsString(dto.getSpecifications()));
    submission.setSubmittedAt(LocalDateTime.now());

    productRequestSubmissionRepository.save(submission);

    return ResponseEntity.ok(Map.of("message", "Product request submitted to admin"));
}

//to show list of requested products and their status 
@PreAuthorize("hasRole('OEM')")
@GetMapping("/my-product-requests")
public ResponseEntity<List<ProductRequestSubmissionResponseDTO>> getMyProductRequests(HttpServletRequest request) {
    String email = jwtUtil.extractUsernameFromRequest(request);

    //List<ProductRequestSubmission> submissions = submissionRepository.findByVendorEmail(email);
    List<ProductRequestSubmission> submissions = productRequestSubmissionRepository.findByVendorEmail(email);


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
        dto.setSubmittedAt(sub.getSubmittedAt().toString());
        dto.setStatus(sub.getStatus().name());
        dto.setRejectionReason(sub.getRejectionReason());

        try {
            List<Map<String, String>> specsList = objectMapper.readValue(sub.getSpecificationsJson(), List.class);
            Map<String, String> specMap = specsList.stream()
                .collect(Collectors.toMap(m -> m.get("key"), m -> m.get("value")));
            dto.setSpecifications(specMap);
        } catch (Exception e) {
            dto.setSpecifications(Map.of());
        }

        return dto;
    }).toList();

    return ResponseEntity.ok(response);
}

// ✅ Update an existing product request (only if PENDING)
@PreAuthorize("hasRole('OEM')")
@PutMapping("/product-request/{id}/edit")
public ResponseEntity<?> editProductRequest(
        @PathVariable Long id,
        @RequestParam("product") String productJson,
        @RequestParam(value = "productImage", required = false) MultipartFile productImage,
        @RequestParam(value = "datasheet", required = false) MultipartFile datasheet,
        @RequestParam(value = "categoryImage", required = false) MultipartFile categoryImage,
        HttpServletRequest request) throws IOException {

    String email = jwtUtil.extractUsernameFromRequest(request);
    ProductRequestSubmission submission = productRequestSubmissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product request not found"));

    // ✅ Ensure this vendor owns it
    if (!submission.getVendorEmail().equals(email)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of this product request");
    }

    // ✅ Ensure it's still pending
    if (submission.getStatus() != RequestStatus.PENDING) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot edit after admin approval/rejection");
    }

    // ✅ Update details
    VendorProductSubmissionDTO dto = objectMapper.readValue(productJson, VendorProductSubmissionDTO.class);

    submission.setProductName(dto.getProductName());
    submission.setDescription(dto.getDescription());
    submission.setPrice(dto.getPrice());
    submission.setStock(dto.getStock());
    submission.setSelectedCategory(dto.getSelectedCategory());
    submission.setSelectedSubCategory(dto.getSelectedSubCategory());
    submission.setSuggestedCategory(dto.getSuggestedCategory());
    submission.setSuggestedSubCategory(dto.getSuggestedSubCategory());
    submission.setSpecificationsJson(objectMapper.writeValueAsString(dto.getSpecifications()));

    // ✅ Upload new files if provided
    if (productImage != null && !productImage.isEmpty()) {
        submission.setProductImageUrl(cloudinaryService.uploadFile(productImage, "products/"));
    }
    if (datasheet != null && !datasheet.isEmpty()) {
        submission.setDatasheetUrl(cloudinaryService.uploadFile(datasheet, "datasheets/"));
    }
    if (categoryImage != null && !categoryImage.isEmpty()) {
        submission.setCategoryImageUrl(cloudinaryService.uploadFile(categoryImage, "categories/"));
    }

    productRequestSubmissionRepository.save(submission);

    return ResponseEntity.ok(Map.of("message", "Product request updated successfully"));
}

// ✅ Delete a product request (only if PENDING)
@PreAuthorize("hasRole('OEM')")
@DeleteMapping("/product-request/{id}/delete")
public ResponseEntity<?> deleteProductRequest(@PathVariable Long id, HttpServletRequest request) {
    String email = jwtUtil.extractUsernameFromRequest(request);
    ProductRequestSubmission submission = productRequestSubmissionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product request not found"));

    // ✅ Ensure this vendor owns it
    if (!submission.getVendorEmail().equals(email)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of this product request");
    }

    // ✅ Ensure it's still pending
    if (submission.getStatus() != RequestStatus.PENDING) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot delete after admin approval/rejection");
    }

    productRequestSubmissionRepository.delete(submission);

    return ResponseEntity.ok(Map.of("message", "Product request deleted successfully"));
}


}
