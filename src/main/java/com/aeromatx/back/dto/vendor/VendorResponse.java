// src/main/java/com/aeromatx/back/dto/vendor/VendorResponse.java
package com.aeromatx.back.dto.vendor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorResponse {
    private String vendorId;
    private String businessName;
    private String email;
    private String status;
    private double ratings;

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
    private String establishmentDate;
    private String serviceArea;
    private String businessType;
    private boolean insured;
    private boolean licensed;
    private String licenseNumber;
    private String annualSales;

    private String bankName;
    private String beneficiaryName;

    private String submissionDate;
    private String signature;
    private String website;
}
