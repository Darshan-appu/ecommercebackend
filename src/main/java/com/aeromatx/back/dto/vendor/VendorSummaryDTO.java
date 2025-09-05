package com.aeromatx.back.dto.vendor;

import java.time.LocalDate;

import com.aeromatx.back.enums.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VendorSummaryDTO {
    private Long vendorId;
    private String businessName;
    private String email;
    private VendorStatus status;
    //
    // private Long vendorId;
    // private String businessName;
    // private String email;
    // private String status;
    // private Double ratings;
    // private String firstName;
    // private String lastName;
    // private String companyAddress;
    // private String city;
    // private String state;
    // private String zipCode;
    // private String phoneDay;
    // private String phoneEvening;
    // private String position;
    // private String serviceDetails;
    // private LocalDate establishmentDate;
    // private String serviceArea;
    // private String businessType;
    // private Boolean insured;
    // private Boolean licensed;
    // private String licenseNumber;
    // private String annualSales;
    // private String bankName;
    // private String beneficiaryName;
    // private LocalDate submissionDate;
    // private String signature;
    // private String website;
}
