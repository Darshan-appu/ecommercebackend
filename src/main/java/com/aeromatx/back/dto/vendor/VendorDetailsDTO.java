package com.aeromatx.back.dto.vendor;

import lombok.Builder;
import lombok.Data;
import com.aeromatx.back.enums.VendorStatus;

@Data
@Builder
public class VendorDetailsDTO {
    private Long vendorId;
    private String businessName;
    private String email;
    private VendorStatus status;
    private Double ratings;
    private String firstName;
    private String lastName;
    private String companyAddress;
    private String city;
    private String state;
    private String zipCode;
    private String phoneDay;
    private String phoneEvening;
    private String position;
    private String serviceDetails;
    private String establishmentDate; // String to match entity
    private String serviceArea;
    private String businessType;
    private Boolean insured;          // keep wrapper for null-safety in JSON
    private Boolean licensed;         // same here
    private String licenseNumber;
    private String annualSales;
    private String bankName;
    private String beneficiaryName;
    private String submissionDate;    // String to match entity
    private String signature;
    private String website;
}
