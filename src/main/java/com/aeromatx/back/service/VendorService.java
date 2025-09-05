package com.aeromatx.back.service;

import com.aeromatx.back.dto.vendor.*;

import com.aeromatx.back.entity.Vendor;

import java.util.Optional;

public interface VendorService {
    Vendor registerVendor(VendorRequest vendorRequest);
    Optional<Vendor> findByEmail(String email);
    Vendor approveVendor(Long vendorId);
    Vendor rejectVendor(Long vendorId);
    VendorLoginResponseDTO login(VendorLoginRequest request);
    void deleteVendorById(Long vendorId);
    void setEmailVerified(String email);
void setMobileVerified(String mobile);


}
