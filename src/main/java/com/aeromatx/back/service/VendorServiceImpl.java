// VendorServiceImpl.java
package com.aeromatx.back.service;

import com.aeromatx.back.dto.vendor.*;
import com.aeromatx.back.entity.Vendor;
import com.aeromatx.back.enums.VendorStatus;
import com.aeromatx.back.repository.VendorRepository;
import com.aeromatx.back.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public Vendor registerVendor(VendorRequest request) {
        if (vendorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Vendor vendor = Vendor.builder()
                .businessName(request.getBusinessName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(VendorStatus.PENDING)
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
                .build();

        return vendorRepository.save(vendor);
    }

    @Override
    public VendorLoginResponseDTO login(VendorLoginRequest request) {
        Vendor vendor = vendorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!passwordEncoder.matches(request.getPassword(), vendor.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (vendor.getStatus() != VendorStatus.APPROVED) {
            throw new RuntimeException("Vendor is not approved yet");
        }

        //String token = jwtUtil.generateToken(vendor.getEmail());
        String token = jwtUtil.generateVendorToken(vendor.getEmail()); // ✅ CORRECT

        String contactPerson = vendor.getFirstName() + " " + vendor.getLastName();
        String phone = vendor.getPhoneDay() != null ? vendor.getPhoneDay() : vendor.getPhoneEvening();

        return new VendorLoginResponseDTO(
                vendor.getVendorId(),
                vendor.getBusinessName(),
                vendor.getEmail(),
                contactPerson,
                phone,
                token,
                vendor.getStatus().name()
        );
    }

    @Override
    public Optional<Vendor> findByEmail(String email) {
        return vendorRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Vendor approveVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setStatus(VendorStatus.APPROVED);
        return vendorRepository.save(vendor);
    }

    @Override
    @Transactional
    public Vendor rejectVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setStatus(VendorStatus.REJECTED);
        return vendorRepository.save(vendor);
    }

    @Override
    public void deleteVendorById(Long vendorId) {
        if (!vendorRepository.existsById(vendorId)) {
            throw new EntityNotFoundException("Vendor with ID " + vendorId + " not found");
        }
        vendorRepository.deleteById(vendorId);
    }

    @Override
    public void setEmailVerified(String email) {
        Vendor vendor = vendorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setEmailVerified(true);
        vendorRepository.save(vendor);
    }

    @Override
    public void setMobileVerified(String mobile) {
        Vendor vendor = vendorRepository.findByPhoneDay(mobile)
                .or(() -> vendorRepository.findByPhoneEvening(mobile))
                .orElseThrow(() -> new RuntimeException("Vendor not found with mobile: " + mobile));

        if (mobile.equals(vendor.getPhoneDay())) {
            vendor.setPhoneDayVerified(true);
        } else if (mobile.equals(vendor.getPhoneEvening())) {
            vendor.setPhoneEveningVerified(true);
        }

        vendorRepository.save(vendor);
    }
}
