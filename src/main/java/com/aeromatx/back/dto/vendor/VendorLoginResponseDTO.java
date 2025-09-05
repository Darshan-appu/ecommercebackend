package com.aeromatx.back.dto.vendor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorLoginResponseDTO {
    private Long vendorId;
    private String businessName;
    private String email;
    private String contactPerson;
    private String phone;
    private String token;
    private String status;
}
