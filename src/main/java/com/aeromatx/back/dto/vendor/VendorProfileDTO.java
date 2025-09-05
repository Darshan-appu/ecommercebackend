package com.aeromatx.back.dto.vendor;

import com.aeromatx.back.entity.Vendor;
import lombok.Data;

@Data
public class VendorProfileDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String businessName;
    private String phoneDay;
    private String phoneEvening;
    private String companyAddress;
    private String city;
    private String state;
    private String zipCode;
    private boolean emailVerified;
    //private boolean mobileVerified;
    private boolean phoneDayVerified;
    private boolean phoneEveningVerified;

    public static VendorProfileDTO fromEntity(Vendor v) {
        VendorProfileDTO dto = new VendorProfileDTO();
        dto.setFirstName(v.getFirstName());
        dto.setLastName(v.getLastName());
        dto.setEmail(v.getEmail());
        dto.setBusinessName(v.getBusinessName());
        dto.setPhoneDay(v.getPhoneDay());
        dto.setPhoneEvening(v.getPhoneEvening());
        dto.setCompanyAddress(v.getCompanyAddress());
        dto.setCity(v.getCity());
        dto.setState(v.getState());
        dto.setZipCode(v.getZipCode());
        dto.setEmailVerified(v.isEmailVerified());
        //dto.setMobileVerified(v.isMobileVerified());
        dto.setPhoneDayVerified(v.isPhoneDayVerified());
        dto.setPhoneEveningVerified(v.isPhoneEveningVerified());
        return dto;
    }
}
