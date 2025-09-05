// package com.aeromatx.back.dto.vendor;

// import lombok.*;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class VendorRequest {
//     private String vendorId;
//     private String businessName;
//     private String email;
//     private String password;
//     private String status;
//     private double ratings;

//     private String firstName;
//     private String lastName;
//     private String companyAddress;
//     private String city;
//     private String state;
//     private String zipCode;
//     private String phoneDay;
//     private String phoneEvening;
//     private String position;

//     private String serviceDetails;
//     private String establishmentDate;
//     private String serviceArea;
//     private String businessType;
//     private boolean insured;
//     private boolean licensed;
//     private String licenseNumber;
//     private String annualSales;

//     private String bankName;
//     private String beneficiaryName;
//     private String accountNumber;

//     private String submissionDate;
//     private String signature;
// }

//
package com.aeromatx.back.dto.vendor;

import lombok.Data; // or specific Lombok annotations like Getter, Setter, etc.

@Data
public class VendorRequest {
    // Existing fields
    private String businessName;
    private String email;
    private String password;
    private String status;
    private String firstName;
    private String lastName;
    // ... all other existing fields
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
    private String accountNumber;
    private String submissionDate;
    private String signature;

    // ✅ Add the missing fields here
    private String companyOverview;
    private String gstNumber;
    private String ifscCode;
    private String panNumber;

    // The website field was also in your Postman request, but not in your original response. 
    // You may also need to add it here.
    private String website; 
}