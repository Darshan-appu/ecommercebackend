package com.aeromatx.back.entity;

import java.util.ArrayList;

import com.aeromatx.back.enums.VendorStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    private String businessName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private VendorStatus status;

    private double ratings;

    // Point of Contact
    private String firstName;
    private String lastName;
    private String companyAddress;
    private String city;
    private String state;
    private String zipCode;
    private String phoneDay;
    private String phoneEvening;
    private String position;

    // Company Overview
    @Column(length = 2000)
    private String serviceDetails;

    private String establishmentDate;
    private String serviceArea;
    private String businessType;
    private boolean insured;
    private boolean licensed;
    private String licenseNumber;
    private String annualSales;

    // Banking Info
    private String bankName;
    private String beneficiaryName;
    private String accountNumber;

    // Certification
    private String submissionDate;
    private String signature;

    // Company Overview (used in profile)
    @Column(length = 2000)
    private String companyOverview;

    // PAN number (used in profile)
    private String panNumber;

    // GST number (used in profile)
    private String gstNumber;

    // IFSC code (used in profile)
    private String ifscCode;

    // Profile picture URL or filename
    private String profilePicture;

    // Email verification
    @Column(name = "email_verified")
    private boolean emailVerified = false;

    // ✅ Separate mobile verification flags
    @Column(name = "phone_day_verified")
    private boolean phoneDayVerified = false;

    @Column(name = "phone_evening_verified")
    private boolean phoneEveningVerified = false;

    //for website
    private String website;

    // Vendor.java
// @Enumerated(EnumType.STRING)
// @Column(nullable = false)
// private ERole role = ERole.ROLE_OEM; // default value

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "role_id", nullable = false)
private Role role;




    // Getters and Setters for custom fields
    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isPhoneDayVerified() {
        return phoneDayVerified;
    }

    public void setPhoneDayVerified(boolean phoneDayVerified) {
        this.phoneDayVerified = phoneDayVerified;
    }

    public boolean isPhoneEveningVerified() {
        return phoneEveningVerified;
    }

    public void setPhoneEveningVerified(boolean phoneEveningVerified) {
        this.phoneEveningVerified = phoneEveningVerified;
    }

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Product> products = new ArrayList<>();

}
